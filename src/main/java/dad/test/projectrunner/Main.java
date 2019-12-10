package dad.test.projectrunner;

public class Main {

	public static String GSON_SAMPLE = "assets/GsonSample";	
	public static String ADIVINAPP = "assets/AdivinApp";	
	public static String CONTACTOS = "assets/Contactos";	

	public static void main(String [] args) throws Exception {
		
		ProjectRunner.runProjects(CONTACTOS);
		
	}

}
