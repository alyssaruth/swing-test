package com.github.alyssaburlton.swingtest

import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import javax.swing.SwingUtilities

class SwingUtilitiesTest {
    @Test
    fun `Should process all queued events on the EDT`() {
        val fn = mockk<() -> Unit>(relaxed = true)
        val fnToInvoke = {
            Thread.sleep(2000)
            fn()
        }

        SwingUtilities.invokeLater(fnToInvoke)
        verify(exactly = 0) { fn() }

        flushEdt()
        verify { fn() }
    }

    @Test
    fun `Should wait for the condition to complete`() {
        var result = false

        val t = Thread {
            Thread.sleep(1000)
            result = true
        }

        t.start()

        shouldNotThrowAny {
            awaitCondition { result }
        }
    }

    @Test
    fun `Should throw an assertion error if we time out waiting for the condition`() {
        var result = false

        val t = Thread {
            Thread.sleep(5000)
            result = true
        }

        t.start()

        shouldThrow<AssertionError> {
            awaitCondition(timeout = 1000) { result }
        }
    }
}
