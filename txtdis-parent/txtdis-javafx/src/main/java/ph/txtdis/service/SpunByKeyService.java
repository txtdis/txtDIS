package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

public interface SpunByKeyService<T extends Keyed<PK>, PK>
	extends ModuleNamedService {

	default T next(PK key) throws Exception {
		return getRestClientService().module(getModuleName()).getOne("/next?id=" + key);
	}

	RestClientService<T> getRestClientService();

	default T previous(PK key) throws Exception {
		return getRestClientService().module(getModuleName()).getOne("/previous?id=" + key);
	}
}