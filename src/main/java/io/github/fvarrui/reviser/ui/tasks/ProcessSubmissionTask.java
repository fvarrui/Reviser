package io.github.fvarrui.reviser.ui.tasks;

import java.io.File;

import io.github.fvarrui.reviser.test.Processing;
import javafx.concurrent.Task;

public class ProcessSubmissionTask extends Task<Void> {
	
	private File submissionDir;
	
	public ProcessSubmissionTask(File submissionDir) {
		super();
		this.submissionDir = submissionDir;
	}
	
	@Override
	protected Void call() throws Exception {
	
		updateProgress(-1, 0);
		
		updateMessage("Procesando todos los ficheros de la entrega: " + submissionDir.getName());
		
		Processing.processAll(submissionDir);
		
		updateMessage("¡Completado!");

		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
}
