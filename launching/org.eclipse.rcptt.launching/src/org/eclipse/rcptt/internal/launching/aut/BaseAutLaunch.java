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
package org.eclipse.rcptt.internal.launching.aut;

import static org.eclipse.rcptt.internal.launching.Q7LaunchingPlugin.PLUGIN_ID;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.pde.internal.launching.IPDEConstants;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.rcptt.core.ContextType;
import org.eclipse.rcptt.core.Scenarios;
import org.eclipse.rcptt.core.VerificationType;
import org.eclipse.rcptt.core.ecl.core.model.EnterContext;
import org.eclipse.rcptt.core.ecl.core.model.ExecVerification;
import org.eclipse.rcptt.core.ecl.core.model.ExecutionPhase;
import org.eclipse.rcptt.core.ecl.core.model.Q7CoreFactory;
import org.eclipse.rcptt.core.ecl.core.model.Q7Information;
import org.eclipse.rcptt.core.launching.events.AutBundleState;
import org.eclipse.rcptt.core.launching.events.AutEvent;
import org.eclipse.rcptt.core.launching.events.AutEventInit;
import org.eclipse.rcptt.core.launching.events.AutEventLocation;
import org.eclipse.rcptt.core.launching.events.AutEventStart;
import org.eclipse.rcptt.core.model.IContext;
import org.eclipse.rcptt.core.model.IQ7NamedElement;
import org.eclipse.rcptt.core.model.ITestCase;
import org.eclipse.rcptt.core.model.IVerification;
import org.eclipse.rcptt.core.model.ModelException;
import org.eclipse.rcptt.core.scenario.Context;
import org.eclipse.rcptt.core.scenario.NamedElement;
import org.eclipse.rcptt.core.scenario.Scenario;
import org.eclipse.rcptt.core.scenario.Verification;
import org.eclipse.rcptt.core.workspace.RcpttCore;
import org.eclipse.rcptt.ecl.client.tcp.EclTcpClientManager;
import org.eclipse.rcptt.ecl.core.Command;
import org.eclipse.rcptt.ecl.core.CoreFactory;
import org.eclipse.rcptt.ecl.core.CorePackage;
import org.eclipse.rcptt.ecl.core.RestoreState;
import org.eclipse.rcptt.ecl.core.Script;
import org.eclipse.rcptt.ecl.core.Sequence;
import org.eclipse.rcptt.ecl.core.SessionState;
import org.eclipse.rcptt.ecl.debug.commands.CommandsFactory;
import org.eclipse.rcptt.ecl.debug.commands.DebugCommand;
import org.eclipse.rcptt.ecl.debug.commands.DebugScript;
import org.eclipse.rcptt.ecl.gen.ast.AstExec;
import org.eclipse.rcptt.ecl.parser.EclCoreParser;
import org.eclipse.rcptt.ecl.parser.ScriptErrorStatus;
import org.eclipse.rcptt.ecl.runtime.CoreUtils;
import org.eclipse.rcptt.ecl.runtime.IPipe;
import org.eclipse.rcptt.ecl.runtime.IProcess;
import org.eclipse.rcptt.ecl.runtime.ISession;
import org.eclipse.rcptt.internal.core.model.Q7InternalContext;
import org.eclipse.rcptt.internal.core.model.Q7InternalVerification;
import org.eclipse.rcptt.internal.launching.ExecutionStatus;
import org.eclipse.rcptt.internal.launching.Q7LaunchingPlugin;
import org.eclipse.rcptt.internal.launching.ecl.EclContextExecutable;
import org.eclipse.rcptt.internal.launching.ecl.ExecAdvancedInfoUtil;
import org.eclipse.rcptt.launching.AutLaunch;
import org.eclipse.rcptt.launching.AutLaunchListener;
import org.eclipse.rcptt.launching.AutLaunchState;
import org.eclipse.rcptt.launching.IQ7Launch;
import org.eclipse.rcptt.launching.Q7Launcher;
import org.eclipse.rcptt.launching.Q7TeslaProblemInformer;
import org.eclipse.rcptt.launching.TestCaseDebugger;
import org.eclipse.rcptt.launching.utils.TestSuiteUtils;
import org.eclipse.rcptt.tesla.core.TeslaLimits;
import org.eclipse.rcptt.tesla.core.TeslaScenarioContainer;
import org.eclipse.rcptt.tesla.core.network.TeslaNetworkReplayer;
import org.eclipse.rcptt.tesla.core.protocol.raw.TeslaScenario;
import org.eclipse.rcptt.tesla.ecl.model.ShutdownAut;
import org.eclipse.rcptt.tesla.ecl.model.TeslaFactory;
import org.eclipse.rcptt.tesla.ecl.model.TeslaPackage;

