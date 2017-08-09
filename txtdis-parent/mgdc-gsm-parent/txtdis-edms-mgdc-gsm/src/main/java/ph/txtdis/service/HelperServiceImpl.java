package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.domain.EdmsHelper;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.User;
import ph.txtdis.repository.EdmsHelperRepository;
import ph.txtdis.util.Code;

import static java.time.ZonedDateTime.now;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static ph.txtdis.util.DateTimeUtils.toTimestampWithSecondText;

@Service("helperService")
public class HelperServiceImpl //
	implements HelperService {

	@Autowired
	private EdmsHelperRepository edmsHelperRepository;

	@Value("${client.street}")
	private String street;

	@Value("${client.barangay}")
	private String barangay;

	@Value("${client.city}")
	private String city;

	@Value("${client.user}")
	private String username;

	@Value("${prefix.helper}")
	private String helperPrefix;

	@Override
	public String getCode(PickList p) {
		EdmsHelper e = findByName(p);
		return e == null ? "" : e.getCode();
	}

	private EdmsHelper findByName(PickList p) {
		String asst = p.getAssistant();
		return asst == null ? null : edmsHelperRepository.findFirstByNameStartingWithIgnoreCase(p.getAssistant());
	}

	@Override
	public String getCode(EdmsSeller s) {
		EdmsHelper e = edmsHelperRepository.findByNameIgnoreCase(s.getHelper());
		return e == null ? "" : e.getCode();
	}

	@Override
	public String getName(PickList p) {
		EdmsHelper e = findByName(p);
		return e == null ? "" : e.getName();
	}

	@Override
	public User save(User u) {
		edmsHelperRepository.save(toHelper(u));
		return u;
	}

	private EdmsHelper toHelper(User u) {
		EdmsHelper e = new EdmsHelper();
		e.setCode(code());
		e.setName(name(u));
		e.setPhone("");
		e.setAddress(address());
		e.setCreatedBy(username);
		e.setCreatedOn(toTimestampWithSecondText(now()));
		return e;
	}

	private String code() {
		Long id = edmsHelperRepository.count() + 1;
		return helperPrefix + Code.addZeroes(2, id.toString());
	}

	private String name(User u) {
		String name = u.getUsername() + " " + u.getSurname();
		return capitalizeFully(name);
	}

	private String address() {
		return street + " " + barangay + ", " + city;
	}
}