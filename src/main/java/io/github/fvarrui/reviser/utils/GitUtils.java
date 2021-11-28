package io.github.fvarrui.reviser.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.microsoft.alm.secret.Token;
import com.microsoft.alm.storage.windows.CredManagerBackedTokenStore;

public class GitUtils {

	/**
	 * Runs "git pull" on a specified repo
	 * @param repoDir Repo project directory
	 * @throws Exception
	 */
	public static void pull(File repoDir) throws Exception {

		Git repo = Git.open(repoDir);

		String uri = repo.getRepository().getConfig().getString("remote", "origin", "url");
		
		repo.pull()
			.setCredentialsProvider(getCredentialsProvider(uri))
			.call();
		repo.getRepository().close();
		
	}
	
	/**
	 * Runs "git clone" on output directory
	 * @param uri URI to clone
	 * @param outputDir Directory where repository is going to be cloned
	 * @return File pointing to cloned repo
	 * @throws Exception
	 */
	public static File clone(String uri, File outputDir) throws Exception {
		
		String name = URLUtils.getFile(uri);
		outputDir = new File(outputDir, name);
		if (outputDir.exists()) {
			FileUtils.deleteDirectory(outputDir);
		}
		Git repo = Git.cloneRepository()
			.setURI(uri)
			.setDirectory(outputDir)
			.setCredentialsProvider(getCredentialsProvider(uri))
			.call();
		repo.getRepository().close();
		return outputDir;
		
	}
	
	/**
	 * Runs "git clone" on temporary directory
	 * @param uri URI to clone
	 * @return File pointing to cloned repo on temp dir 
	 * @throws Exception
	 */
	public static File clone(String uri) throws Exception {
		File outputDir = new File(System.getProperty("java.io.tmpdir"));
		return clone(uri, outputDir);
	}
	
	/**
	 * Gets credential provider to access Git using Personal Access Token from System Credential Manager 
	 * @param uri Git remote server
	 * @return Credential provider
	 * @throws MalformedURLException The specified URI is malformed
	 */
	private static CredentialsProvider getCredentialsProvider(String uri) throws MalformedURLException {
		URL url = new URL(uri);
		String token = getPersonalAccessToken(url);
		if (token == null || token.isEmpty()) return CredentialsProvider.getDefault();
		return new UsernamePasswordCredentialsProvider("token", token);
	}
	
	/**
	 * Gets Personal Access Token from System Credential Manager
	 * @param url Git remote server
	 * @return Personal Access Token
	 */
	private static String getPersonalAccessToken(URL url) {
		CredManagerBackedTokenStore store = new CredManagerBackedTokenStore();
		Token token = store.get("git:" + url.getProtocol() + "://" + url.getHost());
		if (token == null) return null;
		return token.Value.replaceAll("\0", "");		
	}

}
