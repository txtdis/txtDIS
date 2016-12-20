package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.BillableDetail;

public class LoadManifestServiceImpl extends AbstractBillableService implements LoadManifestService {

	@Autowired
	private CokeItemService itemService;

	@Autowired
	private RouteService routeService;

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		d.setPriceValue(itemService.getLatestPrice(d.getId(), getOrderDate()));
		return d;
	}

	@Override
	public String getAlternateName() {
		return "L/M";
	}

	@Override
	public Long getLoadManifestId() {
		return getNumId();
	}

	@Override
	public String getLoadManifestPrompt() {
		return "Manifest";
	}

	@Override
	public String getRoutePrompt() {
		return "Route";
	}

	@Override
	public String getSubhead() {
		return "";
	}

	@Override
	public List<BigDecimal> getTotals(List<BillableDetail> l) {
		BigDecimal total = l.stream().map(BillableDetail::getFinalSubtotalValue).reduce(BigDecimal.ZERO, BigDecimal::add);
		return Arrays.asList(total);
	}

	@Override
	public List<String> listRoutes() {
		if (isNew())
			return routeService.listNames();
		return Arrays.asList(get().getSuffix());
	}

	@Override
	public void setRoute(String name) {
		get().setSuffix(name);
	}

	@Override
	public void updateUponReferenceIdValidation(Long id) throws Exception {
		// TODO Auto-generated method stub

	}
}
