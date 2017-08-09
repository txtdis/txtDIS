package ph.txtdis.service;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.DecisionNeededValidatedCreatedKeyed;
import ph.txtdis.dto.ForApproval;
import ph.txtdis.dto.Remarked;

public interface DecisionNeededService //
	extends GetterAndSetterService<Long>,
	Remarked {

	default <T extends DecisionNeededValidatedCreatedKeyed<Long>> List<T> approve(List<T> list,
	                                                                              Boolean isValid,
	                                                                              String remarks) {
		return list.stream() //
			.filter(d -> d.getIsValid() == null) //
			.map(d -> updateDecisionStatus(d, isValid, remarks)) //
			.collect(toList());
	}

	default <T extends DecisionNeededValidatedCreatedKeyed<Long>> T updateDecisionStatus(T t,
	                                                                                     Boolean isValid,
	                                                                                     String remarks) {
		if (isValid != null)
			t = setDecisionStatus(t, isValid, remarks);
		return t;
	}

	default <T extends DecisionNeededValidatedCreatedKeyed<Long>> T setDecisionStatus(T t,
	                                                                                  Boolean isValid,
	                                                                                  String remarks) {
		t.setIsValid(isValid);
		t.setRemarks(addDecisionToRemarks(t, isValid, remarks));
		t.setDecidedBy(getUsername());
		return t;
	}

	default <T extends DecisionNeededValidatedCreatedKeyed<Long>> String addDecisionToRemarks(T t,
	                                                                                          Boolean isValid,
	                                                                                          String remarks) {
		if (t instanceof ForApproval)
			return remarks;
		String decisionRemarks = getDecisionTag(isValid, "DIS", "APPROVED", remarks);
		if (t.getRemarks() != null)
			decisionRemarks = decisionRemarks + "\n" + t.getRemarks();
		return decisionRemarks;
	}

	String getUsername();

	default String getDecisionTag(Boolean isValid, String prefix, String decision, String remarks) {
		if (isValid == null || isValid == false)
			decision = prefix + decision;
		remarks = remarks == null ? "" : (" - " + remarks);
		return decision + ": " + getUsername() + " - " + toDateDisplay(now()) + remarks;
	}

	boolean canApprove();

	default boolean canReject() {
		return true;
	}

	String getDecidedBy();

	ZonedDateTime getDecidedOn();

	Boolean getIsValid();

	@Override
	default void setRemarks(String text) {
		((Remarked) get()).setRemarks(text);
	}

	default void updatePerValidity(Boolean isValid, String remarks) {
		if (isValid != null)
			updateDecisionStatus(get(), isValid, remarks);
	}
}
