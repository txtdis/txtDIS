package ph.txtdis.fx.control;

import javafx.scene.control.Label;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("label")
public class AppLabel
	extends Label {
}
