package io.github.fvarrui.reviser.testers;

import java.awt.Desktop;
import java.io.File;

import io.github.fvarrui.reviser.ui.Reviser;

public class Unknown extends Tester {

	@Override
	public boolean matches(File submissionDir) {
		return true;
	}

	@Override
	public void test(File submissionDir) throws Exception {
		// do nothing
		Reviser.console.println("No sé cómo ejecutar este proyecto, es desconocido para mí :_(");
		Reviser.console.println("Abriendo el directorio " + submissionDir.getName() + " en el explorador de archivos");
		Desktop.getDesktop().open(submissionDir);
	}
	
	@Override
	public String toString() {
		return "???";
	}

}
