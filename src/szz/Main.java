package szz;

import java.util.List;
import java.util.Set;

import model.Bug;
import model.Commit;

public class Main {

	public static String repositoryPath = "/Users/joaocorreia/tomcat/.git";
	
	public static void main(String[] args) {
		Commit fix = new Commit("7b870a495d1d9fcf7bf6602f197ae3998ac6499c",  Utils.getDateTime("7b870a495d1d9fcf7bf6602f197ae3998ac6499c", repositoryPath));
		Commit report = new Commit("4ebbc070f40de9c0789f7b136aa4bed7ab0b57e6", Utils.getDateTime("4ebbc070f40de9c0789f7b136aa4bed7ab0b57e6", repositoryPath));
		Bug bug = new Bug(1, fix, report);
		
		//System.out.println(fix.getDateTime().getTime());
		//System.out.println(report.getDateTime().getTime());
		
		List<String> files = Utils.getChangedFiles(fix.getId(), repositoryPath);
	
		Set<String> lines = null;
		for (String file : files) {
			//System.out.println(file);
			lines = Utils.getModifiedLines(fix.getId(), file, repositoryPath);
			//System.out.println(lines.toString());
			Utils.getInsertionCommits(fix.getId(),report.getId(), file, lines, repositoryPath);
		}
		
		/*System.out.println(fix.getDateTime().getTime());
		System.out.println(report.getDateTime().getTime());
		System.out.println(files.toString());*/
		
		
	}

}
