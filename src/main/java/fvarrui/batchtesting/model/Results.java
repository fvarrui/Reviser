package fvarrui.batchtesting.model;

import java.util.ArrayList;
import java.util.List;

public class Results {
	
	private String project;
	private List<Result> results = new ArrayList<>();

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

}
