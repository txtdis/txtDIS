package ph.txtdis.excel;

import static org.apache.poi.ss.usermodel.IndexedColors.GREY_50_PERCENT;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.stereotype.Component;

@Component("excelStyle")
public class ExcelStyle {

	private HSSFWorkbook workbook;

	private Font normalFont, redFont, boldFont;

	private CellStyle bool, center, currency, currencySum, date, decimal, decimalSum, falseBool, fraction, header, id, integer, left, red, right,
			title, trueBool, verticalHeader, strikeout;

	private DataFormat format;

	private String fontName;

	private short fontSize;

	public ExcelStyle add(HSSFWorkbook wb) {
		this.workbook = wb;
		this.format = wb.getCreationHelper().createDataFormat();
		reset();
		return this;
	}

	private void reset() {
		normalFont = null;
		redFont = null;
		boldFont = null;
		bool = null;
		center = null;
		currency = null;
		currencySum = null;
		date = null;
		decimal = null;
		decimalSum = null;
		falseBool = null;
		fraction = null;
		header = null;
		id = null;
		integer = null;
		left = null;
		red = null;
		right = null;
		title = null;
		trueBool = null;
		verticalHeader = null;
		strikeout = null;
	}

	public CellStyle bool() {
		if (bool == null)
			setBool();
		return bool;
	}

	private void setBool() {
		bool = centerNormal();
	}

	private CellStyle centerNormal() {
		CellStyle s = fullBorderedNormal();
		s.setAlignment(HorizontalAlignment.CENTER);
		return s;
	}

	private CellStyle fullBorderedNormal() {
		CellStyle s = normal();
		s = fullThinBorder(s);
		return s;
	}

	private CellStyle normal() {
		CellStyle s = workbook.createCellStyle();
		s.setFont(normalFont());
		return s;
	}

	private Font normalFont() {
		if (normalFont == null)
			setNormalFont();
		return normalFont;
	}

	private void setNormalFont() {
		normalFont = getNormalFont();
	}

	private Font getNormalFont() {
		Font f = workbook.createFont();
		f.setFontName(fontName());
		f.setFontHeightInPoints(fontSize());
		return f;
	}

	private String fontName() {
		if (fontName == null)
			fontName = "Calibri";
		return fontName;
	}

	private short fontSize() {
		if (fontSize == 0)
			fontSize = (short) 11;
		return fontSize;
	}

	public CellStyle center() {
		if (center == null)
			setCenter();
		return center;
	}

	private void setCenter() {
		center = centerNormal();
	}

	public CellStyle currency() {
		if (currency == null)
			setCurrency();
		return currency;
	}

	private void setCurrency() {
		currency = currencyStyle();
	}

	private CellStyle currencyStyle() {
		CellStyle s = rightNormal();
		s.setDataFormat(currencyFormat());
		return s;
	}

	private CellStyle rightNormal() {
		CellStyle s = fullBorderedNormal();
		s.setAlignment(HorizontalAlignment.RIGHT);
		return s;
	}

	private short currencyFormat() {
		return format.getFormat("_(₱* #,##0.00_);[Red]_(₱* (#,##0.00);_(₱* \"-\"??_);_(@_)");
	}

	public CellStyle currencySum() {
		if (currencySum == null)
			setCurrencySum();
		return currencySum;
	}

	private void setCurrencySum() {
		currencySum = currencyStyle();
		currencySum = totalStyle(currencySum);
	}

	private CellStyle totalStyle(CellStyle s) {
		s.setBorderLeft(BorderStyle.NONE);
		s.setBorderRight(BorderStyle.NONE);
		s.setBorderBottom(BorderStyle.DOUBLE);
		return s;
	}

	public CellStyle date() {
		if (date == null)
			setDate();
		return date;
	}

	private void setDate() {
		date = centerNormal();
		date.setDataFormat(format.getFormat("m/d/yy;@"));
	}

	public CellStyle decimal() {
		if (decimal == null)
			setDecimal();
		return decimal;
	}

	private void setDecimal() {
		decimal = decimalStyle();
	}

	private CellStyle decimalStyle() {
		CellStyle s = rightNormal();
		s.setDataFormat(format.getFormat("_(#,##0.00_);[Red]_((#,##0.00);_(\"-\"??_);_(@_)"));
		return s;
	}

	public CellStyle decimalSum() {
		if (decimalSum == null)
			setDecimalSum();
		return decimalSum;
	}

	private void setDecimalSum() {
		decimalSum = decimalStyle();
		decimalSum = totalStyle(decimalSum);
	}

	public CellStyle falseBool() {
		if (falseBool == null)
			setFalseBool();
		return falseBool;
	}

	private void setFalseBool() {
		falseBool = centered();
		falseBool.setFont(redFont());
	}

