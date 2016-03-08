package ph.txtdis;

import static java.util.Arrays.asList;
import static ph.txtdis.type.UserType.ADMIN;
import static ph.txtdis.type.UserType.AUDITOR;
import static ph.txtdis.type.UserType.CASHIER;
import static ph.txtdis.type.UserType.COLLECTOR;
import static ph.txtdis.type.UserType.DRIVER;
import static ph.txtdis.type.UserType.HELPER;
import static ph.txtdis.type.UserType.LEAD_CHECKER;
import static ph.txtdis.type.UserType.MAIN_CASHIER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.STORE_KEEPER;
import static ph.txtdis.util.SpringUtils.encode;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ph.txtdis.domain.Authority;
import ph.txtdis.domain.User;
import ph.txtdis.repository.UserRepository;

@Configuration("persistenceConfiguration")
public class PersistenceConfiguration {

	@Autowired
	private UserRepository repository;

	@PostConstruct
	private void start() {
		if (repository.count() > 0)
			return;

		Authority manager = new Authority();
		manager.setAuthority(MANAGER);

		Authority auditor = new Authority();
		auditor.setAuthority(AUDITOR);

		Authority admin = new Authority();
		admin.setAuthority(ADMIN);

		Authority collector = new Authority();
		collector.setAuthority(COLLECTOR);

		Authority driver = new Authority();
		driver.setAuthority(DRIVER);

		Authority helper = new Authority();
		helper.setAuthority(HELPER);

		Authority cashier = new Authority();
		cashier.setAuthority(CASHIER);

		Authority mainCashier = new Authority();
		mainCashier.setAuthority(MAIN_CASHIER);

		Authority storekeeper = new Authority();
		storekeeper.setAuthority(STORE_KEEPER);

		Authority leadChecker = new Authority();
		leadChecker.setAuthority(LEAD_CHECKER);

		User sysgen = new User();
		sysgen.setUsername("SYSGEN");
		sysgen.setEnabled(true);
		sysgen.setPassword(encode("Vierski@1"));
		sysgen.setRoles(asList(manager));
		repository.save(sysgen);

		User jackie = new User();
		jackie.setUsername("JACKIE");
		jackie.setEnabled(true);
		jackie.setPassword(encode("robbie"));
		jackie.setRoles(asList(manager));
		jackie.setEmail("manila12@gmail.com");
		repository.save(jackie);

		User ronald = new User();
		ronald.setUsername("RONALD");
		ronald.setPassword(encode("alphacowboy"));
		ronald.setEnabled(true);
		ronald.setRoles(asList(manager));
		ronald.setEmail("ronaldallanso@yahoo.com");
		repository.save(ronald);

		User marivic = new User();
		marivic.setUsername("MARIVIC");
		marivic.setPassword(encode("marvic"));
		marivic.setEnabled(true);
		marivic.setRoles(asList(auditor, mainCashier));
		repository.save(marivic);

		User maricel = new User();
		maricel.setUsername("MARICEL");
		maricel.setPassword(encode("password"));
		maricel.setEnabled(true);
		maricel.setRoles(asList(cashier, leadChecker));
		repository.save(maricel);

		User maribel = new User();
		maribel.setUsername("MARIBEL");
		maribel.setPassword(encode("password"));
		maribel.setEnabled(true);
		maribel.setRoles(asList(admin));
		repository.save(maribel);

		User kenneth = new User();
		kenneth.setUsername("KENNETH");
		kenneth.setPassword(encode("password"));
		kenneth.setEnabled(true);
		kenneth.setRoles(asList(storekeeper));
		repository.save(kenneth);

		User beth = new User();
		beth.setUsername("BETH");
		beth.setPassword(encode("password"));
		beth.setEnabled(true);
		beth.setRoles(asList(storekeeper));
		repository.save(beth);

		User ronnavie = new User();
		ronnavie.setUsername("RONNAVIE");
		ronnavie.setPassword(encode("password"));
		ronnavie.setEnabled(true);
		ronnavie.setRoles(asList(storekeeper, collector));
		repository.save(ronnavie);

		User rommel = new User();
		rommel.setUsername("ROMMEL");
		rommel.setPassword(encode("password"));
		rommel.setEnabled(true);
		rommel.setRoles(asList(driver, collector));
		repository.save(rommel);

		User jose = new User();
		jose.setUsername("JOSE");
		jose.setPassword(encode("password"));
		jose.setEnabled(true);
		jose.setRoles(asList(collector));
		repository.save(jose);

		User aljun = new User();
		aljun.setUsername("ALJUN");
		aljun.setPassword(encode("password"));
		aljun.setEnabled(true);
		aljun.setRoles(asList(collector));
		repository.save(aljun);

		User vans = new User();
		vans.setUsername("VANS");
		vans.setPassword(encode("password"));
		vans.setEnabled(true);
		vans.setRoles(asList(driver, collector));
		repository.save(vans);

		User gerald = new User();
		gerald.setUsername("GERALD");
		gerald.setPassword(encode("password"));
		gerald.setEnabled(true);
		gerald.setRoles(asList(driver, collector));
		repository.save(gerald);

		User jerome = new User();
		jerome.setUsername("JEROME");
		jerome.setPassword(encode("password"));
		jerome.setEnabled(true);
		jerome.setRoles(asList(driver));
		repository.save(jerome);

		User aison = new User();
		aison.setUsername("AISON");
		aison.setPassword(encode("password"));
		aison.setEnabled(true);
		aison.setRoles(asList(helper));
		repository.save(aison);

		User ruffy = new User();
		ruffy.setUsername("RUFFY");
		ruffy.setPassword(encode("password"));
		ruffy.setEnabled(true);
		ruffy.setRoles(asList(helper));
		repository.save(ruffy);

		User jerry = new User();
		jerry.setUsername("JERRY");
		jerry.setPassword(encode("password"));
		jerry.setEnabled(true);
		jerry.setRoles(asList(helper));
		repository.save(jerry);

		User harry = new User();
		harry.setUsername("HARRY");
		harry.setPassword(encode("password"));
		harry.setEnabled(true);
		harry.setRoles(asList(helper));
		repository.save(harry);
	}
}
