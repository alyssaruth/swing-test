@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.github.alyssaburlton.swingtest

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import sun.awt.AppContext
import java.awt.Window
import javax.swing.SwingUtilities

class SwingTestCleanupExtension : AfterEachCallback {
    override fun afterEach(context: ExtensionContext?) {
        purgeWindows()
    }
}

fun purgeWindows() {
    val windows = Window.getWindows()
    if (windows.isNotEmpty())
    {
        SwingUtilities.invokeAndWait {
            val visibleWindows = windows.filter { it.isVisible }
            visibleWindows.forEach { it.dispose() }
        }

        AppContext.getAppContext().remove(Window::class.java)
    }
}
