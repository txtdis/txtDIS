package ph.txtdis.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.exception.FailedReplicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;

@Service("restoreService")
public class RestoreServiceImpl
	implements RestoreService {

	private static Logger logger = getLogger(RestoreServiceImpl.class);

	@Value("${database.name}")
	protected String databaseName;

	@Value("${database.version}")
	protected String databaseVersion;

	@Value("${spring.datasource.username}")
	protected String username;

	@Value("${database.password}")
	private String databasePassword;

	@Value("${spring.datasource.password}")
	private String password;

	@Override
	public void restoreFromDownloadedBackup() throws FailedReplicationException {
		try {
			startRestoring();
		} catch (Exception e) {
			throw new FailedReplicationException("Replication");
		}
	}

	private void startRestoring() throws IOException, InterruptedException {
		Process p = build().start();
		logRestoreProcess(p);
		p.waitFor();
	}

	private ProcessBuilder build() {
		ProcessBuilder pb = new ProcessBuilder(restoreCommand());
		pb.environment().put(databasePassword, password);
		pb.redirectErrorStream(true);
		return pb;
	}

	private void logRestoreProcess(Process p) throws IOException {
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String log;
		while ((log = br.readLine()) != null)
			logger.info(log);
	}

	private List<String> restoreCommand() {
		return asList(postgresRestoreAppPath(), //
			"--host=localhost", //
			"--port=5432", //
			"--username=" + username, //
			"--no-password", //
			"--clean", //
			"--verbose", //
			"--dbname=" + databaseName, //
			getProperty("user.home") + separator + databaseName + ".backup");
	}

	private String postgresRestoreAppPath() {
		String os = getProperty("os.name");
		if (os.contains("Windows"))
			return "c:\\Program Files\\PostgreSQL\\" + databaseVersion + "\\bin\\pg_restore.exe";
		else if (os.contains("Mac"))
			return "/Library/PostgreSQL/" + databaseVersion + "/bin/pg_restore";
		return "pg_restore";
	}
}
