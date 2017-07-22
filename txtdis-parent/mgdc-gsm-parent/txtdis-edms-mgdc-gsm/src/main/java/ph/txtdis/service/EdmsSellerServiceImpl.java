package ph.txtdis.service;

import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.util.DateTimeUtils.toTimestampWithSecondText;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.repository.EdmsSellerRepository;

@Service("sellerService")
public class EdmsSellerServiceImpl //
		implements EdmsSellerService {

	@Autowired
	private EdmsSellerRepository sellerRepository;

	@Autowired
	private EdmsTruckService truckService;

	@Autowired
	private EdmsVendorService vendorService;

	@Autowired
	private EdmsWarehouseService warehouseService;

	@Value("${client.street}")
	private String street;

	@Value("${client.barangay}")
	private String barangay;

	@Value("${client.city}")
	private String city;

	@Value("${client.user}")
	private String username;

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
		return route == null || route.isEmpty() ? null //
				: sellerRepository.findByCodeIgnoreCase(route);
	}

	@Override
	public EdmsSeller findByTruckPlateNo(String truck) {
		return truck == null || truck.isEmpty() || truck.equals(PICK_UP.toString()) ? null //
				: sellerRepository.findByPlateNo(truck);
	}

	@Override
	public EdmsSeller findByUsername(String username) {
		return username == null || username.isEmpty() ? null //
				: sellerRepository.findFirstByNameEndingWithIgnoreCaseOrderByIdDesc(username);
	}

	@Override
	public String getCode(Customer c) {
		EdmsSeller s = findByCode(getRouteName(c));
		return s == null ? null : s.getCode();
	}

	private String getRouteName(Customer c) {
		return c == null || c.getRoute() == null ? null : c.getRoute().getName();
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
		Iterable<EdmsSeller> i = sellerRepository.findAll();
		return stream(i.spliterator(), false);
	}

	@Override
	public String getUsername(EdmsSeller s) {
		String name = s.getName();
		name = substringAfter(name, ",").trim();
		return substringBefore(name, " ").toUpperCase().trim();
	}

	@Override
	public String getSurname(EdmsSeller s) {
		String name = s.getName();
		return substringBefore(name, ",").toUpperCase().trim();
	}

	@Override
	public String getUsernameFromCode(String code) {
		EdmsSeller s = findByCode(code);
		return getUsername(s);
	}

	@Override
	public Route save(Route r) {
		EdmsSeller s = findByCode(r.getName());
		if (s == null)
			s = newEdmsSeller(r);
		s.setName(sellerFullName(r));
		s.setTruckCode(truckService.getCode(r));
		sellerRepository.save(s);
		return r;
	}

	private String sellerFullName(Route r) {
		String name = r.getSellerFullName();
		return name == null ? "" : capitalizeFully(name);
	}

	private EdmsSeller newEdmsSeller(Route r) {
		EdmsSeller s = new EdmsSeller();
		s.setCode(capitalizeFully(r.getName(), ' ', '-'));
		s.setAddress(street + ", " + barangay + ", " + city);
		s.setTruckDescription(truckService.getDescription());
		s.setDriver("");
		s.setHelper("");
		s.setWarehouseCode(warehouseService.getMainCode());
		s.setAreaCode(vendorService.getAreaCode());
		s.setTerritoryCode(vendorService.getTerritoryCode());
		s.setDistrictCode(vendorService.getDistrictCode());
		s.setSupervisor(vendorService.getSupervisor());
		s.setCreatedBy(username);
		s.setCreatedOn(toTimestampWithSecondText(r.getCreatedOn()));
		s.setModifiedBy("");
		s.setModifiedOn("");
		return s;
	}
}