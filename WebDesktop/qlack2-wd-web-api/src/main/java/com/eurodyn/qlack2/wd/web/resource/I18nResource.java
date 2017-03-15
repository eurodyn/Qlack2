package com.eurodyn.qlack2.wd.web.resource;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.ops4j.pax.cdi.api.OsgiService;

import com.eurodyn.qlack2.webdesktop.api.I18NService;
import com.eurodyn.qlack2.webdesktop.api.dto.LexiconLanguageDTO;

@Singleton
@Path("i18n")
public class I18nResource {
	@OsgiService @Inject
	private I18NService i18nService;

	@GET
	@Path("languages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LexiconLanguageDTO> getActiveLanguages() {
		return i18nService.getActiveLanguages();
	}

	@GET
	@Path("translations/{groupName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> getModuleTranslations(@PathParam("groupName") String groupName, @QueryParam("lang") String locale) {
		return i18nService.getModuleTranslations(groupName, locale);
	}

	@GET
	@Path("translations")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Map<String, String>> getTranslations(@QueryParam("lang") String locale) {
		return i18nService.getTranslations(locale);
	}
}
