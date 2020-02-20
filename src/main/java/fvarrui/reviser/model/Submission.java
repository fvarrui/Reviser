package fvarrui.reviser.model;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Submission {
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
	public String toString() {
		return getDirectory().getName();
	}

}
