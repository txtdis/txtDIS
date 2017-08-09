package ph.txtdis.app;

import javafx.scene.control.ScrollPane;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.TextAreaDisplay;
import ph.txtdis.service
	.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

public abstract class AbstractRemarkedKeyedApp<
	AS extends
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<PK>,
	T extends Keyed<PK>,
	PK,
	ID>
	extends AbstractKeyedApp<AS, T, PK, ID> {

	@Autowired
	protected TextAreaDisplay remarksDisplay;

	protected void remarksGridNodes(int row, int columnSpan) {
		labelGridNode("Remarks", 0, row);
		gridPane.add(remarksDisplay(), 1, row, columnSpan, 5);
	}

	private ScrollPane remarksDisplay() {
		remarksDisplay.build();
		remarksDisplay.makeEditable();
		return remarksDisplay.get();
	}

	@Override
	protected void save() {
		if (service.isNew())
			service.setRemarks(remarksDisplay.getValue());
		super.save();
	}
}
