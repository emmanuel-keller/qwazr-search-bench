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
package com.qwazr.search.bench;

import org.apache.lucene.facet.FacetsConfig;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by ekeller on 15/02/2017.
 */
public abstract class CommonIndexer implements Consumer<TtlLineReader>, Closeable {

	private final LuceneCommonIndex luceneIndex;
	private final FacetsConfig facetsConfig;
	private final BiConsumer<TtlLineReader, LuceneRecord> converter;
	private final int batchSize;
	private final AtomicInteger totalCount;
	private volatile int nextCommit;

	CommonIndexer(final LuceneCommonIndex luceneIndex, final FacetsConfig facetsConfig,
			final BiConsumer<TtlLineReader, LuceneRecord> converter, final int batchSize) {
		this.luceneIndex = luceneIndex;
		this.facetsConfig = facetsConfig;
		this.converter = converter;
		this.batchSize = batchSize;
		totalCount = new AtomicInteger();
		nextCommit = batchSize;
	}

	final void checkCommit() {
		try {
			final int count = totalCount.get();
			if (count >= nextCommit) {
				luceneIndex.commitAndPublish();
				nextCommit = count + batchSize;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			luceneIndex.commitAndPublish();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	final protected void index(final TtlLineReader line, final LuceneRecord luceneRecord) {
		converter.accept(line, luceneRecord);
		try {
			luceneIndex.updateDocument(facetsConfig, luceneRecord);
			totalCount.incrementAndGet();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
