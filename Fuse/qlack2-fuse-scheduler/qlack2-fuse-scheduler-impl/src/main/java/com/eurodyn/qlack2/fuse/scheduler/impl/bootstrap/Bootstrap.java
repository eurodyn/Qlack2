package com.eurodyn.qlack2.fuse.scheduler.impl.bootstrap;

import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.aries.blueprint.annotation.config.Config;
import org.apache.aries.blueprint.annotation.config.DefaultProperty;
import org.ops4j.pax.cdi.api.OsgiService;

@Config(pid = "com.eurodyn.qlack2.fuse.scheduler", defaults = {
  @DefaultProperty(key = "managedDS", value = "osgi:service/qlack2-ds"),
  @DefaultProperty(key = "nonManagedDS", value = "osgi:service/qlack2-ds-non-managed"),
  @DefaultProperty(key = "scheduler.enabled", value = "true")
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
