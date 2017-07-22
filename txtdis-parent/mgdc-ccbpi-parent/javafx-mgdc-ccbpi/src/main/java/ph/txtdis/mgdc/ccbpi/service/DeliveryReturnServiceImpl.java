package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.service.PickListService;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SpunKeyedService;
import ph.txtdis.util.ClientTypeMap;

@Service("deliveryReturnService")
public class DeliveryReturnServiceImpl //
		implements DeliveryReturnService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	@Autowired
	private PickListService pickListService;

	@Autowired
	private ReadOnlyService<PickList> readOnlyService;

	@Autowired
	private SavingService<PickList> savingService;

	@Autowired
	private SpunKeyedService<PickList, Long> spunService;

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
	public String getHeaderName() {
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
	public BommedDiscountedPricedValidatedItemService getItemService() {
		return itemService;
	}

	@Override
	public String getModuleName() {
		return "loadReturn";
	}

	@Override
	public List<PickListDetail> getPickedDetails() {
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
	public SpunKeyedService<PickList, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + (isNew() ? "New " + getHeaderName() : "P/L No. " + getModuleNo());
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
		if (isNew())
			updateAfterValidatingPickListId(id);
	}

	private void updateAfterValidatingPickListId(Long id) throws Exception {
		PickList p = pickListService.findById(id);
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
