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

/**
 * Created by ekeller on 15/02/2017.
 */
public class TestSettings {

	public static final double HIGH_RAM_BUFFER = 2048;

	final boolean taxonomy;

	final boolean executor;

	final boolean highRamBuffer;

	final TestResults results;

	private TestSettings(Builder builder) {
		this.results = builder.results;
		this.taxonomy = builder.taxonomy == null ? false : builder.taxonomy;
		this.executor = builder.executor;
		this.highRamBuffer = builder.highRamBuffer == null ? false : builder.highRamBuffer;
	}

	double getRamBuffer() {
		return highRamBuffer ? HIGH_RAM_BUFFER : IndexWriterConfig.DEFAULT_RAM_BUFFER_SIZE_MB;
	}

	@Override
	public String toString() {
		return "SETTINGS - Executor: " + executor + " - Taxonomy: " + taxonomy + " - RamBuffer: " + getRamBuffer() +
				"MB";
	}

	public static Builder of(TestResults results) {
		return new Builder(results);
	}

	public static class Builder {

		private Boolean taxonomy = null;

		private Boolean executor = null;

		private Boolean highRamBuffer = null;

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

		public TestSettings build() {
			return new TestSettings(this);
		}

	}

}
