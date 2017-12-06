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

import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.BaseTest;
import com.qwazr.search.bench.test.TestResults;
import com.qwazr.search.bench.test.TestSettings;
import com.qwazr.search.field.FieldDefinition;
import com.qwazr.search.index.QueryDefinition;
import com.qwazr.search.query.MultiFieldQuery;
import com.qwazr.search.query.QueryParserOperator;
import com.qwazr.utils.LoggerUtils;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.junit.BeforeClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MultiFieldTestNoPayload extends MultiFieldTestBase<FullRecordNoPayload> {

	static Logger LOGGER = LoggerUtils.getLogger(MultiFieldTestNoPayload.class);

	public MultiFieldTestNoPayload() {
		super(FullRecordNoPayload.class);
	}

	@BeforeClass
	public static void before() throws Exception {
		before(TestSettings.of(new TestResults())
				.executor(true)
				.index(BaseTest.INDEX_NAME)
				.useCompoundFile(false)
				.similarity(BooleanSimilarity.class)
				.settings());
	}

	@Override
	public Boolean apply(TtlLineReader ttlLineReader) {
		index(new FullRecordNoPayload(ttlLineReader));
		return true;
	}

	@Override
	protected QueryDefinition getQuery(String queryString) {

		final Map<String, Float> fieldsBoosts = new LinkedHashMap<>();
		fieldsBoosts.put(FieldDefinition.ID_FIELD, 4f);
		fieldsBoosts.put("shortAbstract", 2f);
		fieldsBoosts.put("full", 1f);

		return QueryDefinition.of(
				new MultiFieldQuery(fieldsBoosts, null, null, QueryParserOperator.AND, queryString, null, 0.001f, true))
				.queryDebug(true)
				.build();
	}

	@Override
	protected String getExplain(String queryString) {
		switch (queryString) {
		case QUERY1:
			return "(($id$:a http~2)^4.0 | (+shortAbstract:a +shortAbstract:http)^2.0 | (+full:a +full:http))~0.001";
		case QUERY2:
			return "(($id$:autism impaired social interaction~2)^4.0 | (+shortAbstract:autism +shortAbstract:impaired +shortAbstract:social +shortAbstract:interaction)^2.0 | (+full:autism +full:impaired +full:social +full:interaction))~0.001";
		}
		return null;
	}
}
