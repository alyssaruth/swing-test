package com.github.alexburlton.swingtest

import javax.swing.SwingUtilities

fun flushEdt() {
    val lambda = {}
    SwingUtilities.invokeAndWait(lambda)
}

fun awaitCondition(timeout: Int = 10000, condition: (() -> Boolean))
{
    val startTime = System.currentTimeMillis()
    while (!condition()) {
        Thread.sleep(200)

        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed > timeout) {
            throw AssertionError("Timed out waiting for condition")
        }
    }
}