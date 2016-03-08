package ph.txtdis.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Bank;
import ph.txtdis.fx.table.BankTable;
import ph.txtdis.service.BankService;

@Lazy
@Component("bankApp")
public class BankApp extends AbstractTableApp<BankTable, BankService, Bank> {
}
