package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Item;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Scope("prototype")
@Component("itemInputtedDialog")
public class VendorItemInputtedDialogImpl extends AbstractItemInputtedDialog {

	@Override
	public Item validateItemExists() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Item i = itemService.findByVendorId(getId());
		itemNameDisplay.setValue(i.getName());
		return i;
	}
}
