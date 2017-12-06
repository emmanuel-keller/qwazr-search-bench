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
package com.qwazr.search.bench.test.Merging;

import com.qwazr.search.bench.IndexFiles;
import com.qwazr.search.bench.LuceneRecord;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class UpdateStringFieldTest extends UpdateFieldTest {

	protected IndexableField getField(String id) {
		return null;
	}

	protected void prepareRecord(String id, LuceneRecord.Indexable record) {
		record.reset(new Term("id", id), new StringField("id", id, Field.Store.NO), getField(id));
	}

	@Test
	final public void test() throws IOException {
		final IndexFiles emptyIndex = index.getIndexFiles();
		emptyIndex.dump("EMPTY", true);

		IndexFiles currentIndex = index.getIndexFiles();

		final LuceneRecord.Indexable record = new LuceneRecord.Indexable();

		currentIndex = indexLoop(currentIndex, "First docs", 0, DOC_COUNT, DOC_COUNT, record, this::prepareRecord,
				r -> index.updateDocument(null, record));

		currentIndex = indexLoop(currentIndex, "First docs again", 0, DOC_COUNT, DOC_COUNT, record, this::prepareRecord,
				r -> index.updateDocument(null, record));

		currentIndex = indexLoop(currentIndex, "Second docs", DOC_COUNT, DOC_COUNT * 2, DOC_COUNT * 2, record,
				this::prepareRecord, r -> index.updateDocument(null, record));

		currentIndex =
				indexLoop(currentIndex, "Half Second docs", DOC_COUNT, DOC_COUNT * 2 - (DOC_COUNT / 2), DOC_COUNT * 2,
						record, this::prepareRecord, r -> index.updateDocument(null, record));

		Assert.assertNotNull(currentIndex);
	}

}
