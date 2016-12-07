package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Vat;
import ph.txtdis.repository.BillingRepository;

@Service("vatService")
public class VatServiceImpl implements VatService {

	@Autowired
	private BillingRepository repository;

	@Value("${vat.percent}")
	private String vatValue;

	@Override
	public Vat getVat() {
		Vat v = new Vat();
		v.setValue(new BigDecimal("1000.00"));
		v.setVatValue(vatValue(v));
		return v;
	}

	@Override
	public List<Vat> list(LocalDate start, LocalDate end) {
		List<BillableEntity> invoices = repository
				.findByNumIdGreaterThanAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(0L, start, end);
		return vatList(invoices);
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
		v.setCustomer(b.getCustomer().getName());
		v.setOrderDate(b.getOrderDate());
		v.setValue(totalValue(b));
		v.setVatValue(vatValue(v));
		return v;
	}

	private BigDecimal totalValue(BillableEntity b) {
		BigDecimal t = b.getTotalValue();
		return t != null ? t : BigDecimal.ZERO;
	}

	private BigDecimal vatValue(Vat vat) {
		try {
			BigDecimal v = vat.getValue();
			return computeVat(v);
		} catch (Exception e) {
			return ZERO;
		}
	}

	@Override
	public BigDecimal computeVat(BigDecimal v) {
		return v.subtract(divide(v, vatRate()));
	}

	private BigDecimal vatRate() {
		return new BigDecimal("1." + vatValue);
	}
}