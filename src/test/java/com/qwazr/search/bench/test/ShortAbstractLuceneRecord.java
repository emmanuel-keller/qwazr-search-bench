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
package com.qwazr.search.bench.test;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.Term;

import java.io.IOException;

/**
 * Created by ekeller on 01/01/2017.
 */
public class ShortAbstractLuceneRecord {

	final static String URL = "url";
	final static String PREDICATE = "predicate";
	final static String SHORT_ABSTRACT = "shortAbstract";

	final Term termId;
	final Document document;

	ShortAbstractLuceneRecord(final TtlLineReader lineReader) throws IOException {
		termId = new Term(URL, lineReader.subject);
		Document doc = new Document();
		doc.add(new StringField(URL, termId.bytes(), Field.Store.NO));
		doc.add(new SortedSetDocValuesFacetField(PREDICATE, lineReader.predicate));
		doc.add(new TextField(SHORT_ABSTRACT, lineReader.object, Field.Store.NO));
		document = FACETS_CONFIG.build(doc);
	}

	final static FacetsConfig FACETS_CONFIG = new FacetsConfig();

	static {
		FACETS_CONFIG.setMultiValued(PREDICATE, false);
	}
}
