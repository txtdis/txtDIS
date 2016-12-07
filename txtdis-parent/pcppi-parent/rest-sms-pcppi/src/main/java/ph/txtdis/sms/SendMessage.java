package ph.txtdis.sms;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.STORE_KEEPER;
import static ph.txtdis.util.DateTimeUtils.toZonedDateTime;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import ph.txtdis.domain.CustomerImpl;
import ph.txtdis.domain.Item;
import ph.txtdis.domain.SalesOrder;
import ph.txtdis.domain.SalesOrderItem;
import ph.txtdis.domain.SmsLog;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.ItemRepository;
import ph.txtdis.repository.SmsLogRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.service.SalesOrderService;
import ph.txtdis.util.NumberUtils;
import ph.txtdis.util.PhoneUtils;

//@Component("sendMessage")
public class SendMessage implements CommandLineRunner {

	private static Logger logger = getLogger(SendMessage.class);

	private static final String ERROR = "ERROR:\n";

	private static final String ITEM_CODE_REPLY = "Reply with ITEM CODES to receive latest product keywords";

	private String malformedSmsMessage = ERROR//
			+ "SMS must strictly adhere to the following patterns:\n" //
			+ "DELIVER or STOP afterwards S/O number, " //
			+ "or ITEM CODE followed by quantity in cases.\n" //
			+ "E.g., DELIVER 123, STOP 456, or STOCK ON-HAND PBL80Z 7.\n" //
			+ "The 1st approves S/O No. 123, the 2nd rejects S/O No. 456 " //
			+ "and the last, orders Pepsi Blue 8oz " //
			+ "based on its stock-on-hand of seven cases.\n"//
			+ ITEM_CODE_REPLY;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private SalesOrderService salesOrderService;

	@Autowired
	private SmsLogRepository smsLogRepository;

	@Autowired
	private UserRepository userRepository;

	private SalesOrder salesOrder;

	void send(String phone, String msg) {
		if (msg.isEmpty())
			return;

		String senderPhone = PhoneUtils.persistPhone(phone);
		String[] orders = msg.split("\\s+");

		logger.info("size = " + orders.length + "");
		for (String string : orders)
			logger.info("    element = " + string);

		if (orders[0].equals("ERROR:"))
			return;

		if (orders.length == 0 || orders.length % 2 != 0) {
			send(senderPhone, malformedSmsMessage);
			return;
		}

		CustomerImpl customer = customerRepository.findByMobile(senderPhone);
		if (customer == null) {
			send(senderPhone, ERROR + "Unknown phone number");
			return;
		}

		if (orders[0].equals("ITEM"))
			sendItemCodes(senderPhone);
		else if (orders[0].equals("STOP") || orders[0].equals("DELIVER"))
			processDecidedSalesOrder(senderPhone, customer, msg, orders[0], orders[1]);
		else if (areValidItemNameAndQuantityPairs(orders))
			createNewSalesOrder(senderPhone, orders, customer);
		else
			send(senderPhone, malformedSmsMessage);
	}

	private void processDecidedSalesOrder(String senderPhone, CustomerImpl customer, String sms, String decision,
			String salesOrderId) {
		try {
			SmsLog log = logSms(customer, sms);
			salesOrderService.processDecidedSalesOrder(log, customer, salesOrderId);
			if (decision.equals("DELIVER"))
				for (String mobile : getPhoneNosOfStorekeepers())
					logger.info("Sent to " + mobile + ":\n"//
							+ "S/O No. " + salesOrderId + " of " + customer.getName() + " has been approved for delivery");
		} catch (NotFoundException e) {
			send(senderPhone, ERROR + e.getMessage());
		}
	}

	private SmsLog logSms(CustomerImpl customer, String msg) {
		ZonedDateTime zdt = toZonedDateTime(new Date());
		SmsLog log = new SmsLog(zdt, customer, msg);
		return smsLogRepository.save(log);
	}

	private List<String> getPhoneNosOfStorekeepers() {
		List<UserEntity> users = userRepository
				.findByEnabledTrueAndRolesAuthorityInOrderByUsernameAsc(asList(STORE_KEEPER));
		return users.stream().map(UserEntity::getMobile).collect(toList());
	}

