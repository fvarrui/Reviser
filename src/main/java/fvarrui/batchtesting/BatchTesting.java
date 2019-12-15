package fvarrui.batchtesting;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fvarrui.batchtesting.model.Result;
import fvarrui.batchtesting.model.Results;
import fvarrui.batchtesting.model.Student;
import fvarrui.batchtesting.utils.CSVUtils;
import fvarrui.batchtesting.utils.GSONUtils;
import fvarrui.batchtesting.utils.GitUtils;
import fvarrui.batchtesting.utils.IOUtils;
import fvarrui.batchtesting.utils.MavenUtils;
import fvarrui.batchtesting.utils.URLUtils;
import fvarrui.batchtesting.utils.ZipUtils;

public class BatchTesting {
	
	private static final List<String> COMPRESSED_FORMATS = Arrays.asList("zip", "7z", "rar");

	private static File jsonFile, csvFile, studentsFile, submissionsDir;	
	private static List<Student> students = new ArrayList<>(); 
	private static Results results = null;
	
	public static void testSubmissions(String submissionsFilePath, String studentsFilePath, String input) throws JsonSyntaxException, JsonIOException, IOException, CommandLineException {

		System.out.println("----------------------------------------------");
		System.out.println("TESTING ALL SUBMISSIONS FROM " + submissionsFilePath);
		System.out.println("----------------------------------------------");

		File submissionsFile = new File(submissionsFilePath);
		submissionsDir = ZipUtils.uncompress(submissionsFile, true);

		jsonFile = new File(submissionsDir, "results.json");
		csvFile = new File(submissionsDir, "results.csv");	
		
		if (jsonFile.exists())
			results = GSONUtils.loadFromJson(jsonFile, Results.class);
		if (results == null)
			results = new Results();
		
		studentsFile = new File(studentsFilePath);
	
		students = CSVUtils.csvToStudents(studentsFile);
		
		Arrays.asList(submissionsDir.listFiles()).stream().anyMatch(directory -> {
				if (testSubmission(directory, results, input)) {
					String response = IOUtils.readString("Continue (Y/n)?", null);			
					return (response.startsWith("n") || response.startsWith("N"));
				}
				return false;
			});
		
		GSONUtils.jsonToFile(results, jsonFile);
		
		CSVUtils.resultsToCsv(results, csvFile);
		
		System.out.println("----------------------------------------------");
		System.out.println("PROJECTS TESTING FINISHED!");
		System.out.println("----------------------------------------------");
	}

	private static boolean testSubmission(File directory, Results results, String input) {
		String studentName = directory.getName().split("_")[0];
		
		System.out.println();
		System.out.println("---------- Student: " + studentName + "----------");
		
		Result currentResult;

		// comprueba si ya hay un resultado almacenado para el alumno
		Optional<Result> lastResult = results.getResults().stream().filter(r -> r.getName().equals(studentName)).findFirst();		
		if (lastResult.isPresent()) {
			// si el resultado anterior es 100, el alumno tiene bien el proyecto y no hay nada que revisar
			if (lastResult.get().getScore() == 100) {
				System.out.println("Has already been evaluated with a 100");
				return false;
			}
			// si el resultado anterior es mayor que 0, no corrijo de nuevo
//			if (lastResult.get().getScore() > 0 || !lastResult.get().getFeedback().isEmpty()) {
//				System.out.println("Has already been evaluated with a " + lastResult.get().getScore());
//				return false;
//			}
			// si el resultado  no es 100, modificamos ese resultado
			currentResult = lastResult.get();
		} else {
			// si no existe iun resultado anterior para el alumno, creamos uno nuevo y lo añadimos a la lista 
			currentResult = new Result();
			currentResult.setName(studentName);
			results.getResults().add(currentResult);
		}
		
		// busca los datos del estudiante
		Optional<Student> student = students.stream().filter(r -> r.getFullname().equals(studentName)).findFirst();		
		if (student.isPresent()) {
			currentResult.setEmail(student.get().getEmail());
		}
		
		try {

			File submittedFile = directory.listFiles()[0];
			
			testSubmmittedFile(studentName, input, submittedFile);
			
			currentResult.setScore(IOUtils.readInteger("Enter a score", currentResult.getScore()));			
			if (currentResult.getScore() < 100) {
				currentResult.setFeedback(IOUtils.readString("Enter feedback", currentResult.getFeedback()));
			} else {
				currentResult.setFeedback("");
			}
			
		} catch (Exception e) {
			currentResult.setFeedback(e.getMessage());
			currentResult.setScore(0);
			return false;
		}
		
		return true;
		
	}

	private static void testSubmmittedFile(String studentName, String input, File submittedFile) throws Exception, IOException, CommandLineException {

		String extension = FilenameUtils.getExtension(submittedFile.getName());
	
		// el envío es un fichero comprimido
		if (COMPRESSED_FORMATS.contains(extension)) {
			
			// descomprime el fichero 
			ZipUtils.uncompress(submittedFile);
			
			// elimina el original
			submittedFile.delete();
			
			// obtiene el fichero extraído
			submittedFile = submittedFile.getParentFile().listFiles()[0];
			
		}

		
		// el envío es texto en línea
		if (submittedFile.getName().equals("onlinetext.html")) {
			
			String repoUrl = URLUtils.extractUrl(submittedFile);
			
			submittedFile.delete();
			
			// clone git repo
			try {
				submittedFile = GitUtils.clone(repoUrl, submittedFile.getParentFile());
			} catch (Exception e) {
				System.out.println("Error cloning project: " + e.getMessage());
				throw e;
			}			
			
		}
		
		// se trata de un proyecto Maven
		if (submittedFile.isDirectory() && Arrays.asList(submittedFile.list()).contains("pom.xml")) {
				
			if (Arrays.asList(submittedFile.list()).contains(".git")) {
				GitUtils.pull(submittedFile);
			}
			
			testMaven(studentName, submittedFile, input, csvFile);
			
		} else {

			Desktop.getDesktop().open(submittedFile);
			
		}		

	}
	
	private static void testMaven(String name, File projectDir, String input, File output) throws Exception {
		
		// compile project
		try {
			MavenUtils.compile(projectDir);
		} catch (MavenInvocationException e) {
			System.out.println("Error compiling project: " + e.getMessage());
			throw e;
		}

		// execute project
		try { 
			InputStream in = input != null ? new ByteArrayInputStream(input.getBytes()) : null;
			MavenUtils.exec(projectDir, in);
		} catch (MavenInvocationException e) {
			System.out.println("Error executing project: " + e.getMessage());
			throw e;
		}
		
	}

}
