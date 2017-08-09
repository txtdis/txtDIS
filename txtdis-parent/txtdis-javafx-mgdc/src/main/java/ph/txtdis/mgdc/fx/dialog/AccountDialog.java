package ph.txtdis.mgdc.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Account;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.service.RouteService;

import java.util.Arrays;
import java.util.List;

@Scope("prototype")
@Component("accountDialog")
public class AccountDialog //
	extends AbstractFieldDialog<Account> {

	@Autowired
	private LabeledCombo<String> sellerCombo;

	@Autowired
	private LabeledDatePicker startDatePicker;

	@Autowired
	private RouteService service;

	@Override
	protected List<InputNode<?>> addNodes() {
		sellerCombo.name("Seller").items(getSellers()).build();
		startDatePicker.name("Start Date");
		return Arrays.asList(sellerCombo, startDatePicker);
	}

	private List<String> getSellers() {
		try {
			return service.listUsers();
		} catch (Exception e) {
			e.printStackTrace();
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected Account createEntity() {
		return save(new Account());
	}

	private Account save(Account ib) {
		try {
			return service.save(sellerCombo.getValue(), startDatePicker.getValue());
		} catch (Exception | Information e) {
			e.printStackTrace();
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Seller";
	}
}
