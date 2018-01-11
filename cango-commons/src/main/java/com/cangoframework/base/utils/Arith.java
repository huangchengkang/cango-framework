package com.cangoframework.base.utils;
import java.math.BigDecimal;

public class Arith {
	private static final int DEF_DIV_SCALE = 10;
	private static final int DEF_ROUND_SCALE = 10;

	public static double add(double paramDouble1, double paramDouble2) {
		BigDecimal localBigDecimal1 = new BigDecimal(
				Double.toString(paramDouble1));
		BigDecimal localBigDecimal2 = new BigDecimal(
				Double.toString(paramDouble2));
		return localBigDecimal1.add(localBigDecimal2).doubleValue();
	}

	public static double sub(double paramDouble1, double paramDouble2) {
		BigDecimal localBigDecimal1 = new BigDecimal(
				Double.toString(paramDouble1));
		BigDecimal localBigDecimal2 = new BigDecimal(
				Double.toString(paramDouble2));
		return localBigDecimal1.subtract(localBigDecimal2).doubleValue();
	}

	public static double mul(double paramDouble1, double paramDouble2) {
		BigDecimal localBigDecimal1 = new BigDecimal(
				Double.toString(paramDouble1));
		BigDecimal localBigDecimal2 = new BigDecimal(
				Double.toString(paramDouble2));
		return localBigDecimal1.multiply(localBigDecimal2).doubleValue();
	}

	public static double div(double paramDouble1, double paramDouble2) {
		return div(paramDouble1, paramDouble2, 10);
	}

	public static double div(double paramDouble1, double paramDouble2,
			int paramInt) {
		if (paramInt < 0)
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		BigDecimal localBigDecimal1 = new BigDecimal(
				Double.toString(paramDouble1));
		BigDecimal localBigDecimal2 = new BigDecimal(
				Double.toString(paramDouble2));
		return localBigDecimal1.divide(localBigDecimal2, paramInt, 4)
				.doubleValue();
	}

	public static double round(double paramDouble, int paramInt) {
		if (paramInt < 0)
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		BigDecimal localBigDecimal1 = new BigDecimal(
				Double.toString(paramDouble));
		BigDecimal localBigDecimal2 = new BigDecimal("1");
		return localBigDecimal1.divide(localBigDecimal2, paramInt, 4)
				.doubleValue();
	}

	public static double round(double paramDouble) {
		return round(paramDouble, 10);
	}

	public static void test() {
		System.out.println("[0.060000000000000005][" + add(0.05D, 0.01D) + "]");
		System.out.println("[0.5800000000000001][" + sub(1.0D, 0.42D) + "]");
		System.out.println("[401.49999999999994][" + mul(4.015D, 100.0D) + "]");
		System.out.println("[1.2329999999999999][" + div(123.3D, 100.0D) + "]");
		System.out.println(round(38046916.019999996D, 6));
	}
}