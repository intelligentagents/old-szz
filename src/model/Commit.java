package model;

import java.util.Calendar;
import java.util.List;

import control.Utils;

public class Commit{
	private String id;
	private Calendar date;
	private List<ChangedFile> changedFiles;
	
	public Commit(String id, String repositoryPath) {
		super();
		this.id = id;
		date = Utils.getDateTime(id, repositoryPath);
		changedFiles = Utils.getChangedFiles(id, repositoryPath);
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

	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	public void setChangedFiles(List<ChangedFile> changedFiles) {
		this.changedFiles = changedFiles;
	}
	
	
}