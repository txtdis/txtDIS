package ph.txtdis.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.table.BadRmaReplacementTable;
import ph.txtdis.service.BadRmaReplacementService;

@Lazy
@Component("badRmaReplacementApp")
public class BadRmaReplacementAppImpl //
		extends AbstractRmaReplacementApp<BadRmaReplacementService, BadRmaReplacementTable>
		implements BadRmaReplacementApp {
}
