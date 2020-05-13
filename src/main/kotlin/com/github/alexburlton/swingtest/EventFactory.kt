package com.github.alexburlton.swingtest

import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent

fun makeMouseEvent(
    component: Component,
    clickCount: Int = 1,
    x: Int = 0,
    y: Int = 0
): MouseEvent = MouseEvent(component, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), -1, x, y, clickCount, false)

fun makeActionEvent(component: Component, id: Int = 0, action: String? = null): ActionEvent =
    ActionEvent(component, id, action)
