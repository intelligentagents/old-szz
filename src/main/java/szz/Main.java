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
		
		Commit fix = new Commit("1ad05014b573861b4af501cfe13cb7ef00bc98dc", repositoryPath);
		Commit report = new Commit("93dc4a710cc7da1731da45ded503e90c82c84c7e", repositoryPath);
		
		
		Set<String> insertionCommits = new HashSet<String>();
		
		List<ChangedFile> changedFiles = fix.getChangedFiles();
		for (ChangedFile file : changedFiles) {
			
			file.setBugIntroductionCandidate(Utils.getBugIntroductionCandidates(fix.getId(), file.getPath(), repositoryPath));
			insertionCommits.addAll(Utils.getInsertionCommits(fix.getId(), report.getId(), file, repositoryPath));
			
		}	
		System.out.println(insertionCommits);
		
	}

}
