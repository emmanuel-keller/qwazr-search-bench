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

import com.qwazr.profiler.ProfilerManager;
import com.qwazr.utils.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class BaseTest<T> implements Consumer<List<T>>, Function<TtlLineReader, T> {

	public static final String SCHEMA_NAME = "schemaTest";

	public static final String INDEX_NAME = "indexTest";

	public static final int RAM_BUFFER_SIZE = 256;

	public static final int BATCH_SIZE = getEnvOrDefault("SEARCH_BENCH_BATCH_SIZE", 5000);

	public static final int LIMIT = getEnvOrDefault("SEARCH_BENCH_LIMIT", 512000);

	static final String SHORT_ABSTRACT_URL = "http://downloads.dbpedia.org/3.9/en/short_abstracts_en.ttl.bz2";

	public static final File SHORT_ABSTRACT_FILE = new File("data/short_abstracts_en.ttl.bz2");

	static Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

	protected static ExecutorService executor;
	protected static Path indexDirectory;

	private static int getEnvOrDefault(String key, int def) {
		String val = System.getenv(key);
		return val == null ? def : Integer.parseInt(val);
	}

	public static void before(boolean withExecutor) throws Exception {

		ProfilerManager.load(null);

		Thread.sleep(20000);
		if (!SHORT_ABSTRACT_FILE.exists()) {
			SHORT_ABSTRACT_FILE.getParentFile().mkdir();
			try (final InputStream input = new URL(SHORT_ABSTRACT_URL).openStream()) {
				try (ReadableByteChannel rbc = Channels.newChannel(input)) {
					try (final FileOutputStream fos = new FileOutputStream(SHORT_ABSTRACT_FILE)) {
						try (final FileChannel fileChannel = fos.getChannel()) {
							fileChannel.transferFrom(rbc, 0, Long.MAX_VALUE);
						}
					}
				}
			}
		}

		indexDirectory = Files.createTempDirectory("QwazrSearchBench");
		executor = withExecutor ? Executors.newCachedThreadPool() : null;
	}

	@AfterClass
	public static void after() {
		if (executor != null)
			executor.shutdown();
		System.gc();
		ProfilerManager.dump();
		ProfilerManager.reset();
	}

	private final Consumer<List<T>> doNothingConsumer = buffer -> Assert.assertTrue(buffer.size() > 0);

	private final TtlLoader<T> loader;
	private final int limit;

	BaseTest(File ttlFile, int batchSize, int limit) {
		this.loader = new TtlLoader<>(ttlFile, batchSize);
		this.limit = limit;
	}

	@Test
	public void test100loadWarmupDoNothing() throws IOException {
		loader.load(limit, this, doNothingConsumer);
	}

	private static long count;

	@Test
	public void test200LoadRealTest() throws IOException {
		long time = System.currentTimeMillis();
		count = loader.load(limit, this, this);
		time = System.currentTimeMillis() - time;
		final long rate = (count * 1000) / time;
		LOGGER.info("###");
		LOGGER.info("### " + getClass().getSimpleName());
		LOGGER.info("Rate: " + rate);
		LOGGER.info(count + " lines indexed");
	}

	abstract long getNumDocs() throws IOException;

	abstract long getHits(String field, String term) throws IOException;

	@Test
	public void testZZZCheck() throws IOException {
		Assert.assertEquals(count, getNumDocs());
		final Path rootPath = indexDirectory.resolve(BaseTest.SCHEMA_NAME).resolve(BaseTest.INDEX_NAME);
		LOGGER.info("Index size: " + FileUtils.byteCountToDisplaySize(
				FileUtils.sizeOf(rootPath.resolve("data").toFile()) + FileUtils.sizeOf(
						rootPath.resolve("taxonomy").toFile())));
	}

}
