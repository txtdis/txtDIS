package ph.txtdis.dyvek.excel;

import static java.awt.Desktop.getDesktop;
import static java.io.File.createTempFile;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static org.apache.poi.ss.usermodel.IndexedColors.GREY_50_PERCENT;
import static org.apache.poi.ss.usermodel.Sheet.BottomMargin;
import static org.apache.poi.ss.usermodel.Sheet.FooterMargin;
import static org.apache.poi.ss.usermodel.Sheet.HeaderMargin;
import static org.apache.poi.ss.usermodel.Sheet.LeftMargin;
import static org.apache.poi.ss.usermodel.Sheet.RightMargin;
import static org.apache.poi.ss.usermodel.Sheet.TopMargin;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;
import static ph.txtdis.util.DateTimeUtils.toUtilDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.service.ClientBillingService;
import ph.txtdis.excel.ExcelBillWriter;

@Scope("prototype")
@Component("excelBillWriter")
public class ExcelBillWriterImpl //
		implements ExcelBillWriter {

	private static final String CALIBRI = "Calibri";

	@Autowired
	private ClientBillingService service;

	private HSSFWorkbook workbook;

	private HSSFDataFormat format;

	private HSSFSheet sheet;

	private int rowNo;

	@Override
	public void write() throws IOException {
		rowNo = 0;
		workbook = new HSSFWorkbook();
		format = workbook.getCreationHelper().createDataFormat();
		sheet = workbook.createSheet("Bill No. " + service.getBillNo());

		sheet.setMargin(TopMargin, 2);
		sheet.setMargin(BottomMargin, 1);
		sheet.setMargin(LeftMargin, 0.5);
		sheet.setMargin(RightMargin, 0.5);
		sheet.setMargin(HeaderMargin, 0.25);
		sheet.setMargin(FooterMargin, 0.25);
		sheet.setFitToPage(true);
		sheet.protectSheet("secretPassword");

		for (int i = 0; i < 5; i++)
			sheet.setColumnWidth(i, 4864);
		for (int i = 0; i < 4; i++)
			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 4));

		Row row = sheet.createRow(rowNo++);
		row.setHeightInPoints(72);
		Cell cell = row.createCell(0);
		cell.setCellStyle(logoStyle());
		cell.setCellValue(service.getCompanyName());

		row = sheet.createRow(rowNo++);
		cell = row.createCell(0);
		cell.setCellStyle(fullCenterStyle());
		cell.setCellValue(service.getCompanyAddress());

		row = sheet.createRow(rowNo++);
		cell = row.createCell(0);
		cell.setCellStyle(fullCenterStyle());
		cell.setCellValue("Billing Statement");

		row = sheet.createRow(rowNo++);
		cell = row.createCell(0);
		cell.setCellStyle(fullCenterDateStyle());
		cell.setCellValue(toUtilDate(service.getBillDate()));

		rowNo++;
		row = sheet.createRow(rowNo++);
		cell = row.createCell(4);
		cell.setCellStyle(boldCenter());
		cell.setCellValue(service.getPaymentType());

		row = sheet.createRow(rowNo++);
		cell = row.createCell(0);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue("To:");

		cell = row.createCell(1);
		cell.setCellStyle(leftStyle());
		cell.setCellValue(service.getBillTo());

		cell = row.createCell(3);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue("Bill No:");

		cell = row.createCell(4);
		cell.setCellStyle(leftStyle());
		cell.setCellValue(service.getBillNo());

		row = sheet.createRow(rowNo++);
		cell = row.createCell(0);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue(service.getNotePrompt());

		cell = row.createCell(1);
		cell.setCellStyle(leftStyle());
		cell.setCellValue(service.getNote());

		cell = row.createCell(3);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue(service.getReferencePrompt());

		cell = row.createCell(4);
		cell.setCellStyle(leftStyle());
		cell.setCellValue(service.getReferenceNo());

		rowNo++;
		List<String> s = service.getTableColumnHeaders();
		row = sheet.createRow(rowNo++);
		for (int i = 0; i < s.size(); i++) {
			cell = row.createCell(i);
			cell.setCellStyle(headerStyle());
			cell.setCellValue(s.get(i));
		}

		List<BillableDetail> d = service.getDetails();
		for (int i = 0; i < d.size(); i++) {
			rowNo++;
			row = sheet.createRow(rowNo++);
			cell = row.createCell(0);
			cell.setCellStyle(dateStyle());
			cell.setCellValue(toUtilDate(d.get(i).getOrderDate()));

			cell = row.createCell(1);
			cell.setCellStyle(leftStyle());
			cell.setCellValue(d.get(i).getOrderNo());

			cell = row.createCell(2);
			cell.setCellStyle(integerStyle());
			cell.setCellValue(d.get(i).getQty().doubleValue());

			cell = row.createCell(3);
			cell.setCellStyle(currencyStyle());
			cell.setCellValue(d.get(i).getPriceValue().doubleValue());

			cell = row.createCell(4);
			cell.setCellStyle(currencyStyle());
			cell.setCellValue(d.get(i).getValue().doubleValue());
		}

		rowNo += 2;
		row = sheet.createRow(rowNo++);
		cell = row.createCell(3);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue("Grand Total");

		cell = row.createCell(4);
		cell.setCellStyle(currencySumStyle());
		cell.setCellValue(service.getTotalValue().doubleValue());

		rowNo += 2;
		row = sheet.createRow(rowNo++);
		cell = row.createCell(0);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue("Prepared by:");

		cell = row.createCell(3);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue("Reviewed by:");

		row = sheet.createRow(rowNo++);
		cell = row.createCell(1);
		cell.setCellStyle(signatureStyle());
		cell.setCellValue(service.getPreparedBy());

		cell = row.createCell(4);
		cell.setCellStyle(signatureStyle());
		cell.setCellValue(service.getReviewedBy());

		rowNo += 2;
		row = sheet.createRow(rowNo++);
		cell = row.createCell(0);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue("Checked by:");

		row = sheet.createRow(rowNo++);
		cell = row.createCell(1);
		cell.setCellStyle(signatureStyle());
		cell.setCellValue(service.getCheckedBy());

		cell = row.createCell(3);
		cell.setCellStyle(boldLeftStyle());
		cell.setCellValue("Received by:");

		rowNo += 3;
		sheet.addMergedRegion(new CellRangeAddress(rowNo, rowNo, 3, 4));
		row = sheet.createRow(rowNo);
		cell = row.createCell(3);
		cell.setCellStyle(signatureStyle());
		cell.setCellValue("Signature Over Printed Name / Date");
		cell = row.createCell(4);
		cell.setCellStyle(signatureStyle());

		File f = createTempFile(capitalizeFully( //
				service.getCustomer()).replace(" ", ".").replace(",", "") + //
				toHypenatedYearMonthDay(service.getBillDate()) + ".version.", ".xls");
		FileOutputStream os = new FileOutputStream(f);
		workbook.write(os);
		os.close();
		workbook.close();
		getDesktop().open(f);
		f.deleteOnExit();
	}

	private CellStyle boldCenter() {
		CellStyle s = horizCenter();
		s.setFont(boldFont());
		return s;
	}

	private CellStyle currencySumStyle() {
		CellStyle s = currencyStyle();
		s.setBorderTop(BorderStyle.THIN);
		s.setBorderBottom(BorderStyle.DOUBLE);
		return s;
	}

	private CellStyle currencyStyle() {
		CellStyle s = rightStyle();
		s.setDataFormat(format.getFormat("_(₱* #,##0.00_);[Red]_(₱* (#,##0.00);_(₱* \"-\"??_);_(@_)"));
		return s;
	}

	private CellStyle integerStyle() {
		CellStyle s = rightStyle();
		s.setDataFormat(format.getFormat("_(#,##0_);[Red]_((#,##0);_(\"-\"??_);_(@_)"));
		return s;
	}

	private CellStyle dateStyle() {
		CellStyle s = centerStyle();
		s.setDataFormat(format.getFormat("m/d/yyyy;@"));
		return s;
	}

	private CellStyle logoStyle() {
		CellStyle s = fullCenter();
		s.setFont(logoFont());
		return s;
	}

	private CellStyle centerStyle() {
		CellStyle s = horizCenter();
		s.setFont(normalFont());
		return s;
	}

	private CellStyle fullCenter() {
		CellStyle s = horizCenter();
		s.setVerticalAlignment(VerticalAlignment.CENTER);
		return s;
	}

	private CellStyle horizCenter() {
		CellStyle s = workbook.createCellStyle();
		s.setAlignment(HorizontalAlignment.CENTER);
		return s;
	}

	private Font logoFont() {
		Font f = workbook.createFont();
		f.setFontName(service.getLogoFont());
		f.setColor(service.getLogoColor());
		f.setFontHeightInPoints((short) 36);
		return f;
	}

	private CellStyle fullCenterStyle() {
		CellStyle s = fullCenter();
		s.setFont(normalFont());
		return s;
	}

	private CellStyle leftStyle() {
		CellStyle s = left();
		s.setFont(normalFont());
		return s;
	}

	private CellStyle rightStyle() {
		CellStyle s = right();
		s.setFont(normalFont());
		return s;
	}

	private CellStyle right() {
		CellStyle s = workbook.createCellStyle();
		s.setAlignment(HorizontalAlignment.RIGHT);
		return s;
	}

	private CellStyle boldLeftStyle() {
		CellStyle s = left();
		s.setFont(boldFont());
		return s;
	}

	private CellStyle left() {
		CellStyle s = workbook.createCellStyle();
		s.setAlignment(HorizontalAlignment.LEFT);
		return s;
	}

	private Font normalFont() {
		Font f = workbook.createFont();
		f.setFontName(CALIBRI);
		f.setFontHeightInPoints((short) 12);
		return f;
	}

	private Font boldFont() {
		Font f = normalFont();
		f.setBold(true);
		return f;
	}

	private CellStyle fullCenterDateStyle() {
		CellStyle s = fullCenterStyle();
		s.setDataFormat(format.getFormat("mmmm d, yyyy;@"));
		return s;
	}

	private CellStyle headerStyle() {
		CellStyle s = fullCenterStyle();
		s.setFillForegroundColor(GREY_50_PERCENT.getIndex());
		s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		s.setFont(boldFont());
		s.setBorderTop(BorderStyle.THIN);
		s.setBorderRight(BorderStyle.THIN);
		s.setBorderLeft(BorderStyle.THIN);
		s.setBorderBottom(BorderStyle.THIN);
		s.setWrapText(true);
		return s;
	}

	private CellStyle signatureStyle() {
		CellStyle s = centerStyle();
		s.setBorderTop(BorderStyle.THIN);
		return s;
	}
}