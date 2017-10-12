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
import com.qwazr.search.query.MultiFieldQuery;
import com.qwazr.search.query.QueryParserOperator;
import com.qwazr.utils.LoggerUtils;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.junit.Assert;
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
				.similarity(BooleanSimilarity.class)
				.settings());
	}

	@Override
	public Boolean apply(TtlLineReader ttlLineReader) {
		index(new FullRecordNoPayload(ttlLineReader));
		return true;
	}

	@Override
	protected void postTest() {
		final Map<String, Float> fieldsBoosts = new LinkedHashMap<>();
		fieldsBoosts.put(FieldDefinition.ID_FIELD, 4f);
		fieldsBoosts.put("shortAbstract", 2f);
		fieldsBoosts.put("full", 1f);
		final QueryDefinition query = QueryDefinition.of(
				new MultiFieldQuery(fieldsBoosts, null, null, QueryParserOperator.AND, "a the", null, 0.001f))
				.queryDebug(true)
				.build();

		ResultDefinition.WithObject<FullRecordNoPayload> result = indexService.searchQuery(query);

		Assert.assertEquals(
				"(($id$:a the~2)^4.0 | (+shortAbstract:a +shortAbstract:the)^2.0 | (+full:a +full:the))~0.001",
				result.query);
		LOGGER.info("Max score: " + result.getMaxScore() + " - Total hits: " + result.getTotalHits() + " - Time: " +
				result.timer.totalTime);

		System.out.println(indexService.explainQueryDot(query, result.documents.get(0).doc, 80));
	}
}
