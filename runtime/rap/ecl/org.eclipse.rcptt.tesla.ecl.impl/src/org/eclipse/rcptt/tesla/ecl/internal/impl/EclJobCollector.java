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
package org.eclipse.rcptt.tesla.ecl.internal.impl;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.rcptt.ecl.internal.core.Session;

import org.eclipse.rcptt.tesla.ui.IJobCollector;

public class EclJobCollector implements IJobCollector {

	public JobStatus testJob(Job job) {
		if (job instanceof Session.EclJob)
			return JobStatus.IGNORED;
		return JobStatus.UNKNOWN;
	}

	public boolean noSkipMode(Job job) {
		return true;
	}

}
