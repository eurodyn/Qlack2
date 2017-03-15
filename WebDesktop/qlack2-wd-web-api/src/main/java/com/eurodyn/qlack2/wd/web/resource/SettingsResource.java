package com.eurodyn.qlack2.wd.web.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.ops4j.pax.cdi.api.OsgiService;

import com.eurodyn.qlack2.common.util.util.TokenHolder;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SettingsService;
import com.eurodyn.qlack2.webdesktop.api.constants.LVSettings;
import com.eurodyn.qlack2.webdesktop.api.dto.SettingDTO;

/**
 * REST handler for setting settings. 
 */
@Path("/settings")
@Singleton
public class SettingsResource {
	// Services refs.
	@OsgiService @Inject
	private SettingsService settingsService;

	@Path("/system")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<SettingDTO> getSystemSettings(@QueryParam("sensitive")
		@DefaultValue("false") String includeSensitive) {
		List<SettingDTO> settings = settingsService.getAll(LVSettings.SYSTEM_OWNER, 
				Boolean.parseBoolean(includeSensitive));
		
		return settings;
	}

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<SettingDTO> getUserSettings() {
		return settingsService.getAll(
			SignedTicket.fromVal(TokenHolder.getToken()).getUserID(), true);
	}

	@Path("/system")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void setSystemSettings(List<SettingDTO> settings) {
		for (SettingDTO setting : settings) {
			settingsService.set(LVSettings.SYSTEM_OWNER, setting);
		}
	}

	@Path("/")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void setUserSettings(List<SettingDTO> settings) {
		for (SettingDTO setting : settings) {
			settingsService.set(
					SignedTicket.fromVal(TokenHolder.getToken()).getUserID(), setting);
		}
	}
}
