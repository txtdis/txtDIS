package ph.txtdis.mgdc.gsm.util;

import org.springframework.stereotype.Component;

import ph.txtdis.util.AbstractHttpHeader;

@Component("httpHeader")
public class HttpHeaderImpl extends AbstractHttpHeader implements MainHttpHeader {

	@Override
	public String password() {
		return "txtdis:txtDIS4eDMS@MySQL";
	}
}
