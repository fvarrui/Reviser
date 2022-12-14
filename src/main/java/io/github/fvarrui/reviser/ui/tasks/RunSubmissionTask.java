package io.github.fvarrui.reviser.ui.tasks;

import io.github.fvarrui.reviser.model.Submission;
import javafx.concurrent.Task;

public class RunSubmissionTask extends Task<Void> {
	
	private Submission submission;
	
	public RunSubmissionTask(Submission submission) {
		this.submission = submission;
	}

	@Override
	protected Void call() throws Exception {
		submission.process(false);
		submission.run();
		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}

}
