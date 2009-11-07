package it.unipr.ce.dsg.deus.automator;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class AutomatorLogger {
	
	private FileOutputStream file;
	
	/**
	 * constructor of the AutomatorLogger
	 * @param fileName, name of the file on which the results must be written
	 */
	public AutomatorLogger(String fileName) {
		super();			
		 
		try {				
			this.file = new FileOutputStream(fileName,true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that writes on the logger file
	 * @param f, time of the simulator
	 * @param fileValue, set of couples <name,values> to write on the file 
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
			e.printStackTrace();
		}
		
	}	
	
}
