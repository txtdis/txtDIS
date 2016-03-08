package ph.txtdis.fx.table;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Bank;
import ph.txtdis.fx.dialog.BankDialog;

@Lazy
@Component("bankTable")
public class BankTable extends NameListTable<Bank, BankDialog> {
}
