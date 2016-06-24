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
package org.eclipse.rcptt.ctx.parameters.impl.rap;

import java.util.HashMap;

public class ParametersRuntime {

	public static String PLUGIN_ID = "org.eclipse.rcptt.ctx.parameters";

	private static HashMap<String, String> params = new HashMap<String, String>();

	public static void resetParams() {
		params.clear();
	}

	public static String getParam(String name) {
		return params.get(name);
	}

	public static void setParam(String name, String value) {
		params.put(name, value);
	}

}
