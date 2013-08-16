// Point is not just a set of coordinates, but a vector in geometry
// that points from the origin to a point in space
// and provides you with a set of useful tools
// NOTICE: Once created, point won't be modified
// returned values reference other instances

public class Point implements Comparable<Point> {
	public static double e = 0.000001;
	public double x, y; 
	public Point (double ix, double iy) { x = ix; y = iy; }

	// a . b = |a| |b| Sin()
	// a x b = |a| |b| Cos() = area of the parallelogram
	public double dot(Point p) { return x*p.x + y*p.y; }               // Dot product
	public double cross(Point p) { return x*p.y - y*p.x; }             // Cross product
	public double length() { return Math.sqrt(x*x+y*y); }              // Magnitude
	public double angle() { return Math.atan2(y, x); }                 // Inclination

	public Point add(Point p) { return new Point(x+p.x, y+p.y); }      // Addition
	public Point sub(Point p) { return new Point(x-p.x, y-p.y); }      // Subtraction
	public Point scale(double c) { return new Point(x*c, y*c); }       // Scale  
	public Point scaleTo(double len) { return scale(len/length()); }   // Scale to size

	// Rotate point to or by angle a around the origin or point p
	public Point rotateTo(double a) { return new Point(length()*Math.cos(a), length()*Math.sin(a)); }
	public Point rotateTo(double a, Point p) { return p.add(sub(p).rotateTo(a)); }
	public Point rotateBy(double a) { return rotateTo(angle()+a); }
	public Point rotateBy(double a, Point p) { return rotateTo(angle()+a, p); }

	// Next point towards point p using maximum step of length len
	public Point next(Point p, double len) {
		if (len >= sub(p).length()) return p; else return add(p.sub(this).scaleTo(len));
	}

	public boolean isBetween(Point a, Point b) {
		return Math.abs(sub(a).length() + sub(b).length() - a.sub(b).length()) < e;
	}

	// Compare for equality
	public int compareTo(Point p) {
		return Math.abs(x-p.x) < e && Math.abs(y-p.y) < e ? 0 : toString().compareTo(p.toString());
	}
	
	public String toString() { return String.format("(%f,%f)", x, y); }
	
	public static Point between(Point a, Point b) { return between(a, b, 0.5f); }
	public static Point between(Point a, Point b, double ratio) { return a.add(b.sub(a).scale(ratio)); }
}
