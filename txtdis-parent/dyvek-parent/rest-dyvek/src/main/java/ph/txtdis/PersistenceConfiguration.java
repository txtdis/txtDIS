package ph.txtdis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ph.txtdis.domain.AuthorityEntity;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.domain.ItemEntity;
import ph.txtdis.dyvek.repository.CustomerRepository;
import ph.txtdis.dyvek.repository.ItemRepository;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.type.SyncType;
import ph.txtdis.type.UserType;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static ph.txtdis.type.PartnerType.*;
import static ph.txtdis.type.SyncType.VERSION;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.DateTimeUtils.epochDate;
import static ph.txtdis.util.DateTimeUtils.toUtilDate;
import static ph.txtdis.util.UserUtils.encode;

@Configuration("persistenceConfiguration")
public class PersistenceConfiguration {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private SyncRepository syncRepository;

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	private void start() {
		if (syncRepository.count() == 0)
			try {
				userRepository.save(users());
				itemRepository.save(items());
				customerRepository.save(banks());
				customerRepository.save(vendors());
				customerRepository.save(customers());
				customerRepository.save(satellite());
				syncRepository.save(newSync(VERSION, toUtilDate("2009-08-04")));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private List<UserEntity> users() {
		return asList(//
			newUser(null, "SYSGEN", "Vierski@1", MANAGER), //
			newUser("SO", "JACKIE", "robbie", MANAGER), //
			newUser("DY", "JACINTO", "password", MANAGER, OWNER), //
			newUser("DY", "BRENT", "password", MANAGER), //
			newUser("GALURA", "REDENTOR", "password", MANAGER), //
			newUser("GARCIA", "ROSELL", "password", MANAGER, AUDITOR), //
			newUser("MABORRANG", "MARJORIE", "password", MANAGER, SALES_ENCODER), //
			newUser("ROTAQUIO", "JUDY", "password", MANAGER) //
		);
	}

	private List<ItemEntity> items() {
		return asList( //
			newItem("CNOA", "COCONUT OIL, CRUDE A"), //
			newItem("CNOB", "COCONUT OIL, CRUDE B"), //
			newItem("COPR", "COPRA CAKE"), //
			newItem("FOC", "USED EDIBLE OIL"), //
			newItem("FOD", "USED EDIBLE OIL"), //
			newItem("CNOSJ", "COCONUT OIL, SJ GRADE"), //
			newItem("TO", "TORK OIL"), //
			newItem("VCO", "VIRGIN COCONUT OIL"));
	}

	private List<CustomerEntity> banks() {
		return asList( //
			newBank("ALLIED BANK"), //
			newBank("ASIA UNITED BANK"), //
			newBank("BANK OF COMMERCE"), //
			newBank("BANK ONE"), //
			newBank("BDO"), //
			newBank("BPI"), //
			newBank("CHINABANK"), //
			newBank("CITYSTATE BANK"), //
			newBank("EASTWEST BANK"), //
			newBank("MALAYAN BANK"), //
			newBank("METROBANK"), //
			newBank("PNB"), //
			newBank("PSB"), //
			newBank("RCBC"), //
			newBank("SECURITY BANK"), //
			newBank("UCPB"), //
			newBank("UNIONBANK"));
	}

	private List<CustomerEntity> vendors() {
		return Arrays.asList( //
			newVendor("ANTONIO ANGAY"), //
			newVendor("BRANIA-SULIT CORP."), //
			newVendor("BUENALYN BODINO"), //
			newVendor("CYNTHIA RODELAS"), //
			newVendor("EDGARDO ASPURIA"), //
			newVendor("EMPIRE OIL INTEG. MANUF. CORP."), //
			newVendor("FILIPINAS AGRI-MILLING CORP."), //
			newVendor("GIENE MARIE BERNARDO"), //
			newVendor("HACIENDA MACALAUAN, INC."), //
			newVendor("HEILA VENTURE & TRADING CORP. "), //
			newVendor("NEW TAYABAS OIL MILL, INC."), //
			newVendor("NOEL D. VILLARICA"), //
			newVendor("REMEDIOS MARCELO"), //
			newVendor("REYNALDO ANGAY"), //
			newVendor("SAN PABLO MANUF. CORP."), //
			newVendor("SERAFIN SALOJO"));
	}

	private List<CustomerEntity> customers() {
		return asList( //
			newOutlet("FIC MARKETING, INC."), //
			newOutlet("NICO ANINGALAN"), //
			newOutlet("OLEO-FATS, INC."));
	}

	private CustomerEntity satellite() {
		CustomerEntity c = new CustomerEntity();
		c.setName("-SATTELITE-");
		c.setType(INTERNAL);
		return c;
	}

	private SyncEntity newSync(SyncType script, Date date) {
		SyncEntity e = new SyncEntity();
		e.setId(script);
		e.setLastSync(epochDate());
		return e;
	}

	private UserEntity newUser(String surname, String username, String password, UserType... roles) {
		UserEntity u = new UserEntity();
		u.setName(username);
		u.setSurname(surname);
		u.setEnabled(true);
		u.setPassword(encodePassword(password));
		u.setRoles(roles(u, roles));
		return u;
	}

	private ItemEntity newItem(String name, String description) {
		ItemEntity i = new ItemEntity();
		i.setName(name);
		i.setDescription(description);
		return i;
	}

	private CustomerEntity newBank(String name) {
		CustomerEntity c = new CustomerEntity();
		c.setName(name);
		c.setType(FINANCIAL);
		return c;
	}

	private CustomerEntity newVendor(String name) {
		CustomerEntity c = new CustomerEntity();
		c.setName(name);
		c.setType(VENDOR);
		return c;
	}

	private CustomerEntity newOutlet(String name) {
		CustomerEntity c = new CustomerEntity();
		c.setName(name);
		c.setType(OUTLET);
		return c;
	}

	private String encodePassword(String password) {
		if (password == null)
			password = "password";
		return encode(password);
	}

	private List<AuthorityEntity> roles(UserEntity u, UserType[] roles) {
		return asList(roles).stream().map(r -> newRole(u, r)).collect(Collectors.toList());
	}

	private AuthorityEntity newRole(UserEntity u, UserType role) {
		AuthorityEntity r = new AuthorityEntity();
		r.setUser(u);
		r.setRole(role);
		return r;
	}
}
