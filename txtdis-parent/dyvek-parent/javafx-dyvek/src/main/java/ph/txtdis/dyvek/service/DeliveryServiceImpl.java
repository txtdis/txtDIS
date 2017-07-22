package ph.txtdis.dyvek.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.UnauthorizedUserException;

@Service("deliveryService")
public class DeliveryServiceImpl //
		extends AbstractOrderService<VendorService> //
		implements DeliveryService {

	private static final String SATTELITE = "-SATTELITE-";

	@Autowired
	private TradingClientService clientService;

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByOrderNo(String no) throws Exception {
		return findBillable("/delivery?no=" + no + "&of=" + getCustomer());
	}

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
	public String getCustomer() {
		return get().getVendor();
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
	public String getModuleName() {
		return "deliveryReport";
	}

	@Override
	public BigDecimal getMoistureContent() {
		return get().getMoisturePercent();
	}

	@Override
	public String getOrderNo() {
		return get().getDeliveryNo();
	}

	@Override
	public BigDecimal getPercentLauricFreeFattyAcid() {
		return get().getLauricPercent();
	}

	@Override
	public BigDecimal getPercentOleicFreeFattyAcid() {
		return get().getOleicPercent();
	}

	@Override
	public BigDecimal getSaponificationIndex() {
		return get().getSaponificationPercent();
	}

	@Override
	public String getTruckPlateNo() {
		return get().getTruckPlateNo();
	}

	@Override
	public String getTruckScaleNo() {
		return get().getTruckScaleNo();
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
	public void setColor(String color) {
		get().setColor(color);
	}

	@Override
	public void setCustomer(String name) {
		get().setVendor(name);
	}

	@Override
	public void setIodineValue(BigDecimal iv) {
		get().setIodineQty(iv);
	}

	@Override
	public void setMoistureContent(BigDecimal mvm) {
		get().setMoisturePercent(mvm);
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
	public void setPercentLauricFFA(BigDecimal ffa) {
		get().setLauricPercent(ffa);
	}

	@Override
	public void setPercentOleicFFA(BigDecimal ffa) {
		get().setOleicPercent(ffa);
	}

	@Override
	public void setRecipient(String name) {
		get().setClient(name);
	}

	@Override
	public void setSaponificationIndex(BigDecimal si) {
		get().setSaponificationPercent(si);
	}

	@Override
	public void setTruckPlateNo(String no) {
		get().setTruckPlateNo(no);
	}

	@Override
	public void setTruckScaleNo(String no) {
		get().setTruckScaleNo(no);
	}
}
