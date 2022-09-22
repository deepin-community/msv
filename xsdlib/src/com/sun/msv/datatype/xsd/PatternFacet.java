/*
 * @(#)$Id: PatternFacet.java 1652 2005-04-01 00:00:31Z kohsuke $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import com.sun.msv.datatype.xsd.regex.RegExp;
import com.sun.msv.datatype.xsd.regex.RegExpFactory;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;
import java.text.ParseException;

/**
 * "pattern" facet validator
 * 
 * "pattern" is a constraint facet which is applied against lexical space.
 * See http://www.w3.org/TR/xmlschema-2/#dt-pattern for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class PatternFacet extends DataTypeWithLexicalConstraintFacet {
    
    /**
     * actual object that performs regular expression validation.
     * one of the item has to match.
     *
     * Don't serialize so that we won't have hard-coded dependency to
     * a particular regexp lib.
     */
    private transient RegExp[] exps;
    
    public RegExp[] getRegExps() { return exps; }
    
    /**
     * string representations of the above RegularExpressions.
     * this representation is usually human friendly than
     * the one generated by RegularExpression.toString method.
     */
    final public String[] patterns;

    
    
    public PatternFacet( String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets )
        throws DatatypeException {
        super( nsUri, typeName, baseType, FACET_PATTERN, facets.isFixed(FACET_PATTERN) );
        
        
        // TODO : am I supposed to implement my own regexp validator?
        // at this time, I use Xerces' one.
        
        Vector regExps = facets.getVector(FACET_PATTERN);
        patterns = (String[]) regExps.toArray(new String[regExps.size()]);
        
        try {
            compileRegExps();
        } catch( ParseException pe ) {
            // in case regularExpression is not a correct pattern
            throw new DatatypeException( localize( ERR_PARSE_ERROR,
                pe.getMessage() ) );
        }
    }
    
    /** Compiles all the regular expressions. */
    private void compileRegExps() throws ParseException {
        exps = new RegExp[patterns.length];
        RegExpFactory factory = RegExpFactory.createFactory();
        for(int i=0;i<exps.length;i++)
            exps[i] = factory.compile(patterns[i]);
        
        // loosened facet check is almost impossible for pattern facet.
        // ignore it for now.
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if( checkLexicalConstraint(content) )    return;
        
        if( exps.length==1 )
            throw new DatatypeException( DatatypeException.UNKNOWN,
                localize(ERR_PATTERN_1,patterns[0]) );
        else
            throw new DatatypeException( DatatypeException.UNKNOWN,
                localize(ERR_PATTERN_MANY) );
    }
    
    protected final boolean checkLexicalConstraint( String literal ) {
        // makes sure that at least one of the patterns is satisfied.
        
        // regexp can be not thread-safe. Make sure only one thread uses it
        // at any given time.
        synchronized(this) {
            for( int i=0; i<exps.length; i++ )
                if(exps[i].matches(literal))
                    return true;
        }
        // otherwise fail
        return false;
    }
    
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        try {
            compileRegExps();
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
    

    // serialization support
    private static final long serialVersionUID = 1;
}