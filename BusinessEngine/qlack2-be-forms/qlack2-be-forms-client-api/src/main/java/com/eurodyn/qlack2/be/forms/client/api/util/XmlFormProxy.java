package com.eurodyn.qlack2.be.forms.client.api.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A proxy object providing access to the XML contents of an XForm through
 * XPath. Using this proxy object to access the form contents enables
 * rules/forms to be added/updated dynamically, without the need to create new
 * objects for each new form type.
 *
 * @author European Dynamics S.A.
 */
public class XmlFormProxy implements Serializable {

	private static final long serialVersionUID = -3577694251666456141L;

	// The input XML
	private Document inputDoc;
	// The validation result XML
	private Document validationDoc;
	private transient XPathFactory factory;
	private boolean valid = true;

	public XmlFormProxy(String xml) throws ParserConfigurationException,
			SAXException, IOException {
		InputStream is = new ByteArrayInputStream(xml.getBytes());
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		inputDoc = builder.parse(is);

		factory = XPathFactory.newInstance();

		// Initialise the validation result to allow adding errors if necessary
		// during the validation
		createValidationResult();

	}

	private void createValidationResult() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		DOMImplementation domImpl = docBuilder.getDOMImplementation();
		validationDoc = domImpl.createDocument(
				"http://www.qlack.com/qbe/validation",
				"v:validation-result", null);
		Element data = validationDoc.createElement("v:data");
		validationDoc.getDocumentElement().appendChild(data);
		Element globalErrors = validationDoc.createElement("v:global-errors");
		validationDoc.getDocumentElement().appendChild(globalErrors);
		Element existingData = inputDoc.getDocumentElement();
		Node existingDataNode = validationDoc.importNode(existingData, true);
		data.appendChild(existingDataNode);
	}

	private Node evaluateXPath(Document document, String expression)
			throws XPathExpressionException {
		if (factory == null) {
			factory = XPathFactory.newInstance();
		}

		XPath xpath = factory.newXPath();
		return (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
	}

	/**
	 * Returns the content of a form field identified by an XPath expression
	 *
	 * @param fieldExpression
	 *            An XPath expression specifying the field the content of which
	 *            should be returned
	 * @return The content of the field
	 * @throws XPathExpressionException
	 */
	public String getFieldContent(String fieldExpression)
			throws XPathExpressionException {
		Node node = evaluateXPath(inputDoc, fieldExpression);
		return node.getTextContent();
	}

	/**
	 * Adds an error to a form field
	 *
	 * @param fieldExpression
	 *            An XPath expression identifying the field to which the error
	 *            should be added
	 * @param errorMessage
	 *            The error message
	 * @throws XPathExpressionException
	 */
	public void addFieldError(String fieldExpression, String errorMessage)
			throws XPathExpressionException {
		Node node = evaluateXPath(validationDoc, fieldExpression);
		NamedNodeMap attributes = node.getAttributes();
		Attr validAttr = validationDoc.createAttribute("v:valid");
		validAttr.setValue("false");
		attributes.setNamedItem(validAttr);
		Attr alertAttr = validationDoc.createAttribute("v:alert");
		alertAttr.setValue(errorMessage);
		attributes.setNamedItem(alertAttr);
		valid = false;
	}

	/**
	 * Adds a global error to the form
	 *
	 * @param errorMessage
	 *            The error message
	 */
	public void addGlobalError(String errorMessage) {
		Node globalErrorsNode = validationDoc.getDocumentElement()
				.getElementsByTagName("v:global-errors").item(0);
		Element errorNode = validationDoc.createElement("v:global-error");
		NamedNodeMap attributes = errorNode.getAttributes();
		Attr alertAttr = validationDoc.createAttribute("v:alert");
		alertAttr.setValue(errorMessage);
		attributes.setNamedItem(alertAttr);
		globalErrorsNode.appendChild(errorNode);
		valid = false;
	}

	public Document getValidationResult() {
		return validationDoc;
	}

	public boolean isValid() {
		return valid;
	}
}
