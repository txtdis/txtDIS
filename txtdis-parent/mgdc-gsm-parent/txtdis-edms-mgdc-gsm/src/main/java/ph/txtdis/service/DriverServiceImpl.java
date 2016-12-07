package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsDriver;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;
import ph.txtdis.repository.EdmsDriverRepository;

@Service("driverService")
public class DriverServiceImpl implements DriverService {

	@Autowired
	private EdmsDriverRepository edmsDriverRepository;

	@Override
	public String getCode(PickList p) {
		EdmsDriver e = getByName(p);
		return e == null ? null : e.getCode();
	}

	private EdmsDriver getByName(PickList p) {
		return edmsDriverRepository.findByNameStartingWithIgnoreCase(p.getDriver());
	}

	@Override
	public String getCode(EdmsSeller s) {
		EdmsDriver e = edmsDriverRepository.findByNameIgnoreCase(s.getDriver());
		return e == null ? null : e.getCode();
	}

	@Override
	public String getName(PickList p) {
		EdmsDriver e = getByName(p);
		return e == null ? null : e.getName();
	}
}