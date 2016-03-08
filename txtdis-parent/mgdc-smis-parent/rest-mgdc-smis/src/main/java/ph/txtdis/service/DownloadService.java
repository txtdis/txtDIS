package ph.txtdis.service;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static javax.mail.Folder.READ_ONLY;
import static org.apache.log4j.Logger.getLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.AndTerm;
import javax.mail.search.DateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static ph.txtdis.util.DateTimeUtils.epochDate;

import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.exception.NoNewerFileException;
import ph.txtdis.type.SyncType;

@Service("downloadService")
public class DownloadService {

	private static Logger logger = getLogger(DownloadService.class);

	private static final String BACKUP_DOWNLOAD = "Backup download";

	private static final String SCRIPT_CREATION = "Script Creation";

	@Value("${database.name}")
	private String databaseName;

	@Value("${mail.recipient}")
	private String recipient;

	@Value("${mail.recipient.password}")
	private String password;

	public Date download(SyncType type, Date latest) throws NoNewerFileException, FailedReplicationException {
		latest = saveAttachment(type, latest);
		logger.info("Latest Received Date @ download = " + latest);
		return latest;
	}

	private Comparator<Message> compareReceivedDates() {
		return (a, b) -> {
			try {
				return a.getReceivedDate().compareTo(b.getReceivedDate());
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		};
	}

	private MimeBodyPart getAttachment(Message m) throws IOException, MessagingException {
		Multipart mp = (Multipart) m.getContent();
		return (MimeBodyPart) mp.getBodyPart(0);
	}

	private Date getReceivedDate(Message[] messages) {
		try {
			for (Message m : messages)
				logger.info("Message Received Date =" + m.getReceivedDate());
			Date latest = latestMessage(messages).getReceivedDate();
			logger.info("Latest Received Date @ getReceivedDate = " + latest);
			return latest;
		} catch (Exception e) {
			e.printStackTrace();
			return epochDate();
		}
	}

	private String getScripts(Message msg) {
		try {
			MimeBodyPart a = getAttachment(msg);
			return IOUtils.toString((InputStream) a.getContent());
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
			return "";
		}
	}

	private Folder inbox() throws MessagingException {
		logger.info("Opening...");
		Folder inbox = store().getFolder("INBOX");
		inbox.open(READ_ONLY);
		return inbox;
	}

	private Message latestMessage(Message[] messages) {
		return asList(messages).stream().max(compareReceivedDates()).get();
	}

	private Message[] messages(String fileExt, Date latest) throws FailedReplicationException, NoNewerFileException {
		try {
			logger.info("Searching...");
			Message[] messages = inbox().search(searchTerm(fileExt, latest));
			if (messages.length == 0)
				throw new NoNewerFileException(fileExt, latest);
			return messages;
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new FailedReplicationException("Download");
		}
	}

	private String path(SyncType type) {
		return getProperty("user.home") + separator + databaseName + "." + type.toString();
	}

	private Date saveAttachment(SyncType type, Date latest) throws FailedReplicationException, NoNewerFileException {
		Message[] messages = messages(type.toString(), latest);
		logger.info("Downloading...");
		if (type == SyncType.BACKUP)
			latest = saveBackup(type, messages);
		else
			latest = saveScripts(type, messages);
		logger.info("Latest Received Date @ saveAttachment = " + latest);
		return latest;
	}

	private Date saveBackup(SyncType type, Message[] messages) throws FailedReplicationException {
		try {
			Message msg = latestMessage(messages);
			MimeBodyPart mbp = getAttachment(msg);
			mbp.saveFile(path(type));
			return msg.getReceivedDate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedReplicationException(BACKUP_DOWNLOAD);
		}
	}

	private Date saveScripts(SyncType type, Message[] messages) throws FailedReplicationException {
		try (BufferedWriter writer = newBufferedWriter(get(path(type)))) {
			String s = asList(messages).stream()//
					.sorted(compareReceivedDates())//
					.map(m -> getScripts(m))//
					.collect(joining());
			writer.write(s);
			Date latest = getReceivedDate(messages);
			logger.info("Latest Received Date @ saveScipts = " + latest);
			return latest;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedReplicationException(SCRIPT_CREATION);
		}
	}

	private AndTerm searchTerm(String ext, Date latest) {
		return new AndTerm(searchTerms(ext, latest));
	}

	private SearchTerm[] searchTerms(String ext, Date latest) {
		return new SearchTerm[] { sentLaterThan(latest), subjectHasFileExtensionOf(ext) };
	}

	private SearchTerm sentLaterThan(Date latest) {
		return new DateTerm(DateTerm.GT, latest) {

			private static final long serialVersionUID = 1971028183841474615L;

			@Override
			public boolean match(Message msg) {
				try {
					if (msg.getSentDate().compareTo(latest) > 0)
						return true;
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
	}

	private Session session() {
		logger.info("Starting...");
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		return Session.getInstance(props, null);
	}

	private Store store() throws MessagingException {
		Store store = session().getStore();
		logger.info("Connecting...");
		store.connect("imap.gmail.com", recipient, password);
		return store;
	}

	private SubjectTerm subjectHasFileExtensionOf(String ext) {
		return new SubjectTerm(databaseName + "." + ext);
	}
}
