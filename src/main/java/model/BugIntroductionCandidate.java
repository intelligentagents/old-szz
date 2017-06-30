package model;

import java.util.List;

public class BugIntroductionCandidate {
	private int lineFrom;
	private int lineTo;
	private List<String> lineContent;

	public BugIntroductionCandidate() {

	}

	public BugIntroductionCandidate(int lineFrom, int lineTo, List<String> lineContent) {
		super();
		this.lineFrom = lineFrom;
		this.lineTo = lineTo;
		this.lineContent = lineContent;
	}

	public int getLineFrom() {
		return lineFrom;
	}

	public void setLineFrom(int lineFrom) {
		this.lineFrom = lineFrom;
	}

	public int getLineTo() {
		return lineTo;
	}

	public void setLineTo(int lineTo) {
		this.lineTo = lineTo;
	}

	public List<String> getLineContent() {
		return lineContent;
	}

	public void setLineContent(List<String> linesContent) {
		this.lineContent = linesContent;
	}

	public void addLineContent(String line) {
		lineContent.add(line);
	}

	public void removeLineContent(String line) {
		lineContent.remove(line);
	}
}
