import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class P3 {
	public static void main(String[] args) {
		new Task().solve();
	}

	static class Task {
		public static final String INPUT_FILE = "p3.in";
		public static final String OUTPUT_FILE = "p3.out";

		int T; // timpul
		int N; // nr busteni
		int x, y; // coordonate MM
		int E1, E2, E3; // energiile
		Bustean[] busteni;
		List<String> res;

		public void solve() {
			readInput();
			writeOutput(getResult());
		}

		private void readInput() {
			try {
				Scanner sc = new Scanner(new BufferedReader(new FileReader(INPUT_FILE)));
				T = sc.nextInt();
				N = sc.nextInt();
				x = sc.nextInt();
				y = sc.nextInt();
				E1 = sc.nextInt();
				E2 = sc.nextInt();
				E3 = sc.nextInt();

				// citesc coordonatele bustenilor
				busteni = new Bustean[N];
				for (int i = 0; i < N; i++) {
					int xStart = sc.nextInt();
					int yStart = sc.nextInt();
					int xEnd = sc.nextInt();
					int yEnd = sc.nextInt();

					// completez ulterior si directiile in care se muta bustenii
					busteni[i] = new Bustean(xStart, yStart, xEnd, yEnd, null);
				}

				// citesc directiile in care se muta fiecare bustean
				for (int i = 0; i < N; i++) {
					String s = sc.next();
					busteni[i].mutari = s.toCharArray();
				}

				sc.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private int getResult() {
			// voi retine fiecare mutare a bustenilor la momentul t
			List<Mutari>[] mutari = new ArrayList[N];

			// parcurg bustenii
			for (int i = 0; i < N; i++) {
				// extrag coordonatele busteanului curent
				int xStartCurr = busteni[i].xStart;
				int yStartCurr = busteni[i].yStart;
				int xEndCurr = busteni[i].xEnd;
				int yEndCurr = busteni[i].yEnd;
				// il adaug ca prima mutare, adica pozitia initiala la momentul
				// t = 0
				mutari[i] = new ArrayList<>();
				mutari[i].add(new Mutari(0, xStartCurr, yStartCurr, xEndCurr, yEndCurr));

				// pentru fiecare moment t, verific unde se muta busteanul
				for (int t = 1; t <= T; t++) {
					// extrag fiecare mutare
					char mutare = busteni[i].mutari[t - 1];

					// testez ce directie va lua
					if (mutare == 'N') {
						yStartCurr++;
						yEndCurr++;
					} else if (mutare == 'S') {
						yStartCurr--;
						yEndCurr--;
					} else if (mutare == 'E') {
						xStartCurr++;
						xEndCurr++;
					} else if (mutare == 'V') {
						xStartCurr--;
						xEndCurr--;
					}

					// salvez mutarea
					mutari[i].add(new Mutari(t, xStartCurr, yStartCurr, xEndCurr, yEndCurr));
				}
			}

			// generez mutarile posibile ale lui Robin Hood, folosind Dijkstra
			// pentru a afla costul minim, adica energia minima
			PriorityQueue<Rh> pq = new PriorityQueue<>();
			int minEnergie = Integer.MAX_VALUE;
			Set<String> viz = new HashSet<>();

			// adaug pozitia initiala a lui Rh
			pq.add(new Rh(0, 0, 0, 0, new ArrayList<>()));

			while (!pq.isEmpty()) {
				Rh current = pq.poll();

				// verific daca current a fost deja vizitat
				String s = current.timp + " " + current.idxBustean + " " + current.poz;
				if (viz.contains(s)) {
					continue;
				} else {
					viz.add(s);
				}

				// extrag busteanul curent si mutarea lui
				Bustean busteanCurrent = busteni[current.idxBustean];
				Mutari mutareCurrent = mutari[current.idxBustean].get(current.timp);

				// calculez coordonatele poztiei lui Rh
				int xRh = 0, yRh = 0;
				// verific daca busteanul e veritical sau orizontal
				if (busteanCurrent.xStart == busteanCurrent.xEnd) {
					// busteanul e vertical
					xRh = mutareCurrent.x1;
					yRh = mutareCurrent.y1 + current.poz;
				} else if (busteanCurrent.yStart == busteanCurrent.yEnd) {
					// busteanul e orizontal
					xRh = mutareCurrent.x1 + current.poz;
					yRh = mutareCurrent.y1;
				}

				// verific daca RH a ajuns la MM
				if (xRh == x && yRh == y) {
					// calculez energia minima
					if (current.energie < minEnergie) {
						minEnergie = current.energie;
						res = current.moves;
					}

					continue;
				}

				// nu mai am timp sa ajung la MM
				if (current.timp >= T) {
					continue;
				}

				// simulez primul fel de mutare, RH sta pe loc
				List<String> newMoves = new ArrayList<>(current.moves);
				newMoves.add("H");
				pq.add(new Rh(current.timp + 1, current.idxBustean, current.poz,
						current.energie + E1, newMoves));

				// simulez al doilea fel de mutare, N, S, E, V
				int lungime = 0;
				// calculez lungimea busteanului
				if (busteanCurrent.xStart == busteanCurrent.xEnd) {
					lungime = Math.abs(busteanCurrent.yEnd - busteanCurrent.yStart);
				} else {
					lungime = Math.abs(busteanCurrent.xEnd - busteanCurrent.xStart);
				}

				// verific daca busteanul e veritical sau orizontal
				if (busteanCurrent.xStart == busteanCurrent.xEnd) {
					// bustean orizontal
					// RH poate merge in sus, adica N, daca nu se afla deja la
					// yEnd, adica lungime = lungime
					if (current.poz < lungime) {
						newMoves = new ArrayList<>(current.moves);
						newMoves.add("N");
						pq.add(new Rh(current.timp + 1, current.idxBustean,
								current.poz + 1, current.energie + E2, newMoves));
					}

					// RH poate merge in jos, adica S, daca nu se afla la
					// xStart, adica lungime = 0
					if (current.poz > 0) {
						newMoves = new ArrayList<>(current.moves);
						newMoves.add("S");
						pq.add(new Rh(current.timp + 1, current.idxBustean,
								current.poz - 1, current.energie + E2, newMoves));
					}
				} else if (busteanCurrent.yStart == busteanCurrent.yEnd) {
					// RH poate merge la dreapta, adica E, daca nu se afla deja
					// la xEnd, adica lungime = lungime
					if (current.poz < lungime) {
						newMoves = new ArrayList<>(current.moves);
						newMoves.add("E");
						pq.add(new Rh(current.timp + 1, current.idxBustean,
								current.poz + 1, current.energie + E2, newMoves));
					}

					// RH poate merge la stanga, adica V, daca nu se afla deja
					// la xStart, adica lungime = 0
					if (current.poz > 0) {
						newMoves = new ArrayList<>(current.moves);
						newMoves.add("V");
						pq.add(new Rh(current.timp + 1, current.idxBustean,
								current.poz - 1, current.energie + E2, newMoves));
					}
				}

				// simulez al treilea fel de mutare, RH sare pe alt bustean
				for (int i = 0; i < N; i++) {
					// gasesc busteanul curent, sar peste
					if (i == current.idxBustean) {
						continue;
					}

					// verific daca se intersecteaza bustenii
					Mutari altaMutare = mutari[i].get(current.timp);
					int newPoz = 0;
					// verific daca busteanul e vertical sau orizontal
					if (altaMutare.x1 == altaMutare.x2) {
						// busteanul e vertical
						if (xRh == altaMutare.x1 && yRh >= altaMutare.y1 && yRh <= altaMutare.y2) {
							// RH sare pe alt bustean, recalculez pozitia
							newPoz = yRh - altaMutare.y1;
						} else {
							continue;
						}
					} else if (altaMutare.y1 == altaMutare.y2) {
						// busteanul e orizontal
						if (yRh == altaMutare.y1 && xRh >= altaMutare.x1 && xRh <= altaMutare.x2) {
							// RH sare pe alt bustean, recalculez pozitia
							newPoz = xRh - altaMutare.x1;
						} else {
							continue;
						}
					}

					newMoves = new ArrayList<>(current.moves);
					newMoves.add("J " + (i + 1));
					pq.add(new Rh(current.timp + 1, i, newPoz,
							current.energie + E3, newMoves));
				}
			}

			return minEnergie;
		}

		private void writeOutput(int d) {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_FILE)));

				// afisez rezultatul
				if (d == Integer.MAX_VALUE) {
					// nu a ajuns la MM
					pw.println(-1);
				} else {
					// afisez energia minima, numarul de mutari si mutarile
					pw.println(d);
					pw.println(res.size());
					for (String s : res) {
						pw.println(s);
					}
				}

				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// retin coordonatele bustenilor si directiile in care se vor muta
		static class Bustean {
			int xStart, yStart, xEnd, yEnd;
			char[] mutari;

			public Bustean(int xStart, int yStart, int xEnd, int yEnd, char[] mutari) {
				this.xStart = xStart;
				this.yStart = yStart;
				this.xEnd = xEnd;
				this.yEnd = yEnd;
				this.mutari = mutari;
			}
		}

		// ma va ajuta sa retin fiecare mutare pe care o va face un bustean
		static class Mutari {
			int timp;
			int x1, y1, x2, y2;

			public Mutari(int timp, int x1, int y1, int x2, int y2) {
				this.timp = timp;
				this.x1 = x1;
				this.y1 = y1;
				this.x2 = x2;
				this.y2 = y2;
			}
		}

		// ma va ajuta sa gestionez mai usor mutarile lui RH pe busteni
		static class Rh implements Comparable<Rh> {
			int timp, idxBustean, poz, energie;
			List<String> moves;

			public Rh(int timp, int idxBustean, int poz, int energie) {
				this.timp = timp;
				this.idxBustean = idxBustean;
				this.poz = poz;
				this.energie = energie;
			}

			public Rh(int timp, int idxBustean, int poz, int energie, List<String> moves) {
				this.timp = timp;
				this.idxBustean = idxBustean;
				this.poz = poz;
				this.energie = energie;
				this.moves = moves;
			}

			// pentru coada cu prioritate, pentru a obtine energia minima
			@Override
			public int compareTo(Rh other) {
				return Integer.compare(this.energie, other.energie);
			}
		}
	}
}