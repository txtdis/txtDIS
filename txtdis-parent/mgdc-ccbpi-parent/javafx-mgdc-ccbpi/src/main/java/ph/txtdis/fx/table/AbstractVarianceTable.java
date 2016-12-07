package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.FRACTION;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.service.VarianceService;
import ph.txtdis.type.Type;

public abstract class AbstractVarianceTable<AS extends VarianceService<SalesItemVariance>>
		extends AbstractTableView<SalesItemVariance> implements VarianceTable<SalesItemVariance> {

	@Autowired
	private Column<SalesItemVariance, Long> id;

	@Autowired
	private Column<SalesItemVariance, String> item;

	@Autowired
	private Column<SalesItemVariance, BigDecimal> value;

	@Autowired
	private Column<SalesItemVariance, Fraction> expected, actual, variance;

	@Autowired
	protected Column<SalesItemVariance, String> seller;

	@Autowired
	protected AS service;

	@Autowired
	protected SellerFilterContextMenu<SalesItemVariance> menu;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				seller.ofType(TEXT).width(120).build("Seller", "seller"), //
				id.ofType(Type.ID).build("ID", "id"), //
				item.ofType(TEXT).build("Item", "item"), //
				expected.ofType(FRACTION).build(service.getExpectedHeader(), "expectedQtyInFractions"), //
				actual.ofType(FRACTION).build(service.getActualHeader(), "actualQtyInFractions"), //
				variance.ofType(FRACTION).build(service.getVarianceHeader(), "varianceQtyInFractions"), //
				value.ofType(CURRENCY).build("Amount", "value") //
		);
	}
}
