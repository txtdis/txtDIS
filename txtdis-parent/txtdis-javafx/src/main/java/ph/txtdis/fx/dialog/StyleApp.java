package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.User;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.BaseColorPicker;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.UserService;
import ph.txtdis.util.StyleSheet;
import ph.txtdis.util.UserUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.util.UserUtils.user;

@Scope("prototype")
@Component("styleApp")
public class StyleApp
	extends AbstractInputDialog {

	@Autowired
	protected AppGridPane grid;

	@Autowired
	private AppCombo<String> fontCombo;

	@Autowired
	private BaseColorPicker colorPicker;

	@Autowired
	private StyleSheet styleSheet;

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

	private String systemFont() {
		return Font.getDefault().getFamily();
	}

	@Override
	public void goToDefaultFocus() {
		colorPicker.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(changeButton(), closeButton());
	}

	private AppButton changeButton() {
		AppButton changeButton = button.large("Change").build();
		changeButton.onAction(event -> trySavingChanges());
		return changeButton;
	}

	private void trySavingChanges() {
		try {
			saveChanges();
			close();
		} catch (Information i) {
			messageDialog().show(i).addParent(this).start();
		} catch (Exception e) {
			messageDialog().show(e).addParent(this).start();
		}
	}

	private void saveChanges() throws SuccessfulSaveInfo, Exception {
		updateStyle();
		styleSheet.update(style);
		updateUser();
	}

	private void updateStyle() {
		setStyle();
		style.setBase(rgb());
		style.setFont(font());
	}

	private void updateUser() throws SuccessfulSaveInfo, Exception {
		user.setStyle(style);
		user = userService.save(user);
		UserUtils.updateUser(user);
	}

	private void setStyle() {
		style = user().getStyle();
		if (style == null)
			style = new Style();
	}

	private String rgb() {
		Color color = colorPicker.getValue();
		return String.format("rgba(%d, %d, %d, %f)", (int) Math.round(color.getRed() * 255),
			(int) Math.round(color.getGreen() * 255),
			(int) Math.round(color.getBlue() * 255), color.getOpacity());
	}

	private String font() {
		return fontCombo.getValue();
	}

	@Override
	protected String headerText() {
		return "Change UI Theme";
	}

	@Override
	protected List<Node> nodes() {
		return asList(header(), grid(), buttonBox());
	}

	private AppGridPane grid() {
		grid.getChildren().clear();
		grid.add(label.field("Color"), 0, 0);
		grid.add(colorPicker, 1, 0);
		grid.add(label.field("Font"), 0, 1);
		grid.add(fontCombo(), 1, 1);
		return grid;
	}

	private Node fontCombo() {
		fontCombo.items(Font.getFamilies());
		fontCombo.select(systemFont());
		return fontCombo;
	}
}