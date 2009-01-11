package it.unipr.ce.dsg.deus.automator;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class AutomatorLogger {
	
	private FileOutputStream file;
	
	/**
	 * Costruttore dell'AutomatorLogger
	 * @param fileName, nome del file su cui scrivere i risultati
	 */
	public AutomatorLogger(String fileName) {
		super();			
		 
		try {				
			this.file = new FileOutputStream(fileName,true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Fucnzione che scrive sul file di logger
	 * @param f, Tempo del simulatore
	 * @param fileValue, insieme dei parametri <nome,valore> da scrivere sul file 
	 */
	public void write(float f, ArrayList<LoggerObject> fileValue){				
		
		String write = "";
		
		String vt = "VT=" + f + "\n";
		
		String toWrite = "";
		
		for(int i = 0; i < fileValue.size(); i++)
			toWrite = toWrite + fileValue.get(i).getDataName() + "=" + fileValue.get(i).getDataValue() + "\n";
		
		write = vt + toWrite;
		
		try {
			
			this.file.write(write.getBytes());
			this.file.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
}
