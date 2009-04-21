package it.unipr.ce.dsg.deus.automator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResultAutomator {

	private ArrayList<String> filesList = null;
	private ArrayList<VTResults> readedResults = new ArrayList<VTResults>();
	private ArrayList<VTResults> finalResults = new ArrayList<VTResults>();
	
	public ResultAutomator(ArrayList<String> filesList) {
		super();
		this.filesList = filesList;
	}

	/**
	 * Funzione per leggere tutti i file su cui fare la media
	 * @throws IOException
	 */
	public void readTotalResults() throws IOException
	{
		for(int index = 0; index < this.filesList.size(); index++)
			this.readFileResults(this.filesList.get(index));
	}
	
	/**
	 * Funzione che si occupa di creare degli oggetti Result su cui poi efettua le medie
	 * @param fileName , nome del file da leggere
	 * @throws IOException
	 */
	public void readFileResults(String fileName) throws IOException{
		
		VTResults vtResultValue = null;
		
		File f = new File(fileName);
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		
		String linea = br.readLine();
		
		while(linea!=null) {
		       
		       String[] lineSlipts = linea.split("=");
		       
		       if(	lineSlipts.length == 2 )
		       {   
		    	   String name = lineSlipts[0];
		    	   Double value = Double.parseDouble(lineSlipts[1]);
		    	   
		    	   if(name.equals("VT")) {
		    		   if(vtResultValue != null && !vtResultValue.getVT().equals(value))           
		    		   {                              
		    			   this.readedResults.add(vtResultValue);           
		               }
		               
		               if(vtResultValue == null)
		            	   vtResultValue = new VTResults(value);
		               else if(!vtResultValue.getVT().equals(value))
		            	   vtResultValue = new VTResults(value);
		    		   /*
		    		   if(vtResultValue != null)
		    		   {   
		    			   this.readedResults.add(vtResultValue);
		    		   }
		    		   
		    		   vtResultValue = new VTResults(value);
		    		   */
		    	   }
		    	   else{
		    		   vtResultValue.addResult(new Result(name,value));
		    	   }
		    		   
		       }
		       else
		       {
		    	   //TODO Aggiungere invio eccezione
		    	   System.out.println("ERRORE NUMERO DI PARAMETRI LINEA !!!");
		       }
		    	   
		       linea=br.readLine();
		}
		
		if(vtResultValue != null)
		   this.readedResults.add(vtResultValue);
		
				
		fis.close();
		
	}
	
	
	/**
	 * Funzione che effettua la media 
	 * @param fileName
	 */
	public void resultsAverage(String fileName){
		
		for(int index = 0 ; index < this.readedResults.size(); index++)
		{	
			
			if(!this.finalResults.contains(this.readedResults.get(index)))
				this.finalResults.add(this.readedResults.get(index));
					
			else
			{
			//Recupero l'elemento da modificare
			int position = this.finalResults.indexOf(new VTResults(this.readedResults.get(index).getVT()));
			
			VTResults vtResult = this.finalResults.get(position);

			for(int resultIndex = 0 ; resultIndex < this.readedResults.get(index).getVtResultsList().size(); resultIndex++ )
			{
				Result result = this.readedResults.get(index).getVtResultsList().get(resultIndex);
					
				vtResult.getVtResultsList().get(resultIndex).addToValue(result.getValue());
			}
			}
			
		}
		
		//Media finali e scrittura	
	try {
		
		if(new File(fileName).exists())
			new File(fileName).delete();
		
		FileOutputStream file = new FileOutputStream(fileName);
				
		for( int i = 0 ; i < this.finalResults.size(); i++ ){
			
			String outputLine = "VT=" + this.finalResults.get(i).getVT() + "\n";
			
			for(int resultIndex = 0 ; resultIndex < this.finalResults.get(i).getVtResultsList().size(); resultIndex++ )
			{
				
				Result result = this.finalResults.get(i).getVtResultsList().get(resultIndex);
	
				String name = result.getName();
				
				Double value= result.getValue()/((double)this.filesList.size());
				outputLine = outputLine + name + "=" + value + "\n";
				
			}
					
			try {
				file.write(outputLine.getBytes());
				outputLine = "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
				
	}
		
	/**
	 * Funzione che effettua la varianza
	 * @param fileName
	 * @param averageFileName
	 * @throws IOException
	 */
	public void resultsVar(String fileName, String fileName2, String averageFileName) throws IOException{
		
		this.readedResults.clear();
		this.finalResults.clear();
		
		this.readTotalResults();
		
		ArrayList<VTResults> averageResults = readAverageResults(averageFileName);
		
		for(int index = 0 ; index < this.readedResults.size(); index++)
		{																		
			Double average;
			if(!this.finalResults.contains(this.readedResults.get(index)))
				{
				int position = averageResults.indexOf(new VTResults(this.readedResults.get(index).getVT()));
				
				VTResults vtResult = this.readedResults.get(index);
				for(int resultIndex = 0 ; resultIndex < this.readedResults.get(index).getVtResultsList().size(); resultIndex++ )
				 {
					Result result = this.readedResults.get(index).getVtResultsList().get(resultIndex);
					average = averageResults.get(position).getVtResultsList().get(resultIndex).getValue();
					vtResult.getVtResultsList().get(resultIndex).setValue(Math.pow(result.getValue() - average, 2.0));
				 }
				 this.finalResults.add(vtResult);				
				}
			
			else
			{
			//Recupero l'elemento da modificare
			int position = this.finalResults.indexOf(new VTResults(this.readedResults.get(index).getVT()));
			int position2 = averageResults.indexOf(new VTResults(this.readedResults.get(index).getVT()));
			
			VTResults vtResult = this.finalResults.get(position);

			for(int resultIndex = 0 ; resultIndex < this.readedResults.get(index).getVtResultsList().size(); resultIndex++ )
			{
				Result result = this.readedResults.get(index).getVtResultsList().get(resultIndex);
				average = averageResults.get(position2).getVtResultsList().get(resultIndex).getValue();
				vtResult.getVtResultsList().get(resultIndex).addToValue((Math.pow(result.getValue() - average, 2.0)));
			}
			}
			
		}
		
		//Varianza finale e scrittura
		 try {
			if(new File(fileName).exists())
				 new File(fileName).delete();
			
			if(new File(fileName2).exists())
				 new File(fileName2).delete();
			
			FileOutputStream file = new FileOutputStream(fileName);
			FileOutputStream file2 = new FileOutputStream(fileName2);
			 		
			for( int i = 0 ; i < this.finalResults.size(); i++ ){
			
			String outputLine = "VT=" + this.finalResults.get(i).getVT() + "\n";
			String outputLine2 = "VT=" + this.finalResults.get(i).getVT() + "\n";
			
			for(int resultIndex = 0 ; resultIndex < this.finalResults.get(i).getVtResultsList().size(); resultIndex++ )
			{
				
				Result result = this.finalResults.get(i).getVtResultsList().get(resultIndex);
	
				String name = result.getName();
				Double value= result.getValue()/((double)this.filesList.size());
				
				outputLine2 = outputLine2 + name + "=" + Math.sqrt(value) + "\n";
				outputLine = outputLine + name + "=" + value + "\n";
			}
			
			try {
				
				file.write(outputLine.getBytes());
				file2.write(outputLine2.getBytes());
				outputLine = "";
				outputLine2 = "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		
	}
	
	/**
	 * Funzione che effettua una media di appoggio, utilizzata per calcolare la varianza
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public ArrayList<VTResults> readAverageResults(String fileName) throws IOException{
		
		ArrayList<VTResults> averageResults = new ArrayList<VTResults>();
	
		VTResults vtResultValue = null;
		
		File f = new File(fileName);
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		
		String linea = br.readLine();
		
		while(linea!=null) {
		       
		       String[] lineSlipts = linea.split("=");
		       
		       if(	lineSlipts.length == 2 )
		       {   
		    	   String name = lineSlipts[0];
		    	   Double value = Double.parseDouble(lineSlipts[1]);
		    	   
		    	   if(name.equals("VT")){
		    		   
		    		   if(vtResultValue != null)
		    		   {   
		    			   averageResults.add(vtResultValue);
		    		   }
		    		   
		    		   vtResultValue = new VTResults(value);
		    		   
		    	   }
		    	   else{
		    		   vtResultValue.addResult(new Result(name,value));
		    	   }
		    		   
		       }
		       else
		       {
		    	   //TODO Aggiungere invio eccezione
		    	   System.out.println("ERRORE NUMERO DI PARAMETRI LINEA !!!");
		       }
		    	   
		       linea=br.readLine();
		}
		
		if(vtResultValue != null)
			averageResults.add(vtResultValue);
		
		fis.close();
		
		return averageResults;
	}
		
	public ArrayList<String> getFilesList() {
		return filesList;
	}

	public void setFilesList(ArrayList<String> filesList) {
		this.filesList = filesList;
	}

	public ArrayList<VTResults> getFinalResults() {
		return finalResults;
	}
	
}
