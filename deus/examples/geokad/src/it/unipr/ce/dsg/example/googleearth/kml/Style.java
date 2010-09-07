package it.unipr.ce.dsg.example.googleearth.kml;

/**
 * 
 * @author Marco Picone (picone.m@gmail.com)
 *
 */
public class Style {

	private String id = null;
	private String iconHref = null;
	
	public Style(String id, String iconHref) {
		this.id = id;
		this.iconHref = iconHref;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIconHref() {
		return iconHref;
	}

	public void setIconHref(String iconHref) {
		this.iconHref = iconHref;
	}

	
}
