/*
 * Copyright 2017 Emmanuel Keller / QWAZR
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qwazr.search.bench.test;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TestResults {

	final Map<String, SummaryStatistics> statsMap;

	public TestResults() {
		this.statsMap = new LinkedHashMap<>();
	}

	public void add(Object testClass, Integer rate) {
		statsMap.computeIfAbsent(testClass.getClass().getName(), (key) -> new SummaryStatistics()).addValue(rate);
	}

	public void log(Logger logger) {
		statsMap.forEach((clazz, stats) -> logger.info(
				() -> clazz + "- mean: " + (int) stats.getMean() + " - dev: " + (int) stats.getStandardDeviation()));
	}

}
