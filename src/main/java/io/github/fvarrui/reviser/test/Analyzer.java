package io.github.fvarrui.reviser.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jgit.api.errors.InvalidRemoteException;

import io.github.fvarrui.reviser.model.Submission;
import io.github.fvarrui.reviser.utils.CompressionUtils;
import io.github.fvarrui.reviser.utils.FileUtils;
import io.github.fvarrui.reviser.utils.GitUtils;
import io.github.fvarrui.reviser.utils.URLUtils;

public class Analyzer {
	
	public static Class<? extends Submission> analyze(File submissionDir) throws Exception {

		System.out.println("Analizando todos los ficheros de la entrega: " + submissionDir.getName());

		try {
			
			for (File submittedFile : Arrays.asList(submissionDir.listFiles())) {
				analyzeFile(submittedFile);
			}
			
		} catch (InvalidRemoteException e) {

			if (e.getMessage().equals("Invalid remote: origin")) throw new Exception("La URL del repositorio no es correcta");
			throw e;

		}
		System.out.println("¡Completado!");
		
		return Submission.class;

	}

	private static void analyzeFile(File submittedFile) throws Exception {

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

	private static void processOnlineText(File submittedFile) throws Exception {

		// si la entrega es una URL, realiza un procesamiento extra

		System.out.println("Buscando una URL en el fichero ...");
		String url = URLUtils.extractUrl(submittedFile);

		if (url != null) {

			System.out.println("URL encontrada: " + url);
			processUrl(submittedFile, url);

		} else {

			System.out.println("No se ha encontrado ninguna URL");
			System.out.println("Renombrando entrega por onlinetext.processed.html ...");
			FileUtils.rename(submittedFile, "onlinetext.processed.html");

		}

	}

	private static void processUrl(File submittedFile, String url) throws Exception {
		System.out.println("URL extraída de la entrega: " + url);

		// comprueba si la URL es un repo de GitHub
		if (url.startsWith("https://github.com")) {

			System.out.println("La entrega es un repositorio de GitHub");
			cloneRepo(submittedFile, url);

		}
	}

	private static void cloneRepo(File submittedFile, String url) throws Exception {

		System.out.println("Clonando repositorio GIT desde " + url + " en " + submittedFile.getParentFile() + "...");

		File originalFile = submittedFile;

		// clone git repo
		submittedFile = GitUtils.clone(url, submittedFile.getParentFile());

		// elimina el fichero del envío
		originalFile.delete();

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
