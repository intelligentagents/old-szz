package control;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

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

	// Retorna as linhas modificadas em um commit para um arquivo
	public static Set<String> getModifiedLines(String id, String file, String repositoryPath) {
		Set<String> modifiedLines = new LinkedHashSet<String>();

		String command = "git show " + id + " -- " + file;
		String output = null;

		try {
			Process process = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((output = input.readLine()) != null) {

				if (output.length() > 2) {
					if ((output.charAt(0) == '+') && !(output.charAt(1) == '+')) {
						// System.out.println(output.substring(1));
						modifiedLines.add(output.substring(1));
					} else if ((output.charAt(0) == '-') && !(output.charAt(1) == '-')) {
						// System.out.println(output.substring(1));
						modifiedLines.add(output.substring(1));
					}
				}
			}

			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modifiedLines;
	}
	
	//Recupera os commits responsáveis por inserir linhas modificadas durante a correção de um bug
	public static Set<String> getInsertionCommits(String idFix, String idReport, String file, Set<String> lines,
			String repositoryPath) {
		Set<String> insertionCommits = new HashSet<String>();

		String command = "git annotate -l " + idFix + "^ -- " + file;
		String output = null;

		try {
			Calendar dataReport = Utils.getDateTime(idReport, repositoryPath);
			Process process = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((output = input.readLine()) != null) {
				for (String line : lines) {
					if (output.contains(line)) {
						//System.out.println(output);
						Calendar dataCommit = Utils.getDateTime(output.split("	")[0], repositoryPath);

						if (dataCommit.before(dataReport)) {
							insertionCommits.add(output.split("	")[0]);
						}
					}
				}
			}

			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(insertionCommits.toString());
		return insertionCommits;
	}

	// Retorna a data de um commit
	public static Calendar getDateTime(String id, String repositoryPath) {
		String command = "git show " + id + " -s --date=iso --format=\"%cd\"";
		//System.out.println(command);

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
	public static List<String> getChangedFiles(String commit, String repository) {
		ArrayList<String> results = new ArrayList<String>();
		String command = "git diff-tree --no-commit-id --name-only -r " + commit;
		String output = null;

		try {
			Process process = Runtime.getRuntime().exec(command, null, new File(repository));

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((output = input.readLine()) != null) {

				if (output.contains(".java")) {
					results.add(output);
				}

			}

			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
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

		return outputStream.toString().replaceAll("\n", "");
	}

	public static String retrievePreviousCommit(final String commit, final String repositoryPath) {
		
		final String lastCommit = executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "rev-parse", "HEAD");
		executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "checkout", commit);
		final String previousCommit = executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "rev-parse", "HEAD~1");
		executeCommand("git", "--work-tree=" + repositoryPath, "--git-dir=" + repositoryPath + ".git", "checkout", lastCommit);
		return previousCommit;
	}
}