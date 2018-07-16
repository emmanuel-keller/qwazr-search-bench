package com.qwazr.search.bench.test.NrtReplication;

import com.qwazr.search.annotations.AnnotatedIndexService;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.BaseTest;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TestSettings;
import com.qwazr.search.index.QueryBuilder;
import com.qwazr.search.index.QueryDefinition;
import com.qwazr.search.index.ResultDefinition;
import com.qwazr.search.query.MultiFieldQueryParser;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public abstract class NrtReplicationBase extends QwazrTest<NrtReplicationRecord> {

    final static Path schemaDirectory = Paths.get("data").resolve("NrtReplication");

    @Parameterized.Parameters
    public static Collection<Boolean> iterations() {
        return Arrays.asList(false);
    }

    private final QueryDefinition query1 = shortAbstractQuery("a the an then who when where what").build();
    private final QueryDefinition query2 = shortAbstractQuery("is as be to will can have has").build();

    private final SummaryStatistics statsMasterQuery1 = new SummaryStatistics();
    private final SummaryStatistics statsSlave1Query1 = new SummaryStatistics();
    private final SummaryStatistics statsSlave2Query1 = new SummaryStatistics();

    private final SummaryStatistics statsMasterQuery2 = new SummaryStatistics();
    private final SummaryStatistics statsSlave1Query2 = new SummaryStatistics();
    private final SummaryStatistics statsSlave2Query2 = new SummaryStatistics();

    final AnnotatedIndexService<NrtReplicationRecord> master;
    final AnnotatedIndexService<NrtReplicationRecord> slave1;
    final AnnotatedIndexService<NrtReplicationRecord> slave2;

    public NrtReplicationBase() {
        super(NrtReplicationRecord.class);
        master = indexService;
        slave1 = indexServices.get(1);
        slave2 = indexServices.get(2);

    }

    public static void before(String masterName, TestSettings.Builder settingsBuilder) throws Exception {
        QwazrTest.before(settingsBuilder.index("SlaveNoWarmer")
                .master(BaseTest.SCHEMA_NAME + '/' + masterName)
                .useWarmer(false)
                .settings()
                .index("SlaveWithWarmer")
                .master(BaseTest.SCHEMA_NAME + '/' + masterName)
                .useWarmer(true)
                .settings());
    }

    protected QueryBuilder shortAbstractQuery(String term) {
        return QueryDefinition.of(MultiFieldQueryParser.of()
                .addField("shortAbstract", "shortAbstractEn")
                .addBoost("shortAbstract", 2.0f)
                .addBoost("shortAbstractEn", 1.0f)
                .setQueryString(org.apache.lucene.queryparser.classic.QueryParser.escape(term))
                .build()).returnedField("*");
    }

    void dump(String name, StatisticalSummary stat) {
        System.out.println(name + " - mean: " + (int) stat.getMean() + " - max: " + (int) stat.getMax() + " - dev: " +
                (int) stat.getStandardDeviation());
    }

    long query(AnnotatedIndexService<NrtReplicationRecord> index, QueryDefinition query) {
        final long startTime = System.currentTimeMillis();
        final ResultDefinition.WithObject<NrtReplicationRecord> result = index.searchQuery(query);
        final long duration = System.currentTimeMillis() - startTime;
        Assert.assertNotNull(result);
        return duration;
    }

    @Override
    final public Boolean apply(final TtlLineReader ttlLineReader) {
        index(new NrtReplicationRecord(ttlLineReader));
        return true;
    }

    @Override
    public void preTest() throws ExecutionException, InterruptedException {
        slave1.replicationCheck();
        slave2.replicationCheck();

        query(master, query1);
        query(slave1, query1);
        query(slave2, query1);

        query(master, query2);
        query(slave1, query2);
        query(slave2, query2);
    }

    @Override
    public void postCheck() {
    }

    @Override
    public void postFlush() throws ExecutionException, InterruptedException {

        if (flushCount.get() % 10 != 0)
            return;

        slave1.replicationCheck();
        slave2.replicationCheck();

        statsMasterQuery1.addValue(query(master, query1));
        statsSlave1Query1.addValue(query(slave1, query1));
        statsSlave2Query1.addValue(query(slave2, query1));

        statsMasterQuery2.addValue(query(master, query2));
        statsSlave1Query2.addValue(query(slave1, query2));
        statsSlave2Query2.addValue(query(slave2, query2));

        System.out.println("FLUSHED: " + indexedDocumentsCount.get());
        System.out.println("-----");
        dump("Master Query1", statsMasterQuery1);
        dump("Slave1 Query1", statsSlave1Query1);
        dump("Slave2 Query1", statsSlave2Query1);
        System.out.println("-----");
        dump("Master Query2", statsMasterQuery2);
        dump("Slave1 Query2", statsSlave1Query2);
        dump("Slave2 Query2", statsSlave2Query2);
        System.out.println();

    }

}
