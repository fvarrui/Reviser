package io.github.fvarrui.reviser.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import io.github.fvarrui.reviser.ui.App;
import io.github.fvarrui.reviser.utils.CompressionUtils;
import io.github.fvarrui.reviser.utils.FileUtils;
import io.github.fvarrui.reviser.utils.GitUtils;
import io.github.fvarrui.reviser.utils.URLUtils;

public class Processing {
	
	public static void processAllFiles(File submissionsDir) {
		
		App.console.println("--- Procesando todos los ficheros de la entrega: " + submissionsDir.getName());
		Arrays.asList(submissionsDir.listFiles()).stream().forEach(Processing::process);

	}

	private static void process(File submittedFile) {

		try {
		
			App.console.println("--- Procesando entrega: " + submittedFile.getName() + " ... ");
			
			// el envío es un fichero comprimido
			if (CompressionUtils.isCompressedFile(submittedFile)) {
				
				App.console.println("--- El fichero está comprimido");
				uncompress(submittedFile);
				
			} 
			// el envío es texto en línea		
			else if (submittedFile.getName().equals("onlinetext.html")) {
				
				App.console.println("--- El envío es texto en línea: " + submittedFile.getName());
				processOnlineText(submittedFile);
							
			}

			App.console.println("--- Procesamiento completado\n");
			
		} catch (Exception e) {
			App.console.println("--- Error durante el procesamiento:\n");
			App.console.println(e);
		}

	}

	private static void processOnlineText(File submittedFile) throws Exception {

		// si la entrega es una URL, realiza un procesamiento extra

		App.console.println("--- Buscando una URL en el fichero ...");		
		String url = URLUtils.extractUrl(submittedFile);
		
		if (url != null) { 
		
			App.console.println("--- URL encontrada: " + url);					
			processUrl(submittedFile, url);
			
		} else {

			App.console.println("--- No se ha encontrado ninguna URL");					
			App.console.println("--- Renombrando entrega por onlinetext.processed.html ...");
			FileUtils.rename(submittedFile, "onlinetext.processed.html");

		}

	}

	private static void processUrl(File submittedFile, String url) throws Exception {
		App.console.println("--- URL extraída de la entrega: " + url);
		
		// comprueba si la URL es un repo de GitHub
		if (url.startsWith("https://github.com")) {
			
			App.console.println("--- La entrega es un repositorio de GitHub");
			cloneRepo(submittedFile, url);
			
		} 
	}

	private static void cloneRepo(File submittedFile, String url) throws Exception {
		
		App.console.println("--- Clonando repositorio GIT desde " + url + " en " + submittedFile.getParentFile() + "...");

		try {
			
			File originalFile = submittedFile;
			
			// clone git repo
			submittedFile = GitUtils.clone(url, submittedFile.getParentFile());
			
			// elimina el fichero del envío
			originalFile.delete();
			
		} catch (Exception e) {
			App.console.println("Error cloning project: " + e.getMessage());
			throw e;
		}

	}

	private static void uncompress(File submittedFile) throws IOException {
		
		App.console.println("--- Descomprimiendo " + submittedFile.getName() + " ...");
		
		// descomprime el fichero
		CompressionUtils.decompress(submittedFile, false);

		// elimina el original y se queda con el extraído
		submittedFile.delete();

		App.console.println("--- Fichero extraído: " + submittedFile.getName());
	}

	
}
