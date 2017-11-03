package szz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.*;
import control.Utils;

public class Main {

	public static String repositoryPath = "/home/easy/elasticsearch/";
	public static void main(String[] args) throws FileNotFoundException {
		int total = 0, max = 0, min = 10000;
		String result = "";

		final File folder = new File("/home/easy/Elasticsearch");
		List<String> files = Utils.getAllJsonFiles(folder);
		System.out.println(files.toString());

		for (String json : files) {
			Bug bug = Utils.readJson("/home/easy/Elasticsearch/" + json);

			ArrayList<CommitFix> fixes = bug.getCommitFix();

			CommitReport report = bug.getCommitReport();

			for (CommitFix fix : fixes) {
				System.out.println("Analyzing: " + fix.getCommitFix());

				Commit fixForSzz = new Commit(fix.getCommitFix(), repositoryPath);
				Commit reportForSzz = new Commit(report.getCommitReport(), repositoryPath);

				Set<String> insertionCommits = new HashSet<String>();
				List<ChangedFile> changedFiles = fixForSzz.getChangedFiles();

				for (ChangedFile file : changedFiles) {
					System.out.println(file.getPath());
					file.setBugIntroductionCandidate(Utils.getBugIntroductionCandidates(fixForSzz.getId(), file.getPath(), repositoryPath));
					insertionCommits.addAll(Utils.getInsertionCommits(fixForSzz.getId(), reportForSzz.getId(), file, repositoryPath));

				}
				if (insertionCommits.size() < min) {
					min = insertionCommits.size();
				} else if (insertionCommits.size() > max) {
					max = insertionCommits.size();
				}

				total += insertionCommits.size();

				System.out.println(json + "," + fixForSzz.getId() + "," + reportForSzz.getId() + "," + insertionCommits.toString() + "\n");
				result += json + "," + fixForSzz.getId() + "," + reportForSzz.getId() + ",\"" + insertionCommits.toString() + "\"\n";
			}

		}

			result += "Total: " + total + "\n";
			result += "Min: " + min + "\n";
			result += "Max: " + max + "\n";

			Utils.FileWrite("resultElastic.csv", result);


//		for (String bug : bugs) {
//			String[] token = bug.replace(" ", "").split(",");
//
//			String bugId = token[0];
//			String commitFix = token[1];
//			String commitReport = token[2];
//
//			System.out.println("Analyzing: "+commitFix);
//
//			Commit fix = new Commit(commitFix, repositoryPath);
//			Commit report = new Commit(commitReport, repositoryPath);
//
//			Set<String> insertionCommits = new HashSet<String>();
//			List<ChangedFile> changedFiles = fix.getChangedFiles();
//			for (ChangedFile file : changedFiles) {
//				System.out.println(file.getPath());
//				file.setBugIntroductionCandidate(Utils.getBugIntroductionCandidates(fix.getId(), file.getPath(), repositoryPath));
//				insertionCommits.addAll(Utils.getInsertionCommits(fix.getId(), report.getId(), file, repositoryPath));
//
//			}
//			if(insertionCommits.size() < min){
//				min = insertionCommits.size();
//			}else if(insertionCommits.size() > max){
//				max = insertionCommits.size();
//			}
//
//			total += insertionCommits.size();
//
//			System.out.println(bugId+","+commitFix+","+commitReport+",{"+insertionCommits.toString()+"}\n");
//			result += bugId+","+commitFix+","+commitReport+",{"+insertionCommits.toString()+"}\n";
//		}
//
//		result += "Total: "+ total + "\n";
//		result += "Min: "+ min + "\n";
//		result += "Max: "+ max + "\n";
//
//		Utils.FileWrite("derbyResultNew.txt", result);

		}
}
