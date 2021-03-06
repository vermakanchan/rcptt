/*******************************************************************************
 * /*******************************************************************************
 *  * Copyright (c) 2009, 2016 Xored Software Inc and others.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the Eclipse Public License v1.0
 *  * which accompanies this distribution, and is available at
 *  * http://www.eclipse.org/legal/epl-v10.html
 *  *
 *  * Contributors:
 *  *     Xored Software Inc - initial API and implementation and/or initial documentation
 *  *******************************************************************************/
package org.eclipse.rcptt.tesla.ecl.rap.model;

import org.eclipse.rcptt.ecl.core.Command;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Run Js</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.rcptt.tesla.ecl.rap.model.RunJs#getJsCode <em>Js Code</em>}</li>
 * </ul>
 *
 * @see org.eclipse.rcptt.tesla.ecl.rap.model.RapTeslaPackage#getRunJs()
 * @model
 * @generated
 */
public interface RunJs extends Command {
	/**
	 * Returns the value of the '<em><b>Js Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Js Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Js Code</em>' attribute.
	 * @see #setJsCode(String)
	 * @see org.eclipse.rcptt.tesla.ecl.rap.model.RapTeslaPackage#getRunJs_JsCode()
	 * @model required="true"
	 * @generated
	 */
	String getJsCode();

	/**
	 * Sets the value of the '{@link org.eclipse.rcptt.tesla.ecl.rap.model.RunJs#getJsCode <em>Js Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Js Code</em>' attribute.
	 * @see #getJsCode()
	 * @generated
	 */
	void setJsCode(String value);

} // RunJs
