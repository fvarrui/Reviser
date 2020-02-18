package fvarrui.reviser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.codehaus.plexus.util.cli.CommandLineException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.opencsv.exceptions.CsvException;

import fvarrui.reviser.csv.CsvStudent;
import fvarrui.reviser.csv.CsvUtils;
import fvarrui.reviser.model.Result;
import fvarrui.reviser.model.Results;
import fvarrui.reviser.utils.JSONUtils;
import fvarrui.reviser.utils.ZipUtils;

public class BatchTesting {
	

	private static File jsonFile, csvFile, studentsFile, submissionsDir;	
	private static List<CsvStudent> students = new ArrayList<>(); 
	private static Results results = null;

	public static void testSubmissions(String submissionsFilePath, String studentsFilePath, String input) throws JsonSyntaxException, JsonIOException, IOException, CommandLineException, CsvException {

		System.out.println("----------------------------------------------");
		System.out.println("TESTING ALL SUBMISSIONS FROM " + submissionsFilePath);
		System.out.println("----------------------------------------------");

		File submissionsFile = new File(submissionsFilePath);
		submissionsDir = ZipUtils.uncompress(submissionsFile, true);

		jsonFile = new File(submissionsDir, "results.json");
		csvFile = new File(submissionsDir, "results.csv");
		
		if (jsonFile.exists())
			results = JSONUtils.loadFromJson(jsonFile, Results.class);
		if (results == null)
			results = new Results();
		
		studentsFile = new File(studentsFilePath);
	
		students = CsvUtils.csvToStudents(studentsFile);
		
		Arrays.asList(submissionsDir.listFiles()).stream().anyMatch(file -> {
				if (file.isDirectory() && testSubmission(file, results, input)) {
					String response = "Y"; // IOUtils.readString("Continue (Y/n)?", null);			
					return (response.startsWith("n") || response.startsWith("N"));
				}
				return false;
			});
		
		JSONUtils.jsonToFile(results, jsonFile);
		
		System.out.println("----------------------------------------------");
		System.out.println("PROJECTS TESTING FINISHED!");
		System.out.println("----------------------------------------------");
	}

	public static boolean testSubmission(File directory, Results results, String input) {
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
			if (lastResult.get().getScore() > 0 || (lastResult.get().getFeedback() != null && !lastResult.get().getFeedback().isEmpty())) {
				System.out.println("Has already been evaluated with a " + lastResult.get().getScore());
				return false;
			}
			// si el resultado  no es 100, modificamos ese resultado
			currentResult = lastResult.get();
		} else {
			// si no existe iun resultado anterior para el alumno, creamos uno nuevo y lo añadimos a la lista 
			currentResult = new Result();
			currentResult.setName(studentName);
			results.getResults().add(currentResult);
		}
		
		// busca los datos del estudiante
		Optional<CsvStudent> student = students.stream().filter(r -> r.getFullname().equals(studentName)).findFirst();		
		if (student.isPresent()) {
			currentResult.setEmail(student.get().getEmail());
		}
		
		try {

			File submittedFile = directory.listFiles()[0];
			
			Testing.runTest(input, submittedFile);
			
//			currentResult.setScore(IOUtils.readInteger("Enter a score", currentResult.getScore()));			
//			if (currentResult.getScore() < 100) {
//				currentResult.setFeedback(IOUtils.readString("Enter feedback", currentResult.getFeedback()));
//			} else {
//				currentResult.setFeedback("");
//			}
			
		} catch (Exception e) {
			currentResult.setFeedback(e.getMessage());
			currentResult.setScore(0);
			return false;
		}
		
		return true;
		
	}


}
