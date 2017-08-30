package com.qwazr.search.bench.test.NrtReplication;

import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.bench.test.TestSettings;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static com.qwazr.search.bench.test.CommonTestSuite.currentResults;

public class NrtReplicationQwazr extends QwazrTest<QwazrRecord.Master> {

	final static Path schemaDirectory = Paths.get("data").resolve("NrtReplication");

	@Parameterized.Parameters
	public static Collection<Boolean> iterations() {
		return Arrays.asList(false);
	}

	public NrtReplicationQwazr() {
		super(QwazrRecord.Master.class);
	}

	@BeforeClass
	public static void before() throws Exception {
		QwazrTest.before(TestSettings.of(currentResults).executor(true).schemaDirectory(schemaDirectory));
	}

	public void postCheck() {
	}
	
	@Override
	final public void accept(final TtlLineReader ttlLineReader) {
		index(new QwazrRecord.Master(ttlLineReader));
	}
}
