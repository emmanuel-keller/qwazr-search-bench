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

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class BaseTest<T> implements Consumer<List<T>>, Function<TtlLineReader, T> {

	static final int RAM_BUFFER_SIZE = 256;

	static final int BATCH_SIZE = 7500;

	static final int LIMIT = 20000;

	static final File SHORT_ABSTRACT_FILE = new File("data/short_abstracts_en.ttl.bz2");

	static Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

	private final Consumer<List<T>> doNothingConsumer = buffer -> {
		Assert.assertTrue(buffer.size() > 0);
	};

	private final TtlLoader<T> loader;
	private final int limit;
	final Path indexDirectory;
	final ExecutorService executor;

	long count;

	BaseTest(File ttlFile, int batchSize, int limit) throws IOException {
		this.loader = new TtlLoader<>(ttlFile, batchSize);
		this.limit = limit;
		this.indexDirectory = Files.createTempDirectory("QwazrSearchBench");
		this.executor = Executors.newCachedThreadPool();
	}

	@Test
	public void test100loadDoNothing() throws IOException {
		loader.load(limit, this, doNothingConsumer);
	}

	@Test
	public void test200secondLoadDoNothing() throws IOException {
		loader.load(limit, this, doNothingConsumer);
	}

	@Test
	public void test300LoadReadTest() throws IOException {
		long time = System.currentTimeMillis();
		count = loader.load(limit, this, this);
		time = System.currentTimeMillis() - time;
		final long rate = (count * 1000) / time;
		LOGGER.info("Rate: " + rate);
		LOGGER.info(count + " lines indexed");
	}

	@Test
	public abstract void testZZZCheck() throws IOException;

}
