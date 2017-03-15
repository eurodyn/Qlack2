package com.eurodyn.qlack2.webdesktop.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.webdesktop.api.SettingsService;
import com.eurodyn.qlack2.webdesktop.impl.mappers.QFSettingDTOMapperImpl;


@Singleton
@OsgiServiceProvider(classes = { SettingsService.class })
@Transactional
public class SettingsServiceImpl extends BaseService implements SettingsService {
	@OsgiService
	@Inject
	private com.eurodyn.qlack2.fuse.settings.api.SettingsService fuseSettingsService;
	
	// Mapstruct refs.
	private QFSettingDTOMapperImpl qfSettingDTOMapper = new QFSettingDTOMapperImpl();
	
	@Override
	public List<com.eurodyn.qlack2.webdesktop.api.dto.SettingDTO> getAll(String owner, boolean includeSensitive) {
		// TODO security check: owner == caller or owner == system + permission for getting sensitive
		return qfSettingDTOMapper.toSettingDTO(
				fuseSettingsService.getSettings(owner, true));
	}

	@Override
	public com.eurodyn.qlack2.webdesktop.api.dto.SettingDTO get(String owner, String setting, String group) {
		return qfSettingDTOMapper.toSettingDTO(fuseSettingsService.getSetting(
				owner, setting, group));
	}

	@Override
	public void set(String owner,  com.eurodyn.qlack2.webdesktop.api.dto.SettingDTO setting) {
		fuseSettingsService.setVal(owner, setting.getKey(), setting.getVal(), 
				setting.getGroup());
	}
}
