package ph.txtdis.salesforce;

import java.time.ZonedDateTime;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.SalesforceAccount;

@Component("accountUploader")
public class AccountUploader extends SalesforceUploader {

	private static final String RESULT_PATH = ".//*[@id='new']/div/div[3]/div/div[2]/table/tbody/tr[2]/th/a";

	private List<? extends SalesforceAccount> customers;

	public AccountUploader accounts(List<SalesforceAccount> c) {
		customers = c;
		return this;
	}

	@Override
	protected void inputData() {
		clickAccountTab();
		clickNewButton();
		for (SalesforceAccount c : customers)
			inputCustomerData(c);
	}

	private String toSalesforcePaymentTerm(int t) {
		if (t == 0)
			return "COD";
		else if (t == 1)
			return "01D";
		else if (t == 2)
			return "02D";
		else if (t <= 5)
			return "05D";
		else if (t <= 7)
			return "07D";
		else if (t <= 15)
			return "15D";
		else if (t <= 20)
			return "20D";
		else if (t <= 25)
			return "25D";
		else if (t <= 30)
			return "30D";
		else if (t <= 35)
			return "35D";
		else if (t <= 40)
			return "40D";
		else if (t <= 45)
			return "45D";
		else if (t <= 60)
			return "60D";
		else if (t <= 90)
			return "90D";
		else
			return "120D";
	}

	private String toSalesforceVisitFrequency(String f) {
		if (f.equals("F1"))
			return "F1 - Once a Month";
		else if (f.equals("F2"))
			return "F2 - Twice a Month";
		else if (f.equals("F4"))
			return "F4 - Weekly";
		else if (f.equals("F8"))
			return "F8 - Twice a Week";
		else if (f.equals("F12"))
			return "F12 - 3 times a week";
		else if (f.equals("F16"))
			return "F16 - 4 times a week";
		else if (f.equals("F20"))
			return "F20 - 5 times a week";
		else if (f.equals("F24"))
			return "F24 - 6 times a week";
		else if (f.equals("DAILY"))
			return "Daily - Daily";
		return "";
	}

	private void inputCustomerData(SalesforceAccount c) {
		inputDistributor(c.getDistributor());
		inputAccountName(c.getAccountName());
		inputAccountNumber(c.getAccountNumber());
		selectChannel(c.getChannel());
		inputProvince(c.getProvince());
		inputCity(c.getCity());
		inputBarangay(c.getBarangay());
		selectArea(c.getArea());
		selectCluster(c.getCluster());
		selectPaymentTerms(c.getPaymentTerms());
		selectTaxClassification(c.getTaxClassification());
		selectVisitFrequency(c.getVisitFrequency());
		// clickSaveAndNew();
		c.setUploadedOn(ZonedDateTime.now());
		uploads.add(c);
	}

	private void clickAccountTab() {
		driver.findElement(By.linkText("Accounts")).click();
	}

	private void clickNewButton() {
		driver.findElement(By.name("new")).click();
	}

	private void inputDistributor(String s) {
		driver.findElement(By.id("CF00N28000009RMLc")).sendKeys(s);
	}

	private void inputAccountName(String s) {
		driver.findElement(By.id("acc2")).sendKeys(s);
	}

	private void inputAccountNumber(String s) {
		driver.findElement(By.id("acc5")).sendKeys(s);
	}

	private void selectChannel(String s) {
		new Select(driver.findElement(By.id("00N28000009RMLP"))).selectByVisibleText(s);
	}

	private void inputProvince(String s) {
		findByButtonXPath(s, ".//*[@id='CF00N28000009RMLr_lkwgt']/img", RESULT_PATH);
	}

	private void inputCity(String s) {
		findByButtonXPath(s, ".//*[@id='CF00N28000009RMLQ_lkwgt']/img", RESULT_PATH);
	}

	private void inputBarangay(String s) {
		findByButtonXPath(s, ".//*[@id='CF00N28000009RMLM_lkwgt']/img", RESULT_PATH);
	}

	private void selectArea(String s) {
		new Select(driver.findElement(By.id("00N28000009RMLK"))).selectByVisibleText(s);
	}

	private void selectCluster(String s) {
		new Select(driver.findElement(By.id("00N28000009RMLR"))).selectByVisibleText(s);
	}

	private void selectPaymentTerms(int i) {
		String s = toSalesforcePaymentTerm(i);
		new Select(driver.findElement(By.id("00N28000009RMLo"))).selectByVisibleText(s);
	}

	private void selectTaxClassification(String s) {
		new Select(driver.findElement(By.id("00N28000009RMM1"))).selectByVisibleText(s);
	}

	private void selectVisitFrequency(String s) {
		s = toSalesforceVisitFrequency(s);
		new Select(driver.findElement(By.id("00N28000009RMLf"))).selectByVisibleText(s);
	}

	private void clickSaveAndNew() {
		By b = By.xpath(".//*[@id='topButtonRow']/input[2]");
		driver.findElement(b).click();
	}
}
