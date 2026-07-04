package io.github.alyssaruth.swingtest

const val UPDATE_SNAPSHOT_PROP = "updateSnapshots"
const val SCREENSHOT_OS_PROP = "screenshotOs"
const val ASYNC_BY_DEFAULT_PROP = "asyncInteractionByDefault"

val ASYNC_BY_DEFAULT: Boolean
    get() =  System.getProperty(ASYNC_BY_DEFAULT_PROP)?.toBoolean() ?: true