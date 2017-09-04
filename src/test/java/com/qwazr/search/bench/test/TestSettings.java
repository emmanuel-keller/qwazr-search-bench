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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekeller on 15/02/2017.
 */
public class TestSettings {

	static final int DEFAULT_BATCH_SIZE = getEnvOrDefault("SEARCH_BENCH_BATCH_SIZE", 2000);

	static final int DEFAULT_LIMIT = getEnvOrDefault("SEARCH_BENCH_LIMIT", 5000);

	static final String DEFAULT_TTL_URL = "http://downloads.dbpedia.org/3.9/en/short_abstracts_en.ttl.bz2";

	static final File DEFAULT_TTL_FILE = new File("data/short_abstracts_en.ttl.bz2");

	final Index[] indexes;

	final Path schemaDirectory;

	final boolean executor;

	final int batchSize;

	public final int limit;

	final File ttlFile;

	final String ttlUrl;

	final TestResults results;

	private TestSettings(Builder builder) throws IOException {
		this.results = builder.results;
		this.indexes = new Index[builder.indexesBuilder.size()];
		int i = 0;
		for (Index.Builder indexBuilder : builder.indexesBuilder)
			this.indexes[i++] = indexBuilder.build();
		this.schemaDirectory = builder.schemaDirectory == null ?
				Files.createTempDirectory("qwazrSearchBench") :
				builder.schemaDirectory;
		this.executor = builder.executor == null ? false : builder.executor;
		this.batchSize = builder.batchSize == null ? DEFAULT_BATCH_SIZE : builder.batchSize;
		this.limit = builder.limit == null ? DEFAULT_LIMIT : builder.limit;
		this.ttlFile = builder.ttlFile == null ? DEFAULT_TTL_FILE : builder.ttlFile;
		this.ttlUrl = builder.ttlUrl == null ? DEFAULT_TTL_URL : builder.ttlUrl;
	}

	static int getEnvOrDefault(String key, int def) {
		String val = System.getProperty(key.toLowerCase());
		if (val == null)
			val = System.getenv(key.toUpperCase());
		return val == null ? def : Integer.parseInt(val);
	}

	@Override
	public String toString() {
		String s = "SETTINGS  - Executor: " + executor + " - Path: " + schemaDirectory;
		for (Index index : indexes)
			s += System.lineSeparator() + index.toString();
		return s;
	}

	public static Builder of(TestResults results) {
		return new Builder(results);
	}

	public static class Builder {

		private final List<Index.Builder> indexesBuilder = new ArrayList<>();

		private Path schemaDirectory = null;

		private Boolean executor;

		private Integer batchSize;

		private Integer limit;

		private File ttlFile;

		private String ttlUrl;

		private final TestResults results;

		private Builder(TestResults results) {
			this.results = results;
		}

		public Index.Builder index(String index) {
			final Index.Builder indexBuilder = new Index.Builder(this, index);
			indexesBuilder.add(indexBuilder);
			return indexBuilder;
		}

		public Index.Builder index(int i) {
			return indexesBuilder.get(i);
		}

		public Builder schemaDirectory(Path schemaDirectory) {
			this.schemaDirectory = schemaDirectory;
			return this;
		}

		public Builder executor(Boolean executor) {
			this.executor = executor;
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

	public static class Index {

		final String index;

		final boolean taxonomy;

		final double ramBuffer;

		final boolean useCompoundFile;

		final boolean useWarmer;

		final String master;

		Index(Builder builder) {
			this.index = builder.index;
			this.taxonomy = builder.taxonomy == null ? false : builder.taxonomy;
			this.ramBuffer =
					builder.ramBuffer == null ? IndexWriterConfig.DEFAULT_RAM_BUFFER_SIZE_MB : builder.ramBuffer;
			this.useCompoundFile = builder.useCompoundFile == null ? true : builder.useCompoundFile;
			this.useWarmer = builder.useWarmer == null ? true : builder.useWarmer;
			this.master = builder.master;
		}

		@Override
		public String toString() {
			return "INDEX: " + index + " - Taxonomy: " + taxonomy + " - RamBuffer: " + ramBuffer + "MB - UseCFS: " +
					useCompoundFile + " - UseWarmer: " + useWarmer + " - Master: " + master;
		}

		public static class Builder {

			private final TestSettings.Builder builder;

			private final String index;

			private Boolean taxonomy;

			private Double ramBuffer;

			private Boolean useCompoundFile;

			private Boolean useWarmer;

			private String master;

			Builder(TestSettings.Builder builder, String index) {
				this.builder = builder;
				this.index = index;
			}

			public Builder taxonomy(Boolean taxonomy) {
				this.taxonomy = taxonomy;
				return this;
			}

			public Builder ramBuffer(Integer ramBuffer) {
				this.ramBuffer = ramBuffer.doubleValue();
				return this;
			}

			public Builder useCompoundFile(Boolean useCompoundFile) {
				this.useCompoundFile = useCompoundFile;
				return this;
			}

			public Builder useWarmer(Boolean useWarmer) {
				this.useWarmer = useWarmer;
				return this;
			}

			public Builder master(String master) {
				this.master = master;
				return this;
			}

			public TestSettings.Builder settings() {
				return builder;
			}

			Index build() {
				return new Index(this);
			}

		}
	}
}
