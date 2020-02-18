package fvarrui.reviser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fvarrui.reviser.csv.CsvStudent;
import fvarrui.reviser.model.Result;
import fvarrui.reviser.model.Results;
import fvarrui.reviser.utils.FileUtils;
import fvarrui.reviser.utils.GitUtils;
import fvarrui.reviser.utils.JSONUtils;
import fvarrui.reviser.utils.MavenUtils;
import fvarrui.reviser.utils.URLUtils;
import fvarrui.reviser.utils.ZipUtils;

public class Testing {
	
	private static final List<String> COMPRESSED_FORMATS = Arrays.asList("zip", "7z", "rar");

	public static void runTest(String input, File submittedFile) throws Exception, IOException, CommandLineException {

		String extension = FilenameUtils.getExtension(submittedFile.getName());
	
		// el envío es un fichero comprimido
		if (COMPRESSED_FORMATS.contains(extension)) {
			
			// descomprime el fichero 
			ZipUtils.uncompress(submittedFile, false);

			// obtiene el fichero extraído
			File parentFile = submittedFile.getParentFile();
			
			// elimina el original y se queda con el extraído
			submittedFile.delete();
			submittedFile = parentFile.listFiles()[0];
			
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
		
		// busca el proyecto maven de forma recursiva
		File pomFile = FileUtils.find(submittedFile, "pom.xml");
		
		// se trata de un proyecto Maven
		if (pomFile != null) {
			
			File mavenProject = pomFile.getParentFile();
			
			if (Arrays.asList(mavenProject.list()).contains(".git")) {
				GitUtils.pull(mavenProject);
			}
			
			MavenUtils.compileAndExec(mavenProject, input);
			
		} else {

			Arrays.asList(submittedFile.getParentFile().listFiles()).stream().forEach(f -> {
				try {
					Desktop.getDesktop().open(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
		}		

	}

	public static Results loadResultsFromJson(File resultsFile, File submissionsDir) {
		Results results = null;
		try {
			results = JSONUtils.loadFromJson(resultsFile, Results.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			// error loading json
		}
		return loadResultsFromSubmissionsDir(results, submissionsDir);
	}
	
	public static Results loadResultsFromSubmissionsDir(Results results, File submissionsDir) {

		if (results == null) {
			results = new Results();
		}
		
		for (File d : submissionsDir.listFiles()) {
			if (d.isDirectory()) { 
				final String name = d.getName().split("_")[0];
				Optional<Result> first = results.getResults().stream().filter(r -> r.getName().equals(name)).findFirst();
				if (first.isPresent()) {
					Result result = first.get();
					result.setDirectory(d.getName());
				} else {
					Result result = new Result();
					result.setName(name);
					result.setDirectory(d.getName());
					results.getResults().add(result);
				}
			}
		}
		
		return results;
	}
	
	public static Results updateResultsFromStudents(Results results, List<CsvStudent> students) {
		results.getResults().stream()
			.forEach(r -> {
				Optional<CsvStudent> student = students.stream().filter(s -> s.getFullname().equals(r.getName())).findFirst();		
				if (student.isPresent()) {
					r.setEmail(student.get().getEmail());
				}
			});
		return results;
	}
	
}
