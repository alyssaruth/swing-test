package com.github.alyssaburlton.swingtest

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.Scrollbar
import javax.swing.AbstractButton
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollBar
import javax.swing.JTable

fun Container.generateComponentTree() = generateComponentTree("", "", "")

private fun Container.generateComponentTree(prefix: String, constraintDesc: String, treeSoFar: String): String {
    var newTree = treeSoFar
    val children = components
    val borderLayout = layout as? BorderLayout
    newTree += "$prefix$constraintDesc${this.oneLineDescription()}\n"
    children.forEach { child ->
        val newPrefix = " ".repeat(prefix.length)
        val childConstraint = borderLayout?.getConstraints(child)?.let { " [$it] " } ?: ""
        if (child is Container && !child.isBoringContainer()) {
            newTree = child.generateComponentTree("$newPrefix|-", childConstraint.ifEmpty { " " }, newTree)
        } else {
            newTree += "$newPrefix|-$childConstraint ${child.oneLineDescription()}\n"
        }
    }

    return newTree
}

private fun Component.isBoringContainer() = this is JScrollBar || this is JTable || this is JComboBox<*>
private fun Component.oneLineDescription(): String {
    val className = describeClass()

    val desc = when (this) {
        is AbstractButton -> {
            val tooltipInfo = (toolTipText ?: "").ifNotEmpty { """ (ToolTip: "$it")""" }
            """$className - "$text"$tooltipInfo"""
        }
        is JLabel -> """$className - "$text""""
        is JFrame -> """$className - "$title" - ${layout?.describeClass()}"""
        is JDialog -> """$className - "$title" - ${layout?.describeClass()}"""
        is JComboBox<*> -> {
            val item = if (itemCount > 0) getItemAt(0) else null
            val itemType = item?.describeClass() ?: "*"
            val selection = selectedItem?.toString()
            "JComboBox<$itemType> - $itemCount items (Selected: $selection)"
        }
        is JScrollBar -> {
            val orientationString = if (this.orientation == Scrollbar.HORIZONTAL) "HORIZONTAL" else "VERTICAL"
            "$className - $orientationString"
        }
        is JTable -> {
            val columns = columnModel.columns.toList().map { it.headerValue }.joinToString()
            "$className - [$columns] - $rowCount rows"
        }
        is JComponent -> {
            val toolTipDesc = toolTipText?.let { """ - "$it"""" } ?: ""
            """$className$toolTipDesc - ${layout?.describeClass()}"""
        }
        is Container -> "$className - ${layout?.describeClass()}"
        else -> className
    }

    return if (name != null) {
        "$desc [name: $name]"
    } else {
        desc
    }
}

private fun Any.describeClass() = javaClass.simpleName.ifEmpty { javaClass.name }

private fun String.ifNotEmpty(transformer: (String) -> String): String =
    if (isNotEmpty()) transformer(this) else this
