package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Keyed;

@Service("spunService")
public class SpunServiceImpl<T extends Keyed<PK>, PK> implements SpunKeyedService<T, PK> {

	@Autowired
	private ReadOnlyService<T> readOnlyService;

	private String module;

	@Override
	public SpunKeyedService<T, PK> module(String module) {
		this.module = module;
		return this;
	}

	@Override
	public T next(PK key) throws Exception {
		return readOnlyService.module(module).getOne("/next?id=" + key);
	}

	@Override
	public T previous(PK key) throws Exception {
		return readOnlyService.module(module).getOne("/previous?id=" + key);
	}
}
