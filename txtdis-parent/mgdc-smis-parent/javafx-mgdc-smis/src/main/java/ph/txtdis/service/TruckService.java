package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Truck;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.util.TypeMap;

@Service("truckService")
public class TruckService implements Iconed, Listed<Truck>, SavedByName<Truck>, UniquelyNamed<Truck> {

	@Autowired
	private ReadOnlyService<Truck> readOnlyService;

	@Autowired
	private SavingService<Truck> savingService;

	@Autowired
	private ServerService server;

	@Autowired
	public TypeMap typeMap;

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	public boolean isOffSite() {
		return server.isOffSite();
	}

	@Override
	public String getModule() {
		return "truck";
	}

	@Override
	public ReadOnlyService<Truck> getReadOnlyService() {
		return readOnlyService;
	}

	public List<String> listNames() {
		try {
			return list().stream().map(r -> r.getName()).sorted().collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Truck save(String name) throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Truck t = new Truck();
		t.setName(name);
		return savingService.module(getModule()).save(t);
	}
}
