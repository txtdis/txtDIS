package ph.txtdis.service;

import static java.time.ZonedDateTime.now;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static ph.txtdis.util.DateTimeUtils.toTimestampWithSecondText;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsDriver;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.User;
import ph.txtdis.repository.EdmsDriverRepository;
import ph.txtdis.util.Code;

@Service("driverService")
public class DriverServiceImpl //
		implements DriverService {

	@Autowired
	private EdmsDriverRepository edmsDriverRepository;

	@Value("${client.street}")
	private String street;

	@Value("${client.barangay}")
	private String barangay;

	@Value("${client.city}")
	private String city;

	@Value("${client.user}")
	private String username;

	@Value("${prefix.driver}")
	private String driverPrefix;

	@Override
	public String getCode(PickList p) {
		EdmsDriver e = getByName(p);
		return e == null ? "" : e.getCode();
	}

	private EdmsDriver getByName(PickList p) {
		String driver = p.getDriver();
		return driver == null ? null : edmsDriverRepository.findFirstByNameStartingWithIgnoreCase(driver);
	}

	@Override
	public String getCode(EdmsSeller s) {
		EdmsDriver e = edmsDriverRepository.findByNameIgnoreCase(s.getDriver());
		return e == null ? "" : e.getCode();
	}

	@Override
	public String getName(PickList p) {
		EdmsDriver e = getByName(p);
		return e == null ? "" : e.getName();
	}

	@Override
	public User save(User u) {
		edmsDriverRepository.save(toDriver(u));
		return u;
	}

	private EdmsDriver toDriver(User u) {
		EdmsDriver e = new EdmsDriver();
		e.setCode(code());
		e.setName(name(u));
		e.setPhone("");
		e.setAddress(address());
		e.setCreatedBy(username);
		e.setCreatedOn(toTimestampWithSecondText(now()));
		return e;
	}

	private String code() {
		Long id = edmsDriverRepository.count() + 1;
		return driverPrefix + Code.addZeroes(2, id.toString());
	}

	private String name(User u) {
		String name = u.getUsername() + " " + u.getSurname();
		return capitalizeFully(name);
	}

	private String address() {
		return street + " " + barangay + ", " + city;
	}
}