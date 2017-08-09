package ph.txtdis.util;

import org.springframework.stereotype.Component;

import static ph.txtdis.util.UserUtils.username;

@Component("httpHeader")
public class HttpHeaderImpl
	extends AbstractHttpHeader {

	@Override
	public String password() {
		return username() + ":" + UserUtils.password();
	}
}
