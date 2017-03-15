package com.eurodyn.qlack2.fuse.cm.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.persistence.EntityTransaction;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eurodyn.qlack2.common.util.reflection.ReflectionUtil;
import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.FileDTO;

public class VersionServiceImplTest {
	private String testFile = "docker.png";
	private static EntityTransaction tx;

	@BeforeClass
	public static void beforeClass() throws Exception {
		if (!AllTests.suiteRunning) {
			AllTests.init();
		}
	}

	@AfterClass
	public static void afterClass() throws Exception {
		if (!AllTests.suiteRunning) {
			AllTests.tearDownAfterClass();
		}
	}

	@Before
	public void setUp() throws Exception {
		if (tx == null) {
			tx = AllTests.getEm().getTransaction();
		}
		tx.begin();
	}

	@After
	public void tearDown() throws Exception {
		tx.commit();
	}

	/**
	 * Helper to create a file.
	 * 
	 * @return
	 */
	private String createFile() {
		FileDTO fileDTO = new FileDTO();
		return AllTests.getDocumentService().createFile(fileDTO, null, null);
	}

	@Test
	public void testCreateVersion() throws IOException, URISyntaxException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {/*
		// Read binary content.
		testFile = "10MB.bin";
		byte[] file = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(testFile).toURI()));
		System.out.println("Test file size = " + file.length);
		String testfileMD5 = DigestUtils.md5Hex(file);

		// DB testing - complete file ------------------------------------------
		String fileID = createFile();
		System.out.println("File ID - DB = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(), "storageEngine",
				AllTests.getDbStorage());
		String versionID = AllTests.getVersionService()
				.createVersion(fileID, "1", "myfile.png", file, "test", "test");
		System.out.println("Version ID - DB = " + versionID);
		byte[] binContent = AllTests.getVersionService().getBinContent(fileID);
		assertNotNull(binContent);
		System.out.println("File received from DB size = " + file.length);
		assertTrue(binContent.length == file.length);
		assertTrue(testfileMD5.equals(DigestUtils.md5Hex(binContent)));

		// DB testing - chunked file ------------------------------------------
		fileID = createFile();
		System.out.println("File ID - DB chunked = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(), "storageEngine",
				AllTests.getDbStorage());
		versionID = AllTests.getVersionService()
				.createVersion(fileID, "2", "myfile-chunked.png", null, "test", "test");
		System.out.println("Version ID - DB chunked = " + versionID);
		// Write file in chunks.
		int chunkSize = 4096000;
		int start = 0;
		for (int i = 0; i < (file.length / chunkSize) + 1 ; i++) {
			if (start + chunkSize > file.length) {
				byte[] newChunk = new byte[file.length - start];
				System.arraycopy(file, start, newChunk, 0, file.length - start);
				AllTests.getVersionService().setBinChunk(versionID, newChunk, i);
			} else {
				byte[] newChunk = new byte[chunkSize];
				System.arraycopy(file, start, newChunk, 0, chunkSize);
				AllTests.getVersionService().setBinChunk(versionID, newChunk, i);
			}
			start += chunkSize;
		}
		// Read file in chunks.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int currentChunk = 0;
		BinChunkDTO binChunk = AllTests.getVersionService().getBinChunk(versionID, currentChunk);
		baos.write(binChunk.getBinContent());
		while (binChunk != null && binChunk.isHasMoreChunks()) {
			binChunk = AllTests.getVersionService().getBinChunk(versionID, binChunk.getChunkIndex() + 1);
			baos.write(binChunk.getBinContent());	
		}
		byte[] readFile = baos.toByteArray();
		System.out.println("File received from DB chunked size = " + readFile.length);
		assertTrue(readFile.length == file.length);
		assertTrue(testfileMD5.equals(DigestUtils.md5Hex(readFile)));
		
		// FS testing ----------------------------------------------------------
		fileID = createFile();
		System.out.println("File ID - FS = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(), "storageEngine",
				AllTests.getFsStorage());
		// Create a version of this file.
		versionID = AllTests.getVersionService()
				.createVersion(fileID, "1", "myfile.bin", file, "test", "test");
		System.out.println("Version ID - FS = " + versionID);

		binContent = AllTests.getVersionService().getBinContent(fileID);
		System.out.println("File received from FSStorage = " + file.length);
		assertNotNull(binContent);
		assertTrue(testfileMD5.equals(DigestUtils.md5Hex(binContent)));
		
		// FS testing - chunked file ------------------------------------------
		fileID = createFile();
		System.out.println("File ID - FS chunked = " + fileID);
		System.out.println("File ID - FS chunked size = " + file.length);
		System.out.println("File ID - FS chunked MD5 = " + testfileMD5);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(), "storageEngine",
				AllTests.getFsStorage());
		versionID = AllTests.getVersionService()
				.createVersion(fileID, "2", "myfile-chunked.png", null, "test", "test");
		System.out.println("Version ID - FS chunked = " + versionID);
		// Write file in chunks.
		chunkSize = 4096000;
		start = 0;
		for (int i = 0; i < (file.length / chunkSize) + 1 ; i++) {
			if (start + chunkSize > file.length) {
				byte[] newChunk = new byte[file.length - start];
				System.arraycopy(file, start, newChunk, 0, file.length - start);
				AllTests.getVersionService().setBinChunk(versionID, newChunk, i);
			} else {
				byte[] newChunk = new byte[chunkSize];
				System.arraycopy(file, start, newChunk, 0, chunkSize);
				AllTests.getVersionService().setBinChunk(versionID, newChunk, i);
			}
			start += chunkSize;
		}
		// Read file in chunks.
		baos = new ByteArrayOutputStream();
		currentChunk = 0;
		binChunk = AllTests.getVersionService().getBinChunk(versionID, currentChunk);
		baos.write(binChunk.getBinContent());
		while (binChunk != null && binChunk.isHasMoreChunks()) {
			binChunk = AllTests.getVersionService().getBinChunk(versionID, binChunk.getChunkIndex() + 1);
			baos.write(binChunk.getBinContent());	
		}
		readFile = baos.toByteArray();
		System.out.println("File received from FS chunked size = " + readFile.length);
		System.out.println("File received from FS MD5 = " + DigestUtils.md5Hex(readFile));
		assertTrue(readFile.length == file.length);
		assertTrue(testfileMD5.equals(DigestUtils.md5Hex(readFile)));
	*/}
	
	
	/*
	 @Test
	public void testUpdateBinContent() throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, IOException, URISyntaxException,
			QNodeLockException, QNodeLockException {

		byte[] file = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile).toURI()));
		byte[] file2 = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile2).toURI()));
		System.out.println("Test file size = " + file.length);
		System.out.println("Test file2 size = " + file2.length);


		// DB testing ----------------------------------------------------------
		String fileID = createFile();
		System.out.println("File ID - DB = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getDbStorage());

		// Create an initial content for this file.
		BinContentDTO binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		String versionID = AllTests.getVersionService().createVersion(fileID,
				"1", binContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Create the new content
		BinContentDTO newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Update the content of the version
		AllTests.getVersionService().updateBinContent(fileID, null,
				newBinContentDTO, "test", null);
		System.out.println("Version ID - DB = " + versionID);

		// Verify that the content has been updated
		BinContentDTO retContent = AllTests.getVersionService().getBinContent(
				fileID);
		assertNotNull(retContent);
		assertTrue(retContent.getContent().length == testFile2Length);

		// FS testing ----------------------------------------------------------

		fileID = createFile();
		System.out.println("File ID - FS = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getFsStorage());

		// Create an initial content for this file.
		binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		versionID = AllTests.getVersionService().createVersion(fileID, "1",
				binContentDTO, "test", "test");
		System.out.println("Version ID - FS = " + versionID);

		// Create the new content
		newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Update the content of the version
		AllTests.getVersionService().updateBinContent(fileID, null,
				newBinContentDTO, "test", null);
		System.out.println("Version ID - DB = " + versionID);

		// Verify that the content has been updated
		retContent = AllTests.getVersionService().getBinContent(fileID);
		assertNotNull(retContent);
		assertTrue(retContent.getContent().length == testFile2Length);
	}

	@Test
	public void testGetFileVersions() throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, IOException, URISyntaxException,
			QNodeLockException, QNodeLockException {

		byte[] file = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile).toURI()));
		byte[] file2 = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile2).toURI()));
		
		System.out.println("Test file size = " + file.length);

		// DB testing ----------------------------------------------------------
		String fileID = createFile();
		System.out.println("File ID - DB = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getDbStorage());

		// Create an initial content for this file.
		BinContentDTO binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		String versionID = AllTests.getVersionService().createVersion(fileID,
				"1", binContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Create the new content
		BinContentDTO newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Create an new version for the file that contains the new content
		String newVersionID = AllTests.getVersionService().createVersion(
				fileID, "2", newBinContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Now that we have created two versions for the file, we will check
		// that these versions are in place
		List<VersionDTO> versions = AllTests.getVersionService()
				.getFileVersions(fileID);
		assertNotNull(versions);
		assertTrue(versions.size() == 2);
		// Check that the versions have come in ascending order
		assertEquals(versions.get(0).getId(), versionID);
		assertEquals(versions.get(1).getId(), newVersionID);

	}

	@Test
	public void testGetFileLatestVersion() throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, IOException, URISyntaxException,
			QNodeLockException, QNodeLockException {

		byte[] file = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile).toURI()));
		byte[] file2 = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile2).toURI()));
		
		System.out.println("Test file size = " + file.length);
		System.out.println("Test file 2 size = " + file2.length);

		// DB testing ----------------------------------------------------------
		String fileID = createFile();
		System.out.println("File ID - DB = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getDbStorage());

		// Create an initial content for this file.
		BinContentDTO binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		String versionID = AllTests.getVersionService().createVersion(fileID,
				"1", binContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Create the new content
		BinContentDTO newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Create a new version for the file that contains the new content
		String newVersionID = AllTests.getVersionService().createVersion(
				fileID, "2", newBinContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Now that we have created two versions for the file, we will check
		// that the latest version is indeed retrieved.
		VersionDTO version = AllTests.getVersionService().getFileLatestVersion(
				fileID);
		assertNotNull(version);
		// Check that the latest version is retrieved
		assertEquals(version.getId(), newVersionID);

	}
	
	@Test
	public void testGetBinContentString() throws Exception {
		byte[] file = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile).toURI()));
		byte[] file2 = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile2).toURI()));
		System.out.println("Test file size = " + file.length);
		System.out.println("Test file 2 size = " + file2.length);


		// DB testing ----------------------------------------------------------
		String fileID = createFile();
		System.out.println("File ID - DB = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getDbStorage());

		// Create an initial content for this file.
		BinContentDTO binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		String versionID = AllTests.getVersionService().createVersion(fileID,
				"1", binContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Create the new content
		BinContentDTO newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Create an new version for the file that contains the new content
		AllTests.getVersionService().createVersion(
				fileID, "2", newBinContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Verify that the latest/most recent version content is retrieved, since no 
		// version name is provided, and check that it is correctly retrieved.
		BinContentDTO retContent = AllTests.getVersionService().getBinContent(
				fileID);
		assertNotNull(retContent);
		assertTrue(retContent.getContent().length == testFile2Length);
		
		// FS testing ----------------------------------------------------------
		
		fileID = createFile();
		System.out.println("File ID - FS = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getFsStorage());

		// Create an initial content for this file.
		binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		versionID = AllTests.getVersionService().createVersion(fileID,
				"1", binContentDTO, "test", "test");
		System.out.println("Version ID - FS = " + versionID);

		// Create the new content
		newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Create an new version for the file that contains the new content
		AllTests.getVersionService().createVersion(
				fileID, "2", newBinContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Verify that the latest/most recent version content is retrieved, since no 
		// version name is provided, and check that it is correctly retrieved.
		retContent = AllTests.getVersionService().getBinContent(
				fileID);
		assertNotNull(retContent);
		assertTrue(retContent.getContent().length == testFile2Length);
		
	}

	
	
	@Test
	public void testGetBinContentStringString() throws Exception {
		byte[] file = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile).toURI()));
		byte[] file2 = Files.readAllBytes(Paths.get(ClassLoader
				.getSystemResource(testFile2).toURI()));
		
		System.out.println("Test file size = " + file.length);
		System.out.println("Test file 2 size = " + file2.length);
		String version1Name = "1";
		String version2Name = "2";
		
		// DB testing ----------------------------------------------------------
		String fileID = createFile();
		System.out.println("File ID - DB = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getDbStorage());

		// Create an initial content for this file.
		BinContentDTO binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		String versionID = AllTests.getVersionService().createVersion(fileID,
				version1Name, binContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Create the new content for file 2
		BinContentDTO newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Create a new version for the file that contains the new content
		String version2ID = AllTests.getVersionService().createVersion(
				fileID, version2Name, newBinContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + version2ID);

		// Verify that the "selected" version 1 content is retrieved and that it is correctly retrieved.
		BinContentDTO retContent = AllTests.getVersionService().getBinContent(
				fileID, version1Name);
		assertNotNull(retContent);
		assertTrue(retContent.getContent().length == testFileLength);
		
		// FS testing ----------------------------------------------------------
		
		fileID = createFile();
		System.out.println("File ID - DB = " + fileID);
		ReflectionUtil.setPrivateField(AllTests.getVersionService(),
				"storageEngine", AllTests.getFsStorage());

		// Create an initial content for this file.
		binContentDTO = new BinContentDTO();
		binContentDTO.setContent(file);
		binContentDTO.setFilename(testFile);
		binContentDTO.setMimetype("Test");

		// Create an initial version for the file
		versionID = AllTests.getVersionService().createVersion(fileID,
				version1Name, binContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + versionID);

		// Create the new content for file 2
		newBinContentDTO = new BinContentDTO();
		newBinContentDTO.setContent(file2);
		newBinContentDTO.setFilename(testFile2);
		newBinContentDTO.setMimetype("Test");

		// Create a new version for the file that contains the new content
		version2ID = AllTests.getVersionService().createVersion(
				fileID, version2Name, newBinContentDTO, "test", "test");
		System.out.println("Version ID - DB = " + version2ID);

		// Verify that the "selected" version 1 content is retrieved and that it is correctly retrieved.
		retContent = AllTests.getVersionService().getBinContent(
				fileID, version1Name);
		assertNotNull(retContent);
		assertTrue(retContent.getContent().length == testFileLength);
		
	}*/
	
	
	

}
