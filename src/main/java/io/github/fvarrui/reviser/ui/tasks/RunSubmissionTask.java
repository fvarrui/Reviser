package io.github.fvarrui.reviser.ui.tasks;

import java.io.File;

import io.github.fvarrui.reviser.model.Submission;
import javafx.concurrent.Task;

public class RunSubmissionTask extends Task<Void> {
	
	private File inputFile;
	private Submission submission;
	
	public RunSubmissionTask(File inputFile, Submission submission) {
		this.inputFile = inputFile;
		this.submission = submission;
	}

	@Override
	protected Void call() throws Exception {
		submission.process();
		submission.run(inputFile);
		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}

}
