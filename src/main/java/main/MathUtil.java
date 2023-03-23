package main;

public class MathUtil {
	
	public static double pointDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((Math.abs(x1 - x2)), 2) + Math.pow((Math.abs(y1 - y2)), 2));
	}
	
	public static double roundD(double n, int space) {
		return Math.round(n * Math.pow(10, space));
	}
}
