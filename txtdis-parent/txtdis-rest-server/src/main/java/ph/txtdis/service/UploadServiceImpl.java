package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.exception.FailedReplicationException;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static javax.mail.Session.getInstance;

@Service("uploadService")
public class UploadServiceImpl //
	implements UploadService {

	@Value("${database.name}")
	private String databaseName;

	@Value("${mail.sender}")
	private String sender;

	@Value("${mail.sender.password}")
	private String password;

	@Value("${mail.recipient}")
	private String recipient;

	@Override
	public void upload(String type) throws FailedReplicationException {
		try {
			String path = getProperty("user.home") + separator;
			String filename = databaseName + "." + type;
			Transport.send(message(path, filename));
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedReplicationException("Upload");
		}
	}

	private MimeMessage message(String path, String filename) throws AddressException, MessagingException, IOException {
		MimeMessage m = new MimeMessage(session());
		m.setFrom(new InternetAddress(sender));
		m.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		m.setSentDate(new Date());
		m.setSubject(filename);
		m.setContent(attachment(path + filename));
		return m;
	}

	private Session session() {
		return getInstance(properties(), authenticator());
	}

	private MimeMultipart attachment(String filename) throws IOException, MessagingException {
		MimeBodyPart m = new MimeBodyPart();
		m.attachFile(filename);
		return new MimeMultipart(m);
	}

	private Properties properties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", "smtp.gmail.com");
		p.put("mail.smtp.port", "587");
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.starttls.enable", "true");
		return p;
	}

	private Authenticator authenticator() {
		return new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, password);
			}
		};
	}
}
