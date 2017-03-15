package com.eurodyn.qlack2.be.forms.impl.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLNamespaceContext implements NamespaceContext {

	private final static String DEFAULT_NS = "DEFAULT";

	private Map<String, String> namespaces = new HashMap<>();

	public XMLNamespaceContext(String xml) throws Exception {

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader;
		try {
			InputStream isa = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			reader = inputFactory.createXMLStreamReader(isa);

			while (reader.hasNext()) {
				int evt = reader.next();
				if (evt == XMLStreamConstants.START_ELEMENT) {
					QName qName = reader.getName();
					if (qName != null) {
						if (qName.getPrefix() != null) {
							if ("".equals(qName.getPrefix())) {
								namespaces.put(DEFAULT_NS,
										qName.getNamespaceURI());
							} else {
								namespaces.put(qName.getPrefix(),
										qName.getNamespaceURI());
							}
						}
					}
				}
			}

		} catch (XMLStreamException | UnsupportedEncodingException e) {
			throw e;
		}

	}

	@Override
	public String getNamespaceURI(String prefix) {
		// bound prefix Namespace URI bound to prefix in current scope
		// unbound prefix XMLConstants.NULL_NS_URI("")

		if (prefix == null)
			throw new IllegalArgumentException();
		else if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
			if (namespaces.containsKey(DEFAULT_NS)) {
				return namespaces.get(DEFAULT_NS);
			} else {
				return XMLConstants.NULL_NS_URI;
			}
		} else if (XMLConstants.XML_NS_PREFIX.equals(prefix))
			return XMLConstants.XML_NS_URI;
		else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix))
			return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
		else if (namespaces.containsKey(prefix))
			return namespaces.get(prefix);

		return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return getKeyByValue(namespaces, namespaceURI);
	}

	@Override
	public Iterator getPrefixes(String namespaceURI) {
		return null;
	}

	private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

}