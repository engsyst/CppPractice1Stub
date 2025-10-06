package ua.nure.cpp.name.practice1;

public class Part1 {

	/**
	 * The utility method allows you to increase the size of an array by 1.
	 * It creates a copy of an array with larger size.
	 *
	 * @param values an array.
	 * @return new array with increased size.
	 */
	static double[] extendArray(double[] values) {
		return extendArray(values, 1);
	}

	/**
	 * The utility method allows you to increase the size of an array by n.
	 * It creates a copy of an array with larger size.
	 *
	 * @param values an array.
	 * @return new array with increased size.
	 */
	static double[] extendArray(double[] values, int n) {
		double[] newValues = new double[values.length + n];
		System.arraycopy(values, 0, newValues, 0,values.length);
		return newValues;
	}

	// place your code here

	public static void main(String[] args) {

		// replace the code above by your code here

		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}

		double[] v = new double[] {5};
		System.out.println(v.length);
		for (double d : v) {
			System.out.println(d);
		}
		v = extendArray(v, 1);
		System.out.println(v.length);
		for (double d : v) {
			System.out.println(d);
		}

	}
}
