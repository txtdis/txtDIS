package ph.txtdis.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.app.Startable;

@Component
public interface TypeMap {

	public String icon(Startable a);

	public String icon(String name);

	ParameterizedTypeReference<?> type(String path);
}