	private CellStyle centered() {
		CellStyle s = workbook.createCellStyle();
		s = fullThinBorder(s);
		s.setAlignment(HorizontalAlignment.CENTER);
		return s;
	}

	private Font redFont() {
		if (redFont == null)
			setRedFont();
		return redFont;
	}

	private void setRedFont() {
		redFont = normalFont();
		redFont.setColor(HSSFColorPredefined.RED.getIndex());
	}

	public CellStyle fraction(Integer denominator) {
		if (fraction == null)
			setFraction(denominator);
		return fraction;
	}

	private void setFraction(Integer denominator) {
		fraction = rightNormal();
		fraction.setDataFormat(fractionFormat(denominator));
	}

	private short fractionFormat(int denominator) {
		return format.getFormat("# ??/" + denominator + ";-# ??/" + denominator + ";\"\"??");
	}

	public CellStyle header() {
		if (header == null)
			setHeader();
		return header;
	}

	private void setHeader() {
		header = getHeader();
		header.setVerticalAlignment(VerticalAlignment.CENTER);
		header.setFillForegroundColor(GREY_50_PERCENT.getIndex());
		header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	}

	private CellStyle getHeader() {
		CellStyle s = centered();
		s = fullThinBorder(s);
		s.setFont(boldFont());
		return s;
	}

	private CellStyle fullThinBorder(CellStyle s) {
		s.setBorderTop(BorderStyle.THIN);
		s.setBorderRight(BorderStyle.THIN);
		s.setBorderLeft(BorderStyle.THIN);
		s.setBorderBottom(BorderStyle.THIN);
		return s;
	}

	public CellStyle id() {
		if (id == null)
			setId();
		return id;
	}

	private void setId() {
		id = rightNormal();
		id.setDataFormat(format.getFormat("###0"));
	}

	public CellStyle integer() {
		if (integer == null)
			setInteger();
		return integer;
	}

	private void setInteger() {
		integer = rightNormal();
		integer.setDataFormat(format.getFormat("_(#,##0_);[Red]_((#,##0);_(\"-\"??_);_(@_)"));
	}

	public CellStyle left() {
		if (left == null)
			setLeft();
		return left;
	}

	private void setLeft() {
		left = leftNormal();
	}

	private CellStyle leftNormal() {
		CellStyle s = fullBorderedNormal();
		s.setAlignment(HorizontalAlignment.LEFT);
		return s;
	}

	public CellStyle red() {
		if (red == null)
			setRed();
		return red;
	}

	private void setRed() {
		red = workbook.createCellStyle();
		red = fullThinBorder(red);
		red.setAlignment(HorizontalAlignment.RIGHT);
		red.setFont(redFont());
	}

	public CellStyle right() {
		if (right == null)
			setRight();
		return right;
	}

	private void setRight() {
		right = rightNormal();
	}

	public CellStyle strikeout() {
		if (strikeout == null)
			setStrikeout();
		return strikeout;
	}

	private void setStrikeout() {
		strikeout = workbook.createCellStyle();
		strikeout = fullThinBorder(strikeout);
		strikeout.setFont(strikeoutFont());
	}

	private Font strikeoutFont() {
		Font f = getNormalFont();
		f.setStrikeout(true);
		return f;
	}

	public CellStyle title() {
		if (title == null)
			setTitle();
		return title;
	}

	private void setTitle() {
		title = workbook.createCellStyle();
		title = fullThinBorder(title);
		title.setFont(titleFont());
		title.setAlignment(HorizontalAlignment.LEFT);
		title.setVerticalAlignment(VerticalAlignment.CENTER);
		title.setWrapText(true);
	}

	private Font titleFont() {
		Font f = workbook.createFont();
		f.setFontName(fontName());
		f.setBold(true);
		f.setFontHeightInPoints((short) 15);
		return f;
	}

	public CellStyle trueBool() {
		if (trueBool == null)
			setTrueBool();
		return trueBool;
	}

	private void setTrueBool() {
		trueBool = centered();
		trueBool.setFont(greenFont());
	}

	private Font greenFont() {
		Font f = normalFont();
		f.setColor(HSSFColorPredefined.GREEN.getIndex());
		return f;
	}

	public CellStyle verticalHeader() {
		if (verticalHeader == null)
			setVerticalHeader();
		return verticalHeader;
	}

	private void setVerticalHeader() {
		verticalHeader = getHeader();
		verticalHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
		verticalHeader.setWrapText(false);
		verticalHeader.setRotation((short) 90);
	}

	private Font boldFont() {
		if (boldFont == null)
			setBoldFont();
		return boldFont;
	}

	private void setBoldFont() {
		boldFont = normalFont();
		boldFont.setBold(true);
	}
}
