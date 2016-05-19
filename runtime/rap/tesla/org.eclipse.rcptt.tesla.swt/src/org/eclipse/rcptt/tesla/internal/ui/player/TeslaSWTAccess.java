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
package org.eclipse.rcptt.tesla.internal.ui.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Synchronizer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.progress.DeferredTreeContentManager;

import org.eclipse.rcptt.tesla.internal.core.TeslaCore;
import org.eclipse.rcptt.tesla.swt.events.TimerUtils;

@SuppressWarnings("rawtypes")
public class TeslaSWTAccess {

	public static TreeViewer getTreeViewer(Tree tree) {
		return getThis(TreeViewer.class, tree, SWT.Expand);
	}

	public static TableViewer getTableViewer(Table tree) {
		return getThis(TableViewer.class, tree, SWT.Dispose);
	}

	public static CheckboxTableViewer getCheckboxTableViewer(Table tree) {
		return getThis(CheckboxTableViewer.class, tree, SWT.Dispose);
	}

	public static ListViewer getListViewer(org.eclipse.swt.widgets.List list) {
		return getThis(ListViewer.class, list, SWT.Dispose);
	}

	public static Viewer getViewer(org.eclipse.swt.widgets.Control w) {
		return getThis(Viewer.class, w, SWT.Dispose);
	}

	public static <T> T getThis(Class<T> clazz, Widget widget, int event) {
		Listener[] listeners = widget.getListeners(event);
		for (Listener listener : listeners) {
			Object lookFor = listener;
			if (listener instanceof TypedListener) {
				lookFor = ((TypedListener) listener).getEventListener();
			}
			Object viewer = getFromField(lookFor, "this$0");
			if (clazz.isInstance(viewer)) {
				return clazz.cast(viewer);
			}
			viewer = getFromField(lookFor, "arg$1");
			if (clazz.isInstance(viewer)) {
				return clazz.cast(viewer);
			}

		}
		return null;
	}

	private static Object getFromField(Object lookFor, String arg) {
		try {
			Field this$0 = lookFor.getClass().getDeclaredField(arg);
			if (this$0 != null) {
				this$0.setAccessible(true);
				Object viewer = this$0.get(lookFor);
				return viewer;
			}
		} catch (Throwable e) {
			// Ignore
		}
		return null;
	}

	public static <T> T getThis(Class<T> clazz, Object object) {
		try {
			Field this$0 = object.getClass().getDeclaredField("this$0");
			this$0.setAccessible(true);
			Object viewer = this$0.get(object);
			if (clazz.isInstance(viewer))
				return clazz.cast(viewer);
		} catch (Throwable e) {
			// ignore exceptions
		}
		return null;
	}

	private static Object getThis(String clazz, Control control, int event) {
		Listener[] listeners = control.getListeners(event);
		for (Listener listener : listeners) {
			Object lookFor = listener;
			if (listener instanceof TypedListener) {
				lookFor = ((TypedListener) listener).getEventListener();
			}
			try {
				Field this$0 = lookFor.getClass().getDeclaredField("this$0");
				this$0.setAccessible(true);
				Object viewer = this$0.get(lookFor);
				if (viewer.getClass().getName().equals(clazz)) {
					return viewer;
				}
			} catch (Throwable e) {
				// ignore exceptions
			}
		}
		return null;
	}

	public static ColorSelector getColorSelector(Button widget) {
		return getThis(ColorSelector.class, widget, SWT.Dispose);
	}

	public static CheckboxTreeViewer getCheckBoxTreeViewer(SWTUIElement parent) {
		return getThis(CheckboxTreeViewer.class, PlayerWrapUtils.unwrapWidget(parent), SWT.Expand);
	}

	public static Object getPropertySheet(Control tree) {
		return getThis("org.eclipse.ui.views.properties.PropertySheetViewer", tree, SWT.MouseDown);
	}

	public static ControlEditor getControlEditor(Composite parent) {
		return getThis(ControlEditor.class, parent, SWT.Resize);
	}

