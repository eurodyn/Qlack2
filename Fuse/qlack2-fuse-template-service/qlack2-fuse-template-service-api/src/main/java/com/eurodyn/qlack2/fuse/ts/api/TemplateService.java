/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License"). You may not use this work except in
 * compliance with the Licence. You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */
package com.eurodyn.qlack2.fuse.ts.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.fuse.ts.exception.QTemplateServiceException;

// TODO: Auto-generated Javadoc
/**
 * The Interface TemplateService.
 */
public interface TemplateService {

  /**
   * Using the docx4j library the placeholders in a word document will be replaced.
   *
   * @param inputStream The docx document as an Stream
   * @param mappings The key is the placeholder in the document. An example of the placeholder is
   *        ${description}
   * @return the byte array output stream
   * @throws QTemplateServiceException the q template service exception
   */
  ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings);

  /**
   * Creates the table in docx document.
   *
   * @param inputStream the input stream
   * @param header The header of the table in the document that it will be generated.
   * @param content The content of the table.
   * @param tableProperties the table properties
   * @return the byte array output stream
   */
  ByteArrayOutputStream createTableInDocxDocument(InputStream inputStream, List<String> header,
      String tableTitle, List<LinkedHashMap<Map<String, String>, String>> content,
      Map<String, String> tableProperties, List<Map<byte[], String>> iconsToReplaced);

  /**
   * Generate excel spreadsheet.
   *
   * @param xlsxHeader the xlsx header
   * @param xlsxContent the xlsx content
   * @return the byte array output stream
   */
  ByteArrayOutputStream generateExcelSpreadsheet(List<String> xlsxHeader,
      List<LinkedHashMap<Integer, String>> xlsxContent);

  /**
   * Replace placeholders word doc.
   *
   * @param inputStream the input stream
   * @param mappings the mappings
   * @param logo the logo
   * @param imageWidth the image width
   * @return the byte array output stream
   */
  ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, byte[] logo, long imageWidth);
  
  /**
   * Replace placeholders word doc.
   *
   * @param inputStream the input stream
   * @param mappings the mappings
   * @param paragraphList the paragraph list
   * @param position the position
   * @return the byte array output stream
   */
  ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, List<String> paragraphList, Integer position);
  
  
  /**
   * Replace placeholders word doc.
   *
   * @param inputStream the input stream
   * @param mappings the mappings
   * @param logo the logo
   * @param imageWidth the image width
   * @param paragraphList the paragraph list
   * @param position the position
   * @return the byte array output stream
   */
  ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, byte[] logo, long imageWidth, List<String> paragraphList,
      Integer position);

  /**
   * Replace placeholders word doc.
   *
   * @param inputStream the input stream
   * @param mappings the mappings
   * @param checkbox the checkbox
   * @param bulletList the bullet list
   * @param logo the logo
   * @param imageWidth the image width
   * @return the byte array output stream
   */
  ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, String checkbox, List<String> bulletList, byte[] logo, long imageWidth);
  
  
  /**
   * Replace placeholder with table.
   *
   * @param inputStream the input stream
   * @param table the table
   * @param placeholder the placeholder
   * @param identLeft the ident left
   * @return the byte array output stream
   */
  ByteArrayOutputStream replacePlaceholderWithTable(InputStream inputStream,
      List<LinkedHashMap<Map<String, String>, Map<String, String>>> table, String placeholder,
      String identLeft);
  
  /**
   * Replace placeholders with image.
   *
   * @param inputStream the input stream
   * @param iconsToReplaced the icons to replaced
   * @return the byte array output stream
   */
  ByteArrayOutputStream replacePlaceholdersWithImage(InputStream inputStream,
      List<Map<byte[], String>> iconsToReplaced);
}
