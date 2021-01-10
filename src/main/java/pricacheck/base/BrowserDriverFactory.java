package pricacheck.base;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BrowserDriverFactory {

	private ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private Logger log;

	public BrowserDriverFactory(Logger log) {
		this.log = log;
	}


	public WebDriver createChromeDriver() {
		// Create driver
		log.info("Create driver");
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		driver.set(new ChromeDriver());
		return driver.get();
	}
}
