package pricacheck.base;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

	protected WebDriver driver;
	protected Logger log;

	protected String testSuiteName;
	protected String testName;
	protected String testMethodName;

	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method, ITestContext ctx) {
		this.testName = ctx.getCurrentXmlTest().getName();
		log = LogManager.getLogger(testName);
		BrowserDriverFactory factory = new BrowserDriverFactory(log);
		driver = factory.createChromeDriver();
		driver.manage().window().maximize();
		this.testSuiteName = ctx.getSuite().getName();
		this.testMethodName = method.getName();
	}


	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		log.info("Close driver");
		driver.quit();
	}

}
