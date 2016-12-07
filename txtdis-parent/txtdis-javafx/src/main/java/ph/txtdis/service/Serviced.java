package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.util.DateTimeUtils;

public interface Serviced<PK> extends AlternateNamed, SpunById<PK>, Saved<PK>, Titled {

	@SuppressWarnings("unchecked")
	default <T extends Keyed<PK>> T find(LocalDate d) throws Exception {
		T e = (T) getReadOnlyService().module(getSpunModule()).getOne("/date?on=" + d);
		if (e == null)
			throw new NotFoundException(getHeaderText() + " dated " + DateTimeUtils.toDateDisplay(d));
		return e;
	}

	default <T extends Keyed<PK>> T find(Long id) throws Exception {
		return find(id.toString());
	}

	default <T extends Keyed<PK>> T find(String id) throws Exception {
		String endPt = getSpunModule() + "?id=" + id;
		if (getSpunModule().equalsIgnoreCase(getModule()))
			endPt = id;
		T e = findById("/" + endPt);
		if (e == null)
			throw new NotFoundException(getModuleId() + id);
		return e;
	}

	@SuppressWarnings("unchecked")
	default <T extends Keyed<PK>> T findById(String endPt) throws Exception {
		return (T) getReadOnlyService().module(getModule()).getOne(endPt);
	}

	default String getModuleIdNo() {
		return getId().toString();
	}

	<T extends Keyed<PK>> ReadOnlyService<T> getReadOnlyService();

	@Override
	default String getTitleText() {
		return isNew() ? newModule() : getModuleId() + getModuleIdNo();
	}

	default void open(LocalDate d) throws Exception {
		set(find(d));
	}

	default void open(Long id) throws Exception {
		open(id.toString());
	}

	default void open(String id) throws Exception {
		set(find(id));
	}

	default void openById(String id) throws Exception {
		set(findById("/" + getModule() + "?id=" + id));
	}
}
