/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.jmh.ConvertFromDoubleBenchmark;
import org.decimal4j.jmh.value.DoubleType;
import org.decimal4j.jmh.value.SignType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ConvertFromDoubleBenchmarkState extends AbstractBenchmarkState {
	@Param({ "HALF_EVEN" , "DOWN"})
	public RoundingMode roundingMode;
	@Param
	public DoubleType doubleType;
	
	public double[] doubles = new double[ConvertFromDoubleBenchmark.OPERATIONS_PER_INVOCATION];
	public MutableDecimal<?> mutable;

	@Setup
	public void init() {
		super.init(roundingMode);
	}
	@Setup
	public void initValues() {
		for (int i = 0; i < ConvertFromDoubleBenchmark.OPERATIONS_PER_INVOCATION; i++) {
			doubles[i] = doubleType.random(SignType.ALL, scale);
			mutable = Factories.getDecimalFactory(scale).newMutable();
		}
	}
}