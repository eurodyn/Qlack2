package com.eurodyn.qlack2.fuse.lexicon.impl;

import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.KeyService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.exception.QLexiconYMLProcessingException;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Application;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.framework.Bundle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = {BundleUpdateService.class})
@Transactional
public class BundleUpdateServiceImpl implements BundleUpdateService {
    private static final Logger LOGGER = Logger.getLogger(BundleUpdateServiceImpl.class.getName());
    @PersistenceContext(unitName = "fuse-lexicon")
    private EntityManager em;

    @Inject
    private GroupService groupService;

    @Inject
    private LanguageService languageService;

    @Inject
    private KeyService keyService;

    public static String lock = UUID.randomUUID().toString();

    @Override
    public void processBundle(Bundle bundle, String lexiconYAML) {
        Enumeration<URL> entries = bundle
                .findEntries("OSGI-INF", lexiconYAML, false);
        if ((entries != null) && (entries.hasMoreElements())) {
            URL url = entries.nextElement();
            updateBundleTranslations(bundle.getSymbolicName(), url);
        }
    }

    @Override
    public void updateBundleTranslations(String symbolicName, URL yamlUrl) {
        LOGGER.log(Level.FINE, "Handling bundle {0} in Lexicon Translations Handler", symbolicName);

        try {
            String checksum = DigestUtils.md5Hex(yamlUrl.openStream());
            Yaml yaml = new Yaml(new CustomClassLoaderConstructor(getClass().getClassLoader()));

            // Check if this file has been executed again in the past
            Application application = Application.findBySymbolicName(symbolicName, em);
            if (application == null) {
                // If the application was not found in the DB initialise the
                // entity
                // to use it later on when registering the lexicon file
                // execution.
                LOGGER.log(Level.FINE, "Processing translations of bundle {0}.", symbolicName);
                application = new Application();
                application.setSymbolicName(symbolicName);
            } else if (application.getChecksum().equals(checksum)) {
                // If the application has been processed before and the lexicon
                // file
                // has not been modified in the meantime no need to proceed with
                // the processing.
                LOGGER.log(Level.FINEST, "Ignoring translations found in bundle {0}; " +
                        "the translations are unchanged since the last time " +
                        "they were processed.", symbolicName);
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> contents = (Map<String, Object>) yaml.load(yamlUrl.openStream());

            // Process translation groups
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> groups = (List<Map<String, Object>>) contents.get("groups");
            if (groups != null) {
                for (Map<String, Object> group : groups) {
                    String groupName = (String) group.get("name");
                    String groupDescription = (String) group.get("description");
                    LOGGER.log(Level.FINE, "Looking for translations group {0} [{1}]",
                            new Object[]{groupName,  symbolicName});
                    GroupDTO groupDTO = groupService.getGroupByName(groupName);
                    // If a group with this name does not exist create it.
                    if (groupDTO == null) {
                        LOGGER.log(Level.FINE, "Group not found; it will be created [{0}].", symbolicName);
                        groupDTO = new GroupDTO();
                        groupDTO.setTitle(groupName);
                        groupDTO.setDescription(groupDescription);
                        groupService.createGroup(groupDTO);
                    }
                    // Else check the value of the forceUpdate flag
                    else if ((group.get("forceUpdate") != null) && ((Boolean) group.get("forceUpdate") == true)) {
                        groupDTO.setDescription(groupDescription);
                        groupService.updateGroup(groupDTO);
                    }
                }
            }

            // Process languages
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> languages = (List<Map<String, Object>>) contents.get("languages");
            if (languages != null) {
                for (Map<String, Object> language : languages) {
                    String languageName = (String) language.get("name");
                    String locale = (String) language.get("locale");
                    LOGGER.log(Level.FINE, "Looking for language {0} with locale {1} [{2}].",
                            new Object[]{languageName,  locale, symbolicName});
                    LanguageDTO languageDTO = languageService.getLanguageByLocale(locale);
                    // If a language with this locale does not exist create it.
                    if (languageDTO == null) {
                        LOGGER.log(Level.FINE, "Language not found; it will be created [{0}].", symbolicName);
                        languageDTO = new LanguageDTO();
                        languageDTO.setName(languageName);
                        languageDTO.setLocale(locale);
                        languageDTO.setActive(true);
                        languageService.createLanguage(languageDTO, null);
                    }
                    // Else check the value of the forceUpdate flag
                    else if ((language.get("forceUpdate") != null) && ((Boolean) language.get("forceUpdate") == true)) {
                        languageDTO.setName(languageName);
                        languageService.updateLanguage(languageDTO);
                    }
                }
            }

            // Process translations
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> translationContents = (List<Map<String, Object>>) contents.get("translations");
            if (translationContents != null) {
                LOGGER.log(Level.FINE, "Processing translation entries [{0}].", symbolicName);
                for (Map<String, Object> translationContent : translationContents) {
                    @SuppressWarnings("unchecked")
                    List<String> excludedGroupName = (List<String>) translationContent.get("not_in_group");
                    String locale = (String) translationContent.get("locale");
                    String languageId = languageService.getLanguageByLocale(locale).getId();
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> translations = (List<Map<String, Object>>) translationContent.get("keys");

                    // Process not_in_group
                    if (excludedGroupName != null) {
                        Set<GroupDTO> newGroups = groupService.getRemainingGroups(excludedGroupName);
                        for (GroupDTO group : newGroups) {
                            LOGGER.log(Level.FINE, "Add new keys to group {0} [{1}].",
                                    new Object[]{group.getTitle(), symbolicName});
                            updateKeys(translations, group.getId(), languageId);
                        }
                    } else { // Process group
                        String groupName = (String) translationContent.get("group");
                        String groupId = groupService.getGroupByName(groupName).getId();
                        updateKeys(translations, groupId, languageId);
                    }
                }
            }

            // Register the processing of the file in the DB
            application.setChecksum(checksum);
            application.setExecutedOn(DateTime.now().getMillis());
            em.merge(application);

            LOGGER.log(Level.FINE, "Finished processing {0}.", symbolicName);
        } catch (IOException | SecurityException ex) {
            LOGGER.log(Level.SEVERE, "Error handling lexicon YAML file", ex);
            throw new QLexiconYMLProcessingException("Error handling lexicon YAML file");
        }

    }

    private void updateKeys(List<Map<String, Object>> translations, String groupId, String languageId) {
        for (Map<String, Object> translation : translations) {
            String translationKey = translation.keySet().iterator().next();
            String translationValue = (String) translation.get(translationKey);
            KeyDTO keyDTO = keyService.getKeyByName(translationKey, groupId, true);
            // If the key does not exist in the DB then create it.
            if (keyDTO == null) {
                keyDTO = new KeyDTO();
                keyDTO.setGroupId(groupId);
                keyDTO.setName(translationKey);
                Map<String, String> keyTranslations = new HashMap<>();
                keyTranslations.put(languageId, translationValue);
                keyDTO.setTranslations(keyTranslations);
                keyService.createKey(keyDTO, false);
            }
            // If the key exists check if a translation exists and if it
            // does check if it is the same as the key name, which means
            // that the translation was created automatically (ex. when
            // adding a new language) and therefore it should be
            // updated. Otherwise only update the key if the forceUpdate
            // flag is set to true.
            else if ((keyDTO.getTranslations().get(languageId) == null)
                    || (keyDTO.getTranslations().get(languageId).equals(translationKey))
                    || ((translation.get("forceUpdate") != null)
                    && ((Boolean) translation.get("forceUpdate") == true))) {
                keyService.updateTranslation(keyDTO.getId(), languageId, translationValue);
            }
        }
    }
}
