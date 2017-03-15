package com.eurodyn.qlack2.webdesktop.impl;

import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.webdesktop.api.ApplicationRegistrationService;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.impl.model.Application;
import org.ops4j.pax.cdi.api.OsgiService;
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
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = {ApplicationRegistrationService.class})
@Transactional
public class ApplicationRegistrationServiceImpl implements ApplicationRegistrationService {
    /**
     * JUL reference
     */
    private final static Logger LOGGER = Logger.getLogger(ApplicationRegistrationServiceImpl.class.getName());

    @PersistenceContext(unitName = "webdesktop")
    private EntityManager em;

    @OsgiService
    @Inject
    private ResourceService resourceService;

    @Override
    public void registerApplication(Bundle bundle, String yamlLocation) {
        String folder;
        String file;
        if (yamlLocation.contains("/")) {
            folder = yamlLocation.substring(0, yamlLocation.lastIndexOf('/'));
            file = yamlLocation.substring(yamlLocation.lastIndexOf('/') + 1);
        } else {
            folder = "";
            file = yamlLocation;
        }
        Enumeration<URL> entries = bundle.findEntries(folder, file, false);
        String symbolicName = bundle.getSymbolicName();
        if ((entries != null) && (entries.hasMoreElements())) {
            LOGGER.log(Level.FINE, "Web Desktop application {0} identified with " +
                    "YAML file on {1}/{2}.", new Object[]{bundle.getSymbolicName(),
                    folder, file});
            URL url = entries.nextElement();
            try {
                Yaml yaml = new Yaml(new CustomClassLoaderConstructor(getClass().getClassLoader()));
                ApplicationInfo appInfo = yaml.loadAs(url.openStream(), ApplicationInfo.class);

                // Check if the application is already in the WebDesktopDB and
                // if yes check that it is not currently registered through another
                // service. If the application is already in the DB and not
                // currently registered through another service then update the
                // implementing service ID and all the application data that
                // should not be persistent between application deployments.
                Application application = em.find(Application.class, appInfo.getIdentification().getUniqueId());
                if (application == null) {
                    LOGGER.log(Level.FINE, "Application with UUID {0} deployed for the first time.",
                            appInfo.getIdentification().getUniqueId());
                    application = new Application();
                    application.setAppUuid(appInfo.getIdentification().getUniqueId());
                    application.setRestrictAccess(appInfo.getInstantiation().getRestrictAccess());
                    application.setActive(appInfo.isActive());
                    application.setAddedOn(Calendar.getInstance().getTime());

                    // Create a AAA resource corresponding to the application to allow setting
                    // user access.
                    ResourceDTO appResource = new ResourceDTO();
                    appResource.setName(appInfo.getInstantiation().getPath());
                    appResource.setDescription("Web desktop application");
                    appResource.setObjectID(appInfo.getIdentification().getUniqueId());
                    resourceService.createResource(appResource);
                } else if ((application.getBundleSymbolicName() != null)
                        && (!application.getBundleSymbolicName().equals(symbolicName))) {
                    LOGGER.log(Level.WARNING, "Attempted to register bundle {0}" +
                                    " but application with UUID {1} " +
                                    "is already registered by bundle {2}. " +
                                    "This service will not be taken into account",
                            new String[]{symbolicName,
                                    appInfo.getIdentification().getUniqueId(),
                                    application.getBundleSymbolicName()});
                }
                application.setTitleKey(appInfo.getIdentification().getTitleKey());
                application.setDescriptionKey(appInfo.getIdentification().getDescriptionKey());
                application.setVersion(appInfo.getIdentification().getVersion());
                application.setPath(appInfo.getInstantiation().getPath());
                application.setIndex(appInfo.getInstantiation().getIndex());
                application.setTranslationsGroup(appInfo.getInstantiation().getTranslationsGroup());
                application.setMultipleInstances(appInfo.getInstantiation().getMultipleInstances());
                application.setIcon(appInfo.getMenu().getIcon());
                application.setIconSmall(appInfo.getMenu().getIconSmall());
                application.setBgColor(appInfo.getMenu().getBgColor());
                application.setSystem(appInfo.getMenu().isSystem());
                application.setWidth(appInfo.getWindow().getWidth());
                application.setMinWidth(appInfo.getWindow().getMinWidth());
                application.setHeight(appInfo.getWindow().getHeight());
                application.setMinHeight(appInfo.getWindow().getMinHeight());
                application.setResizable(appInfo.getWindow().isResizable());
                application.setMaximizable(appInfo.getWindow().isMaximizable());
                application.setMinimizable(appInfo.getWindow().isMinimizable());
                application.setDraggable(appInfo.getWindow().isDraggable());
                application.setClosable(appInfo.getWindow().isClosable());
                application.setLastDeployedOn(Calendar.getInstance().getTime());
                application.setBundleSymbolicName(symbolicName);
                em.persist(application);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, MessageFormat.format(
                        "Could not read application yaml for bundle {0}.",
                        symbolicName), e);
            }
        } else {
            LOGGER.log(Level.FINEST, "Bundle {0} is not a Web Desktop application.", symbolicName);
        }
    }

    @Override
    public void unregisterApplication(String bundleSymbolicName) {
        Application application = Application.getApplicationForSymbolicName(bundleSymbolicName, em);
        if (application != null) {
            application.setBundleSymbolicName(null);
            em.persist(application);
            LOGGER.log(Level.FINE, "Unregistered Web Desktop application from bundle {0}.",
                    bundleSymbolicName);
        } else {
            LOGGER.log(Level.FINEST, "Bundle {0} is not a Web Desktop application.",
                    bundleSymbolicName);
        }

    }
}
