package ph.txtdis.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Item;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.util.ClientTypeMap;

@Service("loadReturnService")
public class LoadReturnServiceImpl implements LoadReturnService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private PickListService pickListService;

	@Autowired
	private ReadOnlyService<PickList> readOnlyService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<PickList> savingService;

	@Autowired
	private SpunService<PickList, Long> spunService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private Item item;

	private List<PickListDetail> originalDetails;

	private PickList pickList;

	private PickListDetail receivingDetail;

	@Override
	@SuppressWarnings("unchecked")
	public PickList get() {
		if (pickList == null)
			reset();
		return pickList;
	}

	@Override
	public String getAlternateName() {
		return "Load Return";
	}

	@Override
	public String getCreatedBy() {
		return get().getReceivedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getReceivedOn();
	}

	@Override
	public List<PickListDetail> getDetails() {
		return get().getDetails();
	}

	@Override
	public String getHeaderText() {
		return "Load Return";
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public ItemService getItemService() {
		return itemService;
	}

	@Override
	public String getModule() {
		return "loadReturn";
	}

	@Override
	public List<PickListDetail> getOriginalDetails() {
		return originalDetails;
	}

	@Override
	public LocalDate getPickDate() {
		return get().getPickDate();
	}

	@Override
	public ReadOnlyService<PickList> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PickListDetail getReceivingDetail() {
		return receivingDetail;
	}

	@Override
	public SpunService<PickList, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " "
				+ (isNew() ? "New " + getHeaderText() : "P/L No. " + getModuleIdNo());
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	@Override
	public boolean isNew() {
		return get().getReceivedOn() == null;
	}

	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public void reset() {
		set(new PickList());
		setItem(null);
		setReceivingDetail(null);
		originalDetails = null;
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		pickList = (PickList) t;
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void setReceivingDetail(ReceivingDetail detail) {
		receivingDetail = (PickListDetail) detail;
	}

	@Override
	public void updateUponPickListIdValidation(Long id) throws Exception {
		if (!isNew())
			return;
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		updateAfterValidatingPickListId(id);
	}

	private void updateAfterValidatingPickListId(Long id) throws Exception {
		PickList p = pickListService.find(id);
		if (p.getReceivedBy() != null)
			throw new DuplicateException("Load Return for\nPickList No. " + id);
		setForPickListReturnReceipt(p);
	}

	private void setForPickListReturnReceipt(PickList p) {
		originalDetails = p.getDetails();
		p.setDetails(null);
		set(p);
	}

	@Override
	public SavingService<PickList> getSavingService() {
		return savingService;
	}
}
