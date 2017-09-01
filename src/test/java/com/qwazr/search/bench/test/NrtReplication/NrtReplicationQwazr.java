package com.qwazr.search.bench.test.NrtReplication;

import com.qwazr.search.annotations.AnnotatedIndexService;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TestSettings;
import com.qwazr.search.index.QueryDefinition;
import com.qwazr.search.index.ResultDefinition;
import com.qwazr.search.query.QueryParser;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.qwazr.search.bench.test.CommonTestSuite.currentResults;

public class NrtReplicationQwazr extends QwazrTest<QwazrRecord.Master> {

	final static Path schemaDirectory = Paths.get("data").resolve("NrtReplication");

	@Parameterized.Parameters
	public static Collection<Boolean> iterations() {
		return Arrays.asList(false);
	}

	final AtomicBoolean running = new AtomicBoolean(true);

	Future<StatisticalSummary> requester1, requester2;

	public NrtReplicationQwazr() {
		super(QwazrRecord.Master.class, QwazrRecord.Slave1.class, QwazrRecord.Slave2.class);
	}

	@BeforeClass
	public static void before() throws Exception {
		QwazrTest.before(TestSettings.of(currentResults).executor(true).schemaDirectory(schemaDirectory));
	}

	@Before
	public void startQuery() {
		requester1 = executor.submit(new Requester((AnnotatedIndexService<QwazrRecord>) indexServices.get(0)));
		requester2 = executor.submit(new Requester((AnnotatedIndexService<QwazrRecord>) indexServices.get(1)));
	}

	@Override
	public void postFlush() {
		indexServices.get(0).replicationCheck();
		indexServices.get(1).replicationCheck();
	}

	@Override
	public void postCheck() {
		running.set(false);
		try {
			System.out.println(requester1.get());
			System.out.println(requester2.get());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	final public void accept(final TtlLineReader ttlLineReader) {
		index(new QwazrRecord.Master(ttlLineReader));
	}

	class Requester implements Callable<StatisticalSummary>, Consumer<TtlLineReader> {

		private final SummaryStatistics stats = new SummaryStatistics();
		private final AnnotatedIndexService<QwazrRecord> service;

		Requester(AnnotatedIndexService<QwazrRecord> service) {
			this.service = service;
		}

		@Override
		public StatisticalSummary call() throws Exception {
			loader.load(currentSettings.limit, this);
			return stats;
		}

		@Override
		public void accept(TtlLineReader ttlLineReader) {
			final long time = System.currentTimeMillis();
			ResultDefinition.WithObject.WithObject<QwazrRecord> result = service.searchQuery(QueryDefinition.of(
					QueryParser.of("shortAbstract")
							.setQueryString(
									org.apache.lucene.queryparser.classic.QueryParser.escape(ttlLineReader.subject))
							.build()).returnedField("*").build());
			stats.addValue(System.currentTimeMillis() - time);
		}
	}
}
