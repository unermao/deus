package it.unipr.ce.dsg.deus.automator;


/**
 * Exception that is thrown when the points that are necessary to draw a figure are not sufficient
 *  * @author marcopk
 *
 */
public class DeusAutomatorException extends Exception {

	String error = "";
	
	public DeusAutomatorException( String error ){
		this.error = error;
	}
	
	public String toString(){
		return (this.error);
	}
	
}
