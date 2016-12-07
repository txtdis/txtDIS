package ph.txtdis.service;

import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.dto.PartnerType.OUTLET;
import static ph.txtdis.util.DateTimeUtils.toDate;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.NumberUtils.HUNDRED;
import static ph.txtdis.util.NumberUtils.toPercentRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.ChannelEntity;
import ph.txtdis.domain.CustomerDiscountEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.dto.CustomerCredit;
import ph.txtdis.dto.SalesforceAccount;
import ph.txtdis.dto.SalesforceSalesInfo;
import ph.txtdis.dto.SalesforceSalesInfoProduct;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerRepository;

@Service("salesforceService")
public class SalesforceServiceImpl implements LatestCredit, ReportUom {

	private static Logger logger = getLogger(SalesforceServiceImpl.class);

	@Autowired
	private BillingRepository billingRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Value("${client.area}")
	private String area;

	@Value("${client.name}")
	private String clientName;

	@Value("${client.cluster}")
	private String cluster;

	@Value("${vendor.dis.go.live}")
	private String salesforceStart;

	@Value("${tax.classification}")
	private String taxClassification;

	private CustomerEntity customer;

	public List<SalesforceAccount> listCustomersForUpload() {
		List<CustomerEntity> l = customerRepository.findByDeactivatedOnNullAndTypeAndUploadedOnNull(OUTLET);
		if (l == null)
			return null;
		return l.stream().map(c -> toSalesforceAccount(c)).collect(toList());
	}

	private SalesforceAccount toSalesforceAccount(CustomerEntity c) {
		customer = c;
		SalesforceAccount sfa = new SalesforceAccount();
		sfa.setDistributor(clientName);
		logger.info("Distributor = " + sfa.getDistributor());
		sfa.setAccountName(c.getName());
		logger.info("Account Name = " + sfa.getAccountName());
		sfa.setAccountNumber(c.getId().toString());
		logger.info("Account No. = " + sfa.getAccountNumber());
		sfa.setChannel(toSalesforceChannel(c.getChannel()));
		logger.info("Channel = " + sfa.getChannel());
		sfa.setProvince(c.getProvince().getName());
		logger.info("Province = " + sfa.getProvince());
		sfa.setCity(c.getCity().getName());
		logger.info("City = " + sfa.getCity());
		sfa.setBarangay(c.getBarangay().getName());
		logger.info("Barangay = " + sfa.getBarangay());
		sfa.setArea(area);
		logger.info("Area = " + sfa.getArea());
		sfa.setCluster(cluster);
		logger.info("Cluster = " + sfa.getCluster());
		sfa.setPaymentTerms(getCreditTerm());
		logger.info("Payment Terms = " + sfa.getPaymentTerms());
		sfa.setTaxClassification(taxClassification);
		logger.info("Tax Classification = " + sfa.getTaxClassification());
		sfa.setVisitFrequency("F2");
		logger.info("Visit Frequency = " + sfa.getVisitFrequency());
		return sfa;
	}

	private String toSalesforceChannel(ChannelEntity c) {
		switch (c.getName()) {
			case "AMBULANT":
				return "ROLLING STORE";
			case "GROCERY":
				return "GROCERY STORE";
			case "INTERNAL":
			case "RAS":
				return "EMPLOYEES";
			case "SARI-SARI STORE":
				return "SARI - SARI STORE";
			case "MARKET DEALER":
				return "MARKET STALL (WET) - DEALER";
			case "EATERY":
				return "CARENDERIA/EATERY";
			default:
				return c.getName();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomerCredit> T getCredit() {
		return (T) getCredit(customer.getCreditDetails(), LocalDate.now());
	}

	public List<SalesforceSalesInfo> listInvoicesForUpload() {
		List<BillableEntity> l = billingRepository
				.findByNumIdNotNullAndOrderDateGreaterThanEqualAndUploadedOnNull(toDate(salesforceStart));
		if (l == null || l.isEmpty())
			return null;
		return l.stream().map(b -> toSalesforceSalesInfo(b)).collect(toList());
	}

	private SalesforceSalesInfo toSalesforceSalesInfo(BillableEntity b) {
		SalesforceSalesInfo invoice = new SalesforceSalesInfo();
		invoice.setAccount(b.getCustomer().getName());
		invoice.setActualDate(toDateDisplay(b.getOrderDate()));
		invoice.setInvoiceNumber(b.getOrderNo());
		invoice.setDsp(b.getCreatedBy());
		invoice.setProducts(toSalesforceSalesInfoProductList(b));
		return invoice;
	}

	private List<SalesforceSalesInfoProduct> toSalesforceSalesInfoProductList(BillableEntity b) {
		List<BillableDetailEntity> details = b.getDetails();
		return details.stream().map(d -> toSalesforceSalesInfoProduct(d)).collect(toList());
	}

	private SalesforceSalesInfoProduct toSalesforceSalesInfoProduct(BillableDetailEntity d) {
		SalesforceSalesInfoProduct product = new SalesforceSalesInfoProduct();
		ItemEntity item = d.getItem();
		product.setSku(item.getVendorId());
		product.setUom(getReportUom(item).toString());
		product.setQuantity(reportUomQty(d));
		product.setPercentDiscount(computedPercentDiscount(d));
		return product;
	}

	private String reportUomQty(BillableDetailEntity d) {
		BigDecimal qty = d.getUnitQty().divide(getReportUomQty(d), 6, HALF_EVEN);
		return qty.toPlainString();
	}

	private String computedPercentDiscount(BillableDetailEntity bd) {
		List<CustomerDiscountEntity> l = bd.getBilling().getCustomerDiscounts();
		BigDecimal percent = ONE;
		for (CustomerDiscountEntity cd : l)
			percent = percent.multiply(ONE.subtract(toPercentRate(cd.getPercent())));
		return ONE.subtract(percent).multiply(HUNDRED).toPlainString();
	}
}
