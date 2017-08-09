package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.service.GetterAndSetterService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.BillingType;

import static ph.txtdis.util.TextUtils.blankIfNull;
import static ph.txtdis.util.TextUtils.nullIfEmpty;

public interface BillingBasedService //
	extends GetterAndSetterService<Long> {

	default void checkNoDuplicates(RestClientService<Billable> readOnlyservice, String prefix, Long id, String suffix)
		throws Exception {
		Billable b = findBilling(readOnlyservice, prefix, id, suffix);
		if (b != null)
			throw new DuplicateException(getBillingNoPrompt(prefix, id, suffix));
	}

	default Billable findBilling(RestClientService<Billable> readOnlyservice, String prefix, Long id, String suffix)
		throws Exception {
		return readOnlyservice.module("billable").getOne("/orderNo?prefix=" + prefix + "&id=" + id + "&suffix=" +
			suffix);
	}

	default String getBillingNoPrompt(String prefix, Long id, String suffix) {
		return getBillingPrompt(id) + toOrderNo(prefix, id, suffix);
	}

	default String getBillingPrompt(Long id) {
		BillingType billing = id < 0 ? BillingType.DELIVERY : BillingType.INVOICE;
		return billing + "No.";
	}

	default String toOrderNo(String prefix, Long id, String suffix) {
		prefix = prefix == null || prefix.trim().isEmpty() ? "" : prefix + "-";
		suffix = blankIfNull(suffix);
		return prefix + Math.abs(id) + suffix;
	}

	default Billable checkPresence(RestClientService<Billable> readOnlyservice, String prefix, Long id, String suffix)
		throws Exception {
		Billable b = findBilling(readOnlyservice, prefix, id, suffix);
		if (b == null)
			throw new NotFoundException(getBillingNoPrompt(prefix, id, suffix));
		return b;
	}

	default Long getNumId() {
		Long id = get().getNumId();
		return id == null ? null : Math.abs(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	Billable get();

	default void setNumId(Long id) {
		get().setNumId(id);
	}

	default String getPrefix() {
		return get().getPrefix();
	}

	default void setPrefix(String text) {
		get().setPrefix(text);
	}

	default String getSuffix() {
		return get().getSuffix();
	}

	default void setSuffix(String text) {
		get().setSuffix(text);
	}

	default void setThreePartId(String prefix, Long id, String suffix) {
		setPrefix(nullIfEmpty(prefix));
		setNumId(id);
		setSuffix(nullIfEmpty(suffix));
	}
}
