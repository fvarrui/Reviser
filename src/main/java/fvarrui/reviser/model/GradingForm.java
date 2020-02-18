package fvarrui.reviser.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GradingForm {
	private ListProperty<Criterion> criteria = new SimpleListProperty<>(FXCollections.observableArrayList());

	public final ListProperty<Criterion> criteriaProperty() {
		return this.criteria;
	}

	public final ObservableList<Criterion> getCriteria() {
		return this.criteriaProperty().get();
	}

	public final void setCriteria(final ObservableList<Criterion> criteria) {
		this.criteriaProperty().set(criteria);
	}

}
