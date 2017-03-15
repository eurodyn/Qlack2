package com.eurodyn.qlack2.webdesktop.api;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.SettingDTO;

public interface SettingsService {
	List<SettingDTO> getAll(String owner, boolean includeSensitive);
	SettingDTO get(String owner, String setting, String group);
	void set(String owner, SettingDTO setting);
}
