/**
 * © Nowina Solutions, 2018-2018
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.api;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Shell32Util;
import com.sun.jna.platform.win32.ShlObj;

/**
 * Handles creation of Nexu configuration folder.
 * 
 * @author Landry Soules
 *
 */
public class ConfigurationManager {

	public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);

	public File manageConfiguration(String applicationName) throws IOException {

		// For Windows OS, we want to conform with MS guidelines:
		// https://msdn.microsoft.com/en-us/library/ms995853.aspx
		final String os = getOs();
		if (os != null && os.toLowerCase().contains("windows")) {
			return manageWindowsConfiguration(applicationName);
		} else {
			return manageCommonConfiguration(applicationName);
		}

	}

	File manageCommonConfiguration(String appName) {
		final File file = getCommonConfigPath(appName).toFile();
		if (file.exists()) {
			return file.canWrite() ? file : null;
		} else {
			return file.mkdirs() && file.canWrite() ? file : null;
		}
	}

	File manageWindowsConfiguration(String appName) throws IOException {
		final File windowsConfFolder = createWindowsConfigurationFolder(appName);
		/* It is possible that an "old" configuration folder exists on the host. If such configuration folder exists, NexU copy all the
		 * old configuration files to the new location. 
		 */
		File incorrectConfigFile = findExistingConfigFolder(appName);
		if (incorrectConfigFile.exists()) {
			moveExistingConfigFolder(incorrectConfigFile, windowsConfFolder);
		}
		return windowsConfFolder;
	}

	File createWindowsConfigurationFolder(String appName) {
		final String appDataPath = getWindowsAppDataPath();
		if (isBlank(appDataPath)) {
			return null;
		}
		final File windowsConfigFolder = Paths.get(getWindowsConfigPath().toString(), appName).toFile();
		if (windowsConfigFolder.exists()) {
			return windowsConfigFolder.canWrite() ? windowsConfigFolder : null;
		} else {
			return windowsConfigFolder.mkdirs() && windowsConfigFolder.canWrite() ? windowsConfigFolder : null;
		}
	}

	File findExistingConfigFolder(String appName) {
		final File oldConfigFolder = getCommonConfigPath(appName).toFile();
		if (oldConfigFolder == null) {
			return null;
		}
		if (oldConfigFolder.exists() && oldConfigFolder.list().length == 0) {
			oldConfigFolder.delete();
			return null;
		}
		return oldConfigFolder;
	}

	File moveExistingConfigFolder(File existingConfigFolder, File windowsConfigFolder) throws IOException {
		for (File configFile : existingConfigFolder.listFiles()) {
			try {
				Files.copy(configFile.toPath(), Paths.get(windowsConfigFolder.getAbsolutePath(), configFile.getName()));
			} catch (FileAlreadyExistsException faee) {
				// If file already exists, we don't do anything
				LOGGER.debug("File {} already exists, we don't replace it.", configFile.getAbsolutePath());
			}
		}
		// If everything went fine, we just backup old backup folder
		File backupFolder = Paths.get(getConfigBackupPath(existingConfigFolder).toString()).toFile();
		existingConfigFolder.renameTo(backupFolder);
		return backupFolder;
	}

	String getUserHome() {
		return System.getProperty("user.home");
	}

	String getWindowsAppDataPath() {
		if (getOs().toLowerCase().contains("windows")) {
			return Shell32Util.getFolderPath(ShlObj.CSIDL_LOCAL_APPDATA);
		}
		return "";
	}

	String getOs() {
		return System.getProperty("os.name");
	}

	String getCompanyName() {
		return "Nowina";
	}

	Path getCommonConfigPath(String appName) {
		return Paths.get(getUserHome(), "." + appName);
	}

	Path getWindowsConfigPath() {
		return Paths.get(getWindowsAppDataPath(), getCompanyName());
	}

	Path getConfigBackupPath(File originalFile) {
		return Paths.get(originalFile.getAbsolutePath() + ".BKP");
	}
}
