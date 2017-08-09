package ph.txtdis.util;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.Base64;

public abstract class AbstractHttpHeader
	implements HttpHeader {

	private MultiValueMap<String, String> headers;

	@Override
	public MultiValueMap<String, String> headers() {
		if (headers == null)
			headers = createHeaders();
		return headers;
	}

	private MultiValueMap<String, String> createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", credentials());
		return headers;
	}

	private String credentials() {
		byte[] credentials = password().getBytes();
		credentials = Base64.getEncoder().encode(credentials);
		return "Basic " + new String(credentials);
	}
}
