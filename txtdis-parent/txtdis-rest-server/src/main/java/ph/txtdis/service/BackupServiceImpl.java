package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.exception.FailedReplicationException;

import java.io.IOException;
import java.util.List;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static ph.txtdis.util.BinaryUtils.toBytes;

@Service("backupService")
public class BackupServiceImpl //
	implements BackupService {

	@Value("${database.name}")
	protected String databaseName;

	@Value("${database.version}")
	protected String databaseVersion;

	@Value("${spring.datasource.username}")
	protected String username;

	protected String backup;

	@Value("${database.password}")
	private String databasePassword;

	@Value("${spring.datasource.password}")
	private String password;

	@Override
	public byte[] getBackupBytes() throws FailedReplicationException {
		backup();
		return toBytes(backup);
	}

	@Override
	public void backup() throws FailedReplicationException {
		try {
			backup = getProperty("user.home") + separator + databaseName + ".backup";
			startBackup();
		} catch (Exception e) {
			throw new FailedReplicationException("Backup");
		}
	}

	private void startBackup() throws IOException, InterruptedException, FailedReplicationException {
		Process p = buildProcess().start();
		p.waitFor();
		if (p.exitValue() != 0)
			throw new FailedReplicationException("Backup");
	}

	private ProcessBuilder buildProcess() {
		ProcessBuilder pb = new ProcessBuilder(backupCommand());
		pb.environment().put(databasePassword, password);
		pb.redirectErrorStream(true);
		return pb;
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

	private String postgresAppPath() {
		String os = getProperty("os.name");
		if (os.contains("Windows"))
			return "c:\\Program Files\\PostgreSQL\\" + databaseVersion + "\\bin\\pg_dump.exe";
		else if (os.contains("Mac"))
			return "/Library/PostgreSQL/" + databaseVersion + "/bin/pg_dump";
		return "pg_dump";
	}
}
