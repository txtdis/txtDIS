package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("itemApp")
public class ItemAppImpl extends AbstractTabbedItemApp {
}
