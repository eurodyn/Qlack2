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
package com.eurodyn.qlack2.fuse.componentlibrary.impl;

import java.util.logging.Logger;

//TODO Mock...

/**
 *
 * @author European Dynamics SA
 */
//@MessageDriven(mappedName = "jms/QlackAPIQueue", activationConfig =  {
//        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
//        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
//    })
//public class ComponentMDB implements MessageListener {
	public class ComponentMDB {

    private static final Logger LOGGER = Logger.getLogger(ComponentMDB.class.getName());

    public ComponentMDB() {
    }


//    public void onMessage(Message message) {
//
//        HttpClient client = null;
//        HttpMethod method = null;
//        try {
//            HashMap payload = (HashMap)((ObjectMessage)message).getObject();
//            if (((String)payload.get("action")).equals("http_call")) {
//                // Delay the call, so that we give a chance to the server to properly commit its own transaction.
//                Thread.sleep(Long.parseLong(PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.API.gadgets.notification.delay")));
//                client = new HttpClient();
//                if (!StringUtils.isEmpty(PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.API.httpClient.proxy.host")) &&
//                   (!StringUtils.isEmpty(PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.API.httpClient.proxy.port")))) {
//                    String proxyHost = PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.API.httpClient.proxy.host");
//                    String proxyPort = PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.API.httpClient.proxy.port");
//                    if ((!StringUtils.isEmpty(proxyPort)) && (!StringUtils.isEmpty(proxyPort))) {
//                        client.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
//                    }
//                }
//                String url = (String)payload.get("url");
//                method = new GetMethod(url);
//                LOGGER.log(Level.FINEST, "Executing HTTP call: {0}", url);
//                client.executeMethod(method);
//                method.getResponseBody();
//            }
//        // We do not want to propagate any other exception to avoid a poison message situation,
//        // therefore we simply log it.
//        } catch (IOException ex) {
//            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//        } catch (JMSException ex) {
//            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//        } catch (InterruptedException ex) {
//            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//        }
//        finally {
//            if (method != null) {
//                method.releaseConnection();
//            }
//        }
//    }
}