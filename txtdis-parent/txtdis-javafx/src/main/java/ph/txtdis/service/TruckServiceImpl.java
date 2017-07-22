package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Truck;
import ph.txtdis.info.Information;
import ph.txtdis.util.ClientTypeMap;

@Service("truckService")
public class TruckServiceImpl implements TruckService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Truck> readOnlyService;

	@Autowired
	private SavingService<Truck> savingService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public String getModuleName() {
		return "truck";
	}

	@Override
	public ReadOnlyService<Truck> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + TruckService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<String> listNames() {
		try {
			return list().stream().map(r -> r.getName()).sorted().collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public Truck save(String name) throws Information, Exception {
		Truck t = new Truck();
		t.setName(name);
		return savingService.module(getModuleName()).save(t);
	}
}
