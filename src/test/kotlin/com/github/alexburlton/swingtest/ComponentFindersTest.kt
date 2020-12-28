package com.github.alexburlton.swingtest

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.awt.event.ActionListener
import javax.swing.*

class ComponentFindersTest {
    @Test
    fun `Should find all components of a given type`() {
        val panel = JPanel()
        val buttonA = JButton("A")
        val buttonB = JButton("B")
        val radioButton = JRadioButton("C")

        panel.add(buttonA)
        panel.add(buttonB)
        panel.add(radioButton)

        panel.findAll<JButton>().shouldContainExactly(buttonA, buttonB)
        panel.findAll<JRadioButton>().shouldContainExactly(radioButton)
        panel.findAll<AbstractButton>().shouldContainExactly(buttonA, buttonB, radioButton)
    }

    @Test
    fun `Should recurse through containers to find components`() {
        val frame = JFrame()
        val panel = JPanel()
        frame.contentPane.add(panel)

        val buttonA = JButton("A")
        panel.add(buttonA)

        val innerPanel = JPanel()
        panel.add(innerPanel)

        val buttonB = JButton("B")
        innerPanel.add(buttonB)

        frame.findAll<JButton>().shouldContainExactly(buttonA, buttonB)
    }

    @Test
    fun `Should throw MultipleComponentsException if more than one component meets the criteria`() {
        val panel = JPanel()
        val buttonA = JButton("A")
        val buttonB = JButton("B")

        panel.add(buttonA)
        panel.add(buttonB)

        val e = shouldThrow<MultipleComponentsException> {
            panel.findChild<JButton>()
        }

        e.message shouldBe "Found 2 JButtons, expected 1 or 0. Text [null], ToolTipText [null]"
    }

    @Test
    fun `MultipleComponentsException should include text and toolTipText`() {
        val panel = JPanel()
        val buttonA = JButton("Button")
        buttonA.toolTipText = "Click me"

        val buttonB = JButton("Button")
        buttonB.toolTipText = "Click me"

        panel.add(buttonA)
        panel.add(buttonB)

        val e = shouldThrow<MultipleComponentsException> {
            panel.findChild<JButton>(text = "Button", toolTipText = "Click me")
        }

        e.message shouldBe "Found 2 JButtons, expected 1 or 0. Text [Button], ToolTipText [Click me]"
    }

    @Test
    fun `Should throw a NoSuchMethodException if specifying text for a component type that does not have it`() {
        val panel = JPanel()

        val e = shouldThrow<NoSuchMethodException> {
            panel.findChild<JPanel>(text = "Foo")
        }

        e.message shouldBe "javax.swing.JPanel.getText()"
    }

    @Test
    fun `Should filter by text correctly`() {
        val panel = JPanel()
        val buttonA = JButton("Foo")
        val buttonB = JButton("Bar")
        panel.add(buttonA)
        panel.add(buttonB)

        panel.findChild<JButton>(text = "Foo") shouldBe buttonA
        panel.findChild<JButton>(text = "Bar") shouldBe buttonB
        panel.findChild<JButton>(text = "Baz") shouldBe null
    }

    @Test
    fun `Should filter by toolTipText correctly`() {
        val panel = JPanel()
        val labelA = JLabel("")
        val labelB = JLabel("")
        panel.add(labelA)
        panel.add(labelB)

        labelA.toolTipText = "Label 1"
        labelB.toolTipText = "Label 2"

        panel.findChild<JLabel>(toolTipText = "Label 1") shouldBe labelA
        panel.findChild<JLabel>(toolTipText = "Label 2") shouldBe labelB
        panel.findChild<JLabel>(toolTipText = "zz") shouldBe null
    }

    @Test
    fun `Should support custom filters`() {
        val panel = JPanel()
        val buttonA = JButton()
        val buttonB = JButton()
        panel.add(buttonA)
        panel.add(buttonB)

        buttonA.isEnabled = true
        buttonB.isEnabled = false

        panel.findChild<JButton> { it.isEnabled } shouldBe buttonA
        panel.findChild<JButton> { !it.isEnabled } shouldBe buttonB
    }

    @Test
    fun `Should apply all filters together to find the right component`() {
        val panel = JPanel()

        val expected = JButton("Button Text")
        expected.toolTipText = "Yes"

        val wrongType = JRadioButton("Button Text")
        wrongType.toolTipText = "Yes"

        val wrongText = JButton("Other Text")
        wrongText.toolTipText = "Yes"

        val wrongToolTip = JButton("Button Text")
        wrongToolTip.toolTipText = "No"

        val disabled = JButton("Button Text")
        disabled.toolTipText = "Yes"
        disabled.isEnabled = false

        panel.add(expected)
        panel.add(wrongType)
        panel.add(wrongText)
        panel.add(wrongToolTip)
        panel.add(disabled)

        @Suppress("unused")
        panel.findChild<JButton>(text = "Button Text", toolTipText = "Yes") { it.isEnabled } shouldBe expected
    }

    @Test
    fun `Should handle an absent child component correctly`() {
        val panel = JPanel()
        panel.findChild<JButton>().shouldBeNull()

        shouldThrow<NoSuchComponentException> {
            panel.getChild<JButton>()
        }
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

        panel.clickChild<JButton>("A")

        verify { listenerA.actionPerformed(any()) }
        verifyNotCalled { listenerB.actionPerformed(any()) }
    }
}