package com.github.alexburlton.swingtest;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(buttons.get(0), buttonOne);
        Assert.assertEquals(buttons.get(1), buttonTwo);
    }
}
