package io.github.fvarrui.reviser.ui.tasks;

import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;

import javafx.concurrent.Task;

public class RemoveExerciseTask extends Task<Void> {

	private File exerciseDir;

	public RemoveExerciseTask(File exerciseDir) {
		super();
		this.exerciseDir = exerciseDir;
	}

	@Override
	protected Void call() throws Exception {

		updateProgress(-1, 0);
		
		updateMessage("Eliminando ejercicio " + exerciseDir.getName());
		deleteDirectory(exerciseDir);
		updateMessage("¡Ejercicio eliminado!");
		
		return null;
	}
	
	public void start() {
		new Thread(this).start();
	}

}
