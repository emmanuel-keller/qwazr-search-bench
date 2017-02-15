package com.qwazr.search.bench.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ekeller on 15/02/2017.
 */
public abstract class CommonTestSuite {

	public final static Logger LOGGER = LoggerFactory.getLogger(CommonTestSuite.class);

	public static TestResults currentResults;

	@BeforeClass
	public static void before() {
		currentResults = new TestResults();
	}

	@AfterClass
	public static void after() {
		currentResults.log(LOGGER);
	}
}
