package ph.txtdis.sms;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.STORE_KEEPER;
import static ph.txtdis.util.DateTimeUtils.toZonedDateTime;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.smslib.AGateway;
import org.smslib.AGateway.Protocols;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ph.txtdis.domain.CustomerImpl;
import ph.txtdis.domain.Item;
import ph.txtdis.domain.SalesOrder;
import ph.txtdis.domain.SalesOrderItem;
import ph.txtdis.domain.SmsLog;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.ItemRepository;
import ph.txtdis.repository.SmsLogRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.service.SalesOrderService;
import ph.txtdis.util.NumberUtils;
import ph.txtdis.util.PhoneUtils;

@Component("smsRunner")
public class SmsRunner implements CommandLineRunner {

	private static Logger logger = getLogger(SmsRunner.class);

	private static final String ERROR = "ERROR:\n";

	private static final String ITEM_CODE_REPLY = "Reply with ITEM CODES to receive latest product keywords";

	private String malformedSmsMessage = ERROR//
			+ "SMS must strictly adhere to the following patterns:\n" //
			+ "DELIVER or STOP afterwards S/O number, " //
			+ "or ITEM CODE followed by quantity in cases.\n" //
			+ "E.g., DELIVER 123, STOP 456, or STOCK ON-HAND PBL80Z 7.\n" //
			+ "The 1st approves S/O No. 123, the 2nd rejects S/O No. 456 " //
			+ "and the last, orders Pepsi Blue 8oz " //
			+ "based on its current stock-on-hand of seven cases.\n"//
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

	private class InboundNotification implements IInboundMessageNotification {
		@Override
		public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
			try {
				logger.info(msg);
				SmsRunner.this.processSms(msg);
				Service.getInstance().deleteMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run(String... args) throws Exception {
		SerialModemGateway gateway = new SerialModemGateway("txtDIS", "COM8", 115200, "ZTE", "MF622");
		gateway.setInbound(true);
		gateway.setOutbound(true);
		gateway.setSmscNumber("+639180000101");
		gateway.setProtocol(Protocols.PDU);

		Service s = Service.getInstance();
		s.setInboundMessageNotification(new InboundNotification());
		s.addGateway(gateway);
		s.startService();

		List<InboundMessage> msgList = new ArrayList<>();
		Service.getInstance().readMessages(msgList, MessageClasses.ALL);
		for (InboundMessage msg : msgList)
			Service.getInstance().deleteMessage(msg);
	}

	public void send(String phone, String message) throws Exception {
		OutboundMessage msg = new OutboundMessage(phone, message);
		System.out.println(msg);
		Service.getInstance().sendMessage(msg);
	}

	public void processSms(InboundMessage sms) throws Exception {
		String msg = sms.getText();
		if (msg.isEmpty())
			return;

		String senderPhone = PhoneUtils.persistPhone(sms.getOriginator());
		String[] orders = msg.trim().toUpperCase().split("\\s+");

		logger.info("size = " + orders.length);
		for (String string : orders)
			logger.info("element = " + string);

		if (orders.length == 0 || orders.length % 2 != 0) {
			send(senderPhone, malformedSmsMessage);
			return;
		}

		if (orders[0].equals("ERROR:"))
			return;

		CustomerImpl customer = customerRepository.findByMobile(senderPhone);
		if (customer == null) {
			send(senderPhone, ERROR + "Unknown phone number");
			return;
		}

		if (orders[0].equals("ITEM"))
			sendItemCodes(senderPhone);
		else if (orders[0].equals("STOP") || orders[0].equals("DELIVER"))
			processDecidedSalesOrder(customer, sms, orders[0], orders[1]);
		else if (areValidItemNameAndQuantityPairs(orders))
			createNewSalesOrder(senderPhone, orders, customer);
		else
			send(senderPhone, malformedSmsMessage);
	}

	private boolean areValidItemNameAndQuantityPairs(String[] orders) {
		List<String> items = itemNames();
		for (int i = 0; i < orders.length; i += 2) {

			String item = orders[i];
			logger.info("index = " + i + ", item = " + item);
			if (!items.contains(item)) {
				malformedSmsMessage = error(item, "product code") + " " + ITEM_CODE_REPLY;
				return false;
			}

			String qty = orders[i + 1];
			logger.info("index = " + (i + 1) + ", qty = " + qty);
			if (!isNumeric(qty)) {
				malformedSmsMessage = error(qty, "quantity of product: " + item);
				return false;
			}
		}
		return true;
	}

	private String error(String value, String name) {
		return ERROR + "[" + value + "] is not a valid " + name;
	}

	private void processDecidedSalesOrder(CustomerImpl customer, InboundMessage sms, String decision,
			String salesOrderId) throws Exception {
		SmsLog log = logSms(customer, sms);
		salesOrderService.processDecidedSalesOrder(log, customer, salesOrderId);
		if (decision.equals("DELIVER"))
			for (String mobile : getPhoneNosOfStorekeepers())
				send(mobile, "S/O No. " + salesOrderId + " of " + customer.getName() + " has been approved for delivery");
		else
			send(sms.getOriginator(),
					"This is to confirm your disapproval to deliver S/O No. " + salesOrderId + ". "
							+ "If you've sent the text by mistake, "
							+ "please resend the current stock-on-hand of the product/s you want delivered "
							+ "to repeat the process.");
	}

	private SmsLog logSms(CustomerImpl customer, InboundMessage sms) {
		ZonedDateTime zdt = toZonedDateTime(sms.getDate());
		SmsLog log = new SmsLog(zdt, customer, sms.getText());
		return smsLogRepository.save(log);
	}

	private List<String> getPhoneNosOfStorekeepers() {
		List<UserEntity> users = userRepository
				.findByEnabledTrueAndRolesAuthorityInOrderByUsernameAsc(asList(STORE_KEEPER));
		return users.stream().map(UserEntity::getMobile).collect(toList());
	}

	private void sendItemCodes(String originator) throws Exception {
		List<String> itemNames = itemNames();
		int l = 469;
		String names = "";
		for (String name : itemNames) {
			names += (name.toLowerCase().replace("l", "L") + "\n");
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

	private void createNewSalesOrder(String originator, String[] orders, CustomerImpl customer) throws Exception {
		computeSalesOrderItemQuantitiesBasedOnInventoryLevel(orders, customer);
		SalesOrder so = salesOrderService.save();
		String msg = salesOrder(so);
		String[] split = StringUtils.split(msg, "|");
		for (String part : split)
			send(originator, part);
		SmsLog log = new SmsLog(ZonedDateTime.now(), customer, replace(msg, "|", ""));
		log = smsLogRepository.save(log);
		salesOrderService.setSentLog(log);
		salesOrderService.save();
	}

	private void computeSalesOrderItemQuantitiesBasedOnInventoryLevel(String[] orders, CustomerImpl customer) {
		for (int i = 0; i < orders.length; i += 2)
			salesOrderService.computeSalesOrderItemQuantitiesBasedOnStockOnHand(customer, orders[i], orders[i + 1]);
	}

	private String salesOrder(SalesOrder so) {
		return "Get the delivery schedule of the S/O below "//
				+ "by replying DELIVER " + so.getId() //
				+ ", else STOP:" + so.getId() + "\n"//
				+ "TOTAL = P" + NumberUtils.format2Place(salesOrderService.computeTotalAmount(so)) + "\n"//
				+ salesOrderItems(so);
	}

	private String salesOrderItems(SalesOrder so) {
		String s = "";
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
}
