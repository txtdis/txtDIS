package ph.txtdis.salesforce;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.SalesforceSalesInfo;
import ph.txtdis.dto.SalesforceSalesInfoProduct;

import java.time.ZonedDateTime;
import java.util.List;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.linkText;
import static org.openqa.selenium.Keys.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@Component("salesInfoUploader")
public class SalesInfoUploader
	extends AccountUploader {

	private List<SalesforceSalesInfo> invoices;

	public static void main(String[] args) {
		/*
		 * SalesforceAccount c = new SalesforceAccount(); c.setDistributor(
		 * "MAGNUM GROWTH DISTRIBUTION"); c.setAccountName("TEST");
		 * c.setAccountNumber("123"); c.setChannel("SARI - SARI STORE");
		 * c.setProvince("BULACAN"); c.setCity("SANTA MARIA");
		 * c.setBarangay("BAGBAGUIN"); c.setArea("Greater Manila Area");
		 * c.setCluster("NORTH GMA"); c.setPaymentTerms(0);
		 * c.setTaxClassification("Vatable"); c.setVisitFrequency("F2");
		 * List<SalesforceAccount> a = Arrays.asList(c);
		 * 
		 * SalesforceSalesInfoProduct product1 = new SalesforceSalesInfoProduct();
		 * product1.setSku("5011200230029"); product1.setUom("KG");
		 * product1.setQuantity("1"); product1.setPercentDiscount("5");
		 * 
		 * SalesforceSalesInfoProduct product2 = new SalesforceSalesInfoProduct();
		 * product2.setSku("5011200208450"); product2.setUom("KG");
		 * product2.setQuantity("2"); product2.setPercentDiscount("3");
		 * 
		 * SalesforceSalesInfo invoice = new SalesforceSalesInfo();
		 * invoice.setAccount("MHENG FROZEN PRODUCT");
		 * invoice.setActualDate("2/14/2016");
		 * invoice.setInvoiceNumber("1234567890"); invoice.setDsp("HOMER");
		 * invoice.setProducts(asList(product1, product2));
		 * List<SalesforceSalesInfo> i = asList(invoice);
		 * 
		 * for (SalesforceEntity u : new
		 * SalesInfoUploader().invoices(i).accounts(a).start()) { String s =
		 * "Invoice"; if (u instanceof SalesforceAccount) s = "Customer";
		 * System.out.println(s + " = " + u); }
		 */
	}

	public SalesInfoUploader invoices(List<SalesforceSalesInfo> invoices) {
		this.invoices = invoices;
		return this;
	}

	@Override
	protected void inputData() {
		super.inputData();
		driver.findElement(linkText("Sales Information")).click();
		for (SalesforceSalesInfo invoice : invoices)
			inputInvoiceData(invoice);
	}

	private void inputInvoiceData(SalesforceSalesInfo invoice) {
		inputDetails(invoice);
		inputProducts(invoice);
		clickProceedButton();
		clickSaveAndNewButton();
		invoice.setUploadedOn(ZonedDateTime.now());
		uploads.add(invoice);
	}

	private void inputDetails(SalesforceSalesInfo invoice) {
		inputAccount(invoice.getAccount());
		inputActualDate(invoice.getActualDate());
		inputInvoiceNumber(invoice.getInvoiceNumber());
		inputDSP(invoice.getDsp());
	}

	private void inputProducts(SalesforceSalesInfo invoice) {
		for (SalesforceSalesInfoProduct product : invoice.getProducts()) {
			searchSKU(product.getSku());
			selectLineItemCheckBox();
			selectUOM(product.getUom());
			inputQuantity(product.getQuantity());
			inputPercentDiscount(product.getPercentDiscount());
		}
	}

	private void clickProceedButton() {
		By b = id("mainPage:mainForm:mainPageBlock:j_id5:j_id6");
		driver.findElement(b).click();
	}

	private void clickSaveAndNewButton() {
		By b = id("mainPage:mainForm:SelectedTablePageBlock:PageblockButtons:j_id69");
		driver.findElement(b).click();
	}

	private void inputAccount(String s) {
		By b = id("mainPage:mainForm:mainPageBlock:j_id7:j_id8");
		driver.findElement(b).sendKeys(s, Keys.TAB);
	}

	private void inputActualDate(String s) {
		By b = By.xpath(".//*[@id='mainPage:mainForm:mainPageBlock:j_id7:j_id10']");
		wait.until(elementToBeClickable(b));
		driver.findElement(b).sendKeys(Keys.chord(Keys.CONTROL, "a"), s);
	}

	private void inputInvoiceNumber(String s) {
		By b = By.xpath(".//*[@id='mainPage:mainForm:mainPageBlock:j_id7:j_id12']");
		wait.until(elementToBeClickable(b));
		driver.findElement(b).sendKeys(s);
	}

	private void inputDSP(String s) {
		findByButtonXPath(s, ".//*[@id='mainPage:mainForm:mainPageBlock:j_id7:j_id13_lkwgt']/img", //
			".//*[@id='Contact_body']/table/tbody/tr[2]/th/a");
	}

	private void searchSKU(String s) {
		By sku = By.name("mainPage:mainForm:mainPageBlock:j_id14:j_id17");
		driver.findElement(sku).sendKeys(chord(CONTROL, "a"), s, TAB, TAB, TAB, TAB, TAB, SPACE);
	}

	private void selectLineItemCheckBox() {
		By b = By.xpath(".//*[@id='mainPage:mainForm:mainPageBlock:productListTable:salesTable:0:inputId']");
		wait.until(elementToBeClickable(b));
		driver.findElement(b).click();
	}

	private void selectUOM(String s) {
		By b = id("mainPage:mainForm:mainPageBlock:productListTable:salesTable:0:uom");
		new Select(driver.findElement(b)).selectByVisibleText(s);
	}

	private void inputQuantity(String s) {
		By orderedQty = id("mainPage:mainForm:mainPageBlock:productListTable:salesTable:0:oqty");
		driver.findElement(orderedQty).sendKeys(s);
		By actualQty = id("mainPage:mainForm:mainPageBlock:productListTable:salesTable:0:qty");
		driver.findElement(actualQty).sendKeys(s);
	}

	private void inputPercentDiscount(String s) {
		By b = id("mainPage:mainForm:mainPageBlock:productListTable:salesTable:0:dis");
		driver.findElement(b).sendKeys(s);
	}
}
