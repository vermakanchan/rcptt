package org.eclipse.rcptt.ecl.platform.internal.ui.commands;

import static org.eclipse.rcptt.ecl.platform.ui.PlatformUIPlugin.createError;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.rcptt.ecl.core.Command;
import org.eclipse.rcptt.ecl.platform.ui.commands.ToClipboard;
import org.eclipse.rcptt.ecl.runtime.ICommandService;
import org.eclipse.rcptt.ecl.runtime.IProcess;

public class ToClipboardService implements ICommandService {
	public IStatus service(Command command, IProcess context) throws InterruptedException, CoreException {
		if (!(command instanceof ToClipboard)) {
			return Status.CANCEL_STATUS;
		}
		final String input = ((ToClipboard) command).getInput(); 
		if (input == null) {
			return createError("No input specified");
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(input);
		clipboard.setContents(strSel, null);
		return Status.OK_STATUS;
	}
}
