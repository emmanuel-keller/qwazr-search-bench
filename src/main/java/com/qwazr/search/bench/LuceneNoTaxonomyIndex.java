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

import com.qwazr.utils.IOUtils;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.replicator.IndexRevision;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Created by ekeller on 01/01/2017.
 */
public class LuceneNoTaxonomyIndex extends LuceneCommonIndex {

	private final SearcherManager searcherManager;

	public LuceneNoTaxonomyIndex(final Path rootDirectory, final String schemaName, final String indexName,
			final ExecutorService executorService, final double ramBufferSize) throws IOException {
		super(rootDirectory, schemaName, indexName, ramBufferSize);
		searcherManager = new SearcherManager(this.indexWriter,
				executorService == null ? new SearcherFactory() : new MultiThreadSearcherFactory(executorService));
	}

	@Override
	final synchronized public void commitAndPublish() throws IOException {
		if (!indexWriter.hasUncommittedChanges())
			return;
		indexWriter.flush();
		indexWriter.commit();
		searcherManager.maybeRefresh();
		localReplicator.publish(new IndexRevision(indexWriter));
	}

	@Override
	final public <T> T search(final FunctionEx<IndexSearcher, T, IOException> search) throws IOException {
		final IndexSearcher indexSearcher = searcherManager.acquire();
		try {
			return search.apply(indexSearcher);
		} finally {
			searcherManager.release(indexSearcher);
		}
	}

	@Override
	final public void updateDocument(final FacetsConfig facetsConfig, final LuceneRecord record) throws IOException {
		indexWriter.updateDocument(record.termId,
				facetsConfig == null ? record.document : facetsConfig.build(record.document));
	}

	@Override
	public void close() throws IOException {
		IOUtils.close(searcherManager, indexWriter, dataDirectory);
	}

}
