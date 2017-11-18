package com.trendyol.test;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.trendyol.reader.ConfigurationReader;

public class ConfigurationReaderTest {

	private ConfigurationReader reader;

	@Before
	public void initialize() throws ClassNotFoundException, SQLException {

		reader = new ConfigurationReader("SERVICE-A",
				"jdbc:mysql://localhost:3306/configuration_reader", 3000);

	}

	@Test
	public void configurationReaderTest() throws Exception {

		// First assume that database is empty.
		String siteName = reader.getValue("SiteName", String.class);
		Assert.assertTrue(siteName == null);

		// Let's try to get value again but database still empty.
		siteName = reader.getValue("SiteName", String.class);
		Assert.assertTrue(siteName == null);

		// Now let's add the configurations to database.
		reader.addConfiguration(1, "SiteName", "String", "trendyol.com", true);
		reader.addConfiguration(2, "IsBasketEnabled", "Boolean", "1", true);
		reader.addConfiguration(3, "MaxItemCount", "Int", "50", false);

		// Check to see if the reader read the values.
		siteName = reader.getValue("SiteName", String.class);
		Assert.assertEquals("trendyol.com", siteName);

	}

}
