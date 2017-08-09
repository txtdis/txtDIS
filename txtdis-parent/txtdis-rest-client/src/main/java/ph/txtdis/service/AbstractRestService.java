package ph.txtdis.service;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;

import static org.apache.http.impl.client.HttpClients.custom;

public class AbstractRestService<RSS extends RestServerService>
	extends RestTemplate
	implements RestService {

	private final RSS serverService;

	private boolean init;

	public AbstractRestService(RSS serverService) {
		this.serverService = serverService;
	}

	@Override
	public RestService init() {
		if (!init) {
			setRequestFactory(requestFactory());
			init = true;
		}
		return this;
	}

	private ClientHttpRequestFactory requestFactory() {
		return new HttpComponentsClientHttpRequestFactory(httpClient());
	}

	private HttpClient httpClient() {
		return custom().setSSLSocketFactory(socketFactory()).build();
	}

	private SSLConnectionSocketFactory socketFactory() {
		try {
			return new SSLConnectionSocketFactory(
				new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy())
					.loadKeyMaterial(keyStore(), txtDIS()).build());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private KeyStore keyStore() {
		try {
			KeyStore k = KeyStore.getInstance("pkcs12");
			k.load(keyStoreStream(), txtDIS());
			return k;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private char[] txtDIS() {
		return "txtDIS".toCharArray();
	}

	private FileInputStream keyStoreStream() throws FileNotFoundException {
		File file = new File(serverService.getKeystore());
		return new FileInputStream(file);
	}
}
