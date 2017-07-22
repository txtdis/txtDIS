package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

public interface SavedNameListService<T extends Keyed<Long>> //
		extends SavedService<T>, NameListService<T> {
}
