package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Bank;
import ph.txtdis.service.BankService;

@Scope("prototype")
@Component("bankDialog")
public class BankDialog extends NameListDialog<Bank, BankService> {

	@Override
	protected Bank createEntity() {
		try {
			return service.save(nameField.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Bank";
	}
}