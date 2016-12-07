package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsRemittance;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.repository.EdmsRemittanceRepository;
import ph.txtdis.util.Code;
import ph.txtdis.util.NumberUtils;

@Service("remittanceService")
public class RemittanceServiceImpl implements RemittanceService {

	private static Logger logger = getLogger(RemittanceServiceImpl.class);

	@Autowired
	private EdmsRemittanceRepository edmsRemittanceRepository;

	@Autowired
	private BillableService billingService;

	@Autowired
	private SellerService sellerService;

	@Value("${bank.asiaunited}")
	private String asiaUnitedBank;

	@Value("${bank.commerce}")
	private String bankOfCommerce;

	@Value("${bank.one}")
	private String bankOne;

	@Value("${bank.bdo}")
	private String bdo;

	@Value("${bank.bpi}")
	private String bpi;

	@Value("${bank.china}")
	private String chinabank;

	@Value("${bank.citystate}")
	private String cityStateBank;

	@Value("${bank.eastwest}")
	private String eastWestBank;

	@Value("${bank.malayan}")
	private String malayanBank;

	@Value("${bank.metro}")
	private String metrobank;

	@Value("${bank.pnb}")
	private String pnb;

	@Value("${bank.psb}")
	private String psb;

	@Value("${bank.rcbc}")
	private String rcbc;

	@Value("${bank.security}")
	private String securityBank;

	@Value("${bank.ucpb}")
	private String ucpb;

	@Value("${bank.union}")
	private String unionbank;

	@Value("#{'${aliases.asiaunited}'.split(',')}")
	private List<String> asiaUnitedBankAliases;

	@Value("#{'${aliases.commerce}'.split(',')}")
	private List<String> bankOneAliases;

	@Value("#{'${aliases.one}'.split(',')}")
	private List<String> bankOfCommerceAliases;

	@Value("#{'${aliases.bdo}'.split(',')}")
	private List<String> bdoAliases;

	@Value("#{'${aliases.bpi}'.split(',')}")
	private List<String> bpiAliases;

	@Value("#{'${aliases.china}'.split(',')}")
	private List<String> chinabankAliases;

	@Value("#{'${aliases.citystate}'.split(',')}")
	private List<String> cityStateBankAliases;

	@Value("#{'${aliases.eastwest}'.split(',')}")
	private List<String> eastWestBankAliases;

	@Value("#{'${aliases.malayan}'.split(',')}")
	private List<String> malayanBankAliases;

	@Value("#{'${aliases.metro}'.split(',')}")
	private List<String> metrobankAliases;

	@Value("#{'${aliases.pnb}'.split(',')}")
	private List<String> pnbAliases;

	@Value("#{'${aliases.psb}'.split(',')}")
	private List<String> psbAliases;

	@Value("#{'${aliases.rcbc}'.split(',')}")
	private List<String> rcbcAliases;

	@Value("#{'${aliases.security}'.split(',')}")
	private List<String> securityBankAliases;

	@Value("#{'${aliases.ucpb}'.split(',')}")
	private List<String> ucpbAliases;

	@Value("#{'${aliases.union}'.split(',')}")
	private List<String> unionbankAliases;

	@Value("${client.user}")
	private String userName;

	@Override
	public BigDecimal getUnpaidAmount(EdmsInvoice i) {
		List<EdmsRemittance> l = edmsRemittanceRepository.findByBillingNo(i.getReferenceNo());
		if (l == null || l.isEmpty())
			return i.getTotalValue();
		BigDecimal paymentDue = i.getTotalValue();
		BigDecimal totalPayment = l.stream()//
				.filter(r -> isValid(r))//
				.map(r -> sumPayment(r))//
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal balance = paymentDue.subtract(totalPayment);
		return isFullyPaid(balance) ? BigDecimal.ZERO : balance;
	}

	private Boolean isFullyPaid(BigDecimal balance) {
		return balance == null ? null : balance.compareTo(BigDecimal.ZERO) <= 0;
	}

	private boolean isValid(EdmsRemittance e) {
		if (getBank(e) != null && toChequeId(e) == null)
			return false;
		return e.getValidity() != Code.INVALID;
	}

	private BigDecimal sumPayment(EdmsRemittance r) {
		if (isValid(r))
			return r.getCashValue().add(r.getCreditValue());
		return BigDecimal.ZERO;
	}

