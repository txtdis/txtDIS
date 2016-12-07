package ph.txtdis.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.table.GoodRmaReplacementTable;
import ph.txtdis.service.GoodRmaReplacementService;

@Lazy
@Component("goodRmaReplacementApp")
public class GoodRmaReplacementAppImpl //
		extends AbstractRmaReplacementApp<GoodRmaReplacementService, GoodRmaReplacementTable>
		implements GoodRmaReplacementApp {
}
