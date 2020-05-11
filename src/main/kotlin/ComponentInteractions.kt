import java.awt.Component
import java.awt.event.FocusEvent
import javax.swing.JComponent
import javax.swing.KeyStroke

@JvmOverloads
fun Component.doClick(x: Int = 0, y: Int = 0) {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseListeners.forEach {
        it.mouseClicked(me)
        it.mouseReleased(me)
    }
}

@JvmOverloads
fun Component.doubleClick(x: Int = 0, y: Int = 0) {
    val me = makeMouseEvent(this, x = x, y = y, clickCount = 2)
    mouseListeners.forEach {
        it.mouseClicked(me)
        it.mouseReleased(me)
    }
}

@JvmOverloads
fun Component.doHover(x: Int = 0, y: Int = 0) {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseListeners.forEach { it.mouseEntered(me) }
}

@JvmOverloads
fun Component.doHoverAway(x: Int = 0, y: Int = 0) {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseListeners.forEach { it.mouseExited(me) }
}

fun Component.doMouseMove() {
    val me = makeMouseEvent(this, x = x, y = y)
    mouseMotionListeners.forEach { it.mouseMoved(me) }
}

fun JComponent.processKeyPress(key: Int) {
    val actionName = inputMap[KeyStroke.getKeyStroke(key, JComponent.WHEN_FOCUSED)]
    if (!actionMap.keys().contains(actionName)) {
        return
    }

    val action = actionMap[actionName]
    action.actionPerformed(makeActionEvent(this))
}

fun JComponent.doLoseFocus() {
    focusListeners.forEach { it.focusLost(FocusEvent(this, FocusEvent.FOCUS_LOST)) }
}

fun JComponent.doGainFocus() {
    focusListeners.forEach { it.focusGained(FocusEvent(this, FocusEvent.FOCUS_GAINED)) }
}