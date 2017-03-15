/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.jrebel.plugin.paxweb;

import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.Integration;
import org.zeroturnaround.javarebel.IntegrationFactory;
import org.zeroturnaround.javarebel.Plugin;

public class PaxWebPlugin implements Plugin {

	public boolean checkDependencies(ClassLoader classLoader, ClassResourceSource classResourceSource) {
		return classResourceSource.getClassResource("org.ops4j.pax.web.extender.war.internal.WebAppWebContainerContext") != null;
	}

	public String getAuthor() {
		return "European Dunamics S.A.";
	}

	public String getDescription() {
		return "A JRebel plugin to enable reloading static resources with Pax Web WAR Extender";
	}

	public String getId() {
		return "pax-web-plugin";
	}

	public String getName() {
		return "Pax Web JRebel Plugin";
	}

	public String getSupportedVersions() {
		return "Pax Web 3.0+";
	}

	public String getTestedVersions() {
		return "Pax Web 3.0.1";
	}

	public String getWebsite() {
		return null;
	}

	public void preinit() {
		// Register the CBP
	    Integration i = IntegrationFactory.getInstance();
	    ClassLoader cl = PaxWebPlugin.class.getClassLoader();	    
	    i.addIntegrationProcessor(cl, "org.ops4j.pax.web.extender.war.internal.WebAppHttpContext", new ReloadStaticResourceCBP());
	}

}
