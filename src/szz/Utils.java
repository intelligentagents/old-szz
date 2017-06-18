package szz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

	/*
	 * // Retorna as linhas modificadas em um commit para um arquivo public
	 * static Set<String> getModifiedLines(String id, String file, String
	 * repositoryPath) { Set<String> modifiedLines = new
	 * LinkedHashSet<String>();
	 * 
	 * Utils.FileWrite("diff.txt", Utils.executeCommand("git show " + id +" -- "
	 * +file , repositoryPath)); List<String> diff = Utils.FileRead("diff.txt");
	 * 
	 * for (String line : diff) { if(line.length() > 0){ if((line.charAt(0) ==
	 * '+') && !(line.charAt(1) == '+')){ modifiedLines.add(line.substring(1));
	 * }else if((line.charAt(0) == '-') && !(line.charAt(1) == '-')){
	 * modifiedLines.add(line.substring(1)); } } } return modifiedLines; }
	 */

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

	public static void executeScript(String scriptPath, String arguments) throws InterruptedException {
		try {
			ProcessBuilder pb = new ProcessBuilder(scriptPath, "This is ProcessBuilder Example from JCG");
			System.out.println("Run echo command");
			Process process = pb.start(); 
			int errCode = process.waitFor();
			System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
			System.out.println("Echo Output:\n" + output(process.getInputStream()));

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

	/*
	 * public static List<String> executeCommand(String command){
	 * ArrayList<String> results = new ArrayList<String>(); String output =
	 * null;
	 * 
	 * try { Process process = Runtime.getRuntime().exec(command, null, null);
	 * 
	 * BufferedReader input = new BufferedReader(new
	 * InputStreamReader(process.getInputStream()));
	 * 
	 * while ((output = input.readLine()) != null) { results.add(output); }
	 * 
	 * input.close(); } catch (Exception e) { e.printStackTrace(); } return
	 * results; }
	 */
}
