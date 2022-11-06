package io.github.fvarrui.reviser.ui.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.fvarrui.reviser.model.Submission;
import io.github.fvarrui.reviser.ui.Reviser;
import io.github.fvarrui.vulturehunter.Comparison;
import io.github.fvarrui.vulturehunter.Project;
import javafx.concurrent.Task;

public class CompareSubmissionsTask extends Task<Void> {
	
	private static final double THRESHOLD = 75.0;
	private static final double SIMILARITY = 85.0;

    private static final List<String> TEXT_FILES = Arrays.asList("java", "fxml", "xml", "gradle", "txt", "json", "meta", "html", "sh", "ps1");
    private static final List<String> BINARY_FILES = Arrays.asList("pdf", "png", "jpg", "jpeg", "gif", "bmp", "docx", "doc", "odt");
    private static final List<String> EXCLUDED_FILES = Arrays.asList(".*/fotos/.*", ".*/docs/.*", ".*/target/.*", ".*/bin/.*", ".*/\\..*");

	private List<Submission> submissions;
	
	public CompareSubmissionsTask(List<Submission> submissions) {
		this.submissions = submissions;
	}

	@Override
	protected Void call() throws Exception {
		
		Reviser.console.println("Comparando todos los proyectos...");
		compareAll(submissions, SIMILARITY, THRESHOLD);
		Reviser.console.println("\n¡Comparaciones completadas!");

		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	private void compareAll(List<Submission> submissions, double similarity, double threshold) throws IOException {

		List<Submission> comparedProjects = new ArrayList<>();
		
		// compares all found projects
		submissions
			.stream()
			.forEach(s1 -> {

				comparedProjects.add(s1);
				
				submissions
					.stream()
					.filter(s2 -> !comparedProjects.contains(s2))
					.map(s2 -> compare(threshold, s1, s2))
					.filter(c -> c != null)
					.filter(c -> c.getSimilarity() > similarity)
					.map(c -> "\n" + c.toString())
					.forEach(Reviser.console::print);
				
			});

		
	}
	
	private Comparison compare(double threshold, Submission submission1, Submission submission2) {
		try {
			Project project1 = new Project(submission1.getPath(), TEXT_FILES, BINARY_FILES, EXCLUDED_FILES);
			Project project2 = new Project(submission2.getPath(), TEXT_FILES, BINARY_FILES, EXCLUDED_FILES);
			Comparison comparison = new Comparison(project1, project2);
			comparison.calculateSimilarity(threshold);
			return comparison;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
