package fvarrui.reviser.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

public class CommandUtils {

	private static Object[] expandArray(Object[] array) {
		List<Object> args = new ArrayList<Object>();
		for (Object o : array) {
			if (o.getClass().isArray()) {
				Object[] a = (Object[]) o;
				args.addAll(Arrays.asList(a));
			} else {
				args.add(o);
			}
		}
		return args.toArray();
	}

	private static void createArguments(Commandline command, Object... arguments) {
		for (Object argument : arguments) {
			if (argument instanceof File)
				command.createArg().setFile((File) argument);
			else if (argument.getClass().isArray())
				createArguments(command, argument);
			else
				command.createArg().setValue(argument.toString());
		}
	}

	public static String execute(File workingDirectory, String executable, Object... arguments) throws IOException, CommandLineException {
		StringBuffer outputBuffer = new StringBuffer();

		arguments = expandArray(arguments);

		System.out.println("Executing command: " + executable + " " + StringUtils.join(arguments, " "));

		Commandline command = new Commandline();
		command.setWorkingDirectory(workingDirectory);
		command.setExecutable(executable);
		createArguments(command, arguments);

		Process process = command.execute();

		BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		while (process.isAlive()) {
			if (output.ready())
				outputBuffer.append(output.readLine() + "\n");
			if (error.ready())
				System.err.println(error.readLine());
		}

		output.close();
		error.close();
		
		if (process.exitValue() != 0) {
			throw new CommandLineException("exitCode == " + process.exitValue());
		}

		return outputBuffer.toString();
	}

	public static String execute(String executable, Object... arguments) throws IOException, CommandLineException {
		return execute(new File("."), executable, arguments);
	}

}
