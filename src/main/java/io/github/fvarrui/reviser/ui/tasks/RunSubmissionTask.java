package io.github.fvarrui.reviser.ui.tasks;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import io.github.fvarrui.reviser.test.Processing;
import io.github.fvarrui.reviser.test.Testing;
import javafx.concurrent.Task;

public class RunSubmissionTask extends Task<Void> {
	
	private File inputFile;
	private File submissionDir;
	
	public RunSubmissionTask(File inputFile, File submissionDir) {
		this.inputFile = inputFile;
		this.submissionDir = submissionDir;
	}

	@Override
	protected Void call() throws Exception {
		String input = inputFile.exists() ? FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8) : "";
		Processing.processAll(submissionDir);
		Testing.testAll(input, submissionDir);	
		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}

}
