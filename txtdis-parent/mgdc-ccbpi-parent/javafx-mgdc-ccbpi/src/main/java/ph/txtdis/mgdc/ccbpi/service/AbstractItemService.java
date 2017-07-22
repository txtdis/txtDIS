package ph.txtdis.mgdc.ccbpi.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.capitalize;
import static ph.txtdis.type.ItemType.PURCHASED;
import static ph.txtdis.type.UserType.MANAGER;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.NoVendorIdPurchasedItemException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SpunKeyedService;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractItemService //
		implements BommedDiscountedPricedValidatedItemService {

	private static final String ITEM = "item";

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Item> itemReadOnlyService;

	@Autowired
	private SavingService<Item> savingService;

	@Autowired
	private SpunKeyedService<Item, Long> spunService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	protected String modulePrefix;

	protected Item item;

	protected List<Item> items;

	public AbstractItemService() {
		reset();
	}

	@Override
	public boolean canApprove() {
		return credentialService.isUser(MANAGER);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Item findById(Long id) throws Exception {
		Item i = findItem(id.toString());
		if (i == null)
			throw new NotFoundException("Item No. " + id);
		if (i.getDeactivatedOn() != null)
			throw new DeactivatedException(i.getName());
		if (i.getType() == PURCHASED && i.getVendorNo() == null)
			throw new NoVendorIdPurchasedItemException(i.getName());
		return i;
	}

	protected Item findItem(String endPt) throws Exception {
		return itemReadOnlyService.module(ITEM).getOne("/" + endPt);
	}

	@Override
	public Item findByName(String name) throws Exception {
		return findItem("find?name=" + name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Item get() {
		if (item == null)
			item = new Item();
		return item;
	}

	@Override
	public String getAlternateName() {
		return capitalize(ITEM);
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public String getDecidedBy() {
		return getUsername();
	}

	@Override
	public ZonedDateTime getDecidedOn() {
		return ZonedDateTime.now();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Boolean getIsValid() {
		return getDeactivatedOn() != null;
	}

	@Override
	public String getLastModifiedBy() {
		return get().getLastModifiedBy();
	}

	@Override
	public ZonedDateTime getLastModifiedOn() {
		return get().getLastModifiedOn();
	}

	@Override
	public ReadOnlyService<Item> getListedReadOnlyService() {
		return getReadOnlyService();
	}

	@Override
	public String getModuleName() {
		return ITEM;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Item> getReadOnlyService() {
		return itemReadOnlyService;
	}

	@Override
	public String getRemarks() {
		return "";
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Item> getSavingService() {
		return savingService;
	}

	@Override
	public SpunKeyedService<Item, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + BommedDiscountedPricedValidatedItemService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public String getUsername() {
		return credentialService.username();
	}

	@Override
	public boolean isNew() {
		return getCreatedOn() == null;
	}

	@Override
	public List<Item> list() {
		return items;
	}

	@Override
	public List<String> listNames() {
		try {
			return getList("").stream().map(i -> i.getName()).collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public void reset() {
		item = null;
		items = null;
	}

	@Override
	public List<Item> search(String text) throws Exception {
		String endPt = text.isEmpty() ? "" : "/search?name=" + text;
		return items = getList(endPt);
	}

	protected List<Item> getList(String endPt) throws Exception {
		return itemReadOnlyService.module(ITEM).getList(endPt);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		item = (Item) t;
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}
}
