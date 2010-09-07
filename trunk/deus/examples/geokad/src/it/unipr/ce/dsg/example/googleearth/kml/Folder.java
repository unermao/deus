package it.unipr.ce.dsg.example.googleearth.kml;

import java.util.ArrayList;

public class Folder {

	private String name = null;
	private String open = null;
	private String description = null;
	private LookAt lookAt = null;
	private String listItemType = null;
	
	private ArrayList<PlaceMark> plmList = null;

	public Folder(String name, String open, String description, LookAt lookAt,
			String listItemType, ArrayList<PlaceMark> plmList) {
		super();
		this.name = name;
		this.open = open;
		this.description = description;
		this.lookAt = lookAt;
		this.listItemType = listItemType;
		this.plmList = plmList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LookAt getLookAt() {
		return lookAt;
	}

	public void setLookAt(LookAt lookAt) {
		this.lookAt = lookAt;
	}

	public String getListItemType() {
		return listItemType;
	}

	public void setListItemType(String listItemType) {
		this.listItemType = listItemType;
	}

	public ArrayList<PlaceMark> getPlmList() {
		return plmList;
	}

	public void setPlmList(ArrayList<PlaceMark> plmList) {
		this.plmList = plmList;
	}
	
	
}