	private boolean areValidItemNameAndQuantityPairs(String[] orders) {
		List<String> items = itemNames();
		for (int i = 0; i < orders.length; i += 2) {

			String item = orders[i];
			logger.info("index = " + i + ", item = " + item);
			if (!items.contains(item)) {
				if (item.length() == 6)
					malformedSmsMessage = error(item, "item code") + " " + ITEM_CODE_REPLY;
				return false;
			}

			String qty = orders[i + 1];
			logger.info("index = " + (i + 1) + ", qty = " + qty);
			if (!isNumeric(qty)) {
				malformedSmsMessage = error(qty, "quantity of item " + item);
				return false;
			}
		}
		return true;
	}

	private String error(String value, String name) {
		return ERROR + "[" + value + "] is not a valid " + name;
	}

	private void sendItemCodes(String originator) {
		List<String> itemNames = itemNames();
		int l = 469;
		String names = "";
		for (String name : itemNames) {
			names += name;
			if (names.length() > l) {
				names += ("|");
				l += 480;
			}
		}
		String[] msg = StringUtils.split(names, "|");
		for (String part : msg)
			send(originator, part);
	}

	private List<String> itemNames() {
		Iterable<Item> items = itemRepository.findAll();
		Stream<Item> stream = StreamSupport.stream(items.spliterator(), false);
		return stream.map(s -> s.getSmsId()).sorted().collect(Collectors.toList());
	}

	private void createNewSalesOrder(String originator, String[] orders, CustomerImpl customer) {
		computeSalesOrderItemQuantitiesBasedOnInventoryLevel(orders, customer);
		salesOrder = salesOrderService.save();
		String msg = salesOrder(salesOrder);
		String[] split = StringUtils.split(msg, "|");
		for (String part : split)
			logger.info(part);
		SmsLog log = new SmsLog(ZonedDateTime.now(), customer, replace(msg, "|", ""));
		log = smsLogRepository.save(log);
		salesOrderService.setSentLog(log);
		salesOrder = salesOrderService.save();
	}

	private void computeSalesOrderItemQuantitiesBasedOnInventoryLevel(String[] orders, CustomerImpl customer) {
		for (int i = 0; i < orders.length; i += 2)
			salesOrderService.computeSalesOrderItemQuantitiesBasedOnStockOnHand(customer, orders[i], orders[i + 1]);
	}

	private String salesOrder(SalesOrder so) {
		return "Get the delivery schedule of the S/O below "//
				+ "by replying DELIVER " + so.getId() //
				+ ", else STOP " + so.getId() + " \n"//
				+ "TOTAL = P" + NumberUtils.format2Place(salesOrderService.computeTotalAmount(so)) + "\n"//
				+ salesOrderItems(so);
	}

	private String salesOrderItems(SalesOrder so) {
		String s = "\n";
		int l = 462;
		for (SalesOrderItem detail : so.getDetails()) {
			s += (detail.getItem().getSmsId() + " " + detail.getQty() + "\n");
			if (s.length() > l) {
				s += ("|");
				l += 480;
			}
		}
		return s;
	}

	@Override
	public void run(String... args) throws Exception {
		send("09175393161", "Hello world!");
		send("09171234567", "DELIVER 1");
		send("09175393161",
				"GBB350  5  \n"//
						+ "PBL8OZ 10\n"//
						+ "SRG751  5 \n"//
						+ "SRG7OZ 10\n"//
						+ "TOR240  5 \n"//
						+ "TOR355  5\n");
		send("09175393161",
				"GBB350  5  \n"//
						+ "PBL8OZ 10\n"//
						+ "SRG751  5 \n"//
						+ "SRG7OZ 1A\n"//
						+ "TOR240  5 \n"//
						+ "TOR355  5\n");
		send("09175393161",
				"GBB350  5  \n"//
						+ "PBL8OZ 10\n"//
						+ "SRG750  5 \n"//
						+ "SRG7OZ 10\n"//
						+ "TOR240  5 \n"//
						+ "TOR355  5\n");
		send("09175393161", "DELIVER " + salesOrder.getId());
		send("09258307534",
				"GBB350  5  \n"//
						+ "PBL8OZ 10\n"//
						+ "SRG750  5 \n"//
						+ "SRG7OZ 10\n"//
						+ "TOR240  5 \n"//
						+ "TOR355  5\n");
		send("09258307534", "STOP " + salesOrder.getId());
	}
}