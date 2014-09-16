package ch.javasoft.decimal;

/**
 * Mode to apply if arithmetic operations cause an overflow.
 */
public enum OverflowMode {
	/**
	 * Operations causing an overflow silently return the truncated result (the
	 * low order bytes of the extended result); no exception is thrown.
	 */
	STANDARD,
	/**
	 * Operations causing an overflow throw an {@link ArithmeticException}.
	 */
	CHECKED;
	
	/**
	 * Returns true if overflow leads to an {@link ArithmeticException}
	 * @return true if {@code this == CHECKED}
	 */
	public boolean isChecked() {
		return this == CHECKED;
	}
}
