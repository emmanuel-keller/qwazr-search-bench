package com.qwazr.search.bench;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class IndexFiles {

	final Map<String, IndexFile> files;

	public IndexFiles(Path path) throws IOException {
		files = new LinkedHashMap<>();
		Files.list(path).forEach(p -> {
			File f = p.toFile();
			files.put(f.getName(), new IndexFile(f));
		});
	}

	public void dump() {
		files.forEach((name, item) -> System.out.println(name + ": " + item));
	}

	public class IndexFile {

		final long size;
		final long time;
		final String checksum;

		IndexFile(File file) {
			size = file.length();
			time = file.lastModified();
			try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
				checksum = DigestUtils.md5Hex(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return "size: " + size + " - time: " + time + " - checksum: " + checksum;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof IndexFile))
				return false;
			if (o == this)
				return true;
			final IndexFile i = (IndexFile) o;
			return size == i.size && time == i.time && Objects.equals(checksum, i.checksum);
		}
	}
}
