package ph.txtdis.fx.control;

import javafx.scene.control.Label;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Lazy
@Scope("prototype")
@Component("label")
public class AppLabel
	extends Label {
}
