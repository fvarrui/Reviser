package io.github.fvarrui.reviser.testers;

import static org.apache.commons.io.FilenameUtils.isExtension;

import java.io.File;
import java.util.Arrays;

import io.github.fvarrui.reviser.ui.Reviser;

public class PowerShellScript extends Tester {

	private boolean isScript(File file) {
		return file.isFile() && isExtension(file.getName().toLowerCase(), "ps1");
	}

	public boolean matches(File submissionDir) {
		return Arrays.asList(submissionDir.listFiles())
				.stream()
				.anyMatch(this::isScript);
	}

	@Override
	public void test(File submissionDir) throws Exception {
		Reviser.console.println("Abriendo PowerShell en " + submissionDir);
		new ProcessBuilder()
				.command("powershell")
				.directory(submissionDir)
				.start();
		Reviser.console.println("¡Completado!");
	}
	
	@Override
	public String toString() {
		return "PowerShell script";
	}

}
