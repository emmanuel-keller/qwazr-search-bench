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

import com.qwazr.utils.IOUtils;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager;
import org.apache.lucene.replicator.IndexAndTaxonomyRevision;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Created by ekeller on 01/01/2017.
 */
public class LuceneWithTaxonomyIndex extends LuceneCommonIndex {

	private final Directory taxonomyDirectory;
	private final IndexAndTaxonomyRevision.SnapshotDirectoryTaxonomyWriter taxonomyWriter;
	private final SearcherTaxonomyManager searcherTaxonomyManager;

	public LuceneWithTaxonomyIndex(final Path rootDirectory, final String schemaName, final String indexName,
			final ExecutorService executorService, final double ramBufferSize) throws IOException {
		super(rootDirectory, schemaName, indexName, ramBufferSize);

		this.taxonomyDirectory = FSDirectory.open(indexDirectory.resolve("taxonomy"));
		this.taxonomyWriter = new IndexAndTaxonomyRevision.SnapshotDirectoryTaxonomyWriter(taxonomyDirectory);
		searcherTaxonomyManager = new SearcherTaxonomyManager(this.indexWriter, true,
				executorService == null ? new SearcherFactory() : new MultiThreadSearcherFactory(executorService),
				this.taxonomyWriter);
	}

	@Override
	final synchronized public void commitAndPublish() throws IOException {
		final boolean hasTaxoChanges = taxonomyWriter.getIndexWriter().hasUncommittedChanges();
		if (hasTaxoChanges)
			taxonomyWriter.commit();
		final boolean hasDataChanges = indexWriter.hasUncommittedChanges();
		if (hasDataChanges)
			indexWriter.commit();
		if (hasTaxoChanges || hasDataChanges) {
			searcherTaxonomyManager.maybeRefresh();
			localReplicator.publish(new IndexAndTaxonomyRevision(indexWriter, taxonomyWriter));
		}
	}

	@Override
	final public <T> T search(final FunctionEx<IndexSearcher, T, IOException> search) throws IOException {
		final SearcherTaxonomyManager.SearcherAndTaxonomy searcherAndTaxonomy = searcherTaxonomyManager.acquire();
		try {
			return search.apply(searcherAndTaxonomy.searcher);
		} finally {
			searcherTaxonomyManager.release(searcherAndTaxonomy);
		}
	}

	@Override
	final public void updateDocument(final FacetsConfig facetsConfig, final LuceneRecord record) throws IOException {
		indexWriter.updateDocument(record.termId, facetsConfig.build(taxonomyWriter, record.document));
	}

	@Override
	public void close() throws IOException {
		IOUtils.close(searcherTaxonomyManager, taxonomyWriter, indexWriter, taxonomyDirectory, dataDirectory);
	}

}
