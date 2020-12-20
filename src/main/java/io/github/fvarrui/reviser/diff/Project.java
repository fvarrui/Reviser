package io.github.fvarrui.reviser.diff;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class Project {
	
	private File rootDir;
	private List<ComparedFile> files; 

	public Project(File rootDir) {
		this.rootDir = rootDir;
		this.listFiles();
	}
	
	public void listFiles() {
	    final String[] SUFFIX = {"java", "fxml", "xml", "gradle"};
	    files = 
	    		FileUtils
	    			.listFiles(rootDir, SUFFIX, true)
	    			.stream()
	    			.filter(f -> !f.getAbsolutePath().contains("target"))
			    	.map(f -> {
						try {
							ComparedFile cf = new ComparedFile(f);
							cf.setProject(this);
							return cf;
						} catch (IOException e) {
							return null;
						}
			    	})
			    	.filter(f -> f != null).collect(Collectors.toList());
	}
	
	public File getRootDir() {
		return rootDir;
	}
	
	public String getName() {
		return rootDir.getName();
	}
	
	public List<ComparedFile> getFiles() {
		return files;
	}
	
}
