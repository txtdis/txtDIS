package ph.txtdis.util;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import static ph.txtdis.util.UserUtils.isAuthenticated;
import static ph.txtdis.util.UserUtils.username;

@Component("springSecurityAuditorAware")
public class SpringSecurityAuditorAware
	implements AuditorAware<String> {

	@Override
	public String getCurrentAuditor() {
		if (!isAuthenticated())
			return "SYSGEN";
		return username();
	}
}
