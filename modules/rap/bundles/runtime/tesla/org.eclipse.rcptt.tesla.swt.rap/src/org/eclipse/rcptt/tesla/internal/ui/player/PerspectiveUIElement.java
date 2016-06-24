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
package org.eclipse.rcptt.tesla.internal.ui.player;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import org.eclipse.rcptt.tesla.core.protocol.ElementKind;
import org.eclipse.rcptt.tesla.internal.ui.IBasicMappingNode;
import org.eclipse.rcptt.tesla.ui.RWTUtils;

public class PerspectiveUIElement implements IBasicMappingNode {

	private final IPerspectiveDescriptor persectiveDescriptor;

	public PerspectiveUIElement(String name) {
		persectiveDescriptor = findPerspectiveDescriptor(name);
	}

	public boolean isPerspeciveFind() {
		return persectiveDescriptor != null;
	}

	public String getGenerationKind() {
		return ElementKind.Perspective.toString();
	}

	public String getPerspectiveId() {
		return persectiveDescriptor.getId();
	}

	private static IPerspectiveDescriptor findPerspectiveDescriptor(String name) {
		IPerspectiveDescriptor[] perspectives = RWTUtils.getWorkbench()
				.getPerspectiveRegistry().getPerspectives();
		for (IPerspectiveDescriptor persectiveDescr : perspectives) {
			if (persectiveDescr.getLabel().equals(name)) {
				return persectiveDescr;
			}
		}
		return null;
	}
}
