package org.ncsu.cs.edu.models;

import java.util.ArrayList;

public class Type {
	private String typeid;
	
	private String sname;
	
	private String catId;
	
	private ArrayList<String> attributes = new ArrayList<String>();
	private ArrayList<String> attributeTypes = new ArrayList<String>();
	

	public ArrayList<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}

	public ArrayList<String> getAttributeTypes() {
		return attributeTypes;
	}

	public void setAttributeTypes(ArrayList<String> attributeTypes) {
		this.attributeTypes = attributeTypes;
	}
}
