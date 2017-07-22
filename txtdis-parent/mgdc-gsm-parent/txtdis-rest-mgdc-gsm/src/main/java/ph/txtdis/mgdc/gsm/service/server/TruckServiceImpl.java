package ph.txtdis.mgdc.gsm.service.server;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.TruckEntity;
import ph.txtdis.dto.Truck;
import ph.txtdis.service.AbstractTruckService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;

@Service("truckService")
public class TruckServiceImpl
		extends AbstractTruckService //
		implements ImportedTruckService {

	private static final String TRUCK = "truck";

	@Autowired
	private SavingService<Truck> savingService;

	@Autowired
	private ReadOnlyService<Truck> readOnlyService;

	@Override
	public void importAll() throws Exception {
		List<Truck> l = readOnlyService.module(TRUCK).getList();
		repository.save(toEntities(l));
	}

	@Override
	public List<Truck> list() {
		Iterable<TruckEntity> i = repository.findAll();
		return i == null ? null : stream(i.spliterator(), false).map(e -> toModel(e)).collect(toList());
	}

	@Override
	@Transactional
	public Truck save(Truck t) {
		try {
			t = super.save(t);
			return saveToEdms(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Truck saveToEdms(Truck t) throws Exception {
		savingService.module(TRUCK).save(t);
		return t;
	}
}