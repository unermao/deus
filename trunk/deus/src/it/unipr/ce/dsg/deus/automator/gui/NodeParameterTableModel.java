package it.unipr.ce.dsg.deus.automator.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class NodeParameterTableModel extends AbstractTableModel {

	// Array di String che contiene i nomi delle colonne
	protected String columnNames[] = { "Node Id","Param Name","Initial Value", "Final Value" ,"Step Value"};

	// Array che contiene i valori per le diverse righe nel modello della
	// tabella
	protected Object dataValues[][];

	int rowcount;

	int columncount = 5;

	private ArrayList<NodeParameter> nodeParametersList;

	public void set_FileTableModel(ArrayList<NodeParameter> nodeParametersList) {

		this.nodeParametersList = nodeParametersList;
		
		// Create some data
		Object dataValues_app[][] = new Object[nodeParametersList.size()][columncount];

		rowcount = nodeParametersList.size();

		dataValues = dataValues_app;

		for (int i = 0; i < nodeParametersList.size(); i++) {
			dataValues[i][0] = nodeParametersList.get(i).getNodeId();
			dataValues[i][1] = nodeParametersList.get(i).getParamName();
			dataValues[i][2] = nodeParametersList.get(i).getInitialValue();
			dataValues[i][3] = nodeParametersList.get(i).getFinalValue();
			dataValues[i][4] = nodeParametersList.get(i).getStepValue();
		}
	}
	
	/**
	 * Restituisce il numero di righe del modello
	 */
	public int getRowCount() {
		// TODO Auto-generated method stub
		return rowcount;
	}

	/**
	 * Restituisce il nome della colonna in base al valore della colonna
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Restituisce il tipo di oggetto in base al valore della colonna
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/**
	 * Restituisce il numero di colonne presenti nel modello
	 */
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columncount;
	}

	/**
	 * Restituisce l'oggetto presente nel modello alle posizioni passate alla
	 * funzione.
	 */
	public Object getValueAt(int row, int col) {
		
		return dataValues[row][col];
	}
	
	/**
	 * Restituisce l'oggetto presente nel modello alle posizioni passate alla
	 * funzione.
	 */
	public Object getRowObject(int row ) {
		
		return (String)dataValues[row][0];
	}


	/**
	 * Permette di impostare il valore di un oggetto nel modello alla posizione
	 * indicata dai valori passati alla funzione.
	 */
	public void setValueAt(Object value, int row, int col) {
		dataValues[row][col] = value;
		
		nodeParametersList.get(row).setNodeId((String)dataValues[row][0]);
		nodeParametersList.get(row).setParamName((String)dataValues[row][1]);
		nodeParametersList.get(row).setInitialValue((Double)dataValues[row][2]);
		nodeParametersList.get(row).setFinalValue((Double)dataValues[row][3]);
		nodeParametersList.get(row).setStepValue((Double)dataValues[row][4]);

		fireTableCellUpdated(row, col);
	}

	/**
	 * Stabilisce quali celle del modello sono editabili oppure no.
	 */
	public boolean isCellEditable(int row, int col) {

			return true;
	}

}