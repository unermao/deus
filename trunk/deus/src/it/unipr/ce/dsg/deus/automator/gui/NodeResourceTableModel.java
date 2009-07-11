package it.unipr.ce.dsg.deus.automator.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class NodeResourceTableModel extends AbstractTableModel {

	// Array di String che contiene i nomi delle colonne
	protected String columnNames[] = { "Node Id","Handler Name","ResParam Value", "Initial Value","Final Value" ,"Step Value"};

	// Array che contiene i valori per le diverse righe nel modello della
	// tabella
	protected Object dataValues[][];

	int rowcount;

	int columncount = 6;

	private ArrayList<NodeResource> nodeResourceList;

	/**
	 * Permette di settare tramite i valori delle Mail all'iterno del modello
	 * della tabella.
	 * 
	 * @param mail_list
	 */
	public void set_FileTableModel(ArrayList<NodeResource> nodeResourceList) {

		this.nodeResourceList = nodeResourceList;
		
		// Create some data
		Object dataValues_app[][] = new Object[nodeResourceList.size()][columncount];

		rowcount = nodeResourceList.size();

		dataValues = dataValues_app;

		for (int i = 0; i < nodeResourceList.size(); i++) {
			dataValues[i][0] = nodeResourceList.get(i).getNodeId();
			dataValues[i][1] = nodeResourceList.get(i).getHandlerName();
			dataValues[i][2] = nodeResourceList.get(i).getResParamValue();
			dataValues[i][3] = nodeResourceList.get(i).getInitialValue();
			dataValues[i][4] = nodeResourceList.get(i).getFinalValue();
			dataValues[i][5] = nodeResourceList.get(i).getStepValue();
		}
	}
	
	/**
	 * Restituisce il numero di righe del modello
	 */
	public int getRowCount() {
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
		
		nodeResourceList.get(row).setNodeId((String)dataValues[row][0]);
		nodeResourceList.get(row).setHandlerName((String)dataValues[row][1]);
		nodeResourceList.get(row).setResParamValue((String)dataValues[row][2]);
		nodeResourceList.get(row).setInitialValue((Double)dataValues[row][3]);
		nodeResourceList.get(row).setFinalValue((Double)dataValues[row][4]);
		nodeResourceList.get(row).setStepValue((Double)dataValues[row][5]);
		
		fireTableCellUpdated(row, col);
	}

	/**
	 * Stabilisce quali celle del modello sono editabili oppure no.
	 */
	public boolean isCellEditable(int row, int col) {

			return true;
	}

}