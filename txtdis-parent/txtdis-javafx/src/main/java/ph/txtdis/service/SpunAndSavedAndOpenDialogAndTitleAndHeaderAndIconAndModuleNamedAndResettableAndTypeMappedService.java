package ph.txtdis.service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;

public interface SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<PK> //
		extends OpenDialogHeaderTextService, ResettableService, SpunById<PK>, SavedService<PK>,
		TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	<T extends Keyed<PK>> ReadOnlyService<T> getReadOnlyService();

	@SuppressWarnings("unchecked")
	default <T extends Keyed<PK>> T findByEndPt(String endPt) throws Exception {
		return (T) getReadOnlyService().module(getModuleName()).getOne(endPt);
	}

	default <T extends Keyed<PK>> T findById(Long id) throws Exception {
		return findByOrderNo(id.toString());
	}

	default <T extends Keyed<PK>> T findByModuleId(Long id) throws Exception {
		return findByModuleKey(id.toString());
	}

	default <T extends Keyed<PK>> T findByModuleKey(String key) throws Exception {
		return findByEndPt("/find?id=" + key);
	}

	default <T extends Keyed<PK>> T findByOrderNo(String key) throws Exception {
		T e = findByEndPt("/" + key);
		if (e == null)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		return e;
	}

	default String getModuleNo() {
		return getId().toString();
	}

	default String getPostedTitleText() {
		return getAbbreviatedModuleNoPrompt() + getModuleNo();
	}

	@Override
	default String getTitleName() {
		return isNew() ? getNewHeaderName() : getPostedTitleText();
	}

	default void openByDoubleClickedTableCellId(Long id) throws Exception {
		openByDoubleClickedTableCellKey(id.toString());
	}

	default void openByDoubleClickedTableCellKey(String key) throws Exception {
		set(findByOrderNo(key));
	}

	default void openByOpenDialogInputtedKey(String key) throws Exception {
		set(findByModuleKey(key));
	}
}
