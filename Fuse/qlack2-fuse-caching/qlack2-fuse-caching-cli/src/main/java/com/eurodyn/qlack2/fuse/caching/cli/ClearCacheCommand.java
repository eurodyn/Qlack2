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
package com.eurodyn.qlack2.fuse.caching.cli;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Command(scope = "qlack", name = "caching-list", description = "List entries currently in cache.")
@Service
public final class ListCacheCommand implements Action {

  @Argument(index = 0, name = "filter", description = "A regex to filter by.", required = false,
    multiValued = false)
  private String filter;

  @Argument(index = 1, name = "keyCutoff", description = "The maximum number of characters to display for each key.",
    required = false, multiValued = false)
  private int keyCutoff = 36;

  @Argument(index = 2, name = "valueCutoff", description = "The maximum number of characters to display for each value.",
    required = false, multiValued = false)
  private int valueCutoff = 255;

  @Reference
  private CacheService cacheService;

  @Override
  public Object execute() {
    /** Get all keys */
    Set<String> keyNames = cacheService.getKeyNames();
    System.out.println("Total keys: " + keyNames.size());

    /** Filter keys if requested so */
    if (filter != null && filter.length() > 0) {
      keyNames = keyNames.parallelStream().filter(k -> k.matches(filter)).collect(Collectors.toSet());
    }

    for (String keyName : keyNames) {
      System.out.format("\t%." + keyCutoff + "s %."+ valueCutoff + "s\n", keyName, cacheService.get(keyName));
    }

    System.out.println("Keys displayed: " + keyNames.size());

    return null;
  }

}
