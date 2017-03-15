package com.eurodyn.qlack2.fuse.cm.impl.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;

import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;
import com.eurodyn.qlack2.fuse.cm.api.storage.StorageEngine;
import com.eurodyn.qlack2.fuse.cm.impl.mappers.BinContentDTOMapperImpl;
import com.eurodyn.qlack2.fuse.cm.impl.model.Version;
import com.eurodyn.qlack2.fuse.cm.impl.model.VersionBin;

@Transactional
@Singleton
public class DBStorage implements StorageEngine {
	@PersistenceContext(unitName = "fuse-contentmanager")
	private EntityManager em;

	@Value("${chunkSize}")
	private int chunkSize;

	public static final BinContentDTOMapperImpl mapper = new BinContentDTOMapperImpl();
	
	private String persistBinChunk(Version version, byte[] content, int chunkIndex) {
			
		VersionBin versionBin = new VersionBin();
		versionBin.setBinContent(content);
		versionBin.setChunkIndex(chunkIndex);
		versionBin.setId(UUID.randomUUID().toString());
		versionBin.setVersion(version);
		System.out.println("PersistBinChunk -- BEFORE PERSIST ");
		em.persist(versionBin);
		em.flush();
		
		return versionBin.getId();
	}
	
	@Override
	public void setVersionContent(String versionID, byte[] content) {
		Version version = em.find(Version.class, versionID);
		
		int start = 0;
		for (int i = 0; i < (content.length / chunkSize) + 1 ; i++) {
			if (start + chunkSize > content.length) {
				byte[] newChunk = new byte[content.length - start];
				System.arraycopy(content, start, newChunk, 0, content.length - start);
				persistBinChunk(version, newChunk, i);
			} else {
				byte[] newChunk = new byte[chunkSize];
				System.arraycopy(content, start, newChunk, 0, chunkSize);
				persistBinChunk(version, newChunk, i);
			}
			start += chunkSize;
		}
	}

	@Override
	public byte[] getVersionContent(String versionID) throws IOException {
		Version version = em.find(Version.class, versionID);

		TypedQuery<VersionBin> q = em.createQuery(
				"from VersionBin vb where vb.version = :version "
						+ "order by vb.chunkIndex",
				VersionBin.class);
		q.setParameter("version", version);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		for (VersionBin vb : q.getResultList()) {
			bOut.write(vb.getBinContent());
		}

		return bOut.toByteArray();
	}

	@Override
	public String setBinChunk(String versionID, byte[] content, int chunkIndex) {
		Version version = em.find(Version.class, versionID);
		return persistBinChunk(version, content, chunkIndex);
	}

	@Override
	public BinChunkDTO getBinChunk(String versionID, int chunkIndex) {
		BinChunkDTO binChunkDTO = null;
		Version version = em.find(Version.class, versionID);

		TypedQuery<VersionBin> q = em.createQuery(
				"from VersionBin vb where vb.version = :version and "
				+ "vb.chunkIndex in :chunkIndexes order by vb.chunkIndex",
				VersionBin.class);
		q.setParameter("version", version);
		q.setParameter("chunkIndexes", Arrays.asList(new Integer[]{chunkIndex, chunkIndex + 1}));
		
		List<VersionBin> resultList = q.getResultList();
		if (resultList.size() > 0) {
			binChunkDTO = mapper.map(resultList.get(0));
			binChunkDTO.setHasMoreChunks(resultList.size() == 2);
		}
		
		return binChunkDTO;
	}

	@Override
	public boolean deleteVersion(String versionID) {
		// NO-OP
		return true;
	}

}
