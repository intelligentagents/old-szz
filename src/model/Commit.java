package model;

import java.util.Calendar;

public class Commit{
	private String id;
	private Calendar date;
	
	public Commit(String id, Calendar date) {
		super();
		this.id = id;
		this.date = date;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	
}