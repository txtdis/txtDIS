package ph.txtdis.mgdc.ccbpi.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Warehouse;
import ph.txtdis.service.ReadOnlyService;

@Service("warehouseService")
public class WarehouseServiceImpl //
		implements WarehouseService {

	@Autowired
	private ReadOnlyService<Warehouse> readOnlyService;

	@Override
	public ReadOnlyService<Warehouse> getListedReadOnlyService() {
		return readOnlyService;
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
