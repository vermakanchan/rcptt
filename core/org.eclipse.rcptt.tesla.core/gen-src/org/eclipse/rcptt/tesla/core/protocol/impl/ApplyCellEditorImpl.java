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
package org.eclipse.rcptt.tesla.core.protocol.impl;

import org.eclipse.rcptt.tesla.core.protocol.ApplyCellEditor;
import org.eclipse.rcptt.tesla.core.protocol.ProtocolPackage;

import org.eclipse.rcptt.tesla.core.protocol.raw.Element;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Apply Cell Editor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.rcptt.tesla.core.protocol.impl.ApplyCellEditorImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.rcptt.tesla.core.protocol.impl.ApplyCellEditorImpl#getElement <em>Element</em>}</li>
 *   <li>{@link org.eclipse.rcptt.tesla.core.protocol.impl.ApplyCellEditorImpl#isDeactivate <em>Deactivate</em>}</li>
 *   <li>{@link org.eclipse.rcptt.tesla.core.protocol.impl.ApplyCellEditorImpl#getColumn <em>Column</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ApplyCellEditorImpl extends EObjectImpl implements ApplyCellEditor {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final int ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected int id = ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getElement() <em>Element</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElement()
	 * @generated
	 * @ordered
	 */
	protected Element element;

	/**
	 * The default value of the '{@link #isDeactivate() <em>Deactivate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeactivate()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEACTIVATE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDeactivate() <em>Deactivate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeactivate()
	 * @generated
	 * @ordered
	 */
	protected boolean deactivate = DEACTIVATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getColumn() <em>Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColumn()
	 * @generated
	 * @ordered
	 */
	protected static final int COLUMN_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getColumn() <em>Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColumn()
	 * @generated
	 * @ordered
	 */
	protected int column = COLUMN_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ApplyCellEditorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ProtocolPackage.Literals.APPLY_CELL_EDITOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(int newId) {
		int oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProtocolPackage.APPLY_CELL_EDITOR__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetElement(Element newElement, NotificationChain msgs) {
		Element oldElement = element;
		element = newElement;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT, oldElement, newElement);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setElement(Element newElement) {
		if (newElement != element) {
			NotificationChain msgs = null;
			if (element != null)
				msgs = ((InternalEObject)element).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT, null, msgs);
			if (newElement != null)
				msgs = ((InternalEObject)newElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT, null, msgs);
			msgs = basicSetElement(newElement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT, newElement, newElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDeactivate() {
		return deactivate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeactivate(boolean newDeactivate) {
		boolean oldDeactivate = deactivate;
		deactivate = newDeactivate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProtocolPackage.APPLY_CELL_EDITOR__DEACTIVATE, oldDeactivate, deactivate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setColumn(int newColumn) {
		int oldColumn = column;
		column = newColumn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProtocolPackage.APPLY_CELL_EDITOR__COLUMN, oldColumn, column));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT:
				return basicSetElement(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ProtocolPackage.APPLY_CELL_EDITOR__ID:
				return getId();
			case ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT:
				return getElement();
			case ProtocolPackage.APPLY_CELL_EDITOR__DEACTIVATE:
				return isDeactivate();
			case ProtocolPackage.APPLY_CELL_EDITOR__COLUMN:
				return getColumn();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ProtocolPackage.APPLY_CELL_EDITOR__ID:
				setId((Integer)newValue);
				return;
			case ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT:
				setElement((Element)newValue);
				return;
			case ProtocolPackage.APPLY_CELL_EDITOR__DEACTIVATE:
				setDeactivate((Boolean)newValue);
				return;
			case ProtocolPackage.APPLY_CELL_EDITOR__COLUMN:
				setColumn((Integer)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ProtocolPackage.APPLY_CELL_EDITOR__ID:
				setId(ID_EDEFAULT);
				return;
			case ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT:
				setElement((Element)null);
				return;
			case ProtocolPackage.APPLY_CELL_EDITOR__DEACTIVATE:
				setDeactivate(DEACTIVATE_EDEFAULT);
				return;
			case ProtocolPackage.APPLY_CELL_EDITOR__COLUMN:
				setColumn(COLUMN_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ProtocolPackage.APPLY_CELL_EDITOR__ID:
				return id != ID_EDEFAULT;
			case ProtocolPackage.APPLY_CELL_EDITOR__ELEMENT:
				return element != null;
			case ProtocolPackage.APPLY_CELL_EDITOR__DEACTIVATE:
				return deactivate != DEACTIVATE_EDEFAULT;
			case ProtocolPackage.APPLY_CELL_EDITOR__COLUMN:
				return column != COLUMN_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (id: ");
		result.append(id);
		result.append(", deactivate: ");
		result.append(deactivate);
		result.append(", column: ");
		result.append(column);
		result.append(')');
		return result.toString();
	}

} //ApplyCellEditorImpl
