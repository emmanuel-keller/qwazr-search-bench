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
import com.qwazr.search.query.QueryParserOperator;
import org.junit.BeforeClass;

public class MultiFieldTestWithPayload extends MultiFieldTestBase<FullRecordWithPayload> {

	public MultiFieldTestWithPayload() {
		super(FullRecordWithPayload.class);
	}

	@BeforeClass
	public static void before() throws Exception {
		before(TestSettings.of(new TestResults())
				.executor(true)
				.index(BaseTest.INDEX_NAME)
				.useCompoundFile(false)
				.similarity(BooleanPayloadSimilarity.class)
				.settings());
	}

	@Override
	public Boolean apply(TtlLineReader ttlLineReader) {
		index(new FullRecordWithPayload(ttlLineReader));
		return true;
	}

	@Override
	protected String getExplain(String queryString) {
		switch (queryString) {
		case QUERY1:
			return "(PayloadScoreQuery($id$:a http, function: MaxPayloadFunction, includeSpanScore: true) | (+PayloadScoreQuery(shortAbstract:a, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(shortAbstract:http, function: MaxPayloadFunction, includeSpanScore: true)) | (+PayloadScoreQuery(full:a, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(full:http, function: MaxPayloadFunction, includeSpanScore: true)))";
		case QUERY2:
			return "(PayloadScoreQuery($id$:autism impaired social interaction, function: MaxPayloadFunction, includeSpanScore: true) | (+PayloadScoreQuery(shortAbstract:autism, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(shortAbstract:impaired, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(shortAbstract:social, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(shortAbstract:interaction, function: MaxPayloadFunction, includeSpanScore: true)) | (+PayloadScoreQuery(full:autism, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(full:impaired, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(full:social, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(full:interaction, function: MaxPayloadFunction, includeSpanScore: true)))";
		}
		return null;
	}

	@Override
	protected QueryDefinition getQuery(String queryString) {
		return QueryDefinition.of(
				new PayloadDismaxQuery(QueryParserOperator.AND, queryString, 0f, FieldDefinition.ID_FIELD,
						"shortAbstract", "full")).queryDebug(true).build();
	}

}
