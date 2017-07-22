package ph.txtdis.service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;

public interface SavedService<PK> //
		extends ModuleAlternateNameAndNoPromptService, GetterAndSetterService<PK>, ModuleNamedService {

	default String getOrderNo() {
		PK id = get().getId();
		return id == null ? "" : id.toString();
	}

	default String getSavingInfo() {
		return getAbbreviatedModuleNoPrompt() + getOrderNo();
	}

	<T extends Keyed<PK>> SavingService<T> getSavingService();

	default void save() throws Information, Exception {
		set(save(get()));
		throw new SuccessfulSaveInfo(getSavingInfo());
	}

	@SuppressWarnings("unchecked")
	default <T extends Keyed<PK>> T save(T t) throws Exception {
		return (T) getSavingService().module(getModuleName()).save(t);
	}
}
