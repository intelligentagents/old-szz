package model;

import java.util.ArrayList;
import java.util.Calendar;

public class Bug {

	private int issueID;
    private String title;
    private String author;
    private CommitReport commitReport;
    private ArrayList<CommitFix> commitFix;
    private ArrayList<String> labels;
    private ArrayList<String> comments;
	private Calendar reportDate;
	private Calendar fixDate;
    private String typeFix;

	@Override
	public String toString() {
		return issueID + "";
	}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CommitReport getCommitReport() {
        return commitReport;
    }

    public void setCommitReport(CommitReport commitReport) {
        this.commitReport = commitReport;
    }

    public ArrayList<CommitFix> getCommitFix() {
        return commitFix;
    }

    public void setCommitFix(ArrayList<CommitFix> commitFix) {
        this.commitFix = commitFix;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public Calendar getReportDate() {
        return reportDate;
    }

    public void setReportDate(Calendar reportDate) {
        this.reportDate = reportDate;
    }

    public Calendar getFixDate() {
        return fixDate;
    }

    public void setFixDate(Calendar fixDate) {
        this.fixDate = fixDate;
    }

    public String getTypeFix() {
        return typeFix;
    }

    public void setTypeFix(String typeFix) {
        this.typeFix = typeFix;
    }
}
