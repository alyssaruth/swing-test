import java.awt.Component
import java.awt.Container
import javax.swing.JComponent

inline fun <reified T> Container.findAll(): List<T> {
    val ret = mutableListOf<T>()

    val components = components
    addComponents(ret, components, T::class.java)

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

inline fun <reified T : JComponent> Container.find(text: String? = null, toolTipText: String? = null): T? {
    val allComponents = findAll<T>()

    var filtered = filterByField(allComponents, "Text", text)
    filtered = filterByField(filtered, "ToolTipText", toolTipText)

    if (filtered.size > 1) {
        throw Exception("Non-unique class - ${allComponents.size} ${T::class.simpleName}s found")
    }

    return allComponents.firstOrNull()
}

inline fun <reified T : JComponent> filterByField(components: List<T>, fieldName: String, match: String?): List<T> {
    match ?: return components

    val instanceClass = T::class.java
    val getter = instanceClass.getMethod("get$fieldName")
    return components.filter {
        val result = getter.invoke(it)
        if (result !is String) {
            throw Exception("wah")
        }

        result.contains(match, ignoreCase = true)
    }
}