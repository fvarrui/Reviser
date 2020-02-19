package fvarrui.reviser.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Criterion {
	private LongProperty id = new SimpleLongProperty();
	private StringProperty name = new SimpleStringProperty();
	private DoubleProperty weight = new SimpleDoubleProperty();
	
	public Criterion() {}
	
	public Criterion(Long id, String name, double weight) {
		setId(id);
		setName(name);
		setWeight(weight);
	}

	public final LongProperty idProperty() {
		return this.id;
	}

	public final long getId() {
		return this.idProperty().get();
	}

	public final void setId(final long id) {
		this.idProperty().set(id);
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

	public final DoubleProperty weightProperty() {
		return this.weight;
	}

	public final double getWeight() {
		return this.weightProperty().get();
	}

	public final void setWeight(final double weight) {
		this.weightProperty().set(weight);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return ((Criterion)obj).getId() == getId();
	}
	
}
