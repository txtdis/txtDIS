package ph.txtdis.controller;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Billing;
import ph.txtdis.dto.Vat;
import ph.txtdis.repository.BillingRepository;

@RestController("vatController")
@RequestMapping("/vats")
public class VatController {

	@Autowired
	private BillingRepository repository;

	@Value("${vat.percent}")
	private String vatValue;

	@RequestMapping(path = "/rate", method = RequestMethod.GET)
	public ResponseEntity<?> getVat() {
		return new ResponseEntity<>(newVat(), HttpStatus.OK);
	}

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list(@RequestParam Date start, @RequestParam Date end) {
		LocalDate first = start.toLocalDate();
		LocalDate last = end.toLocalDate();
		List<Billing> invoices = repository
				.findByNumIdGreaterThanAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(0L, first,
						last);
		return new ResponseEntity<>(vatList(invoices), HttpStatus.OK);
	}

	private Vat newVat() {
		Vat v = new Vat();
		v.setValue(new BigDecimal("1000.00"));
		v.setVatValue(vatValue(v));
		return v;
	}

	private Vat newVat(Billing i) {
		Vat v = new Vat();
		v.setId(i.getId());
		v.setPrefix(i.getPrefix());
		v.setNbrId(i.getNumId());
		v.setSuffix(i.getSuffix());
		v.setCustomer(i.getCustomer().getName());
		v.setOrderDate(i.getOrderDate());
		v.setValue(i.getTotalValue());
		v.setVatValue(vatValue(v));
		return v;
	}

	private List<Vat> vatList(List<Billing> invoices) {
		try {
			return invoices.stream().map(i -> newVat(i)).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	private BigDecimal vatRate() {
		return new BigDecimal("1." + vatValue);
	}

	private BigDecimal vatValue(Vat vat) {
		try {
			BigDecimal v = vat.getValue();
			return v.subtract(divide(v, vatRate()));
		} catch (Exception e) {
			return ZERO;
		}
	}
}