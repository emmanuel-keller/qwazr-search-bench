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
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortAbstractLuceneTest extends BaseTest<ShortAbstractLuceneRecord> {

	private LuceneIndex luceneIndex;

	public ShortAbstractLuceneTest() throws IOException, URISyntaxException {
		super(SHORT_ABSTRACT_FILE, BATCH_SIZE, LIMIT);
		luceneIndex = new LuceneIndex(indexDirectory, executor, RAM_BUFFER_SIZE);
	}

	@Override
	public void finalize() {
		IOUtils.close(luceneIndex);
	}

	@Override
	final public void accept(final List<ShortAbstractLuceneRecord> buffer) {
		try {
			luceneIndex.write(writer -> {
				for (ShortAbstractLuceneRecord record : buffer)
					writer.updateDocument(record.termId, record.document);
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	final public ShortAbstractLuceneRecord apply(final TtlLineReader lineReader) {
		try {
			return new ShortAbstractLuceneRecord(lineReader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void testZZZCheck() throws IOException {
		Long numDocs = luceneIndex.search(searcher -> (long) searcher.getIndexReader().numDocs());
		Assert.assertEquals(count, numDocs.longValue());
	}

}
