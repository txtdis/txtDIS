package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.type.TransactionDirectionType;

@Service("loadManifestService")
public class LoadManifestServiceImpl extends AbstractSpunBillableService implements LoadManifestService {

	private static Logger logger = getLogger(LoadManifestServiceImpl.class);

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository
				.findFirstByCustomerNullAndNumIdNotNullAndSuffixNullAndOrderDateOrderByIdAsc(d.toLocalDate());
		return toDTO(b);
	}

	@Override
	public Billable find(Long shipment) {
		BillableEntity b = repository.findByCustomerNullAndNumIdAndSuffixNull(shipment);
		return toDTO(b);
	}

	@Override
	public List<BomEntity> getBomList(LocalDate start, LocalDate end) {
		List<BillableEntity> l = repository.findByCustomerNullAndNumIdNotNullAndSuffixNullAndOrderDateBetween(start, end);
		logger.info("\n    LoadManifestList = " + l);
		List<BomEntity> boms = toBomList(TransactionDirectionType.OUTGOING, l);
		logger.info("\n    LoadManifestBoms = " + boms);
		return boms;
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository.findFirstByCustomerNullAndNumIdNotNullAndSuffixNullAndIdGreaterThanOrderByIdAsc(id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository.findFirstByCustomerNullAndNumIdNotNullAndSuffixNullAndIdLessThanOrderByIdDesc(id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByCustomerNullAndNumIdNotNullAndSuffixNullOrderByIdAsc();
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByCustomerNullAndNumIdNotNullAndSuffixNullOrderByIdDesc();
		return toDTO(b);
	}
}