/*
 * @(#)$Id: TestDriver.java 1650 2005-03-31 23:19:14Z kohsuke $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.conformance;

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

import java.util.Iterator;

/**
 * conformance test runner.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class TestDriver implements ErrorReceiver
{
    public static void main (String args[]) throws Exception {
        try {
            String parser;
            if( args.length>=1 )    parser = args[0];
            else                    parser = "org.apache.xerces.parsers.SAXParser";
            // reads test case file
            Document doc = new SAXBuilder(parser).build(
                TestDriver.class.getResourceAsStream("DataTypeTest.xml") );

            DataTypeTester tester = new DataTypeTester(System.out,new TestDriver());
            // perform test for each "case" item
            Iterator itr = doc.getRootElement().getChildren("case").iterator();
            while(itr.hasNext())
                tester.run( (Element)itr.next() );
        } catch(JDOMException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
    
    private DatatypeException getDiagnosis( Datatype dt, String literal ) {
        try {
            dt.checkValid(literal,DummyContextProvider.theInstance);
            return null;
        } catch ( DatatypeException de ) {
            return de;
        }
    }
    
    public boolean report( UnexpectedResultException exp ) {
        Object o = exp.type.createValue(exp.testInstance,DummyContextProvider.theInstance);
        Object jo = exp.type.createJavaObject(exp.testInstance,DummyContextProvider.theInstance);
        
        System.out.println("************* error *************");
        System.out.println("type name            : "+exp.baseTypeName);
        System.out.println("tested instance      : \""+exp.testInstance+"\"");
        System.out.println("supposed to be valid : "+exp.supposedToBeValid);
        System.out.println("verify method        : "+exp.type.isValid(exp.testInstance,DummyContextProvider.theInstance) );
        System.out.println("createValue method   : "+(o!=null) );
        System.out.println("createJavaObj method : "+(jo!=null) );
        System.out.println("diagnose method      : "+(getDiagnosis(exp.type,exp.testInstance)==null) );
        System.out.println("JavaObjectType       : "+(jo!=null?jo.getClass().toString():"N/A") );
        System.out.println("expected Type        : "+exp.type.getJavaObjectType() );
        
        if(jo!=null)
            try {
                System.out.println("serializeJavaObject  : "+exp.type.serializeJavaObject(jo,DummyContextProvider.theInstance) );
            } catch( Exception e ) {
                System.out.println("serializeJavaObject  : "+e );
            }
        
        if(o!=null)
            try {
                System.out.println("convertToLexical     : "+exp.type.convertToLexicalValue(o,DummyContextProvider.theInstance));
            } catch( Exception e ) {
                System.out.println("convertToLexical     : "+e);
                e.printStackTrace();
            }
        
        if( exp.incubator.isEmpty() )
            System.out.println("facets: none");
        else
            exp.incubator.dump(System.out);

        DatatypeException err = getDiagnosis( exp.type, exp.testInstance );
        
        if( err!=null && err.getMessage()!=null )
            System.out.println("diagnosis: " + err.getMessage() );
        else
            System.out.println("diagnosis: N/A");
        
        // do it again (for trace purpose)
        exp.type.isValid(exp.testInstance,DummyContextProvider.theInstance);
        
        return false;
    }

    public boolean reportTestCaseError( XSDatatype baseType, TypeIncubator incubator, DatatypeException e ) {
/*
        System.err.println("---- warning ----");
        System.err.println("test case error");
        facets.dump(System.err);
        System.err.println();
        
        try
        {// do it again (for debug)
            baseType.derive("anonymous",facets);
        }
        catch( Exception ee ) { ; }
*/
        
        return false;
    }
}
