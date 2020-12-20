package io.github.fvarrui.reviser.diff;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class ComparedFile {

	private Project project;
	private File file;
	private List<String> lines;
	
	public ComparedFile(File file) throws IOException {
		this.file = file;
		this.lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
		this.lines = this.lines.stream().map(s -> StringUtils.trimToNull(s)).filter(s -> s != null).collect(Collectors.toList()); // removes empty/blank lines
		// TODO remove Java comments
	}
	
	public String getName() {
		return file.getName();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public String toString() {
		String relative = getProject().getRootDir().toURI().relativize(getFile().toURI()).getPath();
		return "[" + getProject().getName() + ":" +  relative + "]";
	}

}
