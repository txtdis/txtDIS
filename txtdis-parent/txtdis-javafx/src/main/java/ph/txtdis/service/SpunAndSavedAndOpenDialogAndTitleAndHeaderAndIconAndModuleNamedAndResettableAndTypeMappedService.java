package ph.txtdis.service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;

public interface SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<PK> //
	extends OpenDialogHeaderTextService,
	ResettableService,
	SpunByIdService<PK>,
	SavedService<PK>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	default <T extends Keyed<PK>> T findById(Long id) throws Exception {
		return findByOrderNo(id.toString());
	}

	default <T extends Keyed<PK>> T findByOrderNo(String key) throws Exception {
		T e = findByEndPt("/" + key);
		if (e == null)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		return e;
	}

	@SuppressWarnings("unchecked")
	default <T extends Keyed<PK>> T findByEndPt(String endPt) throws Exception {
		return (T) getRestClientService().module(getModuleName()).getOne(endPt);
	}

	default <T extends Keyed<PK>> T findByModuleId(Long id) throws Exception {
		return findByModuleKey(id.toString());
	}

	default <T extends Keyed<PK>> T findByModuleKey(String key) throws Exception {
		return findByEndPt("/find?id=" + key);
	}

	@Override
	default String getTitleName() {
		return isNew() ? getNewHeaderName() : getPostedTitleText();
	}

	default String getPostedTitleText() {
		return getAbbreviatedModuleNoPrompt() + getModuleNo();
	}

	default String getModuleNo() {
		return getId().toString();
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
