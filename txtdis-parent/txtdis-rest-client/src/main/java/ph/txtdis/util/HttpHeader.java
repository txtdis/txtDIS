package ph.txtdis.util;

import org.springframework.util.MultiValueMap;

public interface HttpHeader {

	MultiValueMap<String, String> headers();

	String password();
}