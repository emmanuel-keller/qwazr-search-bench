package com.qwazr.search.bench;

import com.qwazr.utils.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.SegmentCommitInfo;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.store.Directory;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class IndexFiles {

	final TreeMap<String, IndexFile> files;
	final long totalSize;
	final Integer numDocs;
	final Integer numDeletedDocs;

	public IndexFiles(Path path, Directory directory) throws IOException {

		try (final DirectoryReader reader = DirectoryReader.open(directory)) {
			numDocs = reader.numDocs();
			numDeletedDocs = reader.numDeletedDocs();
		}

		final Set<Path> fileSet = new HashSet<>();

		final MultivaluedMap<String, String> codecMap = new MultivaluedHashMap<>();
		for (SegmentCommitInfo segmentCommitInfo : SegmentInfos.readLatestCommit(directory)) {
			final Codec codec = segmentCommitInfo.info.getCodec();
			segmentCommitInfo.files().forEach(fileName -> {
				codecMap.add(fileName, codec.getName());
				fileSet.add(path.resolve(fileName));
			});
		}

		files = new TreeMap<>();
		fileSet.forEach(p -> {
			final File file = p.toFile();
			final IndexFile indexFile = new IndexFile(file, codecMap.get(file.getName()));
			files.put(file.getName(), indexFile);
		});
		totalSize = computeSize(files);
	}

	private IndexFiles(TreeMap<String, IndexFile> files) {
		this.files = files;
		totalSize = computeSize(files);
		numDocs = null;
		numDeletedDocs = null;
	}

	static long computeSize(Map<String, IndexFile> files) {
		long size = 0;
		for (final IndexFile file : files.values())
			size += file.size;
		return size;
	}

	public void dump(String prefix, boolean printDetail) {
		dump(prefix, totalSize, printDetail ? files : null);
	}

	public long getTotalSize() {
		return totalSize;
	}

	public Integer getNumDocs() {
		return numDocs;
	}

	public int getFileCount() {
		return files.size();
	}

	public class IndexFile {

		final long size;
		final long time;
		final String checksum;
		final List<String> codecs;

		IndexFile(final File file, final List<String> codecs) {
			this.size = file.length();
			this.time = file.lastModified();
			this.codecs = codecs;
			try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
				this.checksum = DigestUtils.md5Hex(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return "size: " + FileUtils.byteCountToDisplaySize(size) + " - time: " + time + " - codecs: " + codecs +
					" - checksum: " + checksum;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof IndexFile))
				return false;
			if (o == this)
				return true;
			final IndexFile i = (IndexFile) o;
			return size == i.size && Objects.equals(checksum, i.checksum);
		}

	}

	static void dump(String prefix, long totalSize, Map<String, IndexFile> files) {
		if (files != null)
			System.out.println();
		System.out.println(prefix + " - totalSize: " + FileUtils.byteCountToDisplaySize(totalSize));
		if (files != null)
			files.forEach((name, item) -> System.out.println(name + ": " + item));
	}

	public Updated getUpdatedFiles(IndexFiles previousFiles) {
		final TreeMap<String, IndexFile> modifiedFiles = new TreeMap<>();
		final TreeMap<String, IndexFile> newFiles = new TreeMap<>();
		files.forEach((name, file) -> {
			final IndexFile previousFile = previousFiles.files.get(name);
			if (previousFile != null) {
				if (previousFile.equals(file))
					return;
				modifiedFiles.put(name, file);
			} else
				newFiles.put(name, file);
		});
		return new Updated(modifiedFiles, newFiles);
	}

	public class Updated {

		final TreeMap<String, IndexFile> modifiedFiles;
		final TreeMap<String, IndexFile> newFiles;
		final long modifiedFilesSize;
		final long newFilesSize;
		final long totalSize;

		private Updated(final TreeMap<String, IndexFile> modifiedFiles, final TreeMap<String, IndexFile> newFiles) {
			this.modifiedFiles = modifiedFiles;
			this.newFiles = newFiles;
			modifiedFilesSize = computeSize(modifiedFiles);
			newFilesSize = computeSize(newFiles);
			totalSize = modifiedFilesSize + newFilesSize;
		}

		public int getNewFiles() {
			return newFiles.size();
		}

		public int getModifiedFiles() {
			return modifiedFiles.size();
		}

		public void dump(String prefix) {
			if (modifiedFiles.size() > 0)
				IndexFiles.dump(prefix + " - Modified files", modifiedFilesSize, modifiedFiles);
			if (newFiles.size() > 0)
				IndexFiles.dump(prefix + " - New files", newFilesSize, newFiles);
		}

		public long getTotalSize() {
			return totalSize;
		}

		public int getFileCount() {
			return getNewFiles() + getModifiedFiles();
		}
	}

}
