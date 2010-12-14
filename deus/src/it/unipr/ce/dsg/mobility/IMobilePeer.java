package it.unipr.ce.dsg.mobility;

/**
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public interface IMobilePeer {

	/**
	 * Initialization of mobility model management
	 * 
	 * @param triggeringTime
	 * @param mobilityModel
	 */
	public void mobilityInit(float triggeringTime, ISpeedModel mobilityModel);
	
	/**
	 * 
	 * @param triggeringTime
	 */
	public void scheduleMove(float triggeringTime);
	

	/**
	 * 
	 * @param triggeringTime
	 */
	public void move(float triggeringTime);

	/**
	 * 
	 */
	public void configureMobilityModelParameter();
	
	/**
	 * Conversion of sec value into VT value
	 * 
	 * @param secValue
	 * @return
	 */
	public double sec2VT(double secValue);
	
	/**
	 * Conversion of VT value into sec value
	 * 
	 * @param vtValue
	 * @return
	 */
	public double vt2Sec(double vtValue); 
	
	/**
	 * 
	 * @param actualSS
	 * @return
	 */
	public MobilityPath selectNextSwitchSation(SwitchStation actualSS);
}
