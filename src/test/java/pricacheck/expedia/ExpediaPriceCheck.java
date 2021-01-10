package pricacheck.expedia;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.testng.annotations.Test;

import pricacheck.base.CsvDataProviders;
import pricacheck.base.TestUtilities;
import pricacheck.expedia.pages.ExpediaResults;

public class ExpediaPriceCheck extends TestUtilities {

	@Test(priority = 1, dataProvider = "csvReader", dataProviderClass = CsvDataProviders.class)
	public void checkPrice(Map<String, String> testData) {
		String departureCity = testData.get("departure");
		String destinationCity = testData.get("destination");
		log.info("From: " + departureCity + " To: " + destinationCity);

		int daysfromtodayMin = getDaysToDate(testData.get("departureMin"));
		int daysfromtodayMax = getDaysToDate(testData.get("departureMax"));

		// 0 for one way
		int lengthofstayMin = Integer.parseInt(testData.get("lengthofstayMin"));
		int lengthofstayMax = Integer.parseInt(testData.get("lengthofstayMax"));

		double maxPrice = Integer.parseInt(testData.get("maxPrice"));
		boolean filterNonStop = false;
		try {
			filterNonStop = Boolean.parseBoolean(testData.get("nonStop"));
		} catch (NumberFormatException e) {
			//e.printStackTrace(); // if not in the spreadsheet
		}

		for (int i = daysfromtodayMin; i <= daysfromtodayMax; i++) {
			String departureDate = getDepartureDate(i, "MM/dd/yyyy");
			String returnDate = "";
			String url = "";
			if (lengthofstayMin != 0 && lengthofstayMax != 0) {
				for (int j = lengthofstayMin; j <= lengthofstayMax; j++) {
					returnDate = getReturnDate(i, j, "MM/dd/yyyy");
					url = "https://www.expedia.com/Flights-Search?mode=search&trip=roundtrip&leg1=from:" + departureCity + ",to:" + destinationCity
							+ ",departure:" + departureDate + "TANYT&leg2=from:" + destinationCity + ",to:" + departureCity + ",departure:"
							+ returnDate + "TANYT&passengers=children:0,adults:1,seniors:0,infantinlap:Y&flexibleSearch=true";
					printInfo(maxPrice, departureDate, returnDate, url, filterNonStop);
				}
			} else {
				url = "https://www.expedia.com/Flights-Search?mode=search&trip=oneway&leg1=from:" + departureCity + ",to:" + destinationCity
						+ ",departure:" + departureDate + "TANYT&passengers=children:0,adults:1,seniors:0,infantinlap:Y&flexibleSearch=true";
				printInfo(maxPrice, departureDate, returnDate, url, filterNonStop);
			}
		}
	}


	private void printInfo(double maxPrice, String departureDate, String returnDate, String url, boolean filterNonStop) {
		// open main page
		ExpediaResults results = new ExpediaResults(driver, log);
		results.open(url);
		if (filterNonStop) {
			results.filterNonStop();
		}
		results.printInfoFromItins(5, url, departureDate, returnDate, maxPrice);
	}


	/** Get departure date based on daysfromtoday with given format */
	public static String getDepartureDate(int daysFromToday, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, daysFromToday);
		return sdf.format(c.getTime());
	}


	/** Get return date based on daysfromtoday and lengthofstay */
	public static String getReturnDate(int daysFromToday, int lengthOfStay, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, daysFromToday + lengthOfStay);
		return sdf.format(c.getTime());
	}
}
