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
package org.decimal4j.jmh;

import java.io.IOException;

import org.decimal4j.jmh.state.ConvertToStringBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for to-string conversion.
 */
public class ConvertToStringBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(ConvertToStringBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimalsToPlainString(ConvertToStringBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimalsToPlainString(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(ConvertToStringBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immitableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(ConvertToStringBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(ConvertToStringBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}
	
	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimalsToAppendable(ConvertToStringBenchmarkState state, Blackhole blackhole) throws IOException {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimalsToAppendable(state, state.values[i]));
		}
	}

	private static final <S extends ScaleMetrics> String bigDecimals(ConvertToStringBenchmarkState state, Values<S> values) {
		return values.bigDecimal1.toString();
	}
	private static final <S extends ScaleMetrics> String bigDecimalsToPlainString(ConvertToStringBenchmarkState state, Values<S> values) {
		return values.bigDecimal1.toPlainString();
	}

	private static final <S extends ScaleMetrics> String immitableDecimals(ConvertToStringBenchmarkState state, Values<S> values) {
		return values.immutable1.toString();
	}

	private static final <S extends ScaleMetrics> String mutableDecimals(ConvertToStringBenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).toString();
	}

	private static final <S extends ScaleMetrics> String nativeDecimals(ConvertToStringBenchmarkState state, Values<S> values) {
		return state.arithmetic.toString(values.unscaled1);
	}

	private static final <S extends ScaleMetrics> StringBuilder nativeDecimalsToAppendable(ConvertToStringBenchmarkState state, Values<S> values) throws IOException {
		final StringBuilder appendable = state.appendable;
		appendable.setLength(0);
		state.arithmetic.toString(values.unscaled1, appendable);
		return appendable;
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(ConvertToStringBenchmark.class);
	}
}
