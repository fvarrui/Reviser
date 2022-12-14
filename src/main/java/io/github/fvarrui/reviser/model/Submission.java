package io.github.fvarrui.reviser.model;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;

import io.github.fvarrui.reviser.processors.Processor;
import io.github.fvarrui.reviser.testers.Tester;
import io.github.fvarrui.reviser.utils.GitUtils;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Submission {

	@Expose(serialize = false)
	private ObjectProperty<Tester> tester = new SimpleObjectProperty<>();

	@Expose(serialize = false)
	private IntegerProperty score = new SimpleIntegerProperty(0);

	@Expose(serialize = false)
	private ObjectProperty<File> parent = new SimpleObjectProperty<>();

	private StringProperty name = new SimpleStringProperty();
	private StringProperty feedback = new SimpleStringProperty();
	private StringProperty email = new SimpleStringProperty();
	private StringProperty directory = new SimpleStringProperty();
	private ListProperty<Grade> grades = new SimpleListProperty<>(FXCollections.observableArrayList(g -> new Observable[] { g.criterionIdProperty(), g.feedbackProperty(), g.valueProperty(), g.weightedValueProperty() }));
	private BooleanProperty evaluated = new SimpleBooleanProperty();
	private ObjectProperty<SubmissionType> type = new SimpleObjectProperty<>();
	private StringProperty test = new SimpleStringProperty();  

	public Submission() {}

	public Submission(File submissionDirectory) {
		this();
		setDirectory(submissionDirectory.getName());
		setParent(submissionDirectory.getParentFile());
		analyze();
	}

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

	public final ObjectProperty<File> parentProperty() {
		return this.parent;
	}
	
	public final File getParent() {
		return this.parentProperty().get();
	}

	public final void setParent(final File parent) {
		this.parentProperty().set(parent);
	}
	
	public final ObjectProperty<Tester> testerProperty() {
		return this.tester;
	}
	
	public final Tester getTester() {
		return this.testerProperty().get();
	}
	
	public final void setTester(final Tester tester) {
		this.testerProperty().set(tester);
	}
	
	public File getPath() {
		return new File(getParent(), getDirectory());
	}
	
	public final ObjectProperty<SubmissionType> typeProperty() {
		return this.type;
	}

	public final SubmissionType getType() {
		return this.typeProperty().get();
	}

	public final void setType(final SubmissionType type) {
		this.typeProperty().set(type);
	}

	public final StringProperty testProperty() {
		return this.test;
	}

	public final String getTest() {
		return this.testProperty().get();
	}

	public final void setTest(final String test) {
		this.testProperty().set(test);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Submission submission = (Submission) obj;
		if (obj == null)
			return false;
		return submission.getName().equals(getName());
	}

	@Override
	public String toString() {
		return "Submission [" + 
					"score=" + getScore() + ", " + 
					"name=" + getName() + ", " + 
					"feedback=" + getFeedback() + ", " + 
					"email=" + getEmail() + ", " + 
					"directory=" + getDirectory() + ", " + 
					"grades=" + getGrades() + ", " + 
					"type=" + getType() + ", " + 
					"test=" + getTest() + ", " + 
					"evaluated=" + isEvaluated() + 
		"]";
	}

	public Grade findGradeByCriterion(Long criterionId) {
		Optional<Grade> grade = getGrades().stream().filter(g -> g.getCriterionId() == criterionId).findFirst();
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
		System.out.println("fail de " + getName() + " feedback " + feedback);
		setFeedback(feedback);
		setEvaluated(true);
	}

	public boolean gradesHasFeedback() {
		return getGrades().stream().anyMatch(Grade::hasFeedback);
	}

	public boolean gradesHasValue() {
		return getGrades().stream().anyMatch(Grade::hasValue);
	}

	public String getFullFeedback() {
		if (!gradesHasFeedback() && !gradesHasValue()) {
			return getFeedback();
		}
		List<String> feedback = getGrades().stream().map(Grade::toString).collect(Collectors.toList());
		return "<p>" + StringUtils.join(feedback, "</p><p>") + "</p>";
	}
	
	public File getFilesDir() {
		return new File(getPath(), "files");
	}
	
	public void analyze() {
		tester.set(Tester.analyze(getFilesDir()));
	}

	public void run() throws Exception {
		if (getTester() == null) {
			analyze();
		}
		GitUtils.pullIfRepo(getFilesDir());
		getTester().test(getFilesDir());
	}

	public void process(boolean force) throws Exception {
		Processor.process(getPath(), force);
		analyze();
	}
	
}
