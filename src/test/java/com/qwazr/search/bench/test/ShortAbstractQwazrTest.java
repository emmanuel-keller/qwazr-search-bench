/**
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
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortAbstractQwazrTest extends BaseTest<ShortAbstractQwazrRecord> {

	private IndexManager indexManager;
	private AnnotatedIndexService<ShortAbstractQwazrRecord> indexService;

	public ShortAbstractQwazrTest() throws IOException, URISyntaxException {
		super(SHORT_ABSTRACT_FILE, BATCH_SIZE, LIMIT);
		indexManager = new IndexManager(null, indexDirectory, executor);
		indexService = indexManager.getService(ShortAbstractQwazrRecord.class);
		indexService.createUpdateSchema();
		indexService.createUpdateIndex();
		indexService.createUpdateFields();
	}

	@Override
	public void finalize() {
		indexManager.close();
	}

	@Override
	final public void accept(final List<ShortAbstractQwazrRecord> buffer) {
		try {
			indexService.postDocuments(buffer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	final public ShortAbstractQwazrRecord apply(final TtlLineReader lineReader) {
		return new ShortAbstractQwazrRecord(lineReader);
	}

	@Override
	public void testZZZCheck() {
		Assert.assertEquals(count, (long) indexService.getIndexStatus().num_docs);
	}
}
