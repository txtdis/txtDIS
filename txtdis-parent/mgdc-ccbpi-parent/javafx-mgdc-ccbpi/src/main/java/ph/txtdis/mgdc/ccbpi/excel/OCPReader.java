package ph.txtdis.mgdc.ccbpi.excel;

import static org.apache.poi.openxml4j.opc.OPCPackage.open;
import static org.apache.poi.openxml4j.opc.PackageAccess.READ;

import java.io.File;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.exception.InvalidWorkBookFormatException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.ccbpi.service.DeliveryListService;

@Scope("prototype")
@Component("ocpReader")
public class OCPReader {

	@Autowired
	private DeliveryListService service;

	public void importOCP(File ocp) throws Information, Exception {
		service.initializeMap();
		extractDeliveryListAfterWorkbookValidation(ocp);
		service.saveExtractedDDLs();
	}

	private void extractDeliveryListAfterWorkbookValidation(File ocp) throws Exception {
		OPCPackage pkg = open(ocp, READ);
		extractDeliveryListAfterWorkbookValidation(pkg);
		pkg.revert();
	}

	private void extractDeliveryListAfterWorkbookValidation(OPCPackage pkg) throws Exception {
		Workbook wb = new XSSFWorkbook(pkg);
		extractDeliveryListAfterWorkbookValidation(wb);
		wb.close();
	}

	private void extractDeliveryListAfterWorkbookValidation(Workbook wb) throws Exception {
		for (Row row : wb.getSheetAt(0)) {
			service.setItem(null);
			for (Cell cell : row)
				extractDeliveryListAfterWorkbookValidation(cell);
		}
	}

	private void extractDeliveryListAfterWorkbookValidation(Cell cell) throws Exception {
		validateWorkbook(cell);
		setDDLDetailsForEachRoute(cell);
	}

	private void validateWorkbook(Cell cell) throws Exception {
		verifyCellB1ContainsClientName(cell);
		verifyThatRow4ContainsRoutesForMappingColumnsToDDLs(cell);
	}

	private void setDDLDetailsForEachRoute(Cell cell) throws Exception {
		setItemUponValidation(cell);
		setRouteItemQtyUponValidation(cell);
	}

	private void verifyCellB1ContainsClientName(Cell cell) throws Exception {
		if (isAddress(cell, "B1"))
			service.validateNameIsOfClient(text(cell, "the company's name"));
	}

	private void verifyThatRow4ContainsRoutesForMappingColumnsToDDLs(Cell cell) throws Exception {
		if (cell.getRowIndex() == 3 && cell.getColumnIndex() > 0)
			service.mapIndexToDDL(cell.getColumnIndex(), text(cell, "a route name"));
		if (isAddress(cell, "A5"))
			service.validateNamesAreOfRoutes();
	}

	private void setItemUponValidation(Cell cell) throws Exception {
		if (cell.getColumnIndex() == 0 && cell.getCellTypeEnum() == CellType.NUMERIC)
			service.setItemUponValidation((int) cell.getNumericCellValue());
	}

	private void setRouteItemQtyUponValidation(Cell cell) throws Exception {
		if (cell.getColumnIndex() > 0 && cell.getCellTypeEnum() == CellType.NUMERIC)
			service.setRouteItemQtyUponValidation(cell.getColumnIndex(), cell.getNumericCellValue());
	}

	private boolean isAddress(Cell cell, String address) {
		return cell.getAddress().toString().equalsIgnoreCase(address);
	}

	private String text(Cell cell, String required) throws Exception {
		try {
			return cell.getStringCellValue();
		} catch (Exception e) {
			throw new InvalidWorkBookFormatException(cell.getAddress().toString(), required);
		}
	}
}