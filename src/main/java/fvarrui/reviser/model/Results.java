package fvarrui.reviser.model;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Results {

	private ObjectProperty<GradingForm> form = new SimpleObjectProperty<>();
	private ListProperty<Result> results = new SimpleListProperty<>(FXCollections.observableArrayList(r -> new Observable[]{ r.nameProperty(), r.emailProperty(), r.feedbackProperty(), r.scoreProperty() })); 

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

}
