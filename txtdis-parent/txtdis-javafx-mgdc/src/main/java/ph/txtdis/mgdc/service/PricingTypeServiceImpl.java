package ph.txtdis.mgdc.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.PricingType;
import ph.txtdis.info.Information;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.util.ClientTypeMap;

@Service("pricingTypeService")
public class PricingTypeServiceImpl implements PricingTypeService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<PricingType> readOnlyService;

	@Autowired
	private SavingService<PricingType> savingService;

	@Autowired
	public ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public String getHeaderName() {
		return "Pricing Type";
	}

	@Override
	public String getModuleName() {
		return "pricingType";
	}

	@Override
	public ReadOnlyService<PricingType> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + getHeaderName();
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
	public PricingType save(String name) throws Information, Exception {
		PricingType entity = new PricingType();
		entity.setName(name);
		return savingService.module(getModuleName()).save(entity);
	}
}
