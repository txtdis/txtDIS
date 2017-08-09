package ph.txtdis.mgdc.gsm.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ph.txtdis.util.AbstractHttpHeader;

@Component("inventoryHttpHeader")
public class InventoryHttpHeaderImpl
	extends AbstractHttpHeader
	implements InventoryHttpHeader {

	@Value("${inventory.server.password}")
	private String password;

	@Override
	public String password() {
		return password;
	}
}
