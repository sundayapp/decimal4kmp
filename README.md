[![Build Status](https://travis-ci.org/tools4j/decimal4j.svg?branch=master)](https://travis-ci.org/tools4j/decimal4j)
[![Maven Central](https://img.shields.io/maven-central/v/org.decimal4j/decimal4j.svg)](https://search.maven.org/search?q=decimal4j)
[![Javadocs](http://www.javadoc.io/badge/org.decimal4j/decimal4j.svg)](http://www.javadoc.io/doc/org.decimal4j/decimal4j)

## decimal4kmp
Kotlin multiplatform library for fast fixed-point arithmetic based on longs with support for up to 18 decimal places.
Ported for the library decimal4j

#### Features
 - Fixed-point arithmetic with 0 to 18 decimal places
   - Implementation based on unscaled long value
   - Option to throw an exception when an arithmetic overflow occurs
 - Scale
   - Type of a variable defines the scale (except for wildcard types)
   - Result has usually the same scale as the primary operand (also for multiplication and division)
 - Type Conversion
   - Efficient conversion from and to various other number types
   - Convenience methods to directly inter-operate with other data types (long, double, ...)
   - All rounding modes supported (default: HALF_UP)
 - Efficiency
   - Fast and efficient implementation (see [performance benchmarks](https://github.com/tools4j/decimal4j/wiki/Performance))
   - [`MutableDecimal`](https://github.com/tools4j/decimal4j/wiki/Examples#example-3-mean-and-standard-deviation-with-mutabledecimal) implementation for chained operations
   - `DecimalArithmetic`  API for [zero-garbage computations](https://github.com/tools4j/decimal4j/wiki/DecimalArithmetic-API) (with unscaled long values)

#### Maven/Gradle

###### Maven
```xml
<dependency>
	<groupId>org.decimal4kmp</groupId>
	<artifactId>decimal4kmp</artifactId>
	<version>0.0.1</version>
	<scope>compile</scope>
</dependency>
```

###### Gradle
```
compile 'org.decimal4kmp:decimal4kmp:0.0.1'
```

