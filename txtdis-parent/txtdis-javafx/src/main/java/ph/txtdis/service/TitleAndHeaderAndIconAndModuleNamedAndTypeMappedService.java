package ph.txtdis.service;

public interface TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService //
		extends HeaderNameService, IconAndModuleNamedAndTypeMappedService {

	default String getTitleName() {
		return getHeaderName() + " Master";
	}
}
