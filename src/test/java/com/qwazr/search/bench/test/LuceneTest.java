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

import com.qwazr.utils.IOUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class LuceneTest<T extends LuceneRecord> extends BaseTest<T> {

	private static LuceneIndex luceneIndex;

	@BeforeClass
	public static void before() throws Exception {
		BaseTest.before();
		luceneIndex = new LuceneIndex(indexDirectory, executor, RAM_BUFFER_SIZE);
	}

	@AfterClass
	public static void after() {
		IOUtils.close(luceneIndex);
		BaseTest.after();
	}

	protected LuceneTest(File ttlFile, int batchSize, int limit) {
		super(ttlFile, batchSize, limit);
	}

	@Override
	final public void accept(final List<T> buffer) {
		try {
			luceneIndex.write(writer -> {
				for (T record : buffer)
					writer.updateDocument(record.termId, record.document);
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	final long getNumDocs() throws IOException {
		return luceneIndex.search(searcher -> (long) searcher.getIndexReader().numDocs());
	}

	@Override
	long getHits(String field, String term) throws IOException {
		return (long) luceneIndex.search(
				searcher -> searcher.search(new TermQuery(new Term(field, term)), 1).totalHits);
	}
}
