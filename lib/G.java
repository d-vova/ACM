// GRAPH //////////////////////////////////////////////////////////////////////

import java.util.*;

public class G {
	public static class Node implements Comparable<Node> {
		public String value, hash;
		public Node (String v) { value = v; hash = v; }
		public Node (String v, String h) { value = v; hash = h; }
		public int compareTo(Node n) { return hash.compareTo(n.hash); }
		public int compare(Node n) { return value.compareTo(n.value); }
		public String toString() { return hash + "-" + value; }
	}
	
	public class State implements Comparable<State> {
		public State state; public Node node; public double weight; public int len;
		public State(Node n) { node = n; weigh(); }
		public State(State s, Node n) { state = s; node = n; weigh(); }
		public State(Node[] path) {
			for (int i = 0; i < path.length-1; i++) state = new State(state, path[i]);
			node = path[path.length-1]; weigh();
		}
		
		public LinkedList<Node> backtrack() {
			LinkedList<Node> path = new LinkedList<Node>();
			backtrack(path);
			return path;
		}
		
		public int compareTo(State s) {
			if (weight != s.weight) return (int)(weight - s.weight);
			if (state == null) return s.state == null ? 0 : -1;
			else return s.state == null ? 1 : state.compareTo(s.state);
		}
		
		void backtrack(LinkedList<Node> path) {
			path.push(node); if (state != null) state.backtrack(path);
		}
		
		void weigh() {
			if (state == null) { weight = 0; len = 1; }
			else { weight = edge(state.node, node) + state.weight; len = state.len + 1; }
		}
	}
	
	public TreeSet<Node> nodes = new TreeSet<Node>();
	public TreeMap<Node, TreeMap<Node, Double>> edges = new TreeMap<Node, TreeMap<Node, Double>>();
	
	public boolean has(Node n) { return nodes.contains(n); }
	public boolean has(Node f, Node t) { return has(f) && edges.get(f).containsKey(t); }
	public void add(Node n) { if (!has(n)) { nodes.add(n); edges.put(n, new TreeMap<Node, Double>()); } }
	public void add(Node f, Node t, double c, boolean d) { add(f); add(t); edge(f, t, c); if (!d) edge(t, f, c); }
	public double edge(Node f, Node t) { return edges.get(f).get(t); }
	public void edge(Node f, Node t, double c) { edges.get(f).put(t, c); }
	public void remove(Node n) { for (Node f: nodes) remove(f, n); edges.remove(n); nodes.remove(n); }
	public void remove(Node f, Node t) { if (has(f, t)) edges.get(f).remove(t); }
	
	// reliable path finder (len <= 0 - at most len nodes)
	public State path(Node f, Node t, Queue<State> q, int len) {
		HashSet<Node> v = new HashSet<Node>();
		q.offer(new State(f));
		while (!q.isEmpty()) {
			State cur = q.poll();
			if (!v.contains(cur.node)) {
				v.add(cur.node);
				if (cur.node.compareTo(t) == 0) return cur;
				for (Node n: edges.get(cur.node).keySet())
					if (!v.contains(n) && (len <= 0 || cur.len < len)) q.offer(new State(cur, n));
			}
		}
		return null;
	}
	
	// TOOLS //////////////////////////////////////////////////////////////////
	public G() { }
	public G(G g) {
		for (Node n: g.nodes) add(n);
		for (Node f: g.nodes) for (Node t: g.edges.get(f).keySet()) edge(f, t, g.edge(f, t));
	}
	
	// Shortest Path
	public State bfs(Node f, Node t) { return path(f, t, new LinkedList<State>(), 0); }
	
	// Cheapest Path (positive edges)
	public State dijkstra(Node f, Node t) { return path(f, t, new PriorityQueue<State>(), 0); }
	
	// Cheapest Path (no negative cycles)
	public State bellmanFord(Node f, Node t) {
		TreeMap<Node, Double> cost = new TreeMap<Node, Double>();
		TreeMap<Node, Node> bt = new TreeMap<Node, Node>();
		for (Node n: nodes) { cost.put(n, Double.MAX_VALUE); bt.put(n, null); }
		cost.put(f, 0.0);
		boolean update = true;
		while (update) {
			update = false;
			for (Node a: nodes) for (Node b: edges.get(a).keySet()) {
				double buf = cost.get(a) + edge(a, b);
				if (cost.get(b) > buf) { cost.put(b, buf); bt.put(b, a); update = true; }
			}
		}
		LinkedList<Node> path = new LinkedList<Node>(); Node cur = t;
		while (cur != null) { path.addFirst(cur); if (cur.compareTo(f) == 0) break; else cur = bt.get(cur); }
		return cur == null ? null : new State(path.toArray(new Node[path.size()]));
	}
	
	// Longest Path (in a DAG)
	public State longestPath() {
		Collection<Node> list = dag(); if (list.isEmpty()) return null;
		
		Node[] seq = list.toArray(new Node[list.size()]);
		State[] path = new State[seq.length]; path[0] = new State(seq[0]);
		int best = 0;
		for (int i = 1; i < seq.length; i++) {
			int last = i - 1;
			while(last >= 0 && !has(seq[last], seq[i])) last--;
			path[i] = new State(last < 0 ? null : path[last], seq[i]);
			if (path[best].len < path[i].len) best = i;
		}
		
		return path[best];
	}
	
