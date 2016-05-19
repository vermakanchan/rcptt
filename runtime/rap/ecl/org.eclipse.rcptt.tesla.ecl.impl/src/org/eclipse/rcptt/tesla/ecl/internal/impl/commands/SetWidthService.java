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
package org.eclipse.rcptt.tesla.ecl.internal.impl.commands;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.rcptt.ecl.core.Command;

import org.eclipse.rcptt.tesla.core.protocol.ColumnUIElement;
import org.eclipse.rcptt.tesla.core.protocol.ElementKind;
import org.eclipse.rcptt.tesla.ecl.impl.AbstractActionService;
import org.eclipse.rcptt.tesla.ecl.impl.TeslaBridge;
import org.eclipse.rcptt.tesla.ecl.model.ControlHandler;
import org.eclipse.rcptt.tesla.ecl.model.SetWidth;

public class SetWidthService extends AbstractActionService {

	@Override
	protected Object exec(Command command) throws CoreException {
		if (!(command instanceof SetWidth))
			throw new IllegalArgumentException("this service is only for SetWidth scriptlet");

		SetWidth sw = (SetWidth) command;
		ControlHandler control = sw.getControl();
		if (control.getKind() != ElementKind.ColumnHeader)
			throw new IllegalArgumentException("SetWidth is only applicable for column headers");

		new ColumnUIElement(TeslaBridge.find(control), TeslaBridge.getPlayer()).setWidth(sw.getWidth());
		return control;
	}

}
