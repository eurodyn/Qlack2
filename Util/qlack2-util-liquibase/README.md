#qlack2-util-liquibase
A Liquibase changelog executor with a few different execution strategies.

## How to discover liquibase scripts in your bundles
### Automatically (discoveryMode=AUTO)
Your `MANIFEST.MF` should have the `Q-Liquibase-ChangeLog` header. Optionally, 
you can also specify a priority level by using the `Q-Liquibase-Priority` header.
Here's how you could configure those using `maven-bundle-plugin`:

```
<plugin>
    <groupId>org.apache.felix</groupId>
    <artifactId>maven-bundle-plugin</artifactId>
    <configuration>
        <instructions>
            ...
            <Q-Liquibase-ChangeLog>db/myproject-changelog.xml</Q-Liquibase-ChangeLog>
            <Q-Liquibase-Priority>100</Q-Liquibase-Priority>
        </instructions>
    </configuration>
</plugin>
```

- `Q-Liquibase-ChangeLog` should point to the location of the changelog into your JAR.
- `Q-Liquibase-Priority` can be any arbitrary number to specify the order in which
this changelog should be executed when queued among other changelogs. See the
_Migration strategies_ part for more details.

### Manually (discoveryMode=MANUAL)  
Automatic discovery of liquibase-headers may be problematic on a complex environment
when multiple bundles start up at different levels. On such cases, it is recommended
to manually register your bundle's liquibase scripts using the provided `MigrationService`.

    MigrationService.registerBundleForMigrations(Bundle)
    MigrationService.unregisterBundleForMigrations(Bundle)

## Migration strategies
The reason that led us to develop this component (and not using something like
[this](https://github.com/openengsb-labs/labs-liquibase) which we have indeed
used in the past) was that we wanted to have some extra-control over the exact 
point in time at which changelogs are executed. To that front, we support three
different migration execution strategies.
- `ASAP`  
This executed a changelog as soon as it is discovered. Be careful when using
such as migration strategy as some of your OSGi bundles may keep loading or
being deployed when changelogs from other already deployed bundles have began to
execute. With this migration strategy, the `Q-Liquibase-Priority` header plays
little, if any role, and changelogs from bundles are - mor or less - executed
according to the bundle's start-level.
- `MANUAL`  
This is a migration strategy mainly to be used when developing and testing. The
qlack2-util-liquibase will still find your changelogs but will never automatically
execute them. All changelogs detected will be queued and may only be executed
manually. To trigger a manual execution you have two different options:
  - Using Karaf's console via the `qlack:liquibase-list` and `qlack:liquibase-run`
  commands.
  - From your own code by getting a reference to the MigrationService service
  exposed in OSGi by this component.  

  _Please note that MANUAL migration strategy is different and completely unrelated
   to the MANUAL discovery mode._
- `START_LEVEL`  
This is the most interesting feature of this component. When using this migration
strategy all changelogs discovered by bundles being deployed are internally queued
and nothing gets executed immediately. Instead, we monitor the current start-level
of the OSGi framework and only when it reached a user-specified level changelogs
start to be executed based on their priority levels. This is particularly useful
when OSGi bootstrap and several bundles are all deployed at once. As soon as the
predefined start-level is reached and the enqueued changelogs applies, the component
applies newly deployed changelogs immediately.

## Datasource injection failsafe
A problem we have faced using other similar conponents in the past was that the
datasource used for changelogs execution was not always available at the time
when those changelogs were executed. This was frequently the case when the OSGi
framework was cold-booting since PAX-JDBC (that we're using to handle datasources) 
starts - by default - at level 80. Even if we manually set it on a lower start-level
we could never be sure that it had enough time to read its configuration from OSGi's
config-admin, create the datasource and register it in JDNI.

This component actively monitors the existence of the user-defined Liquibase 
datasource in JNDI and only when it becomes available it starts applying changes
thus eliminating the problem of breaking because a datasource might not have been
available at the time of changelogs execution.
 
## Config admin configuration file
The following options can be defined in config-admin for this component; they
should be placed under `etc/com.eurodyn.qlack2.util.liquibase.cfg`.

- `migrationExecutionStrategy`   
Type: `ASAP`, `MANUAL`, `START_LEVEL`.   
Default value: `START_LEVEL`  
_Sets the changelogs execution strategy to be used._

- `datasource`  
Type: String  
Example: myProjectDS  
_The name of the datasource to use to execute changelogs._

- `maxWaitForDS`  
Type: Long / msec  
Default value: 180000  
_The number of msec the component is waiting for the datasource to appear in
JDNI. If this duration expires without having managed to obtain a reference to
the datasource the component fails._

- `maxWaitForSL`
Type: Long / msec   
Default value: 300000  
_The number of msec the component is waiting for requested start-level to be 
reached. If this duration expires without having reached the desired start-level
the component fails._

- `bootCompleteSL`  
Type: Integer  
Default value: 100  
_The start-level in which you consider bootstrap having finished. Be careful that
 at least one of the bundles being deployed in your OSGi framework are actually
 set to start at this level._
 
- `initialExecutionDelay`  
Type: Long / msec   
Default value: 5000  
_As soon as all preconditions are met (the migration strategy as well as the 
registration of the datasource), this is an additional delay being applied before
changelogs start to be executed._

- `discoveryMode`  
Type: `AUTO`, `MANUAL`  
Default value: `AUTO`  
_Sets the mode in which liquibase scripts are discovered in your bundles. `AUTO`
mode automatically looks up for the `Q-Liquibase-ChangeLog` header when your
bundle is deployed and if found queues it for migrations executions; `MANUAL`
does nothing during bundle deployment and you need to manually queue your bundle.

- `liquibase.*`  
You may pass parameters to the underlying liquibase driver using the `liquibase.`
prefix, e.g. `liquibase.defaultSchema myappschema`, `liquibase.myvar1 val1`, etc.
Parameters passed this way can also be used in your liquibase changelogs, referenced
with `${}`, e.g. `${myvar1}`.