# QLACK Fuse mailing

## Authentication
You do not need to set a specific setting to enable/disable authentication
other than providing values for `server.user` and `server.password`. If
these are set, your connection to the SMTP will be authenticated.

## TLS
We support STARTTLS (no support for SSL on a custom port). To enable it
you need to set `server.starttls` to true.

### Self-signed certificates
In case your SMTP uses a self-signed certificate (which will be the case
when working in a development environment) you need to import its
certifcate into your local `cacerts` file. Here are the steps to follow:

#### Find the location of your `cacerts` file.
Windows: {TBC}

Mac: `cd $(/usr/libexec/java_home)/jre/lib/security`

Linux: {TBC}

#### Extract the certificate of your SMTP server
`openssl s_client -connect smtphost:25 -starttls smtp`

From the above output, copy in a file e.g. `smtp-dev.cert`, the content
In `-----BEGIN CERTIFICATE-----` and `-----END CERTIFICATE-----`.

#### Insert the certificate into the keystore
`sudo keytool -import -alias smtpdev -file smtp-dev.cert --keystore cacerts`

(_Default keystore pass is: changeit_)