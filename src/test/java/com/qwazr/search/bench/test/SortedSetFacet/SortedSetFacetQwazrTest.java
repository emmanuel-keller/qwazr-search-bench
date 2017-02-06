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
package com.qwazr.search.bench.test.SortedSetFacet;

import com.qwazr.search.annotations.AnnotatedIndexService;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TtlLineReader;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SortedSetFacetQwazrTest extends QwazrTest<SortedSetFacetQwazrRecord> {

	private static AnnotatedIndexService<SortedSetFacetQwazrRecord> indexService;

	@BeforeClass
	public static void before() throws Exception {
		QwazrTest.before();
		indexService = indexManager.getService(SortedSetFacetQwazrRecord.class);
		indexService.createUpdateSchema();
		indexService.createUpdateIndex();
		indexService.createUpdateFields();
	}

	public SortedSetFacetQwazrTest() {
		super(SHORT_ABSTRACT_FILE, BATCH_SIZE, LIMIT, indexService);
	}

	@Override
	public SortedSetFacetQwazrRecord apply(final TtlLineReader ttlLineReader) {
		return new SortedSetFacetQwazrRecord(ttlLineReader);
	}
}
