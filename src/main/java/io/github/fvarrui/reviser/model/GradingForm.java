package io.github.fvarrui.reviser.model;

import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GradingForm {
	private ListProperty<Criterion> criteria = new SimpleListProperty<>(FXCollections.observableArrayList(c -> new Observable[]{ c.idProperty(), c.nameProperty(), c.weightProperty() }));
	
	public final ListProperty<Criterion> criteriaProperty() {
		return this.criteria;
	}

	public final ObservableList<Criterion> getCriteria() {
		return this.criteriaProperty().get();
	}

	public final void setCriteria(final ObservableList<Criterion> criteria) {
		this.criteriaProperty().set(criteria);
	}
	
	public Criterion findCriterion(Long id) {
		Optional<Criterion> criterion = getCriteria().stream().filter(c -> c.getId() == id).findFirst();
		if (criterion.isPresent()) return criterion.get();
		return null;
	}

}
