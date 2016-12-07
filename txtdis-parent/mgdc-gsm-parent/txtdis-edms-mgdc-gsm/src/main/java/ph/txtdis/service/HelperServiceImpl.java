package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsHelper;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;
import ph.txtdis.repository.EdmsHelperRepository;

@Service("helperService")
public class HelperServiceImpl implements HelperService {

	@Autowired
	private EdmsHelperRepository edmsHelperRepository;

	@Override
	public String getCode(PickList p) {
		try {
			EdmsHelper e = edmsHelperRepository.findByNameStartingWithIgnoreCase(p.getHelper());
			return e.getCode();
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public String getCode(EdmsSeller s) {
		try {
			EdmsHelper e = edmsHelperRepository.findByNameIgnoreCase(s.getHelper());
			return e.getCode();
		} catch (Exception e) {
			return "";
		}
	}
}