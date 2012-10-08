class F {
	public static String format(String s, int len) {

		StringBuilder b = new StringBuilder();
		int slen = len-s.length();

		for (int i = 0; i < slen; i++)
			b.append(' ');

		b.append(s);
		return b.toString();
	}

	public static String format(Object x, int len){
		return format(String.valueOf(x), len);
	}

	public static String format(long x, int len){
		return format(String.valueOf(x), len);
	}

	public static String format(double x, int len){
		return format(String.valueOf(x), len);
	}

	public static String format(char x, int len){
		return format(String.valueOf(x), len);
	}
}
