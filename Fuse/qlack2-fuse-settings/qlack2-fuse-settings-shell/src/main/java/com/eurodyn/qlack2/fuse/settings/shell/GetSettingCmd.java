package com.eurodyn.qlack2.fuse.settings.shell;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.eurodyn.qlack2.fuse.settings.api.SettingsService;
import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;

@Command(scope = "qlack", name = "settings-get-setting", description = "Gets an existing setting from the database.")
@Service
public final class GetSettingCmd implements Action {
	@Reference
	SettingsService settingsService;

	@Argument(index = 0, name = "ownerID", description = "The ID of the owner key name to retrieve.", required = true, multiValued = false)
	private String ownerID;
	@Argument(index = 1, name = "group", description = "The group under which the key exists.", required = true, multiValued = false)
	private String group;
	@Argument(index = 2, name = "key", description = "The key name of the setting to retrieve.", required = true, multiValued = false)
	private String key;

	@Override
	public Object execute() {
		SettingDTO setting = settingsService.getSetting(ownerID, key, group);
		return setting != null ? setting.getOwner() + ":" + setting.getGroup() + ":" + key 
				+ " = " + setting.getVal() : "Not found.";
	}

}
