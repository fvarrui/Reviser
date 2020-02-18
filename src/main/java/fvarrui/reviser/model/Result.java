package fvarrui.reviser.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Result {
	private IntegerProperty score = new SimpleIntegerProperty(0);
	private StringProperty name = new SimpleStringProperty();
	private StringProperty feedback = new SimpleStringProperty();
	private StringProperty email = new SimpleStringProperty();
	private StringProperty directory = new SimpleStringProperty();
	private ListProperty<Grade> grades = new SimpleListProperty<>(FXCollections.observableArrayList());

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

	@Override
	public boolean equals(Object obj) {
		Result result = (Result) obj;
		return getName().equals(result.getName());
	}

	@Override
	public String toString() {
		return "Result [score=" + getScore() + ", name=" + getName() + ", feedback=" + getFeedback() + ", email="
				+ getEmail() + ", directory=" + getDirectory() + "]";
	}
	
}
