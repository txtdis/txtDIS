package ph.txtdis.excel;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.poi.ss.usermodel.Sheet.BottomMargin;
import static org.apache.poi.ss.usermodel.Sheet.FooterMargin;
import static org.apache.poi.ss.usermodel.Sheet.HeaderMargin;
import static org.apache.poi.ss.usermodel.Sheet.LeftMargin;
import static org.apache.poi.ss.usermodel.Sheet.RightMargin;
import static org.apache.poi.ss.usermodel.Sheet.TopMargin;
import static ph.txtdis.util.DateTimeUtils.toUtilDate;
import static ph.txtdis.util.ReflectionUtils.invokeMethod;
import static ph.txtdis.util.TextUtils.toBoolSign;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.Fraction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.table.AppTable;
import ph.txtdis.fx.table.AppTableColumn;

@Scope("prototype")
@Component("excelWriter")
public class ExcelWriter {

	private int colIdx;

	private ArrayList<String> getters;

	private HSSFWorkbook wb;

	private List<List<AppTable<?>>> tables;

	private Sheet sheet;

	private String[] sheetnames;

	private String filename;

	private AppTable<?> table;

	private AppTableColumn column;

	@Autowired
	private ExcelStyle s;

	public ExcelWriter filename(String filename) {
		this.filename = filename;
		return this;
	}

	public ExcelWriter sheetname(String... sheetnames) {
		this.sheetnames = sheetnames;
		return this;
	}

	@SuppressWarnings("unchecked")
	public ExcelWriter table(List<AppTable<?>>... tables) {
		this.tables = asList(tables);
		return this;
	}

	public ExcelWriter table(AppTable<?>... tables) {
		this.tables = asList(asList(tables));
		return this;
	}

	public void write() throws IOException {
		setup();
		writeWorkbook();
	}

	private void writeWorkbook() throws IOException {
		File f = File.createTempFile(filename + ".version.", ".xls");
		writeWorkbook(f);
		Desktop.getDesktop().open(f);
		f.deleteOnExit();
	}

	private void writeWorkbook(File f) throws IOException {
		FileOutputStream s = new FileOutputStream(f);
		wb.write(s);
		s.close();
		wb.close();
	}

	private void addCell(CellStyle style, Row row, int i) {
		Cell c = row.createCell(i);
		c.setCellValue(column.getText());
		c.setCellStyle(style);
	}

	private void addHeader() {
		List<?> columns = filterVisibleColumns();
		for (int i = 0; i < columns.size(); i++) {
			column = (AppTableColumn) columns.get(i);
			sheet.setColumnWidth(i + colIdx, getWidth());
			addCell(headerStyle(), headerRow(), i + colIdx);
			getters.add("get" + capitalize(column.getId()));
		}
	}

	private Row headerRow() {
		Row r = getRow(1);
		if (column.isHeaderVertical())
			r.setHeightInPoints(96);
		return r;
	}

	private CellStyle headerStyle() {
		if (column.isHeaderVertical())
			return s.verticalHeader();
		return s.header();
	}

	private List<?> filterVisibleColumns() {
		return table.getColumns().stream()//
				.filter(c -> ((AppTableColumn) c).isVisible())//
				.collect(Collectors.toList());
	}

	private void addSummationIfRequired() {
		List<BigDecimal> totals = table.getColumnTotals();
		for (int i = totals.size() - 1; i >= 0; i--)
			setValue(totals.get(i), addSumSuffixToGetterMethodName(getters, i), createSumCell(i));
	}

	private String addSumSuffixToGetterMethodName(ArrayList<String> getters, int i) {
		return getters.get(i + table.getColumnIndexOfFirstTotal()) + "Sum";
	}

	private void addTitle() {
		sheet.addMergedRegion(new CellRangeAddress(0, 0, colIdx, colIdx + table.getColumnCount() - 1));
		Row row = getRow(0);
		row.setHeightInPoints(30);
		setTitleCell(row);
	}

	private void create() {
		for (int i = 0; i < sheetnames.length; i++) {
			createSheet(sheetnames[i]);
			colIdx = 0;
			for (AppTable<?> table : tables.get(i)) {
				this.table = table;
				getters = new ArrayList<>();
				addTitle();
				addHeader();
				populateRows();
				colIdx += table.getColumnCount();
				sheet.setColumnWidth(colIdx++, 750);
			}
		}
	}

