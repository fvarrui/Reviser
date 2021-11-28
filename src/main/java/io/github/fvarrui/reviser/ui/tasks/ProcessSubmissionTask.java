package io.github.fvarrui.reviser.ui.tasks;

import io.github.fvarrui.reviser.model.Submission;
import javafx.concurrent.Task;

public class ProcessSubmissionTask extends Task<Void> {
	
	private Submission submission;
	
	public ProcessSubmissionTask(Submission submission) {
		super();
		this.submission = submission;
	}
	
	@Override
	protected Void call() throws Exception {
	
		updateProgress(-1, 0);
		
		updateMessage("Procesando todos los ficheros de la entrega: " + submission.getDirectory());
		
		submission.process();
		
		updateMessage("¡Completado!");

		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
}
