package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.domain.TruckEntity;
import ph.txtdis.dto.Truck;
import ph.txtdis.service.AbstractTruckService;
import ph.txtdis.service.RestClientService;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Service("truckService")
public class TruckServiceImpl
	extends AbstractTruckService //
	implements ImportedTruckService {

	private static final String TRUCK = "truck";

	@Autowired
	private RestClientService<Truck> restClientService;

	@Override
	public void importAll() throws Exception {
		List<Truck> l = restClientService.module(TRUCK).getList();
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
		restClientService.module(TRUCK).save(t);
		return t;
	}
}