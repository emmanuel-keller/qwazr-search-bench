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
package com.qwazr.search.bench.test.Merging;

import com.qwazr.search.bench.IndexFiles;
import com.qwazr.search.bench.LuceneCommonIndex;
import com.qwazr.search.bench.LuceneNoTaxonomyIndex;
import com.qwazr.search.bench.LuceneRecord;
import com.qwazr.search.bench.test.BaseTest;
import com.qwazr.utils.concurrent.ConsumerEx;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public abstract class UpdateFieldTest {

	final static int DOC_COUNT = 10_000;

	private ExecutorService executor;
	protected LuceneCommonIndex index;

	@Before
	public void setup() throws IOException {
		executor = Executors.newCachedThreadPool();
		final Path schemaDirectory = Files.createTempDirectory("lucene-merging");
		System.out.println(schemaDirectory);
		index = new LuceneNoTaxonomyIndex(schemaDirectory, BaseTest.SCHEMA_NAME, BaseTest.INDEX_NAME, executor, 1024,
				false);
		index.commitAndPublish();
	}

	/**
	 * Index the document and return an image of the index
	 *
	 * @param from
	 * @param to
	 * @param expectedNumDocs
	 * @return
	 * @throws IOException
	 */
	protected <T extends LuceneRecord> IndexFiles indexLoop(IndexFiles previousIndex, String sessionName, int from,
			int to, int expectedNumDocs, T record, BiConsumer<String, T> recordProvider,
			ConsumerEx<T, IOException> recordConsumer) throws IOException {

		// Let's index the records
		for (int i = from; i < to; i++) {
			recordProvider.accept("pt" + i, record);
			recordConsumer.accept(record);
		}
		index.commitAndPublish();

		// Check we have the correct number of documents
		int numDocs = index.search(searcher -> searcher.getIndexReader().numDocs());
		Assert.assertEquals(expectedNumDocs, numDocs);

		final String sessionNameExt = sessionName + " (" + from + "-" + to + ")";
		// Compare the files
		final IndexFiles indexFiles = index.getIndexFiles();
		indexFiles.dump(sessionNameExt + " INDEX", true);
		indexFiles.getUpdatedFiles(previousIndex).dump(sessionNameExt + " DIFF");

		return indexFiles;
	}

	@After
	public void cleanup() {
		executor.shutdown();
	}
}
