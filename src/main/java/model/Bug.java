package model;

public class Bug {
	private int id;

	private Commit report;
	private Commit fix;
	
	public Bug(int id, Commit report, Commit fix) {
		super();
		this.id = id;
		this.report = report;
		this.fix = fix;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Commit getReport() {
		return report;
	}
	public void setReport(Commit report) {
		this.report = report;
	}
	public Commit getFix() {
		return fix;
	}
	public void setFix(Commit fix) {
		this.fix = fix;
	}
	

}