package ph.txtdis.app;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.QUANTITY;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.type.Type;

@Component("totaledPricedVarianceTableApp")
public class TotaledPricedVarianceTableAppImpl<T> extends AbstractTotaledTableApp<T> {

	@Override
	public HBox addTotalDisplays(int count) {
		totalDisplays = new ArrayList<>();
		for (int i = 1; i <= count; i++)
			buildTotalDisplays(i == count);
		return box.forTableTotals(totalDisplays);
	}

	private void buildTotalDisplays(boolean isLast) {
		Type type = isLast ? CURRENCY : QUANTITY;
		totalDisplays.add(new AppField<BigDecimal>().readOnly().build(type));
	}
}
