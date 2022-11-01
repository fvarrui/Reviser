package io.github.fvarrui.reviser.processors;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jgit.api.errors.InvalidRemoteException;

import io.github.fvarrui.reviser.utils.CompressionUtils;
import io.github.fvarrui.reviser.utils.FileUtils;
import io.github.fvarrui.reviser.utils.GitUtils;
import io.github.fvarrui.reviser.utils.URLUtils;

public class Processor {

	public static void process(File submissionDir) throws Exception {
		System.out.println("Procesando todos los ficheros de la entrega: " + submissionDir.getName());
		try {
			for (File submittedFile : Arrays.asList(submissionDir.listFiles())) {
				processFile(submittedFile);
			}
			System.out.println("¡Completado!");			
		} catch (InvalidRemoteException e) {
			if (e.getMessage().equals("Invalid remote: origin")) throw new Exception("La URL del repositorio no es correcta");
			throw e;
		}
	}

	private static void processFile(File submittedFile) throws Exception {

		System.out.println("Procesando entrega: " + submittedFile.getName() + " ... ");

		// el envío es un fichero comprimido
		if (CompressionUtils.isCompressedFile(submittedFile)) {

			System.out.println("El fichero está comprimido");
			uncompress(submittedFile);

		}
		// el envío es texto en línea
		else if (submittedFile.getName().equals("onlinetext.html")) {

			System.out.println("El envío es texto en línea: " + submittedFile.getName());
			processOnlineText(submittedFile);

		}

		System.out.println("Procesamiento completado");

	}

	private static void processOnlineText(File onlineTextFile) throws Exception {

		// si la entrega es una URL, realiza un procesamiento extra

		System.out.println("Buscando una URL en el fichero ...");
		String url = URLUtils.extractUrl(onlineTextFile);

		if (url != null) {

			System.out.println("URL encontrada: " + url);
			File outputDir = onlineTextFile.getParentFile();
			processUrl(outputDir, url);
			onlineTextFile.delete();

		} else {

			System.out.println("No se ha encontrado ninguna URL");
			System.out.println("Renombrando entrega por onlinetext.processed.html ...");
			FileUtils.rename(onlineTextFile, "onlinetext.processed.html");

		}

	}

	private static void processUrl(File destinationDir, String url) throws Exception {
		System.out.println("URL extraída de la entrega: " + url);

		// comprueba si la URL es un repo de GitHub
		if (url.matches("https://github.com/.*")) {

			System.out.println("Clonando repositorio GIT desde " + url + " en " + destinationDir + "...");
			GitUtils.clone(url, destinationDir);

		} else {

			System.out.println("La entrega es un recurso en la nube");
			downloadResource(url, destinationDir);
			
		}
		
	}

	private static void downloadResource(String url, File destinationDir) throws Exception {

		String filename = URLUtils.getFile(url);
		File destinationFile = new File(destinationDir, filename);
		
		System.out.println("Descargando recurso desde " + url + " en " + destinationFile + "...");
		
		org.apache.commons.io.FileUtils.copyURLToFile(URLUtils.toURL(url), destinationFile);
		
	}

	private static void uncompress(File submittedFile) throws IOException {

		System.out.println("Descomprimiendo " + submittedFile.getName() + " ...");

		// descomprime el fichero
		CompressionUtils.decompress(submittedFile, false);

		// elimina el original y se queda con el extraído
		submittedFile.delete();

		System.out.println("Fichero extraído: " + submittedFile.getName());
	}

}
