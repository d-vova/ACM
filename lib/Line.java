public class Line {
	public static double e = Point.e;
	public double a, b, c; // Line is given by equation: a*x+b*y=c
	
	public Line(double ia, double ib, double ic) { a = ia; b = ib; c = ic; }
	public Line(double mx, double my, double nx, double ny) { a = my - ny; b = nx - mx; c = a * mx + b * my; }
	
	public double angle() { return (new Point(b, -a)).angle(); }
	public double x(double y) { return (b * y - c) / a; }
	public double y(double x) { return (a * x - c) / b; }
	public boolean has(Point p) { return Math.abs(a * p.x + b * p.y - c) < e; }
	
	// Points offset by distance d from p on the line
	public Point[] offset(Point p, double d) {
		Point u = new Point(0, d); double angle = angle();
		return new Point[] { p.add(u.rotateTo(angle)), p.add(u.rotateTo(angle+Math.PI)) };
	}
	
	// Point of intersection with line l or null if a single point doesn't exist
	public Point intersect(Line l) {
		double d = a*l.b - b*l.a;
		return d == 0 ? null : new Point ((c*l.b - b*l.c) / d, (a*l.c - c*l.a) / d);
	}
	
	// Point on the line closest to p
	public Point closestTo(Point p) { return intersect(perpendicular(p)); }
	
	// Perpendicular line through point p
	public Line perpendicular(Point p) { return new Line(-b, a, a * p.y - b * p.x); }
	
	public String toString() { return String.format("%fX + %fY = %f", a, b, c); }
	
	// Static constructor of a line by two points
	public static Line line(Point a, Point b) {
		return a.compareTo(b) == 0 ? null : new Line(a.x, a.y, b.x, b.y);
	}
	// Offset distance d on ray ab
	public static Point rayOffset(Point a, Point b, double d) {
		Point[] p = Line.line(a, b).offset(a, d);
		return b.sub(p[0]).length() < b.sub(p[1]).length() ? p[0] : p[1];
	}
}
