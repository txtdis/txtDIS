package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.domain.EdmsSupervisor;
import ph.txtdis.repository.EdmsAreaRepository;
import ph.txtdis.repository.EdmsDistrictRepository;
import ph.txtdis.repository.EdmsSupervisorRepository;
import ph.txtdis.repository.EdmsTerritoryRepository;

@Service("vendorService")
public class EdmsVendorServiceImpl //
	implements EdmsVendorService {

	@Autowired
	private EdmsSupervisorRepository edmsSupervisorRepository;

	@Autowired
	private EdmsAreaRepository edmsAreaRepository;

	@Autowired
	private EdmsDistrictRepository edmsDistrictRepository;

	@Autowired
	private EdmsTerritoryRepository edmsTerritoryRepository;

	@Override
	public String getSupervisor() {
		return getEdmsSupervisor().getName();
	}

	@Override
	public EdmsSupervisor getEdmsSupervisor() {
		return edmsSupervisorRepository.findOne(1L);
	}

	@Override
	public String getDistrictCode() {
		return edmsDistrictRepository.findOne(1L).getName();
	}

	@Override
	public String getAreaCode() {
		return edmsAreaRepository.findOne(1L).getName();
	}

	@Override
	public String getTerritoryCode() {
		return edmsTerritoryRepository.findOne(1L).getName();
	}

	@Override
	public void save(EdmsSupervisor s) {
		edmsSupervisorRepository.save(s);
	}
}