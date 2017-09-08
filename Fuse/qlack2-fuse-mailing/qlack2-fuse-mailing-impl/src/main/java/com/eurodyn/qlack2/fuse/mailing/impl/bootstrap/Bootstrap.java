package com.eurodyn.qlack2.fuse.mailing.impl.bootstrap;

import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.aries.blueprint.annotation.config.Config;
import org.apache.aries.blueprint.annotation.config.DefaultProperty;
import org.ops4j.pax.cdi.api.OsgiService;

@Config(pid = "com.eurodyn.qlack2.fuse.mailing", defaults = {
  @DefaultProperty(key = "interval", value = "10000"),
  @DefaultProperty(key = "maxTries", value = "3"),
  @DefaultProperty(key = "debug", value = "false"),
  @DefaultProperty(key = "server.host", value = "localhost"),
  @DefaultProperty(key = "server.port", value = "25"),
  @DefaultProperty(key = "server.user", value = ""),
  @DefaultProperty(key = "server.password", value = ""),
  @DefaultProperty(key = "server.starttls", value = "false")
})
@Singleton
public class Bootstrap {

  @Inject
  @OsgiService
  /** Make sure liquibase migrations are executed before allowing access
   * to this bundle.
   */
    LiquibaseBootMigrationsDoneService liquibaseBootMigrationsDoneService;

  @PostConstruct
  public void init() {
  }
}
