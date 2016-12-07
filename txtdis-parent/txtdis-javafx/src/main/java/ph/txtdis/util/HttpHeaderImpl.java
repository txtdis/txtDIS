package ph.txtdis.util;

import org.springframework.stereotype.Component;

@Component("httpHeader")
public class HttpHeaderImpl extends AbstractHttpHeader {

	@Override
	public String password() {
		return credentialService.username() + ":" + credentialService.password();
	}
}
