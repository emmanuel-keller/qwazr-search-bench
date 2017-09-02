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
package com.qwazr.search.bench.test.FullText;

import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TestSettings;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by ekeller on 15/02/2017.
 */
public abstract class ShortAbstractQwazrTest<T extends ShortAbstractQwazrRecord> extends QwazrTest<T> {

	protected ShortAbstractQwazrTest(Class<T> masterRecordClass, Class<?>... optionalRecordClasses) {
		super(masterRecordClass, optionalRecordClasses);
	}

	@Parameterized.Parameters
	public static Collection<Boolean> iterations() {
		return Arrays.asList(true, false);
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static class NoTaxonomy extends ShortAbstractQwazrTest<ShortAbstractQwazrRecord.NoTaxonomy> {

		public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
			QwazrTest.before(settingsBuilder.taxonomy(false));
		}

		NoTaxonomy() {
			super(ShortAbstractQwazrRecord.NoTaxonomy.class);
		}

		@Override
		final public Boolean apply(final TtlLineReader ttlLineReader) {
			index(new ShortAbstractQwazrRecord.NoTaxonomy(ttlLineReader));
			return true;
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static class WithTaxonomy extends ShortAbstractQwazrTest<ShortAbstractQwazrRecord.WithTaxonomy> {

		public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
			QwazrTest.before(settingsBuilder.taxonomy(true));
		}

		WithTaxonomy() {
			super(ShortAbstractQwazrRecord.WithTaxonomy.class);
		}

		@Override
		final public Boolean apply(final TtlLineReader ttlLineReader) {
			index(new ShortAbstractQwazrRecord.WithTaxonomy(ttlLineReader));
			return true;
		}
	}
}
