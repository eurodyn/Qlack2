package com.eurodyn.qlack2.fuse.aaa.impl;

import com.eurodyn.qlack2.fuse.aaa.api.JSONConfigService;
import com.eurodyn.qlack2.fuse.aaa.api.OpTemplateService;
import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.JSONConfig;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.framework.Bundle;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = {JSONConfigService.class})
@Transactional
public class JSONConfigServiceImpl implements JSONConfigService {
    private static final Logger LOGGER = Logger.getLogger(JSONConfigServiceImpl.class.getName());

    // JSON parser.
    ObjectMapper mapper = new ObjectMapper();

    // Service REFs
    @Inject
    private UserGroupService groupService;

    @Inject
    private OpTemplateService templateService;

    @Inject
    private OperationService operationService;

    @PersistenceContext(unitName = "fuse-aaa")
    private EntityManager em;

    @Override
    public void processBundle(Bundle bundle) {
        // Check if this bundles contains a configuration file.
        Enumeration<URL> entries = bundle
                .findEntries("OSGI-INF", "qlack-aaa-config.json", false);

        // If no configuration file found, return doing nothing.
        if ((entries == null) || (!entries.hasMoreElements())) {
            return;
        }

        // Get the configuration file.
        URL url = entries.nextElement();

        // Process the configuration file.
        parseConfig(bundle.getSymbolicName(), url);
    }

    @Override
    public void parseConfig(String bundleSymbolicName, URL configFileURL) {
        LOGGER.log(Level.FINE, "Handling FUSE AAA configuration for bundle {0}.",
                bundleSymbolicName);
        LOGGER.log(Level.FINE, "Reading configuration from {0}.", configFileURL.toExternalForm());

        // Parse the JSON file.
        JSONConfig config = null;
        try {
            config = mapper.readValue(configFileURL, JSONConfig.class);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, MessageFormat.format(
                    "Could not parse configuration file {0}.", configFileURL.toExternalForm()), e);
            return;
        }

        // Calculate an MD5 for this file to know if it has changed in order to
        // avoid unnecessary database access.
        String checksum = null;
        try {
            checksum = DigestUtils.md5Hex(mapper.writeValueAsString(config));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, MessageFormat.format(
                    "Could not calculate MD5 for file {0}.", configFileURL.toExternalForm()), e);
            return;
        }

        // Check if this file has been executed again in the past.
        Application application = Application.findBySymbolicName(bundleSymbolicName, em);
        if (application == null) {
            // If the application was not found in the DB initialize it.
            LOGGER.log(Level.FINE, "Processing AAA configuration of bundle {0}.", bundleSymbolicName);
            application = new Application();
            application.setSymbolicName(bundleSymbolicName);
            application.setChecksum(checksum);
            em.persist(application);
        } else if (application.getChecksum().equals(checksum)) {
            // If the application has been processed before and the JSON file
            // has not been modified, no need to proceed with the processing.
            LOGGER.log(
                    Level.FINE,
                    "Ignoring operations found in bundle {0}; the operations "
                            + "are unchanged since the last time they were processed",
                    bundleSymbolicName);
            return;
        }
        em.flush();

        // Create groups.
        for (JSONConfig.Group g : config.getGroups()) {
            // If the group exists, update it, otherwise create it.
            LOGGER.log(Level.FINEST, "Processing group {0}", g.getName());
            GroupDTO groupDTO = groupService.getGroupByName(g.getName(), true);
            boolean isNew = groupDTO == null;
            if (isNew) {
                groupDTO = new GroupDTO();
            }
            groupDTO.setDescription(g.getDescription());
            groupDTO.setName(g.getName());
            if (StringUtils.isNotBlank(g.getParentGroupName())) {
                GroupDTO parentGroup = groupService.getGroupByName(g.getParentGroupName(), true);
                groupDTO.setParent(new GroupDTO(parentGroup.getId()));
            }
            if (isNew) {
                groupService.createGroup(groupDTO);
            } else {
                groupService.updateGroup(groupDTO);
            }
        }
        em.flush();

        // Create templates.
        for (JSONConfig.Template t : config.getTemplates()) {
            // If the template exists, update it, otherwise create it.
            LOGGER.log(Level.FINEST, "Processing template {0}", t.getName());
            OpTemplateDTO templateDTO = templateService.getTemplateByName(t.getName());
            boolean isNew = templateDTO == null;
            if (isNew) {
                templateDTO = new OpTemplateDTO();
            }
            templateDTO.setDescription(t.getDescription());
            templateDTO.setName(t.getName());
            if (isNew) {
                templateService.createTemplate(templateDTO);
            } else {
                templateService.updateTemplate(templateDTO);
            }
        }
        em.flush();

        // Create Operations.
        for (JSONConfig.Operation o : config.getOperations()) {
            // If the operation exists, update it, otherwise create it.
            LOGGER.log(Level.FINEST, "Processing operation {0}", o.getName());
            OperationDTO opDTO = operationService.getOperationByName(o.getName());
            boolean isNew = opDTO == null;
            if (isNew) {
                opDTO = new OperationDTO();
            }
            opDTO.setDescription(o.getDescription());
            opDTO.setName(o.getName());
            if (isNew) {
                operationService.createOperation(opDTO);
            } else {
                operationService.updateOperation(opDTO);
            }
        }
        em.flush();

        // Create Group has Operations.
        for (JSONConfig.GroupHasOperation gho : config.getGroupHasOperations()) {
            // If the operation exists, update it, otherwise create it.
            LOGGER.log(Level.FINEST, "Processing group has operation {0}-{1}",
                    new String[]{gho.getGroupName(), gho.getOperationName()});
            GroupDTO groupDTO = groupService.getGroupByName(gho.getGroupName(), true);
            if (!operationService.getAllowedGroupsForOperation(
                    gho.getOperationName(), false).contains(groupDTO.getId())) {
                operationService.addOperationToGroup(
                        groupDTO.getId(), gho.getOperationName(), gho.isDeny());
            }
        }
        em.flush();

        // Create Template has Operations.
        for (JSONConfig.TemplateHasOperation tho : config.getTemplateHasOperations()) {
            // If the operation exists, update it, otherwise create it.
            LOGGER.log(Level.FINEST, "Processing template has operation {0}-{1}",
                    new String[]{tho.getTemplateName(), tho.getOperationName()});
            OpTemplateDTO templateDTO = templateService.getTemplateByName(tho.getTemplateName());
            if (templateService.getOperationAccess(templateDTO.getId(), tho.getOperationName()) == null) {
                templateService.addOperation(templateDTO.getId(), tho.getOperationName(), tho.isDeny());
            }
        }
        em.flush();

        // Update last execution date for this application's AAA resources.
        application.setExecutedOn(Instant.now().getMillis());
    }

}
