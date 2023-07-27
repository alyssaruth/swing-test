package com.github.alyssaburlton.swingtest

import java.awt.Component
import java.awt.Container
import java.awt.Window

/**
 * Finds a window of a given type, optionally matching a predicate.
 *
 * @param W: The class of window to find
 * @param predicate: A predicate to match against the window
 */
inline fun <reified W : Window> findWindow(noinline predicate: (window: W) -> Boolean = { true }): W? =
    findWindow(W::class.java, predicate)

/**
 * Finds a window of a given type, optionally matching a predicate.
 * Non reified version for calling from Java.
 *
 * @param clazz: The class of window to find
 * @param predicate: A predicate to match against the window
 */
@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun <W : Window> findWindow(clazz: Class<W>, predicate: (window: W) -> Boolean = { true }): W? {
    val filtered = Window.getWindows().filter { clazz.isInstance(it) && predicate(it as W) } as List<W>

    if (filtered.size > 1) {
        val trees = filtered.joinToString("------\n") { it.generateComponentTree() }

        throw MultipleWindowsException(
            "Found ${filtered.size} ${clazz.simpleName}s, expected 1 or 0:\n\n$trees"
        )
    }

    return filtered.firstOrNull()
}

class MultipleWindowsException(message: String) : Exception(message)


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

class MultipleComponentsException(container: Container, message: String) :
    Exception("$message. \n\nComponent tree:\n\n${container.generateComponentTree()}")
class NoSuchComponentException(container: Container, message: String) :
    Exception("$message. \n\nComponent tree:\n\n${container.generateComponentTree()}")

/**
 * Find a single child component, recursing through child containers.
 *
 * @return the found component, or null if no match is found.
 *
 * @param T: The class of component to look for
 * @param name: If non-null, filter to components with this name
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text is specified for a component type that does not have it
 */
inline fun <reified T : Component> Container.findChild(
    name: String? = null,
    text: String? = null,
    noinline filterFn: ((T) -> Boolean)? = null,
): T? = findChild(T::class.java, name, text, filterFn)

/**
 * Find a single child component, recursing through child containers.
 * Non reified version for calling from Java.
 *
 * @return the found component, or null if no match is found.
 *
 * @param clazz: The class of component to look for
 * @param name: If non-null, filter to components with this name
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text is specified for a component type that does not have them
 */
@JvmOverloads
fun <T : Component> Container.findChild(
    clazz: Class<T>,
    name: String? = null,
    text: String? = null,
    filterFn: ((T) -> Boolean)? = null,
): T? {
    val allComponents = findAll(clazz)

    var filtered = filterByText(clazz, allComponents, text)
    filtered = filtered.filter { name == null || it.name == name }
    filterFn?.let { filtered = filtered.filter { filterFn(it) } }

    if (filtered.size > 1) {
        throw MultipleComponentsException(
            this,
            "Found ${filtered.size} ${clazz.simpleName}s, expected 1 or 0. name [$name], Text [$text]"
        )
    }

    return filtered.firstOrNull()
}

private fun <T : Component> filterByText(
    clazz: Class<T>,
    components: List<T>,
    match: String?,
): List<T> {
    match ?: return components

    val getter = clazz.getMethod("getText")
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
 * @param name: If non-null, filter to components with a name set to the specified String
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text is specified for a component type that does not have them
 */
inline fun <reified T : Component> Container.getChild(
    name: String? = null,
    text: String? = null,
    noinline filterFn: ((T) -> Boolean)? = null,
): T = getChild(T::class.java, name, text, filterFn)

/**
 * Get a single child component, recursing through child containers.
 * Non reified version for calling from Java.
 *
 * @return the found component
 *
 * @param clazz: The class of component to look for
 * @param name: If non-null, filter to components with a name set to the specified String
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param filterFn: Lambda argument to allow custom additional filters to be imposed
 *
 * @throws NoSuchComponentException if no matching component is found
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
@JvmOverloads
fun <T : Component> Container.getChild(
    clazz: Class<T>,
    name: String? = null,
    text: String? = null,
    filterFn: ((T) -> Boolean)? = null,
): T = findChild(clazz, name, text, filterFn)
    ?: throw NoSuchComponentException(this, "Found 0 ${clazz.simpleName}s. Text [$text], name [$name]")
