package ph.txtdis.salesforce;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ph.txtdis.dto.SalesforceEntity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Paths.get;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.log4j.Logger.getLogger;

public abstract class SalesforceUploader {

	private static Logger logger = getLogger(SalesforceUploader.class);

	protected WebDriver driver;

	protected WebDriverWait wait;

	protected List<SalesforceEntity> uploads = new ArrayList<>();

	public List<? extends SalesforceEntity> start() {
		try {
			setFirefoxDriver();
			loginToSalesForceWebsite();
			inputData();
			saveCookies();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploads;
	}

	private void setFirefoxDriver() {
		driver = new FirefoxDriver();
		addCookies();
		driver.manage().timeouts().implicitlyWait(300, SECONDS);
		setWait();
	}

	private void loginToSalesForceWebsite() {
		driver.get("https://login.salesforce.com");
		driver.findElement(By.id("username")).sendKeys("rosbel.trinidad@gmail.com");
		driver.findElement(By.id("password")).sendKeys("Rosbeltrinidad1!");
		driver.findElement(By.id("Login")).click();
	}

	protected abstract void inputData();

	private void saveCookies() {
		try (BufferedWriter w = newBufferedWriter(path())) {
			for (Cookie c : driver.manage().getCookies())
				writeCookie(w, c);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void addCookies() {
		try (Stream<String> stream = Files.lines(path())) {
			stream.map(l -> l.split("|"))//
				.map(a -> getCookie(a))//
				.forEach(c -> driver.manage().addCookie(c));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void setWait() {
		wait = new WebDriverWait(driver, 300);
		wait.pollingEvery(1, SECONDS);
		wait.ignoring(StaleElementReferenceException.class);
	}

	private Path path() {
		return get(getProperty("user.home") + separator + //
			"AppData" + separator + //
			"Local" + separator + //
			"txtDIS" + separator + //
			"salesforce.cookie");
	}

	private void writeCookie(BufferedWriter w, Cookie c) throws IOException {
		w.write(c.getName() + "|" + //
			c.getValue() + "|" + "\n");
	}

	private Cookie getCookie(String[] s) {
		String name = s[0];
		String value = s[1];
		return new Cookie(name, value);
	}

	protected void findByButtonXPath(String item, String button, String foundItem) {
		driver.findElement(By.xpath(button)).click();
		String parentWindow = driver.getWindowHandle();
		find(item, foundItem);
		driver.switchTo().window(parentWindow);
	}

	private void find(String item, String foundItem) {
		switchToPopup();
		search(item);
		clickFoundItem(foundItem);
	}

	private void switchToPopup() {
		String subWindowHandler = null;
		Iterator<String> iterator = driver.getWindowHandles().iterator();
		while (iterator.hasNext())
			subWindowHandler = iterator.next();
		driver.switchTo().window(subWindowHandler);
	}

	private void search(String item) {
		driver.switchTo().frame("searchFrame");
		driver.findElement(By.id("lksrch")).sendKeys(item);
		driver.findElement(By.name("go")).click();
		driver.switchTo().defaultContent();
	}

	private void clickFoundItem(String path) {
		driver.switchTo().frame("resultsFrame");
		driver.findElement(By.xpath(path)).click();
	}
}
