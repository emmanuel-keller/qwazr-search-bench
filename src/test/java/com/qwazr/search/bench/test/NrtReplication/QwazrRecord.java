package com.qwazr.search.bench.test.NrtReplication;

import com.qwazr.search.annotations.Index;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.BaseTest;
import com.qwazr.search.bench.test.FullText.ShortAbstractQwazrRecord;

public class QwazrRecord {

	@Index(name = BaseTest.INDEX_NAME, schema = BaseTest.SCHEMA_NAME)
	public static class Master extends ShortAbstractQwazrRecord {

		public Master() {
		}

		public Master(final TtlLineReader line) {
			super(line);
		}
	}

	private final static String INDEX_NAME_SLAVE = BaseTest.INDEX_NAME + "Slave";

	@Index(name = INDEX_NAME_SLAVE, schema = BaseTest.SCHEMA_NAME, replicationMaster = BaseTest.INDEX_NAME)
	public static class Slave extends ShortAbstractQwazrRecord {

		public Slave() {
		}

		public Slave(final TtlLineReader line) {
			super(line);
		}
	}
}
