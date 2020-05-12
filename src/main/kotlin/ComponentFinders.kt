import java.awt.Component
import java.awt.Container

inline fun <reified T> Container.findAll(): List<T>  = findAll(T::class.java)

fun <T> Container.findAll(clazz: Class<T>): List<T> {
    val ret = mutableListOf<T>()

    val components = components
    addComponents(ret, components, clazz)

    return ret
}

@Suppress("UNCHECKED_CAST")
fun <T> addComponents(ret: MutableList<T>, components: Array<Component>, desiredClazz: Class<T>) {
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

class MultipleComponentsException(override val message: String?) : Exception(message)

/**
 * @param text: If non-null, filter to components with a text field containing the specified String
 * @param toolTipText: If non-null, filter to components with a toolTipText field containing the specified String
 *
 * @throws MultipleComponentsException if more than one component is found
 * @throws NoSuchMethodException if text or toolTipText are specified for a component type that does not have them
 */
@JvmOverloads
inline fun <reified T : Component> Container.find(
    text: String? = null,
    toolTipText: String? = null,
    noinline filterFn: ((T) -> Boolean)? = null
): T? {
    val allComponents = findAll<T>()

    var filtered = filterByField(allComponents, "Text", text)
    filtered = filterByField(filtered, "ToolTipText", toolTipText)
    filterFn?.let { filtered = filtered.filter { filterFn(it) } }

    if (filtered.size > 1) {
        throw MultipleComponentsException("Found ${filtered.size} ${T::class.simpleName}s, expected 1 or 0. Text [$text], ToolTipText [$toolTipText]")
    }

    return filtered.firstOrNull()
}

inline fun <reified T : Component> filterByField(components: List<T>, fieldName: String, match: String?): List<T> {
    match ?: return components

    val instanceClass = T::class.java
    val getter = instanceClass.getMethod("get$fieldName")
    return components.filter {
        val result = getter.invoke(it)
        if (result !is String) {
            throw NoSuchMethodException("$instanceClass.get$fieldName exists, but has non-String return type: ${getter.returnType}")
        }

        result == match
    }
}