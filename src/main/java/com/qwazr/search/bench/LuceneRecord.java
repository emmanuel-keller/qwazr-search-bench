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
package com.qwazr.search.bench;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekeller on 01/01/2017.
 */
abstract public class LuceneRecord {

	public Term termId;

	final static public class Indexable extends LuceneRecord {

		public final Document document;

		public Indexable() {
			this.document = new Document();
		}

		final public void reset(final Term termId, final IndexableField... fields) {
			this.termId = termId;
			document.clear();
			if (fields != null)
				for (IndexableField field : fields)
					if (field != null)
						document.add(field);
		}

	}

	final static public class DocValues extends LuceneRecord {

		public final List<Field> docValuesFields;

		public DocValues() {
			this.docValuesFields = new ArrayList<>();
		}

		final public void reset(final Term termId, final Field... fields) {
			this.termId = termId;
			docValuesFields.clear();
			if (fields != null)
				for (Field field : fields)
					if (field != null)
						docValuesFields.add(field);
		}
	}

}
