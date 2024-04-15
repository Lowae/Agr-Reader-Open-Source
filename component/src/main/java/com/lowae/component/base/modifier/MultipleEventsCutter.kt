package com.lowae.component.base.modifier

interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit)

    companion object
}

fun MultipleEventsCutter.Companion.get(interval: Long = 500L): MultipleEventsCutter =
    MultipleEventsCutterImpl(interval)

private class MultipleEventsCutterImpl(private val interval: Long) : MultipleEventsCutter {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= interval) {
            event.invoke()
        }
        lastEventTimeMs = now
    }
}