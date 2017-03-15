# Installation of QBE (for developers)

## Prerequisites

* JDK 1.8.x
* Karaf 4.0.3
* MySQL 5.7.10

## Cloning from GIT (branch v2, wherever it exists)

    TODO

## Installation on Karaf

### Install QBE Karaf features

    feature:repo-add mvn:com.eurodyn.qlack2.be/qlack2-be-karaf-features/2.0.0/xml/features

### Install DB connectivity feature and automatic Liquibase update

    feature:install qlack2-util-repack-mysql; \
    feature:install qlack2-util-repack-liquibase

### Configure the QBE datasource

    config:edit org.ops4j.datasource-qbe; \
    config:property-set user root; \
    config:property-set password root; \
    config:property-set url jdbc:mysql://localhost/qbe?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&connectionCollation=utf8mb4_unicode_ci&createDatabaseIfNotExist=true; \
    config:property-set dataSourceName qbe-ds; \
    config:property-set osgi.jdbc.driver.name mysql-pool-xa; \
    config:property-set pool.testOnBorrow true; \
    config:property-set pool.maxTotal 50; \
    config:property-set pool.maxIdle 10; \
    config:property-set pool.minIdle 1; \
    config:update

### Configure Liquibase to use the 'wd' schema and 'qlack-wd-ds' datasource

    config:edit org.openengsb.labs.liquibase; \
    config:property-set defaultSchema qbe; \
    config:property-set datasource qbe-ds; \
    config:update

### Set UTF8 configuration for your database

    jdbc:execute qbe-ds alter database qbe DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci

### Create a datasource JNDI alias for QLACK2 components

    jndi:alias osgi:service/qbe-ds /osgi:service/qlack2-ds


### Choose the cluster implementation to use
#### Dummy cluster
A dummy cluster implementation, suitable only for local use.
    feature:install qlack2-util-cluster-dummy
    
#### Hazelcast-based cluster
Hazelcast will use the first available interface it finds. Check your Karaf's output
and make sure this is the interface you are actually using and not an interface
used privately for VMs, etc. To configure hazelcast to use a different interface
issue the following command by replacing 127.0.0.1 with the IP address you need:
    config:edit com.eurodyn.qlack2.util.cluster.hazelcast; \
    config:property-set interfaces 127.0.0.1; \
    config:update
    
    feature:install qlack2-util-cluster-hazelcast


### Configure Security Proxy to use the correct security token name

    config:edit com.eurodyn.qlack2.fuse.security.proxy; \
    config:property-set ticket.header.name X-Qlack-Fuse-IDM-Token-QBE; \
    config:update


### Configure EventAdmin to ignore timeouts for Fuse AAA

    config:edit org.apache.felix.eventadmin.impl.EventAdmin; \
    config:property-set org.apache.felix.eventadmin.IgnoreTimeout com.eurodyn.qlack2.fuse.aaa.impl.listeners.; \
    config:update
	
	
	
	
### Install the WD back-end feature (without the static web content)

    feature:install qlack2-wd-back-end


## Running and developing for the front-end

    # Goto the ESCP web folder
    cd qlack2-escp-web

    # Update node components
    npm install

    # Update bower components
    bower install

    # Start the embedded live-reload web server
    gulp build webserver
    
**Note:** In case of the following or similar error on **bower install** or **bower update**:

    ECMDERR Failed to execute "git ls-remote --tags --heads git://github.com/twbs/bootstrap.git", exit code of #128
        
run the following command on a terminal and try again:
    
    git config --global url."https://".insteadOf git://

## Setting up proxy for NodeJS/Bower/Gulp

TBD

## Creating a defaut user

The ESCP users database comes empty, so you need a default user to login with.
Go to your Karaf console and execute:

    qlack:aaa-user-add user1 jijikos true 1

You now have a user with:
username: user1
password: jijikos
