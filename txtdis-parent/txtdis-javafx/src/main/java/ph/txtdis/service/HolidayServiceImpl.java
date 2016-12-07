package ph.txtdis.service;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Holiday;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.util.ClientTypeMap;

@Service("holidayService")
public class HolidayServiceImpl implements HolidayService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Holiday> readOnlyService;

	@Autowired
	private SavingService<Holiday> savingService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public String getModule() {
		return "holiday";
	}

	@Override
	public ReadOnlyService<Holiday> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + HolidayService.super.getTitleText();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isAHoliday(LocalDate d) {
		if (d != null)
			try {
				return holiday(d) != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	}

	private Holiday holiday(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getOne("/find?date=" + d);
	}

	@Override
	public LocalDate nextWorkDay(LocalDate date) {
		do {
			date = date.plusDays(1L);
		} while (date.getDayOfWeek() == DayOfWeek.SUNDAY || isAHoliday(date));
		return date;
	}

	@Override
	public Holiday save(LocalDate date, String name)
			throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Holiday h = new Holiday();
		h.setDeclaredDate(date);
		h.setName(name);
		return savingService.module(getModule()).save(h);
	}

	@Override
	public void validateDate(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, DuplicateException {
		if (d == null)
			return;
		if (holiday(d) != null)
			throw new DuplicateException(toDateDisplay(d));
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}
}
