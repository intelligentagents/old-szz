package szz;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Commit;
import control.Utils;

public class Main {

	public static String repositoryPath = "/Users/joaocorreia/tomcat/.git";
	
	public static void main(String[] args) {
		
		Commit fix = new Commit("1516a4fbb13d87130a41cba2e738939cf74c2130",  Utils.getDateTime("1516a4fbb13d87130a41cba2e738939cf74c2130", repositoryPath));
		Commit report = new Commit("b8e93369cae6035bc97a0e168e92e9c34bcab3e1", Utils.getDateTime("b8e93369cae6035bc97a0e168e92e9c34bcab3e1", repositoryPath));
		
		List<String> files = Utils.getChangedFiles(fix.getId(), repositoryPath);
	
		Set<String> lines = null;
		Set<String> insertionCommits = new HashSet<String>();
		
		for (String file : files) {
			lines = Utils.getModifiedLines(fix.getId(), file, repositoryPath);
			insertionCommits.addAll(Utils.getInsertionCommits(fix.getId(), report.getId(), file, lines, repositoryPath));
		}
	}

}
