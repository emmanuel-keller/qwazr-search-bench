package com.qwazr.search.bench.test.FullText;

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
@Suite.SuiteClasses({ ShortAbstractTestSuite.LuceneNoExecutorWithTaxonomyWarmup.class,
		ShortAbstractTestSuite.LuceneNoExecutorWithTaxonomy.class,
		ShortAbstractTestSuite.LuceneNoExecutorNoTaxonomyWarmup.class,
		ShortAbstractTestSuite.LuceneNoExecutorNoTaxonomy.class,
		ShortAbstractTestSuite.LuceneWithExecutorWithTaxonomyWarmup.class,
		ShortAbstractTestSuite.LuceneWithExecutorWithTaxonomy.class,
		ShortAbstractTestSuite.LuceneWithExecutorNoTaxonomyWarmup.class,
		ShortAbstractTestSuite.LuceneWithExecutorNoTaxonomy.class,
		ShortAbstractTestSuite.QwazrWithTaxonomyWarmup.class,
		ShortAbstractTestSuite.QwazrWithTaxonomy.class,
		ShortAbstractTestSuite.QwazrNoTaxonomyWarmup.class,
		ShortAbstractTestSuite.QwazrNoTaxonomy.class })
public class ShortAbstractTestSuite extends CommonTestSuite {


	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneNoExecutorWithTaxonomyWarmup extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(true).executor(false).taxonomy(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneNoExecutorWithTaxonomy extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(false).executor(false).taxonomy(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneNoExecutorNoTaxonomyWarmup extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(true).executor(false).taxonomy(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneNoExecutorNoTaxonomy extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(false).executor(false).taxonomy(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneWithExecutorWithTaxonomyWarmup extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(true).executor(true).taxonomy(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneWithExecutorWithTaxonomy extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(false).executor(true).taxonomy(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneWithExecutorNoTaxonomyWarmup extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(true).executor(true).taxonomy(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class LuceneWithExecutorNoTaxonomy extends ShortAbstractLuceneTest {

		@BeforeClass
		public static void before() throws Exception {
			before(TestSettings.of(currentResults).warmup(false).executor(true).taxonomy(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class QwazrNoTaxonomyWarmup extends ShortAbstractQwazrTest.NoTaxonomy {

		@BeforeClass
		public static void before() throws Exception {
			ShortAbstractQwazrTest.NoTaxonomy.before(TestSettings.of(currentResults).warmup(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class QwazrNoTaxonomy extends ShortAbstractQwazrTest.NoTaxonomy {

		@BeforeClass
		public static void before() throws Exception {
			ShortAbstractQwazrTest.NoTaxonomy.before(TestSettings.of(currentResults).warmup(false));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class QwazrWithTaxonomyWarmup extends ShortAbstractQwazrTest.WithTaxonomy {

		@BeforeClass
		public static void before() throws Exception {
			ShortAbstractQwazrTest.WithTaxonomy.before(TestSettings.of(currentResults).warmup(true));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static public class QwazrWithTaxonomy extends ShortAbstractQwazrTest.WithTaxonomy {

		@BeforeClass
		public static void before() throws Exception {
			ShortAbstractQwazrTest.WithTaxonomy.before(TestSettings.of(currentResults).warmup(false));
		}
	}
}
