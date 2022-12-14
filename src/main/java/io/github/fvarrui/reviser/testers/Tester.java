package io.github.fvarrui.reviser.testers;

import java.io.File;
import java.util.stream.Stream;

public abstract class Tester {
	
	private static final Tester DEFAULT_TESTER = new Unknown();
	
	private static final Tester [] TESTERS = {
		new Maven(),
		new BashScript(),
		new PowerShellScript(),
		new Report(),
		new Screenshots()
	};
	
	public abstract boolean matches(File submissionDir);

	public abstract void test(File submissionDir) throws Exception;
	
	public static Tester analyze(File submissionsDir) {
		if (!submissionsDir.exists()) {
			return DEFAULT_TESTER;
		}
		return Stream.of(TESTERS)
			.filter(tester -> tester.matches(submissionsDir))
			.findFirst()
			.orElse(DEFAULT_TESTER);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
