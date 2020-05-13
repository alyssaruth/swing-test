package com.github.alexburlton.swingtest

import java.awt.Component
import java.awt.Container
import javax.swing.AbstractButton

/**
 * Finds all child components of a given type, recursing through child containers.
 *
 * @param T: The class of components to return
 */
inline fun <reified T> Container.findAll(): List<T> = findAll(T::class.java)

/**
 * Finds all child components of a given type, recursing through child containers.
 * Non reified version for calling from Java.
 *
 * @param clazz: The class of components to return
 */
fun <T> Container.findAll(clazz: Class<T>): List<T> {
    val ret = mutableListOf<T>()
    addComponents(ret, components, clazz)
    return ret
}

@Suppress("UNCHECKED_CAST")
private fun <T> addComponents(ret: MutableList<T>, components: Array<Component>, desiredClazz: Class<T>) {
    for (comp in components) {
        if (desiredClazz.isInstance(comp)) {
            ret.add(comp as T)
        }

        if (comp is Container) {
            val subComponents = comp.components
            addComponents(ret, subComponents, desiredClazz)
        }
    }
}

class MultipleComponentsException(override val message: String) : Exception(message)
class NoSuchComponentException(override val message: String) : Exception(message)

/**
 * Find a single child component, recursing through child containers.
 *
 * @return the found component, or null if no match is found.
 *
 * @param T: The class of component to look for
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param toolTipText: If non-null, filter to components with a toolTipText field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
inline fun <reified T : Component> Container.findChild(
    text: String? = null,
    toolTipText: String? = null,
    noinline filterFn: ((T) -> Boolean)? = null
): T? = findChild(T::class.java, text, toolTipText, filterFn)

/**
 * Find a single child component, recursing through child containers.
 * Non reified version for calling from Java.
 *
 * @return the found component, or null if no match is found.
 *
 * @param clazz: The class of component to look for
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param toolTipText: If non-null, filter to components with a toolTipText field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
@JvmOverloads
fun <T : Component> Container.findChild(
    clazz: Class<T>, text: String? = null,
    toolTipText: String? = null,
    filterFn: ((T) -> Boolean)? = null
): T? {
    val allComponents = findAll(clazz)

    var filtered = filterByField(clazz, allComponents, "Text", text)
    filtered = filterByField(clazz, filtered, "ToolTipText", toolTipText)
    filterFn?.let { filtered = filtered.filter { filterFn(it) } }

    if (filtered.size > 1) {
        throw MultipleComponentsException("Found ${filtered.size} ${clazz.simpleName}s, expected 1 or 0. Text [$text], ToolTipText [$toolTipText]")
    }

    return filtered.firstOrNull()
}

private fun <T : Component> filterByField(
    clazz: Class<T>,
    components: List<T>,
    fieldName: String,
    match: String?
): List<T> {
    match ?: return components

    val getter = clazz.getMethod("get$fieldName")
    return components.filter {
        val result = getter.invoke(it)
        "$result" == match
    }
}

/**
 * Get a single child component, recursing through child containers.
 *
 * @return the found component
 *
 * @param T: The class of component to look for
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param toolTipText: If non-null, filter to components with a toolTipText field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
inline fun <reified T : Component> Container.getChild(
    text: String? = null,
    toolTipText: String? = null,
    noinline filterFn: ((T) -> Boolean)? = null
): T = getChild(T::class.java, text, toolTipText, filterFn)

/**
 * Get a single child component, recursing through child containers.
 * Non reified version for calling from Java.
 *
 * @return the found component
 *
 * @param clazz: The class of component to look for
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param toolTipText: If non-null, filter to components with a toolTipText field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
@JvmOverloads
fun <T : Component> Container.getChild(
    clazz: Class<T>,
    text: String? = null,
    toolTipText: String? = null,
    filterFn: ((T) -> Boolean)? = null
): T = findChild(clazz, text, toolTipText, filterFn)
    ?: throw NoSuchComponentException("Found 0 ${clazz.simpleName}s. Text [$text], ToolTipText [$toolTipText]")

/**
 * Simulate a click on a child component, recursing through child containers.
 *
 * @param T: The class of component to look for
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param toolTipText: If non-null, filter to components with a toolTipText field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
inline fun <reified T : AbstractButton> Container.clickChild(
    text: String? = null,
    toolTipText: String? = null,
    noinline filterFn: ((T) -> Boolean)? = null
) {
    clickChild(T::class.java, text, toolTipText, filterFn)
}

/**
 * Simulate a click on a child component, recursing through child containers.
 * Non reified version for calling from Java.
 *
 * @param clazz: The class of component to look for
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param toolTipText: If non-null, filter to components with a toolTipText field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
@JvmOverloads
fun <T : AbstractButton> Container.clickChild(
    clazz: Class<T>,
    text: String? = null,
    toolTipText: String? = null,
    filterFn: ((T) -> Boolean)? = null
) {
    getChild(clazz, text, toolTipText, filterFn).doClick()
}