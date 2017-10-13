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

import com.qwazr.search.bench.test.BaseQwazrRecord;
import com.qwazr.search.bench.test.QwazrTest;
import com.qwazr.search.index.QueryDefinition;
import com.qwazr.search.index.ResultDefinition;
import com.qwazr.utils.LoggerUtils;
import org.junit.Assert;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

public abstract class MultiFieldTestBase<T extends BaseQwazrRecord> extends QwazrTest<T> {

	static Logger LOGGER = LoggerUtils.getLogger(MultiFieldTestBase.class);

	// Based on wikipedia short abstract content
	static final String QUERY1 = "a http";
	static final String QUERY2 = "autism impaired social interaction";
	static final String[] QUERY_STRINGS = { QUERY1, QUERY2 };

	@Parameterized.Parameters
	public static Collection<Boolean> iterations() {
		return Arrays.asList(false);
	}

	protected MultiFieldTestBase(Class<T> recordClass) {
		super(recordClass);
	}

	protected abstract QueryDefinition getQuery(String queryString);

	protected abstract String getExplain(String queryString);

	void checkQuery(QueryDefinition query, String expectedExplain) {

		final ResultDefinition.WithObject<T> result = indexService.searchQuery(query);

		Assert.assertEquals(expectedExplain, result.query);

		LOGGER.info("Max score: " + result.getMaxScore() + " - Total hits: " + result.getTotalHits() + " - Time: " +
				result.timer.totalTime);

		System.out.println(indexService.explainQueryDot(query, result.documents.get(0).doc, 80));
	}

	@Override
	final protected void postTest() {
		for (String queryString : QUERY_STRINGS)
			checkQuery(getQuery(queryString), getExplain(queryString));
	}
}
