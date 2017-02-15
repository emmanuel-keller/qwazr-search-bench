package com.qwazr.search.bench.test.SortedSetFacet;

import com.qwazr.search.bench.test.CommonTestSuite;
import com.qwazr.search.bench.test.TestSettings;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

/**
 * Created by ekeller on 15/02/2017.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ SortedSetFacetTestSuite.LuceneNoExecutorWarmup.class,
		SortedSetFacetTestSuite.LuceneNoExecutor.class,
		SortedSetFacetTestSuite.LuceneWithExecutorWarmup.class,
		SortedSetFacetTestSuite.LuceneWithExecutor.class,
		SortedSetFacetTestSuite.QwazrWarmup.class,
		SortedSetFacetTestSuite.Qwazr.class })
public class SortedSetFacetTestSuite extends CommonTestSuite {

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneNoExecutorWarmup extends SortedSetFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			SortedSetFacetLuceneTest.before(TestSettings.of(currentResults).warmup(true).executor(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneNoExecutor extends SortedSetFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			SortedSetFacetLuceneTest.before(TestSettings.of(currentResults).warmup(false).executor(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneWithExecutorWarmup extends SortedSetFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			SortedSetFacetLuceneTest.before(TestSettings.of(currentResults).warmup(true).executor(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneWithExecutor extends SortedSetFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			SortedSetFacetLuceneTest.before(TestSettings.of(currentResults).warmup(false).executor(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class QwazrWarmup extends SortedSetFacetQwazrTest {

		@BeforeClass
		public static void before() throws Exception {
			SortedSetFacetQwazrTest.before(TestSettings.of(currentResults).warmup(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class Qwazr extends SortedSetFacetQwazrTest {

		@BeforeClass
		public static void before() throws Exception {
			SortedSetFacetQwazrTest.before(TestSettings.of(currentResults).warmup(false));
		}
	}
}
