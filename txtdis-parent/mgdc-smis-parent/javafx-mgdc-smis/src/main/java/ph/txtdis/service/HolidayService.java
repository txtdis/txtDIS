package ph.txtdis.service;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
import ph.txtdis.util.TypeMap;

@Scope("prototype")
@Service("holidayService")
public class HolidayService implements Iconed, Listed<Holiday> {

	@Autowired
	private ReadOnlyService<Holiday> readOnlyService;

	@Autowired
	private SavingService<Holiday> savingService;

	@Autowired
	private ServerService server;

	@Autowired
	public TypeMap typeMap;

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public String getModule() {
		return "holiday";
	}

	@Override
	public ReadOnlyService<Holiday> getReadOnlyService() {
		return readOnlyService;
	}

	public boolean isAHoliday(LocalDate d) {
		if (d != null)
			try {
				return holiday(d) != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return true;
	}

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

	public void validateDate(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, DuplicateException {
		if (d == null)
			return;
		if (holiday(d) != null)
			throw new DuplicateException(toDateDisplay(d));
	}

	private Holiday holiday(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getOne("/find?date=" + d);
	}

	public boolean isOffSite() {
		return server.isOffSite();
	}
}
