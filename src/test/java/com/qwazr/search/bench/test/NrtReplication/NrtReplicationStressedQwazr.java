package com.qwazr.search.bench.test.NrtReplication;

import com.qwazr.search.annotations.AnnotatedIndexService;
import com.qwazr.search.bench.TtlLineReader;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Before;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class NrtReplicationStressedQwazr extends NrtReplicationMasterNoCfs {

	private final AtomicBoolean running = new AtomicBoolean(true);

	private Future<StatisticalSummary> requesterMaster, requesterSlave1, requesterSlave2;

	@Before
	public void startQuery() {
		requesterMaster = executor.submit(new Requester(master));
		requesterSlave1 = executor.submit(new Requester(slave1));
		requesterSlave2 = executor.submit(new Requester(slave2));
	}

	@Override
	public void postCheck() {
		super.postCheck();
		running.set(false);
		try {
			dump("MASTER", requesterMaster.get());
			dump("SLAVE1", requesterSlave1.get());
			dump("SLAVE2", requesterSlave2.get());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	class Requester implements Callable<StatisticalSummary>, Function<TtlLineReader, Boolean> {

		private final SummaryStatistics stats = new SummaryStatistics();
		private final AnnotatedIndexService<NrtReplicationRecord> service;

		private int count;

		Requester(AnnotatedIndexService<NrtReplicationRecord> service) {
			this.service = service;
			this.count = 0;
		}

		@Override
		public StatisticalSummary call() throws Exception {
			loader.load(currentSettings.limit, this);
			return stats;
		}

		@Override
		public Boolean apply(TtlLineReader ttlLineReader) {
			final long duration = query(service, shortAbstractQuery(ttlLineReader.subject).build());
			if (count++ > 0)
				stats.addValue(duration);
			return running.get();
		}
	}
}
