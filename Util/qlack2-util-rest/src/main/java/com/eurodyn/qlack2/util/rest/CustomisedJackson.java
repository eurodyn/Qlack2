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
package com.eurodyn.qlack2.util.rest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @deprecated You should use a {@link javax.ws.rs.ext.ContextResolver} defined specifically for
 * your project. This class will be removed in future releases.
 * Example: https://stackoverflow.com/questions/14400193/jackson-json-not-working-with-cxf
 */
public class CustomisedJackson extends ObjectMapper {

  public CustomisedJackson() {
    super();
  }

  public void setAcceptSingleValueAsArray(boolean state) {
    super.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, state);
  }

  public void setSerialiseNullValues(boolean b) {
    super.setSerializationInclusion(b ? Include.ALWAYS : Include.NON_EMPTY);
  }

  public void setWriteDatesAsTimestamps(boolean b) {
    super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, b);
  }

  public void setWriteDatesAsNanoseconds(boolean b) {
    super.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, b);
  }
}
