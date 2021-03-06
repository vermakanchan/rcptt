/*******************************************************************************
 * Copyright (c) 2009, 2014 Xored Software Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Xored Software Inc - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.rcptt.tesla.ecl.model;

import org.eclipse.rcptt.ecl.core.Command;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Wait Until Eclipse Is Ready</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.rcptt.tesla.ecl.model.TeslaPackage#getWaitUntilEclipseIsReady()
 * @model annotation="http://www.eclipse.org/ecl/docs description='Suspend execution until Eclipse is ready.' returns='nothing' recorded='true' example='get-menu \"File/Restart\" | click\nwait-until-eclipse-is-ready\nget-view \"Q7 Explorer\" | get-tree | select Project'"
 * @generated
 */
public interface WaitUntilEclipseIsReady extends Command {
} // WaitUntilEclipseIsReady
