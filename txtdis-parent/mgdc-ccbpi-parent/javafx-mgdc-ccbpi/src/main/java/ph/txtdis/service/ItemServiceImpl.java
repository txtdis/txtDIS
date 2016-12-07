package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Item;
import ph.txtdis.dto.Price;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("itemService")
public class ItemServiceImpl extends AbstractItemService implements CokeItemService {

	@Override
	public void confirmVendorIdIsUnique(String id) throws DuplicateException {
		try {
			if (getByVendorId(Long.valueOf(id)) != null)
				throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DuplicateException("ID no. " + id + "\nhas non-numerals or");
		}
	}

	@Override
	public BigDecimal getLatestPrice(Long itemId, LocalDate orderDate) {
		try {
			return find(itemId).getPriceList().stream() //
					.filter(p -> p.getType().getName().equalsIgnoreCase("PURCHASE")
							&& isApprovedAndStartDateIsNotInTheFuture(p, orderDate)) //
					.max(Price::compareTo).get().getPriceValue();
		} catch (Exception e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
	}

	@Override
	public String getModuleIdNo() {
		return get().getVendorId();
	}

	@Override
	public List<String> listEmpties() {
		try {
			return getEmptiesList();
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private List<String> getEmptiesList() throws Exception {
		if (isNew())
			return findItems("/empties").stream().map(i -> i.getDescription()).collect(Collectors.toList());
		return getEmptiesAsList();
	}

	private List<String> getEmptiesAsList() {
		String i = get().getEmpties();
		return i == null ? null : Arrays.asList(i);
	}

	@Override
	public void open(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Long vendorId = toVendorId(id);
		Item i = findByVendorId(vendorId);
		set(i);
	}

	private Long toVendorId(String id) throws NotFoundException {
		try {
			return Long.parseLong(id);
		} catch (NumberFormatException e) {
			throw new NotFoundException("Vendor Item No. " + id);
		}
	}

	@Override
	public void setEmpties(String empties) {
		get().setEmpties(empties);
	}
}
