package ph.txtdis.dyvek.service;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.User;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.excel.ExcelBillWriter;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.info.Information;
import ph.txtdis.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.NumberUtils.isPositive;

@Service("clientBillingService")
public class ClientBillingServiceImpl 
	extends AbstractBillingService 
	implements ClientBillingService {

	@Autowired
	private UserService userService;

	@Autowired
	private ExcelBillWriter excel;

	@Value("#{'${alternate.billing.items}'.split(',')}")
	private List<String> alternateBillingItems;

	@Value("${alternate.billing.name}")
	private String alternateBillingName;

	@Value("${alternate.billing.address}")
	private String alternateBillingAddress;

	@Value("${default.billing.name}")
	private String defaultBillingName;

	@Value("${default.billing.address}")
	private String defaultBillingAddress;

	@Override
	public void generateBill() throws Exception {
		excel.write();
	}

	@Override
	public String getAlternateName() {
		return "Bill";
	}

	@Override
	public List<BillableDetail> getDetails() {
		return get().getBillings();
	}

	@Override
	public void setDetails(List<BillableDetail> l) {
		if (l != null)
			l = l.stream()
				.filter(d -> d != null && isPositive(d.getAssignedQty()))
				.collect(toList());
		get().setBookings(l);
	}

	@Override
	public String getHeaderName() {
		return "Customer Billing";
	}

	@Override
	public String getModuleName() {
		return "clientBill";
	}

	@Override
	public String getOrderNo() {
		return getBillNo();
	}

	@Override
	public LocalDate getOrderDate() {
		return getBillDate();
	}

	@Override
	public void save() throws Information, Exception {
		get().setCreatedBy("");
		super.save();
	}

	@Override
	public void setOrderNoUponValidation(String no) throws Exception {
		Billable b = findBillable("/bill?no=" + no);
		if (b != null)
			throw new DuplicateException("Bill No. " + no);
		get().setBillNo(no);
	}

	@Override
	public void updateTotals(List<BillableDetail> items) {
		get().setTotalValue(items 
			.stream() 
			.map(BillableDetail::getValue)
			.reduce(ZERO, BigDecimal::add));
	}

	@Override
	public String getCompanyName() {
		if (alternateBillingItems.contains(getItem()))
			return alternateBillingName;
		return defaultBillingName;
	}

	@Override
	public String getItem() {
		return get().getItemDescription();
	}

	@Override
	public String getLogoFont() {
		return "Ubuntu";
	}

	@Override
	public short getLogoColor() {
		return HSSFColorPredefined.GREEN.getIndex();
	}

	@Override
	public String getCompanyAddress() {
		if (alternateBillingItems.contains(getItem()))
			return alternateBillingAddress;
		return defaultBillingAddress;
	}

	@Override
	public String getBillTo() {
		return getCustomer();
	}

	@Override
	public String getReferencePrompt() {
		return "P/O No.";
	}

	@Override
	public String getReferenceNo() {
		return getSalesNo();
	}

	@Override
	public String getSalesNo() {
		return get().getSalesNo();
	}

	@Override
	public String getPaymentType() {
		return "COD";
	}

	@Override
	public String getNotePrompt() {
		return "Item:";
	}

	@Override
	public String getNote() {
		return getItem();
	}

	@Override
	public List<String> getTableColumnHeaders() {
		return asList( 
			"Date", "D/R No.", "Destination Weight", "Unit Price", "Amount");
	}

	@Override
	public String getPreparedBy() {
		try {
			List<User> l = userService.listByRole(SALES_ENCODER);
			return toFullName(l);
		} catch (Exception e) {
			return null;
		}
	}

	private String toFullName(List<User> l) {
		User u = l.get(0);
		String name = u.getUsername() + " " + u.getSurname();
		return capitalizeFully(name);
	}

	@Override
	public String getReviewedBy() {
		try {
			List<User> l = userService.listByRole(OWNER);
			return toFullName(l);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getCheckedBy() {
		try {
			List<User> l = userService.listByRole(AUDITOR);
			return toFullName(l);
		} catch (Exception e) {
			return null;
		}
	}
}
