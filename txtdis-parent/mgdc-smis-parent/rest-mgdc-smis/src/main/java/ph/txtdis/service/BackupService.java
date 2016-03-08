package ph.txtdis.service;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.BinaryUtils.toBytes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.exception.FailedReplicationException;

@Service("backupService")
public class BackupService {

	private static Logger logger = getLogger(BackupService.class);

	@Value("${database.version}")
	private String databaseVersion;

	@Value("${database.name}")
	private String databaseName;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	private String backup;

	public void backup() throws FailedReplicationException {
		try {
			backup = getProperty("user.home") + separator + databaseName + ".backup";
			startBackup();
		} catch (Exception e) {
			throw new FailedReplicationException("Backup");
		}
	}

	public byte[] getBackupBytes() throws FailedReplicationException {
		backup();
		return toBytes(backup);
	}

	private List<String> backupCommand() {
		return asList(postgresAppPath(), //
				"--host=localhost", //
				"--port=5432", //
				"--username=" + username, //
				"--no-password", //
				"--file=" + backup, //
				"--format=custom", //
				"--compress=9", //
				"--verbose", //
				"--dbname=" + databaseName);
	}

	private ProcessBuilder buildProcess() {
		ProcessBuilder pb = new ProcessBuilder(backupCommand());
		pb.environment().put("PGPASSWORD", password);
		pb.redirectErrorStream(true);
		return pb;
	}

	private void logBackupProcess(Process p) throws IOException {
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String log;
		while ((log = br.readLine()) != null)
			logger.info(log);
	}

	private String postgresAppPath() {
		String os = getProperty("os.name");
		if (os.contains("Windows"))
			return "c:\\Program Files\\PostgreSQL\\" + databaseVersion + "\\bin\\pg_dump.exe";
		else if (os.contains("Mac"))
			return "/Library/PostgreSQL/" + databaseVersion + "/bin/pg_dump";
		return "pg_dump";
	}

	private void startBackup() throws IOException, InterruptedException, FailedReplicationException {
		Process p = buildProcess().start();
		logBackupProcess(p);
		p.waitFor();
		if (p.exitValue() != 0)
			throw new FailedReplicationException("Backup");
	}
}
