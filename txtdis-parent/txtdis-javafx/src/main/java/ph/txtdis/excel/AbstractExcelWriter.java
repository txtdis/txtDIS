package ph.txtdis.excel;

import org.apache.commons.lang3.math.Fraction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.fx.table.AppTableColumn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Desktop.getDesktop;
import static java.io.File.createTempFile;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.poi.ss.usermodel.Sheet.*;
import static ph.txtdis.util.DateTimeUtils.toUtilDate;
import static ph.txtdis.util.ReflectionUtils.invokeMethod;
import static ph.txtdis.util.TextUtils.*;

public abstract class AbstractExcelWriter //
	implements ExcelWriter {

	@Autowired
	protected ExcelStyle style;

	protected AppTableColumn column;

	protected List<String> getters;

	protected int colIdx;

	protected AppTable<?> table;

	protected HSSFWorkbook workbook;

	protected Sheet sheet;

	private List<List<AppTable<?>>> tables;

	private String filename;

	private String[] sheetnames;

	@Override
	public ExcelWriter filename(String filename) {
		this.filename = filename;
		return this;
	}

	@Override
	public ExcelWriter sheetname(String... sheetnames) {
		this.sheetnames = sheetnames;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ExcelWriter table(List<AppTable<?>>... tables) {
		this.tables = asList(tables);
		return this;
	}

	@Override
	public ExcelWriter table(AppTable<?>... tables) {
		this.tables = asList(asList(tables));
		return this;
	}

	@Override
	public void write() throws IOException {
		setup();
		writeWorkbook();
	}

	private void setup() {
		workbook = new HSSFWorkbook();
		style.add(workbook);
		create();
	}

	private void create() {
		for (int i = 0; i < sheetnames.length; i++) {
			sheet = workbook.createSheet(sheetnames[i]);
			createSheet();
			colIdx = 0;
			for (AppTable<?> table : tables.get(i)) {
				this.table = table;
				getters = new ArrayList<>();
				addTitle();
				addHeader();
				populateRows();
				addFooter();
				colIdx += table.getColumnCount();
				sheet.setColumnWidth(colIdx++, 750);
			}
		}
	}

	protected void createSheet() {
		sheet.createFreezePane(0, 2, 0, 2);
		sheet.setMargin(TopMargin, 0.25);
		sheet.setMargin(BottomMargin, 0.25);
		sheet.setMargin(LeftMargin, 0.25);
		sheet.setMargin(RightMargin, 0.25);
		sheet.setMargin(TopMargin, 0.25);
		sheet.setMargin(HeaderMargin, 0.25);
		sheet.setMargin(FooterMargin, 0.25);
		sheet.setFitToPage(true);
		sheet.setPrintGridlines(true);
		sheet.setRepeatingRows(CellRangeAddress.valueOf("1:2"));
		sheet.protectSheet("secretPassword");
	}

	protected void addTitle() {
		mergedRegion(0);
		Row row = getRow(0);
		row.setHeightInPoints(30);
		setTitleCell(row);
	}

	protected void mergedRegion(int row) {
		sheet.addMergedRegion(new CellRangeAddress(row, row, colIdx, colIdx + table.getColumnCount() - 1));
	}

	private void setTitleCell(Row row) {
		Cell c = row.createCell(colIdx);
		c.setCellValue(table.getId());
		c.setCellStyle(style.title());
	}

	protected void addHeader() {
		List<?> columns = filterVisibleColumns();
		for (int i = 0; i < columns.size(); i++) {
			column = (AppTableColumn) columns.get(i);
			sheet.setColumnWidth(i + colIdx, getWidth());
			addCell(headerStyle(), headerRow(), i + colIdx);
			getters.add("get" + capitalize(column.getId()));
		}
	}

	private List<?> filterVisibleColumns() {
		return table.getColumns().stream()//
			.filter(c -> ((AppTableColumn) c).isVisible())//
			.collect(toList());
	}

	private int getWidth() {
		return (int) (column.getWidth() / 10 + 2) * 256;
	}

	protected void addCell(CellStyle style, Row row, int i) {
		Cell c = row.createCell(i);
		c.setCellValue(column.getText());
		c.setCellStyle(style);
	}

	private CellStyle headerStyle() {
		if (column.isHeaderVertical())
			return style.verticalHeader();
		return style.header();
	}

	protected Row headerRow() {
		Row r = getRow(1);
		if (column.isHeaderVertical())
			r.setHeightInPoints(96);
		return r;
	}

	protected Row getRow(int rowIdx) {
		Row row = sheet.getRow(rowIdx);
		if (row == null)
			row = sheet.createRow(rowIdx);
		return row;
	}

	private void populateRows() {
		List<?> items = table.getItems();
		for (int i = 0; i < items.size(); i++)
			populateColumns(items.get(i), getRow(i + 2));
		addSummationIfRequired();
	}

	private void populateColumns(Object item, Row row) {
		for (int i = 0; i < getters.size(); i++)
			setValue(getValue(item, getters.get(i)), getters.get(i), row.createCell(i + colIdx));
	}

	private void setValue(Object o, String getter, Cell cell) {
		if (o == null)
			setText(cell, "");
		else if (endsWith(getter, "Id"))
			setId(cell, (Long) o);
		else if (endsWith(getter, "Count") || endsWith(getter, "InDays"))
			setInteger(cell, (Integer) o);
		else if (endsWith(getter, "Vol"))
			setDecimal(cell, (BigDecimal) o);
		else if (endsWith(getter, "ValueSum"))
			setCurrencySum(cell, (BigDecimal) o);
		else if (endsWith(getter, "QtySum"))
			setQtySum(cell, (BigDecimal) o);
		else if (endsWith(getter, "Value"))
			setCurrency(cell, (BigDecimal) o);
		else if (endsWith(getter, "Qty"))
			setQty(cell, (BigDecimal) o);
		else if (endsWith(getter, "Date"))
			setDate(cell, (LocalDate) o);
		else if (endsWith(getter, "Type") || endsWith(getter, "Uom") || endsWith(getter, "Day"))
			setCenter(cell, blankIfNull(o));
		else if (endsWith(getter, "InFractions"))
			setFraction(cell, (Fraction) o);
		else if (endsWith(getter, "Level"))
			setRight(cell, (String) o);
		else if (endsWith(getter, "Valid") || endsWith(getter, "Paid") || endsWith(getter, "Inactive") ||
			endsWith(getter, "ed"))
			setBoolean(cell, (Boolean) o);
		else if (endsWith(getter, "Name"))
			setName(cell, (String) o);
		else
			setText(cell, o.toString());
	}

	private void setId(Cell c, Long l) {
		if (l != null) {
			c.setCellStyle(style.id());
			c.setCellValue(l);
		}
		else
			setNullCell(c);
	}

	private void setNullCell(Cell c) {
		c.setCellStyle(style.left());
		c.setCellValue("");
	}

	private void setInteger(Cell c, Integer i) {
		if (i != null) {
			c.setCellStyle(style.integer());
			c.setCellValue(i);
		}
		else
			setNullCell(c);
	}

	private void setDecimal(Cell c, BigDecimal d) {
		c.setCellStyle(style.decimal());
		c.setCellValue(d.doubleValue());
	}

	protected void setCurrencySum(Cell c, BigDecimal d) {
		c.setCellStyle(style.currencySum());
		c.setCellValue(d.doubleValue());
	}

	private void setQtySum(Cell c, BigDecimal d) {
		c.setCellStyle(style.decimalSum());
		c.setCellValue(d.doubleValue());
	}

	private void setCurrency(Cell c, BigDecimal d) {
		c.setCellStyle(style.currency());
		c.setCellValue(d.doubleValue());
	}

	private void setQty(Cell c, BigDecimal d) {
		c.setCellStyle(style.integer());
		c.setCellValue(d.doubleValue());
	}

	private void setDate(Cell c, LocalDate d) {
		c.setCellStyle(style.date());
		c.setCellValue(toUtilDate(d));
	}

	private void setCenter(Cell c, String t) {
		c.setCellStyle(style.center());
		c.setCellValue(t);
	}

	private void setFraction(Cell c, Fraction f) {
		int denominator = f.getDenominator();
		c.setCellStyle(style.fraction(denominator));
		c.setCellValue(f.doubleValue());
	}

	private void setRight(Cell c, String t) {
		c.setCellStyle(t.contains(">") ? style.red() : style.right());
		c.setCellValue(t);
	}

	private void setBoolean(Cell c, Boolean b) {
		c.setCellStyle(style.bool());
		c.setCellValue(toBoolSign(b));
	}

	private void setName(Cell c, String t) {
		if (t != null) {
			c.setCellStyle(t.endsWith(HAS_OVERDUES) ? style.strikeout() : style.left());
			c.setCellValue(t.replace(HAS_OVERDUES, ""));
		}
		else
			setNullCell(c);
	}

	private void setText(Cell c, String t) {
		if (t != null) {
			c.setCellStyle(t.contains(">") ? style.red() : style.left());
			c.setCellValue(t);
		}
		else
			setNullCell(c);
	}

	private Object getValue(Object item, String method) {
		return invokeMethod(item, method);
	}

	private void addSummationIfRequired() {
		List<BigDecimal> totals = table.getColumnTotals();
		for (int i = totals.size() - 1; i >= 0; i--)
			setValue(totals.get(i), addSumSuffixToGetterMethodName(getters, i), createSumCell(i));
	}

	private String addSumSuffixToGetterMethodName(List<String> getters, int i) {
		return getters.get(i + table.getColumnIndexOfFirstTotal()) + "Sum";
	}

	private Cell createSumCell(int i) {
		return getLastRow().createCell(i + colIdx + table.getColumnIndexOfFirstTotal());
	}

	protected Row getLastRow() {
		return getRow(table.getLastRowIndex() + 2);
	}

	private void writeWorkbook() throws IOException {
		File f = createTempFile(filename + ".version.", ".xls");
		writeWorkbook(f);
		getDesktop().open(f);
		f.deleteOnExit();
	}

	private void writeWorkbook(File f) throws IOException {
		FileOutputStream s = new FileOutputStream(f);
		workbook.write(s);
		s.close();
		workbook.close();
	}

	protected void addFooter() {
	}
}