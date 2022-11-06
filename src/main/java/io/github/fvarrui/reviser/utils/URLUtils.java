package io.github.fvarrui.reviser.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class URLUtils {
	
	private static final Pattern URL_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

	public static String getFile(String uri) {
		String [] fragments = uri.split("/");
		String filename = fragments[fragments.length - 1];
		return filename.split("\\?")[0];
	}
	
	/**
	 * Finds first URL in a string
	 * @param text String
	 * @return URL
	 */
	public static String findUrl(String text) {
		Matcher m = URL_PATTERN.matcher(text);
		if (m.find()) {
			return m.group();
		}
		return null;
	}
	
	/**
	 * Extracts first URL in a file
	 * @param file
	 * @return URL
	 */
	public static String extractUrl(File file) {
		try {
			String content = FileUtils.readFileToString(file, Charset.defaultCharset());
			return findUrl(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * Check if is a valid URL
	 * @param urlString url to be checked
	 * @return true if is a valid URL
	 */
	public static boolean isUrl(String urlString) {
		return toURL(urlString) != null; 
	}
	
	/**
	 * Converts a string to an URL
	 * @param urlString string containing an URL
	 * @return URL
	 */
	public static URL toURL(String urlString) {
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	/**
	 * Download a resource
	 * @param url URL of the resource to download
	 * @param destinationFile Destination file
	 * @return Destination file
	 * @throws Exception 
	 */
	public static File download(String url, File destinationFile) throws IOException {
		return download(url, destinationFile, new HashMap<>());
	}

	/**
	 * Download a file through HTTP, specifying headers
	 * @param url URL of the resource to download
	 * @param destinationFile Destination file
	 * @param headers HTTP headera
	 * @return Destination file
	 * @throws Exception If file cannot be downloaded
	 */
	public static File download(String url, File destinationFile, Map<String,String> headers) throws IOException {

		OkHttpClient client = new OkHttpClient();
		
		Builder builder = new Request.Builder().url(url);
		headers.forEach(builder::addHeader);
		
		Request request = builder.build();
		
		Response response = client.newCall(request).execute();
		ResponseBody body = response.body();
		if (body == null) {
	        throw new IllegalStateException("Response doesn't contain a file");
	    }
		
		IOUtils.copy(body.byteStream(), new FileOutputStream(destinationFile));
		
		return destinationFile;
	}

}
