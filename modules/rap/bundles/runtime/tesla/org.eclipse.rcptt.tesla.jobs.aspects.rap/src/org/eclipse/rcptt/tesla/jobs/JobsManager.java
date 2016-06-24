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
package org.eclipse.rcptt.tesla.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.jobs.InternalJob;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class JobsManager {
	private Set<Job> asyncJobs = new HashSet<Job>();
	private Set<Job> toNullifyTime = new HashSet<Job>();
	private static JobsManager instance = null;
	private static List<InternalJob> cancelled = new ArrayList<InternalJob>();
	private static Map<InternalJob, Long> timeouts = new HashMap<InternalJob, Long>();

	public synchronized void notifyJobDone(Job job, IStatus status,
			boolean notify) {
		if (status.equals(Job.ASYNC_FINISH)) {
			asyncJobs.add(job);
		} else {
			asyncJobs.remove(job);
		}
		timeouts.remove(job);
	}

	public synchronized boolean isFinishedAsyncJob(Job job) {
		return asyncJobs.contains(job);
	}

	public synchronized static JobsManager getInstance() {
		if (instance == null) {
			instance = new JobsManager();
		}
		return instance;
	}

	public synchronized long calculateNewTime(InternalJob job, long time) {
		if (toNullifyTime.contains(job)) {
			toNullifyTime.remove(job);
			return 0;
		}
		return time;
	}

	public synchronized void putJobTime(InternalJob job, long time) {
		if (time != 0 && time != -1) {
			long now = System.currentTimeMillis();
			if( time >= now) {
				time = time - now;
			}
			timeouts.put(job, time);
		}
	}

	public synchronized void nulifyTime(Job job) {
		toNullifyTime.add(job);
		timeouts.remove(job);
	}

	public synchronized void notifyJobCancel(InternalJob job) {
		cancelled.add(job);
		timeouts.remove(job);
	}

	public synchronized void clean() {
		toNullifyTime.clear();
		cancelled.clear();
	}

	public synchronized void removeCanceled(Job job) {
		cancelled.remove(job);
	}

	public synchronized boolean isCanceled(Job job) {
		return cancelled.contains(job);
	}

	public synchronized Long getTimeout(Job job) {
		return timeouts.get(job);
	}
}
