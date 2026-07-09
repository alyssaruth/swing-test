package io.github.alyssaruth.swingtest

import java.awt.Component
import java.awt.Container
import javax.swing.AbstractButton
import javax.swing.JButton
import javax.swing.JTabbedPane

fun Container.clickOk(async: Boolean = ASYNC_BY_DEFAULT) = clickCommonButton("ok", async)
fun Container.clickCancel(async: Boolean = ASYNC_BY_DEFAULT) = clickCommonButton("cancel", async)
fun Container.clickYes(async: Boolean = ASYNC_BY_DEFAULT) = clickCommonButton("yes", async)
fun Container.clickNo(async: Boolean = ASYNC_BY_DEFAULT) = clickCommonButton("no", async)

private fun Container.clickCommonButton(text: String, async: Boolean) =
    clickChild<AbstractButton>(async = async, text = text)

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
    async: Boolean = ASYNC_BY_DEFAULT,
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
    async: Boolean = ASYNC_BY_DEFAULT,
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
    async: Boolean = ASYNC_BY_DEFAULT,
    filterFn: ((JButton) -> Boolean)? = null,
) {
    clickChild<JButton>(name, text, async, filterFn)
}


inline fun <reified T : Component> JTabbedPane.selectTab(
    name: String,
    async: Boolean = ASYNC_BY_DEFAULT,
    noinline filterFn: ((T) -> Boolean)? = null,
) {
    maybeAsync(async) { selectedComponent = getChild<T>(name, filterFn = filterFn) }
}