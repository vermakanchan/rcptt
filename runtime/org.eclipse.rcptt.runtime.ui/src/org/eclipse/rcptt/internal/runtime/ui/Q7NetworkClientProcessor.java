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
package org.eclipse.rcptt.internal.runtime.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.rcptt.tesla.core.protocol.raw.SetMode;
import org.eclipse.rcptt.tesla.core.protocol.raw.TeslaMode;
import org.eclipse.rcptt.tesla.internal.core.network.server.ITeslaNetworkClientProcessor;
import org.eclipse.rcptt.tesla.internal.core.network.server.NetworkTeslaClient;
import org.eclipse.rcptt.tesla.internal.core.network.server.TeslaNetworkClientConnection;
import org.eclipse.rcptt.util.ShellUtilsProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Q7NetworkClientProcessor implements ITeslaNetworkClientProcessor {

	public void activateMode(SetMode command, TeslaMode oldMode) {
		// Focus on AUT
		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = getWorkbench().getWorkbenchWindows();
			if (windows.length > 0) {
				window = windows[0];
			} else {
				return;
			}
		}
		final Shell shell = window.getShell();
		if (shell != null && !shell.isDisposed()) {
			if (Thread.currentThread() == shell.getDisplay().getThread()) {
				makeActive(shell);
			} else {
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!shell.isDisposed()) {
							makeActive(shell);
						}
					}
				});
			}
		}
	}

	private void makeActive(Shell shell) {
		try {
			ShellUtilsProvider.getShellUtils().forceActive(shell);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		shell.forceFocus();
	}

	private IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	public void initialize(
			TeslaNetworkClientConnection teslaNetworkClientConnection,
			DataInputStream din, DataOutputStream dout,
			NetworkTeslaClient teslaClient) {
	}

	public void terminate(boolean last) {
	}

	public void setFeature(String name, String value) {
	}

	public void resetAssertSelection() {
	}
}
