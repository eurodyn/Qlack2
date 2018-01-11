/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.ts.impl;

import com.eurodyn.qlack2.fuse.ts.api.TemplateService;
import com.eurodyn.qlack2.fuse.ts.exception.QTemplateServiceException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import javax.xml.bind.JAXBException;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class TemplateServiceImpl implements TemplateService {
 
  @Override
  public ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream, Map<String, String> mappings){
    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
    try {
      
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
      MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
      documentPart.variableReplace(mappings);
      
      Docx4J.save(wordMLPackage, baos);
     
    } catch (Docx4JException|JAXBException e) {
      throw new QTemplateServiceException("The document cannot be created!");
    } 
    
    return baos; 
      
  }

}