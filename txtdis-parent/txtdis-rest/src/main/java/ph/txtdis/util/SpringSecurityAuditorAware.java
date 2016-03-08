package ph.txtdis.util;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {

	@Override
	public String getCurrentAuditor() {
		if (!SpringUtils.isAuthenticated())
			return "SYSGEN";
		return SpringUtils.username();
	}
}
