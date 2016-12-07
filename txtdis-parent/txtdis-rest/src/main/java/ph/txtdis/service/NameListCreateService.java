package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

public interface NameListCreateService<T extends Keyed<Long>> extends CreateService<T, Long>, NameListService<T> {
}
