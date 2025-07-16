import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class P1 {
	public static void main(String[] args) {
		new Task().solve();
	}

	static class Task {
		public static final String INPUT_FILE = "p1.in";
		public static final String OUTPUT_FILE = "p1.out";

		// nr noduri
		int N;
		// vectorul de distante
		int[] v;
		// salvare rezultat, muchii
		List<int[]> res;

		public void solve() {
			readInput();
			writeOutput(getResult());
		}

		private void readInput() {
			try {
				Scanner sc = new Scanner(new BufferedReader(new FileReader(INPUT_FILE)));
				// citire nr noduri
				N = sc.nextInt();

				// citire vector distante
				v = new int[N + 1];

				// unde voi retine muchiile rezultate
				res = new ArrayList<>();

				for (int i = 1; i <= N; i++) {
					v[i] = sc.nextInt();
				}

				sc.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private int getResult() {
			// voi retine aici nodurile asociate unei distante
			// de ex: dist 1 -> nodurile {2, 3}
			Map<Integer, List<Integer>> distMap = new TreeMap<>();

			// populez TreeMap-ul
			for (int i = 1; i <= N; i++) {
				// primeste distanta
				int dist = v[i];

				// daca contine deja distanta curenta, va adauga la perechea
				// deja creata urmatorul nod
				if (!distMap.containsKey(dist)) {
					distMap.put(dist, new ArrayList<>());
				}
				// altfel creeaza cheia cu distanta curenta si adauga nodul
				distMap.get(dist).add(i);
			}

			// daca nu contine cheia 0, atunci nu exista nodul 1 (distanta e
			// mereu 0 in nodul 1), graful e invalid sau daca contine doar nodul
			// 1, din nou e un graf invalid, nu am ce muchii crea sau daca am mai
			// multe noduri cu distanta 0, nu pot avea un graf valid
			if (!distMap.containsKey(0) || distMap.get(0).size() != 1
					|| distMap.get(0).get(0) != 1) {
				return -1;
			}

			// pornesc din nodul 1
			int startNode = 1;
			int lastNode;

			// parcurg TreeMap-ul
			for (Map.Entry<Integer, List<Integer>> entry : distMap.entrySet()) {
				// extrag distanta si lista de noduri asociata acestei distante
				int distanta = entry.getKey();
				List<Integer> noduri = entry.getValue();

				// daca distanta e 0, sunt la nodul 1, sar peste
				if (distanta == 0) {
					continue;
				}

				// daca nu contine distanta precedenta sau nu are niciun nod
				// asociat, nu pot crea un graf valid, adica 0 2 2, nu e valid
				if (!distMap.containsKey(distanta - 1) || distMap.get(distanta - 1).isEmpty()) {
					return -1;
				}

				// prima data leg nodurile de nodul 1
				if (distanta == 1) {
					for (int node : noduri) {
						res.add(new int[]{startNode, node});
					}
				} else {
					// interschimb nodul de start pentru a nu lega toate
					// nodurile de un singur nod
					int index = 0;
					lastNode = distMap.get(distanta - 1).get(index);
					for (int node : noduri) {
						// adaug muchiile
						res.add(new int[]{lastNode, node});
						// trec la un alt nod de care voi lega urmatorul nod
						index++;
						if (index == distMap.get(distanta - 1).size()) {
							index = 0;
						}
						// actualizez nodul de unde incepe muchia
						lastNode = distMap.get(distanta - 1).get(index);
					}
				}
			}

			return res.size();
		}

		private void writeOutput(int d) {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_FILE)));

				// afisez cate muchii am sau -1 daca nu am reusit sa obtin un
				// graf valid
				pw.println(d);
				// aici afisez muchiile daca graful e valid
				if (d != -1) {
					for (int[] edge : res) {
						pw.println(edge[0] + " " + edge[1]);
					}
				}

				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
