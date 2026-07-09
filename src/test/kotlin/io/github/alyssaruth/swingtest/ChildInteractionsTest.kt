package io.github.alyssaruth.swingtest

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTabbedPane

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

    @Test
    fun `Should be able to select a tab by name`() {
        val pane = JTabbedPane()
        val panelA = JPanel().apply { name = "panelA" }
        val panelB = JPanel().apply { name = "panelB" }
        val panelC = JPanel().apply { name = "panelC" }

        pane.addTab("A", panelA)
        pane.addTab("B", panelB)
        pane.addTab("C", panelC)

        pane.selectedComponent shouldBe panelA


        pane.selectTab<JPanel>("panelC")
        pane.selectedComponent shouldBe panelC
    }
}
