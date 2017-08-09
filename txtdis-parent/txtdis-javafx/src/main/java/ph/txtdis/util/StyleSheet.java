package ph.txtdis.util;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Style;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

@Component
@SuppressWarnings("restriction")
public class StyleSheet {

	private static String MODENA_STYLESHEET_URL = ButtonSkin.class.getResource("modena/modena.css").toExternalForm();

	private static String MODENA_STYLESHEET_BASE = MODENA_STYLESHEET_URL.substring(0,
		MODENA_STYLESHEET_URL.lastIndexOf('/') + 1);

	private static StringURLStreamHandlerFactory factory;

	private String content = "";

	private String styleBase = "";

	private Style style;

	{
		if (factory == null)
			factory = new StringURLStreamHandlerFactory();
		URL.setURLStreamHandlerFactory(factory);
	}

	public Style getStyle() {
		return style;
	}

	public void update(Style style) {
		this.style = style;

		styleBase = MODENA_STYLESHEET_BASE;
		content = loadUrl(MODENA_STYLESHEET_URL) + "\n.root {\n";
		content += " -fx-base: " + getColor() + ";\n";
		content += " -fx-font: 11pt \"" + getFont() + "\";\n" + "}\n";

		Application.setUserAgentStylesheet("internal:stylesheet" + Math.random() + ".css");
	}

	private static String loadUrl(String url) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}

	private String getColor() {
		return style == null ? rgb() : style.getBase();
	}

	private String getFont() {
		return style == null ? Font.getDefault().getFamily() : style.getFont();
	}

	private String rgb() {
		Color color = Color.SLATEBLUE;
		return String.format("rgba(%d, %d, %d, %f)", (int) Math.round(color.getRed() * 255),
			(int) Math.round(color.getGreen() * 255), (int) Math.round(color.getBlue() * 255), color.getOpacity());
	}

	private class StringURLConnection
		extends URLConnection {
		public StringURLConnection(URL url) {
			super(url);
		}

		@Override
		public void connect() throws IOException {
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(content.getBytes("UTF-8"));
		}
	}

	private class StringURLStreamHandlerFactory
		implements URLStreamHandlerFactory {
		URLStreamHandler streamHandler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL url) throws IOException {
				if (url.toString().toLowerCase().endsWith(".css"))
					return new StringURLConnection(url);
				else
					return new URL(styleBase + url.getFile()).openConnection();
			}
		};

		@Override
		public URLStreamHandler createURLStreamHandler(String protocol) {
			return "internal".equals(protocol) ? streamHandler : null;
		}
	}
}
