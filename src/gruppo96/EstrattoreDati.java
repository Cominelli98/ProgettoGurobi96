package gruppo96;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EstrattoreDati {
	
	private int m_filiali;
	private int n_configurazioni;
	private ArrayList<Filiale> filiali;
	private int k_processori;
	private int g_GB;
	private ArrayList<Integer> profittiConf; 		//arrayList di utilità
	private ArrayList<Integer> processoriConf;		//arrayList di utilità
	private ArrayList<Integer> gbConf;				//arrayList di utilità

	public EstrattoreDati() {
		
		filiali = new ArrayList<>();
		profittiConf = new ArrayList<>();
		processoriConf = new ArrayList<>();
		gbConf = new ArrayList<>();
	}
	
	public void leggiDatiInInput() {
		
		FileReader fr;
		try {
			fr = new FileReader("singolo96.txt");
			BufferedReader br = new BufferedReader(fr);
			leggiDatiFiliale(fr, br);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo che legge, dati il numero di filiali e il numero di configurazioni, il profitto,
	 * il numero di processori ed i GigaByte di memoria.
	 * Riempie inoltre gli array di utilità che serviranno nella classe modelloGRB
	 * @param file
	 * @param br
	 * @throws IOException
	 */
	private void leggiDatiFiliale(FileReader file, BufferedReader br) throws IOException {
		
		String[] riga;
		riga = br.readLine().split("-");
		m_filiali = Integer.parseInt(riga[0]);	//selezione del primo intero della prima riga del file > numero di fliali
		n_configurazioni = Integer.parseInt(riga[1]);	//selezione del secondo intero della prima riga del file > numero di configurazioni
		riga = br.readLine().split("-");
		k_processori = Integer.parseInt(riga[0]);	//selezione del primo intero della seconda riga del file > numero di processori e dispositivi di storage
		g_GB = Integer.parseInt(riga[1]);	//selezione del secondo intero della seconda riga del file > numero di gigaByte di memoria
		for (int i = 0; i < m_filiali; i++) {
			Filiale filiale = new Filiale();
			for (int j = 0; j < n_configurazioni; j++) {
				riga = br.readLine().split("-");
				int profitto = Integer.parseInt(riga[0]);
				profittiConf.add(Integer.parseInt(riga[0]));
				int processori = Integer.parseInt(riga[1]);
				processoriConf.add(Integer.parseInt(riga[1]));
				int gbm = Integer.parseInt(riga[2]);
				gbConf.add(Integer.parseInt(riga[2]));
				filiale.aggiungiProcesso(processori, gbm, profitto);
			}
			filiali.add(filiale);
		}
	}
	
	public ArrayList<Filiale> getFiliali() {
		
        return filiali;
    }
	
	public ArrayList<Integer> getProfittiConf() {
		
		return profittiConf;
	}
	
	public ArrayList<Integer> getGbConf() {
		
		return gbConf;
	}
	
	public ArrayList<Integer> getProcessoriConf() {
		
		return processoriConf;
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
