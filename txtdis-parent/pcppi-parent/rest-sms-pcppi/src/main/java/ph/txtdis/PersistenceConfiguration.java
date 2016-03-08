package ph.txtdis;

import static java.time.LocalDate.parse;
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

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ph.txtdis.domain.Authority;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.CustomerSalesVolume;
import ph.txtdis.domain.Item;
import ph.txtdis.domain.Price;
import ph.txtdis.domain.User;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.CustomerSalesVolumeRepository;
import ph.txtdis.repository.ItemRepository;
import ph.txtdis.repository.UserRepository;

@Configuration("persistenceConfiguration")
public class PersistenceConfiguration {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerSalesVolumeRepository customerSalesVolumeRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	private void start() {
		if (userRepository.count() > 0)
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
		sysgen.setMobile("+639498592927");
		userRepository.save(sysgen);

		User butch = new User();
		butch.setUsername("RECHIE");
		butch.setEnabled(true);
		butch.setPassword(encode("password"));
		butch.setRoles(asList(manager));
		butch.setEmail("rechie.bobier@pcppi.com.ph");
		butch.setMobile("+639175594323");
		userRepository.save(butch);

		User rechie = new User();
		rechie.setUsername("DONG");
		rechie.setEnabled(true);
		rechie.setPassword(encode("password"));
		rechie.setRoles(asList(manager, storekeeper));
		rechie.setEmail("butchlim888@yahoo.com");
		rechie.setMobile("+639175422348");
		userRepository.save(rechie);

