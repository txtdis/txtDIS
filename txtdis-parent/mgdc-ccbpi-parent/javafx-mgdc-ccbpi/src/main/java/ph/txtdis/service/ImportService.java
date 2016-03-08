package ph.txtdis.service;

import static java.time.LocalDate.now;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billing;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemList;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoItemPriceException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;

@Service("importService")
public class ImportService {

	private static Logger logger = getLogger(ImportService.class);

	private static final int ITEM_NAME = 6;

	private static final int CUSTOMER_NAME = 2;

	@Autowired
	private BillingService billingService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private SavingService<List<Billing>> savingService;

	public void verifyNoImportDone() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException, DuplicateException {
		if (billingService.importExists())
			throw new DuplicateException("At least one imported S/O");
	}

	public void importFile(File f) throws SuccessfulSaveInfo, NoItemPriceException, IOException {
		OPCPackage pkg = null;
		XSSFWorkbook wb = null;
		Billing b = null;
		ItemList bd = null;
		List<Billing> billings = new ArrayList<>();
		List<ItemList> details = new ArrayList<>();
		try {
			pkg = OPCPackage.open(f);
			wb = new XSSFWorkbook(pkg);
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				for (Cell cell : row) {
					if (isCustomerId(cell)) {
						if (b != null) {
							billings.add(b);
							logger.info("Billing list at customer = " + billings);
						}
						details = new ArrayList<>();
						Customer c = getCustomer(row, cell);
						b = new Billing();
						b.setCustomer(c);
						b.setOrderDate(now());
						logger.info("Customer  = " + c);
						logger.info("Billing at customer = " + b);
					} else if (isItemId(cell)) {
						Item i = getItem(row, cell);
						bd = new ItemList();
						bd.setItem(i);
						bd.setPriceValue(i.getPriceValue());
						logger.info("Item  = " + i);
						logger.info("Billing at item = " + b);
						logger.info("Item list at item = " + bd);
					} else if (isItemQty(cell)) {
						int qty = (int) cell.getNumericCellValue();
						bd.setInitialCount(qty * bd.getItem().getBottlePerCase());
						details.add(bd);
						b.setDetails(details);
						logger.info("Qty  = " + qty);
						logger.info("Item list at qty = " + bd);
						logger.info("Billing details at qty = " + details);
						logger.info("Billing at qty = " + b);
					} else if (isAtLastRow(cell)) {
						billings.add(b);
						logger.info("Billing list at last row  = " + billings);
					}
				}
			}
			logger.info("Billing list at end = " + billings);
			savingService.module("billing").save(billings);
			throw new SuccessfulSaveInfo("imported S/O's");
		} catch (NoServerConnectionException | StoppedServerException | FailedAuthenticationException | InvalidException
				| RestException | InvalidFormatException | DuplicateException e) {
			e.printStackTrace();
		} finally {
			if (pkg != null)
				pkg.close();
		}
	}

	private Customer getCustomer(Row row, Cell cell) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, DuplicateException, RestException {
		Long id = (long) cell.getNumericCellValue();
		logger.info("Customer ID from cell = " + id);
		Customer c = customerService.findByCode(id);
		return c != null ? c : saveCustomer(row, id);
	}

	private String getCustomerName(Row row) {
		Cell c = row.getCell(CUSTOMER_NAME);
		return c.getStringCellValue();
	}

	private Item getItem(Row row, Cell cell) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, DuplicateException, RestException, NoItemPriceException {
		Long id = (long) cell.getNumericCellValue();
		logger.info("Item ID from cell = " + id);

		Item i = itemService.findByCode(id);
		logger.info("Item from database = " + i);
		if (i == null)
			return saveItem(row, id);

		BigDecimal price = i.getPriceValue();
		logger.info("Price from item = " + price);
		if (price == null)
			throw new NoItemPriceException(id);

		return i;
	}

	private String getItemName(Row row) {
		Cell c = row.getCell(ITEM_NAME);
		return c.getStringCellValue();
	}

	private boolean isAtLastRow(Cell c) {
		return c.getColumnIndex() == 0 && c.getCellType() == Cell.CELL_TYPE_STRING
				&& c.getStringCellValue().equals("Grand Total");
	}

	private boolean isCustomerId(Cell c) {
		return c.getColumnIndex() == 1 && c.getCellType() == Cell.CELL_TYPE_NUMERIC;
	}

	private boolean isItemId(Cell c) {
		return c.getColumnIndex() == 5 && c.getCellType() == Cell.CELL_TYPE_NUMERIC;
	}

	private boolean isItemQty(Cell c) {
		return c.getColumnIndex() == 8 && c.getCellType() == Cell.CELL_TYPE_NUMERIC;
	}

	private Customer saveCustomer(Row row, Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		String name = getCustomerName(row);
		logger.info("Customer name from cell = " + name);

		return customerService.save(id, name);
	}

	private Item saveItem(Row row, Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NoItemPriceException, RestException {
		String name = getItemName(row);
		logger.info("Item name from cell = " + name);

		Item i = itemService.save(id, name, 0, null);
		logger.info("Item after save = " + i);

		throw new NoItemPriceException(id);
	}
}
