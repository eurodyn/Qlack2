# qlack2-wdapps-demo-security-external
This is a sample app to demonstrate how a Web Desktop application running on a domain
different than Web Desktop itself, can still communicate with Web Desktop.

## How to run
`gulp serve`

## Register the application to Web Desktop
To make this application visible to Web Desktop you need to let it know of its 
existence (as well as its name, icon, color, etc.). The actual application can
run on any domain/server/URL.

Grab [qlack2-wdapps-demo-security-external.jar](https://github.com/eurodyn/Qlack2-WebDesktopApps/blob/develop/qlack2-wdapps-demo-security-external/OSGI/qlack2-wdapps-demo-security-external.jar)
and copy it under your Apache Karaf running Web Desktop in the folder `deploy`.
