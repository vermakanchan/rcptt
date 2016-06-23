package org.eclipse.rcptt.util.swt.internal.ui;

import org.eclipse.rcptt.util.DisplayUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

public class DefaultDisplayUtils implements DisplayUtils {

	@Override
	public boolean isWidget(Object object) {
		return object instanceof Widget;
	}

	@Override
	public void asyncExec(Object widget, Runnable runnable) {
		getDisplay(widget).asyncExec(runnable);
	}

	@Override
	public void syncExec(Object widget, Runnable runnable) {
		getDisplay(widget).syncExec(runnable);
	}


	private Display getDisplay(Object object)
	{
		return isWidget(object) ? ((Widget)object).getDisplay() : Display.getDefault();
	}
}