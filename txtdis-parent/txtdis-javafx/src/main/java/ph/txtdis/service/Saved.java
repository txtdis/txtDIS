package ph.txtdis.service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;

public interface Saved<PK> extends AlternateNamed, GetSet<PK>, Moduled {

	default String getOrderNo() {
		PK id = get().getId();
		return id == null ? "" : id.toString();
	}

	<T extends Keyed<PK>> SavingService<T> getSavingService();

	default void save() throws Information, Exception {
		set(save(get()));
		throw new SuccessfulSaveInfo(getSavingInfo());
	}

	default String getSavingInfo() {
		return getModuleId() + getOrderNo();
	}

	@SuppressWarnings("unchecked")
	default <T extends Keyed<PK>> T save(T t) throws Exception {
		return (T) getSavingService().module(getModule()).save(t);
	}
}
