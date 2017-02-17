/**
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
