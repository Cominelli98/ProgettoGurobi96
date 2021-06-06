package gruppo96;

import java.util.ArrayList;


public class Filiale {
	
	private ArrayList<Processo> processi;
	 
	
	public Filiale() {
		
		processi = new ArrayList<>();
	}
	
	public void aggiungiProcesso(int processori, int gbm, int profitto) {
		
		processi.add(new Processo(processori, gbm, profitto));
	}
	
	public ArrayList<Processo> getProcessi(){
		
		return processi;
	}
	
	public int dimensioneProcessi() {
		
		int size = processi.size();
		return size;
	}

}
