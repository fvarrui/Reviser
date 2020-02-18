package fvarrui.reviser;

public class DAD {

	// submissions files
	public static String GSON_SAMPLE 			= "DAD-GsonSample-90960.zip";	
	public static String ADIVINAPP 				= "DAD-AdivinApp-214196.zip";	
	public static String CAMBIO_DIVISA 			= "DAD-CambioDivisa-170992.zip";	
	public static String CALCULADORA_COMPLEJA 	= "DAD-CalculadoraCompleja-145600.zip";	
	public static String BUSCAR_Y_REEMPLAZAR 	= "DAD-Buscar y reemplazar-96245.zip";	
	public static String INICIO_SESION_MVC 		= "DAD-Inicio de sesión MVC-143055.zip";	
	public static String CALCULADORA_FXML 		= "DAD-Calculadora FXML-94708.zip";	
	public static String GEOMETRIA 				= "DAD-Geometría-147194.zip";
	public static String AHORCADO				= "DAD-Ahorcado-197857.zip";
	public static String GEOFX					= "DAD-GeoFX-220536.zip";
	public static String MICV					= "DAD-MiCV-149800.zip";
	public static String CONTACTOS 				= "DAD-Examen Contactos (10122019)-181078.zip";

	// project path
	public static String PROYECTO 				= "C:\\Users\\fvarrui\\Downloads\\" + CONTACTOS;	

	// students files
	public static String STUDENTS 				= "C:\\Users\\fvarrui\\Google Drive (fvarrui@iesdomingoperezminik.es)\\modulos\\dad\\students-2019-2020.csv";
	
	public static void main(String [] args) throws Exception {

		// DAD
		BatchTesting.testSubmissions(PROYECTO, STUDENTS, GsonSampleInput());
		
	}
	
	public static String GsonSampleInput() {
		return 
				"abc\n" +
				"def\n" +
				"12\n";
	}
	

}
 