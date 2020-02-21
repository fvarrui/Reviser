package fvarrui.reviser.model;

import com.google.gson.annotations.Expose;

import fvarrui.reviser.utils.StringUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Grade {

	@Expose(serialize = false)
	private ObjectProperty<Criterion> criterion = new SimpleObjectProperty<>();

	@Expose(serialize = false)
	private DoubleProperty weightedValue = new SimpleDoubleProperty();
	private LongProperty criterionId = new SimpleLongProperty();
	private DoubleProperty value = new SimpleDoubleProperty();
	private StringProperty feedback = new SimpleStringProperty();

	public Grade() {
		criterion.addListener((o, ov, nv) -> {
			if (nv != null) {
				criterionId.bind(nv.idProperty());
			} else
				criterionId.unbind();
		});
	}

	public Grade(Criterion criterion) {
		this();
		setCriterion(criterion);
	}

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

	public final LongProperty criterionIdProperty() {
		return this.criterionId;
	}

	public final long getCriterionId() {
		return this.criterionIdProperty().get();
	}

	public final void setCriterionId(final long criterionId) {
		this.criterionIdProperty().set(criterionId);
	}

	public final DoubleProperty weightedValueProperty() {
		return this.weightedValue;
	}

	public final double getWeightedValue() {
		return this.weightedValueProperty().get();
	}

	public final void setWeightedValue(final double weightedValue) {
		this.weightedValueProperty().set(weightedValue);
	}

	public void updateWeightedValue(double total) {
		if (total == 0) setWeightedValue(0.0);
		else setWeightedValue(getValue() * getCriterion().getWeight() / total);
	}
	
	public void clear() {
		setValue(0.0);
		setFeedback("");
	}

	@Override
	public String toString() {
		return StringUtils.adjust(getCriterion().getName(), 30) + " [Peso: " + getCriterion().getWeight() + ", Nota: " + getValue() + ", Nota ponderada:" + getWeightedValue() + ", Comentario: " + (getFeedback() != null ? getFeedback() : "") + "]";
	}	

}
