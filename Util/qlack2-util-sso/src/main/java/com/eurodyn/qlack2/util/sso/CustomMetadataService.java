package com.eurodyn.qlack2.util.sso;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.cxf.rs.security.saml.sso.MetadataService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An extension of {@link MetadataService} allowing metadata-XML to be further customised.
 */
@Path("metadata")
public class CustomMetadataService extends MetadataService {

  /**
   * Various XML paths to be used while manipulating the metadata
   */
  private static final String XML_TAG_MD_NS = "urn:oasis:names:tc:SAML:2.0:metadata";
  private static final String XML_TAG_SIGNATURE = "Signature";
  private static final String XML_TAG_KEYDESCRIPTOR = "KeyDescriptor";
  private static final String XML_TAG_SPSSODESCRIPTOR = "SPSSODescriptor";
  private static final String XML_TAG_SINGLELOGOUTSERVICE = "SingleLogoutService";

  /**
   * Skips the <Signature> part of the metadata
   */
  private boolean skipSignature;

  public void setSkipSignature(boolean skipSignature) {
    this.skipSignature = skipSignature;
  }

  @Override
  @GET
  @Produces("text/xml")
  public Document getMetadata() {
    final Document metadata = super.getMetadata();

    /** Check if the signature part should be removed */
    if (skipSignature) {
      Element signatureElement = (Element) metadata.getElementsByTagName(XML_TAG_SIGNATURE).item(0);
      signatureElement.getParentNode().removeChild(signatureElement);
    }

    /** Rearrange XML to be compliant with the underlying schema */
    Element keyDescriptorElement = (Element) metadata
      .getElementsByTagNameNS(XML_TAG_MD_NS, XML_TAG_KEYDESCRIPTOR).item(0);
    keyDescriptorElement.getParentNode().removeChild(keyDescriptorElement);
    Node singleLogoutNode = metadata
      .getElementsByTagNameNS(XML_TAG_MD_NS, XML_TAG_SINGLELOGOUTSERVICE).item(0);
    metadata.getElementsByTagNameNS(XML_TAG_MD_NS, XML_TAG_SPSSODESCRIPTOR).item(0)
      .insertBefore(keyDescriptorElement, singleLogoutNode);

    return metadata;
  }
}
