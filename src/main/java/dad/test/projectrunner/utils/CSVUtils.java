package dad.test.projectrunner.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import dad.test.projectrunner.Results;
import dad.test.projectrunner.Student;

public class CSVUtils {

	public static void resultsToCsv(Results results, File csvFile) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("email,name,score,feedback\n");
		results.getResults().stream().forEach(r -> buffer.append(r.getEmail() + "," + r.getName() + "," + r.getScore() + "," + r.getFeedback() + "\n"));
		FileUtils.writeStringToFile(csvFile, buffer.toString(), Charset.forName("UTF-8"));
	}

	public static List<Student> csvToStudents(File csvFile) throws IOException {
		List<String> lines = FileUtils.readLines(csvFile, Charset.forName("UTF-8"));
		lines.remove(0); // elimina los encabezados
		return 
			lines.stream()
				.filter(line -> !line.isEmpty())
				.map(line -> {
					String [] fields = line.split(",");
					Student student = new Student();
					student.setName(fields[0]);
					student.setSurname(fields[1]);
					student.setEmail(fields[2]);
					return student;
				})
				.collect(Collectors.toList());
	}

}
