package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Vat;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.divide;

@Service("vatService")
public class VatServiceImpl
	implements VatService {

	private static final String CANCELLED = "CANCELLED";

	@Autowired
	private BillableRepository repository;

	@Value("${vat.percent}")
	private String vatValue;

	@Value("${vendor.id}")
	private String vendorId;

	@Override
	public Vat getVat() {
		Vat v = new Vat();
		v.setValue(new BigDecimal("1000.00"));
		v.setVatValue(vatValue(v));
		return v;
	}

	private BigDecimal vatValue(Vat vat) {
		try {
			return computeVat(vat(vat));
		} catch (Exception e) {
			return ZERO;
		}
	}

	@Override
	public BigDecimal computeVat(BigDecimal v) {
		return v.subtract(vatable(v));
	}

	private BigDecimal vat(Vat vat) {
		return vat.getValue();
	}

	private BigDecimal vatable(BigDecimal v) {
		return divide(v, vatRate());
	}

	private BigDecimal vatRate() {
		return new BigDecimal("1." + vatValue);
	}

	@Override
	public List<Vat> list(LocalDate start, LocalDate end) {
		List<BillableEntity> invoices = repository
			.findByCustomerIdNotAndNumIdGreaterThanAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
				vendorId(), 0L, start, end);
		return vatList(invoices);
	}

	private Long vendorId() {
		return Long.valueOf(vendorId);
	}

	private List<Vat> vatList(List<BillableEntity> invoices) {
		try {
			return invoices.stream().map(i -> newVat(i)).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	private Vat newVat(BillableEntity b) {
		Vat v = new Vat();
		v.setId(b.getId());
		v.setPrefix(b.getPrefix());
		v.setNbrId(b.getNumId());
		v.setSuffix(b.getSuffix());
		v.setCustomer(cancelledForNull(b));
		v.setOrderDate(b.getOrderDate());
		v.setValue(totalValue(b));
		v.setVatableValue(vatable(vat(v)));
		v.setVatValue(vatValue(v));
		return v;
	}

	private String cancelledForNull(BillableEntity b) {
		CustomerEntity e = b.getCustomer();
		return e == null ? CANCELLED : e.getName();
	}

	private BigDecimal totalValue(BillableEntity b) {
		BigDecimal t = b.getTotalValue();
		return t != null ? t : BigDecimal.ZERO;
	}
}