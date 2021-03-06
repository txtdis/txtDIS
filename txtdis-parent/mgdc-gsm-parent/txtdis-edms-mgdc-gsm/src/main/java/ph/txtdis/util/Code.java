package ph.txtdis.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import ph.txtdis.dto.UnitMeasured;
import ph.txtdis.type.UomType;

public class Code {

	public static final String EDMS = "eDMS";

	public static final String MGDC_GSM = "MGDC GSM";

	public static final String GSMI = "GINEBRA SAN MIGUEL, INC.";

	public static final String CUSTOMER_AUTO_NO = "Customer List";

	public static final String ITEM_AUTO_NO = "Item list";

	public static final String SELLER_AUTO_NO = "Salesman List";

	public static final String CUSTOMER_PREFIX = "Cust";

	public static final String PICKING_PREFIX = "LOGP";

	public static final String RECEIVING_PREFIX = "ILR";

	public static final String CREDIT_MEMO_PREFIX = "CM";

	public static final String DELIVERY_PREFIX = "DR";

	public static final String REMITTANCE_VARIANCE_PREFIX = "RSR";

	public static final String INVOICE_PREFIX = "SI";

	public static final String BOOKING_PREFIX = "SO";

	public static final String PAYMENT_REFERENCE_PREFIX = "REF";

	public static final String REMITTANCE_PREFIX = "PAY";

	public static final String PURCHASE_RECEIPT_PREFIX = "GSMISI";

	public static final String TRANSFER_ORDER_PREFIX = "STO";

	public static final String TRANSFER_RECEIPT_PREFIX = "STI";

	public static final String INVENTORY_VARIANCE_PREFIX = "DSMR";

	public static final String CREDIT = "Credit";

	public static final String CASH = "Cash";

	public static final String COD = "COD";

	public static final String CASH_ONLY = "1";

	public static final String CASH_AND_CREDIT = "0";

	public static final String CLOSED = "Closed";

	public static final String TRUE = "True";

	public static final String FALSE = "False";

	public static final String POSTED = TRUE;

	public static final String UNPOSTED = FALSE;

	public static final String PROMO = "Promo & Sampling";

	public static final String SALES = "Sales";

	public static final String CS = "Case";

	public static final String BTL = "Bottle";

	public static final String SALES_INVOICE = "Sales - Invoice";

	public static final String REGULAR = "Regular";

	public static final String BAD_ORDER = "Bad Order";

	public static final String BREAKAGES = "Breakages";

	public static final String GOOD_RETURNS = "Good Stock";

	public static final String ACTIVE = "Active";

	public static final String LIQUOR = "LIQUOR";

	public static final String BRANDY = "BRANDY";

	public static final String CHINESE_WINE = "CHINESE WINE";

	public static final String GIN = "GIN";

	public static final String VODKA = "VODKA";

	public static final String RETAIL = "RETAIL";

	public static final String PDC = "PDC";

	public static final String DATED_CHECK = "Dated Check";

	public static final String CREDIT_MEMO = "CREDIT MEMO";

	public static final String EWT = "W/HOLDING TAX";

	public static final String INVOICE = "INV";

	public static final String VOID = "Void";

	public static final String WAREHOUSE_SALES = "WAREHOUSE SALES";

	public static final String SHORT = "Short";

	public static final String OVER = "Over";

	public static final String EQUAL = "Equal";

	public static final String FE = "FE";

	public static final String DELIVERY = "Delivery";

	public static final String REWARD = "Reward";

	public static final Byte YES = 1;

	public static final Byte NO = 0;

	public static final short VALID = 2;

	public static final short INVALID = 1;

	public static final short PENDING = 0;

	public static final String DAMAGED = "Damage";

	public static final String EXPIRED = "Expired";

	public static final String PULL_OUT = "Pull Out";

	public static final byte STILL_ACTIVE = -1;

	public static final byte DEACTIVATED = 0;

	public static String increment(String code) {
		String[] codes = StringUtils.split(code, "-");
		int index = codes.length == 1 ? 0 : 1;
		long l = Long.valueOf(codes[index]);
		return prefix(codes, index) + incrementThenPrependWithZeroesTo8Digits(l);
	}

	private static String prefix(String[] codes, int index) {
		return index == 0 ? "" : codes[0] + "-";
	}

	private static String incrementThenPrependWithZeroesTo8Digits(long l) {
		return addZeroes(String.valueOf(++l));
	}

	public static String addZeroes(String no) {
		return addZeroes(8, no);
	}

	public static String addZeroes(int count, String no) {
		return StringUtils.leftPad(no, count, "0");
	}

	public static String padOnly(String code) {
		String[] codes = StringUtils.split(code, "-");
		int index = codes.length == 1 ? 0 : 1;
		return prefix(codes, index) + addZeroes(codes[index]);
	}

	public static String getUomCode(UnitMeasured u) {
		return u.getUom() == UomType.CS ? Code.CS : Code.BTL;
	}

	public static String capitalize(String s) {
		return s == null ? null : WordUtils.capitalizeFully(s);
	}

	public static String numbersOnly(String s) {
		return StringUtils.removePattern(s, "[a-zA-Z]");
	}
}
