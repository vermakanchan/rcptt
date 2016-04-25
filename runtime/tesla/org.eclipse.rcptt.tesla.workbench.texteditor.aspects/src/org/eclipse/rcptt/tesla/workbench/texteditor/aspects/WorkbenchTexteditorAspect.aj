package org.eclipse.rcptt.tesla.workbench.texteditor.aspects;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.rcptt.tesla.core.am.AspectManager;

public aspect WorkbenchTexteditorAspect {
	public WorkbenchTexteditorAspect() {
		AspectManager.activateAspect(
				WorkbenchTexteditorAspectActivator.PLUGIN_ID, this.getClass()
						.getName());
	}

//	@SuppressAjWarnings("adviceDidNotMatch")
//	ISourceViewer around(AbstractTextEditor editor): 
//		execution(ISourceViewer org.eclipse.ui.texteditor.AbstractTextEditor.createSourceViewer(Composite, IVerticalRuler, int))
//		&& target(editor) {
//		ISourceViewer result = proceed(editor);
//		try {
//			WorkbenchTexteditorManager.mapEditor(result, editor);
//		} catch (Throwable e) {
//			WorkbenchTexteditorAspectActivator.log(e);
//		}
//		return result;
//	}
}
