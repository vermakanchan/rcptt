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
package org.eclipse.rcptt.launching.rap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.rcptt.internal.launching.Q7LaunchingPlugin;
import org.eclipse.rcptt.internal.launching.aut.BaseAutLaunch;
import org.eclipse.rcptt.launching.AutLaunchState;
import org.eclipse.rcptt.launching.ILaunchExecutor;
import org.eclipse.rcptt.tesla.core.TeslaLimits;

public class RapLaunchExecutor implements ILaunchExecutor {

	@Override
	public ILaunch launch(String mode, ILaunchConfiguration config, IProgressMonitor monitor) throws CoreException {
		return config.launch(mode, monitor);
	}

	@Override
	public void waitForRestart(BaseAutLaunch launch, IProgressMonitor monitor) throws CoreException {
		long startTime = System.currentTimeMillis();
		// wait for restart
		try {
			String lastUUID = launch.getLastActivateID();
			// Wait until activation ID will be changed.
			while (launch.getLastActivateID().equals(lastUUID)
					&& !launch.getState().equals(AutLaunchState.TERMINATE)) {
				waitFor(100, startTime);
			}
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.CANCEL,
					Q7LaunchingPlugin.PLUGIN_ID, e.getMessage(), e));
		}
	}

	private void waitFor(long time, long startTime)
			throws InterruptedException, CoreException {
		Thread.sleep(time);
		long currentTime = System.currentTimeMillis();
		if (currentTime > startTime + TeslaLimits.getAUTStartupTimeout()) {
			throw new CoreException(new Status(IStatus.ERROR,
					Q7LaunchingPlugin.PLUGIN_ID,
					"Waiting for restart failed: timeout error"));
		}
	}
}
