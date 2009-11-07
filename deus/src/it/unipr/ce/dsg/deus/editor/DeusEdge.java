package it.unipr.ce.dsg.deus.gui;

import java.io.Serializable;

/**
 * Define an edge
 * @author Fabrizio Caramia (caramia@ce.unipr.it)
 * @author Mario Sabbatelli (smario@ce.unipr.it)
 */
public class DeusEdge implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int number;
	
	public DeusEdge(){
		setNumber(0);
	}
	public void setNumber(int num){
		this.number=num;
		
	}
	
	public int getNumber(){
		
		return this.number;
	}
	
	public String toString(){
		  
		  return null;
	  }
}
