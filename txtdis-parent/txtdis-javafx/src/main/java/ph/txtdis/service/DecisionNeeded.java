package ph.txtdis.service;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.TextUtils.blankIfNull;

import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.EntityDecisionNeeded;
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

	default <T extends EntityDecisionNeeded<Long>> T updateDecisionStatus(T entity, Boolean isValid, String remarks) {
		if (isValid != null)
			setDecisionStatus(entity, isValid, remarks);
		return entity;
	}

	default <T extends EntityDecisionNeeded<Long>> T setDecisionStatus(T entity, Boolean isValid, String remarks) {
		entity.setIsValid(isValid);
		entity.setRemarks((entity.getRemarks() == null ? "" : entity.getRemarks() + "\n") //
				+ getDecisionTag(isValid) + blankIfNull(remarks));
		entity.setDecidedBy(username());
		entity.setDecidedOn(ZonedDateTime.now());
		String script = entity.getId() + "|" + isValid + "|" + entity.getRemarks().replace("\n", "@") + "|"
				+ entity.getDecidedBy() + "|" + entity.getDecidedOn();
		getScriptService().set(getScriptType(entity), script);
		return entity;
	}

	default <T extends EntityDecisionNeeded<Long>> List<T> approve(List<T> list, Boolean isValid, String remarks) {
		return list.stream() //
				.filter(d -> d.getIsValid() == null) //
				.map(d -> updateDecisionStatus(d, isValid, remarks)) //
				.collect(toList());
	}

	default String getDecisionTag(Boolean isValid) {
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
		if (isValid != null)
			updateDecisionStatus(get(), isValid, remarks);
	}

	String username();
}
