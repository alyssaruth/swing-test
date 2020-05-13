package com.github.alexburlton.swingtest

import javax.swing.SwingUtilities

fun flushEdt() {
    val lambda = {}
    SwingUtilities.invokeAndWait(lambda)
}