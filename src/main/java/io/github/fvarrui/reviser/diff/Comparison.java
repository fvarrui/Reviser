package io.github.fvarrui.reviser.diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.difflib.algorithm.DiffException;

import io.github.fvarrui.reviser.utils.FilenameUtils;

public class Comparison {

	private Project project1;
	private Project project2;
	private List<Match> matches;

	public Comparison(Project project1, Project project2) throws IOException, DiffException {
		this.project1 = project1;
		this.project2 = project2;
		this.matches = compare(project1, project2);
	}

	public Comparison(File project1, File project2) throws IOException, DiffException {
		this(new Project(project1), new Project(project2));
	}
	
	private List<Match> compare(Project project1, Project project2) throws IOException, DiffException {
		List<Match> matches = new ArrayList<>();
		for (ComparedFile file1 : project1.getFiles()) {
			for (ComparedFile file2 : project2.getFiles()) {
				if (FilenameUtils.equalExtensions(file1.getFile(), file2.getFile())) {
					matches.add(new Match(file1, file2));
				}
			}
		}
		return matches;
	}

	public Project getProject1() {
		return project1;
	}

	public Project getProject2() {
		return project2;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public List<Match> getMatches(double threshold) {
		return matches.stream().filter(m -> m.getSimilarity() > threshold).collect(Collectors.toList());
	}
	
	public double getSimilarity(double threshold) {
		int totalMatches = getMatches(threshold).size();
		double totalFiles = Math.max(project1.getFiles().size(), project2.getFiles().size());
		return ((double)totalMatches / (double)totalFiles) * 100.0;
	}

}
