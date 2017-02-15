package com.qwazr.search.bench.test;

import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ekeller on 15/02/2017.
 */
public class TestResults {

	final Map<String, Integer> rates;

	public TestResults() {
		this.rates = new LinkedHashMap<>();
	}

	public void addResult(Object testClass, Integer rate) {
		rates.put(testClass.getClass().getSimpleName(), rate);
	}

	public void log(Logger logger) {
		rates.forEach((clazz, rate) -> {
			logger.info(clazz + ": " + rate);
		});
	}

}
