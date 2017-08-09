package ph.txtdis.mgdc.ccbpi.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.FRACTION;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.fx.table.SellerFilterContextMenu;
import ph.txtdis.service.VarianceService;
import ph.txtdis.type.Type;

public abstract class AbstractVarianceTable<AS extends VarianceService<SalesItemVariance>> //
	extends AbstractTable<SalesItemVariance> //
	implements VarianceTable<SalesItemVariance> {

	@Autowired
	protected Column<SalesItemVariance, BigDecimal> value;

	@Autowired
	protected Column<SalesItemVariance, Fraction> other, expected, actual, returned, variance;

	@Autowired
	protected Column<SalesItemVariance, Long> id;

	@Autowired
	protected Column<SalesItemVariance, String> item, seller;

	@Autowired
	protected AS service;

	@Autowired
	protected SellerFilterContextMenu<SalesItemVariance> menu;

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> addColumns() {
		buildColumns();
		return columns();
	}

	protected void buildColumns() {
		seller.ofType(TEXT).width(60).build("Route", "seller");
		id.ofType(Type.ID).width(72).build("ID", "id");
		item.ofType(TEXT).width(120).build("Item", "item");
		variance.ofType(FRACTION).build(varianceColumnName(), varianceQtyMethodName());
		value.ofType(CURRENCY).build(valueColumnName(), valueMethodName());
		buildLaunchableColumns();
	}

	protected List<TableColumn<SalesItemVariance, ?>> columns() {
		return asList(seller, id, item, expected, actual, returned, variance, value);
	}

	private String varianceColumnName() {
		return service.getVarianceColumnName();
	}

	private String varianceQtyMethodName() {
		return service.getVarianceQtyMethodName();
	}

	protected String valueColumnName() {
		return "Amount";
	}

	protected String valueMethodName() {
		return "value";
	}

	protected void buildLaunchableColumns() {
		other.ofType(FRACTION).build(otherColumnName(), otherQtyMethodName());
		expected.ofType(FRACTION).build(expectedHeader(), expectedQtyMethodName());
		actual.ofType(FRACTION).build(actualHeader(), actualQtyMethodName());
		returned.ofType(FRACTION).build(returnedHeader(), returnedQtyMethodName());
	}

	protected String otherColumnName() {
		return service.getOtherColumnName();
	}

	protected String otherQtyMethodName() {
		return service.getOtherQtyMethodName();
	}

	protected String expectedHeader() {
		return service.getExpectedColumnName();
	}

	protected String expectedQtyMethodName() {
		return service.getExpectedQtyMethodName();
	}

	protected String actualHeader() {
		return service.getActualColumnName();
	}

	protected String actualQtyMethodName() {
		return service.getActualQtyMethodName();
	}

	protected String returnedHeader() {
		return service.getReturnedColumnName();
	}

	protected String returnedQtyMethodName() {
		return "returnedQtyInFractions";
	}
}
