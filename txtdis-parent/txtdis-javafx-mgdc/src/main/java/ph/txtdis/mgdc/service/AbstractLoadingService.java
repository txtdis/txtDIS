package ph.txtdis.mgdc.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.PickList;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SpunKeyedService;
import ph.txtdis.service.SyncService;
import ph.txtdis.service.UserService;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractLoadingService //
		implements LoadingService {

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private SavingService<PickList> savingService;

	@Autowired
	private SpunKeyedService<PickList, Long> spunService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ClientTypeMap typeMap;

	@Autowired
	protected CredentialService credentialService;

	@Autowired
	protected ReadOnlyService<PickList> readOnlyService;

	@Autowired
	protected UserService userService;

	@Value("${prefix.module}")
	protected String modulePrefix;

	private PickList pickList;

	public AbstractLoadingService() {
		reset();
	}

	@Override
	@SuppressWarnings("unchecked")
	public PickList get() {
		if (pickList == null)
			set(new PickList());
		return pickList;
	}

	@Override
	public String getHeaderName() {
		return getAlternateName();
	}

	@Override
	public HolidayService getHolidayService() {
		return holidayService;
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public LocalDate getPickDate() {
		return get().getPickDate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<PickList> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<PickList> getSavingService() {
		return savingService;
	}

	@Override
	public SpunKeyedService<PickList, Long> getSpunService() {
		return spunService;
	}

	@Override
	public SyncService getSyncService() {
		return syncService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + LoadingService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public void reset() {
		set(null);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		pickList = (PickList) t;
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	protected String username() {
		return credentialService.username();
	}
}