	// Every Path
	public ArrayList<State> dfs(Node f, Node t) {
		ArrayList<State> list = new ArrayList<State>();
		Stack<State> stack = new Stack<State>();
		HashSet<Node> v = new HashSet<Node>();
		stack.push(new State(f));
		while (!stack.isEmpty()) {
			State cur = stack.peek();
			if (!v.contains(cur.node)) {
				v.add(cur.node);
				// adds every path from f to t, and can be change to meet a different criteria
				if (cur.node.compareTo(t) == 0) list.add(cur);
				else for (Node n: edges.get(cur.node).keySet())
					if (!v.contains(n)) stack.push(new State(cur, n));
			}
			else v.remove(stack.pop().node);
		}
		return list;
	}
	
	// Minimum Spanning Tree
	public G prim() {
		G tree = new G();
		tree.add(nodes.first());
		while (tree.nodes.size() < nodes.size()) {
			PriorityQueue<State> q = new PriorityQueue<State>();
			for (Node f: tree.nodes) for (Node t: edges.get(f).keySet())
				if (!tree.nodes.contains(t)) q.offer(new State(new State(f), t));
			if (q.isEmpty()) break;
			State best = q.poll(); Node f = best.state.node, t = best.node;
			tree.add(f, t, best.weight, false);
		}
		return tree;
	}
	
	// Minimum Spanning Tree
	public G kruskal() {
		G tree = new G();
		PriorityQueue<State> q = new PriorityQueue<State>();
		for (Node f: nodes) {
			for (Node t: edges.get(f).keySet()) q.offer(new State(new State(f), t));
			tree.add(f);
		} 
		while (!q.isEmpty() && tree.edgeCount()/2 < tree.nodes.size()-1) {
			State cur = q.poll(); Node f = cur.state.node, t = cur.node;
			if (tree.bfs(f, t) == null) tree.add(f, t, cur.weight, false);
		}
		return tree;
	}
	
	// Maximum Flow (integer edges)
	public double fordFulkerson(Node from, Node to) {
		G g = new G(this); /*G flow = new G();
		for (Node n: nodes) flow.add(n);
		for (Node f: nodes) for (Node t: edges.get(f).keySet()) flow.add(f, t, 0, false);*/
		double total = 0;
		while (true) {
			State p = g.bfs(from, to); if (p == null) break;
			LinkedList<Node> l = p.backtrack();
			double min = Double.MAX_VALUE;
			for (int i = 1; i < l.size(); i++) min = Math.min(min, g.edge(l.get(i-1), l.get(i)));
			for (int i = 1; i < l.size(); i++) {
				Node f = l.get(i-1), t = l.get(i);
				double ft = (g.has(f, t) ? g.edge(f, t) : 0) - min;
				double tf = (g.has(t, f) ? g.edge(t, f) : 0) + min;
				if (ft == 0) g.remove(f, t); else g.edge(f, t, ft);
				if (tf == 0) g.remove(t, f); else g.edge(t, f, tf);
				//flow.edge(f, t, flow.edge(f, t) + min);
				//flow.edge(t, f, flow.edge(t, f) - min);
			}
			total += min;
		}
		return total;
		//return flow;
	}
	
	// All-Pairs Shortest Path (detecting positive cycles or not)
	public double[][] floydWarshall(boolean cycles) {
		Node[] an = nodes.toArray(new Node[nodes.size()]);
		double[][] map = new double[an.length][an.length];
		//int[][] bt = new int[an.length][an.length];
		
		for (int i = 0; i < an.length; i++)
			for (int j = 0; j < an.length; j++) {
				map[i][j] = has(an[i], an[j]) ? edge(an[i], an[j]) : cycles || i != j ? Double.NaN : 0;
				//bt[i][j] = -1;
			}
				
		for (int k = 0; k < an.length; k++)
			for (int i = 0; i < an.length; i++)
				for (int j = 0; j < an.length; j++)
					if (Double.isNaN(map[i][j]) || map[i][k] + map[k][j] < map[i][j]) {
						map[i][j] = map[i][k]+map[k][j];
						//bt[i][j] = k;
					}
		
		return map;
	}
	
	// Flood Fill
	public Collection<Node> floodFill(Node f, Node t) {
		LinkedList<Node> q = new LinkedList<Node>();
		HashSet<Node> v = new HashSet<Node>();
		q.offer(f);
		while (!q.isEmpty()) {
			Node cur = q.poll();
			// visit node only if f value is different from t value
			if (!v.contains(cur) && cur.compare(t) != 0) {
				v.add(cur);
				for (Node n: edges.get(cur).keySet())
					if (!v.contains(n)) q.offer(n);
			}
		}
		return v;
	}
	
	// Directed Acyclic Graph representation
	public Collection<Node> dag() {
		ArrayList<Node> list = new ArrayList<Node>();
		LinkedList<Node> q = new LinkedList<Node>();
		for (Node n: nodes) q.offer(n);
		while(!q.isEmpty()) {
			Node cur = q.poll(); boolean next = true;
			for (Node n: nodes) if (!list.contains(n) && has(n, cur)) { next = false; break; }
			if (next) list.add(cur); else q.offer(cur);
		}
		return list;
	}
	
	// List of Leaf Nodes (with at most e outgoing edges)
	public Collection<Node> leaves(int e) {
		ArrayList<Node> list = new ArrayList<Node>();
		for (Node n: nodes) if (edges.get(n).size() <= e) list.add(n);
		return list;
	}

	// count all directed edges in the graph
	public int edgeCount() { int c = 0; for (Node n: nodes) c += edges.get(n).size(); return c; }
	
	// serialize graph for debugging purposes
	public String toString() {
		String g = "";
		for (Node f: nodes) {
			g += f.toString() + " => ";
			for (Node t: edges.get(f).keySet()) g += t.toString() + " (" + edge(f, t) + ")  ";
			g += "\n";
		}
		return "Graph:\n" + g + "End";
	}
}
