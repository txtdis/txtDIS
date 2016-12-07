package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsTruck;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Truck;
import ph.txtdis.repository.EdmsTruckRepository;
import ph.txtdis.util.DateTimeUtils;

@Service("truckService")
public class TruckServiceImpl extends AbstractCreateNameListService<EdmsTruckRepository, EdmsTruck, Truck>
		implements EdmsTruckService {

	private static Logger logger = getLogger(TruckServiceImpl.class);

	@Value("${client.user}")
	private String userName;

	@Override
	public String getCode(Billable i) {
		// TODO
		return null;
	}

	@Override
	public String getCode(PickList p) {
		EdmsTruck e = repository.findByNameIgnoreCase(p.getTruck());
		return e == null ? null : e.getCode();
	}

	@Override
	public String getPlateNo(Billable i) {
		// TODO
		return null;
	}

	@Override
	protected Truck toDTO(EdmsTruck e) {
		Truck t = new Truck();
		t.setName(e.getName());
		t.setCreatedBy(e.getCreatedBy());
		t.setCreatedOn(DateTimeUtils.toZonedDateTimeFrom24HourTimestamp(e.getCreatedOn()));
		return t;
	}

	@Override
	protected EdmsTruck toEntity(Truck t) {
		EdmsTruck e = new EdmsTruck();
		e.setCode(getIncrementedCode());
		e.setName(t.getName());
		e.setDescription("");
		e.setCreatedBy(userName);
		e.setCreatedOn(DateTimeUtils.to24HourTimestampText(t.getCreatedOn()));
		logger.info("\n\t\t\t\tTruck: " + e.getCode() + " - " + e.getName());
		return e;
	}

	private String getIncrementedCode() {
		EdmsTruck e = repository.findFirstByOrderByCodeDesc();
		String code = e.getCode();
		String no = StringUtils.right(code, 2);
		Long id = Long.valueOf(no) + 1;
		code = StringUtils.left(code, 7);
		no = StringUtils.leftPad(id.toString(), 2, "0");
		return code + no;
	}

	@Override
	public Long getId(EdmsInvoice i) {
		EdmsTruck e = repository.findByCode(i.getTruckCode());
		return e.getId();
	}
}