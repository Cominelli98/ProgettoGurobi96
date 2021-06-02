package gruppo96;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class EstrattoreDati {
	
	private int m_filiali;
	private int n_configurazioni;
	private ArrayList<Filiale> filiali;
	private int k_processori;
	private int g_GB;
	
	public EstrattoreDati() {
		
		filiali = new ArrayList<>();
	}
	
	public void leggiFileInput() throws FileNotFoundException {
		
		FileReader file;
		file = new FileReader("singolo96.txt");
		BufferedReader br;
		br = new BufferedReader(file);
		try {
			leggiDatiIniziali(file, br);
		} catch (IOException e) {
			System.out.println("Errore lettura file");
		}
		try {
			leggiDatiFiliale(file, br);
		} catch (IOException e) {
			System.out.println("Errore lettura file");
		}
		
	}
	
	private void leggiDatiIniziali(FileReader file, BufferedReader br) throws IOException {
		
		String[] riga;
		riga = br.readLine().split("-");
		m_filiali = Integer.parseInt(riga[0]);				//selezione del primo intero della prima riga del file > numero di fliali
		n_configurazioni = Integer.parseInt(riga[1]);		//selezione del secondo intero della prima riga del file > numero di configurazioni
		riga = br.readLine().split("-");
		k_processori = Integer.parseInt(riga[0]);	//selezione del primo intero della seconda riga del file > numero di processori e dispositivi di storage
		g_GB = Integer.parseInt(riga[1]);					//selezione del secondo intero della seconda riga del file > numero di gigaByte di memoria
	}
	
	
	/**
	 * Metodo che legge, dati il numero di filiali e il numero di configurazioni, il profitto,
	 * il numero di processori ed i GigaByte di memoria
	 * @param file
	 * @param br
	 * @throws IOException
	 */
	
	public void leggiDatiFiliale(FileReader file, BufferedReader br) throws IOException {
		
		String[] riga;
		for (int i = 0; i < m_filiali; i++) {
			Filiale filiale = new Filiale();
			for (int j = 0; j < n_configurazioni; j++) {
				riga = br.readLine().split("-");
				int profitto = Integer.parseInt(riga[0]);
				int processori = Integer.parseInt(riga[1]);
				int gbm = Integer.parseInt(riga[2]);
				filiale.aggiungiProcesso(processori, gbm, profitto);
			}
			filiali.add(filiale);
		}
	}
	
	public ArrayList<Filiale> getFiliali(){
		
        return filiali;
    }
	
	public int getM_filiali() {
		return m_filiali;
	}

	public int getN_configurazioni() {
		return n_configurazioni;
	}

	public int getK_processori() {
		return k_processori;
	}

	public int getG_GB() {
		return g_GB;
	}


}
