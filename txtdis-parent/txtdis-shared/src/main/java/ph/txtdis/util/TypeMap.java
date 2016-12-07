package ph.txtdis.util;

import org.springframework.core.ParameterizedTypeReference;

public interface TypeMap {

	ParameterizedTypeReference<?> type(String path);
}