		Item prg8oz = itemRepository.save(new Item("PRG8OZ", "0101020110", "Pepsi 8oz"));
		Item prg12o = itemRepository.save(new Item("PRG12O", "0101030110", "Pepsi 12oz"));
		Item prg10l = itemRepository.save(new Item("PRG10L", "0101040110", "Pepsi 1L"));
		Item prg156 = itemRepository.save(new Item("PRG156", "0101050310", "Pepsi 1.5LX6"));
		Item prg15l = itemRepository.save(new Item("PRG15L", "0101060310", "Pepsi 1.5L NR"));
		Item prg250 = itemRepository.save(new Item("PRG250", "0101110210", "Pepsi 250mL can"));
		Item prg500 = itemRepository.save(new Item("PRG500", "0101200310", "Pepsi 500 NR"));
		Item prg20l = itemRepository.save(new Item("PRG20L", "0101310310", "Pepsi 2L"));
		Item prg750 = itemRepository.save(new Item("PRG750", "0101390110", "Pepsi 750mL"));
		Item prg125 = itemRepository.save(new Item("PRG125", "0101800310", "Pepsi 1.25LX6"));
		Item prg345 = itemRepository.save(new Item("PRG345", "0101920310", "Pepsi 345mL X12 PET"));
		Item pbl8oz = itemRepository.save(new Item("PBL8OZ", "0103020110", "Pepsi Blue 8oz"));
		Item pmx15l = itemRepository.save(new Item("PMX15L", "0107060310", "Pepsi Maxx 1.5L"));
		Item mmx500 = itemRepository.save(new Item("PMX500", "0107200310", "Pepsi Maxx 500 NR"));
		Item mmx20l = itemRepository.save(new Item("PMX20L", "0107310310", "Pepsi Maxx 2L"));
		Item srg7oz = itemRepository.save(new Item("SRG7OZ", "0211010110", "Seven-Up 7oz"));
		Item srg12o = itemRepository.save(new Item("SRG12O", "0211030110", "Seven-Up 12oz"));
		Item srg10l = itemRepository.save(new Item("SRG10L", "0211040110", "Seven-Up 1L"));
		Item srg15l = itemRepository.save(new Item("SRG15L", "0211060310", "Seven-Up 1.5 NR"));
		Item srg500 = itemRepository.save(new Item("SRG500", "0211200310", "Seven-Up 500 NR"));
		Item srg750 = itemRepository.save(new Item("SRG750", "0211390110", "Seven-Up 750mL"));
		Item srg125 = itemRepository.save(new Item("SRG125", "0211800310", "Seven-Up 1.25LX6"));
		Item mdr12o = itemRepository.save(new Item("MDR12O", "0346030110", "Mountain Dew 12oz"));
		Item mdr156 = itemRepository.save(new Item("MDR156", "0346050310", "Mountain Dew 1.5LX6"));
		Item mdr15l = itemRepository.save(new Item("MDR15L", "0346060310", "Mountain Dew 1.5 NR"));
		Item mdr250 = itemRepository.save(new Item("MDR250", "0346110210", "Mountain Dew 250mL can"));
		Item mdr500 = itemRepository.save(new Item("MDR500", "0346200310", "Mountain Dew 500 NR"));
		Item mdr20l = itemRepository.save(new Item("MDR20L", "0346310310", "Mountain Dew 2L"));
		Item mdr750 = itemRepository.save(new Item("MDR750", "0346390110", "Mountain Dew 750mL"));
		Item mdr125 = itemRepository.save(new Item("MDR125", "0346800310", "Mountain Dew 1.25LX6"));
		Item mdr400 = itemRepository.save(new Item("MDR400", "0346830310", "Mountain Dew 400X12 NR"));
		Item mor8oz = itemRepository.save(new Item("MOR8OZ", "0413020110", "Mirinda Orange 8oz"));
		Item mor15l = itemRepository.save(new Item("MOR15L", "0413060310", "Mirinda Orange 1.5 NR"));
		Item mor500 = itemRepository.save(new Item("MOR500", "0413200310", "Mirinda Orange 500 NR"));
		Item mor125 = itemRepository.save(new Item("MOR125", "0413800310", "Mirinda Orange 1.25LX6"));
		Item mrb15l = itemRepository.save(new Item("MRB15L", "0521060310", "Mug Root Beer 1.5L NR"));
		Item mrbcan = itemRepository.save(new Item("MRBCAN", "0521130210", "Mug Root Beer can"));
		Item mrb20l = itemRepository.save(new Item("MRB20L", "0521310310", "Mug Root Beer 2L"));
		Item mrb125 = itemRepository.save(new Item("MRB125", "0521800310", "Mug Root Beer 1.25LX6"));
		Item pwr500 = itemRepository.save(new Item("PWR500", "0600200310", "Premier Water 500X24"));
		Item pwr350 = itemRepository.save(new Item("PWR350", "0600420310", "Premier Water 350X48"));
		Item tor240 = itemRepository.save(new Item("TOR240", "0813100110", "Tropicana Orange 240mL RB"));
		Item tor355 = itemRepository.save(new Item("TOR355", "0813150310", "Tropicana Orange 355mL"));
		Item gle500 = itemRepository.save(new Item("GLE500", "0911200310", "Gatorage Lemon 500mL"));
		Item ggr15l = itemRepository.save(new Item("GGR15L", "0929060310", "Gatorage Grape 1.5L"));
		Item ggr400 = itemRepository.save(new Item("GGR400", "0929170110", "Gatorage Grape 400mL NR"));
		Item ggr500 = itemRepository.save(new Item("GGR500", "0929200310", "Gatorage Grape 500mL"));
		Item ggr350 = itemRepository.save(new Item("GGR350", "0929610310", "Gatorage Grape 350mL"));
		Item ggb15l = itemRepository.save(new Item("GGB15L", "0934060310", "Gatorage BBolt 1.5L"));
		Item ggb240 = itemRepository.save(new Item("GGB240", "0934100110", "Gatorage BBolt 240mL RB"));
		Item gbb400 = itemRepository.save(new Item("GBB400", "0934170110", "Gatorage BBolt 400mL NR"));
		Item gbb500 = itemRepository.save(new Item("GBB500", "0934200310", "Gatorage BBolt 500mL"));
		Item ggb350 = itemRepository.save(new Item("GBB350", "0934610310", "Gatorage BBolt 350mL"));
		Item goc500 = itemRepository.save(new Item("GOC500", "0935200310", "Gatorage OChill 500mL"));
		Item gtf15l = itemRepository.save(new Item("GTF15L", "0936060310", "Gatorage TFruit 1.5L"));
		Item gtf240 = itemRepository.save(new Item("GTF240", "0936100110", "Gatorage TFruit 240mL RB"));
		Item gtf400 = itemRepository.save(new Item("GTF400", "0936170110", "Gatorage TFruit 400mL NR"));
		Item gtf500 = itemRepository.save(new Item("GTF500", "0936200310", "Gatorage TFruit 500mL"));
		Item lgr450 = itemRepository.save(new Item("LGR450", "1024360310", "Lipton Green 450mL NR"));
		Item lle450 = itemRepository.save(new Item("LBK450", "1025360310", "Lipton Black 450mL NR"));
		Item lre240 = itemRepository.save(new Item("LRE240", "1043100110", "Lipton Red 240mL RB"));
		Item lre450 = itemRepository.save(new Item("LRE450", "1043360310", "Lipton Red 450mL NR"));
		Item ses240 = itemRepository.save(new Item("SES240", "1130100110", "String Strawberry 240mL RB"));
		Item ses330 = itemRepository.save(new Item("SES330", "1130130310", "String Strawberry 330mL PET"));
		Item sep240 = itemRepository.save(new Item("SEP240", "11A2100110", "String PowerPacq 240mL RB"));

