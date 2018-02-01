package com.eurodyn.qlack2.fuse.settings.impl;

import com.eurodyn.qlack2.common.util.exception.QAlreadyExistsException;
import com.eurodyn.qlack2.common.util.exception.QDoesNotExistException;
import com.eurodyn.qlack2.fuse.settings.api.CASettingsService;
import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;
import com.eurodyn.qlack2.fuse.settings.api.exception.QSettingsException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Singleton
@OsgiServiceProvider(classes = {CASettingsService.class})
public class CASettingsServiceImpl implements CASettingsService {

  // Reference to OSGi Config Admin service.
  @OsgiService
  @Inject
  private ConfigurationAdmin configAdmin;

  /**
   * Parses a PID configuration via Config Admin.
   *
   * @param pid The name of the PID (i.e. .cfg file without the .cfg extension).
   * @return The configuration held by the PID.
   */
  private Configuration getConfigAdminPID(String pid) {
    // Return value.
    Configuration configuration;

    try {
      configuration = configAdmin.getConfiguration(pid);
    } catch (IOException e) {
      throw new QSettingsException("Could not parse PID: ", e);
    }

    return configuration;
  }

  /**
   * Reads a .cfg file from Config Admin and returns all properties found.
   *
   * @param pid The name of the PID (i.e. .cfg file without the .cfg extension).
   * @return Returns the list of all properties in PID.
   */
  private Dictionary<String, Object> getConfigAdminProps(String pid) {
    return getConfigAdminPID(pid).getProperties();
  }

  /**
   * Updates config admin PID with the given properties.
   *
   * @param pid The name of the PID (i.e. .cfg file without the .cfg extension).
   * @param configAdminProps The dictionary object holding the properties to be updated.
   */
  private void setConfigAdminProps(String pid, Dictionary<String, Object> configAdminProps) {
    try {
      getConfigAdminPID(pid).update(configAdminProps);
    } catch (IOException e) {
      throw new QSettingsException("Could not update Config Admin.", e);
    }
  }

  @Override
  public List<SettingDTO> getSettings(String pid) {
    // Return value.
    List<SettingDTO> settings = new ArrayList<>();

    // Fetch settings from Config Admin.
    final Dictionary<String, Object> configAdminProps = getConfigAdminProps(pid);
    final Enumeration<String> keys = configAdminProps.keys();

    // Convert values to DTOs.
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      settings.add(new SettingDTO(key, (String) configAdminProps.get(key)));
    }

    return settings;
  }

  @Override
  public Optional<SettingDTO> getSetting(String pid, String key) {
    return getSettings(pid).stream().filter(o -> o.getKey().equals(key)).findFirst();
  }

  @Override
  public void createSetting(String pid, String key, String val) {
    // Check this setting does not already exist.
    if (getSetting(pid, key).isPresent()) {
      throw new QAlreadyExistsException(
        MessageFormat.format("Already found a setting with key: {0}.", key));
    }

    // Create the key.
    final Dictionary<String, Object> configAdminProps = getConfigAdminProps(pid);
    configAdminProps.put(key, val);
    setConfigAdminProps(pid, configAdminProps);
  }

  @Override
  public void setVal(String pid, String key, String val) {
    // Check this setting does not already exist.
    if (!getSetting(pid, key).isPresent()) {
      throw new QDoesNotExistException(MessageFormat.format("Could not find key: {0}.", key));
    }

    // Set the key.
    final Dictionary<String, Object> configAdminProps = getConfigAdminProps(pid);
    configAdminProps.put(key, val);
    setConfigAdminProps(pid, configAdminProps);
  }
}
