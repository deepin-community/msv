/*
 * @(#)$Id: ISchema.java 1567 2003-06-09 20:50:21Z kk122374 $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.model;

import com.sun.msv.grammar.Grammar;

/**
 * Represents a compiled shcmea.
 * 
 * The format of the compiled schema is implementation-dependent.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface ISchema {
    Grammar asGrammar();
}