	public static ColumnViewerEditor getColumnViewerEditor(CellEditor cellEditor) {
		try {
			Field listeners = CellEditor.class.getDeclaredField("listeners");
			listeners.setAccessible(true);
			ListenerList listenersList = (ListenerList) listeners.get(cellEditor);
			if (listenersList != null) {
				Object[] objects = listenersList.getListeners();
				for (Object object : objects) {
					if (object instanceof ICellEditorListener) {
						ICellEditorListener list = (ICellEditorListener) object;
						try {
							Field this$0 = list.getClass().getDeclaredField("this$0");
							this$0.setAccessible(true);
							Object viewer = this$0.get(list);
							if (viewer instanceof ColumnViewerEditor) {
								return (ColumnViewerEditor) viewer;
							}
						} catch (NoSuchFieldException e) {
							// TeslaCore.log(e);
						}
					}
				}
			}
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	public static CellEditor getCellEditorFromColumnEditor(ColumnViewerEditor editor) {
		Class<? extends ColumnViewerEditor> class1 = ColumnViewerEditor.class;
		Field field;
		CellEditor cEditor = null;
		try {
			field = class1.getDeclaredField("cellEditor");
			field.setAccessible(true);
			cEditor = (CellEditor) field.get(editor);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return cEditor;
	}

	public static Text getCComboText(CCombo combo) {
		try {
			Field field = CCombo.class.getDeclaredField("text");
			field.setAccessible(true);
			return (Text) field.get(combo);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	public static Object getThis(Job job) {
		Class<? extends Job> class1 = job.getClass();
		Field field;
		try {
			field = class1.getDeclaredField("this$0");
			field.setAccessible(true);
			Object object = field.get(job);
			return object;
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	public static Object call(Object object, String methodName, Object... args) {
		try {
			final Class<?> argTypes[] = new Class<?>[args.length];
			for (int i = 0; i < argTypes.length; i++)
				argTypes[i] = args[i].getClass();

			Class<?> class_ = object.getClass();
			while (true) {
				try {
					final Method method = class_.getDeclaredMethod(methodName, argTypes);
					method.setAccessible(true);
					return method.invoke(object, args);
				} catch (NoSuchMethodException e) {
					class_ = class_.getSuperclass();
					if (class_ == null)
						return null;
				}
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T getField(Class<T> targetClass, Object object, String fieldName) {
		if (object == null)
			return null;
		try {
			Class<?> class_ = object.getClass();

			while (true) {
				try {
					Field field = class_.getDeclaredField(fieldName);
					field.setAccessible(true);
					Object value = field.get(object);
					if (value == null)
						return null;
					if (targetClass.isInstance(value))
						return targetClass.cast(value);
					else
						return null;

				} catch (NoSuchFieldException e) {
					class_ = class_.getSuperclass();
					if (class_ == null)
						return null;
				}
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static void setField(Object object, String fieldName, Object value) {
		try {
			Class<?> class_ = object.getClass();
			Field field = class_.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			return;
		}
	}

	public static void setField(Class<?> class_, Object object, String fieldName, Object value) {
		try {
			Field field = class_.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			return;
		}
	}

	public static boolean ignoreMarkersView(Object data) {
		if (data != null) {
			if (data.getClass().getName().equals("org.eclipse.ui.internal.views.markers.MarkerEntry")) {
				return true;
				// Avoild calling expand
				// for MarkerEntry. This
				// cause NPE.
			}
		}
		return false;
	}

	public static ViewerCell createUpdateCell(Object element) {
		try {
			Class<?> cl = Class.forName("org.eclipse.jface.UpdateViewerCell");
			Constructor<?> constructor = cl.getConstructor(Object.class);
			return (ViewerCell) constructor.newInstance(element);

		} catch (ClassNotFoundException e) {
			TeslaCore.log(e);
		}
		// ViewerCell cell = new
		// provider.update(cell);
		catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	public static void fireCheckStateChanged(final CheckboxTreeViewer viewer, final boolean state,
			Object elementToCheck) {
		Method method;
		try {
			method = CheckboxTreeViewer.class.getDeclaredMethod("fireCheckStateChanged", CheckStateChangedEvent.class);
			method.setAccessible(true);
			method.invoke(viewer, new CheckStateChangedEvent(viewer, elementToCheck, !state));
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
	}

	public static void fireCheckStateChanged(final CheckboxTableViewer viewer, final boolean newState, Object cur) {
		Method method;
		try {
			method = CheckboxTableViewer.class.getDeclaredMethod("fireCheckStateChanged", CheckStateChangedEvent.class);
			method.setAccessible(true);
			method.invoke(viewer, new CheckStateChangedEvent(viewer, cur, newState));
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
	}

	public static boolean isDefferedTreeContentProvider(Job job) {
		if (!job.isSystem()) {
			// Deferred tree content update job is system
			return false;
		}
		// Check for deferred tree job update
		try {
			Class<? extends Job> class1 = job.getClass();
			Field field = class1.getDeclaredField("this$0");
			field.setAccessible(true);
			Object object = field.get(job);
			if (object instanceof DeferredTreeContentManager) {
				return true;
			}
			return false;
		} catch (Throwable e) {
		}
		return false;
	}

	public static void callMethod(final Object vv, final String name, final Object... args) {
		callMethod(CellEditor.class, vv, name, args);
	}

	@SuppressWarnings("unchecked")
	public static Object callMethod(Class clazz, final Object vv, final String name, final Object... args) {
		try {
			final Class<?> argTypes[] = new Class<?>[args.length];
			for (int i = 0; i < argTypes.length; i++) {
				argTypes[i] = args[i].getClass();
			}
			final Method method = clazz.getDeclaredMethod(name, argTypes);
			method.setAccessible(true);
			return method.invoke(vv, args);
		} catch (final Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Object callMethod(Class clazz, final Object vv, final String name, final Class[] argTypes,
			final Object... args) {
		try {
			final Method method = clazz.getDeclaredMethod(name, argTypes);
			method.setAccessible(true);
			return method.invoke(vv, args);
		} catch (final Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	public static void callMethodValueChanged(final Object vv, final String name, final boolean val1,
			final boolean val2) {
		try {
			final Method method = CellEditor.class.getDeclaredMethod(name,
					new Class<?>[] { boolean.class, boolean.class });
			method.setAccessible(true);
			method.invoke(vv, val1, val2);
		} catch (final Throwable e) {
			TeslaCore.log(e);
		}
	}

	public static void callHandleSelect(final Object vv, final TreeItem[] selection) {
		try {
			final Method method = vv.getClass().getDeclaredMethod("handleSelect", new Class[] { TreeItem.class });
			method.setAccessible(true);
			method.invoke(vv, selection[0]);
		} catch (final Throwable e) {
			TeslaCore.log(e);
		}
	}

	public static void callHandleSelect(final Object vv, final TableItem[] selection) {
		try {
			final Method method = vv.getClass().getDeclaredMethod("handleSelect", new Class[] { TreeItem.class });
			method.setAccessible(true);
			method.invoke(vv, selection[0]);
		} catch (final Throwable e) {
			TeslaCore.log(e);
		}
	}

	public static Runnable[] getTimers(Display display) {
		try {
			return TimerUtils.getTimers(display);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return new Runnable[0];
	}

	public static Object getThis(Object job) {
		Class class1 = job.getClass();
		Field field;
		try {
			field = class1.getDeclaredField("this$0");
			field.setAccessible(true);
			Object object = field.get(job);
			return object;
		} catch (Throwable e) {
			// Ignore
			// TeslaCore.log(e);
		}
		return null;
	}

	public static Control getBookPage(PageBook book) {
		Class<? extends PageBook> class1 = book.getClass();
		try {
			Field field = class1.getDeclaredField("currentPage");
			field.setAccessible(true);
			return (Control) field.get(book);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	public static boolean getHasLocation(Menu menu) {
		try {
			Field locationField = Menu.class.getDeclaredField("hasLocation");
			locationField.setAccessible(true);
			return ((Boolean) locationField.get(menu)).booleanValue();
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return false;
	}

	public static void setHasLocation(Menu menu, boolean oldHasLocation) {
		try {
			Field locationField = Menu.class.getDeclaredField("hasLocation");
			locationField.setAccessible(true);
			locationField.set(menu, Boolean.valueOf(oldHasLocation));
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
	}

	public static Rectangle getCTabItemCloseRect(CTabItem tabItem) {
		try {
			Field locationField = CTabItem.class.getDeclaredField("closeRect");
			locationField.setAccessible(true);
			return ((Rectangle) locationField.get(tabItem));
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;

	}

	public static Menu getCTabFolderItemListMenu(CTabFolder tabFolder) {
		try {
			Field menuField = CTabFolder.class.getDeclaredField("showMenu");
			menuField.setAccessible(true);
			return ((Menu) menuField.get(tabFolder));
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;

	}

	public static int getRunnables(Synchronizer synchronizer) {
		try {
			Field locationField = Synchronizer.class.getDeclaredField("messageCount");
			locationField.setAccessible(true);
			return ((Integer) locationField.get(synchronizer)).intValue();
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return 0;
	}

	public static boolean getWizardDialogHasActiveOperations(WizardDialog dialog) {
		try {
			Field locationField = WizardDialog.class.getDeclaredField("activeRunningOperations");
			locationField.setAccessible(true);
			return ((Long) locationField.get(dialog)).longValue() > 0;
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return false;
	}

	public static void setWizardDialogHasActiveOperations(WizardDialog dialog, long count) {
		try {
			Field locationField = WizardDialog.class.getDeclaredField("activeRunningOperations");
			locationField.setAccessible(true);
			locationField.set(dialog, count);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
	}

	public static ProgressMonitorPart getProgressMonitorPart(WizardDialog dialog) {
		try {
			Field locationField = WizardDialog.class.getDeclaredField("progressMonitorPart");
			locationField.setAccessible(true);
			return ((ProgressMonitorPart) locationField.get(dialog));
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static boolean getJobEventReSchedule(IJobChangeEvent event) {
		try {
			Field field = org.eclipse.core.internal.jobs.JobChangeEvent.class.getDeclaredField("reschedule");
			field.setAccessible(true);
			Boolean reschedule = (Boolean) field.get(event);
			return reschedule.booleanValue();
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return false;
	}

	@SuppressWarnings("restriction")
	public static org.eclipse.ui.internal.decorators.DecorationScheduler getDecorationScheduler(
			org.eclipse.ui.internal.decorators.DecoratorManager manager) {
		try {
			Field field = org.eclipse.ui.internal.decorators.DecoratorManager.class.getDeclaredField("scheduler");
			field.setAccessible(true);
			return (org.eclipse.ui.internal.decorators.DecorationScheduler) field.get(manager);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static Map getDecorationResultMap(org.eclipse.ui.internal.decorators.DecorationScheduler manager) {
		try {
			Field field = org.eclipse.ui.internal.decorators.DecorationScheduler.class.getDeclaredField("resultCache");
			field.setAccessible(true);
			return (Map) field.get(manager);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static Object getDecoratorManagerFamily() {
		return org.eclipse.ui.internal.decorators.DecoratorManager.FAMILY_DECORATE;
	}

	@SuppressWarnings("restriction")
	public static int getJobFlags(Job job) {
		try {
			Field field = org.eclipse.core.internal.jobs.InternalJob.class.getDeclaredField("flags");
			field.setAccessible(true);
			return (Integer) field.get(job);
		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return 0;
	}

	public static CTabFolderEvent createCTabFolderEvent(Widget widget) {
		try {
			Class<?> eventClassDefinition = Class.forName("org.eclipse.swt.custom.CTabFolderEvent");
			Class<?>[] ArgsClass = new Class<?>[] { Widget.class };
			Object[] Args = new Object[] { widget };
			Constructor<?> classConstructor = eventClassDefinition.getDeclaredConstructor(ArgsClass);
			classConstructor.setAccessible(true);
			return (CTabFolderEvent) classConstructor.newInstance(Args);

		} catch (Throwable e) {
			TeslaCore.log(e);
		}
		return null;

	}
}
