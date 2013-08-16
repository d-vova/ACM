// MATRIX /////////////////////////////////////////////////////////////////////

public class M implements Comparable<M> {
	public double[][] map;
	public int width, height, total;
	
	public M(int w, int h) { setup(w, h); }
	public M(int w, int h, double v) { setup(w, h); fill(v); }
	public M(int w, int h, M m) { setup(w, h); fill(m); }
	void setup(int w, int h) { map = new double[h][w]; width = w; height = h; total = w * h; }
	
	public double item(int x, int y) { return map[y % height][x % width]; }
	public void item(int x, int y, double v) { map[y % height][x % width] = v; }
	
	public int compareTo(M m) {
		if (total != m.total) return total - m.total;
		if (width != m.width) return width - m.width;
		if (height != m.height) return height - m.height;
		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
			if (item(x, y) != m.item(x, y)) return item(x, y) - m.item(x, y) > 0 ? 1 : -1;
		return 0;
	}

	// TOOLS //////////////////////////////////////////////////////////////////
	// fill matrix with values or patterns
	public M fill(double v) { return fill(0, 0, width, height, v); }
	public M fill(int x, int y, int w, int h, double v) {
		for (int i = 0; i < w && x+i < width; i++)
			for (int j = 0; j < h && y+j < height; j++)
				item(x+i, y+j, v);
		return this;
	}
	public M fill(M m) { return fill(0, 0, width, height, m); }
	public M fill(int x, int y, int w, int h, M ptrn) {
		for (int i = 0; i < w && x+i < width; i++)
			for (int j = 0; j < h && y+j < height; j++)
				item(x+i, y+j, ptrn.item(i, j));
		return this;
	}
	
	// copy matrix
	public M copy() { return copy(0, 0, width, height); }
	public M copy(int x, int y, int w, int h) {
		M m = new M(w, h);
		for (int i = 0; i < w && x+i < width; i++)
			for (int j = 0; j < h && y+j < height; j++)
				m.item(i, j, item(x+i, y+j));
		return m;
	}
	// paste matrix
	public M paste(int x, int y, M ptrn) { return fill(x, y, ptrn.width, ptrn.height, ptrn); }
	
	// rotate matrix counter clockwise by 0, 90, 180, or 270 degrees
	public M rotate(int deg) {
		int rot = (deg + 45) / 90 % 4;
		M m = rot % 2 == 0 ? new M(width, height) : new M(height, width);
		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++) {
			switch (rot) {
			case 0: m.item(x, y, item(x, y)); break;
			case 1: m.item(y, m.height-x-1, item(x, y)); break;
			case 2: m.item(m.width-x-1, m.height-y-1, item(x, y)); break;
			case 3: m.item(m.width-y-1, x, item(x, y)); break;
			}
		}
		return m;
	}
	
	// shift matrix
	public M shift(int dx, int dy) {
		M m = new M(width, height);
		if (dx < 0) dx = width + dx % width; if (dy < 0) dy = height + dy % height;
		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++) m.item(x, y, item(x+dx, y+dy));
		return m;
	}
	public M shift(int dx, int dy, double v) {
		M m = new M(width, height);
		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
			if (x+dx < 0 || x+dx >= width || y+dy < 0 || y+dy >= height) m.item(x, y, v);
			else m.item(x, y, item(x+dx, y+dy));
		return m;
	}
	
        // matrix operations for solving equation systems
	public M plus(M m) {
		M res = new M(width, height);
		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
			res.item(x, y, item(x, y) + m.item(x, y));
		return res;
	}
	
	public M times(double c) {
		M res = new M(width, height);
		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
			res.item(x, y, c * item(x, y));
		return res;
	}
	
	public M cross(M m) {
		if (width != m.height) return null;
		M res = new M(m.width, height);
		for (int x = 0; x < res.width; x++) for (int y = 0; y < res.height; y++) {
			double v = 0;
			for (int i = 0; i < width; i++) v += item(i, y) * m.item(x, i);
			res.item(x, y, v);
		}
		return res;
	}
	
	public double determinant() {
		if (width != height) return 0;
		M m = copy(); double  res = 1; boolean[] used = new boolean[height];
		for (int i = 0; i < width; i++) {
			int l = 0; while(l < height && (used[l] || m.item(i, l) == 0)) l++;
			if (l == height) return 0;
			res *= m.item(i, l) * ((i - l) % 2 == 0 ? 1 : -1); used[l] = true;
			for (int j = 0; j < height; j++) if (!used[j]) {
				double c = m.item(i, j) / m.item(i, l);
				for (int k = i; k < width; k++) m.item(k, j, m.item(k, j) - m.item(k, l) * c);
			}
		}
		return res;
	}
	
	public double cofactor(int c, int r) {
		return ((c + r) % 2 == 0 ? 1 : -1) * subMatrix(c, r).determinant();
	}
	
	public M subMatrix(int c, int r) {
		if (r < 0 || r >= height || c < 0 || c >= width) return copy();
		if (width < 2 || height < 2) return null;
		M m = new M(width - 1, height - 1);
		for (int i = 0; i < m.width; i++) for (int j = 0; j < m.height; j++)
			m.item(i, j, item(i + (i >= c ? 1 : 0), j + (j >= r ? 1 : 0)));
		return m;
	}
	
	public M transpose() {
		if (width != height) return null;
		M m = new M(width, height);
		for (int i = 0; i < width; i++) for (int j = 0; j < height; j++) m.item(i, j, item(j, i));
		return m;
	}
	
	public M adjoint() {
		if (width != height) return null;
		M m = new M(width, height);
		for (int i = 0; i < width; i++) for (int j = 0; j < height; j++) m.item(i, j, cofactor(i, j));
		return m.transpose();
	}
	 
	public M inverse() {
		double d = width == height ? determinant() : 0;
		return d != 0 ? adjoint().times(1 / d) : null;
	}
	
        // Chain Matrix Multiplication
	public static M cross(M[] m) {
		if (m.length == 0) return null;
		for (int i = 1; i < m.length; i++) if (m[i-1].width != m[i].height) return null;
		
		long[][] score = new long[m.length][m.length];
		int[][] bt = new int[m.length][m.length];
		
		for (int step = 0; step < m.length; step++)
			for (int i = 0; i < m.length-step; i++) {
				int j = i + step;
				if (step == 0) { score[i][j] = 0; bt[i][j] = -1; }
				else {
					score[i][j] = Long.MAX_VALUE;
					for (int k = i; k < j; k++) {
						long buf = score[i][k] + score[k+1][j] + m[i].height*m[k].width*m[j].width;
						if (buf < score[i][j]) { score[i][j] = buf; bt[i][j] = k; }
					}
				}
			}
		return cross(m, bt, 0, m.length-1);
	}
	
	static M cross(M[] m, int[][] bt, int i, int j) {
		if (bt[i][j] == -1) return m[i];
		else return cross(m, bt, i, bt[i][j]).cross(cross(m, bt, bt[i][j]+1, j));
	}
	
	// Solve Equation System Ax = B
	public static M solve (M a, M b) {
		M inverse = a.inverse();
		return inverse != null ? inverse.cross(b) : null;
	}
	
	public String toString() {
		String s = "Matrix:";
		for (int y = 0; y < height; y++) {
			s += "\n";
			for (int x = 0; x < width; x++) s += String.format("   %5.2f", item(x, y));
		}
		return s;
	}
}
