package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.NoVendorIdPurchasedItemException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.capitalize;
import static ph.txtdis.type.ItemType.PURCHASED;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractItemService //
	implements ExpandedBommedDiscountedPricedValidatedItemService {

	@Value("${prefix.module}")
	protected String modulePrefix;

	protected Item item;

	protected List<Item> items;

	@Autowired
	private RestClientService<Item> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	public AbstractItemService() {
		reset();
	}

	@Override
	public void reset() {
		item = null;
		items = null;
	}

	@Override
	public boolean canApprove() {
		return isUser(MANAGER);
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
		return getItemRestService().getOne("/" + endPt);
	}

	private RestClientService<Item> getItemRestService() {
		return restClientService.module(getModuleName());
	}

	@Override
	public String getModuleName() {
		return "item";
	}

	@Override
	public Item findByName(String name) throws Exception {
		return findItem("find?name=" + name);
	}

	@Override
	public String getAlternateName() {
		return capitalize(getModuleName());
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Item get() {
		if (item == null)
			item = new Item();
		return item;
	}

	@Override
	public String getDecidedBy() {
		return getUsername();
	}

	@Override
	public String getUsername() {
		return username();
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
	public void setId(Long id) {
		get().setId(id);
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
	public String getRemarks() {
		return "";
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Item> getRestClientService() {
		return restClientService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Item> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " +
			ExpandedBommedDiscountedPricedValidatedItemService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isNew() {
		return getCreatedOn() == null;
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
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

	protected List<Item> getList(String endPt) throws Exception {
		return getItemRestService().getList(endPt);
	}

	@Override
	public List<Item> search(String text) throws Exception {
		String endPt = text.isEmpty() ? "" : "/search?name=" + text;
		return items = getList(endPt);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		item = (Item) t;
	}
}
