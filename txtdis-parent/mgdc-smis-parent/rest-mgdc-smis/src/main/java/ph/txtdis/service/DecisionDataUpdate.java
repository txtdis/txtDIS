package ph.txtdis.service;

import static ph.txtdis.util.TextUtils.toBoolean;
import static ph.txtdis.util.TextUtils.toText;
import static ph.txtdis.util.TextUtils.toZonedDateTime;

import org.springframework.data.repository.CrudRepository;

import ph.txtdis.domain.DecisionNeeded;

public interface DecisionDataUpdate {

	default <T extends DecisionNeeded, R extends CrudRepository<T, Long>> T updateDecisionData(R repository,
			String[] s) {
		T t = repository.findOne(Long.valueOf(s[1]));
		t = updateDecisionData(t, s);
		return repository.save(t);
	}

	default <T extends DecisionNeeded> T updateDecisionData(T d, String[] s) {
		d.setIsValid(toBoolean(s[2]));
		d.setRemarks(s[3].replace("@", "\n"));
		d.setDecidedBy(toText(s[4]));
		d.setDecidedOn(toZonedDateTime(s[5]));
		return d;
	}
}
