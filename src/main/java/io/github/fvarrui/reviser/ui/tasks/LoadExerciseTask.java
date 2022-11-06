package io.github.fvarrui.reviser.ui.tasks;

import java.io.File;

import io.github.fvarrui.reviser.model.Exercise;
import javafx.concurrent.Task;

public class LoadExerciseTask extends Task<Exercise> {

	private File exerciseDir;

	public LoadExerciseTask(File exerciseDir) {
		super();
		this.exerciseDir = exerciseDir;
	}

	@Override
	protected Exercise call() throws Exception {
		updateProgress(-1, 0);
		updateMessage("Cargando ejercicio " + exerciseDir.getName());
		Exercise exercise = Exercise.load(exerciseDir);		
		updateMessage("¡Ejercicio cargado!");
		return exercise;
	}
	
	public void start() {
		new Thread(this).start();
	}

}
