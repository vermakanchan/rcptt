package org.eclipse.rcptt.ctx.capability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rcptt.core.scenario.CapabilityContext;
import org.eclipse.rcptt.core.scenario.CapabilityContextItem;
import org.eclipse.rcptt.core.scenario.Context;
import org.eclipse.rcptt.core.scenario.ScenarioFactory;

public class CapabilityManager {
	private static CapabilityManager manager;

	public static CapabilityManager getManager() {
		if (manager == null) {
			manager = new CapabilityManager();
		}
		return manager;
	}

	public String[] getAvaliableCapabiltiies(CapabilityContext context) {
		return getAvaliableCapabiltiies(context, null);
	}

	public String[] getAvaliableCapabiltiies(CapabilityContext context, CapabilityContextItem ignore) {
		final List<String> result = new ArrayList<String>(Arrays.asList(getAllCapabilities()));

		for (CapabilityContextItem item : context.getItems()) {
			if (!item.equals(ignore)) {
				result.removeAll(item.getCapability());
			}
		}

		return result.toArray(new String[result.size()]);
	}

	public String[] getAllCapabilities() {
		return new String[] { "e3", "e4", "rap" };
	}

	public CapabilityContextItem addCapability(CapabilityContext context, String... capabilities) {
		checkContext(context, null, capabilities);

		final CapabilityContextItem item = ScenarioFactory.eINSTANCE.createCapabilityContextItem();
		item.getCapability().addAll(Arrays.asList(capabilities));

		context.getItems().add(item);
		return item;
	}

	public void updateCapabilityItem(CapabilityContextItem item, String... capabilities) {
		checkContext((CapabilityContext) item.eContainer(), item, capabilities);

		item.getCapability().clear();
		item.getCapability().addAll(Arrays.asList(capabilities));
	}

	public void convertCoCapabilityContext(Context ctx) {

	}

	private void checkContext(CapabilityContext context, CapabilityContextItem ignore, String... capabilities) {
		for (CapabilityContextItem item : context.getItems()) {
			if (!item.equals(ignore)) {
				for (String capability : capabilities) {
					if (item.getCapability().contains(capability)) {
						throw new IllegalStateException("The context contains" + capability + "capability.");
					}
				}
			}
		}
	}
}
