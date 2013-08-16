import java.util.*;

// Examples of using dynamic programming
public class DP {
	// Longest Increasing Subsequence
	public static Integer[] longestIncSubseq(Integer[] seq) {
		int[] score = new int[seq.length], bt = new	int[seq.length];
		Arrays.fill(score, 1); Arrays.fill(bt, -1);
		int best = 0;
		for (int i = 1; i < seq.length; i++) {
			int last = i - 1;
			while (last >= 0 && seq[last] > seq[i]) last--;
			if (last >= 0) { score[i] = score[last] + 1; bt[i] = last; }
			if (score[best] < score[i]) best = i;
		}
		LinkedList<Integer> list = new LinkedList<Integer>();
		while(best >= 0) { list.offerFirst(seq[best]); best = bt[best]; } 
		return list.toArray(new Integer[list.size()]);
	}
	
	// Edit Distance
	public static int editDistance(String from, String to) {
		int[][] map = new int[from.length()+1][to.length()+1];
		for (int i = 0; i <= from.length(); i++) for (int j = 0; j <= to.length(); j++)
			if (i == 0) map[i][j] = j;
			else if (j == 0) map[i][j] = i;
			else {
				char f = from.charAt(i-1), t = to.charAt(j-1);
				map[i][j] = map[i-1][j-1] + (f == t ? 0 : 1);
				map[i][j] = Math.min(map[i][j], map[i-1][j] + 1);
				map[i][j] = Math.min(map[i][j], map[i][j-1] + 1);
			}
		return map[from.length()][to.length()];
	}
	
	// Knapsack (with and without repetition)
}
