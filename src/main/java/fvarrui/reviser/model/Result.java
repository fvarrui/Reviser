package fvarrui.reviser.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Result {

	@Expose(serialize = false)
	private IntegerProperty score = new SimpleIntegerProperty(0);

	private StringProperty name = new SimpleStringProperty();
	private StringProperty feedback = new SimpleStringProperty();
	private StringProperty email = new SimpleStringProperty();
	private StringProperty directory = new SimpleStringProperty();
	private ListProperty<Grade> grades = new SimpleListProperty<>(FXCollections.observableArrayList(g -> new Observable[] { g.criterionIdProperty(), g.feedbackProperty(), g.valueProperty(), g.weightedValueProperty() }));
	private BooleanProperty evaluated = new SimpleBooleanProperty();

	public Result() {}

	public final IntegerProperty scoreProperty() {
		return this.score;
	}

	public final int getScore() {
		return this.scoreProperty().get();
	}

	public final void setScore(final int score) {
		this.scoreProperty().set(score);
	}

	public final StringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
	}

	public final StringProperty feedbackProperty() {
		return this.feedback;
	}

	public final String getFeedback() {
		return this.feedbackProperty().get();
	}

	public final void setFeedback(final String feedback) {
		this.feedbackProperty().set(feedback);
	}

	public final StringProperty emailProperty() {
		return this.email;
	}

	public final String getEmail() {
		return this.emailProperty().get();
	}

	public final void setEmail(final String email) {
		this.emailProperty().set(email);
	}

	public final StringProperty directoryProperty() {
		return this.directory;
	}

	public final String getDirectory() {
		return this.directoryProperty().get();
	}

	public final void setDirectory(final String directory) {
		this.directoryProperty().set(directory);
	}

	public final ListProperty<Grade> gradesProperty() {
		return this.grades;
	}

	public final ObservableList<Grade> getGrades() {
		return this.gradesProperty().get();
	}

	public final void setGrades(final ObservableList<Grade> grades) {
		this.gradesProperty().set(grades);
	}

	public final BooleanProperty evaluatedProperty() {
		return this.evaluated;
	}

	public final boolean isEvaluated() {
		return this.evaluatedProperty().get();
	}

	public final void setEvaluated(final boolean evaluated) {
		this.evaluatedProperty().set(evaluated);
	}

	@Override
	public boolean equals(Object obj) {
		Result result = (Result) obj;
		if (obj == null) return false;
		return result.getName().equals(getName());
	}

	@Override
	public String toString() {
		return "Result [score=" + getScore() + ", name=" + getName() + ", feedback=" + getFeedback() + ", email="
				+ getEmail() + ", directory=" + getDirectory() + "]";
	}

	public Grade findGradeByCriterion(Long id) {
		Optional<Grade> grade = getGrades().stream().filter(g -> g.getCriterionId() == id).findFirst();
		if (grade.isPresent())
			return grade.get();
		return null;
	}

	private double getTotalWeight() {
		return getGrades().stream().map(Grade::getCriterion).mapToDouble(Criterion::getWeight).sum();
	}

	public void updateGrades() {
		getGrades().stream().forEach(g -> g.updateWeightedValue(getTotalWeight()));
	}

	public void updateScore() {
		updateGrades();
		double score = getGrades().stream().mapToDouble(Grade::getWeightedValue).sum();
		setScore((int) Math.round(score));
	}
	
	public void resetScore() {
		getGrades().stream().forEach(Grade::clear);
		updateScore();
		setFeedback("");
		setEvaluated(false);
	}
	
	public void fail(String feedback) {
		resetScore();
		setFeedback(feedback);
		setEvaluated(true);
	}
	
	public String getFullFeedback() {
		if (getFeedback() != null && !getFeedback().trim().isEmpty()) {
			return "Comentario general: " + getFeedback();
		}
		List<String> feedback = getGrades()
				.stream()
				.map(Grade::toString)
				.collect(Collectors.toList());
		return StringUtils.join(feedback, "\n");
	}

}
