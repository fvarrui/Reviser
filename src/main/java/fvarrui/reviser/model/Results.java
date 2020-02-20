package fvarrui.reviser.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fvarrui.reviser.csv.CsvStudent;
import fvarrui.reviser.json.JSONUtils;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Results {

	private ObjectProperty<GradingForm> form = new SimpleObjectProperty<>();
	private ListProperty<Result> results = new SimpleListProperty<>(FXCollections.observableArrayList(r -> new Observable[]{ r.nameProperty(), r.emailProperty(), r.feedbackProperty(), r.scoreProperty(), r.evaluatedProperty() })); 

	public final ListProperty<Result> resultsProperty() {
		return this.results;
	}

	public final ObservableList<Result> getResults() {
		return this.resultsProperty().get();
	}

	public final void setResults(final ObservableList<Result> results) {
		this.resultsProperty().set(results);
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
	
	public void populateGrades() {
		
		// associates grades to criteria and removes grades without criterion
		for (Result result : getResults()) {
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
			for (Result result : getResults()) {
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
			GradingForm form = new GradingForm();
			form.getCriteria().add(new Criterion(0L, "General", 100.0));
			setForm(form);
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
	
	public static Results load(File resultsFile, File submissionsDir) {
		Results results = null;
		try {
			results = JSONUtils.loadFromJson(resultsFile, Results.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			results = new Results();
		}
		results.createResultsFromSubmissionsDir(submissionsDir);
		results.initGradingForm();
		results.configListener();
		results.populateGrades();
		return results;
	}
	
	public void save(File resultsFile) throws JsonSyntaxException, JsonIOException, IOException {
		JSONUtils.jsonToFile(this, resultsFile);
	}
	
	public void createResultsFromSubmissionsDir(File submissionsDir) {
		if (submissionsDir == null) return;
		for (File d : submissionsDir.listFiles()) {
			if (d.isDirectory()) { 
				final String name = d.getName().split("_")[0];
				Optional<Result> first = getResults().stream().filter(r -> r.getName().equals(name)).findFirst();
				if (first.isPresent()) {
					Result result = first.get();
					result.setDirectory(d.getName());
				} else {
					Result result = new Result();
					result.setName(name);
					result.setDirectory(d.getName());
					getResults().add(result);
				}
			}
		}
	}
	
	public void updateFromStudents(List<CsvStudent> students) {
		getResults().stream()
			.forEach(r -> {
				Optional<CsvStudent> student = students.stream().filter(s -> s.getFullname().equals(r.getName())).findFirst();		
				if (student.isPresent()) {
					r.setEmail(student.get().getEmail());
				}
			});
	}
	
	public void updateScores() {
		getResults().stream().forEach(Result::updateScore);
	}
	
}
