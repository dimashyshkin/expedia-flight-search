package pricacheck.expedia.pages;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ExpediaResults extends BasePageObject {

	private By price = By.xpath(".//div[@data-test-id='price-column']//span[2]"); // 12242020
	private By journeyDuration = By.xpath(".//div[@data-test-id='journey-duration']"); // 12242020
	private By airline = By.xpath(".//div[@data-test-id='flight-operated']"); // 12242020
	private By layoverInfo = By.xpath(".//div[@data-test-id='layovers']"); // 12242020
	private By progressBar = By.xpath(".//div[@class='uitk-loading-bar-current']"); // 12242020
	private By oneStopCheckbox = By.xpath(".//input[@id='stops-1']"); // 12242020
	private By nonStopCheckbox = By.xpath(".//input[@id='stops-0']"); // 12242020

	private By itinerary = By.xpath("//li[@data-test-id='offer-listing']");

	public ExpediaResults(WebDriver driver, Logger log) {
		super(driver, log);
	}


	public void open(String url) {
		openUrl(url);
		loadFlights();
	}


	private void loadFlights() {
		waitForVisibilityOf(price, 60);
		waitForInvisibilityOf(progressBar, 30);
	}


	public double getPrice(WebElement element) {
		try {
			return Double.parseDouble(element.findElement(price).getText().replaceAll("[^0-9]+", ""));
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return 100000;
	}


	public String getStops(WebElement element) {
		try {
			waitForVisibilityOf(journeyDuration, 30);
			String journeyDurationText = element.findElement(journeyDuration).getText(); // 7h 25m (1 stop) | 4h 9m (Nonstop)
			String numberOfStops = journeyDurationText.substring(journeyDurationText.indexOf('(') + 1, journeyDurationText.indexOf(')'));
			return numberOfStops;
		} catch (Exception e) {
		}
		return "";
	}


	private String getAirline(WebElement itinerary) {
		try {
			return itinerary.findElement(airline).getText();
		} catch (Exception e) {
		}
		return "";
	}


	public String getDuration(WebElement element) {
		try {
			String journeyDurationText = element.findElement(journeyDuration).getText(); // 7h 25m (1 stop) | 4h 9m (Nonstop)
			String d = journeyDurationText.substring(0, journeyDurationText.indexOf('('));
			// Add zeros if less then 10 (5h30m > 05h 30m)
			int h = Integer.parseInt(d.substring(0, d.indexOf("h")));
			int m = Integer.parseInt(d.substring(d.indexOf("h") + 2, d.indexOf("m")));
			String newH = (h < 10) ? "0" + h : "" + h;
			String newM = (m < 10) ? "0" + m : "" + m;
			return newH + "h " + newM + "m";
		} catch (Exception e) {
			log.debug(e);
		}
		return "";
	}


	public String getLayovers(WebElement element) {
		try {
			return element.findElement(layoverInfo).getText();
		} catch (Exception e) {
		}
		return "";
	}


	/** return false if one stop is not awailable */
	public boolean filter1Stop() {
		try {
			waitForVisibilityOf(oneStopCheckbox, 2);
			click(oneStopCheckbox);
			loadFlights();
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	public void printInfoFromItins(int num, String url, String departureDate, String returnDate, double maxPrice) {
		List<WebElement> itineraries = driver.findElements(itinerary);
		double previousPrice = 0;
		int printed = 0;
		for (WebElement itinerary : itineraries) {
			double price = getPrice(itinerary);

			if (price != previousPrice && price <= maxPrice) {
				previousPrice = price;
				String duration = getDuration(itinerary);
				String stops = getStops(itinerary);
				String airline = getAirline(itinerary);
				String returnDatePrint = "";
				String layovers = getLayovers(itinerary);
				if (!returnDate.isEmpty()) {
					returnDatePrint = "-" + returnDate;
				}
				log.info("| " + departureDate + returnDatePrint + " | " + price + " | " + stops + " | " + duration + " | " + airline + " | "
						+ layovers + " | " + url);
				printed++;
			}
			if (printed == num) {
				return;
			}
		}
	}


	public boolean filterNonStop() {
		try {
			waitForVisibilityOf(nonStopCheckbox, 2);
			click(nonStopCheckbox);
			loadFlights();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
