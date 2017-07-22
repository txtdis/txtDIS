package ph.txtdis.service;

import static ph.txtdis.util.Code.addZeroes;
import static ph.txtdis.util.DateTimeUtils.to24HourTimestampText;
import static ph.txtdis.util.DateTimeUtils.toZonedDateTimeFrom24HourTimestamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsTruck;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Truck;
import ph.txtdis.repository.EdmsTruckRepository;

@Service("truckService")
public class EdmsTruckServiceImpl //
		extends AbstractCreateNameListService<EdmsTruckRepository, EdmsTruck, Truck> //
		implements EdmsTruckService {

	@Value("${client.user}")
	private String username;

	@Value("${client.truck}")
	private String description;

	@Value("${prefix.truck}")
	private String truckPrefix;

	@Override
	public EdmsTruck findEntityByPlateNo(String no) {
		return no == null || no.isEmpty() ? null : //
				repository.findByNameIgnoreCase(no);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Long getId(EdmsInvoice i) {
		EdmsTruck e = truck(i);
		return e == null ? null : e.getId();
	}

	private EdmsTruck truck(EdmsInvoice i) {
		return i == null ? null : repository.findByCodeIgnoreCase(i.getTruckCode());
	}

	@Override
	public String getCode(Keyed<Long> r) {
		return r == null || r.getId() == null ? "" : truckPrefix + addZeroes(2, r.getId().toString());
	}

	@Override
	protected EdmsTruck toEntity(Truck t) {
		EdmsTruck e = new EdmsTruck();
		e.setCode(getCode(t));
		e.setName(t.getName());
		e.setDescription(getDescription());
		e.setCreatedBy(username);
		e.setCreatedOn(to24HourTimestampText(t.getCreatedOn()));
		e.setModifiedBy("");
		e.setModifiedOn("");
		return e;
	}

	@Override
	protected Truck toModel(EdmsTruck e) {
		Truck t = new Truck();
		t.setName(e.getName());
		t.setCreatedBy(e.getCreatedBy());
		t.setCreatedOn(toZonedDateTimeFrom24HourTimestamp(e.getCreatedOn()));
		return t;
	}
}