package com.qwazr.search.bench.test.MultiField;

import com.qwazr.search.analysis.SmartAnalyzerSet;
import com.qwazr.search.annotations.IndexField;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.BaseQwazrRecord;
import com.qwazr.search.field.FieldDefinition;

public class FullRecordBase extends BaseQwazrRecord {

	@IndexField(template = FieldDefinition.Template.TextField,
			analyzerClass = SmartAnalyzerSet.AsciiIndex.class,
			queryAnalyzerClass = SmartAnalyzerSet.AsciiQuery.class,
			stored = false)
	protected final String shortAbstract;

	public FullRecordBase() {
		shortAbstract = null;
	}

	public FullRecordBase(final TtlLineReader line) {
		super(line);
		shortAbstract = line.object;
	}
}
