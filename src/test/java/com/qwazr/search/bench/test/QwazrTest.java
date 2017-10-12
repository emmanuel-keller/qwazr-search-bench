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

import com.qwazr.search.annotations.AnnotatedIndexService;
import com.qwazr.search.index.IndexManager;
import com.qwazr.search.index.IndexSettingsDefinition;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class QwazrTest<T extends BaseQwazrRecord> extends BaseTest {

	public static IndexManager indexManager;

	private List<T> buffer;

	protected AtomicInteger indexedDocumentsCount = new AtomicInteger();

	protected AtomicInteger flushCount = new AtomicInteger();

	public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
		BaseTest.before(settingsBuilder.executor(true).build());
		indexManager = new IndexManager(schemaDirectory, executor);
	}

	static private <T> AnnotatedIndexService<T> createService(final Class<T> recordClass, final String schema,
			final String index, final IndexSettingsDefinition settings) {
		try {
			final AnnotatedIndexService<T> indexService =
					new AnnotatedIndexService<>(indexManager.getService(), recordClass, schema, index, settings);
			indexService.createUpdateSchema();
			indexService.createUpdateIndex();
			indexService.createUpdateFields();
			return indexService;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterClass
	public static void after() throws InterruptedException {
		indexManager.close();
		BaseTest.after();
	}

	protected final List<AnnotatedIndexService<T>> indexServices;
	protected final AnnotatedIndexService<T> indexService;

	protected QwazrTest(Class<T> recordClass) {
		this.indexServices = new ArrayList<>();
		for (TestSettings.Index index : currentSettings.indexes)
			indexServices.add(createService(recordClass, BaseTest.SCHEMA_NAME, index.index, toSettings(index)));
		indexService = indexServices.get(0);
		this.buffer = new ArrayList<>();
	}

	static IndexSettingsDefinition toSettings(final TestSettings.Index index) {
		try {
			return IndexSettingsDefinition.of()
					.enableTaxonomyIndex(index.taxonomy)
					.useCompoundFile(index.useCompoundFile)
					.ramBufferSize(index.ramBuffer)
					.indexReaderWarmer(index.useWarmer)
					.mergedSegmentWarmer(index.useWarmer)
					.master(index.master)
					.similarityClass(index.similarityClass)
					.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	final public void index(final T record) {
		buffer.add(record);
		if (buffer.size() >= currentSettings.batchSize)
			flush();
	}

	@Override
	final public void flush() {
		if (buffer.isEmpty())
			return;
		try {
			indexService.postDocuments(buffer);
			indexedDocumentsCount.addAndGet(buffer.size());
			buffer.clear();
			flushCount.incrementAndGet();
			postFlush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void postFlush() {
	}

	@Override
	long getNumDocs() throws IOException {
		return indexService.getIndexStatus().num_docs;
	}

}
