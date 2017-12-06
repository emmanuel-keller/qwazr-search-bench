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
import com.qwazr.utils.FileUtils;
import com.qwazr.utils.RandomUtils;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MergingTest {

	final static int ITERATIONS = 100;
	final static int RECORDS_BUFFER_MIN = 50;
	final static int RECORDS_BUFFER_MAX = 5_000;
	final static int ID_MAX = 10_000;
	final static int RAM_BUFFER_SIZE = 1024;
	final static int FIELDS_NUMBER = 10;
	final static int TEXT_MIN_SIZE = 20;
	final static int TEXT_MAX_SIZE = 50;
	final static int TERM_MIN_SIZE = 2;
	final static int TERM_MAX_SIZE = 8;

	static ExecutorService executorService;

	static String[] dictionary;
	static ZipfDistribution distribution;

	@BeforeClass
	public static void setup() {
		executorService = Executors.newCachedThreadPool();

		// Let's build a dictionary of terms
		dictionary = new String[100_000];
		for (int i = 0; i < dictionary.length; i++)
			dictionary[i] = RandomUtils.alphanumeric(RandomUtils.nextInt(TERM_MIN_SIZE, TERM_MAX_SIZE));
		distribution = new ZipfDistribution(dictionary.length, 1.07f);
	}

	@AfterClass
	public static void cleanup() {
		executorService.shutdown();
	}

	@Test
	public void test() throws IOException {
		final Path schemaDirectory = Files.createTempDirectory("lucene-merging");

		final LuceneCommonIndex index =
				new LuceneNoTaxonomyIndex(schemaDirectory, BaseTest.SCHEMA_NAME, BaseTest.INDEX_NAME, executorService,
						RAM_BUFFER_SIZE, false);
		index.commitAndPublish();

		IndexFiles lastIndexFiles = index.getIndexFiles();
		lastIndexFiles.dump("**** EMPTY INDEX *****", true);

		final LuceneRecord.Indexable luceneRecord = new LuceneRecord.Indexable();

		for (int i = 0; i < ITERATIONS; i++) {
			final int recordsCount = RandomUtils.nextInt(RECORDS_BUFFER_MIN, RECORDS_BUFFER_MAX);
			addRecords(index, recordsCount, () -> {
				final String id = "id" + RandomUtils.nextInt(0, ID_MAX);
				luceneRecord.reset(new Term("id", id), new StringField("id", id, Field.Store.NO),
						new TextField(randomField(), randomText(), Field.Store.NO));
				return luceneRecord;
			});
			index.commitAndPublish();

			final IndexFiles newIndexFiles = index.getIndexFiles();
			dumpDiff(i, recordsCount, lastIndexFiles, newIndexFiles);
			lastIndexFiles = newIndexFiles;
		}

	}

	static void dumpDiff(final int iteration, final int recordsCount, final IndexFiles lastIndexFiles,
			final IndexFiles newIndexFiles) {
		final IndexFiles.Updated updatedFiles = newIndexFiles.getUpdatedFiles(lastIndexFiles);
		final long percentChange = (updatedFiles.getTotalSize() * 100 / newIndexFiles.getTotalSize());

		String dump = "**** DIFF " + iteration + " - Records: " + recordsCount + '/' + lastIndexFiles.getNumDocs();
		dump += " - Sizes: " + FileUtils.byteCountToDisplaySize(updatedFiles.getTotalSize()) + '/' +
				FileUtils.byteCountToDisplaySize(newIndexFiles.getTotalSize()) + " (" + percentChange + "%)";
		dump += " - Files: " + updatedFiles.getFileCount() + '(' + updatedFiles.getNewFiles() + '/' +
				updatedFiles.getModifiedFiles() + ") / " + newIndexFiles.getFileCount();
		System.out.println(dump);
		if (percentChange > 50 || updatedFiles.getModifiedFiles() > 0) {
			lastIndexFiles.dump("** PREVIOUS", true);
			updatedFiles.dump("** CHANGES");
		}
	}

	String randomField() {
		return "field" + RandomUtils.nextInt(0, FIELDS_NUMBER);
	}

	String randomText() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < RandomUtils.nextInt(TEXT_MIN_SIZE, TEXT_MAX_SIZE); i++) {
			if (i != 0)
				builder.append(' ');
			builder.append(dictionary[distribution.sample() - 1]);
		}
		return builder.toString();
	}

	void addRecords(LuceneCommonIndex index, int count, Supplier<LuceneRecord.Indexable> recordSupplier)
			throws IOException {
		while (count-- > 0) {
			index.updateDocument(null, recordSupplier.get());
		}
	}

}
