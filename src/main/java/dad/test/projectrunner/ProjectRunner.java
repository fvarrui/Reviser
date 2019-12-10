package dad.test.projectrunner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.maven.shared.invoker.MavenInvocationException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import dad.test.projectrunner.utils.CSVUtils;
import dad.test.projectrunner.utils.GitUtils;
import dad.test.projectrunner.utils.GSONUtils;
import dad.test.projectrunner.utils.IOUtils;
import dad.test.projectrunner.utils.MavenUtils;
import dad.test.projectrunner.utils.URLUtils;
import dad.test.projectrunner.utils.ZipUtils;

public class ProjectRunner {

	private static Config config;
	private static File configFile, jsonFile, csvFile, studentsFile, submissionsDir;	
	private static List<Student> students = new ArrayList<>(); 
	private static Results results = null;
	
	public static void runProjects(String projectPath) throws JsonSyntaxException, JsonIOException, IOException {

		System.out.println("----------------------------------------------");
		System.out.println("RUNNING ALL PROJECTS FROM " + projectPath);
		System.out.println("----------------------------------------------");

		configFile = new File(projectPath, "config.json");
		jsonFile = new File(projectPath, "results.json");
		csvFile = new File(projectPath, "results.csv");	
		
		config = GSONUtils.loadFromJson(configFile, Config.class); 

		if (jsonFile.exists())
			results = GSONUtils.loadFromJson(jsonFile, Results.class);
		if (results == null)
			results = new Results();
		
		studentsFile = new File(projectPath, config.getStudents());
	
		students = CSVUtils.csvToStudents(studentsFile);

		submissionsDir = ZipUtils.unzip(new File(config.getSubmissionsFile()), true);
		
		Arrays.asList(submissionsDir.listFiles()).stream().anyMatch(directory -> {
				if (runProject(config, directory, results)) {
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

	private static boolean runProject(Config config, File directory, Results results) {
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
		
		File input = config.getInput() != null ? new File(configFile.getParentFile(), config.getInput()) : null;
		
		try {
			
			if (config.isGit()) {
				File submissionFile = new File(directory, "onlinetext.html");
				String repoUrl = URLUtils.extractUrl(submissionFile);
				testGit(studentName, repoUrl, input, csvFile);
			} else {
				File zipFile = directory.listFiles()[0];
				File projectDir = zipFile.isFile() ? ZipUtils.unzip(zipFile, false) : zipFile;
				testProject(studentName, projectDir, input, csvFile);
			}
			
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

	private static void testGit(String name, String repoUrl, File input, File output) throws Exception {
				
		// clone git repo
		File projectDir = null;
		try {
			projectDir = GitUtils.clone(repoUrl);
		} catch (Exception e) {
			System.out.println("Error cloning project: " + e.getMessage());
			throw e;
		}

		testProject(name, projectDir, input, output);
		
	}
	
	private static void testProject(String name, File projectDir, File input, File output) throws Exception {
		
		// compile project
		try {
			MavenUtils.compile(projectDir);
		} catch (MavenInvocationException e) {
			System.out.println("Error compiling project: " + e.getMessage());
			throw e;
		}

		// execute project
		try { 
			InputStream in = input != null ? new FileInputStream(input) : null;
			MavenUtils.exec(projectDir, in);
		} catch (MavenInvocationException e) {
			System.out.println("Error executing project: " + e.getMessage());
			throw e;
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		
	}

}
