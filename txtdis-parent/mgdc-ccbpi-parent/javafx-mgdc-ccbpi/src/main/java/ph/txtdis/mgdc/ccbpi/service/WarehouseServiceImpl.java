package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.service.RestClientService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service("warehouseService")
public class WarehouseServiceImpl //
	implements WarehouseService {

	@Autowired
	private RestClientService<Warehouse> restClientService;

	@Override
	public RestClientService<Warehouse> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getModuleName() {
		return "warehouse";
	}

	@Override
	public List<String> listNames() {
		try {
			return list().stream().map(w -> w.getName()).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void reset() {
	}
}
