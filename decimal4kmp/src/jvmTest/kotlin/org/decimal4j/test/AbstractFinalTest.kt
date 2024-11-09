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
package org.decimal4j.test

import org.junit.Assert
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.jvm.kotlinFunction

/**
 * Base class for unit test to enforce final fields and methods.
 */
abstract class AbstractFinalTest {
    /**
     * Throws an [AssertionError] if the given class is non-final.
     *
     * @param clazz
     * the class under test
     */
    protected fun assertClassIsFinal(clazz: Class<*>) {
        val mod = clazz.modifiers
        Assert.assertTrue(
            "class should be abstract or final: $clazz",
            Modifier.isAbstract(mod) || Modifier.isFinal(mod)
        )
    }

    /**
     * Throws an [AssertionError] if any of the declared methods of the
     * specified `clazz` is non-final. Delegates to
     * [.assertMethodIsFinal].
     *
     * @param clazz
     * the class under test
     */
    protected fun assertAllMethodsAreFinal(clazz: Class<*>) {
        // all methods of a class should be final
        for (method in clazz.declaredMethods) {
            assertMethodIsFinal(method)
        }
        // all methods of an enum constant should also be final
        if (clazz.isEnum) {
            for (constValue in clazz.enumConstants) {
                for (method in constValue.javaClass.declaredMethods) {
                    assertMethodIsFinal(method)
                }
            }
        }
    }

    /**
     * Throws an [AssertionError] if any of the declared fields of the
     * specified `clazz` is non-final or non-conforming. Delegates to
     * [.assertFieldFinal].
     *
     * @param clazz
     * the class under test
     */
    protected fun assertAllFieldsAreFinal(clazz: Class<*>) {
        // all methods of a class should be final
        for (field in clazz.declaredFields) {
            assertFieldFinal(field)
        }
        // all fields of an enum constant should also be final
        if (clazz.isEnum) {
            for (constValue in clazz.enumConstants) {
                for (field in constValue.javaClass.declaredFields) {
                    assertFieldFinal(field)
                }
            }
        }
    }

    /**
     * Throws an [AssertionError] if the specified `method` is
     * non-final. The method is exempt from the test if it is synthetic or
     * abstract.
     *
     * @param method
     * the method under test
     * @throws AssertionError
     * if method is non-final and not exempt
     */
    private fun assertMethodIsFinal(method: Method) {
        val mod = method.modifiers
        if (!method.isSynthetic && !isSyntheticEnumMethod(method)
            && method.kotlinFunction == null
            && method.name != "getEntries"
            && method.name != "getOverflowMode"
            && method.name != "getScaleMetrics"
            && method.name != "getRoundingMode"
            && method.name != "getScale"
            && method.name != "getTruncationPolicy"
        ) {
            Assert.assertTrue(
                "method should be abstract or final: $method",
                Modifier.isAbstract(mod) || Modifier.isFinal(mod)
            )
        }
    }

    /**
     * Throws an [AssertionError] if the specified `field` is
     * non-final or otherwise non-conforming (e.g. non-private etc.).
     *
     * @param field
     * the field under test
     * @throws AssertionError
     * if field is non-final or non-conforming
     */
    private fun assertFieldFinal(field: Field) {
        val mod = field.modifiers
        if (!field.isSynthetic) {
            if (isAllowedNonFinalField(field)) {
                Assert.assertFalse("field should be non-static: $field", Modifier.isStatic(mod))
                Assert.assertTrue("field should be private: $field", Modifier.isPrivate(mod))
            } else {
                Assert.assertTrue("field should be final: $field", Modifier.isFinal(mod))
            }
            if (field.type.isArray) {
                // array should be private as it can be modified
                Assert.assertTrue("array field should be private: $field", Modifier.isPrivate(mod))
            }
            // TODO collections and maps should be immutable
        }
    }

    /**
     * Returns true if the specified field is allowed to be non-static. Default
     * implementation always returns false.
     *
     * @param field
     * the field to check
     * @return always false except if overridden
     */
    protected open fun isAllowedNonStaticField(field: Field): Boolean {
        return false
    }

    /**
     * Returns true if the specified non-static field is allowed to be
     * non-final. Default implementation always returns false.
     *
     * @param field
     * the field to check
     * @return always false except if overridden
     */
    protected open fun isAllowedNonFinalField(field: Field): Boolean {
        return false
    }

    private fun isSyntheticEnumMethod(method: Method): Boolean {
        if (method.declaringClass.isEnum) {
            if ("values" == method.name && method.parameterTypes.size == 0) {
                return true
            }

            if ("valueOf" == method.name && method.parameterTypes.size == 1 && method.parameterTypes[0] == String::class.java) {
                return true
            }
        }
        return false
    }
}
