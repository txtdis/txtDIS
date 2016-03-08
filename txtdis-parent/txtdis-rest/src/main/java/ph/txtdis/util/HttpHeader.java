package ph.txtdis.util;

import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component("httpHeader")
public class HttpHeader {

	private MultiValueMap<String, String> headers;

	public MultiValueMap<String, String> headers() {
		if (SpringUtils.isAuthenticated())
			return headers;
		return headers = createHeaders();
	}

	private String auth() {
		return SpringUtils.username() + ":" + SpringUtils.password();
	}

	private String authHeader() {
		return "Basic " + new String(encodedAuth());
	}

	private MultiValueMap<String, String> createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader());
		return headers;
	}

	private byte[] encodedAuth() {
		return Base64.getEncoder().encode(auth().getBytes());
	}
}
