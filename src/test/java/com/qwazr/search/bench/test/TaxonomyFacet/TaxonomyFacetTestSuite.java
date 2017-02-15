package com.qwazr.search.bench.test.TaxonomyFacet;

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
@Suite.SuiteClasses({ TaxonomyFacetTestSuite.LuceneNoExecutorWarmup.class,
		TaxonomyFacetTestSuite.LuceneNoExecutor.class,
		TaxonomyFacetTestSuite.LuceneWithExecutorWarmup.class,
		TaxonomyFacetTestSuite.LuceneWithExecutor.class,
		TaxonomyFacetTestSuite.QwazrWarmup.class,
		TaxonomyFacetTestSuite.Qwazr.class })
public class TaxonomyFacetTestSuite extends CommonTestSuite {

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneNoExecutorWarmup extends TaxonomyFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			TaxonomyFacetLuceneTest.before(TestSettings.of(currentResults).warmup(true).executor(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneNoExecutor extends TaxonomyFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			TaxonomyFacetLuceneTest.before(TestSettings.of(currentResults).warmup(false).executor(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneWithExecutorWarmup extends TaxonomyFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			TaxonomyFacetLuceneTest.before(TestSettings.of(currentResults).warmup(true).executor(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class LuceneWithExecutor extends TaxonomyFacetLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			TaxonomyFacetLuceneTest.before(TestSettings.of(currentResults).warmup(false).executor(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class QwazrWarmup extends TaxonomyFacetQwazrTest {

		@BeforeClass
		public static void before() throws Exception {
			TaxonomyFacetQwazrTest.before(TestSettings.of(currentResults).warmup(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class Qwazr extends TaxonomyFacetQwazrTest {

		@BeforeClass
		public static void before() throws Exception {
			TaxonomyFacetQwazrTest.before(TestSettings.of(currentResults).warmup(false));
		}
	}
}
