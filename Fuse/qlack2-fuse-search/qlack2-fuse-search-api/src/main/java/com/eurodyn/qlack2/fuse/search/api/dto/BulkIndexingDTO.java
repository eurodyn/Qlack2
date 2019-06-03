package com.eurodyn.qlack2.fuse.search.api.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a multiple number of documents to be indexed.
 */
public class BulkIndexingDTO<T> extends ESDocumentIdentifierDTO {

  private Map<String, T> objects;

  /**
   * Default constructor.
   */
  public BulkIndexingDTO() {
    super();
  }

  /**
   * Public Constructor.
   *
   * @param index The index name to index to.
   * @param type The type in the index to index to.
   * @param objects This map contains the documents to be indexed. The key of the map is the id and
   *        the value the document to index.
   */
  public BulkIndexingDTO(String index, String type, Map<String, T> objects) {
    super(index, type, null);
    this.objects = objects;
  }

  /**
   * Gets the map with the documents to index.
   *
   * @return The map with the documents to index.
   */
  public Map<String, T> getObjects() {
    if (objects == null) {
      objects = new HashMap<>();
    }

    return objects;
  }

  public void setObjects(Map<String, T> objects) {
    this.objects = objects;
  }
}
