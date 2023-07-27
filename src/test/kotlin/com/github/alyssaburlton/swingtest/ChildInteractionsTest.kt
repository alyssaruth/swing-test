package com.github.alyssaburlton.swingtest

import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JPanel

class ChildInteractionsTest {
    @Test
    fun `Should support common button texts, ignoring case`() {
        val panel = JPanel()
        val okListener = addButtonWithListener(panel, "OK")
        val cancelListener = addButtonWithListener(panel, "Cancel")
        val yesListener = addButtonWithListener(panel, "yes")
        val noListener = addButtonWithListener(panel, "nO")

        panel.clickOk()
        panel.clickYes()
        panel.clickCancel()
        panel.clickNo()

        verifySequence {
            okListener.actionPerformed(any())
            yesListener.actionPerformed(any())
            cancelListener.actionPerformed(any())
            noListener.actionPerformed(any())
        }
    }

    private fun addButtonWithListener(panel: JPanel, text: String): ActionListener {
        val button = JButton(text)
        val listener = mockk<ActionListener>(relaxed = true)
        button.addActionListener(listener)
        panel.add(button)
        return listener
    }

    @Test
    fun `Should click a child button and trigger its ActionListeners`() {
        val panel = JPanel()
        val buttonA = JButton("A")
        val buttonB = JButton("B")
        val listenerA = mockk<ActionListener>(relaxed = true)
        val listenerB = mockk<ActionListener>(relaxed = true)

        buttonA.addActionListener(listenerA)
        buttonB.addActionListener(listenerB)
        panel.add(buttonA)
        panel.add(buttonB)

        panel.clickChild<JButton>(text = "A")

        verify { listenerA.actionPerformed(any()) }
        verifyNotCalled { listenerB.actionPerformed(any()) }
    }
}
