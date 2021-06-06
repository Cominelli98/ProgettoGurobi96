package gruppo96;

import java.util.ArrayList;
import gurobi.*;
import gurobi.GRB.DoubleAttr;
import gurobi.GRB.IntAttr;
import gurobi.GRB.IntParam;

public class ModelloGRB {
	
	private final double PERCENTUALE_UTILIZZO_VOLUTO = 0.9;	//viene richiesta una percerntuale di utilizzo del 90% di processori e memoria

	private GRBEnv ambiente;
	private GRBModel modello;
	private EstrattoreDati dati;
	private GRBVar[][] variabili;
	private GRBVar y1;	
	private GRBVar y2;
	ArrayList<Filiale> filiali;
	ArrayList<Processo> processi = new ArrayList<>();
	
	public ModelloGRB(EstrattoreDati dati) {
		this.dati = dati;
		dati.leggiDatiInInput();
		filiali = new ArrayList<>(dati.getFiliali());
		for(int i = 0; i < filiali.size(); i++) {
            processi = new ArrayList<>(filiali.get(i).getProcessi());
		}
		variabili = new GRBVar[dati.getM_filiali()][dati.getN_configurazioni()];
	}
	
	/**
	 * Creazione del modello originario del problema
	 * @throws GRBException
	 */
	public void creaModello1() throws GRBException {
			ambiente = new GRBEnv();
			modello = new GRBModel(ambiente);
			aggiungiVariabili();
			modello.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);
			aggiungiFO();			
			aggiungiVincolo1();
			aggiungiVincolo2();
			aggiungiVincolo3();
			aggiungiVincolo4();
			aggiungivincolo5();
			aggiungiVincolo6();
	}
	
	/**
	 * Creazione di un secondo modello con stesse variabili e stessi vincoli, ma funzione obiettivo diversa,
	 * utilizzato per risolvere il terzo punto
	 * @throws GRBException
	 */
	public void creaModello2() throws GRBException {
		
		ambiente = new GRBEnv();
		modello = new GRBModel(ambiente);
		aggiungiVariabili();
		modello.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);

		AggiungiFOminProcessori();
		aggiungiVincolo1();
		aggiungiVincolo2();
		aggiungiVincolo3();
		aggiungiVincolo4();
		aggiungivincolo5();
		aggiungiVincolo6();
	}

	/**
	 * Ciclo for che crea le variabili binarie utilizzate.
	 * Sono inoltre create due variabili binarie aggiuntive
	 * @throws GRBException
	 */
	private void aggiungiVariabili() throws GRBException {
		
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < processi.size(); j++) {
				variabili[i][j] = modello.addVar(0, 1, 0, GRB.BINARY, "x(" + i + "," + j + ")");
			}
		}
		y1 = modello.addVar(0, 1, 0, GRB.BINARY, "y(1)");
		y2 = modello.addVar(0, 1, 0, GRB.BINARY, "y(2)");

	}

	/**
	 * Metodo che crea il vincolo SOMMATORIA(i,j)(a(i,j)*X(i,j)) <= k
	 * @throws GRBException
	 */
	private void aggiungiVincolo1() throws GRBException {

		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < processi.size(); j++) {
				expr.addTerm(filiali.get(i).getProcessi().get(j).getA_processori(), variabili[i][j]);
			}
		}
		modello.addConstr(expr, GRB.LESS_EQUAL, dati.getK_processori(), "V1");
	}
	
	/**
	 * Metodo che crea il vincolo SOMMATORIA(i,j)(b(i,j)*X(i,j)) <= g
	 * @throws GRBException
	 */
	private void aggiungiVincolo2() throws GRBException {

		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < processi.size(); j++) {
				expr.addTerm(filiali.get(i).getProcessi().get(j).getB_GBM(), variabili[i][j]);
			}
		}
		modello.addConstr(expr, GRB.LESS_EQUAL, dati.getG_GB(), "V2");
	}

	/**
	 * Metodo che crea il vincolo SOMMATORIA(i,j)(a(i,j)*X(i,j)) <= k*0.9*y1
	 * @throws GRBException
	 */
	private void aggiungiVincolo3() throws GRBException {

		GRBLinExpr exprSx = new GRBLinExpr();
		GRBLinExpr exprDx = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < processi.size(); j++) {
				exprSx.addTerm(filiali.get(i).getProcessi().get(j).getA_processori(), variabili[i][j]);
			}
		}
		exprDx.addTerm(PERCENTUALE_UTILIZZO_VOLUTO * dati.getK_processori(), y1);
		modello.addConstr(exprSx, GRB.GREATER_EQUAL, exprDx, "V3");
	}
	
	/**
	 * Metodo che crea il vincolo SOMMATORIA(i,j)(b(i,j)*X(i,j)) <= g*0.9*y2
	 * @throws GRBException
	 */
	private void aggiungiVincolo4() throws GRBException {

		GRBLinExpr exprSx = new GRBLinExpr();
		GRBLinExpr exprDx = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < processi.size(); j++) {
				exprSx.addTerm(filiali.get(i).getProcessi().get(j).getB_GBM(), variabili[i][j]);
			}
		}
		exprDx.addTerm(PERCENTUALE_UTILIZZO_VOLUTO * dati.getG_GB(), y2);
		modello.addConstr(exprSx, GRB.GREATER_EQUAL, exprDx, "V4");
	}
	
	/**
	 * Metodo che crea il vincolo y1+y2 >= 1
	 * @throws GRBException
	 */
	private void aggiungivincolo5() throws GRBException {
		
		GRBLinExpr expr = new GRBLinExpr();
		expr.addTerm(1, y1);
		expr.addTerm(1, y2);
		modello.addConstr(expr, GRB.GREATER_EQUAL, 1, "y1+y2>=1");
	}
	
	/**
	 * Metodo che crea il vincolo SOMMATORIA(x(i,j)) = 1 per ogni i
	 * @throws GRBException
	 */
	private void aggiungiVincolo6() throws GRBException {
		
		for (int i = 0; i < filiali.size(); i++) {
			GRBLinExpr expr = new GRBLinExpr();
			for (int j = 0; j < processi.size(); j++) {
				expr.addTerm(1, variabili[i][j]);
			}
			modello.addConstr(expr, GRB.EQUAL, 1, "V" + i + 4);
		}
	}
	
	/**
	 * Metodo che crea la funzione obiettivo MAX(SOMMATORIA(i,j)(q(i,j)*X(i,j))
	 * @throws GRBException
	 */
	private void aggiungiFO() throws GRBException {
		
		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < processi.size(); j++) {
				expr.addTerm(filiali.get(i).getProcessi().get(j).getQ_profitto(), variabili[i][j]);
			}
		}
		modello.setObjective(expr, GRB.MAXIMIZE);
	}

	public void risolvi() throws GRBException {
		
			modello.optimize();
	}
	
	/**
	 * Metodo che restituisce come stringa il valore della funzione obiettivo
	 * @param WString
	 * @return s
	 * @throws GRBException
	 */
	public StringBuffer ValoreFO(int WString) throws GRBException {
		
		StringBuffer s = new StringBuffer("");
		if(WString == 1)
			s.append("Funzione obiettivo: " + modello.get(GRB.DoubleAttr.ObjVal));
		else {
			s.append("k_min: " + modello.get(GRB.DoubleAttr.ObjVal));
		}
		return s;
	}
	
	/**
	 * Metodo che restituisce una stringa con tutte le filiali e i loro rispettivi valori di profitto
	 * @return s
	 * @throws GRBException
	 */
	public StringBuffer valoreVariabili() throws GRBException {
		
		 double[] valVars = modello.get(GRB.DoubleAttr.X, modello.getVars());
		 StringBuffer s = new StringBuffer("");
		 int j = 1;
		 for (int i = 0; i < valVars.length-2; i++) {
			if(valVars[i] == 1) {
				
				s.append("filiale_" + j + ": " + dati.getProfittiConf().get(i) + "\n");
				j++;
			}
		}
		 return s;
	}
	
	/**
	 * Metodo che ritorna una stringa con il numero di processori inutilizzati
	 * @return s
	 * @throws GRBException
	 */
	public StringBuffer processoriInutilizzati() throws GRBException {
		
		double[] valVars = modello.get(GRB.DoubleAttr.X, modello.getVars());
		StringBuffer s = new StringBuffer("");
		int somma = 0;
		for (int i = 0; i < valVars.length-2; i++) {
			if(valVars[i] == 1) {
				somma = somma + dati.getProcessoriConf().get(i);
			}
		}
		int processoriInutilizzati = dati.getK_processori() - somma;
		s.append("Processori inutilizzati: " + processoriInutilizzati);
		return s;
	}
	
	/**
	 * Metodo che ritorna una stringa con il numero di GB inutilizzati
	 * @return s
	 * @throws GRBException
	 */
	public StringBuffer gigaByteInutilizzati() throws GRBException {
		
		double[] valVars = modello.get(GRB.DoubleAttr.X, modello.getVars());
		StringBuffer s = new StringBuffer("");
		int somma = 0;
		for (int i = 0; i < valVars.length-2; i++) {
			if(valVars[i] == 1) {
				somma = somma + dati.getGbConf().get(i);
			}
		}
		int gbInutilizzati = dati.getG_GB() - somma;
		s.append("GB inutilizzati: " + gbInutilizzati);
		return s;
	}
	
	/**
	 * Metodo che restituisce una stringa con il valore delle filiali, i processori ed i GB inutilizzati di
	 * un secondo ottimo, sempre che quest'ultimo esista
	 * @return s
	 * @throws GRBException
	 */
	public StringBuffer trovaSoluzioneOttimaSecondaria() throws GRBException {
		
		StringBuffer s = new StringBuffer("");
		int conteggioSol = modello.get(IntAttr.SolCount);
		double bestvalFO = modello.get(DoubleAttr.ObjVal);

		if(conteggioSol > 1) {	//condizione che valuta se ci sono più soluzioni
			for (int i = 1; i < conteggioSol; i++) {
				modello.set(IntParam.SolutionNumber, i);
				if (bestvalFO == modello.get(DoubleAttr.PoolObjVal)) {	//condizione che valuta se il valore della funzione obiettivo della soluzione secondaria trovata è pari all'ottimo
					double[] valVars = modello.get(GRB.DoubleAttr.Xn, modello.getVars());
					 int j = 1;
					 for (int x = 0; x < valVars.length-2; x++) {
						if(valVars[j] == 1) {
							s.append("filiale_" + j + ": " + dati.getProfittiConf().get(i) + "\n");
							j++;
						}
					 }
					 int somma = 0;
						for (int l = 0; l < valVars.length-2; l++) {
							if(valVars[l] == 1) {
								somma = somma + dati.getProcessoriConf().get(l);
							}
						}
						int processoriInutilizzati = dati.getK_processori() - somma;
						s.append("Processori inutilizzati: " + processoriInutilizzati + "\n");
						somma = 0;
						for (int m = 0; m < valVars.length-2; m++) {
							if(valVars[m] == 1) {
								somma = somma + dati.getGbConf().get(m);
							}
						}
						int gbInutilizzati = dati.getG_GB() - somma;
						s.append("GB inutilizzati: " + gbInutilizzati);
					 
				}else {
					s.append("NON ESISTE");
					break;	//break utile in quanto le soluzioni secondarie sono messe in ordine di valore della funzione obiettivo, quindi se la prima non rispetta la condizione, nemmeno le altre lo faranno
				}
			}
		}else {
			s.append("NON ESISTE");
		}
		return s;
	}

	/**
	 * Metodo che crea la funzione obiettivo min(SOMMATORIA(k(i,j)*X(i,j))
	 * @throws GRBException
	 */
	private void AggiungiFOminProcessori() throws GRBException {

		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < processi.size(); j++) {
				expr.addTerm(filiali.get(i).getProcessi().get(j).getA_processori(), variabili[i][j]);
			}
		}
		modello.setObjective(expr, GRB.MINIMIZE);
	}
	
}
