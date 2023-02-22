package com.github.alyssaburlton.swingtest

import io.kotest.matchers.shouldBe
import io.mockk.MockKMatcherScope
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import java.awt.Component
import java.awt.event.*
import javax.swing.*

class ComponentInteractionsTest {
    @Test
    fun `doClick should call mouseClicked and mouseReleased in order, on all listeners`() {
        val listenerOne = mockk<MouseListener>(relaxed = true)
        val listenerTwo = mockk<MouseListener>(relaxed = true)

        val label = JLabel()
        label.addMouseListener(listenerOne)
        label.addMouseListener(listenerTwo)

        label.doClick()

        verifySequence {
            listenerOne.mouseClicked(any())
            listenerOne.mouseReleased(any())
        }

        verifySequence {
            listenerTwo.mouseClicked(any())
            listenerTwo.mouseReleased(any())
        }
    }

    @Test
    fun `Should simulate a double click`() {
        val label = JLabel()
        val listener = mockk<MouseListener>(relaxed = true)

        label.addMouseListener(listener)
        label.doubleClick()

        verify { listener.mouseClicked(eventWithClickCount(2)) }
        verify { listener.mouseReleased(eventWithClickCount(2)) }
    }

    @Test
    fun `Should call mouseEntered`() {
        val label = JLabel()
        val listener = mockk<MouseListener>(relaxed = true)

        label.addMouseListener(listener)
        label.doHover()

        verify { listener.mouseEntered(any()) }
    }

    @Test
    fun `Should call mouseMoved`() {
        val label = JLabel()
        val listener = mockk<MouseMotionListener>(relaxed = true)

        label.addMouseMotionListener(listener)
        label.doMouseMove()

        verify { listener.mouseMoved(any()) }
    }

    @Test
    fun `Should call mouseExited`() {
        val label = JLabel()
        val listener = mockk<MouseListener>(relaxed = true)

        label.addMouseListener(listener)
        label.doHoverAway()

        verify { listener.mouseExited(any()) }
    }

    @Test
    fun `Should process a particular key event`() {
        val fn = mockk<() -> Unit>(relaxed = true)
        val action = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                fn()
            }
        }

        val key = KeyEvent.VK_ENTER
        val table = JTable()
        table.inputMap.put(KeyStroke.getKeyStroke(key, JComponent.WHEN_FOCUSED), "my_action")
        table.actionMap.put("my_action", action)

        table.processKeyPress(KeyEvent.VK_SPACE)
        verify(exactly = 0) { fn() }

        table.processKeyPress(KeyEvent.VK_ENTER)
        verify { fn() }
    }

    @Test
    fun `Should gain focus`() {
        val focusListener = mockk<FocusListener>(relaxed = true)
        val label = JLabel()
        label.addFocusListener(focusListener)

        label.doGainFocus()
        verify { focusListener.focusGained(focusEvent(label, FocusEvent.FOCUS_GAINED)) }
    }

    @Test
    fun `Should lose focus`() {
        val focusListener = mockk<FocusListener>(relaxed = true)
        val label = JLabel()
        label.addFocusListener(focusListener)

        label.doLoseFocus()
        verify { focusListener.focusLost(focusEvent(label, FocusEvent.FOCUS_LOST)) }
    }

    @Test
    fun `Should do nothing if checkbox is already unchecked`() {
        val checkbox = JCheckBox()
        val listener = mockk<ActionListener>(relaxed = true)
        checkbox.addActionListener(listener)

        checkbox.isSelected shouldBe false
        checkbox.uncheck()
        checkbox.isSelected shouldBe false

        verifyNotCalled {
            listener.actionPerformed(any())
        }
    }

    @Test
    fun `Should do nothing if checkbox is already checked`() {
        val checkbox = JCheckBox()
        checkbox.isSelected = true

        val listener = mockk<ActionListener>(relaxed = true)
        checkbox.addActionListener(listener)

        checkbox.isSelected shouldBe true
        checkbox.check()
        checkbox.isSelected shouldBe true

        verifyNotCalled {
            listener.actionPerformed(any())
        }
    }

    @Test
    fun `Should check an unchecked checkbox`() {
        val checkbox = JCheckBox()

        val listener = mockk<ActionListener>(relaxed = true)
        checkbox.addActionListener(listener)

        checkbox.isSelected shouldBe false
        checkbox.check()
        checkbox.isSelected shouldBe true

        verify {
            listener.actionPerformed(any())
        }
    }

    @Test
    fun `Should uncheck a checked checkbox`() {
        val checkbox = JCheckBox()
        checkbox.isSelected = true

        val listener = mockk<ActionListener>(relaxed = true)
        checkbox.addActionListener(listener)

        checkbox.uncheck()
        checkbox.isSelected shouldBe false

        verify {
            listener.actionPerformed(any())
        }
    }

    private fun MockKMatcherScope.eventWithClickCount(count: Int) = match<MouseEvent> {
        it.clickCount == count
    }

    private fun MockKMatcherScope.focusEvent(component: Component, id: Int) = match<FocusEvent> {
        it.id == id && it.component == component
    }
}
