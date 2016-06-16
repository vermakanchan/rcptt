package org.eclipse.rcptt.ctx.capability.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.rcptt.ctx.capability.ui.views.messages"; //$NON-NLS-1$
	public static String CapabilityContextEditor_Title0;
	public static String CapabilityContextTables_AddActionText;
	public static String CapabilityContextTables_SectionName;
	public static String CapabilityDialog_Message;
	public static String CapabilityDialog_Title;
	public static String CapabiltyContextTable_DeleteActionTooltip;
	public static String CapabiltyContextTable_EditActionTooltip;
	public static String CapabiltyContextTable_ProjectConflictText;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
