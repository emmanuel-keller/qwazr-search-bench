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
package com.qwazr.search.bench.test.MultiField;

import com.qwazr.search.analysis.SmartAnalyzerSet;
import com.qwazr.search.annotations.Index;
import com.qwazr.search.annotations.IndexField;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.BaseTest;
import com.qwazr.search.field.FieldDefinition;

import java.util.ArrayList;
import java.util.List;

@Index(schema = BaseTest.SCHEMA_NAME, name = BaseTest.INDEX_NAME)
public class FullRecordNoPayload extends FullRecordBase {

	@IndexField(template = FieldDefinition.Template.TextField,
			analyzerClass = SmartAnalyzerSet.AsciiIndex.class,
			queryAnalyzerClass = SmartAnalyzerSet.AsciiQuery.class,
			stored = false)
	protected final List<String> full;

	public FullRecordNoPayload() {
		full = null;
	}

	public FullRecordNoPayload(final TtlLineReader line) {
		super(line);
		full = new ArrayList<>();
		full.add(url);
		full.add(shortAbstract);
	}
}
