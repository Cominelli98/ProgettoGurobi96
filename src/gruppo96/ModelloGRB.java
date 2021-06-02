package gruppo96;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import gurobi.*;

public class ModelloGRB {
	
	private final double PERCENTUALE_UTILIZZO_VOLUTO = 0.9;

	private GRBEnv ambiente;
	private GRBModel modello;
	private EstrattoreDati dati;
	private GRBVar[][] variabili;
	private GRBVar y1;
	private GRBVar y2;
	ArrayList<Filiale> filiali;
	
	public ModelloGRB(EstrattoreDati dati) {
		this.dati = dati;
		filiali = new ArrayList<>(dati.getFiliali());
		variabili = new GRBVar[dati.getM_filiali()][dati.getN_configurazioni()];
		try {
			dati.leggiFileInput();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		filiali = new ArrayList<>(dati.getFiliali());
	}
	
	public void creaModello() {
		try {
			ambiente = new GRBEnv();
			modificaParametri();
			modello = new GRBModel(ambiente);
			aggiungiVariabili();
			modello.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);
			aggiungiVincolo1();
			aggiungiVincolo2();
			aggiungiVincolo3();
			aggiungiVincolo4();
			aggiungivincolo5();
			aggiungiVincolo6();
			aggiungiFO();

		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	private void modificaParametri() throws GRBException
	{
	//	env.set(GRB.IntParam.Threads, 8);
	//	env.set(GRB.IntParam.Presolve, 0);
	//	env.set(IntParam.Method, 0);
	//	env.set(IntParam.Cuts, 3);
	//	env.set(IntParam.GomoryPasses,0);
	//	env.set(DoubleParam.Heuristics, 1);
	}

	private void aggiungiVariabili() throws GRBException {
		
		System.out.println(filiali.size());

		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < filiali.get(i).getProcessi().size(); j++) {
				variabili[i][j] = modello.addVar(0, 1, 0, GRB.BINARY, "x(" + i + "," + j + ")");
			}
		}
		y1 = modello.addVar(0, 1, 0, GRB.BINARY, "y(1)");
		y2 = modello.addVar(0, 1, 0, GRB.BINARY, "y(2)");

	}

	private void aggiungiVincolo1() throws GRBException {

		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < filiali.get(i).getProcessi().size(); j++) {
				expr.addTerm(filiali.get(i).getProcessi().get(j).getA_processori(), variabili[i][j]);
			}
		}
		modello.addConstr(expr, GRB.LESS_EQUAL, dati.getK_processori(), "ax<=k");
	}
	
	private void aggiungiVincolo2() throws GRBException {

		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < filiali.get(i).getProcessi().size(); j++) {
				expr.addTerm(filiali.get(i).getProcessi().get(j).getB_GBM(), variabili[i][j]);
			}
		}
		modello.addConstr(expr, GRB.LESS_EQUAL, dati.getG_GB(), "bx<=g");
	}

	private void aggiungiVincolo3() throws GRBException {

		GRBLinExpr exprSx = new GRBLinExpr();
		GRBLinExpr exprDx = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < filiali.get(i).getProcessi().size(); j++) {
				exprSx.addTerm(filiali.get(i).getProcessi().get(j).getA_processori(), variabili[i][j]);
			}
		}
		exprDx.addTerm(PERCENTUALE_UTILIZZO_VOLUTO * dati.getK_processori(), y1);
		modello.addConstr(exprSx, GRB.GREATER_EQUAL, exprDx, "ax<=0.9ky");
	}
	
	private void aggiungiVincolo4() throws GRBException {

		GRBLinExpr exprSx = new GRBLinExpr();
		GRBLinExpr exprDx = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < filiali.get(i).getProcessi().size(); j++) {
				exprSx.addTerm(filiali.get(i).getProcessi().get(j).getB_GBM(), variabili[i][j]);
			}
		}
		exprDx.addTerm(PERCENTUALE_UTILIZZO_VOLUTO * dati.getG_GB(), y2);
		modello.addConstr(exprSx, GRB.GREATER_EQUAL, exprDx, "bx<=0.9gy");
	}
	
	private void aggiungivincolo5() throws GRBException {
		
		GRBLinExpr expr = new GRBLinExpr();
		expr.addTerm(1, y1);
		expr.addTerm(1, y2);
		modello.addConstr(expr, GRB.GREATER_EQUAL, 1, "y1+y2>=1");
	}
	
	private void aggiungiVincolo6() throws GRBException {
		
		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < filiali.get(i).getProcessi().size(); j++) {
				expr.addTerm(1, variabili[i][j]);
			}
		}
		modello.addConstr(expr, GRB.EQUAL, dati.getG_GB(), "x=1");
	}
	
	private void aggiungiFO() throws GRBException {
		
		GRBLinExpr expr = new GRBLinExpr();
		for (int i = 0; i < filiali.size(); i++) {
			for (int j = 0; j < filiali.get(i).getProcessi().size(); j++) {
				expr.addTerm(filiali.get(i).getProcessi().get(j).getQ_profitto(), variabili[i][j]);
			}
		}
		modello.setObjective(expr, GRB.MAXIMIZE);
	}

	public void risolvi() {
		try
		{
			modello.optimize();
		} catch (GRBException e)
		{
			e.printStackTrace();
		}
	}
	

}
