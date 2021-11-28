package io.github.fvarrui.reviser.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import io.github.fvarrui.reviser.csv.CsvStudent;
import io.github.fvarrui.reviser.json.JSONUtils;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Exercise {
	
	public static final String EXERCISE_FILENAME = "exercise.json"; 

	@Expose(serialize = false)
	private ObjectProperty<File> directory = new SimpleObjectProperty<>();
	
	private ListProperty<Submission> submissions = new SimpleListProperty<>(FXCollections.observableArrayList(r -> new Observable[]{ r.nameProperty(), r.emailProperty(), r.feedbackProperty(), r.scoreProperty(), r.evaluatedProperty() }));
	private ObjectProperty<GradingForm> form = new SimpleObjectProperty<>();
	
	public Exercise() {
		super();
		directory.addListener(this::onDirectoryChanged);
	}

	public final ListProperty<Submission> submissionsProperty() {
		return this.submissions;
	}

	public final ObservableList<Submission> getSubmissions() {
		return this.submissionsProperty().get();
	}

	public final void setSubmissions(final ObservableList<Submission> submissions) {
		this.submissionsProperty().set(submissions);
	}
	
	public final ObjectProperty<GradingForm> formProperty() {
		return this.form;
	}

	public final GradingForm getForm() {
		return this.formProperty().get();
	}

	public final void setForm(final GradingForm form) {
		this.formProperty().set(form);
	}
	
	public final ObjectProperty<File> directoryProperty() {
		return this.directory;
	}

	public final File getDirectory() {
		return this.directoryProperty().get();
	}

	public final void setDirectory(final File directory) {
		this.directoryProperty().set(directory);
	}

	public void populateGrades() {
		
		// associates grades to criteria and removes grades without criterion
		for (Submission result : getSubmissions()) {
			List<Grade> toRemove = new ArrayList<>();
			for (Grade grade : result.getGrades()) {
				Criterion criterion = getForm().findCriterion(grade.getCriterionId());
				if (criterion != null) {
					grade.setCriterion(criterion);
				} else {
					toRemove.add(grade);
				}
			}
			result.getGrades().removeAll(toRemove);
		}
		
		// add grades for new criteria
		for (Criterion criterion : getForm().getCriteria()) {
			for (Submission result : getSubmissions()) {
				Grade grade = result.findGradeByCriterion(criterion.getId()); 
				if (grade == null) {
					result.getGrades().add(new Grade(criterion));
				}
			}
		}
		
		// update scores
		updateScores();
		
	}
	
	public void initGradingForm() {
		if (getForm() == null) {
			
			Criterion criterion = new Criterion(0L, "General", 100.0);
			
			GradingForm form = new GradingForm();
			form.getCriteria().add(criterion);
			setForm(form);
			
			// migrate data from old version to new version
			getSubmissions().stream().forEach(r -> {
				Grade grade = new Grade(criterion);
				grade.setValue(r.getScore());
				grade.setFeedback(r.getFeedback());
				r.getGrades().setAll(grade);
			});
			
		}		
	}
	
	public void configListener() {
		getForm().criteriaProperty().addListener((ListChangeListener.Change<? extends Criterion> c) -> {
			c.next();
			populateGrades();
		});
		getForm().getCriteria().stream().forEach(c -> c.weightProperty().addListener((o, ov, nv) -> {
			this.updateScores();
		}));
	}
	
	public void createResultsFromSubmissionsDir(File submissionsDir) {
		if (submissionsDir == null) return;
		for (File d : submissionsDir.listFiles()) {
			if (d.isDirectory()) { 
				final String name = d.getName().split("_")[0];
				Optional<Submission> first = getSubmissions().stream().filter(r -> r.getName().equals(name)).findFirst();
				if (first.isPresent()) {
					Submission result = first.get();
					result.setDirectory(d.getName());
				} else {
					Submission result = new Submission();
					result.setName(name);
					result.setDirectory(d.getName());
					getSubmissions().add(result);
				}
			}
		}
	}
	
	public void updateFromStudents(List<CsvStudent> students) {
		getSubmissions().stream()
			.forEach(r -> {
				students.stream()
					.filter(s -> s.getFullname().equals(r.getName().trim().replaceAll(" +", " ")))
					.forEach(s -> r.setEmail(s.getEmail()));
			});
	}
	
	public void updateScores() {
		getSubmissions().stream().forEach(Submission::updateScore);
	}
	
	public void evaluateAll(boolean newValue) {
		getSubmissions().stream().forEach(r -> r.setEvaluated(newValue));
	}
	
	public void save() throws JsonSyntaxException, JsonIOException, IOException {
		File exerciseFile = new File(getDirectory(), EXERCISE_FILENAME);		
		System.out.println("saving exercise ... " + exerciseFile);
		JSONUtils.writeJsonToFile(this, exerciseFile);
	}
	
	public static Exercise load(File submissionsDir) {
		System.out.println("loading exercise ... " + submissionsDir);
		File exerciseFile = new File(submissionsDir, EXERCISE_FILENAME);
		Exercise exercise = null;
		try {
			exercise = JSONUtils.readJsonFromFile(exerciseFile, Exercise.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			exercise = new Exercise();
		}
		exercise.setDirectory(submissionsDir);
		exercise.createResultsFromSubmissionsDir(submissionsDir);
		exercise.initGradingForm();
		exercise.configListener();
		exercise.populateGrades();
		return exercise;
	}
	
	private void onDirectoryChanged(ObservableValue<? extends File> o, File ov, File nv) {
		getSubmissions().stream().forEach(s -> s.setParent(nv));
	}
	
	@Override
	public String toString() {
		return getDirectory().getName();
	}
	
}
