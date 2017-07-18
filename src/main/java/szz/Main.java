package szz;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Commit;
import model.ChangedFile;
import control.Utils;

public class Main {

	public static String repositoryPath = "/Users/joaocorreia/tomcat/";
	public static void main(String[] args) {
		int total = 0, max = 0, min = 10000;
		String result = "";
		
		List<String> bugs = Utils.FileRead("bugs.in");
		
		for (String bug : bugs) {
			String[] token = bug.replace(" ", "").split(",");
			String bugId = token[0];
			String commitFix = token[1];
			String commitReport = token[2];

			Commit fix = new Commit(commitFix, repositoryPath);
			Commit report = new Commit(commitReport, repositoryPath);
				
			Set<String> insertionCommits = new HashSet<String>();	
			List<ChangedFile> changedFiles = fix.getChangedFiles();
			for (ChangedFile file : changedFiles) {
				//System.out.println(file.getPath());
				file.setBugIntroductionCandidate(Utils.getBugIntroductionCandidates(fix.getId(), file.getPath(), repositoryPath));
				insertionCommits.addAll(Utils.getInsertionCommits(fix.getId(), report.getId(), file, repositoryPath));
				
			}	
			if(insertionCommits.size() < min){
				min = insertionCommits.size();
			}else if(insertionCommits.size() > max){
				max = insertionCommits.size();
			}
			
			total += insertionCommits.size();
			
			System.out.println(bugId+","+commitFix+","+commitReport+","+insertionCommits.toString()+","+insertionCommits.size()+"\n");
			result += bugId+","+commitFix+","+commitReport+","+insertionCommits.toString()+","+insertionCommits.size()+"\n";
		}
		
		result += "Total: "+ total;
		result += "Min: "+ min;
		result += "Max: "+ max;
		
		Utils.FileWrite("BugInsertionTomcat.txt", result);
		
	}

}
