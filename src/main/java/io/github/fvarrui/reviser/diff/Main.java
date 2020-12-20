package io.github.fvarrui.reviser.diff;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.github.difflib.algorithm.DiffException;

public class Main {
	
	private static final double threshold = 80.0; 
	
	public static void test(String submission) throws IOException, DiffException {
		
		File submissionsDir = new File(submission);
		List<File> submissions = Arrays.asList(submissionsDir.listFiles());
		
		System.out.println("==========================================================");
		System.out.println("===> " + submissionsDir.getName());		
		System.out.println("==========================================================");
		System.out.println();
		
		for (File submission1 : submissions) {
			
			if (submission1.isDirectory()) {

				for (File submission2 : submissions) {
				
					if (submission2.isDirectory() && !submission1.equals(submission2)) {

						Comparison comparison = new Comparison(submission1, submission2);

						double similarity = comparison.getSimilarity(threshold);
						
						if (similarity > 75.0) {
						
							System.out.println("-----------------------------------------------------------------------");
							System.out.println("--->" + submission1.getName() + " compared with " + submission2.getName());
							System.out.println("-----------------------------------------------------------------------");
							
							System.out.println(comparison.getSimilarity(threshold));
							comparison.getMatches(threshold).forEach(m -> System.out.println(m));
							System.out.println();
							
						}
						
					}
					
				}
				
			}
		}
		
	}

	public static void main(String[] args) throws IOException, DiffException {
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-AdivinApp-214196");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-Buscar y reemplazar-96245");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-Calculadora FXML-94708");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-CalculadoraCompleja-145600");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-CambioDivisa-170992");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-Enviar email-92076");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-GsonSample-90960");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-IMC-286933");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-Inicio de sesión MVC-143055");
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-MiCV-149800");
		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-Ventana con memoria-92137");
		
//		test("C:\\Users\\fvarrui\\.Reviser\\submissions\\DAD-Examen FaltApp (11122020)-160630");
	}
	
}
