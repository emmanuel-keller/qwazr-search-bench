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

import com.qwazr.search.bench.CommonIndexer;
import com.qwazr.search.bench.ConcurrentIndexer;
import com.qwazr.search.bench.LuceneCommonIndex;
import com.qwazr.search.bench.LuceneNoTaxonomyIndex;
import com.qwazr.search.bench.LuceneRecord;
import com.qwazr.search.bench.LuceneWithTaxonomyIndex;
import com.qwazr.search.bench.SingleIndexer;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.utils.IOUtils;
import org.apache.lucene.facet.FacetsConfig;
import org.junit.AfterClass;

import java.io.IOException;
import java.util.function.BiConsumer;

public abstract class LuceneTest extends BaseTest implements BiConsumer<TtlLineReader, LuceneRecord> {

	protected static LuceneCommonIndex luceneIndex;

	protected static final FacetsConfig FACETS_CONFIG = new FacetsConfig();

	protected final CommonIndexer indexer;

	public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
		final TestSettings settings = settingsBuilder.build();
		BaseTest.before(settings);
		LuceneTest.luceneIndex = currentSettings.taxonomy ?
				new LuceneWithTaxonomyIndex(schemaDirectory, BaseTest.SCHEMA_NAME, BaseTest.INDEX_NAME, executor,
						settings.getRamBuffer()) :
				new LuceneNoTaxonomyIndex(schemaDirectory, BaseTest.SCHEMA_NAME, BaseTest.INDEX_NAME, executor,
						settings.getRamBuffer());
	}

	@AfterClass
	public static void after() {
		IOUtils.close(luceneIndex);
		BaseTest.after();
	}

	@Override
	final public void accept(final TtlLineReader line) {
		indexer.accept(line);
	}

	@Override
	final public void flush() {
		indexer.close();
	}

	protected LuceneTest() {
		indexer = currentSettings.executor ?
				new ConcurrentIndexer(executor, luceneIndex, FACETS_CONFIG, this, currentSettings.batchSize) :
				new SingleIndexer(luceneIndex, FACETS_CONFIG, this, currentSettings.batchSize);
	}

	@Override
	final long getNumDocs() throws IOException {
		return luceneIndex.search(searcher -> (long) searcher.getIndexReader().numDocs());
	}

}
