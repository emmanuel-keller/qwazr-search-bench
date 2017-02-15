package com.qwazr.search.bench.test.FullText;

import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TestSettings;
import com.qwazr.search.bench.TtlLineReader;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Created by ekeller on 15/02/2017.
 */
class ShortAbstractQwazrTest {

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static class NoTaxonomy extends QwazrTest<ShortAbstractQwazrRecord.NoTaxonomy> {

		public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
			QwazrTest.before(settingsBuilder.taxonomy(false));
		}

		NoTaxonomy() {
			super(SHORT_ABSTRACT_FILE, ShortAbstractQwazrRecord.NoTaxonomy.class);
		}

		@Override
		final public ShortAbstractQwazrRecord.NoTaxonomy apply(final TtlLineReader ttlLineReader) {
			return new ShortAbstractQwazrRecord.NoTaxonomy(ttlLineReader);
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static class WithTaxonomy extends QwazrTest<ShortAbstractQwazrRecord.WithTaxonomy> {

		public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
			QwazrTest.before(settingsBuilder.taxonomy(true));
		}

		WithTaxonomy() {
			super(SHORT_ABSTRACT_FILE, ShortAbstractQwazrRecord.WithTaxonomy.class);
		}

		@Override
		final public ShortAbstractQwazrRecord.WithTaxonomy apply(final TtlLineReader ttlLineReader) {
			return new ShortAbstractQwazrRecord.WithTaxonomy(ttlLineReader);
		}
	}
}
