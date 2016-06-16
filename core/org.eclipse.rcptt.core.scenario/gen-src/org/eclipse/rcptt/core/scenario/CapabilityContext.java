/**
 */
package org.eclipse.rcptt.core.scenario;

import java.util.Map;
import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Capability Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.rcptt.core.scenario.CapabilityContext#getItems <em>Items</em>}</li>
 * </ul>
 *
 * @see org.eclipse.rcptt.core.scenario.ScenarioPackage#getCapabilityContext()
 * @model
 * @generated
 */
public interface CapabilityContext extends Context {

	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.rcptt.core.scenario.CapabilityContextItem}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Items</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Items</em>' containment reference list.
	 * @see org.eclipse.rcptt.core.scenario.ScenarioPackage#getCapabilityContext_Items()
	 * @model containment="true"
	 * @generated
	 */
	EList<CapabilityContextItem> getItems();
} // CapabilityContext
