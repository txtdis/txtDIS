package ph.txtdis.fx.dialog;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.User;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.BaseColorPicker;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.UserService;
import ph.txtdis.util.StyleSheet;

@Scope("prototype")
@Component("styleApp")
public class StyleApp extends AbstractInputDialog {

	@Autowired
	protected AppGridPane grid;

	@Autowired
	private AppButtonImpl changeButton;

	@Autowired
	private AppCombo<String> fontCombo;

	@Autowired
	private BaseColorPicker colorPicker;

	@Autowired
	private LabelFactory label;

	@Autowired
	private StyleSheet styleSheet;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private UserService userService;

	private Style style;

	private User user;

	@Override
	public void refresh() {
		colorPicker.reset();
		fontCombo.select(systemFont());
		goToDefaultFocus();
	}

	@Override
	public void goToDefaultFocus() {
		colorPicker.requestFocus();
	}

	private Button changeButton() {
		changeButton.large("Change").build();
		changeButton.onAction(event -> trySavingChanges());
		return changeButton;
	}

	private String font() {
		return fontCombo.getValue();
	}

	private Node fontCombo() {
		fontCombo.items(Font.getFamilies());
		fontCombo.select(systemFont());
		return fontCombo;
	}

	private AppGridPane grid() {
		grid.getChildren().clear();
		grid.add(label.field("Color"), 0, 0);
		grid.add(colorPicker, 1, 0);
		grid.add(label.field("Font"), 0, 1);
		grid.add(fontCombo(), 1, 1);
		return grid;
	}

	private String rgb() {
		Color color = colorPicker.getValue();
		return String.format("rgba(%d, %d, %d, %f)", (int) Math.round(color.getRed() * 255), (int) Math.round(color.getGreen() * 255),
				(int) Math.round(color.getBlue() * 255), color.getOpacity());
	}

	private void saveChanges() throws SuccessfulSaveInfo, Exception {
		updateStyle();
		styleSheet.update(style);
		updateUser();
	}

	private void setStyle() {
		style = user().getStyle();
		if (style == null)
			style = new Style();
	}

	private String systemFont() {
		return Font.getDefault().getFamily();
	}

	private void trySavingChanges() {
		try {
			saveChanges();
			close();
		} catch (Exception | Information e) {
			dialog.show((Exception) e).addParent(this).start();
		}
	}

	private void updateStyle() {
		setStyle();
		style.setBase(rgb());
		style.setFont(font());
	}

	private void updateUser() throws SuccessfulSaveInfo, Exception {
		user.setStyle(style);
		user = userService.save(user);
		credentialService.updateUser(user);
	}

	private User user() {
		return user = credentialService.user();
	}

	@Override
	protected Button[] buttons() {
		return new Button[] { changeButton(), closeButton() };
	}

	@Override
	protected String headerText() {
		return "Change UI Theme";
	}

	@Override
	protected List<Node> nodes() {
		return Arrays.asList(header(), grid(), buttonBox());
	}
}