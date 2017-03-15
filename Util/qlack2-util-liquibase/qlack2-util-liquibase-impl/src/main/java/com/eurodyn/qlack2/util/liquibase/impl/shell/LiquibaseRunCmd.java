package com.eurodyn.qlack2.util.liquibase.impl.shell;

import com.eurodyn.qlack2.util.liquibase.api.MigrationService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "qlack", name = "liquibase-run", description = "Executes currently queued changelogs.")
@Service
public final class LiquibaseRunCmd implements Action {
    @Reference
    MigrationService migrationService;

    @Override
    public Object execute() throws Exception {
        if (migrationService.list().length == 0) {
            return "No changelogs queued.";
        } else {
            migrationService.run();
            return "";
        }
    }
}
