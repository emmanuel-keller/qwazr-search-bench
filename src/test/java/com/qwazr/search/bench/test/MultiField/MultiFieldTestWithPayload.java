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
import com.qwazr.search.index.ResultDefinition;
import com.qwazr.search.query.QueryParserOperator;
import com.qwazr.utils.LoggerUtils;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.logging.Logger;

public class MultiFieldTestWithPayload extends MultiFieldTestBase<FullRecordWithPayload> {

	static Logger LOGGER = LoggerUtils.getLogger(MultiFieldTestWithPayload.class);

	public MultiFieldTestWithPayload() {
		super(FullRecordWithPayload.class);
	}

	@BeforeClass
	public static void before() throws Exception {
		before(TestSettings.of(new TestResults())
				.executor(true)
				.index(BaseTest.INDEX_NAME)
				.similarity(BooleanPayloadSimilarity.class)
				.settings());
	}

	@Override
	public Boolean apply(TtlLineReader ttlLineReader) {
		index(new FullRecordWithPayload(ttlLineReader));
		return true;
	}

	private final static String EXPLAIN_DISMAX_WITH_SPAN =
			"(PayloadScoreQuery($id$:a http, function: MaxPayloadFunction, includeSpanScore: true) | (+PayloadScoreQuery(shortAbstract:a, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(shortAbstract:http" +
					", function: MaxPayloadFunction, includeSpanScore: true)) | (+PayloadScoreQuery(full:a, function: MaxPayloadFunction, includeSpanScore: true) +PayloadScoreQuery(full:http, function: MaxPayloadFunction, includeSpanScore: true)))";

	private final static String EXPLAIN_DISMAX_WITHOUT_SPAN =
			"(PayloadScoreQuery($id$:a http, function: MaxPayloadFunction, includeSpanScore: false) | (+PayloadScoreQuery(shortAbstract:a, function: MaxPayloadFunction, includeSpanScore: false) +PayloadScoreQuery(shortAbstract:http, function: MaxPayloadFunction, includeSpanScore: false)) | (+PayloadScoreQuery(full:a, function: MaxPayloadFunction, includeSpanScore: false) +PayloadScoreQuery(full:http, function: MaxPayloadFunction, includeSpanScore: false)))";

	@Override
	protected void postTest() {
		QueryDefinition query = QueryDefinition.of(
				new PayloadDismaxQuery(QueryParserOperator.AND, "a http", 0f, FieldDefinition.ID_FIELD, "shortAbstract",
						"full")).queryDebug(true).build();

		ResultDefinition.WithObject<FullRecordWithPayload> result = indexService.searchQuery(query);

		Assert.assertEquals(EXPLAIN_DISMAX_WITH_SPAN, result.query);
		LOGGER.info("Max score: " + result.getMaxScore() + " - Total hits: " + result.getTotalHits() + " - Time: " +
				result.timer.totalTime);

		System.out.println(indexService.explainQueryDot(query, result.documents.get(0).doc, 80));
	}
}
