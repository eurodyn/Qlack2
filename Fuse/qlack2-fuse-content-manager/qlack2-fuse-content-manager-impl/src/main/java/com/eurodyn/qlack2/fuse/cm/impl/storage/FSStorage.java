package com.eurodyn.qlack2.fuse.cm.impl.storage;

import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;
import com.eurodyn.qlack2.fuse.cm.api.storage.QStorageException;
import com.eurodyn.qlack2.fuse.cm.api.storage.StorageEngine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Transactional
//TODO check for injection in filenames, e.g. "../../foo.bar".
public class FSStorage implements StorageEngine {
	private static final Logger LOGGER = Logger.getLogger(FSStorage.class.getName());
	// The number of available buckets in powers of 10. Make sure you do not
	// change this value on a running system as buckets will be misaligned.
	@Value("${buckets}")
	private int numberOfBuckets;

	@Value("${chunkSize}")
	private int chunkSize;

	// The root of the filesystem (set in Blueprint).
	@Value("${rootFS}")
	private String rootFS;

	/**
	 * Converts a uuid to a file-system path using a bucketing algorithm.
	 * 
	 * @param uuid
	 * @return
	 */
	private String bucketise(String uuid) {
		return StringUtils.join(new String[] { rootFS, String.valueOf(Math.abs(uuid.hashCode()))
				.substring(0, (int) Math.log10(numberOfBuckets)), uuid + ".bin" }, File.separator);
	}

	@Override
	public void setVersionContent(String versionID, byte[] content) {
		File f = new File(bucketise(versionID));
		try {
			Files.createDirectories(f.getParentFile().toPath());
			Files.write(f.toPath(), content);
			LOGGER.log(Level.FINEST, "Created file: {0}.", f.getAbsolutePath());
		} catch (IOException ex) {
			throw new QStorageException("Could not persist file into " + f.getAbsolutePath(), ex);
		}
	}

	@Override
	public byte[] getVersionContent(String versionID) {
		byte[] retVal = null;
		String fileLocation = bucketise(versionID);

		try {
			LOGGER.log(Level.FINEST, "Reading from file: {0}.", fileLocation);
			retVal = Files.readAllBytes(new File(fileLocation).toPath());
		} catch (IOException ex) {
			throw new QStorageException("Could not read file from " + fileLocation, ex);
		}

		return retVal;
	}

	// Note that chunkIndex is ignored on filesystem-based repositories. Chunks
	// are simply appended to the end of the existing file (or a new one is
	// created) independently of their index/order.
	@Override
	public String setBinChunk(String versionID, byte[] content, int chunkIndex) {
		File f = new File(bucketise(versionID));
		try {
			Files.createDirectories(f.getParentFile().toPath());
			f.createNewFile();
			Files.write(f.toPath(), content, StandardOpenOption.APPEND);
		} catch (IOException ex) {
			throw new QStorageException("Could not persist file into "
					+ f.getAbsolutePath(), ex);
		}

		return f.getAbsolutePath();
	}

	@Override
	public BinChunkDTO getBinChunk(String versionID, int chunkIndex) {
		BinChunkDTO retVal = new BinChunkDTO();
		String fileLocation = bucketise(versionID);
		File file = new File(fileLocation);

		try (RandomAccessFile fileStore = new RandomAccessFile(file, "r")) {
			LOGGER.log(Level.FINEST, "Reading from file: {0}.", fileLocation);
			long startingPosition = chunkIndex * chunkSize;

			fileStore.seek(startingPosition);
			byte[] bb = new byte[chunkSize];
			fileStore.read(bb);

			retVal.setHasMoreChunks(file.length() > ((chunkIndex + 1) * chunkSize));

			if (!retVal.isHasMoreChunks()) {
				int trimPosition = (int) (file.length() - (chunkIndex * chunkSize));
				retVal.setBinContent(Arrays.copyOf(bb, trimPosition));
			} else {
				retVal.setBinContent(bb);
			}
		} catch (IOException ex) {
			throw new QStorageException("Could not read file from " + fileLocation, ex);
		}

		retVal.setChunkIndex(chunkIndex);
		retVal.setVersionID(versionID);

		return retVal;
	}

	@Override
	public boolean deleteVersion(String versionID) {
		String fileLocation = bucketise(versionID);
		try {
			return Files.deleteIfExists(new File(fileLocation).toPath());
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, MessageFormat.format(
					"Could not delete file {0}.", fileLocation), e);
			return false;
		}
	}

}
