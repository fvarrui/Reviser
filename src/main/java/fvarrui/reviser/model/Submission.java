package fvarrui.reviser.model;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Submission implements Comparable<Submission> {
	private ObjectProperty<File> directory = new SimpleObjectProperty<>();
	
	public Submission() {}
	
	public Submission(File directory) {
		setDirectory(directory);
	}

	public final ObjectProperty<File> directoryProperty() {
		return this.directory;
	}

	public final File getDirectory() {
		return this.directoryProperty().get();
	}

	public final void setDirectory(final File directory) {
		this.directoryProperty().set(directory);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Submission)) {
			return false;
		}
		Submission other = (Submission) obj;
		if (getDirectory() == null) {
			if (other.getDirectory() != null) {
				return false;
			}
		} else if (!getDirectory().equals(other.getDirectory())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getDirectory().getName();
	}

	@Override
	public int compareTo(Submission o) {
		return getDirectory().getName().compareTo(o.getDirectory().getName());
	}
	
}
