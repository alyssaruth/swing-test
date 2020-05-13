package com.github.alexburlton.swingtest

import io.kotlintest.shouldBe
import javax.swing.JComponent

fun JComponent.shouldBeEnabled() {
    isEnabled shouldBe true
}

fun JComponent.shouldBeDisabled() {
    isEnabled shouldBe false
}

fun JComponent.shouldBeVisible() {
    isVisible shouldBe true
}

fun JComponent.shouldNotBeVisible() {
    isVisible shouldBe false
}