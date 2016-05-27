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
package org.eclipse.rcptt.verifications.tree.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.rcptt.verifications.status.TreeItemVerificationError;
import org.eclipse.rcptt.core.model.ModelException;
import org.eclipse.rcptt.internal.ui.Q7UIPlugin;
import org.eclipse.rcptt.tesla.core.utils.WidgetModels;
import org.eclipse.rcptt.ui.controls.SectionWithComposite;
import org.eclipse.rcptt.ui.editors.EditorHeader;
import org.eclipse.rcptt.ui.verification.WidgetVerificationEditor;
import org.eclipse.rcptt.verifications.tree.Cell;
import org.eclipse.rcptt.verifications.tree.Column;
import org.eclipse.rcptt.verifications.tree.Row;
import org.eclipse.rcptt.verifications.tree.Tree;
import org.eclipse.rcptt.verifications.tree.TreePackage;
import org.eclipse.rcptt.verifications.tree.TreeVerification;
import org.eclipse.rcptt.verifications.tree.TreeVerificationUtils;
import org.eclipse.rcptt.verifications.tree.VerifyStyleType;

public class TreeVerificationEditor extends WidgetVerificationEditor {
	Binding treeDataBinding = null;
	TreeTableObservable widgetObservable = null;
	Combo verifyStyleCombo = null;

	public TreeVerification getVerificationElement() {
		try {
			return (TreeVerification) super.getElement().getNamedElement();
		} catch (ModelException e) {
			Q7UIPlugin.log(e);
			return null;
		}
	}

	public void dispose() {
		super.dispose();
	}

