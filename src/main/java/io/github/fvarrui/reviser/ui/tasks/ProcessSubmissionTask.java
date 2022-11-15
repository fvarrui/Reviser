package io.github.fvarrui.reviser.ui.tasks;

import io.github.fvarrui.reviser.model.Submission;
import javafx.concurrent.Task;

public class ProcessSubmissionTask extends Task<Void> {
	
	private Submission submission;
	private boolean force;
	
	public ProcessSubmissionTask(Submission submission, boolean force) {
		super();
		this.submission = submission;
		this.force = force;
	}
	
	public ProcessSubmissionTask(Submission submission) {
		this(submission, true);
	}
	
	@Override
	protected Void call() throws Exception {
	
		updateProgress(-1, 0);		
		updateMessage("Procesando entrega: " + submission.getDirectory());
		
		submission.process(force);
		
		updateProgress(1, 1);
		updateMessage("¡Completado!");

		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
}
