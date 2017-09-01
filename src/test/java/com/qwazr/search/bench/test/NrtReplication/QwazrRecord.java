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

	@Index(name = BaseTest.INDEX_NAME + "Slave1",
			schema = BaseTest.SCHEMA_NAME,
			replicationMaster = BaseTest.SCHEMA_NAME + '/' + BaseTest.INDEX_NAME,
			indexReaderWarmer = false)
	public static class Slave1 extends ShortAbstractQwazrRecord {

		public Slave1() {
		}

		public Slave1(final TtlLineReader line) {
			super(line);
		}
	}

	@Index(name = BaseTest.INDEX_NAME + "Slave2",
			schema = BaseTest.SCHEMA_NAME,
			replicationMaster = BaseTest.SCHEMA_NAME + '/' + BaseTest.INDEX_NAME,
			indexReaderWarmer = true)
	public static class Slave2 extends ShortAbstractQwazrRecord {

		public Slave2() {
		}

		public Slave2(final TtlLineReader line) {
			super(line);
		}
	}
}
