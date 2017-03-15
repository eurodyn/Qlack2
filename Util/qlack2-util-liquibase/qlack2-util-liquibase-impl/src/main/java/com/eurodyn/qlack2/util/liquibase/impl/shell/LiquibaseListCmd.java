package com.eurodyn.qlack2.util.liquibase.impl.shell;


import com.eurodyn.qlack2.util.liquibase.api.MigrationService;
import com.eurodyn.qlack2.util.liquibase.api.QChangeLogEntry;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "qlack", name = "liquibase-list", description = "Shows all currently queued changelogs.")
@Service
public final class LiquibaseListCmd  implements Action {
    @Reference
    MigrationService migrationService;

    @Override
    public Object execute() throws Exception {
        final QChangeLogEntry[] list = migrationService.list();

        return list.length == 0 ? "No changelogs queued." : list;
    }
}
