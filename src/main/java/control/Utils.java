package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

import model.BugIntroductionCandidate;
import model.ChangedFile;

public class Utils {

	// Escreve dado conteúdo em um arquivo
	public static void FileWrite(String fileName, String content) {
		File file = new File(fileName);

		try (FileOutputStream fop = new FileOutputStream(file)) {
			if (!file.exists()) {
				file.createNewFile();
			}

			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Lê um arquivo e retorna todas as linhas em forma de lista
	public static List<String> FileRead(String filePath) {
		List<String> result = new ArrayList<String>();

		try {
			FileReader file = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(file);
			String linha = reader.readLine();

			while (linha != null) {
				result.add(linha);
				linha = reader.readLine();
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// Retorna candidatos a inserção de um bug para um arquivo
	public static List<BugIntroductionCandidate> getBugIntroductionCandidates(String id, String filePath,
			String repositoryPath) {
		List<BugIntroductionCandidate> candidates = new ArrayList<BugIntroductionCandidate>();
		BugIntroductionCandidate candidate = null;

		Map<String, String> filesToDiff = retrieveTempFilesToDiff(id, filePath, repositoryPath);

		DiffJExecutor executor = new DiffJExecutor("/Users/joaocorreia/diffj/build/scripts/diffj",
				filesToDiff.keySet().iterator().next(), filesToDiff.values().iterator().next());

		List<String> diff = executor.run();
		
		for (String diffLine : diff) {
			if (diffLine.length() > 2) {
				List<Integer> interval = getLinesInterval(diffLine);

				if (interval.get(0) != -1) {

					if (candidate != null)
						candidates.add(candidate);

					candidate = new BugIntroductionCandidate();
					candidate.setLineContent(new ArrayList<>());
					candidate.setLineFrom(interval.get(0));
					candidate.setLineTo(interval.get(1));
				} else {
					if ((diffLine.charAt(0) == '<')) {
						candidate.addLineContent(diffLine.substring(2));
					}
				}
			}
		}
		candidates.add(candidate);

		return candidates;
	}

	// Analisa e retorna o intervalo de linhas modificadas, que é fornecido pelo
	// DiffJ
	public static List<Integer> getLinesInterval(String line) {
		List<Integer> interval = new ArrayList<Integer>();

		int lineFrom = -1;
		int lineTo = -1;

		// DiffJ informs the modifications interval in four patterns

		// Rule one: ^\d+[a-z]\d+ -> 197d197
		// Rule two: ^\d+[,]\d+[a-z]\d+ -> 266,274d259
		// Rule three: ^\d+[a-z]\d+[,]\d+ -> 266d274,259
		// Rule four: ^\d+[,]\d+[a-z]\d+[,]\d+ -> 266,274d259,123

		Pattern one = Pattern.compile("(^)(\\d+)([a-z])(\\d+)");
		Pattern two = Pattern.compile("(^)(\\d+)([,])(\\d+)([a-z])(\\d+)");
		Pattern three = Pattern.compile("(^)(\\d+)([a-z])(\\d+)([,])(\\d+)");
		Pattern four = Pattern.compile("(^)(\\d+)([,])(\\d+)([a-z])(\\d+)([,])(\\d+)");

		Matcher mOne = one.matcher(line);
		Matcher mTwo = two.matcher(line);
		Matcher mThree = three.matcher(line);
		Matcher mFour = four.matcher(line);

		if (mOne.find()) {
			try {
				String[] token = line.replace(" ", "").split("[a-z]");
				lineFrom = Integer.parseInt(token[0]);
				lineTo = lineFrom;

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (mTwo.find()) {
			try {
				String[] token = line.replace(" ", "").split("[a-z]")[0].split(",");
				lineFrom = Integer.parseInt(token[0]);
				lineTo = Integer.parseInt(token[1]);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (mThree.find()) {
			try {
				String[] token = line.replace(" ", "").split("[a-z]");
				lineFrom = Integer.parseInt(token[0]);
				lineTo = lineFrom;

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (mFour.find()) {
			try {
				String[] token = line.replace(" ", "").split("[a-z]")[0].split(",");
				lineFrom = Integer.parseInt(token[0]);
				lineTo = Integer.parseInt(token[1]);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		interval.add(lineFrom);
		interval.add(lineTo);

		return interval;
	}

	// Recupera os commits responsáveis por inserir linhas modificadas durante a
	// correção de um bug
	public static Set<String> getInsertionCommits(String idFix, String idReport, ChangedFile file,
			String repositoryPath) {
		Set<String> insertionCommits = new HashSet<String>();
		List<String> annotateResult = new ArrayList<String>();

		String command = "git annotate -l " + idFix + "^ -- " + file.getPath();
		String output = null;

		try {
			Calendar dataReport = Utils.getDateTime(idReport, repositoryPath);
			Process process = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((output = input.readLine()) != null) {
				annotateResult.add(output);
			}
				for (BugIntroductionCandidate candidate : file.getBugIntroductionCandidate()) {
	
					for (int i = candidate.getLineFrom() - 1; i <= candidate.getLineTo() - 1; i++) {
	
						for (String line : candidate.getLineContent()) {
							if (annotateResult.get(i).contains(line)) {
								Calendar dataCommit = Utils.getDateTime(annotateResult.get(i).split("	")[0],
										repositoryPath);
	
								if (dataCommit.before(dataReport)) {
									insertionCommits.add(annotateResult.get(i).split("	")[0]);
								}
							}
						}
					}
	
				}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return insertionCommits;
	}

	// Retorna a data de um commit
	public static Calendar getDateTime(String id, String repositoryPath) {
		String command = "git show " + id + " -s --date=iso --format=\"%cd\"";

		String output = null;

		try {

			Process process = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			output = input.readLine();
			String tokens[] = output.replace("\"", "").split(" ");

			output = tokens[0] + " " + tokens[1];

			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			cal.setTime(sdf.parse(output));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cal;

	}

	// Retorna uma lista com os arquivos modificados em um commit
	public static List<ChangedFile> getChangedFiles(String commit, String repository) {
		List<ChangedFile> fileRelatedToBug = new ArrayList<ChangedFile>();
		String command = "git diff-tree --no-commit-id --name-only -r " + commit;
		String output = null;

		try {
			Process process = Runtime.getRuntime().exec(command, null, new File(repository));

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((output = input.readLine()) != null) {

				if (output.contains(".java")) {
					ChangedFile file = new ChangedFile(output, new ArrayList<BugIntroductionCandidate>());
					fileRelatedToBug.add(file);
				}

			}

			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileRelatedToBug;
	}

	private static String executeCommand(final String command, final String... arguments) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		CommandLine commandLine = CommandLine.parse(command);
		if (arguments != null) {
			commandLine.addArguments(arguments);
		}
		DefaultExecutor defaultExecutor = new DefaultExecutor();
		defaultExecutor.setExitValue(0);
		try {
			defaultExecutor.setStreamHandler(streamHandler);
			defaultExecutor.execute(commandLine);
		} catch (ExecuteException e) {
			System.err.println("Execution failed.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("permission denied.");
			e.printStackTrace();
		}

		return outputStream.toString();

	}

	public static String retrievePreviousCommit(final String commit, final String repositoryPath) {

		final String lastCommit = executeCommand("git", "--work-tree=" + repositoryPath,
				"--git-dir=" + repositoryPath + ".git", "rev-parse", "HEAD");
		executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "checkout",
				commit);
		final String previousCommit = executeCommand("git", "--work-tree=" + repositoryPath,
				"--git-dir=" + repositoryPath + ".git", "rev-parse", "HEAD~1");
		executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "checkout",
				lastCommit);
		return previousCommit;
	}

	private static String retrieveFileContentFromCommit(final String commit, final String file,
			final String repositoryPath) {

		final String lastCommit = executeCommand("git", "--work-tree=" + repositoryPath,
				"--git-dir=" + repositoryPath + ".git", "rev-parse", "HEAD");
		executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "checkout",
				commit);

		StringBuilder fileContentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(repositoryPath + file))) {
			String lineContent = null;
			while ((lineContent = br.readLine()) != null) {
				fileContentBuilder.append(lineContent + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "checkout",
				lastCommit);
		return fileContentBuilder.toString();
	}

	public static Map<String, String> retrieveTempFilesToDiff(final String commit, final String file,
			final String repositoryPath) {

		String fixFileContent = null;
		String previousFileContent = null;

		fixFileContent = retrieveFileContentFromCommit(commit, file, repositoryPath);
		final String previousCommitHash = retrievePreviousCommit(commit, repositoryPath);
		previousFileContent = retrieveFileContentFromCommit(previousCommitHash, file, repositoryPath);

		Map<String, String> tempFilesPath = new HashMap<String, String>();

		if ("".equals(fixFileContent.trim()) || "".equals(previousFileContent.trim())) {
			return tempFilesPath;
		}

		try {
			File fixTempFile = File.createTempFile(file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf(".")),
					".java");
			BufferedWriter bw = new BufferedWriter(new FileWriter(fixTempFile));
			bw.write(fixFileContent);
			bw.close();
			File previousTempFile = File
					.createTempFile(file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf(".")), ".java");
			bw = new BufferedWriter(new FileWriter(previousTempFile));
			bw.write(previousFileContent);
			bw.close();

			tempFilesPath.put(previousTempFile.getAbsolutePath(), fixTempFile.getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return tempFilesPath;
	}
}
