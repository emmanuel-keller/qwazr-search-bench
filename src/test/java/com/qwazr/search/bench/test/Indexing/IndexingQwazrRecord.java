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
package com.qwazr.search.bench.test.Indexing;

import com.qwazr.search.annotations.Index;
import com.qwazr.search.annotations.IndexField;
import com.qwazr.search.bench.TtlLineReader;
import com.qwazr.search.bench.test.BaseQwazrRecord;
import com.qwazr.search.bench.test.BaseTest;
import com.qwazr.search.field.FieldDefinition;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * Created by ekeller on 01/01/2017.
 */
public abstract class IndexingQwazrRecord extends BaseQwazrRecord {

	@IndexField(template = FieldDefinition.Template.TextField, analyzerClass = StandardAnalyzer.class)
	final String shortAbstract;

	IndexingQwazrRecord() {
		shortAbstract = null;
	}

	IndexingQwazrRecord(final TtlLineReader line) {
		super(line);
		shortAbstract = line.object;
	}

	@Index(name = BaseTest.INDEX_NAME, schema = BaseTest.SCHEMA_NAME)
	public static class DefaultRamBuffer extends IndexingQwazrRecord {

		public DefaultRamBuffer() {
		}

		DefaultRamBuffer(final TtlLineReader line) {
			super(line);
		}
	}

	@Index(name = BaseTest.INDEX_NAME, schema = BaseTest.SCHEMA_NAME, ramBufferSize = 2048)
	public static class HighRamBuffer extends IndexingQwazrRecord {

		public HighRamBuffer() {
		}

		HighRamBuffer(final TtlLineReader line) {
			super(line);
		}
	}
}
