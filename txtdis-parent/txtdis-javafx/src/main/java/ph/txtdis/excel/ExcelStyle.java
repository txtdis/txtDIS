package ph.txtdis.excel;

import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;
import static org.apache.poi.ss.usermodel.IndexedColors.GREY_50_PERCENT;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("excelStyle")
public class ExcelStyle {

	private static final String CALIBRI = "Calibri";

	private HSSFWorkbook wb;

	private Font normalFont, greenFont, redFont, titleFont, boldFont;

	private CellStyle bool, center, currency, currencySum, date, decimal, decimalSum, falseBool, header, id, integer,
			left, red, right, title, trueBool, verticalHeader;

	private DataFormat format;

	private Map<Integer, CellStyle> styleMap;

	public ExcelStyle add(HSSFWorkbook wb) {
		this.wb = wb;
		this.format = wb.getCreationHelper().createDataFormat();
		setAll();
		return this;
	}

	public CellStyle bool() {
		return bool;
	}

	public CellStyle center() {
		return center;
	}

	public CellStyle currency() {
		return currency;
	}

	public CellStyle currencySum() {
		return currencySum;
	}

	public CellStyle date() {
		return date;
	}

	public CellStyle decimal() {
		return decimal;
	}

	public CellStyle decimalSum() {
		return decimalSum;
	}

	public CellStyle falseBool() {
		return falseBool;
	}

	public CellStyle fraction(Integer denominator) {
		CellStyle s = styleMap.get(denominator);
		if (s == null)
			setFraction(denominator);
		return styleMap.get(denominator);
	}

	public CellStyle header() {
		return header;
	}

	public CellStyle id() {
		return id;
	}

	public CellStyle integer() {
		return integer;
	}

	public CellStyle left() {
		return left;
	}

	public CellStyle red() {
		return red;
	}

	public CellStyle right() {
		return right;
	}

	public CellStyle title() {
		return title;
	}

	public CellStyle trueBool() {
		return trueBool;
	}

	public CellStyle verticalHeader() {
		return verticalHeader;
	}

	private Font boldFont() {
		return boldFont;
	}

	private short currencyFormat() {
		return format.getFormat("_(₱* #,##0.00_);[Red]_(₱* (#,##0.00);_(₱* \"-\"??_);_(@_)");
	}

	private short dateFormat() {
		return format.getFormat("m/d/yy;@");
	}

	private short decimalFormat() {
		return format.getFormat("_(#,##0.00_);[Red]_((#,##0.00);_(\"-\"??_);_(@_)");
	}

	private short fractionFormat(int denominator) {
		return format.getFormat("# ??/" + denominator + ";-# ??/" + denominator + ";\"\"??");
	}

	private Font greenFont() {
		return greenFont;
	}

	private short idFormat() {
		return format.getFormat("###0");
	}

	private short integerFormat() {
		return format.getFormat("_(#,##0_);[Red]_((#,##0);_(\"-\"??_);_(@_)");
	}

	private Font normalFont() {
		return normalFont;
	}

	private Font redFont() {
		return redFont;
	}

	private void setAll() {
		styleMap = new HashMap<>();
		setFonts();
		setStyles();
	}

	private void setBoldFont() {
		boldFont = wb.createFont();
		boldFont.setFontName(CALIBRI);
		boldFont.setBold(true);
		boldFont.setFontHeightInPoints((short) 11);
	}

	private void setBool() {
		bool = wb.createCellStyle();
		bool.setFont(normalFont());
		bool.setAlignment(CENTER);
	}

	private void setCenter() {
		center = wb.createCellStyle();
		center.setFont(normalFont());
		center.setAlignment(CENTER);
	}

	private void setCurrency() {
		currency = wb.createCellStyle();
		currency.setFont(normalFont());
		currency.setDataFormat(currencyFormat());
		currency.setAlignment(HorizontalAlignment.RIGHT);
	}

	private void setCurrencySum() {
		currencySum = wb.createCellStyle();
		currencySum.setFont(normalFont());
		currencySum.setDataFormat(currencyFormat());
		currencySum.setAlignment(HorizontalAlignment.RIGHT);
		currencySum.setBorderTop(BorderStyle.THIN);
		currencySum.setBorderBottom(BorderStyle.DOUBLE);
	}

	private void setDate() {
		date = wb.createCellStyle();
		date.setFont(normalFont());
		date.setDataFormat(dateFormat());
		date.setAlignment(CENTER);
	}

	private void setDecimal() {
		decimal = wb.createCellStyle();
		decimal.setFont(normalFont());
		decimal.setDataFormat(decimalFormat());
		decimal.setAlignment(HorizontalAlignment.RIGHT);
	}

