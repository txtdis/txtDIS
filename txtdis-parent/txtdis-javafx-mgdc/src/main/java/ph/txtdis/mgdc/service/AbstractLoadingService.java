package ph.txtdis.mgdc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.PickList;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service.UserService;
import ph.txtdis.util.ClientTypeMap;

import java.time.LocalDate;

import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractLoadingService //
	implements LoadingService {

	@Autowired
	protected UserService userService;

	@Value("${prefix.module}")
	protected String modulePrefix;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private RestClientService<PickList> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	private PickList pickList;

	public AbstractLoadingService() {
		reset();
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
	@SuppressWarnings("unchecked")
	public PickList get() {
		if (pickList == null)
			set(new PickList());
		return pickList;
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public LocalDate getPickDate() {
		return get().getPickDate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<PickList> getRestClientService() {
		return restClientService;
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + LoadingService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}
}
