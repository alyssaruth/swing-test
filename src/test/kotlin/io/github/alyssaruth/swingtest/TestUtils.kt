package io.github.alyssaruth.swingtest

import io.mockk.MockKVerificationScope
import io.mockk.verify

fun verifyNotCalled(verifyBlock: MockKVerificationScope.() -> Unit) {
    verify(exactly = 0) { verifyBlock() }
}
