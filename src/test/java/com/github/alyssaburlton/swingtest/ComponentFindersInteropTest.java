package com.github.alyssaburlton.swingtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

public class ComponentFindersInteropTest {
    @Test
    public void findAll() {
        JPanel panel = new JPanel();
        JButton buttonOne = new JButton();
        JButton buttonTwo = new JButton();
        panel.add(buttonOne);
        panel.add(buttonTwo);

        List<JButton> buttons = ComponentFindersKt.findAll(panel, JButton.class);
        Assertions.assertEquals(buttons.get(0), buttonOne);
        Assertions.assertEquals(buttons.get(1), buttonTwo);
    }

    @Test
    public void testIds() {
        JPanel panel = new JPanel();
        JButton buttonOne = new JButton();
        buttonOne.setName("ButtonOne");
        JButton buttonTwo = new JButton();
        buttonTwo.setName("ButtonTwo");
        panel.add(buttonOne);
        panel.add(buttonTwo);

        JButton result = ComponentFindersKt.findChild(panel, JButton.class, "ButtonOne");

        Assertions.assertEquals(result, buttonOne);
    }
}
