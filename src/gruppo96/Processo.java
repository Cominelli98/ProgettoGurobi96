package gruppo96;

public class Processo {
	
	private int a_processori; //processori
	private int b_GBM;		//GB di memoria
	private int q_profitto;	//vantaggiosità di una configurazone

	public Processo(int processori, int gbm, int profitto) {
		
		this.a_processori = processori;
		this.b_GBM = gbm;
		this.q_profitto = profitto;
	}

	public int getA_processori() {
		
		return a_processori;
	}

	public int getB_GBM() {
		
		return b_GBM;
	}

	public int getQ_profitto() {
		
		return q_profitto;
	}
}