	public Control create(Composite parent, FormToolkit toolkit,
			IWorkbenchSite site, EditorHeader header) {

		Section section = new SectionWithComposite("Widget Tree",
				Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED)
				.create(parent, toolkit);
		Composite client = (Composite) section.getClient();
		GridLayoutFactory.fillDefaults().
				numColumns(1).
				spacing(SWT.DEFAULT, 16).
				applyTo(client);
		createWidgetControls(client, toolkit, site, header);

		Composite box = toolkit.createComposite(client);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(box);
		GridLayoutFactory.fillDefaults()
				.numColumns(1)
				.spacing(16, 4)
				.applyTo(box);
		createControls(toolkit, box);

		header.getRecordButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Boolean defaultValue = (Boolean) TreePackage.Literals.VERIFY_TREE_DATA__ENABLE_VERIFY_STYLE
						.getDefaultValue();
				getVerificationElement().setEnableVerifyStyle(defaultValue);
			}
		});

		return section;
	}

	private void createControls(final FormToolkit toolkit,
			final Composite client) {
		final Composite propertiesComposite = toolkit.createComposite(client);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(propertiesComposite);
		GridLayoutFactory.fillDefaults().numColumns(4).applyTo(propertiesComposite);
		createPropertiesControls(toolkit, propertiesComposite);

		Label introLabel = toolkit.createLabel(client,
				"Widget tree should be:");
		introLabel
				.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		introLabel.setBackground(null);
		GridDataFactory.fillDefaults().applyTo(introLabel);

		final Composite treeComposite = toolkit.createComposite(client, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(treeComposite);
		GridLayoutFactory.fillDefaults().applyTo(treeComposite);

		widgetObservable = new TreeTableObservable(treeComposite);
		treeDataBinding = dbc.bindValue(widgetObservable, EMFObservables.observeValue(
				getVerificationElement(),
				TreePackage.Literals.COMMON_TREE_VERIFICATION_DATA__TREE));
	}

	private void createPropertiesControls(final FormToolkit toolkit,
			final Composite parent) {

		GridDataFactory align = GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER);

		Label verifyStyleLabel = toolkit.createLabel(parent,
				"Verify styles:");
		verifyStyleLabel
				.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		verifyStyleLabel.setBackground(null);
		align.applyTo(verifyStyleLabel);

		verifyStyleCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		int selectedStyleInd = 0;
		dbc.bindValue(SWTObservables.observeSelection(verifyStyleCombo),
				EMFObservables.observeValue(getVerificationElement(),
						TreePackage.Literals.VERIFY_TREE_DATA__ENABLE_VERIFY_STYLE));
		for (VerifyStyleType type : VerifyStyleType.values()) {
			verifyStyleCombo.add(type.getLiteral());
			if (type.getValue() == getVerificationElement().getVerifyStyle().getValue()) {
				selectedStyleInd = verifyStyleCombo.getItemCount() - 1;
			}
		}
		verifyStyleCombo.select(selectedStyleInd);
		verifyStyleCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getVerificationElement().setVerifyStyle(VerifyStyleType.get(verifyStyleCombo.getText()));
				widgetObservable.updateOutputFormat();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		align.applyTo(verifyStyleCombo);

		Button unverifiedChildrenCheck = new Button(parent, SWT.CHECK);
		unverifiedChildrenCheck.setText("Allow uncaptured children");
		unverifiedChildrenCheck
				.setToolTipText("Tree items from verification without chirdren, may contain children in UI. " +
						"This might happen when tree was not fully expanded during capturing.");
		dbc.bindValue(SWTObservables.observeSelection(unverifiedChildrenCheck),
				EMFObservables.observeValue(getVerificationElement(),
						TreePackage.Literals.VERIFY_TREE_DATA__ALLOW_UNCAPTURED_CHILDREN));
		align.applyTo(unverifiedChildrenCheck);

		// Button missingColumnsCheck = new Button(parent, SWT.CHECK);
		// missingColumnsCheck.setText("Allow missing columns");
		// missingColumnsCheck.setToolTipText("This verification may contain columns, which are not present in UI");
		// dbc.bindValue(SWTObservables.observeSelection(missingColumnsCheck),
		// EMFObservables.observeValue(getVerificationElement(),
		// TreePackage.Literals.VERIFY_TREE_DATA__ALLOW_MISSING_COLUMNS));

		Button verifyIconsCheck = new Button(parent, SWT.CHECK);
		verifyIconsCheck.setText("Verify icons");

		dbc.bindValue(SWTObservables.observeSelection(verifyIconsCheck),
				EMFObservables.observeValue(getVerificationElement(),
						TreePackage.Literals.VERIFY_TREE_DATA__VERIFY_ICONS));
		verifyIconsCheck.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetObservable.updateOutputFormat();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		align.applyTo(verifyIconsCheck);
	}

	@Override
	public void select(Object data) {
		if (data instanceof TreeItemVerificationError) {
			TreeItemVerificationError err = (TreeItemVerificationError) data;

			widgetObservable.selectItemAt(err.getItemIndexPath());
		}
	}

	@Override
	public void setSelectionAtLine(int line) {
	}

	private class TreeTableObservable extends AbstractObservableValue {
		private Composite treeComposite = null;
		private TreeViewer viewer = null;
		private VerificationTreeLabelProvider labelProvider = null;
		private List<Image> columnImages = new ArrayList<Image>();

		public TreeTableObservable(Composite treeComposite) {
			this.treeComposite = treeComposite;
		}

		public void updateOutputFormat() {
			treeComposite.setRedraw(false);
			labelProvider.setDrawIcons(getVerificationElement().isVerifyIcons());
			labelProvider.setIgnoreStyle(getVerificationElement().getVerifyStyle() == VerifyStyleType.IGNORE_STYLES);
			labelProvider
					.setSkipStyledText(getVerificationElement().getVerifyStyle() == VerifyStyleType.IGNORE_STYLED_TEXT);
			TreeColumn[] cols = viewer.getTree().getColumns();
			if (getVerificationElement().isVerifyIcons()) {
				for (int i = 0; i < cols.length; i++) {
					if (i<columnImages.size())
						cols[i].setImage(columnImages.get(i));
				}
			} else {
				for (int i = 0; i < cols.length; i++) {
					cols[i].setImage(null);
				}
			}
			viewer.refresh();
			verifyStyleCombo.setEnabled(getVerificationElement().isEnableVerifyStyle());
			treeComposite.setRedraw(true);
		}

		private TreePath toTreePath(List<Integer> indices) {
			@SuppressWarnings("unchecked")
			List<Row> rows = (List<Row>) viewer.getInput();
			List<Row> segments = new ArrayList<Row>();
			for (int i : indices) {
				if (i < 0 || i >= rows.size()) {
					break;
				}
				Row curr = rows.get(i);
				segments.add(curr);
				rows = curr.getChildren();
			}
			return new TreePath(segments.toArray());
		}

		public void selectItemAt(List<Integer> itemIndexPath) throws IllegalArgumentException {
			if (viewer != null && viewer.getTree() != null && itemIndexPath.size() > 0) {
				TreePath treePath = toTreePath(itemIndexPath);
				viewer.setSelection(new TreeSelection(treePath), true);
			}
		}

		@Override
		public Object getValueType() {
			return null;
		}

		@Override
		protected Object doGetValue() {
			return null;
		}

		@Override
		protected void doSetValue(Object value) {
			if (value instanceof Tree) {
				Tree treeData = (Tree) value;
				Map<String, Image> images = deserializeImages(treeComposite.getDisplay(), getVerificationElement()
						.getImages());

				treeComposite.setRedraw(false);
				viewer = createTreeWidget(treeComposite, treeData, images);

				if (treeData.getSortColumnInd() != -1) {
					viewer.getTree().setSortColumn(viewer.getTree().getColumn(treeData.getSortColumnInd()));
					viewer.getTree().setSortDirection(treeData.getSortDirection());
				}
				labelProvider = new VerificationTreeLabelProvider(images,
						getVerificationElement().isVerifyIcons(),
						getVerificationElement().getVerifyStyle() == VerifyStyleType.IGNORE_STYLES,
						getVerificationElement().getVerifyStyle() == VerifyStyleType.IGNORE_STYLED_TEXT);
				viewer.setLabelProvider(labelProvider);
				viewer.setContentProvider(new VerificationTreeContentProvider());
				viewer.setInput(treeData.getRows());

				viewer.expandAll();

				widgetObservable.updateOutputFormat();

				treeComposite.setRedraw(true);
			}
		}

		private Map<String, Image> deserializeImages(Display display, EMap<String, byte[]> images) {
			Map<String, Image> result = new HashMap<String, Image>();
			for (Entry<String, byte[]> img : images.entrySet()) {
				result.put(img.getKey(), deserializeImage(display, img.getValue()));
			}
			return result;
		}

		private TreeViewer createTreeWidget(Composite parent, Tree treeData, Map<String, Image> images) {
			for (Control control : parent.getChildren()) {
				control.dispose();
			}
			TreeViewer viewer = new TreeViewer(parent, treeData.getStyle() | SWT.FULL_SELECTION);
			org.eclipse.swt.widgets.Tree tree = viewer.getTree();
			tree.setEnabled(treeData.isEnabled());
			tree.setHeaderVisible(treeData.isHeaderVisible());
			tree.setLinesVisible(treeData.isLinesVisible());
			tree.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if (event.detail == SWT.CHECK && (event.item instanceof TreeItem)) {
						TreeItem item = (TreeItem) event.item;
						item.setChecked(!item.getChecked());
					}
				}
			});

			TreeViewerEditor.create(viewer, new ColumnViewerEditorActivationStrategy(viewer) {
				protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
					return (super.isEditorActivationEvent(event) && (getVerificationElement().getVerifyStyle()
							.getValue() == VerifyStyleType.IGNORE_STYLED_TEXT_VALUE));
				}
			}, ColumnViewerEditor.DEFAULT);

			String[] columnProperties = new String[treeData.getColumns().size()];
			CellEditor[] cellEditors = new CellEditor[treeData.getColumns().size()];

			for (int columnNum = 0; columnNum < treeData.getColumns().size(); columnNum++) {
				final TreeViewerColumn column = new TreeViewerColumn(viewer, SWT.NONE);

				// apply only for table verifications
				Column treeColumn = treeData.getColumns().get(columnNum);
				column.getColumn().setText(treeColumn.getName());
				column.getColumn().setWidth(treeColumn.getWidth());
				column.getColumn().setToolTipText(treeColumn.getTooltip());

				String imgPath = TreeVerificationUtils.getDecoratedImagePath(treeColumn.getImage());
				if (images.containsKey(imgPath)) {
					Image img = images.get(imgPath);
					columnImages.add(img);
					if (getVerificationElement().isVerifyIcons()) {
						column.getColumn().setImage(img);
					}
				} else {
					columnImages.add(null);
				}
				column.setLabelProvider(new ColumnLabelProvider());
				columnProperties[columnNum] = "" + columnNum;
				cellEditors[columnNum] = new TextCellEditor(tree);
			}

			if (treeData.getColumns().size() == 0) {
				//  initial for tree
				viewer.setColumnProperties(new String[] { "0" });
				viewer.setCellEditors(new CellEditor[] { new TextCellEditor(tree) });
			} else {
				viewer.setColumnProperties(columnProperties);
				viewer.setCellEditors(cellEditors);
			}

			viewer.setCellModifier(new ICellModifier() {
				public void modify(Object element, String property, Object value) {
					String newValue = (String) value;
					int index = Integer.parseInt(property);
					if (((TreeItem) element).getData() instanceof Row) {
						Cell cellItem = ((Row) ((TreeItem) element).getData()).getValues().get(index);
						if (newValue != null && !cellItem.getData().getText().equals(newValue)) {
							cellItem.getData().setText(newValue);
							cellItem.getStyle().clear();
							getVerificationElement().setEnableVerifyStyle(false);
							updateOutputFormat();
						}
					}
				}

				public Object getValue(Object element, String property) {
					String result = "";
					if (element instanceof Row) {
						int index = Integer.parseInt(property);
						Cell cellItem = ((Row) element).getValues().get(index);
						result = WidgetModels.getTextWithoutStyle(cellItem.getStyle(), cellItem.getData().getText());
					}
					return result;
				}

				public boolean canModify(Object element, String property) {
					return getVerificationElement().getVerifyStyle()
							.getValue() == VerifyStyleType.IGNORE_STYLED_TEXT_VALUE;
				}
			});

			GridDataFactory.fillDefaults().grab(true, true).hint(100, 10).applyTo(viewer.getTree());
			parent.layout(true, true);

			return viewer;
		}
	}

	private static byte[] serializeImage(Image img) {
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { img.getImageData() };
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		imageLoader.save(stream, SWT.IMAGE_PNG);
		return stream.toByteArray();
	}

	private static Image deserializeImage(Display display, byte[] img) {
		ImageLoader imageLoader = new ImageLoader();
		ByteArrayInputStream stream = new ByteArrayInputStream(img);
		ImageData[] data = imageLoader.load(stream);
		return new Image(display, data[0]);
	}
}
