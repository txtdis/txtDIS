package ph.txtdis.mgdc.gsm.service.server;

import static java.time.ZonedDateTime.now;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;

@Component("scheduledRemittanceValidationTask")
public class ScheduledRemittanceValidationTaskImpl //
		implements ScheduledRemittanceValidationTask {

	@Autowired
	private PastValidationPeriodRemittanceService service;

	@Value("${remittance.validation.period.in.days}")
	private long remittanceValidationPeriodInDays;

	private boolean updatedRemittances;

	@Override
	@Scheduled(cron = "0 10 8/1 * * *")
	public void voidAllUnvalidatedAfterPrescribedPeriodsSincePaymentAndCreationHaveBothExpired() {
		if (LocalDate.now().isBefore(LocalDate.of(2017, 6, 15)) || updatedRemittances)
			return;
		List<RemittanceEntity> l = service.findAllUnvalidatedAfterPrescribedPeriodSincePaidHasExpired(remittanceValidationPeriodInDays);
		if (l != null) {
			for (RemittanceEntity r : l)
				service.updatePaymentBasedOnValidation( //
						"", //
						r.getId().toString(), //
						"false", //
						"INVALID DUE TO NON-VALIDATION OVER " + remittanceValidationPeriodInDays + " DAYS SINCE PAYMENT AND/OR CREATION", // 
						"SYSGEN", //
						toTimestampText(now()));
			updatedRemittances = true;
		}
	}
}