		prg8oz.setPriceList(asList(new Price(parse("2014-04-17"), new BigDecimal("112.00"))));
		prg12o.setPriceList(asList(new Price(parse("2010-01-21"), new BigDecimal("192.00"))));
		prg10l.setPriceList(asList(new Price(parse("2012-04-16"), new BigDecimal("200.00"))));
		prg156.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("450.00"))));
		prg15l.setPriceList(asList(new Price(parse("2016-02-02"), new BigDecimal("261.00"))));
		prg250.setPriceList(asList(new Price(parse("2015-12-16"), new BigDecimal("240.00"))));
		prg500.setPriceList(asList(new Price(parse("2014-12-05"), new BigDecimal("525.00"))));
		prg20l.setPriceList(asList(new Price(parse("2014-09-01"), new BigDecimal("406.00"))));
		prg750.setPriceList(asList(new Price(parse("2015-08-01"), new BigDecimal("140.00"))));
		prg125.setPriceList(asList(new Price(parse("2013-05-25"), new BigDecimal("150.00"))));
		prg345.setPriceList(asList(new Price(parse("2015-11-18"), new BigDecimal("120.00"))));
		pbl8oz.setPriceList(asList(new Price(parse("2014-04-17"), new BigDecimal("112.00"))));
		pmx15l.setPriceList(asList(new Price(parse("2016-02-12"), new BigDecimal("522.00"))));
		mmx500.setPriceList(asList(new Price(parse("2014-12-05"), new BigDecimal("525.00"))));
		mmx20l.setPriceList(asList(new Price(parse("2014-09-01"), new BigDecimal("406.00"))));
		srg7oz.setPriceList(asList(new Price(parse("2015-08-01"), new BigDecimal("116.00"))));
		srg12o.setPriceList(asList(new Price(parse("2010-01-21"), new BigDecimal("192.00"))));
		srg10l.setPriceList(asList(new Price(parse("2010-12-27"), new BigDecimal("224.00"))));
		srg15l.setPriceList(asList(new Price(parse("2016-02-12"), new BigDecimal("522.00"))));
		srg500.setPriceList(asList(new Price(parse("2014-12-05"), new BigDecimal("525.00"))));
		srg750.setPriceList(asList(new Price(parse("2015-09-28"), new BigDecimal("156.00"))));
		srg125.setPriceList(asList(new Price(parse("2013-05-25"), new BigDecimal("150.00"))));
		mdr12o.setPriceList(asList(new Price(parse("2015-09-27"), new BigDecimal("192.00"))));
		mdr156.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("450.00"))));
		mdr15l.setPriceList(asList(new Price(parse("2016-02-02"), new BigDecimal("261.00"))));
		mdr250.setPriceList(asList(new Price(parse("2015-12-16"), new BigDecimal("240.00"))));
		mdr500.setPriceList(asList(new Price(parse("2014-12-05"), new BigDecimal("525.00"))));
		mdr20l.setPriceList(asList(new Price(parse("2014-09-01"), new BigDecimal("406.00"))));
		mdr750.setPriceList(asList(new Price(parse("2010-10-11"), new BigDecimal("156.00"))));
		mdr125.setPriceList(asList(new Price(parse("2013-05-25"), new BigDecimal("150.00"))));
		mdr400.setPriceList(asList(new Price(parse("2014-09-01"), new BigDecimal("156.00"))));
		mor8oz.setPriceList(asList(new Price(parse("2015-08-01"), new BigDecimal("116.00"))));
		mor15l.setPriceList(asList(new Price(parse("2016-02-12"), new BigDecimal("522.00"))));
		mor500.setPriceList(asList(new Price(parse("2014-12-05"), new BigDecimal("525.00"))));
		mor125.setPriceList(asList(new Price(parse("2013-05-25"), new BigDecimal("150.00"))));
		mrb15l.setPriceList(asList(new Price(parse("2014-09-01"), new BigDecimal("522.00"))));
		mrbcan.setPriceList(asList(new Price(parse("2014-12-05"), new BigDecimal("492.00"))));
		mrb20l.setPriceList(asList(new Price(parse("2014-09-01"), new BigDecimal("406.00"))));
		mrb125.setPriceList(asList(new Price(parse("2013-05-25"), new BigDecimal("150.00"))));
		pwr500.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("230.00"))));
		pwr350.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("312.00"))));
		tor240.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("198.00"))));
		tor355.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("482.00"))));
		gle500.setPriceList(asList(new Price(parse("2015-08-27"), new BigDecimal("672.00"))));
		ggr15l.setPriceList(asList(new Price(parse("2015-08-27"), new BigDecimal("756.00"))));
		ggr400.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("537.00"))));
		ggr500.setPriceList(asList(new Price(parse("2016-01-25"), new BigDecimal("340.00"))));
		ggr350.setPriceList(asList(new Price(parse("2016-01-27"), new BigDecimal("250.00"))));
		ggb15l.setPriceList(asList(new Price(parse("2015-08-27"), new BigDecimal("756.00"))));
		ggb240.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("198.00"))));
		gbb400.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("537.00"))));
		gbb500.setPriceList(asList(new Price(parse("2016-01-25"), new BigDecimal("340.00"))));
		ggb350.setPriceList(asList(new Price(parse("2016-01-27"), new BigDecimal("250.00"))));
		goc500.setPriceList(asList(new Price(parse("2015-08-27"), new BigDecimal("672.00"))));
		gtf15l.setPriceList(asList(new Price(parse("2015-08-27"), new BigDecimal("756.00"))));
		gtf240.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("198.00"))));
		gtf400.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("537.00"))));
		gtf500.setPriceList(asList(new Price(parse("2016-01-25"), new BigDecimal("340.00"))));
		lgr450.setPriceList(asList(new Price(parse("2013-01-13"), new BigDecimal("400.00"))));
		lle450.setPriceList(asList(new Price(parse("2013-01-13"), new BigDecimal("400.00"))));
		lre240.setPriceList(asList(new Price(parse("2010-06-15"), new BigDecimal("168.00"))));
		lre450.setPriceList(asList(new Price(parse("2013-01-13"), new BigDecimal("400.00"))));
		ses240.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("198.00"))));
		ses330.setPriceList(asList(new Price(parse("2011-02-06"), new BigDecimal("360.00"))));
		sep240.setPriceList(asList(new Price(parse("2000-01-01"), new BigDecimal("198.00"))));
		
		prg8oz = itemRepository.save(prg8oz);
		prg12o = itemRepository.save(prg12o);
		prg10l = itemRepository.save(prg10l);
		prg156 = itemRepository.save(prg156);
		prg15l = itemRepository.save(prg15l);
		prg250 = itemRepository.save(prg250);
		prg500 = itemRepository.save(prg500);
		prg20l = itemRepository.save(prg20l);
		prg750 = itemRepository.save(prg750);
		prg125 = itemRepository.save(prg125);
		prg345 = itemRepository.save(prg345);
		pbl8oz = itemRepository.save(pbl8oz);
		pmx15l = itemRepository.save(pmx15l);
		mmx500 = itemRepository.save(mmx500);
		mmx20l = itemRepository.save(mmx20l);
		srg7oz = itemRepository.save(srg7oz);
		srg12o = itemRepository.save(srg12o);
		srg10l = itemRepository.save(srg10l);
		srg15l = itemRepository.save(srg15l);
		srg500 = itemRepository.save(srg500);
		srg750 = itemRepository.save(srg750);
		srg125 = itemRepository.save(srg125);
		mdr12o = itemRepository.save(mdr12o);
		mdr156 = itemRepository.save(mdr156);
		mdr15l = itemRepository.save(mdr15l);
		mdr250 = itemRepository.save(mdr250);
		mdr500 = itemRepository.save(mdr500);
		mdr20l = itemRepository.save(mdr20l);
		mdr750 = itemRepository.save(mdr750);
		mdr125 = itemRepository.save(mdr125);
		mdr400 = itemRepository.save(mdr400);
		mor8oz = itemRepository.save(mor8oz);
		mor15l = itemRepository.save(mor15l);
		mor500 = itemRepository.save(mor500);
		mor125 = itemRepository.save(mor125);
		mrb15l = itemRepository.save(mrb15l);
		mrbcan = itemRepository.save(mrbcan);
		mrb20l = itemRepository.save(mrb20l);
		mrb125 = itemRepository.save(mrb125);
		pwr500 = itemRepository.save(pwr500);
		pwr350 = itemRepository.save(pwr350);
		tor240 = itemRepository.save(tor240);
		tor355 = itemRepository.save(tor355);
		gle500 = itemRepository.save(gle500);
		ggr15l = itemRepository.save(ggr15l);
		ggr400 = itemRepository.save(ggr400);
		ggr500 = itemRepository.save(ggr500);
		ggr350 = itemRepository.save(ggr350);
		ggb15l = itemRepository.save(ggb15l);
		ggb240 = itemRepository.save(ggb240);
		gbb400 = itemRepository.save(gbb400);
		gbb500 = itemRepository.save(gbb500);
		ggb350 = itemRepository.save(ggb350);
		goc500 = itemRepository.save(goc500);
		gtf15l = itemRepository.save(gtf15l);
		gtf240 = itemRepository.save(gtf240);
		gtf400 = itemRepository.save(gtf400);
		gtf500 = itemRepository.save(gtf500);
		lgr450 = itemRepository.save(lgr450);
		lle450 = itemRepository.save(lle450);
		lre240 = itemRepository.save(lre240);
		lre450 = itemRepository.save(lre450);
		ses240 = itemRepository.save(ses240);
		ses330 = itemRepository.save(ses330);
		sep240 = itemRepository.save(sep240);

		Customer ws1 = new Customer("WHOLESALER ONE", "+639175393161");
		ws1 = customerRepository.save(ws1);

		Customer ws2 = new Customer("WHOLESALER TWO", "+639258307534");
		ws2 = customerRepository.save(ws2);

		customerSalesVolumeRepository.save(asList(//
				new CustomerSalesVolume(ws1, ggb240, new BigDecimal("13.4359")), //
				new CustomerSalesVolume(ws1, gbb400, new BigDecimal("0.0032")), //
				new CustomerSalesVolume(ws1, gbb500, new BigDecimal("0.1603")), //
				new CustomerSalesVolume(ws1, ggr500, new BigDecimal("0.0224")), //
				new CustomerSalesVolume(ws1, gle500, new BigDecimal("0.0224")), //
				new CustomerSalesVolume(ws1, goc500, new BigDecimal("0.0385")), //
				new CustomerSalesVolume(ws1, gtf240, new BigDecimal("1.9455")), //
				new CustomerSalesVolume(ws1, gtf500, new BigDecimal("0.0769")), //
				new CustomerSalesVolume(ws1, lre240, new BigDecimal("0.2724")), //
				new CustomerSalesVolume(ws1, mdr125, new BigDecimal("2.1747")), //
				new CustomerSalesVolume(ws1, mdr15l, new BigDecimal("0.0256")), //
				new CustomerSalesVolume(ws1, mdr156, new BigDecimal("0.1875")), //
				new CustomerSalesVolume(ws1, mdr12o, new BigDecimal("99.5833")), //
				new CustomerSalesVolume(ws1, mdr250, new BigDecimal("3.6795")), //
				new CustomerSalesVolume(ws1, mdr20l, new BigDecimal("0.0160")), //
				new CustomerSalesVolume(ws1, mdr400, new BigDecimal("16.5256")), //
				new CustomerSalesVolume(ws1, mdr750, new BigDecimal("37.1218")), //
				new CustomerSalesVolume(ws1, mor125, new BigDecimal("1.5016")), //
				new CustomerSalesVolume(ws1, mor8oz, new BigDecimal("3.6442")), //
				new CustomerSalesVolume(ws1, mrb125, new BigDecimal("0.0128")), //
				new CustomerSalesVolume(ws1, mrb20l, new BigDecimal("0.141")), //
				new CustomerSalesVolume(ws1, mrbcan, new BigDecimal("0.0256")), //
				new CustomerSalesVolume(ws1, pbl8oz, new BigDecimal("2.6635")), //
				new CustomerSalesVolume(ws1, pmx15l, new BigDecimal("0.0032")), //
				new CustomerSalesVolume(ws1, mmx20l, new BigDecimal("0.0032")), //
				new CustomerSalesVolume(ws1, prg125, new BigDecimal("1.9503")), //
				new CustomerSalesVolume(ws1, prg15l, new BigDecimal("0.0224")), //
				new CustomerSalesVolume(ws1, prg156, new BigDecimal("0.1202")), //
				new CustomerSalesVolume(ws1, prg12o, new BigDecimal("0.1763")), //
				new CustomerSalesVolume(ws1, prg10l, new BigDecimal("1.8942")), //
				new CustomerSalesVolume(ws1, prg250, new BigDecimal("1.8526")), //
				new CustomerSalesVolume(ws1, prg345, new BigDecimal("0.0032")), //
				new CustomerSalesVolume(ws1, prg750, new BigDecimal("1.7660")), //
				new CustomerSalesVolume(ws1, prg8oz, new BigDecimal("50.8397")), //
				new CustomerSalesVolume(ws1, pwr350, new BigDecimal("0.0064")), //
				new CustomerSalesVolume(ws1, sep240, new BigDecimal("0.0481")), //
				new CustomerSalesVolume(ws1, ses240, new BigDecimal("4.0288")), //
				new CustomerSalesVolume(ws1, srg125, new BigDecimal("1.6587")), //
				new CustomerSalesVolume(ws1, srg15l, new BigDecimal("0.0032")), //
				new CustomerSalesVolume(ws1, srg12o, new BigDecimal("2.2628")), //
				new CustomerSalesVolume(ws1, srg10l, new BigDecimal("0.391")), //
				new CustomerSalesVolume(ws1, srg750, new BigDecimal("0.1154")), //
				new CustomerSalesVolume(ws1, srg7oz, new BigDecimal("3.9135")), //
				new CustomerSalesVolume(ws1, tor240, new BigDecimal("13.8558")), //
				new CustomerSalesVolume(ws2, ggb15l, new BigDecimal("0.016")), //
				new CustomerSalesVolume(ws2, ggb240, new BigDecimal("26.0417")), //
				new CustomerSalesVolume(ws2, ggb350, new BigDecimal("0.2404")), //
				new CustomerSalesVolume(ws2, gbb400, new BigDecimal("0.1122")), //
				new CustomerSalesVolume(ws2, gbb500, new BigDecimal("0.3301")), //
				new CustomerSalesVolume(ws2, ggr15l, new BigDecimal("0.0096")), //
				new CustomerSalesVolume(ws2, ggr350, new BigDecimal("0.0160")), //
				new CustomerSalesVolume(ws2, ggr400, new BigDecimal("0.0256")), //
				new CustomerSalesVolume(ws2, ggr500, new BigDecimal("0.0801")), //
				new CustomerSalesVolume(ws2, goc500, new BigDecimal("0.0064")), //
				new CustomerSalesVolume(ws2, gtf15l, new BigDecimal("0.0064")), //
				new CustomerSalesVolume(ws2, gtf240, new BigDecimal("3.0321")), //
				new CustomerSalesVolume(ws2, gtf400, new BigDecimal("0.0385")), //
				new CustomerSalesVolume(ws2, gtf500, new BigDecimal("0.0545")), //
				new CustomerSalesVolume(ws2, lgr450, new BigDecimal("0.0096")), //
				new CustomerSalesVolume(ws2, lle450, new BigDecimal("0.5288")), //
				new CustomerSalesVolume(ws2, lre240, new BigDecimal("0.1795")), //
				new CustomerSalesVolume(ws2, lre450, new BigDecimal("0.5865")), //
				new CustomerSalesVolume(ws2, mdr125, new BigDecimal("1.4984")), //
				new CustomerSalesVolume(ws2, mdr15l, new BigDecimal("0.0769")), //
				new CustomerSalesVolume(ws2, mdr156, new BigDecimal("0.2003")), //
				new CustomerSalesVolume(ws2, mdr12o, new BigDecimal("146.4647")), //
				new CustomerSalesVolume(ws2, mdr250, new BigDecimal("6.9167")), //
				new CustomerSalesVolume(ws2, mdr20l, new BigDecimal("0.0288")), //
				new CustomerSalesVolume(ws2, mdr400, new BigDecimal("7.6891")), //
				new CustomerSalesVolume(ws2, mdr500, new BigDecimal("0.3205")), //
				new CustomerSalesVolume(ws2, mdr750, new BigDecimal("50.0128")), //
				new CustomerSalesVolume(ws2, mor125, new BigDecimal("0.6667")), //
				new CustomerSalesVolume(ws2, mor15l, new BigDecimal("0.0897")), //
				new CustomerSalesVolume(ws2, mor500, new BigDecimal("0.2724")), //
				new CustomerSalesVolume(ws2, mor8oz, new BigDecimal("15.0417")), //
				new CustomerSalesVolume(ws2, mrb15l, new BigDecimal("0.1122")), //
				new CustomerSalesVolume(ws2, mrb20l, new BigDecimal("0.1763")), //
				new CustomerSalesVolume(ws2, pbl8oz, new BigDecimal("20.9199")), //
				new CustomerSalesVolume(ws2, mmx500, new BigDecimal("0.0032")), //
				new CustomerSalesVolume(ws2, prg125, new BigDecimal("1.2997")), //
				new CustomerSalesVolume(ws2, prg15l, new BigDecimal("0.0801")), //
				new CustomerSalesVolume(ws2, prg156, new BigDecimal("0.1442")), //
				new CustomerSalesVolume(ws2, prg10l, new BigDecimal("0.7372")), //
				new CustomerSalesVolume(ws2, prg250, new BigDecimal("3.3429")), //
				new CustomerSalesVolume(ws2, prg20l, new BigDecimal("0.0128")), //
				new CustomerSalesVolume(ws2, prg345, new BigDecimal("0.1603")), //
				new CustomerSalesVolume(ws2, prg500, new BigDecimal("0.3365")), //
				new CustomerSalesVolume(ws2, prg750, new BigDecimal("7.7115")), //
				new CustomerSalesVolume(ws2, prg8oz, new BigDecimal("146.0673")), //
				new CustomerSalesVolume(ws2, pwr350, new BigDecimal("0.1763")), //
				new CustomerSalesVolume(ws2, pwr500, new BigDecimal("1.8173")), //
				new CustomerSalesVolume(ws2, ses240, new BigDecimal("26.5385")), //
				new CustomerSalesVolume(ws2, ses330, new BigDecimal("0.7115")), //
				new CustomerSalesVolume(ws2, srg125, new BigDecimal("0.6843")), //
				new CustomerSalesVolume(ws2, srg15l, new BigDecimal("0.0609")), //
				new CustomerSalesVolume(ws2, srg12o, new BigDecimal("8.6314")), //
				new CustomerSalesVolume(ws2, srg10l, new BigDecimal("0.5417")), //
				new CustomerSalesVolume(ws2, srg500, new BigDecimal("0.3365")), //
				new CustomerSalesVolume(ws2, srg750, new BigDecimal("0.2885")), //
				new CustomerSalesVolume(ws2, srg7oz, new BigDecimal("35.2724")), //
				new CustomerSalesVolume(ws2, tor240, new BigDecimal("35.7115")), //
				new CustomerSalesVolume(ws2, tor355, new BigDecimal("0.5288"))));
	}
}
