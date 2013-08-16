public class Circle {
	public static double e = Point.e;
	public Point center; public double radius;

	public Circle(Point c, double r) { center = c; radius = r; }
	public Circle(double x, double y, double r) { center = new Point(x, y); radius = r; }

	public double arcLength(double f, double t) { return Math.abs(radius * (t - f)); }
	public boolean has(Point p) { return Math.abs(center.sub(p).length() - radius) < e; }
	
	public Point[] intersect(Line l) {
		Point p = l.perpendicular(center).intersect(l);
		double d = center.sub(p).length();
		if (d > radius) return new Point[0];
		if (d == radius) return new Point[] { p };
		return l.offset(p, Math.sqrt(radius*radius - d*d));
	}
	
	public Point[] intersect(Circle c) {
		double d = center.sub(c.center).length(), R = radius, r = c.radius;
		if (d > r + R || r > d + R || R > d + r || center.compareTo(c.center) == 0) return new Point[0];
		double x = (d*d + R*R - r*r) / (2*d), y = Math.sqrt(R*R - x*x);
		Point[] points = Line.line(center, c.center).offset(center, x);
		Point[] left = Line.line(center, c.center).perpendicular(points[0]).offset(points[0], y);
		Point[] right = Line.line(center, c.center).perpendicular(points[1]).offset(points[1], y);
		Point[] p = left.length > 0 && c.has(left[0]) ? left : right;
		return p[0] == p[1] ? new Point[] { p[0] } : p;
	}
	
	// Static constructor of a circle by three points
	public static Circle circle(Point a, Point b, Point c) {
		Line abm = Line.line(a, b).perpendicular(Point.between(a, b));
		Line bcm = Line.line(b, c).perpendicular(Point.between(b, c));
		Point center = abm.intersect(bcm);
		return center == null ? null : new Circle(center, a.sub(center).length());
	}
}
