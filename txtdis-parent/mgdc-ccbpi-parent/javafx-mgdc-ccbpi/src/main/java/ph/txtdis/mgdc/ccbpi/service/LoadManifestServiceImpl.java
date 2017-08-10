package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.type.PriceType;

import java.time.LocalDate;

@Service("loadManifestService")
public class LoadManifestServiceImpl //
	extends AbstractCokeBillableService //
	implements LoadManifestService {

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByOrderNo(String id) throws Exception {
		Billable b = getRestClientService().module(getModuleName()).getOne("/lm?shipment=" + id);
		return throwNotFoundExceptionIfNull(b, id);
	}

	@Override
	public String getModuleName() {
		return "loadManifest";
	}

	@Override
	public String getAlternateName() {
		return "L/M";
	}

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		d.setPriceValue(itemService.getCurrentPriceValue(d.getId(), getOrderDate(), PriceType.PURCHASE));
		return d;
	}

	@Override
	public void setShipmentDateAndId(LocalDate d, Long id) {
		setOrderDate(d);
		setBookingId(id);
	}
}