	private Long toChequeId(EdmsRemittance e) {
		String checkNo = e.getChequeNo();
		if (checkNo == null || checkNo.isEmpty())
			return null;
		return Long.valueOf(Code.numbersOnly(checkNo));
	}

	@Override
	public Boolean isFullyPaid(Billable b) {
		return isFullyPaid(b.getUnpaidValue());
	}

	@Override
	public List<Remittance> list() {
		return StreamSupport.stream(edmsRemittanceRepository.findAll().spliterator(), false)//
				.collect(Collectors.groupingBy(EdmsRemittance::getReferenceNo))//
				.entrySet().stream().sorted((a, b) -> a.getKey().compareTo(b.getKey()))//
				.map(i -> convert(i)).filter(r -> r != null).collect(Collectors.toList());
	}

	private Remittance convert(Entry<String, List<EdmsRemittance>> l) {
		List<EdmsRemittance> remittances = l.getValue().stream()//
				.filter(r -> isValid(r))//
				.collect(Collectors.toList());
		if (remittances == null || remittances.isEmpty())
			return null;
		EdmsRemittance e = remittances.get(0);
		Remittance r = new Remittance();
		r.setCheckId(toChequeId(e));
		r.setCollector(toCollector(e));
		r.setDraweeBank(getBank(e));
		r.setPaymentDate(toPaymentDate(e));
		r.setValue(sumPayments(remittances));
		r.setDetails(toDetails(remittances));
		return r;
	}

	private String toCollector(EdmsRemittance e) {
		return sellerService.getUsernameFromCode(e.getSellerCode());
	}

	private String getBank(EdmsRemittance e) {
		String b = e.getBankName();
		return b == null || b.isEmpty() ? null : toProperBankName(b.toUpperCase().trim());
	}

	private String toProperBankName(String bank) {
		if (asiaUnitedBankAliases.stream().anyMatch(b -> b.equals(bank)))
			return asiaUnitedBank;
		if (bankOfCommerceAliases.stream().anyMatch(b -> b.equals(bank)))
			return bankOfCommerce;
		if (bankOneAliases.stream().anyMatch(b -> b.equals(bank)))
			return bankOne;
		if (bdoAliases.stream().anyMatch(b -> b.equals(bank)))
			return bdo;
		if (chinabankAliases.stream().anyMatch(b -> b.equals(bank)))
			return chinabank;
		if (cityStateBankAliases.stream().anyMatch(b -> b.equals(bank)))
			return cityStateBank;
		if (eastWestBankAliases.stream().anyMatch(b -> b.equals(bank)))
			return eastWestBank;
		if (metrobankAliases.stream().anyMatch(b -> b.equals(bank)))
			return metrobank;
		if (pnbAliases.stream().anyMatch(b -> b.equals(bank)))
			return pnb;
		if (securityBankAliases.stream().anyMatch(b -> b.equals(bank)))
			return securityBank;
		if (unionbankAliases.stream().anyMatch(b -> b.equals(bank)))
			return unionbank;
		return bank;
	}

	private LocalDate toPaymentDate(EdmsRemittance e) {
		return e.getMaturityDate() == null ? e.getPaymentDate() : e.getMaturityDate();
	}

	private BigDecimal sumPayments(List<EdmsRemittance> l) {
		return l.stream().map(r -> sumPayment(r)).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private List<RemittanceDetail> toDetails(List<EdmsRemittance> l) {
		return l == null ? null
				: l.stream().map(i -> convert(i))//
						.filter(d -> d.getPaymentValue().compareTo(BigDecimal.ZERO) > 0)//
						.collect(Collectors.toList());
	}

	private RemittanceDetail convert(EdmsRemittance e) {
		RemittanceDetail d = new RemittanceDetail();
		d.setOrderNo(getOrderNo(e));
		d.setPaymentValue(sumPayment(e));
		logger.info(
				"\n\t\t\t\tRemittanceDetail: " + d.getOrderNo() + " - " + NumberUtils.toCurrencyText(d.getPaymentValue()));
		return d;
	}

	private String getOrderNo(EdmsRemittance e) {
		return billingService.getOrderNoFromBillingNo(e.getBillingNo());
	}
}
