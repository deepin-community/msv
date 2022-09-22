/*
 * @(#)$Id: GYearType.java 1634 2004-04-05 17:42:28Z kohsuke $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;


/**
 * "gYear" type.
 * 
 * type of the value object is {@link com.sun.msv.datatype.xsd.datetime.IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#gYear for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GYearType extends DateTimeBaseType {
    
    public static final GYearType theInstance = new GYearType();
    private GYearType() { super("gYear"); }
    
    protected final String getFormat() {
        return "%Y%z";
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
