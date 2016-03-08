package ph.txtdis.service;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.SpringUtil.username;
import static ph.txtdis.util.TextUtils.blankIfNull;

import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.EntityDecisionNeeded;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.ScriptType;

public interface DecisionNeeded extends GetSet<Long> {

	boolean canApprove();

	default boolean canReject() {
		return true;
	}

	default boolean closeAppIfInvalid() {
		return false;
	}

	String getDecidedBy();

	ZonedDateTime getDecidedOn();

	Boolean getIsValid();

	String getRemarks();

	ScriptService getScriptService();

	<T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d);

	void save() throws SuccessfulSaveInfo, Exception;

	default <T extends EntityDecisionNeeded<Long>> T updateDecisionStatus(T entity, Boolean isValid, String remarks) {
		if (isValid != null)
			setDecisionStatus(entity, isValid, remarks);
		return entity;
	}

	default <T extends EntityDecisionNeeded<Long>> T setDecisionStatus(T entity, Boolean isValid, String remarks) {
		entity.setIsValid(isValid);
		entity.setRemarks(blankIfNull(entity.getRemarks()) + (entity.getRemarks() == null ? "" : "\n")
				+ decisionTag(isValid) + blankIfNull(remarks));
		entity.setDecidedBy(username());
		entity.setDecidedOn(ZonedDateTime.now());
		String script = entity.getId() + "|" + isValid + "|" + entity.getRemarks().replace("\n", "@") + "|"
				+ entity.getDecidedBy() + "|" + entity.getDecidedOn();
		getScriptService().set(getScriptType(entity), script);
		return entity;
	}

	default <T extends EntityDecisionNeeded<Long>> List<T> approve(List<T> list, Boolean isValid, String remarks) {
		return list.stream().map(d -> updateDecisionStatus(d, isValid, remarks)).collect(toList());
	}

	default String decisionTag(Boolean isValid) {
		if (isValid == null)
			isValid = false;
		return decisionTag(isValid, "DIS", "APPROVED");
	}

	default String decisionTag(boolean isValid, String prefix, String decision) {
		String s = decision;
		if (!isValid)
			s = prefix + s;
		return "[" + s + ": " + username() + " - " + toDateDisplay(now()) + "] ";
	}

	default void updatePerValidity(Boolean isValid, String remarks) {
		if (isValid == null)
			return;
		updateDecisionStatus(get(), isValid, remarks);
	}
}
