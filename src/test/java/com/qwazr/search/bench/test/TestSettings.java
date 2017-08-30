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

import org.apache.lucene.index.IndexWriterConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by ekeller on 15/02/2017.
 */
public class TestSettings {

	static final int DEFAULT_BATCH_SIZE = getEnvOrDefault("SEARCH_BENCH_BATCH_SIZE", 5000);

	static final int DEFAULT_LIMIT = getEnvOrDefault("SEARCH_BENCH_LIMIT", 50000);

	static final String DEFAULT_TTL_URL = "http://downloads.dbpedia.org/3.9/en/short_abstracts_en.ttl.bz2";

	static final File DEFAULT_TTL_FILE = new File("data/short_abstracts_en.ttl.bz2");

	static final double HIGH_RAM_BUFFER = 2048;

	final boolean taxonomy;

	final boolean executor;

	final boolean highRamBuffer;

	final Path schemaDirectory;

	final int batchSize;

	final int limit;

	final File ttlFile;

	final String ttlUrl;

	final TestResults results;

	private TestSettings(Builder builder) throws IOException {
		this.results = builder.results;
		this.taxonomy = builder.taxonomy == null ? false : builder.taxonomy;
		this.executor = builder.executor;
		this.highRamBuffer = builder.highRamBuffer == null ? false : builder.highRamBuffer;
		this.schemaDirectory = builder.schemaDirectory == null ?
				Files.createTempDirectory("qwazrSearchBench") :
				builder.schemaDirectory;
		this.batchSize = builder.batchSize == null ? DEFAULT_BATCH_SIZE : builder.batchSize;
		this.limit = builder.limit == null ? DEFAULT_LIMIT : builder.limit;
		this.ttlFile = builder.ttlFile == null ? DEFAULT_TTL_FILE : builder.ttlFile;
		this.ttlUrl = builder.ttlUrl == null ? DEFAULT_TTL_URL : builder.ttlUrl;
	}

	static int getEnvOrDefault(String key, int def) {
		String val = System.getenv(key);
		return val == null ? def : Integer.parseInt(val);
	}

	double getRamBuffer() {
		return highRamBuffer ? HIGH_RAM_BUFFER : IndexWriterConfig.DEFAULT_RAM_BUFFER_SIZE_MB;
	}

	@Override
	public String toString() {
		return "SETTINGS - Executor: " + executor + " - Taxonomy: " + taxonomy + " - RamBuffer: " + getRamBuffer() +
				"MB" + " - Path: " + schemaDirectory;
	}

	public static Builder of(TestResults results) {
		return new Builder(results);
	}

	public static class Builder {

		private Boolean taxonomy = null;

		private Boolean executor = null;

		private Boolean highRamBuffer = null;

		private Path schemaDirectory = null;

		private Integer batchSize;

		private Integer limit;

		private File ttlFile;

		private String ttlUrl;

		private final TestResults results;

		private Builder(TestResults results) {
			this.results = results;
		}

		public Builder taxonomy(boolean taxonomy) {
			this.taxonomy = taxonomy;
			return this;
		}

		public Builder executor(boolean executor) {
			this.executor = executor;
			return this;
		}

		public Builder highRamBuffer(boolean highRamBuffer) {
			this.highRamBuffer = highRamBuffer;
			return this;
		}

		public Builder schemaDirectory(Path schemaDirectory) {
			this.schemaDirectory = schemaDirectory;
			return this;
		}

		public Builder batchSize(Integer batchSize) {
			this.batchSize = batchSize;
			return this;
		}

		public Builder limit(Integer limit) {
			this.limit = limit;
			return this;
		}

		public Builder ttlFile(File ttlFile) {
			this.ttlFile = ttlFile;
			return this;
		}

		public Builder ttlUrl(String ttlUrl) {
			this.ttlUrl = ttlUrl;
			return this;
		}

		public TestSettings build() throws IOException {
			return new TestSettings(this);
		}

	}

}
