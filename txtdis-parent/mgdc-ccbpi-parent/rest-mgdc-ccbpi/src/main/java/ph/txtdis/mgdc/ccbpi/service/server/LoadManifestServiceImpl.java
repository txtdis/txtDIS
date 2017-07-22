package ph.txtdis.mgdc.ccbpi.service.server;

import static org.apache.log4j.Logger.getLogger;

import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.repository.LoadManifestRepository;
import ph.txtdis.type.TransactionDirectionType;

@Service("loadManifestService")
public class LoadManifestServiceImpl //
		extends AbstractSpunSavedBillableService //
		implements LoadManifestService {

	private static Logger logger = getLogger(LoadManifestServiceImpl.class);

	@Autowired
	private LoadManifestRepository loadManifestRepository;

	@Override
	public Billable find(Long shipment) {
		BillableEntity b = loadManifestRepository.findByCustomerNullAndBookingIdAndSuffixNull(shipment);
		return toModel(b);
	}

	@Override
	public List<BomEntity> getBomList(LocalDate start, LocalDate end) {
		List<BomEntity> boms = toBomList(TransactionDirectionType.OUTGOING, list(start, end));
		logger.info("\n    LoadManifestBoms = " + boms);
		return boms;
	}

	@Override
	public List<BillableEntity> list(LocalDate start, LocalDate end) {
		List<BillableEntity> l = loadManifestRepository.findByCustomerNullAndBookingIdNotNullAndSuffixNullAndOrderDateBetween( //
				start, end);
		logger.info("\n    LoadManifestList = " + l);
		return l;
	}

	@Override
	protected BillableEntity firstEntity() {
		return loadManifestRepository.findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return loadManifestRepository.findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return loadManifestRepository.findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullOrderByIdDesc();
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return loadManifestRepository.findFirstByCustomerNullAndBookingIdNotNullAndSuffixNullAndIdLessThanOrderByIdDesc(id);
	}
}