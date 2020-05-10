import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import javax.swing.JButton

fun makeMouseEvent(
    clickCount: Int = 1,
    x: Int = 0,
    y: Int = 0,
    component: Component = JButton()
): MouseEvent = MouseEvent(component, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), -1, x, y, clickCount, false)

fun makeActionEvent(component: Component, id: Int = 0, action: String? = null): ActionEvent =
    ActionEvent(component, id, action)
