package ph.txtdis.dyvek.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.UnauthorizedUserException;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Service("deliveryService")
public class DeliveryServiceImpl //
	extends AbstractOrderService<VendorService> //
	implements DeliveryService {

	private static final String SATTELITE = "-SATTELITE-";

	@Autowired
	private TradingClientService clientService;

	@Override
	public List<Billable> findUnpaidBillings(String client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlternateName() {
		return "D/R";
	}

	@Override
	public String getColor() {
		return get().getColor();
	}

	@Override
	public void setColor(String color) {
		get().setColor(color);
	}

	@Override
	public BigDecimal getGrossWeight() {
		return get().getGrossWeight();
	}

	@Override
	public void setGrossWeight(BigDecimal gross) {
		get().setGrossWeight(gross);
	}

	@Override
	public BigDecimal getTareWeight() {
		return get().getTareWeight();
	}

	@Override
	public String getHeaderName() {
		return "Delivery Report";
	}

	@Override
	public BigDecimal getIodineValue() {
		return get().getIodineQty();
	}

	@Override
	public void setIodineValue(BigDecimal iv) {
		get().setIodineQty(iv);
	}

	@Override
	public String getModuleName() {
		return "deliveryReport";
	}

	@Override
	public String getOrderNo() {
		return get().getDeliveryNo();
	}

	@Override
	public BigDecimal getPercentFreeFattyAcid() {
		return get().getFfaPercent();
	}

	@Override
	public void setPercentFreeFattyAcid(BigDecimal ffa) {
		get().setFfaPercent(ffa);
	}

	@Override
	public String getTruckPlateNo() {
		return get().getTruckPlateNo();
	}

	@Override
	public void setTruckPlateNo(String no) {
		get().setTruckPlateNo(no);
	}

	@Override
	public String getTruckScaleNo() {
		return get().getTruckScaleNo();
	}

	@Override
	public void setTruckScaleNo(String no) {
		get().setTruckScaleNo(no);
	}

	@Override
	public List<String> listCustomers() {
		return isNew() ? vendorsPlusSatellite() : asList(getCustomer());
	}

	private List<String> vendorsPlusSatellite() {
		List<String> l = customerService.listVendors();
		l.add(SATTELITE);
		return l;
	}

	@Override
	public String getCustomer() {
		return get().getVendor();
	}

	@Override
	public void setCustomer(String name) {
		get().setVendor(name);
	}

	@Override
	public List<Billable> listDeliveries(Billable b) {
		try {
			return findBillables("/deliveriesOf?no=" + b.getPurchaseNo());
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public List<String> listRecipients() {
		return isNew() ? clientsPlusSatellite() : asList(getRecipient());
	}

	private List<String> clientsPlusSatellite() {
		List<String> l = clientService.listClients();
		l.add(SATTELITE);
		return l;
	}

	@Override
	public void setOrderNoUponValidation(String no) throws Exception {
		if (!isManager() && !isStockChecker())
			throw new UnauthorizedUserException("Stock checkers only.");
		Billable b = findByOrderNo(no);
		if (b != null)
			throw new DuplicateException("D/R No. " + no);
		get().setDeliveryNo(no);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByOrderNo(String no) throws Exception {
		return findBillable("/delivery?no=" + no + "&of=" + getCustomer());
	}

	@Override
	public void setRecipient(String name) {
		get().setClient(name);
	}
}
