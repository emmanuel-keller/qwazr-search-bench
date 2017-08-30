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

import com.qwazr.profiler.ProfilerManager;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.TtlLoader;
import com.qwazr.utils.FileUtils;
import com.qwazr.utils.LoggerUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public abstract class BaseTest implements Consumer<TtlLineReader> {

	@Parameterized.Parameter
	public Boolean warmup;

	public static final String SCHEMA_NAME = "schemaTest";

	public static final String INDEX_NAME = "indexTest";

	static Logger LOGGER = LoggerUtils.getLogger(BaseTest.class);

	protected static TestSettings currentSettings;
	protected static ExecutorService executor;
	protected static Path schemaDirectory;

	public static void before(final TestSettings settings) throws Exception {

		ProfilerManager.load(null);

		Thread.sleep(20000);

		// Download the DBPedia file
		if (!settings.ttlFile.exists()) {
			settings.ttlFile.getParentFile().mkdir();
			try (final InputStream input = new URL(settings.ttlUrl).openStream()) {
				try (ReadableByteChannel rbc = Channels.newChannel(input)) {
					try (final FileOutputStream fos = new FileOutputStream(settings.ttlFile)) {
						try (final FileChannel fileChannel = fos.getChannel()) {
							fileChannel.transferFrom(rbc, 0, Long.MAX_VALUE);
						}
					}
				}
			}
		}

		currentSettings = settings;
		schemaDirectory = settings.schemaDirectory;
		executor = currentSettings.executor ? Executors.newCachedThreadPool() : null;

		if (!Files.exists(schemaDirectory))
			Files.createDirectory(schemaDirectory);
	}

	@AfterClass
	public static void after() {
		if (executor != null)
			executor.shutdown();
		System.gc();
	}

	private final TtlLoader loader;
	private final int limit;

	BaseTest() {
		this.loader = new TtlLoader(currentSettings.ttlFile);
		this.limit = currentSettings.limit;
	}

	private static long count;

	@Test
	public void test100Test() throws IOException {
		ProfilerManager.reset();
		LOGGER.info(currentSettings.toString());
		LOGGER.info("INDEX DIR: " + schemaDirectory);
		long time = System.currentTimeMillis();
		count = loader.load(limit, this);
		flush();
		time = System.currentTimeMillis() - time;
		final int rate = (int) ((count * 1000) / time);
		if (!warmup) {
			LOGGER.info("###");
			LOGGER.info("### " + getClass().getName());
			LOGGER.info("Rate: " + rate);
			LOGGER.info(count + " lines indexed");
			if (ProfilerManager.isInitialized())
				ProfilerManager.dump();
			if (currentSettings.results != null)
				currentSettings.results.add(this, rate);
		}
	}

	abstract void flush();

	abstract long getNumDocs() throws IOException;

	@After
	public void testZZZCheck() throws IOException {
		Assert.assertEquals(count, getNumDocs());
		final Path rootPath = schemaDirectory.resolve(BaseTest.SCHEMA_NAME).resolve(BaseTest.INDEX_NAME);
		long size = FileUtils.sizeOf(rootPath.resolve("data").toFile());
		if (currentSettings.taxonomy) {
			Path taxoPath = rootPath.resolve("taxonomy");
			size += FileUtils.sizeOf(taxoPath.toFile());
		}
		if (!warmup)
			LOGGER.info("Index size: " + FileUtils.byteCountToDisplaySize(size));
	}

}
