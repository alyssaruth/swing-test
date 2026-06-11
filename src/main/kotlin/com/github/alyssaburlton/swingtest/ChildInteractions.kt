package com.github.alyssaburlton.swingtest

import java.awt.Container
import javax.swing.AbstractButton
import javax.swing.JButton

fun Container.clickOk(async: Boolean = true) = clickCommonButton("ok", async)
fun Container.clickCancel(async: Boolean = true) = clickCommonButton("cancel", async)
fun Container.clickYes(async: Boolean = true) = clickCommonButton("yes", async)
fun Container.clickNo(async: Boolean = true) = clickCommonButton("no", async)

private fun Container.clickCommonButton(text: String, async: Boolean) =
    clickChild<AbstractButton>(async = async) {
        it.text.equals(text, ignoreCase = true)
    }

/**
 * Simulate a click on a child component, recursing through child containers.
 *
 * @param T: The class of component to look for
 * @param name: If non-null, filter to components with a name set to the specified String
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param async: If true, clicks the component via an invokeLater (to avoid blocking) and then flushes the EDT queue
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
inline fun <reified T : AbstractButton> Container.clickChild(
    name: String? = null,
    text: String? = null,
    async: Boolean = true,
    noinline filterFn: ((T) -> Boolean)? = null,
) {
    clickChild(T::class.java, name, text, async, filterFn)
}

/**
 * Simulate a click on a child component, recursing through child containers.
 * Non reified version for calling from Java.
 *
 * @param clazz: The class of component to look for
 * @param name: If non-null, filter to components with name set to the specified String
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param async: If true, clicks the component via an invokeLater (to avoid blocking) and then flushes the EDT queue
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
@JvmOverloads
fun <T : AbstractButton> Container.clickChild(
    clazz: Class<T>,
    name: String? = null,
    text: String? = null,
    async: Boolean = true,
    filterFn: ((T) -> Boolean)? = null,
) {
    val child = getChild(clazz, name, text, filterFn)
    maybeAsync(async) {
        child.doClick()
    }
}

/**
 * Specific overload for buttons
 */
fun Container.clickButton(
    name: String? = null,
    text: String? = null,
    async: Boolean = true,
    filterFn: ((JButton) -> Boolean)? = null,
) {
    clickChild<JButton>(name, text, async, filterFn)
}
