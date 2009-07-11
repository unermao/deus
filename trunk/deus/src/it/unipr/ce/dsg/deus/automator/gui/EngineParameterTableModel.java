package it.unipr.ce.dsg.deus.automator.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class EngineParameterTableModel extends AbstractTableModel {

	// Array di String che contiene i nomi delle colonne
	protected String columnNames[] = { "Seed Value" };

	// Array che contiene i valori per le diverse righe nel modello della
	// tabella
	protected Object dataValues[][];

	int rowcount;

	int columncount = 1;

	private ArrayList<EngineParameter> engineParameterList;

	/**
	 * Permette di settare tramite i valori delle Mail all'iterno del modello
	 * della tabella.
	 * 
	 * @param mail_list
	 */
	public void set_FileTableModel(ArrayList<EngineParameter> engineParameterList) {

		this.engineParameterList = engineParameterList;
		
		// Create some data
		Object dataValues_app[][] = new Object[engineParameterList.size()][columncount];

		rowcount = engineParameterList.size();

		dataValues = dataValues_app;

		for (int i = 0; i < engineParameterList.size(); i++) {
			dataValues[i][0] = engineParameterList.get(i).getSeedValue();
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
		
		engineParameterList.get(row).setSeedValue((String)dataValues[row][0]);
		
		fireTableCellUpdated(row, col);
	}

	/**
	 * Stabilisce quali celle del modello sono editabili oppure no.
	 */
	public boolean isCellEditable(int row, int col) {

			return true;
	}

}