import com.google.common.base.Preconditions;

@SuppressWarnings("restriction")
public class BaseAutLaunch implements AutLaunch, IBaseAutLaunchRetarget {
	private final String id;
	private BaseAut aut;
	private volatile AutLaunchState state = AutLaunchState.LAUNCH;
	private volatile ILaunch launch;
	private volatile IStatus status;

	private final CopyOnWriteArrayList<AutLaunchListener> listeners = new CopyOnWriteArrayList<AutLaunchListener>();
	private IProgressMonitor currentTestMonitor = null;
	private String locationOnRestart = null;
	private String lastActivateID = "";

	private AutEventInit autInit;
	private AutEventStart autStart;

	BaseAutLaunch(ILaunch launch, BaseAut aut) {
		id = UUID.randomUUID().toString();
		this.aut = aut;
		setLaunch(launch);
	}

	public String getLastActivateID() {
		return lastActivateID;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public BaseAut getAut() {
		return aut;
	}

	@Override
	public AutLaunchState getState() {
		return state;
	}

	@Override
	public void addListener(AutLaunchListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(AutLaunchListener listener) {
		listeners.remove(listener);
	}

	@Override
	public synchronized Object execute(Command command) throws CoreException,
			InterruptedException {
		return execute(command, Q7Launcher.getLaunchTimeout() * 1000);
	}

	@Override
	public synchronized Object execute(Command command, long timeout)
			throws CoreException, InterruptedException {
		return execute(command, timeout, new NullProgressMonitor());
	}

	@Override
	public void handleAutEvent(AutEvent autEvent) {
		if (getState().equals(AutLaunchState.TERMINATE)) {
			return;
		}
		if (autEvent instanceof AutEventStart) {
			this.autStart = (AutEventStart) autEvent;
			for (AutLaunchListener listener : listeners) {
				listener.autStarted(this, this.autStart.getEclPort(), this.autStart.getTeslaPort());
			}
		} else if (autEvent instanceof AutEventInit) {
			this.autInit = (AutEventInit) autEvent;
			for (AutLaunchListener listener : listeners) {
				listener.autInit(this, this.autInit.getBundleState());
			}
		} else if (autEvent instanceof AutEventLocation) {
			for (AutLaunchListener listener : listeners) {
				listener.autLocationChange(this, ((AutEventLocation) autEvent).getLocation());
			}
		}

	}

	@Override
	public synchronized Object execute(Command command, long timeout,
			IProgressMonitor monitor) throws CoreException,
			InterruptedException {

		ISession session = createEclSession();
		try {
			IPipe out = session.createPipe();
			IProcess rc = session.execute(command, null, out);
			IStatus status = rc.waitFor(timeout, monitor);
			if (!status.isOK()) {
				throw new CoreException(status);
			}
			return out.take(timeout);
		} finally {
			safeClose(session);
		}
	}

	public Object unsafeExecute(final Command command, final long timeout,
			final IProgressMonitor monitor) throws CoreException,
			InterruptedException {
		final Object[] result = new Object[] { null };
		final Exception[] wrappedException = new Exception[] { null };
		Thread execThread = new Thread() {
			@Override
			public void run() {
				ISession session = null;
				try {
					session = createEclSession();
					IPipe out = session.createPipe();
					IProcess rc = session.execute(command, null, out);
					IStatus status = rc.waitFor(timeout, monitor);
					if (!status.isOK()) {
						throw new CoreException(status);
					}
					result[0] = out.take(timeout);
				} catch (Exception e) {
					wrappedException[0] = e;
				} finally {
					if (session != null) {
						safeClose(session);
					}
				}
			};
		};
		execThread.start();
		execThread.join(timeout);
		execThread.interrupt();

		Exception wrapped = wrappedException[0];
		if (wrapped != null) {
			String message = "Failed to execute command " + command;
			if (wrapped instanceof CoreException) {
				throw new CoreException(new MultiStatus(PLUGIN_ID, 0,
						new IStatus[] { ((CoreException) wrapped).getStatus() }, message, null));
			}
			throw new CoreException(Q7LaunchingPlugin.createStatus(IStatus.ERROR, message, wrapped));
		}

		return result[0];
	}

	public synchronized List<Object> executeAndTakeAll(Command command,
			long timeout) throws CoreException, InterruptedException {
		return executeAndTakeAll(command, timeout, new NullProgressMonitor());
	}

	public synchronized List<Object> executeAndTakeAll(Command command,
			long timeout, IProgressMonitor monitor) throws CoreException,
			InterruptedException {

		ISession session = createEclSession();
		try {
			return executeAndTakeAll(session, command, timeout, monitor);
		} finally {
			safeClose(session);
		}
	}

	private synchronized List<Object> executeAndTakeAll(ISession session, Command command,
			long timeout, IProgressMonitor monitor) throws CoreException,
			InterruptedException {
		IPipe out = session.createPipe();
		IProcess rc = session.execute(command, null, out);
		IStatus status = rc.waitFor(timeout, monitor);
		if (!status.isOK()) {
			throw new CoreException(status);
		}

		ArrayList<Object> result = new ArrayList<Object>();
		while (true) {
			Object o = out.take(timeout);
			if (o instanceof IStatus)
				return result;
			result.add(o);
		}
	}

	public void activate(String host, int ecl, int tesla, String platform, String capability, float seconds,
			IProgressMonitor monitor)
			throws CoreException {
		Q7LaunchingPlugin.logInfo("Activating AUT at host %s. ECL port: %d. Tesla port: %d", host, ecl, tesla);
		monitor.beginTask("AUT pinging", (int) seconds);
		synchronized (launch) {
			launch.setAttribute(IQ7Launch.ATTR_HOST, host);
			launch.setAttribute(IQ7Launch.ATTR_ECL_PORT, Integer.toString(ecl));
			launch.setAttribute(IQ7Launch.ATTR_TESLA_PORT, Integer.toString(tesla));
			launch.setAttribute(IQ7Launch.ATTR_AUT_PLATFORM, platform);
			launch.setAttribute(IQ7Launch.ATTR_AUT_CAPABILITY, capability);
		}
		// make sure connection available
		long start = System.currentTimeMillis();
		try {
			try {
				while (true) {
					if (System.currentTimeMillis() - start > seconds * 1000) {
						break;
					}
					try {
						ping();
						break;
					} catch (CoreException e) {
						// ignore while there is still time left
					}
					Thread.sleep(1000);
					monitor.worked(1);
				}
			} catch (InterruptedException e) {
				throw new CoreException(Status.CANCEL_STATUS);
			} finally {
				monitor.done();
			}
			try {
				ping();
			} catch (CoreException e) {
				throw new CoreException(Q7LaunchingPlugin
						.createStatus("AUT connection has been failing for " + seconds + " seconds", e));
			}
			monitor.done();
		} catch (InterruptedException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		setState(AutLaunchState.ACTIVE);
		this.lastActivateID = UUID.randomUUID().toString();
	}

	@Override
	public void ping() throws CoreException, InterruptedException {
		try {
			Object object = unsafeExecute(
					Q7CoreFactory.eINSTANCE.createGetQ7Information(),
					TeslaLimits.getAUTStartupTimeout(),
					new NullProgressMonitor());
			if (object instanceof Q7Information) {
				Q7Information info = (Q7Information) object;
				if (!info.isTeslaActive()) {
					throw new CoreException(
							Q7LaunchingPlugin
									.createStatus("Tesla is not activated"));
				}
				if (info.getWindowCount() == 0) {
					throw new CoreException(
							Q7LaunchingPlugin
									.createStatus("AUT has no windows"));
				}
			} else {
				throw new CoreException(
						Q7LaunchingPlugin
								.createStatus("Expect Q7Information but found: "
										+ object));
			}
		} catch (CoreException e) {
			throw new CoreException(Q7LaunchingPlugin.createStatus(
					"Couldn't connect to AUT: " + e.getMessage(), e));
		}
	}

	public void restart() {
		try {
			setState(AutLaunchState.RESTART);
			ILaunchConfiguration launchConfiguration = launch
					.getLaunchConfiguration();
			final ILaunchConfigurationWorkingCopy copy;
			if (launchConfiguration.isWorkingCopy()) {
				copy = ((ILaunchConfigurationWorkingCopy) launchConfiguration)
						.getOriginal().getWorkingCopy();
			} else {
				copy = launchConfiguration.getWorkingCopy();
			}
			LaunchInfoCache.copyCache(launchConfiguration, copy);
			LaunchInfoCache.remove(launchConfiguration);
			copy.setAttribute(IPDEConstants.RESTART, true);
			copy.setAttribute(IQ7Launch.ATTR_AUT_ID, getId());
			if (locationOnRestart != null) {
				copy.setAttribute(IPDELauncherConstants.LOCATION,
						locationOnRestart);
			}
			final ILaunch oldLaunch = launch;
			launch.setAttribute(IQ7Launch.ATTR_AUT_ID, ""); // To disable
															// terminate by id.
			launch = copy.launch(launch.getLaunchMode(),
					new NullProgressMonitor());

			BaseAutManager.INSTANCE.handleRestart(BaseAutLaunch.this,
					oldLaunch, launch, copy);
		} catch (Exception e) {
			terminated(e);
			Q7LaunchingPlugin.log(e);
			setState(AutLaunchState.TERMINATE);
		}
	}

	public ILaunch setLaunch(ILaunch launch) {
		Preconditions.checkNotNull(launch);
		ILaunch oldLaunch = this.launch;
		this.launch = launch;
		launch.setAttribute(IQ7Launch.ATTR_AUT_ID, id);
		return oldLaunch;
	}

	private void safeClose(ISession session) {
		try {
			session.close();
		} catch (Exception e) {
			Q7LaunchingPlugin.log(e);
		}
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	public int getTeslaPort() {
		synchronized (launch) {
			String attr = launch.getAttribute(IQ7Launch.ATTR_TESLA_PORT);
			return Integer.parseInt(attr);
		}
	}

	public int getEclPort() {
		synchronized (launch) {
			String port = launch.getAttribute(IQ7Launch.ATTR_ECL_PORT);
			return Integer.valueOf(port);
		}
	}

	public String getHost() {
		synchronized (launch) {
			return launch.getAttribute(IQ7Launch.ATTR_HOST);
		}
	}

	public IStatus getStatus() {
		return status;
	}

	public void terminateProcess(int exitCode) {
		// launch ask for restart
		// Eclipse mars can return exit code 24 on restart if -vm argument was set
		if (exitCode == 23 || (exitCode == 24/* && locationOnRestart != null */)) {
			restart();
		} else {
			terminated(exitCode);
		}
	}

	public void terminated(int exitCode) {
		if (exitCode == 15) {
			String message = "AUT workspace already in use";
			String workspace = getWorkspace(launch);
			if (workspace != null) {
				message = message + ": " + workspace;
			}
			terminated(Q7LaunchingPlugin.createStatus(message));
		} else if (exitCode == 13) {
			terminated(Q7LaunchingPlugin
					.createStatus("The application could not start. Details can be found in the log."));
		} else {
			terminated(Status.OK_STATUS);
		}
	}

	public void terminated(Exception e) {
		terminated(Q7LaunchingPlugin.createStatus(e.getMessage(), e));
	}

	public void terminated(IStatus status) {
		setState(AutLaunchState.TERMINATE);
		this.autInit = null;
		this.autStart = null;
		LaunchInfoCache.remove(launch.getLaunchConfiguration());

		this.status = status;
		// make sure all processes finished
		if (launch.canTerminate()) {
			try {
				Q7LaunchingPlugin.logInfo("AUT terminated by RCPTT");
				launch.terminate();
			} catch (DebugException e) {
				Q7LaunchingPlugin.log("Termination failed", e);
			}
		}
	}

	public void setState(AutLaunchState state) {
		this.state = state;
		for (AutLaunchListener listener : listeners) {
			listener.stateChanged(this, state);
		}
	}

	private String getWorkspace(ILaunch launch) {
		try {
			ILaunchConfiguration launchConfiguration = launch
					.getLaunchConfiguration();
			return launchConfiguration.getAttribute(
					IPDELauncherConstants.LOCATION, ""); //$NON-NLS-1$
		} catch (Exception e) {
			Q7LaunchingPlugin.log("Couldn't find launch workspace", e);
		}
		return null;
	}

	@Override
	public synchronized void run(IQ7NamedElement element, long timeout,
			IProgressMonitor monitor, ExecutionPhase phase) throws CoreException {
		execElement(element, timeout, monitor, null, phase);
	}

	@Override
	public synchronized void debug(IQ7NamedElement element, long timeout,
			IProgressMonitor monitor, TestCaseDebugger debugger, ExecutionPhase phase)
			throws CoreException {
		execElement(element, timeout, monitor, debugger, phase);
	}

	@Override
	public void cancelTestExecution() {
		if (currentTestMonitor != null) {
			currentTestMonitor.setCanceled(true);
		}
	}

	private void execElement(IQ7NamedElement element, long timeout,
			IProgressMonitor monitor, TestCaseDebugger debugger, ExecutionPhase phase)
			throws CoreException {
		if (!element.exists()) {
			throw new CoreException(
					Q7LaunchingPlugin.createStatus("Resource does not exist: "
							+ element));
		}
		if (element instanceof ITestCase || EclContextExecutable.isEclScriptContext(element)) {
			execTestOrEclContext(element, timeout, monitor, debugger);
		} else if (element instanceof IContext) {
			execContext((IContext) element, monitor);
		} else if (element instanceof IVerification) {
			execVerification((IVerification) element, monitor, phase);
		}
	}

	private void execContext(IContext contextElement, IProgressMonitor monitor) throws CoreException {
		Context context = (Context) EcoreUtil.copy(contextElement.getModifiedNamedElement());
		if (!(contextElement instanceof Q7InternalContext)) {
			ContextType type = contextElement.getType();
			if (type != null) {
				type.getMaker().makeExecutable(context, contextElement);
			}
		}
		EnterContext ec = Q7CoreFactory.eINSTANCE.createEnterContext();
		ec.setData(context);
		try {
			IStatus result = internalExecute(ec, TeslaLimits.getContextRunnableTimeout(), monitor);
			if (!result.isOK()) {
				throw new CoreException(result);
			}
		} catch (InterruptedException e) {
			throw new CoreException(ExecAdvancedInfoUtil.askForAdvancedInfo(this,
					"Failed to apply context: " + contextElement.getElementName()));
		}
	}

	private void execVerification(IVerification verificationElement,
			IProgressMonitor monitor, ExecutionPhase phase) throws CoreException {
		Verification verification = (Verification) EcoreUtil.copy(verificationElement.getModifiedNamedElement());
		if (!(verificationElement instanceof Q7InternalVerification)) {
			VerificationType type = verificationElement.getType();
			if (type != null)
				type.getMaker().makeExecutable(verification, verificationElement);
		}

		ExecVerification command = Q7CoreFactory.eINSTANCE.createExecVerification();
		command.setVerification(verification);
		command.setPhase(phase);
		try {
			IStatus result = internalExecute(command, TeslaLimits.getContextRunnableTimeout(), monitor);
			if (!result.isOK()) {
				throw new CoreException(result);
			}
		} catch (InterruptedException e) {
			throw new CoreException(ExecAdvancedInfoUtil.askForAdvancedInfo(this,
					"Failed to apply verification: " + verificationElement.getElementName()));
		}
	}

	private void execTestOrEclContext(IQ7NamedElement test, long timeout, IProgressMonitor monitor,
			TestCaseDebugger debugger)
			throws CoreException {
		currentTestMonitor = monitor;

		try {
			NamedElement element = EcoreUtil.copy(test
					.getModifiedNamedElement());
			Script ecl = extractScript(element);
			TeslaScenario tesla = extractTesla(element);
			Map<String, String> idToPathMap = new HashMap<String, String>();
			if (ecl != null) {
				if (debugger != null) {
					DebugScript ds = CommandsFactory.eINSTANCE
							.createDebugScript();
					IPath path = test.getPath();
					ds.setPath(path.toString());
					ds.setSession(debugger.getSessionId());
					ds.setContent(ecl.getContent());
					ds.setHost(ecl.getHost());
					ds.getBindings().addAll(
							EcoreUtil.copyAll(ecl.getBindings()));

					for (IContext ctx : RcpttCore.getInstance().getContexts(test, null, true)) {
						if (ctx.getResource() == null) {
							continue;
						}
						String ctxId = ctx.getID();
						String ctxPath = ctx.getResource().getFullPath().toPortableString();
						ds.getPaths().put(ctxId, ctxPath);
						idToPathMap.put(ctxId, ctxPath);
					}
					ecl = ds;
				}
				doExecute(ecl, debugger, timeout, monitor, test.getID(), idToPathMap);
			} else if (tesla != null) {
				runTeslaScenario(tesla, monitor);
			} else {
				throw new CoreException(new Status(IStatus.ERROR,
						Q7LaunchingPlugin.PLUGIN_ID,
						"Couldn't launch test case: invalid format"));
			}
		} catch (CoreException e) {
			if (e.getStatus() instanceof ExecutionStatus) {
				((ExecutionStatus) e.getStatus()).setElement(test);
			}
			throw e;
		} finally {
			currentTestMonitor = null;
		}
	}

	private TeslaScenario extractTesla(NamedElement element)
			throws ModelException {
		if (element instanceof Scenario
				&& Scenarios.isTeslaMode((Scenario) element)) {
			return Scenarios.getTesla((Scenario) element);
		}
		return null;
	}

	private Script extractScript(NamedElement element) throws ModelException {
		if (element instanceof Scenario
				&& Scenarios.isEclMode((Scenario) element)) {
			Script ecl = Scenarios.getEcl((Scenario) element);
			return ecl == null ? CoreFactory.eINSTANCE.createScript() : ecl;
		}
		if (element instanceof Context) {
			EStructuralFeature scriptFeature = element.eClass()
					.getEStructuralFeature("script");
			if (scriptFeature != null
					&& scriptFeature.getEType().equals(
							CorePackage.Literals.SCRIPT)) {
				return (Script) element.eGet(scriptFeature);
			}
		}
		return null;
	}

	@Override
	public synchronized void execute(Script script, long timeout,
			IProgressMonitor progressMonitor) throws CoreException {
		doExecute(script, null, timeout, progressMonitor, null, null);
	}

	protected void doExecute(Script script, TestCaseDebugger debugger,
			long timeout, IProgressMonitor progressMonitor, String id, Map<String, String> idToPathMap)
			throws CoreException {
		if (script == null) {
			return;
		}
		try {
			// eclipse 3.4 compatibility:
			// EcoreUtil.copy raise exception if argument is null
			script = EcoreUtil.copy(script);
			setupPlayer();
			List<Command> parts = splitByRestart(script, id);
			Assert.isTrue(parts.size() > 0);

			/*
			 * if (id != null) { setResourceID(parts, id); }
			 */

			debugHook(script, parts, idToPathMap);
			Iterator<Command> it = parts.iterator();
			while (it.hasNext()) {
				status = internalExecute(it.next(), timeout, progressMonitor);
				if (!status.isOK()) {
					throw new CoreException(status);
				}
				if (!it.hasNext())
					break;
				if (debugger != null) {
					debugger.beforeRestart();
				}
				waitForRestart(new NullProgressMonitor()); // Do not use monitor
															// then wait for
															// restart
				if (debugger != null) {
					debugger.afterRestart();
				}
				setupPlayer();
			}
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.CANCEL,
					Q7LaunchingPlugin.PLUGIN_ID, e.getMessage(), e));
		} catch (CoreException e) {
			if (e.getStatus() instanceof ScriptErrorStatus) {
				throw new CoreException(new ExecutionStatus(e.getStatus()));
			}
			throw e;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Q7LaunchingPlugin.PLUGIN_ID,
					"Failed to launch ECL scenario", e));
		} finally {
			try {
				shutdownPlayer();
			} catch (CoreException e) {
				Q7LaunchingPlugin.log(e.getStatus());
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private static void debugHook(Script main, List<Command> parts, Map<String, String> idToPathMap) {
		if (main instanceof DebugScript) {
			DebugScript ds = (DebugScript) main;
			ArrayList<Command> list = new ArrayList<Command>(parts);
			parts.clear();
			for (Command part : list) {
				DebugCommand dc = CommandsFactory.eINSTANCE
						.createDebugCommand();
				if (idToPathMap != null) {
					for (Map.Entry<String, String> e : idToPathMap.entrySet()) {
						dc.getPaths().put(e.getKey(), e.getValue());
					}
				}
				dc.setCommand(part);
				dc.setPath(ds.getPath());
				dc.setSession(ds.getSession());
				parts.add(dc);
			}
		}
	}

	@Override
	public void waitForRestart(IProgressMonitor monitor) throws CoreException {
		// wait logic is launch specific -> delegate to executor
		aut.getExecutor().waitForRestart(this, monitor);
	}

	private void setupPlayer() throws InterruptedException, CoreException {
		execute(TeslaFactory.eINSTANCE.createSetupPlayer());
	}

	private void shutdownPlayer() throws InterruptedException, CoreException {
		execute(TeslaFactory.eINSTANCE.createShoutdownPlayer());
	}

	@Override
	public void shutdown() {
		try {
			ShutdownAut shutdownCmd = TeslaFactory.eINSTANCE
					.createShutdownAut();
			execute(shutdownCmd);
		} catch (Exception e) {
			Q7LaunchingPlugin.log("Shutdown failed", e);
		}
		terminate();
	}

	public void gracefulShutdown(int timeoutSeconds) throws CoreException, InterruptedException {
		int launchTimeout = Q7Launcher.getLaunchTimeout();
		try {
			ShutdownAut shutdownCmd = TeslaFactory.eINSTANCE.createShutdownAut();
			unsafeExecute(shutdownCmd, launchTimeout * 1000, new NullProgressMonitor());
		} catch (Exception e) {
			if (!TestSuiteUtils.isConnectionProblem(e)) {
				throw new CoreException(new Status(IStatus.ERROR, Q7LaunchingPlugin.PLUGIN_ID,
						"Error during graceful shutdown", e));
			}
		} finally {
			try {
				for (int i = 0; i < timeoutSeconds; ++i) {
					if (launch.isTerminated())
						return;
					Thread.sleep(1000);
				}
			} finally {
				terminate();
			}
		}
	}

	@Override
	public void terminate() {
		if (launch.canTerminate()) {
			try {
				launch.terminate();
			} catch (DebugException e) {
				Q7LaunchingPlugin.log("Termination failed", e);
			}
		}
	}

	private SessionState currentState;

	@Override
	public void resetState() {
		currentState = null;
	}

	private void dumpState(ISession session) {
		try {
			List<Object> result = executeAndTakeAll(session,
					CoreFactory.eINSTANCE.createSaveState(),
					Q7Launcher.getLaunchTimeout() * 1000,
					new NullProgressMonitor());
			if (result.size() != 1) {
				return;
			}
			Object state = result.get(0);
			if (state instanceof SessionState) {
				currentState = (SessionState) state;
			}
		} catch (Exception e) {
			Q7LaunchingPlugin.log("error getting interpreter state", e);
		}
	}

	private void restoreState(ISession session) {
		if (currentState == null) {
			return;
		}
		try {
			RestoreState command = CoreFactory.eINSTANCE.createRestoreState();
			command.setState(currentState);
			session.execute(command).waitFor();
		} catch (Exception e) {
			Q7LaunchingPlugin.log("error restoring interpreter state", e);
		}
	}

	private IStatus internalExecute(Command command, long timeout,
			IProgressMonitor monitor) throws InterruptedException,
			CoreException {
		ISession session = createEclSession();
		restoreState(session);
		try {
			return new ExecutionStatus(session.execute(command).waitFor(timeout, monitor));
		} finally {
			if (!monitor.isCanceled()) {
				dumpState(session);
			}
			safeClose(session);
		}
	}

	private IStatus runTeslaScenario(TeslaScenario scenario,
			IProgressMonitor progressMonitor) throws CoreException {
		final IStatus[] s = new IStatus[1];
		s[0] = Status.OK_STATUS;
		TeslaNetworkReplayer player = new TeslaNetworkReplayer(getHost(),
				getTeslaPort(), progressMonitor, new TeslaScenarioContainer(
						scenario),
				new Q7TeslaProblemInformer(s));
		try {
			player.exec();
		} catch (Exception e) {
			throw new CoreException(Q7LaunchingPlugin.createStatus(
					e.getMessage(), e));
		}
		return s[0];

	}

	private static List<Command> splitByRestart(Command command, String id)
			throws CoreException {
		List<Command> list = new ArrayList<Command>();
		if (command instanceof Script) {
			Script script = (Script) command;
			return splitByRestart(EclCoreParser.newCommand(script.getContent(), id), id);
		} else if (command instanceof Sequence) {
			Sequence seq = (Sequence) command;
			if (!checkRestart(seq.getCommands())) {
				list.add(command);
				return list;
			}
			Sequence sub = CoreFactory.eINSTANCE.createSequence();
			for (Command c : seq.getCommands()) {
				if (isEclipseRestartCommand(c)) {
					list.add(sub);
					sub = CoreFactory.eINSTANCE.createSequence();
				} else {
					sub.getCommands().add(copy(c));
				}
			}
			list.add(sub);
		} else {
			checkForUnexpectedRestartInside(command);
			list.add(command);
		}
		return list;
	}

	private static Command copy(Command c) {
		// eclipse 3.4 compatibility:
		// - EcoreUtil.copy raise exception if argument is null
		// - added explicit cast to (Command)
		return (c != null) ? (Command) EcoreUtil.copy(c) : null;
	}

	/**
	 * @return true if this command contains restarts and false otherwise
	 * @throws CoreException
	 *             when there are unexpected restarts
	 */
	private static boolean checkRestart(List<Command> commands)
			throws CoreException {
		boolean haveRestarts = false;
		for (Command command : commands) {
			if (isEclipseRestartCommand(command)) {
				haveRestarts = true;
			} else {
				checkForUnexpectedRestartInside(command);
			}
		}
		return haveRestarts;
	}

	private static void checkForUnexpectedRestartInside(Command newCommand)
			throws CoreException {
		TreeIterator<EObject> allContents = newCommand.eAllContents();
		while (allContents.hasNext()) {
			EObject o = allContents.next();
			if (isEclipseRestartCommand(o)) {
				StringBuilder msg = new StringBuilder();
				msg.append("Unexpected  use of ");
				msg.append(RESTART_COMMAND_NAME);
				msg.append(".\nIt must not be inside of pipes, blocks or substitutions.");
				ExecutionStatus es = new ExecutionStatus(IStatus.ERROR,
						Q7LaunchingPlugin.PLUGIN_ID, msg.toString());
				AstExec restart = (AstExec) o;
				es.setLine(restart.getLine());
				es.setColumn(restart.getColumn());
				es.setLength(restart.getLength());
				throw new CoreException(es);
			}
		}
	}

	private static boolean isEclipseRestartCommand(EObject c) {
		if (c instanceof AstExec) {
			AstExec exec = (AstExec) c;
			if (RESTART_COMMAND_NAME.equals(exec.getName())) {
				return true;
			}
		}
		return false;
	}

	private ISession createEclSession() throws CoreException {
		try {
			return EclTcpClientManager.Instance.startClientSession(
					InetAddress.getByName(getHost()), getEclPort());
		} catch (Exception e) {
			throw new CoreException(Q7LaunchingPlugin.createStatus(
					"Couldn't open ECL session", e));
		}
	}

	private static final String RESTART_COMMAND_NAME = CoreUtils
			.getScriptletNameByClass(TeslaPackage.eINSTANCE
					.getWaitUntilEclipseIsReady());

	/**
	 * For internal USE only
	 */
	@Override
	public void retarget(BaseAut newAut) {
		aut = newAut;
	}

	public void setLocationOnRestart(String location) {
		this.locationOnRestart = location;
	}

	public List<AutBundleState> getBundles() {
		if (this.autInit != null) {
			return new ArrayList<AutBundleState>(this.autInit.getBundleState());
		}
		return null;
	}

	@Override
	public String getCapability() {
		return autStart != null ? autStart.getCapability().getLiteral().toLowerCase() : null;
	}
}
