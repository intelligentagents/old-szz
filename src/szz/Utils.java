package szz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Utils {
	
	//Escreve um conteúdo em um arquivo
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
	
	//Lê um arquivo e retorna todas as linhas em forma de lista
		 public static List<String> FileRead(String filePath){
			List<String> result = new ArrayList<String>();
			  
			try {
				FileReader file = new FileReader(filePath);
				BufferedReader reader = new BufferedReader(file);	
				String linha = reader.readLine();	
			
				while(linha != null){
					result.add(linha);
					linha = reader.readLine();
				}
				file.close();
			} catch (Exception e) {
				e.printStackTrace();
			}  
			  return result;
	} 

	private static Calendar getDateTime(String id, String repositoryPath) {
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
}
