package gruppo96;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import gurobi.GRBException;

public final class WriteTXT {
	
	/**
	 * Metodo che crea un file e ci scrive tutto il necessario rispettando la consegna
	 * @throws IOException
	 * @throws GRBException
	 */
	
	public static void ScriviTXT() throws IOException, GRBException {
		
		EstrattoreDati ed = new EstrattoreDati();
		ModelloGRB modello = new ModelloGRB(ed);
		modello.creaModello1();
		modello.risolvi();
		File f = new File("Risposte_Gruppo96.txt");
		if(f.exists()) {
			f.delete();
			f = new File("Risposte_Gruppo96.txt");
		}
		PrintWriter scrivi = new PrintWriter(f);
		scrivi.println("GRUPPO 96 - COMINELLI ALEX\n");
		scrivi.println("\nQUESITO 1:");
		scrivi.println(modello.ValoreFO(1));
		scrivi.print(modello.valoreVariabili());
		scrivi.println(modello.processoriInutilizzati());
		scrivi.println(modello.gigaByteInutilizzati());
		scrivi.println("\n\nQUESITO 2:");
		scrivi.println(modello.trovaSoluzioneOttimaSecondaria());
		scrivi.println("\n\nQUESITO 3:");
		modello.creaModello2();
		modello.risolvi();
		scrivi.println(modello.ValoreFO(2));
		scrivi.close();
	}
}
