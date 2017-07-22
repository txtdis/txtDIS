package ph.txtdis.service;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import org.apache.commons.lang3.StringUtils;

public interface HeaderNameService //
		extends ModuleNamedService {

	default String getHeaderName() {
		String name = capitalize(getModuleName());
		String[] names = splitByCharacterTypeCamelCase(name);
		return StringUtils.join(names, " ");
	}

	default String getNewHeaderName() {
		return "New " + getHeaderName();
	}
}
