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
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class QwazrTest<T extends BaseQwazrRecord> extends BaseTest {

	public static IndexManager indexManager;

	private List<T> buffer;

	public static void before(final TestSettings.Builder settingsBuilder) throws Exception {
		BaseTest.before(settingsBuilder.executor(true).build());
		indexManager = new IndexManager(schemaDirectory, executor);
	}

	static private <T> AnnotatedIndexService<T> createService(final Class<T> recordClass) {
		try {
			final AnnotatedIndexService<T> indexService = indexManager.getService(recordClass);
			indexService.createUpdateSchema();
			indexService.createUpdateIndex();
			indexService.createUpdateFields();
			return indexService;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterClass
	public static void after() {
		indexManager.close();
		BaseTest.after();
	}

	private final List<AnnotatedIndexService<?>> indexServices;
	private final AnnotatedIndexService<T> indexService;

	protected QwazrTest(Class<T> masterRecordClass, Class<?>... optionalRecordClasses) {
		this.indexServices = new ArrayList<>();
		if (optionalRecordClasses != null)
			for (Class<?> recordClass : optionalRecordClasses)
				indexServices.add(createService(recordClass));
		this.indexService = createService(masterRecordClass);
		this.buffer = new ArrayList<>();
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
			buffer.clear();
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
