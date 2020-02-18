package fvarrui.reviser.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Grade {
	private ObjectProperty<Criterion> criterion = new SimpleObjectProperty<>();
	private DoubleProperty value = new SimpleDoubleProperty();
	private StringProperty feedback = new SimpleStringProperty();

	public final ObjectProperty<Criterion> criterionProperty() {
		return this.criterion;
	}

	public final Criterion getCriterion() {
		return this.criterionProperty().get();
	}

	public final void setCriterion(final Criterion criterion) {
		this.criterionProperty().set(criterion);
	}

	public final DoubleProperty valueProperty() {
		return this.value;
	}

	public final double getValue() {
		return this.valueProperty().get();
	}

	public final void setValue(final double value) {
		this.valueProperty().set(value);
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

}
