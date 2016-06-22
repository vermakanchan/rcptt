package org.eclipse.rcptt.tesla.jface.rap;

import java.lang.reflect.Field;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.rcptt.tesla.core.am.rap.AspectManager;
import org.eclipse.rcptt.tesla.core.context.ContextManagement;
import org.eclipse.rcptt.tesla.core.context.ContextManagement.Context;
import org.eclipse.rcptt.tesla.swt.dialogs.SWTDialogManager;
import org.eclipse.rcptt.tesla.swt.events.TeslaEventManager;

public aspect CellEditorAspect {

	public CellEditorAspect() {
		AspectManager.activateAspect(JFaceAspectsActivator.PLUGIN_ID, this
				.getClass().getName());
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	after(CellEditor cellEditor): 
		execution(void org.eclipse.rap.jface.viewers.CellEditor.activate ()) && target(cellEditor) {
		TeslaCellEditorManager.getInstance().addManager(cellEditor);
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	after(CellEditor cellEditor,
			ColumnViewerEditorActivationEvent activationEvent): 
		execution(void org.eclipse.rap.jface.viewers.CellEditor.activate (ColumnViewerEditorActivationEvent)) && target(cellEditor) && args(activationEvent) {
		TeslaCellEditorManager.getInstance().addManager(cellEditor);
	}
	
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(CellEditor cellEditor):
	 execution(void org.eclipse.rap.jface.viewers.CellEditor.focusLost ()) &&
	 target(cellEditor) {
		if (TeslaEventManager.getManager().hasListeners()
				&& TeslaCellEditorManager.getInstance().isInActivation()) {
			return null; // No focus lost
		}
		return proceed(cellEditor);
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(CellEditor cellEditor): 
		execution(void org.eclipse.rap.jface.viewers.CellEditor.deactivate ()) && target(cellEditor) {
		if (TeslaEventManager.getManager().hasListeners()
				&& TeslaCellEditorManager.getInstance().removeManager(
						cellEditor)) {
			// Manager are not player activated one.
			return proceed(cellEditor);
		}
		Context context = ContextManagement.currentContext();
		if (context.contains("org.eclipse.rap.jface.viewers.CellEditor", "create")) {
			return proceed(cellEditor);
		}
		if (!TeslaEventManager.getManager().hasListeners()) {
			return proceed(cellEditor);
		}
		return null;
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(MessageDialog box):
		execution(int org.eclipse.rap.jface.dialogs.MessageDialog.open()) && target(box) {
		try {
			if (SWTDialogManager.isCancelAllMessageBoxes()) {
				Field declaredField = MessageDialog.class
						.getDeclaredField("buttonLabels");
				declaredField.setAccessible(true);
				String[] fields = (String[]) declaredField.get(box);
				int no_id = -1;
				int cancel_id = -1;
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].equals(IDialogConstants.get().NO_LABEL)) {
						no_id = i;
					}
					if (fields[i].equals(IDialogConstants.get().NO_TO_ALL_LABEL)) {
						no_id = i;
					}
					if (fields[i].equals(IDialogConstants.get().CANCEL_LABEL)) {
						cancel_id = i;
					}
				}
				if (no_id != -1) {
					return no_id;
				}
				if (cancel_id != -1) {
					// Could hang
					return cancel_id;
				}
				return SWT.CANCEL;
			}
		} catch (Throwable e) {
			JFaceAspectsActivator.log(e);
		}
		return proceed(box);
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	before(ControlDecoration decoration, Control ctrl, int style,
			Composite composite):
		execution(org.eclipse.rap.jface.fieldassist.ControlDecoration.new(Control, int, Composite))
		&& target(decoration) && args(ctrl, style, composite) {
		ControlDecoratorRecordingHolder.add(ctrl, decoration);
		ControlDecoratorRecordingHolder.add(composite, decoration);
	}
}
