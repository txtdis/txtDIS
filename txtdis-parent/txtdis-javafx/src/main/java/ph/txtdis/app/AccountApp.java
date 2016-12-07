package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Account;
import ph.txtdis.fx.table.AccountTable;
import ph.txtdis.service.AccountService;

@Scope("prototype")
@Component("accountApp")
public class AccountApp extends AbstractTableApp<AccountTable, AccountService, Account> implements Launchable {

	@Override
	public void actOn(String... ids) {
		try {
			open(ids);
		} catch (Exception e) {
			dialog.show(e).addParent(this).start();
		}
	}

	@Override
	public void refresh() {
		try {
			table.items(service.getSellerHistory());
			updateTitleAndHeader();
			setFocus();
		} catch (Exception e) {
			dialog.show(e).addParent(this).start();
		}
	}

	private void open(String[] ids) throws Exception {
		service.find(ids);
		refresh();
	}
}
