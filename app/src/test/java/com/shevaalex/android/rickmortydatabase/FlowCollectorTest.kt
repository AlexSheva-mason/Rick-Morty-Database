package com.shevaalex.android.rickmortydatabase;

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.junit.Assert.*
import kotlin.reflect.KClass


/**
 * Author: Daniele Segato (https://github.com/danielesegato)
 * https://gist.github.com/danielesegato/3d33b5272b7ce787d0e3dde1d74acc77
 */
class FlowCollectorTest<T> {

    private var job: Job? = null
    private val _values = mutableListOf<T>()
    var error: Throwable? = null
        get() = synchronized(this) {
            field
        }
        private set(value) {
            synchronized(this) {
                field = value
            }
        }
    var completed: Boolean = false
        get() = synchronized(this) { field }
        private set(value) {
            synchronized(this) {
                field = value
            }
        }
    val values: List<T>
        get() = synchronized(this) {
            _values.toList()
        }

    internal fun testOn(scope: CoroutineScope, flow: Flow<T>) {
        synchronized(this) {
            check(job == null) { "this TestFlowCollector testOn has already been used" }
            job = scope.launch {
                flow
                    .catch { e -> error = e }
                    .onCompletion { completed = true }
                    .collect { item ->
                        synchronized(this) {
                            _values.add(item)
                        }
                    }
            }
        }
    }

    fun cancel() {
        val j = job
        check(j != null) { "this TestFlowCollector testOn has never been used" }
        j.cancel()
    }
}

fun <T> Flow<T>.testTest(scope: CoroutineScope): FlowCollectorTest<T> {
    val testCollector = FlowCollectorTest<T>()
    testCollector.testOn(scope, this)
    return testCollector
}

fun <T> FlowCollectorTest<T>.lastValue(): T? = values.lastOrNull()

fun <T> FlowCollectorTest<T>.assertEmittedCount(
    count: Int,
    message: String = "Emission count does not match expected"
) {
    require(count >= 0) { "asserting emitted count less then zero makes no sense" }
    assertEquals(message, count, values.size)
}

fun <T> FlowCollectorTest<T>.assertNothingEmitted(message: String = "Expected nothing emitted") {
    assertEmittedCount(0, message)
}

fun <T> FlowCollectorTest<T>.assertEmittedValuesEquals(
    values: List<T>,
    message: String = "Emitted values expectation is not met"
) {
    assertEquals(message, values, this.values)
}

fun <T> FlowCollectorTest<T>.assertEmittedValuesEquals(
    first: T,
    vararg others: T,
    message: String = "Emitted values expectation is not met"
) {
    assertEquals(message, listOf(first, *others), values)
}

fun <T> FlowCollectorTest<T>.assertEmittedValuesSame(
    values: List<T>,
    message: String = "Emitted values expectation is not met"
) {
    values
        .forEachIndexed { idx, item ->
            assertSame("$message (on index $idx)", values[idx], item)
        }
}

fun <T> FlowCollectorTest<T>.assertEmittedValuesContains(
    value: T,
    message: String = "Value expected to be emitted was not"
) {
    val found = values.firstOrNull { it == value }
    assertEquals(message, value, found)
}

fun <T> FlowCollectorTest<T>.assertCompleted(message: String = "Expected completed but was not") {
    assertTrue(message, completed)
}

fun <T> FlowCollectorTest<T>.assertNotCompleted(message: String = "Expected not completed but was") {
    assertFalse(message, completed)
}

fun <T> FlowCollectorTest<T>.assertNoError(message: String = "Expected no error but there was one") {
    assertNull(message, error)
}

fun <T, E : Throwable> FlowCollectorTest<T>.assertErrorThrown(
    clazz: KClass<E>,
    message: String = "Error expectation was not met"
): E? {
    val e = error
    assertNotNull(message, e)
    if (e != null) {
        assertEquals(message, clazz, e::class)
    }
    return e as? E
}

fun <T, E : Throwable> FlowCollectorTest<T>.assertErrorThrown(
    clazz: Class<E>,
    message: String = "Error expectation was not met"
): E? {
    val e = error
    assertNotNull(message, e)
    if (e != null) {
        assertEquals(message, clazz, e::class.java)
    }
    return e as? E
}

fun <T> FlowCollectorTest<T>.assertLastEmittedValueEquals(
    v: T,
    message: String = "Last Emitted value did not met expectations"
) {
    assertEquals(message, v, lastValue())
}