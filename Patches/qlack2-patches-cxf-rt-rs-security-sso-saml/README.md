# Patch for SAML Web SSO signature validation

This is to patch `cxf-rt-rs-security-sso-saml` to support signature
validation on SAML Responses.
See [relevant discussion](http://cxf.547215.n5.nabble.com/JAX-RS-SAML-Web-SSO-Validating-SAML-Response-in-OSGi-td5781687.html).

The change consists of patching `org.apache.cxf.rs.security.saml.sso.SAMLProtocolResponseValidator`
in CXF 3.1.8 similarly to this
[GitHub commit](https://github.com/apache/cxf/commit/0287f7d3a30908cdde0ca30f470f2103dab26a7c#diff-4869efd874233eb9778d8d26b4bdc49c).

In addition to this patch, we are also preparing a replacement Karaf
feature for `cxf-rs-security-sso-saml` (`qlack-patch-cxf-rs-security-sso-saml`)
which contains the patched JAR.

## When this patch should be removed
* A new CXF version containing this [GitHub commit](https://github.com/apache/cxf/commit/0287f7d3a30908cdde0ca30f470f2103dab26a7c#diff-4869efd874233eb9778d8d26b4bdc49c)
 is released.
* A new Karaf version containing the above release is also released.

## Version checks
Here are the version of Karaf we have already checked to be non-conformant
(e.g. they do not contain a CXF version with the above commit included):

* Karaf version: `4.0.9` (contains CXF version: `3.1.8`)