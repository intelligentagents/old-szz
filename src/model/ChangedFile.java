package model;

import java.util.List;

import control.Utils;

public class ChangedFile {
	private String path;
	List<BugIntroductionCandidate> bugIntroductionCandidate;

	public ChangedFile(String path, List<BugIntroductionCandidate> bugIntroductionCandidate) {
		super();
		this.path = path;
		this.bugIntroductionCandidate = bugIntroductionCandidate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<BugIntroductionCandidate> getBugIntroductionCandidate() {
		return bugIntroductionCandidate;
	}

	public void setBugIntroductionCandidate(List<BugIntroductionCandidate> bugIntroductionCandidate) {
		this.bugIntroductionCandidate = bugIntroductionCandidate;
	}

}
