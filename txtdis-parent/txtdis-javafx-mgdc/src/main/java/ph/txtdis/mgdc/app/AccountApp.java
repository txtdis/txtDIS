package ph.txtdis.mgdc.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.dto.Account;
import ph.txtdis.mgdc.fx.table.AccountTable;
import ph.txtdis.mgdc.service.AccountService;

@Scope("prototype")
@Component("accountApp")
public class AccountApp extends AbstractTableApp<AccountTable, AccountService, Account> implements LaunchableApp {

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
			refreshTitleAndHeader();
			goToDefaultFocus();
		} catch (Exception e) {
			dialog.show(e).addParent(this).start();
		}
	}

	private void open(String[] ids) throws Exception {
		service.find(ids);
		refresh();
	}
}
