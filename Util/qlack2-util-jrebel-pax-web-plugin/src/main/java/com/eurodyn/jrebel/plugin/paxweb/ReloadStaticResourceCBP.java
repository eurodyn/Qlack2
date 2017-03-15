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


import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.NotFoundException;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class ReloadStaticResourceCBP extends JavassistClassBytecodeProcessor {

	@Override
	public void process(ClassPool cp, ClassLoader cl, CtClass ctClass)
			throws Exception {
		LoggerFactory.getInstance().echo(
				"Patching the WebAppWebContainerContext class of Pax Web..");

		try {
			CtMethod getResourceMethod = ctClass.getDeclaredMethod("getResource");
			// Looking for the name used for the bundle field since this is different between different Pax Web versions.
			LoggerFactory.getInstance().echo("Looking for bundle field...");
			String bundleFieldName = null;
			String logFieldName = null;
			CtField[] fields = ctClass.getFields();
			for (CtField field : fields) {
				if (field.getName().equals("bundle")) {
					LoggerFactory.getInstance().echo("bundle field found");
					bundleFieldName = "bundle";
					logFieldName = "log";
					break;
				}
				else if (field.getName().equals("m_bundle")) {
					LoggerFactory.getInstance().echo("m_bundle field found");
					bundleFieldName = "m_bundle";
					logFieldName = "LOG";
					break;
				}
			}
			
			if (bundleFieldName == null) {
				LoggerFactory.getInstance().error("Could not find bundle field - Pax Web will not be patched");
				return;
			}
//			getResourceMethod.insertAfter("{java.net.URL rebel = " + bundleFieldName + ".getResource(\"rebel.xml\");" +
			getResourceMethod.insertAfter("{java.util.Enumeration rebelFiles = " + bundleFieldName + ".getResources(\"rebel.xml\");" +
					"if (rebelFiles != null) {" +
						"while (rebelFiles.hasMoreElements()) {" +
							"synchronized(this) {" +
								"java.net.URL rebel = (java.net.URL)rebelFiles.nextElement();" +						
								logFieldName + ".debug(\"Attempting to get static resources from JRebel\");" +
								"java.io.InputStream is = rebel.openStream();" +
								"org.w3c.dom.Document rebelDoc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);" +
								"org.w3c.dom.NodeList linkNodes = rebelDoc.getElementsByTagName(\"link\");" +
								"for (int i = 0; i < linkNodes.getLength(); i++) {" +
									"org.w3c.dom.Node dirNode = linkNodes.item(i).getChildNodes().item(1);" +
									"if (dirNode.getNodeName().equals(\"dir\")) {" +
										"String resourcePath = dirNode.getAttributes().getNamedItem(\"name\").getNodeValue();" +
										"String location = resourcePath + name;" +
										"if (new java.io.File(location).exists()) {" +
											logFieldName + ".debug(\"Static resource found at \" + location);" +
											"$_ = new java.net.URL(\"file:///\" + location);" +
											"break;" +
										"}" +
									"}" +
								"}" +
							"}" +
						"}" +
					"}}");
		} catch (NotFoundException e) {
			LoggerFactory.getInstance().error(e);
		}
	}

}
