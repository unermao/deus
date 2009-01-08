package it.unipr.ce.dsg.deus.automator;


/**
 * Eccezione lanciata nel caso in cui i punti necessari per disegnare una figura siano insufficenti
 * @author marcopk
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
