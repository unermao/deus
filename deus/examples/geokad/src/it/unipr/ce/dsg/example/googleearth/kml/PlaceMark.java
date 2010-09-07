package it.unipr.ce.dsg.example.googleearth.kml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Marco Picone (picone.m@gmail.com)
 *
 */
public class PlaceMark {

	private String name = null;
	private Date date = null;	
	private GeographicPoint gp = null;
	private String dateString = null;
	
	public PlaceMark(Date date, GeographicPoint gp, String name )
	{
		this.gp = gp;
		this.name = name;
		this.date = date;	
		
		
		//1997-07-16T07:30:15Z
		DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		dateString = dateFormat.format(date);
		dateString = dateString.replaceAll(" ", "T");
		dateString = dateString + "Z";
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GeographicPoint getGp() {
		return gp;
	}

	public void setGp(GeographicPoint gp) {
		this.gp = gp;
	}
	
	
	
}
