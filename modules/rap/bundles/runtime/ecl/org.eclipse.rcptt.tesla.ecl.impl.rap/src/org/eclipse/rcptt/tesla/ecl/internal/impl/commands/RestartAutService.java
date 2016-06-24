/*******************************************************************************
 * Copyright (c) 2009, 2016 Xored Software Inc and others.
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.rcptt.ecl.core.Command;
import org.eclipse.rcptt.ecl.runtime.ICommandService;
import org.eclipse.rcptt.ecl.runtime.IProcess;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class RestartAutService implements ICommandService {
	@Override
	public IStatus service(Command command, IProcess context) throws InterruptedException, CoreException {
		ShutdownAutService.tryTerminateLaunches();
		PlatformUI.getWorkbench().saveAllEditors(false);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO RAP-FIX: Does not support: eclipse.org/bugs/show_bug.cgi?id=201545
				// PlatformUI.getWorkbench().restart();
			}
		});
		return Status.OK_STATUS;
	}

}
