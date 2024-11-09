/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.arithmetic

import org.junit.Assert
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

/**
 * Unit test for [ThreadLocals].
 */
class ThreadLocalsTest {

    private class ThreadLocalInstances {
        val stringBuilder: StringBuilder = StringConversion.STRING_BUILDER_THREAD_LOCAL.get()
        val unsignedDecimal1: UnsignedDecimal9i36f = UnsignedDecimal9i36f.THREAD_LOCAL_1.get()
        val unsignedDecimal2: UnsignedDecimal9i36f = UnsignedDecimal9i36f.THREAD_LOCAL_2.get()
    }

    @Test
    fun shoudUseDifferentThreadLocalInstancesInTwoThreads() {
        //given
        val ref = AtomicReference<ThreadLocalInstances?>()
        var tli1: ThreadLocalInstances?
        var tli2: ThreadLocalInstances?
        val runnable = Runnable { ref.set(ThreadLocalInstances()) }


        //when
        Thread(runnable).start()
        do tli1 = ref.getAndSet(null)
        while (tli1 == null)

        Thread(runnable).start()
        do tli2 = ref.getAndSet(null)
        while (tli2 == null)

        //then
        Assert.assertNotSame("string builder should be different instances", tli1.stringBuilder, tli2.stringBuilder)
        Assert.assertNotSame(
            "unsigned decimal 1 should be different instances",
            tli1.unsignedDecimal1,
            tli2.unsignedDecimal1
        )
        Assert.assertNotSame(
            "unsigned decimal 2 should be different instances",
            tli1.unsignedDecimal2,
            tli2.unsignedDecimal2
        )
    }

    @Test
    fun shoudReuseThreadLocalInstancesInSameThread() {
        //when
        val tli1 = ThreadLocalInstances()
        val tli2 = ThreadLocalInstances()


        //then
        Assert.assertSame("string builder should be same instance", tli1.stringBuilder, tli2.stringBuilder)
        Assert.assertSame("unsigned decimal 1 should be same instance", tli1.unsignedDecimal1, tli2.unsignedDecimal1)
        Assert.assertSame("unsigned decimal 2 should be same instance", tli1.unsignedDecimal2, tli2.unsignedDecimal2)
    }

    @Test
    fun shoudRemoveThreadLocalInstances() {
        //given
        val tli1 = ThreadLocalInstances()

        //when
        ThreadLocals.removeAll()
        val tli2 = ThreadLocalInstances()


        //then
        Assert.assertNotSame("string builder should be different instances", tli1.stringBuilder, tli2.stringBuilder)
        Assert.assertNotSame(
            "unsigned decimal 1 should be different instances",
            tli1.unsignedDecimal1,
            tli2.unsignedDecimal1
        )
        Assert.assertNotSame(
            "unsigned decimal 2 should be different instances",
            tli1.unsignedDecimal2,
            tli2.unsignedDecimal2
        )
    }
}
