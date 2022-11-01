package io.github.fvarrui.reviser.testers;

import static org.apache.commons.io.FilenameUtils.isExtension;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.github.fvarrui.reviser.ui.Reviser;

public class Report extends Tester {

	private static final List<String> EXTENSIONS = Arrays.asList("pdf", "odt", "doc", "docx", "md");

	private boolean isReport(File file) {
		return file.isFile() && isExtension(file.getName().toLowerCase(), EXTENSIONS);
	}

	public boolean matches(File submissionDir) {
		return Arrays.asList(submissionDir.listFiles())
				.stream()
				.anyMatch(this::isReport);
	}

	@Override
	public void test(File submissionDir) throws Exception {
		Reviser.console.println("Abriendo documentos:");
		Arrays.asList(submissionDir.listFiles())
			.stream()
			.forEach(file -> {
				try {
					Reviser.console.println("* " + file);
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});		
		Reviser.console.println("¡Completo!");
	}

}
