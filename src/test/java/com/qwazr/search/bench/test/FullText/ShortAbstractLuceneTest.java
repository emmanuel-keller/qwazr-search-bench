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
package com.qwazr.search.bench.test.FullText;

import com.qwazr.search.bench.LuceneRecord;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.LuceneTest;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class ShortAbstractLuceneTest extends LuceneTest {

	final static String URL = "url";
	final static String SHORT_ABSTRACT = "shortAbstract";

	@Override
	final public void accept(final TtlLineReader lineReader, final LuceneRecord record) {
		final BytesRef termBytesRef = new BytesRef(lineReader.subject);
		record.reset(new Term(URL, termBytesRef));
		record.document.add(new StringField(URL, termBytesRef, Field.Store.NO));
		record.document.add(new TextField(SHORT_ABSTRACT, lineReader.object, Field.Store.NO));
	}

}
