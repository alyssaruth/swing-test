package com.github.alyssaburlton.swingtest

import javax.swing.SwingUtilities

fun flushEdt() {
    val lambda = {}
    SwingUtilities.invokeAndWait(lambda)
}

fun waitForAssertion(timeout: Int = 10000, assertion: (() -> Unit)) {
    var passed = false
    val startTime = System.currentTimeMillis()
    while (!passed) {
        try {
            assertion()
            passed = true
        } catch (e: AssertionError) {
            Thread.sleep(200)

            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed > timeout) {
                throw AssertionError("Timed out waiting for assertion - see cause for details", e)
            }
        }
    }
}