	private void setDecimalSum() {
		decimalSum = wb.createCellStyle();
		decimalSum.setFont(normalFont());
		decimalSum.setDataFormat(decimalFormat());
		decimalSum.setAlignment(HorizontalAlignment.RIGHT);
		decimalSum.setBorderTop(BorderStyle.THIN);
		decimalSum.setBorderBottom(BorderStyle.DOUBLE);
	}

	private void setFalseBool() {
		falseBool = wb.createCellStyle();
		falseBool.setFont(redFont());
		falseBool.setAlignment(CENTER);
	}

	private void setFonts() {
		setBoldFont();
		setGreenFont();
		setNormalFont();
		setRedFont();
		setTitleFont();
	}

	private void setFraction(Integer denominator) {
		CellStyle fraction = wb.createCellStyle();
		fraction.setFont(normalFont());
		fraction.setDataFormat(fractionFormat(denominator));
		fraction.setAlignment(HorizontalAlignment.RIGHT);
		styleMap.put(denominator, fraction);
	}

	private void setGreenFont() {
		greenFont = wb.createFont();
		greenFont.setFontName(CALIBRI);
		greenFont.setFontHeightInPoints((short) 11);
		greenFont.setColor(HSSFColor.GREEN.index);
	}

	private void setHeader() {
		header = wb.createCellStyle();
		header.setFillForegroundColor(GREY_50_PERCENT.getIndex());
		header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		header.setFont(boldFont());
		header.setAlignment(CENTER);
		header.setVerticalAlignment(VerticalAlignment.CENTER);
		header.setBorderRight(BorderStyle.THIN);
		header.setBorderLeft(BorderStyle.THIN);
		header.setBorderBottom(BorderStyle.THIN);
		header.setWrapText(true);
	}

	private void setId() {
		id = wb.createCellStyle();
		id.setFont(normalFont());
		id.setDataFormat(idFormat());
		id.setAlignment(HorizontalAlignment.RIGHT);
	}

	private void setInteger() {
		integer = wb.createCellStyle();
		integer.setFont(normalFont());
		integer.setDataFormat(integerFormat());
		integer.setAlignment(HorizontalAlignment.RIGHT);
	}

	private void setLeft() {
		left = wb.createCellStyle();
		left.setFont(normalFont());
		left.setAlignment(HorizontalAlignment.LEFT);
	}

	private void setNormalFont() {
		normalFont = wb.createFont();
		normalFont.setFontName(CALIBRI);
		normalFont.setFontHeightInPoints((short) 11);
	}

	private void setRed() {
		red = wb.createCellStyle();
		red.setFont(redFont());
		red.setAlignment(HorizontalAlignment.RIGHT);
	}

	private void setRedFont() {
		redFont = wb.createFont();
		redFont.setFontName(CALIBRI);
		redFont.setFontHeightInPoints((short) 11);
		redFont.setColor(HSSFColor.RED.index);
	}

	private void setRight() {
		right = wb.createCellStyle();
		right.setFont(normalFont());
		right.setAlignment(HorizontalAlignment.RIGHT);
	}

	private void setStyles() {
		setBool();
		setCenter();
		setCurrency();
		setCurrencySum();
		setDate();
		setDecimal();
		setDecimalSum();
		setFalseBool();
		setHeader();
		setId();
		setInteger();
		setLeft();
		setRed();
		setRight();
		setTitle();
		setTrueBool();
		setVerticalHeader();
	}

	private void setTitle() {
		title = wb.createCellStyle();
		title.setFont(titleFont());
		title.setAlignment(HorizontalAlignment.LEFT);
		title.setVerticalAlignment(VerticalAlignment.CENTER);
		title.setBorderRight(BorderStyle.THIN);
		title.setBorderLeft(BorderStyle.THIN);
		title.setBorderBottom(BorderStyle.THIN);
		title.setWrapText(true);
	}

	private void setTitleFont() {
		titleFont = wb.createFont();
		titleFont.setFontName(CALIBRI);
		titleFont.setBold(true);
		titleFont.setFontHeightInPoints((short) 15);
	}

	private void setTrueBool() {
		trueBool = wb.createCellStyle();
		trueBool.setFont(greenFont());
		trueBool.setAlignment(CENTER);
	}

	private void setVerticalHeader() {
		verticalHeader = wb.createCellStyle();
		verticalHeader.setFont(boldFont());
		verticalHeader.setAlignment(CENTER);
		verticalHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
		verticalHeader.setBorderRight(BorderStyle.THIN);
		verticalHeader.setBorderLeft(BorderStyle.THIN);
		verticalHeader.setBorderBottom(BorderStyle.THIN);
		verticalHeader.setWrapText(false);
		verticalHeader.setRotation((short) 90);
	}

	private Font titleFont() {
		return titleFont;
	}
}
