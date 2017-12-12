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
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class UpdateNumericDocValuesTest extends UpdateFieldTest {

	protected Field getField(String id) {
		return new DoubleDocValuesField("dv", id.hashCode());
	}

	protected void prepareRecord(String id, LuceneRecord.Indexable record) {
		record.reset(new Term("id", id), new StringField("id", id, Field.Store.NO), getField(id));
	}

	protected void prepareSameRecord(String id, LuceneRecord.DocValues record) {
		record.reset(new Term("id", id), getField(id));
	}

	@Test
	final public void test() throws IOException {
		final IndexFiles emptyIndex = index.getIndexFiles();
		emptyIndex.dump("EMPTY", true);

		IndexFiles currentIndex = index.getIndexFiles();

		final LuceneRecord.Indexable indexableRecord = new LuceneRecord.Indexable();

		currentIndex = indexLoop(currentIndex, "First docs (updateDocument)", 0, DOC_COUNT, DOC_COUNT, indexableRecord,
				this::prepareRecord, r -> index.updateDocument(null, indexableRecord));

		final LuceneRecord.DocValues docValuesRecord = new LuceneRecord.DocValues();

		currentIndex = indexLoop(currentIndex, "First docs (updateDocValues)", 0, DOC_COUNT, DOC_COUNT, docValuesRecord,
				this::prepareSameRecord, r -> index.updateDocValues(docValuesRecord));

		currentIndex = indexLoop(currentIndex, "Half first docs (updateDocValues)", 0, DOC_COUNT / 2, DOC_COUNT,
				docValuesRecord, this::prepareSameRecord, r -> index.updateDocValues(docValuesRecord));

		currentIndex = indexLoop(currentIndex, "Second docs (updateDocument)", DOC_COUNT, DOC_COUNT * 2, DOC_COUNT * 2,
				indexableRecord, this::prepareRecord, r -> index.updateDocument(null, indexableRecord));

		currentIndex =
				indexLoop(currentIndex, "Half Second docs (updateDocument)", DOC_COUNT, DOC_COUNT * 2 - (DOC_COUNT / 2),
						DOC_COUNT * 2, indexableRecord, this::prepareRecord,
						r -> index.updateDocument(null, indexableRecord));

		currentIndex = indexLoop(currentIndex, "Quarter Second docs (updateDocument)", DOC_COUNT * 2 - (DOC_COUNT / 2),
				DOC_COUNT * 2 - (DOC_COUNT / 4), DOC_COUNT * 2, indexableRecord, this::prepareRecord,
				r -> index.updateDocument(null, indexableRecord));

		Assert.assertNotNull(currentIndex);
	}
}