	private void createSheet(String id) {
		sheet = wb.createSheet(id);
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

	private Cell createSumCell(int i) {
		return getLastRow().createCell(i + colIdx + table.getColumnIndexOfFirstTotal());
	}

	private Row getLastRow() {
		return getRow(table.getLastRowIndex() + 2);
	}

	private Row getRow(int rowIdx) {
		Row row = sheet.getRow(rowIdx);
		if (row == null)
			row = sheet.createRow(rowIdx);
		return row;
	}

	private Object getValue(Object item, String method) {
		return invokeMethod(item, method);
	}

	private int getWidth() {
		return (int) (column.getWidth() / 10 + 2) * 256;
	}

	private void populateColumns(Object item, Row row) {
		for (int i = 0; i < getters.size(); i++)
			setValue(getValue(item, getters.get(i)), getters.get(i), row.createCell(i + colIdx));
	}

	private void populateRows() {
		List<?> items = table.getItems();
		for (int i = 0; i < items.size(); i++)
			populateColumns(items.get(i), getRow(i + 2));
		addSummationIfRequired();
	}

	private void setBoolean(Cell c, boolean b) {
		c.setCellValue(toBoolSign(b));
		c.setCellStyle(s.center());
	}

	private void setCenter(Cell c, String t) {
		c.setCellValue(t);
		c.setCellStyle(s.center());
	}

	private void setCurrency(Cell c, BigDecimal d) {
		c.setCellValue(d.doubleValue());
		c.setCellStyle(s.currency());
	}

	private void setCurrencySum(Cell c, BigDecimal d) {
		c.setCellValue(d.doubleValue());
		c.setCellStyle(s.currencySum());
	}

	private void setDate(Cell c, LocalDate d) {
		c.setCellValue(toUtilDate(d));
		c.setCellStyle(s.date());
	}

	private void setDecimal(Cell c, BigDecimal d) {
		c.setCellValue(d.doubleValue());
		c.setCellStyle(s.decimal());
	}

	private void setFraction(Cell c, Fraction f) {
		int denominator = f.getDenominator();
		c.setCellValue(f.doubleValue());
		c.setCellStyle(s.fraction(denominator));
	}

	private void setId(Cell c, long l) {
		c.setCellValue(l);
		c.setCellStyle(s.id());
	}

	private void setInteger(Cell c, int i) {
		c.setCellValue(i);
		c.setCellStyle(s.integer());
	}

	private void setQty(Cell c, BigDecimal d) {
		c.setCellValue(d.doubleValue());
		c.setCellStyle(s.integer());
	}

	private void setQtySum(Cell c, BigDecimal d) {
		c.setCellValue(d.doubleValue());
		c.setCellStyle(s.decimalSum());
	}

	private void setRight(Cell c, String t) {
		c.setCellValue(t);
		c.setCellStyle(t.contains(">") ? s.red() : s.right());
	}

	private void setText(Cell c, String t) {
		c.setCellValue(t);
		c.setCellStyle(t.contains(">") ? s.red() : s.left());
	}

	private void setTitleCell(Row row) {
		Cell c = row.createCell(colIdx);
		c.setCellValue(table.getId());
		c.setCellStyle(s.title());
	}

	private void setup() {
		wb = new HSSFWorkbook();
		s.add(wb);
		create();
	}

	private void setValue(Object o, String s, Cell c) {
		if (o == null)
			return;
		if (endsWith(s, "Id"))
			setId(c, (long) o);
		else if (endsWith(s, "Count"))
			setInteger(c, (int) o);
		else if (endsWith(s, "Vol"))
			setDecimal(c, (BigDecimal) o);
		else if (endsWith(s, "ValueSum"))
			setCurrencySum(c, (BigDecimal) o);
		else if (endsWith(s, "QtySum"))
			setQtySum(c, (BigDecimal) o);
		else if (endsWith(s, "Value"))
			setCurrency(c, (BigDecimal) o);
		else if (endsWith(s, "Qty"))
			setQty(c, (BigDecimal) o);
		else if (endsWith(s, "Date"))
			setDate(c, (LocalDate) o);
		else if (endsWith(s, "Type"))
			setCenter(c, o.toString());
		else if (endsWith(s, "InFractions"))
			setFraction(c, (Fraction) o);
		else if (endsWith(s, "Level"))
			setRight(c, o.toString());
		else if (endsWith(s, "Valid") || endsWith(s, "Paid"))
			setBoolean(c, (boolean) o);
		else
			setText(c, o.toString());
	}
}