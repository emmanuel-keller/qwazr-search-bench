package com.qwazr.search.bench.test;

/**
 * Created by ekeller on 15/02/2017.
 */
public class TestSettings {

	final boolean taxonomy;

	final boolean executor;

	final boolean warmup;

	final TestResults results;

	private TestSettings(Builder builder) {
		this.results = builder.results;
		this.taxonomy = builder.taxonomy;
		this.executor = builder.executor;
		this.warmup = builder.warmup;
	}

	@Override
	public String toString() {
		return "SETTINGS - Warmup: " + warmup + " - Executor: " + executor + " - Taxonomy: " + taxonomy;
	}

	public static Builder of(TestResults results) {
		return new Builder(results);
	}

	public static class Builder {

		private Boolean taxonomy = null;

		private Boolean executor = null;

		private Boolean warmup = null;

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

		public Builder warmup(boolean warmup) {
			this.warmup = warmup;
			return this;
		}

		public TestSettings build() {
			return new TestSettings(this);
		}

	}

}
