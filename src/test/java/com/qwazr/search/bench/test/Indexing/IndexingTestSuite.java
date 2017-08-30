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
package com.qwazr.search.bench.test.Indexing;

import com.qwazr.search.bench.test.CommonTestSuite;
import com.qwazr.search.bench.test.TestSettings;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by ekeller on 15/02/2017.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ IndexingTestSuite.LuceneNoExecutorLowRamBuffer.class,
		IndexingTestSuite.LuceneNoExecutorHighRamBuffer.class,
		IndexingTestSuite.LuceneWithExecutorLowRamBuffer.class,
		IndexingTestSuite.LuceneWithExecutorHighRamBuffer.class,
		IndexingTestSuite.QwazrLowRamBuffer.class,
		IndexingTestSuite.QwazrHighRamBuffer.class })
public class IndexingTestSuite extends CommonTestSuite {

	@Parameterized.Parameters
	public static Collection<Boolean> iterations() {
		return Arrays.asList(true, false);
	}

	static public class LuceneNoExecutorLowRamBuffer extends IndexingLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).executor(false));
		}
	}

	static public class LuceneNoExecutorHighRamBuffer extends IndexingLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).executor(false).highRamBuffer(true));
		}
	}

	static public class LuceneWithExecutorLowRamBuffer extends IndexingLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).executor(true));
		}
	}

	static public class LuceneWithExecutorHighRamBuffer extends IndexingLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).executor(true).highRamBuffer(true));
		}
	}

	static public class QwazrLowRamBuffer extends IndexingQwazrTest.DefaultRamBuffer {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults));
		}
	}

	static public class QwazrHighRamBuffer extends IndexingQwazrTest.HighRamBuffer {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults));
		}
	}
}
