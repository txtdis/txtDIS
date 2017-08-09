package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Truck;
import ph.txtdis.info.Information;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.UserUtils.username;

@Service("truckService")
public class TruckServiceImpl
	implements TruckService {

	@Autowired
	private RestClientService<Truck> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + TruckService.super.getTitleName();
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
		return restClientService.module(getModuleName()).save(t);
	}

	@Override
	public String getModuleName() {
		return "truck";
	}

	@Override
	public RestClientService<Truck> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public RestClientService<Truck> getRestClientService() {
		return restClientService;
	}
}
