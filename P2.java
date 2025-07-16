import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class P2 {
	public static void main(String[] args) {
		new Task().solve();
	}

	static class Task {
		public static final String INPUT_FILE = "p2.in";
		public static final String OUTPUT_FILE = "p2.out";

		static boolean[][] viz;
		static Queue<Pair> q;
		// vectori de directie
		static int[] dx = {-1, 1, 0, 0};
		static int[] dy = {0, 0, -1, 1};
		int N;
		int M;
		long K;
		long[][] m;
		long res;

		public void solve() {
			readInput();
			writeOutput(getResult());
		}

		private void readInput() {
			try {
				Scanner sc = new Scanner(new BufferedReader(new FileReader(INPUT_FILE)));
				N = sc.nextInt();
				M = sc.nextInt();
				K = sc.nextInt();

				m = new long[N][M];

				// citire matrice
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < M; j++) {
						m[i][j] = sc.nextLong();
					}
				}

				sc.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private long getResult() {
			res = 0;

			// pornesc un bfs din fiecare celula a matricei, pe rand
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					// resetez vectorul de vizitat
					viz = new boolean[N][M];
					res = Math.max(res, bfs(i, j));
				}
			}

			return res;
		}

		public long bfs(int x, int y) {
			// adaug in coada primul element, de unde incepe bfs-ul
			q = new LinkedList<>();
			q.add(new Pair(x, y, m[x][y]));
			viz[x][y] = true; // marchez ca vizitat
			long count = 1; // am o casuta
			long currMin = m[x][y]; // salvez minimul curent
			long currMax = m[x][y] + K; // salvez maximul curent

			// cat timp mai am vecini de vizitat, adica cat timp coada nu e goala
			while (!q.isEmpty()) {
				// extrag perechea din coada
				Pair p = q.poll();

				// vizitez vecinii
				for (int i = 0; i < 4; i++) {
					// obtin coordonatele vecinului
					int newX = p.x + dx[i];
					int newY = p.y + dy[i];

					// verific daca vecinul apartine granitelor matricei si
					// daca a fost vizitat sau nu
					if (newX >= 0 && newX < N && newY >= 0 && newY < M && !viz[newX][newY]) {
						// daca elementul meu curent apartine intervalului, pot
						// sa il adaug
						if (m[newX][newY] >= currMin && m[newX][newY] <= currMax) {
							q.add(new Pair(newX, newY, m[newX][newY]));
							viz[newX][newY] = true;
							count++;
						}
					}
				}
			}

			return count;
		}

		private void writeOutput(long d) {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_FILE)));

				pw.println(d);

				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// retin perechile de tip (coordonata x, y, valoare) din matrice
		static class Pair {
			int x;
			int y;
			long val;

			Pair(int x, int y, long val) {
				this.x = x;
				this.y = y;
				this.val = val;
			}
		}
	}
}
