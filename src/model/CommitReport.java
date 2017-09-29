package model;

/**
 * Created by gabrielnunes on 21/09/17.
 */
public class CommitReport {

    private String commitReport;
    private String nameReport;
    private String userReport;
    private String emailReport;

    public CommitReport() {
    }

    public String getCommitReport() {
        return commitReport;
    }

    public void setCommitReport(String commitReport) {
        this.commitReport = commitReport;
    }

    public String getEmailReport() {
        return emailReport;
    }

    public void setEmailReport(String emailReport) {
        this.emailReport = emailReport;
    }

    public String getUserReport() {
        return userReport;
    }

    public void setUserReport(String userReport) {
        this.userReport = userReport;
    }

    public String getNameReport() {
        return nameReport;
    }

    public void setNameReport(String nameReport) {
        this.nameReport = nameReport;
    }
}
