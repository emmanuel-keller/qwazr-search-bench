package com.qwazr.search.bench.test.NrtReplication;

import com.qwazr.search.annotations.AnnotatedIndexService;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.FullText.ShortAbstractQwazrRecord;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TestSettings;
import com.qwazr.search.index.QueryBuilder;
import com.qwazr.search.index.QueryDefinition;
import com.qwazr.search.index.ResultDefinition;
import com.qwazr.search.query.QueryParser;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static com.qwazr.search.bench.test.CommonTestSuite.currentResults;

public abstract class NrtReplicationBase extends QwazrTest<QwazrRecord.Master> {

	final static Path schemaDirectory = Paths.get("data").resolve("NrtReplication");

	@Parameterized.Parameters
	public static Collection<Boolean> iterations() {
		return Arrays.asList(false);
	}

	protected final AnnotatedIndexService<QwazrRecord.Master> master;
	protected final AnnotatedIndexService<QwazrRecord.Slave1> slave1;
	protected final AnnotatedIndexService<QwazrRecord.Slave2> slave2;

	public NrtReplicationBase() {
		super(QwazrRecord.Master.class, QwazrRecord.Slave1.class, QwazrRecord.Slave2.class);
		master = indexService;
		slave1 = (AnnotatedIndexService<QwazrRecord.Slave1>) indexServices.get(0);
		slave2 = (AnnotatedIndexService<QwazrRecord.Slave2>) indexServices.get(1);

	}

	protected QueryBuilder shortAbstractQuery(String term) {
		return QueryDefinition.of(QueryParser.of("shortAbstract")
				.setQueryString(org.apache.lucene.queryparser.classic.QueryParser.escape(term))
				.build()).returnedField("*");
	}

	void dump(String name, StatisticalSummary stat) {
		System.out.println(name + " - mean: " + (int) stat.getMean() + " - max: " + (int) stat.getMax() + " - dev: " +
				(int) stat.getStandardDeviation());
	}

	long query(AnnotatedIndexService<? extends ShortAbstractQwazrRecord> index, QueryDefinition query) {
		final long startTime = System.currentTimeMillis();
		ResultDefinition.WithObject<? extends ShortAbstractQwazrRecord> result = index.searchQuery(query);
		final long duration = System.currentTimeMillis() - startTime;
		Assert.assertNotNull(result);
		return duration;
	}

	@Override
	public void postFlush() {
		slave1.replicationCheck();
		slave2.replicationCheck();
	}

	@BeforeClass
	public static void before() throws Exception {
		QwazrTest.before(
				TestSettings.of(currentResults).highRamBuffer(true).executor(true).schemaDirectory(schemaDirectory));
	}

	@Override
	final public Boolean apply(final TtlLineReader ttlLineReader) {
		index(new QwazrRecord.Master(ttlLineReader));
		return true;
	}

}
