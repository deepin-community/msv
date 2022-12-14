/*
 * @(#)$Id: SAXEventGenerator.java 932 2001-07-21 02:40:12Z Bear $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.util.xml;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

/**
 * produces SAX2 event from a DOM tree.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SAXEventGenerator {
	
	/**
	 * scans the specified DOM and sends SAX2 events to the handler.
	 */
	public static void parse( Document dom, final ContentHandler handler ) throws SAXException {
		
		DOMVisitor visitor = new DOMVisitor(){
			public void visit( Element e ) {
				int attLen = e.getAttributes().getLength();
				AttributesImpl atts = new AttributesImpl();
				for( int i=0; i<attLen; i++ ) {
					Attr a = (Attr)e.getAttributes().item(i);
					atts.addAttribute( a.getNamespaceURI(), a.getLocalName(),
						a.getName(), null/*no type available*/, a.getValue() );
				}
				
				try {
					handler.startElement( e.getNamespaceURI(), e.getLocalName(), e.getNodeName(), atts );
					super.visit(e);
					handler.endElement( e.getNamespaceURI(), e.getLocalName(), e.getNodeName() );
				} catch( SAXException x ) {
					throw new SAXWrapper(x);
				}
			}
			
			public void visitNode( Node n ) {
				if( n.getNodeType()==n.TEXT_NODE
				||  n.getNodeType()==n.CDATA_SECTION_NODE ) {
					String text = n.getNodeValue();
					try {
						handler.characters( text.toCharArray(), 0, text.length() );
					} catch( SAXException x ) {
						throw new SAXWrapper(x);
					}
				}
				super.visitNode(n);
			}
		};
		
		// set a dummy locator. We cannot provide location information.
		handler.setDocumentLocator( new LocatorImpl() );
		handler.startDocument();
		try {
			visitor.visit(dom);
		} catch( SAXWrapper w ) {
			throw w.e;
		}
		handler.endDocument();
	}
	
	// wrap SAXException into a RuntimeException so that
	// exception can pass through DOMVisitor.
	private static class SAXWrapper extends RuntimeException {
		SAXWrapper( SAXException e ) { this.e=e; }
		SAXException e;
	}
}
