package org.ncsu.cs.edu.models;

public class Physician extends User{

	private String pid;
	private String pname;
	
	public Physician(String pid, String pname){
		this.pid = pid;
		this.pname = pname;
	}
	
	public String getPid() {
		return pid;
	}
	
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public String getPname() {
		return pname;
	}
	
	public void setPname(String pname) {
		this.pname = pname;
	}
}
