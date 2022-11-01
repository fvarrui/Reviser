package io.github.fvarrui.reviser.testers;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import io.github.fvarrui.reviser.utils.GitUtils;

public abstract class Tester {
	
	private static final List<Tester> TESTERS = Arrays.asList(
		new Maven(),
		new BashScript(),
		new PowerShellScript(),
		new Report(),
		new Screenshots(),
		new Unknown()
	);
	
	public void runTest(File submissionDir) throws Exception {
		GitUtils.pullIfRepo(submissionDir);
		test(submissionDir);
	}
	
	public abstract boolean matches(File submissionDir);

	public abstract void test(File submissionDir) throws Exception;
	
	public static Tester analyze(File submissionsDir) {
		return TESTERS.stream()
			.filter(tester -> tester.matches(submissionsDir))
			.findFirst()
			.orElse(null);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
