package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CHECKBOX;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Role;

@Scope("prototype")
@Component("roleTable")
public class RoleTable //
	extends AbstractTable<Role> {

	@Autowired
	private Column<Role, Boolean> enabled;

	@Autowired
	private Column<Role, String> role;

	@Override
	protected List<TableColumn<Role, ?>> addColumns() {
		return asList( //
			enabled.ofType(CHECKBOX).build("Role", "enabled"), //
			role.ofType(TEXT).build("Authorization", "role"));
	}
}
