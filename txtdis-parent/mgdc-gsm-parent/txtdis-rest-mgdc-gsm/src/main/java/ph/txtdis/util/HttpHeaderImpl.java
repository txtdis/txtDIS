package ph.txtdis.util;

import org.springframework.stereotype.Component;

@Component("httpHeader")
public class HttpHeaderImpl extends AbstractHttpHeader implements MainHttpHeader {

	@Override
	public String password() {
		return "txtdis:txtDIS4eDMS@MySQL";
	}
}
