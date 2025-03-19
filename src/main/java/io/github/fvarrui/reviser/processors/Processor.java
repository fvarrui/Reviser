package io.github.fvarrui.reviser.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import org.eclipse.jgit.api.errors.InvalidRemoteException;

import io.github.fvarrui.reviser.config.Config;
import io.github.fvarrui.reviser.utils.CompressionUtils;
import io.github.fvarrui.reviser.utils.FileUtils;
import io.github.fvarrui.reviser.utils.GitUtils;
import io.github.fvarrui.reviser.utils.URLUtils;

public class Processor {

	public static File process(File submissionDir, boolean force) throws Exception {
		
		System.out.println("Procesando entrega: " + submissionDir.getName());
		try {
			File destinationDir = new File(submissionDir, "files");
			if (destinationDir.exists() && !force) {

				System.out.println("La entrega ya fue procesada y el procesamiento no se ha forzado");
				
			} else {
				
				FileUtils.createFolder(destinationDir, false);
				for (File submittedFile : Objects.requireNonNull(submissionDir.listFiles(pathname -> !pathname.equals(destinationDir)))) {
					processFile(submittedFile, destinationDir);
				}
				
			}
			System.out.println("¡Completado!");
			return destinationDir;
		} catch (InvalidRemoteException e) {
			if (e.getMessage().equals("Invalid remote: origin")) throw new Exception("La URL del repositorio no es correcta");
			throw e;
		}
	}

	private static void processFile(File submittedFile, File destinationDir) throws Exception {

		System.out.println("Procesando fichero: " + submittedFile.getName() + " ... ");

		// el envío es un fichero comprimido
		if (CompressionUtils.isCompressedFile(submittedFile)) {

			uncompress(submittedFile, destinationDir);

		}
		// el envío es texto en línea
		else if (submittedFile.getName().equals("onlinetext.html")) {

			processOnlineText(submittedFile, destinationDir);

		} else {
			
			System.out.println("El envío es un fichero: " + submittedFile.getName());
			FileUtils.moveFile(submittedFile, destinationDir);
			
		}

		System.out.println("Procesamiento completado");

	}

	private static void processOnlineText(File onlineTextFile, File destinationDir) throws Exception {

		// si la entrega es una URL, realiza un procesamiento extra

		System.out.println("Buscando una URL en el fichero " + onlineTextFile.getName());
		String url = URLUtils.extractUrl(onlineTextFile);

		if (url != null) {

			System.out.println("URL encontrada: " + url);
			processUrl(destinationDir, url);

		} else {
			
			System.err.println("No se encontró ninguna URL");			
			
		}

	}

	private static void processUrl(File destinationDir, String url) throws Exception {
		System.out.println("URL extraída de la entrega: " + url);

		// comprueba si la URL es un repo de GitHub
		if (url.matches("https://github.com/.*")) {

			System.out.println("Clonando repositorio GIT desde " + url + " en " + destinationDir + "...");
			GitUtils.clone(url, destinationDir);

		} else if (url.matches("https://raw.githubusercontent.com/.*")) {
			
			downloadPrivateResource(url, destinationDir, Config.getConfig().getGitHubToken());
			
		} else {

			downloadResource(url, destinationDir);
			
		}
		
	}

	private static void downloadResource(String url, File destinationDir) throws Exception {

		String filename = URLUtils.getFile(url);
		File destinationFile = new File(destinationDir, filename);
		
		System.out.println("Descargando recurso desde " + url + " en " + destinationFile + "...");
		
		try {
		
			URLUtils.download(url, destinationFile);
			
		} catch (FileNotFoundException e) {
			
			throw new Exception("Recurso NO encontrado (404): " + url, e);
			
		}
		
	}
	
	private static void downloadPrivateResource(String url, File destinationDir, String token) throws Exception {

		String filename = URLUtils.getFile(url);
		File destinationFile = new File(destinationDir, filename);
		
		System.out.println("Descargando recurso privado desde " + url + " en " + destinationFile + "...");
		
		try {
		
			URLUtils.download(url, destinationFile, new HashMap<>() {{
				put("Authorization", "token " + token);
			}});
			
		} catch (FileNotFoundException e) {
			
			throw new Exception("Recurso NO encontrado (404): " + url, e);
			
		}
		
	}

	private static void uncompress(File submittedFile, File destinationDir) throws IOException {

		System.out.println("Descomprimiendo " + submittedFile.getName() + " ...");

		// descomprime el fichero
		CompressionUtils.decompress(submittedFile, destinationDir);

		// elimina el original y se queda con el extraído
		// submittedFile.delete();

		System.out.println("Fichero extraído: " + submittedFile.getName());
	}

}
