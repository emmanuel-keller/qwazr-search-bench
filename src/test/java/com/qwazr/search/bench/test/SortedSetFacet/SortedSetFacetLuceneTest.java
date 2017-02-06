/**
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
package com.qwazr.search.bench.test.SortedSetFacet;

import com.qwazr.search.bench.test.LuceneRecord;
import com.qwazr.search.bench.test.LuceneTest;
import com.qwazr.search.bench.test.TtlLineReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.URISyntaxException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class SortedSetFacetLuceneTest extends LuceneTest<LuceneRecord> {

	final static String URL = "url";
	final static String PREDICATE = "predicate";

	final static FacetsConfig FACETS_CONFIG = new FacetsConfig();

	static {
		FACETS_CONFIG.setMultiValued(PREDICATE, true);
	}

	public SortedSetFacetLuceneTest() throws IOException, URISyntaxException {
		super(SHORT_ABSTRACT_FILE, BATCH_SIZE, LIMIT);
	}

	@Override
	final public LuceneRecord apply(final TtlLineReader lineReader) {
		try {
			final BytesRef termBytesRef = new BytesRef(lineReader.subject);
			final Term termId = new Term(URL, termBytesRef);
			final Document doc = new Document();
			doc.add(new StringField(URL, termBytesRef, Field.Store.NO));
			doc.add(new SortedSetDocValuesFacetField(PREDICATE, lineReader.predicate));
			return new LuceneRecord(termId, FACETS_CONFIG.build(doc));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
