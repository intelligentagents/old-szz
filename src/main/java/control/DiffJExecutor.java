package control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

public class DiffJExecutor {
	private String scriptPath;
	private String fileBefore;
	private String fileAfter;
	
	public DiffJExecutor(String scriptPath, String fileBefore, String fileAfter){
		this.scriptPath = scriptPath;
		this.fileAfter = fileAfter;
		this.fileBefore = fileBefore;
	}
	
	// Executa o DiffJ(scriptPath) para gerar o diff entre dois arquivos (fileBefore) e (fileAfter)  
	public List<String> run() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

		String command = "sh "+scriptPath+" "+fileBefore+" "+fileAfter;
		
		CommandLine commandLine = CommandLine.parse(command);
		DefaultExecutor defaultExecutor = new DefaultExecutor();
		defaultExecutor.setExitValue(0);
		try {
			defaultExecutor.setStreamHandler(streamHandler);
			defaultExecutor.execute(commandLine);
		} catch (ExecuteException e) {
			//System.err.println("Execution failed.");
		    //e.printStackTrace();
		} catch (IOException e) {
			System.err.println("permission denied.");
			e.printStackTrace();
		}

		return stringToList(outputStream.toString());
	}
	
	// Quebra uma string pelos seus \n e retorna em forma de lista
	private List<String> stringToList(String content) {
		List<String> result = new ArrayList<String>();
		String token[] = content.split("\n");

		for (int i = 0; i < token.length; i++) {
			result.add(token[i]);
		}

		return result;
	}
	
	// Exemplo
	public static void main(String args[]) {
		DiffJExecutor executor = new DiffJExecutor("/Users/bonoddr/Development/Workspace/doutorado/diffj/build/scripts/diffj", "/Users/joaocorreia/diffj/build/scripts/Unchanged.java", "/Users/joaocorreia/diffj/build/scripts/Changed.java" );
		List<String> result = executor.run();
		
		for (String string : result) {
			System.out.println(string);
		}
	}
}