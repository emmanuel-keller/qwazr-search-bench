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

import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TestSettings;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Created by ekeller on 15/02/2017.
 */
class IndexingQwazrTest {

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static class DefaultRamBuffer extends QwazrTest<IndexingQwazrRecord.DefaultRamBuffer> {

		public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
			QwazrTest.before(settingsBuilder);
		}

		DefaultRamBuffer() {
			super(SHORT_ABSTRACT_FILE, IndexingQwazrRecord.DefaultRamBuffer.class);
		}

		@Override
		final public void accept(final TtlLineReader ttlLineReader) {
			index(new IndexingQwazrRecord.DefaultRamBuffer(ttlLineReader));
		}
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	static class HighRamBuffer extends QwazrTest<IndexingQwazrRecord.HighRamBuffer> {

		public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
			QwazrTest.before(settingsBuilder.highRamBuffer(true));
		}

		HighRamBuffer() {
			super(SHORT_ABSTRACT_FILE, IndexingQwazrRecord.HighRamBuffer.class);
		}

		@Override
		final public void accept(final TtlLineReader ttlLineReader) {
			index(new IndexingQwazrRecord.HighRamBuffer(ttlLineReader));
		}
	}
}
