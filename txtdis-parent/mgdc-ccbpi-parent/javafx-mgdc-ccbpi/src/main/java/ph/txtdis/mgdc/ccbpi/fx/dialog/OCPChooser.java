package ph.txtdis.mgdc.ccbpi.fx.dialog;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

import static java.io.File.separator;
import static java.lang.System.getProperty;

@Scope("prototype")
@Component("ocpChooser")
public class OCPChooser {

	public File showOpenDialog(Stage stage) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose OCP File");
		fc.getExtensionFilters().add(new ExtensionFilter("OCP Workbook", "*.xlsx"));
		fc.setInitialDirectory(new File(getProperty("user.home") + separator));
		fc.setSelectedExtensionFilter(fc.getExtensionFilters().get(0));
		return fc.showOpenDialog(stage);
	}
}
