/*
 * @(#)$Id: ClassItem.java 923 2001-07-20 20:45:03Z Bear $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar;

import com.sun.msv.grammar.Expression;

/**
 * represents a generated class.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ClassItem extends TypeItem {
	
	protected ClassItem( String name, Expression exp ) {
		super(name);
		this.exp = exp;
	}
	
	/**
	 * this flag is set to true if this class item is generated by automatic guessing,
	 * rathen than the explicit instruction of the user.
	 */
	public boolean isTemporary = false;
		
	/** super class definition, if any. */
	public SuperClassItem superClass;
	
	public Type getSuperType() {
		if(superClass==null)	return null;
		return superClass.definition;
	}
	
	public Object visitJI( JavaItemVisitor visitor ) {
		return visitor.onClass(this);
	}
}