# Karaf helpers & Tips

* Quickly update everything after a git-pull with many changes
````
    start org.openengsb.labs.liquibase.extender; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-aaa-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-aaa-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-aaa-commands; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-crypto-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-crypto-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-idm-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-idm-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-lexicon-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-lexicon-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-security-proxy-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-security-proxy-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-settings-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-settings-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-ticket-server-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-ticket-server-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-ticket-server-commands; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-file-upload-api; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-file-upload-api-rest; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-file-upload-impl; \
    update com.eurodyn.qlack2.fuse.qlack2-fuse-file-upload-impl-rest; \
    update com.eurodyn.qlack2.wd.qlack2-wd-api; \
    update com.eurodyn.qlack2.wd.qlack2-wd-impl; \
    stop org.openengsb.labs.liquibase.extender
````
Remember to keep Liquibase extended stopped as it leaks connections.