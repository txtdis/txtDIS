package ph.txtdis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import ph.txtdis.service.CredentialService;

@Component("springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {

	@Autowired
	private CredentialService credentialService;

	@Override
	public String getCurrentAuditor() {
		if (!credentialService.isAuthenticated())
			return "SYSGEN";
		return credentialService.username();
	}
}
