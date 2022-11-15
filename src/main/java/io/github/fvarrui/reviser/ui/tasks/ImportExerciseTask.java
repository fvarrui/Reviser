package io.github.fvarrui.reviser.ui.tasks;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.fvarrui.reviser.config.Config;
import io.github.fvarrui.reviser.model.Submission;
import io.github.fvarrui.reviser.utils.CompressionUtils;
import javafx.concurrent.Task;

public class ImportExerciseTask extends Task<File> {

	private File exerciseDir;

	public ImportExerciseTask(File exerciseDir) {
		super();
		this.exerciseDir = exerciseDir;
	}

	@Override
	protected File call() throws Exception {

		updateProgress(-1, 0);
		
		if (exerciseDir.isFile()) {

			updateMessage("Descomprimiendo ejercicio");
			exerciseDir = CompressionUtils.decompress(exerciseDir, Config.exercisesDir);

		} else if (exerciseDir.isDirectory()) {

			updateMessage("Moviendo ejercicios");
			File destination = new File(Config.exercisesDir, exerciseDir.getName());
			copyDirectory(exerciseDir, destination);
			deleteDirectory(exerciseDir);			
			exerciseDir = destination;

		}
		
		List<ProcessSubmissionTask> processingThreads = 
				Stream.of(exerciseDir.listFiles())
						.map(Submission::new)
						.map(ProcessSubmissionTask::new)
						.collect(Collectors.toList());
		
		updateMessage("Procesando entregas");
		ExecutorService s = Executors.newFixedThreadPool(processingThreads.size());
		processingThreads.stream().forEach(s::submit);
		s.shutdown();
		while(!s.isTerminated()) ;
		updateMessage("¡Todas las entregas procesadas!");
		
		return exerciseDir;
	}
	
	public void start() {
		new Thread(this).start();
	}

}
