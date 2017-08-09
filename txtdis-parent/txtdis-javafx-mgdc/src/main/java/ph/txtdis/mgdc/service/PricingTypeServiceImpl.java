package ph.txtdis.mgdc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.PricingType;
import ph.txtdis.info.Information;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.UserUtils.username;

@Service("pricingTypeService")
public class PricingTypeServiceImpl
	implements PricingTypeService {

	@Autowired
	public ClientTypeMap typeMap;

	@Autowired
	private RestClientService<PricingType> restClientService;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public RestClientService<PricingType> getRestClientService() {
		return restClientService;
	}

	@Override
	public RestClientService<PricingType> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + getHeaderName();
	}

	@Override
	public String getHeaderName() {
		return "Pricing Type";
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
		return restClientService.module(getModuleName()).save(entity);
	}

	@Override
	public String getModuleName() {
		return "pricingType";
	}
}
