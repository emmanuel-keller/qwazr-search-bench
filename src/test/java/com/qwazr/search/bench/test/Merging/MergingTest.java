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

import com.qwazr.search.bench.LuceneCommonIndex;
import com.qwazr.search.bench.LuceneNoTaxonomyIndex;
import com.qwazr.search.bench.test.BaseTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MergingTest {

	static ExecutorService executorService;

	@BeforeClass
	public static void setup() {
		executorService = Executors.newCachedThreadPool();
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
						256);
		index.commitAndPublish();

		index.getIndexFiles().dump();

	}

}
