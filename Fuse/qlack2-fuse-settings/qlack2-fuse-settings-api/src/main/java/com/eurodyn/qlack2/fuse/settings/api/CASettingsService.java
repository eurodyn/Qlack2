package com.eurodyn.qlack2.fuse.settings.api;

import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;

/**
 * Provides functionality to set and retrieve settings held in OSGi Config Admin.
 *
 * @author European Dynamics SA.
 */
public interface CASettingsService {
  SettingDTO getSetting(String key, String pid);
  void setSetting(String key, String value, String pid);

}
