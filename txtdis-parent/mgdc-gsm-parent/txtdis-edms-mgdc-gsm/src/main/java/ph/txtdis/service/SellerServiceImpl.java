package ph.txtdis.service;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Route;
import ph.txtdis.repository.EdmsSellerRepository;

@Service("sellerService")
public class SellerServiceImpl implements SellerService {

	@Autowired
	private EdmsSellerRepository edmsSellerRepository;

	@Value("${pickup.plate}")
	private String pickup;

	@Override
	public EdmsSeller extractFrom(Billable b) {
		return findByTruckPlateNo(b.getTruck());
	}

	@Override
	public EdmsSeller extractFrom(PickList p) {
		return findByTruckPlateNo(p.getTruck());
	}

	@Override
	public EdmsSeller findByCode(String route) {
		return route == null ? null : edmsSellerRepository.findByCode(route);
	}

	@Override
	public EdmsSeller findByTruckPlateNo(String truck) {
		if (truck == null || truck.isEmpty() || truck.equals("PICK-UP"))
			truck = pickup;
		return edmsSellerRepository.findByPlateNo(truck);
	}

	@Override
	public EdmsSeller findByUsername(String username) {
		return username == null ? null : edmsSellerRepository.findByNameContainingIgnoreCase(username);
	}

	@Override
	public String getCode(Customer c) {
		return c == null ? null : getCode(c.getRoute());
	}

	private String getCode(Route r) {
		EdmsSeller s = findByCode(r.getName());
		return s == null ? null : s.getCode();
	}

	@Override
	public String getCode(PickList p) {
		EdmsSeller e = extractFrom(p);
		return e == null ? null : e.getCode();
	}

	@Override
	public String getCode(Billable b) {
		EdmsSeller s = extractFrom(b);
		return s == null ? null : s.getCode();
	}

	@Override
	public Stream<EdmsSeller> getAll() {
		Iterable<EdmsSeller> i = edmsSellerRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false);
	}

	@Override
	public String getUsername(EdmsSeller s) {
		String name = s.getName();
		name = StringUtils.substringAfter(name, ",").trim();
		return StringUtils.substringBefore(name, " ").toUpperCase().trim();
	}

	@Override
	public String getSurname(EdmsSeller s) {
		String name = s.getName();
		return StringUtils.substringBefore(name, ",").toUpperCase().trim();
	}

	@Override
	public String getUsernameFromCode(String code) {
		EdmsSeller s = findByCode(code);
		return getUsername(s);
	}
}