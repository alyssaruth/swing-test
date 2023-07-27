package com.github.alyssaburlton.swingtest

import java.awt.Component
import java.awt.event.FocusEvent
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.text.JTextComponent

fun maybeAsync(async: Boolean, interaction: () -> Unit) {
    if (async) {
        SwingUtilities.invokeLater(interaction)
        flushEdt()
    } else {
        SwingUtilities.invokeAndWait(interaction)
    }
}

@JvmOverloads
fun Component.doClick(x: Int = 0, y: Int = 0, async: Boolean = false) = maybeAsync(async) {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseListeners.forEach {
        it.mouseClicked(me)
        it.mouseReleased(me)
    }
}

@JvmOverloads
fun Component.doubleClick(x: Int = 0, y: Int = 0, async: Boolean = false) = maybeAsync(async) {
    val me = makeMouseEvent(this, x = x, y = y, clickCount = 2)
    mouseListeners.forEach {
        it.mouseClicked(me)
        it.mouseReleased(me)
    }
}

@JvmOverloads
fun Component.doHover(x: Int = 0, y: Int = 0, async: Boolean = false) = maybeAsync(async) {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseListeners.forEach { it.mouseEntered(me) }
}

@JvmOverloads
fun Component.doHoverAway(x: Int = 0, y: Int = 0, async: Boolean = false) = maybeAsync(async) {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseListeners.forEach { it.mouseExited(me) }
}

@JvmOverloads
fun Component.doMouseMove(async: Boolean = false) = maybeAsync(async) {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseMotionListeners.forEach { it.mouseMoved(me) }
}

@JvmOverloads
fun JComponent.processKeyPress(key: Int, async: Boolean = false) = maybeAsync(async) {
    val actionName = inputMap[KeyStroke.getKeyStroke(key, JComponent.WHEN_FOCUSED)]
    if (!actionMap.keys().contains(actionName)) {
        return@maybeAsync
    }

    val action = actionMap[actionName]
    action.actionPerformed(makeActionEvent(this))
}

@JvmOverloads
fun JComponent.doLoseFocus(async: Boolean = false) = maybeAsync(async) {
    focusListeners.forEach { it.focusLost(FocusEvent(this, FocusEvent.FOCUS_LOST)) }
}

@JvmOverloads
fun JComponent.doGainFocus(async: Boolean = false) = maybeAsync(async) {
    focusListeners.forEach { it.focusGained(FocusEvent(this, FocusEvent.FOCUS_GAINED)) }
}

@JvmOverloads
fun JCheckBox.check(async: Boolean = false) = maybeAsync(async) {
    if (!isSelected) {
        doClick()
    }
}

@JvmOverloads
fun JCheckBox.uncheck(async: Boolean = false) = maybeAsync(async) {
    if (isSelected) {
        doClick()
    }
}

@JvmOverloads
fun JTextComponent.typeText(newText: String, async: Boolean = false) = maybeAsync(async) {
    text = newText
}
