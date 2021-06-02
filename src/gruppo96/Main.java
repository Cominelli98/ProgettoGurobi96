package gruppo96;

import java.io.FileNotFoundException;


public class Main {

	public static void main(String[] args) {

		EstrattoreDati ed = new EstrattoreDati();
		try {
			ed.leggiFileInput();
		} catch (FileNotFoundException e) {}
		
		System.out.println(ed.getFiliali().size());
		
		ModelloGRB modello = new ModelloGRB(ed);
		modello.creaModello();
		modello.risolvi();
		

	}
}
