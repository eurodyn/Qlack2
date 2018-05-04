package com.eurodyn.qlack2.fuse.search.impl.mappers;

import java.io.UnsupportedEncodingException;

import org.apache.http.nio.entity.NStringEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.eurodyn.qlack2.fuse.search.api.request.CreateIndexRequest;
import com.eurodyn.qlack2.fuse.search.api.request.UpdateMappingRequest;
import com.eurodyn.qlack2.fuse.search.impl.mappers.request.InternalCreateIndexRequest;
import com.eurodyn.qlack2.fuse.search.impl.mappers.request.InternalUpdateMappingRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Mapper
public abstract class CreateIndexRequestMapper {
  private ObjectMapper mapper = new ObjectMapper();
  public static CreateIndexRequestMapper INSTANCE = Mappers.getMapper(CreateIndexRequestMapper.class);

  @Mapping(source = "shards", target = "settings.index.numberOfShards")
  @Mapping(source = "replicas", target = "settings.index.numberOfReplicas")
  @Mapping(source = "indexMapping", target = "mappings")
  @Mapping(source = "analysis", target = "settings.analysis")
  abstract InternalCreateIndexRequest mapToInternal(CreateIndexRequest request);

	public NStringEntity mapToNStringEntity(CreateIndexRequest createIndexRequest)
			throws JsonProcessingException, UnsupportedEncodingException {

		return new NStringEntity(mapper.writeValueAsString(mapToInternal(createIndexRequest)));
	}

	public NStringEntity mapToNStringEntity(UpdateMappingRequest updateMappingRequest)
			throws JsonProcessingException, UnsupportedEncodingException {

		InternalUpdateMappingRequest request = new InternalUpdateMappingRequest();
		request.setProperties(updateMappingRequest.getIndexMapping());

		return new NStringEntity(mapper.writeValueAsString(request));
	}
}
