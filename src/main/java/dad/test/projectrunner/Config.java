package dad.test.projectrunner;

public class Config {
	private String projectName;
	private String submissionsFile;
	private String input;
	private String students;
	private Boolean git;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getSubmissionsFile() {
		return submissionsFile;
	}

	public void setSubmissionsFile(String submissionsFile) {
		this.submissionsFile = submissionsFile;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getStudents() {
		return students;
	}

	public void setStudents(String students) {
		this.students = students;
	}

	public Boolean isGit() {
		return git;
	}

	public void setGit(Boolean git) {
		this.git = git;
	}

}
