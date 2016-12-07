package ph.txtdis.fx.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.service.StockTakeService;
import ph.txtdis.type.QualityType;

@Scope("prototype")
@Component("stockTakeDialog")
public class StockTakeDialogImpl extends AbstractAllItemInCasesAndBottlesInputDialog<StockTakeService, StockTakeDetail>
		implements StockTakeDialog {

	@Autowired
	private LabeledCombo<QualityType> qualityCombo;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(super.addNodes());
		l.add(qualityCombo());
		return l;
	}

	private LabeledCombo<QualityType> qualityCombo() {
		qualityCombo.name("Quality").build();
		qualityCombo.items(Arrays.asList(QualityType.values()));
		qualityCombo.setOnAction(e -> ((StockTakeService) service).setQuality(qualityCombo.getValue()));
		return qualityCombo;
	}
}
