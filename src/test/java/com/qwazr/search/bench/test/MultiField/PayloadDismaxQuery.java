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

import com.qwazr.search.index.QueryContext;
import com.qwazr.search.query.AbstractQuery;
import com.qwazr.search.query.QueryParserOperator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.payloads.MaxPayloadFunction;
import org.apache.lucene.queries.payloads.PayloadScoreQuery;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.QueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final public class PayloadDismaxQuery extends AbstractQuery<PayloadDismaxQuery> {

	private final QueryParserOperator operator;
	private final String queryString;
	private final String[] fields;
	private final float tieBreak;

	protected PayloadDismaxQuery(QueryParserOperator operator, String queryString, Float tieBreak, String... fields) {
		super(PayloadDismaxQuery.class);
		this.operator = operator;
		this.queryString = queryString;
		this.fields = fields;
		this.tieBreak = tieBreak;
	}

	@Override
	public Query getQuery(QueryContext queryContext)
			throws IOException, ParseException, QueryNodeException, ReflectiveOperationException {
		final List<Query> queries = new ArrayList<>();
		final Analyzer analyzer = queryContext.getQueryAnalyzer();
		final BooleanClause.Occur occur = operator == null || operator == QueryParserOperator.AND ?
				BooleanClause.Occur.MUST :
				BooleanClause.Occur.SHOULD;
		for (String field : fields)
			queries.add(new PayloadQueryBuilder(analyzer).createBooleanQuery(field, queryString, occur));
		return new DisjunctionMaxQuery(queries, tieBreak);
	}

	@Override
	protected boolean isEqual(PayloadDismaxQuery query) {
		return Arrays.equals(fields, query.fields);
	}

	public static class PayloadQueryBuilder extends QueryBuilder {

		PayloadQueryBuilder(Analyzer analyzer) {
			super(analyzer);
		}

		protected Query newTermQuery(Term term) {
			return new PayloadScoreQuery(new SpanTermQuery(term), new MaxPayloadFunction(), false);
		}
	}
}
