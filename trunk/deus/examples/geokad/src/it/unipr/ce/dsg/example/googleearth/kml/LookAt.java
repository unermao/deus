package it.unipr.ce.dsg.example.googleearth.kml;

/**
 * 
 * @author Marco Picone (picone.m@gmail.com)
 *
 */
public class LookAt {

	private GeographicPoint gp = null;
	private String range = null;
	private String tilt = null;
	private String heading = null;
	
	public LookAt(GeographicPoint gp, String range, String tilt, String heading) {
		super();
		this.gp = gp;
		this.range = range;
		this.tilt = tilt;
		this.heading = heading;
	}
	
	public GeographicPoint getGp() {
		return gp;
	}
	public void setGp(GeographicPoint gp) {
		this.gp = gp;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getTilt() {
		return tilt;
	}
	public void setTilt(String tilt) {
		this.tilt = tilt;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	
	
	
}
