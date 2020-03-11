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
package com.eurodyn.qlack2.fuse.ts.impl;

import com.eurodyn.qlack2.fuse.ts.api.TemplateService;
import com.eurodyn.qlack2.fuse.ts.exception.QTemplateServiceException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.dml.wordprocessingDrawing.STAlignH;
import org.docx4j.dml.wordprocessingDrawing.STRelFromH;
import org.docx4j.dml.wordprocessingDrawing.STRelFromV;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.structure.HeaderFooterPolicy;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.w14.CTOnOff;
import org.docx4j.w14.CTSdtCheckbox;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTSignedTwipsMeasure;
import org.docx4j.wml.Color;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Document;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcMar;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.GridSpan;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSheetFormatPr;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.SheetData;

/**
 * The Class TemplateServiceImpl.
 */
public class TemplateServiceImpl implements TemplateService {

  /** The Constant NO_DOCUMENT_CREATED. */
  private static final String NO_DOCUMENT_CREATED = "The document cannot be created!";

  /** The Constant IMAGEPART_NOT_CREATED. */
  private static final String IMAGE_PART_NOT_CREATED = "Image part cannot be created!";

  /** The Constant INLINE_IMAGE_NOT_CREATED. */
  private static final String INLINE_IMAGE_NOT_CREATED = "Inline image cannot be created!";

  /** The Constant TENANT_LOGO. */
  private static final String TENANT_LOGO = "sb_logo";

  @Override
  public ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      // Replace placeholders on main part.
      replaceBodyPlaceholders(wordMLPackage, mappings);
      // Replace placeholders on header and footer.
      replaceHeaderAndFooterPlaceholders(wordMLPackage, mappings);
      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  @Override
  public ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, String checkbox, List<String> bulletList, byte[] logo,
      long imageWidth) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      // Replace placeholders on main part.
      replaceBodyPlaceholdersWithCheckbox(wordMLPackage, mappings, checkbox, bulletList);
      // Replace placeholders on header and footer.
      replaceHeaderAndFooterPlaceholders(wordMLPackage, mappings);

      if (logo != null) {
        addHeaderAnchor(wordMLPackage, logo, imageWidth);
      }
      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  @Override
  public ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
    Map<String, String> mappings, String checkbox, List<String> bulletList, Map<String, String> bulletListProperties) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      // Replace placeholders on main part.
      if (bulletListProperties != null && !bulletListProperties.isEmpty()) {
        replaceBodyPlaceholdersWithCheckbox(wordMLPackage, mappings, checkbox, bulletList, bulletListProperties);
      } else {
        replaceBodyPlaceholdersWithCheckbox(wordMLPackage, mappings, checkbox, bulletList);
      }
      // Replace placeholders on header and footer.
      replaceHeaderAndFooterPlaceholders(wordMLPackage, mappings);

      Docx4J.save(wordMLPackage, baos);
    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  @Override
  public ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, byte[] logo, long imageWidth) {
    ByteArrayOutputStream baos = null;
    if (logo != null) {
      baos = replacePlaceHoldersAndLogoDocx(inputStream, mappings, logo, imageWidth, null, null);
    } else {
      baos = replacePlaceholdersWordDoc(inputStream, mappings);
    }
    return baos;
  }

  /**
   * Replace placeholders to documents with logo.
   *
   * @param inputStream the input stream
   * @param mappings the mappings
   * @param logo the logo
   * @return the byte array output stream
   */
  public ByteArrayOutputStream replacePlaceHoldersAndLogoDocx(InputStream inputStream,
      Map<String, String> mappings, byte[] logo, long imageWidth, List<String> paragraphList,
      Integer position) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      // Replace placeholders on main part.
      if (paragraphList != null && !paragraphList.isEmpty() && position != null) {
        replaceBodyPlaceholdersAndParagraph(wordMLPackage, mappings, paragraphList, position);
      } else {
        replaceBodyPlaceholders(wordMLPackage, mappings);
      }
      // Replace placeholders on header and footer.
      replaceHeaderAndFooterPlaceholders(wordMLPackage, mappings);
      // Add logo on document.
      addHeaderImage(wordMLPackage, logo, imageWidth, null);
      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }
  
  /**
   * Adds the header anchor.
   *
   * @param wordMLPackage the word ML package
   * @param logo the logo
   * @param imageWidth the image width
   */
  private void addHeaderAnchor(WordprocessingMLPackage wordMLPackage, byte[] logo,
      long imageWidth) {
    BinaryPartAbstractImage imagePart = null;
    HeaderPart headerPart = checkIfHeaderPartExists(wordMLPackage);
    try {
      if (headerPart != null) {
        imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, headerPart, logo);
      }
    } catch (Exception e) {
      throw new QTemplateServiceException(IMAGE_PART_NOT_CREATED);
    }

    if (imagePart != null) {
      Inline inline = createInlineImage(imagePart, imageWidth);
      // convert the inline to an anchor (xml contents are essentially the same)
      String anchorXml = XmlUtils.marshaltoString(inline, true, false, Context.jc,
          Namespaces.NS_WORD12, "anchor", Inline.class);

      org.docx4j.dml.ObjectFactory dmlFactory = new org.docx4j.dml.ObjectFactory();
      org.docx4j.dml.wordprocessingDrawing.ObjectFactory wordDmlFactory =
          new org.docx4j.dml.wordprocessingDrawing.ObjectFactory();
      try {
        Anchor anchor = (Anchor) XmlUtils.unmarshalString(anchorXml, Context.jc, Anchor.class);
        anchor.setSimplePos(dmlFactory.createCTPoint2D());
        anchor.getSimplePos().setX(0L);
        anchor.getSimplePos().setY(0L);
        anchor.setSimplePosAttr(false);
        anchor.setPositionH(wordDmlFactory.createCTPosH());
        anchor.getPositionH().setAlign(STAlignH.CENTER);
        anchor.getPositionH().setRelativeFrom(STRelFromH.LEFT_MARGIN);
        anchor.setPositionV(wordDmlFactory.createCTPosV());
        anchor.getPositionV().setPosOffset(4646930);
        anchor.getPositionV().setRelativeFrom(STRelFromV.PAGE);
        anchor.setWrapNone(wordDmlFactory.createCTWrapNone());
        // Now add the inline in w:p/w:r/w:drawing
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(anchor);

        headerPart.getContent().add(paragraph);
      } catch (JAXBException e) {
        throw new QTemplateServiceException(IMAGE_PART_NOT_CREATED);
      }
    }
  }

  /**
   * Adds the header image.
   *
   * @param wordMLPackage the word ML package
   * @param logo the logo
   */
  private void addHeaderImage(WordprocessingMLPackage wordMLPackage, byte[] logo, long imageWidth,
      String placeholder) {
    BinaryPartAbstractImage imagePart = null;
    BinaryPartAbstractImage imagePartBody = null;
    HeaderPart headerPart = checkIfHeaderPartExists(wordMLPackage);
    try {
      if (headerPart != null) {
        imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, headerPart, logo);
      }
      imagePartBody = BinaryPartAbstractImage.createImagePart(wordMLPackage, logo);
    } catch (Exception e) {
      throw new QTemplateServiceException(IMAGE_PART_NOT_CREATED);
    }

    P paragraph = null;
    if (imagePart != null) {
      Inline inline = createInlineImage(imagePart, imageWidth);
      paragraph = addInlineImageToParagraph(inline);
      if (placeholder != null) {
        setParagraphAlignment(paragraph, JcEnumeration.LEFT);
      } else {
        setParagraphAlignment(paragraph, JcEnumeration.CENTER);
      }
    }

    Inline inlineBody = createInlineImage(imagePartBody, imageWidth);
    P paragraphBody = addInlineImageToParagraph(inlineBody);
    if (placeholder != null) {
      setParagraphAlignment(paragraphBody, JcEnumeration.LEFT);
    } else {
      setParagraphAlignment(paragraphBody, JcEnumeration.CENTER);
    }

    List<Object> elements = null;
    List<Object> elementsBody = null;
    if (headerPart != null) {
      elements = getAllElementFromObject(headerPart, Tbl.class);
    }
    elementsBody = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), Tbl.class);
    if (placeholder != null) {
      replaceImage(paragraph, elements, placeholder);
      replaceImage(paragraphBody, elementsBody, placeholder);
    } else {
      replaceImage(paragraph, elements, TENANT_LOGO);
      replaceImage(paragraphBody, elementsBody, TENANT_LOGO);
    }
  }

  private static void replaceImageBodyWithPlaceholder(P paragraphBody, Tc tc, String placeholder,
      Text text) {
    if (tc != null && text.getValue().contains(placeholder)) {
      tc.getContent().clear();
      tc.getContent().add(paragraphBody);
    }
  }

  /**
   * Check if first header part exists in the document and return it.
   *
   * @param wordMLPackage the word ML package
   * @return the header part
   */
  private HeaderPart checkIfHeaderPartExists(WordprocessingMLPackage wordMLPackage) {
    HeaderPart headerPart = null;
    List<SectionWrapper> sectionWrappers = wordMLPackage.getDocumentModel().getSections();
    for (SectionWrapper sw : sectionWrappers) {
      HeaderFooterPolicy hfpolicy = sw.getHeaderFooterPolicy();
      HeaderPart hpart = hfpolicy.getFirstHeader();
      if (hpart != null && hpart.getOwningRelationshipPart() != null) {
        headerPart = hpart;
      } else if (hfpolicy.getDefaultHeader() != null
          && hfpolicy.getDefaultHeader().getOwningRelationshipPart() != null) {
        headerPart = hfpolicy.getDefaultHeader();
      }
    }
    return headerPart;
  }

  /**
   * Method that aligns the paragraph to the center.
   *
   * @param paragraph the new paragraph
   */
  private static void setParagraphAlignment(P paragraph, JcEnumeration alignment) {
    ObjectFactory factory = new ObjectFactory();
    PPr paragraphProperties = factory.createPPr();
    Jc justification = factory.createJc();
    justification.setVal(alignment);
    paragraphProperties.setJc(justification);
    paragraph.setPPr(paragraphProperties);
  }

  /**
   * Method that creates the inline image.
   *
   * @param imagePart the image part
   * @return the inline
   */
  private static Inline createInlineImage(BinaryPartAbstractImage imagePart, long imageWidth) {
    int docPrId = 1;
    int cNvPrId = 2;
    Inline inline = null;
    try {
      if (imagePart != null) {
        if (imageWidth > 0) {
          inline = imagePart.createImageInline("", "Image", UUID.randomUUID().variant(), UUID.randomUUID().variant(), imageWidth, false);
        } else {
          inline = imagePart.createImageInline("", "Image", UUID.randomUUID().variant(), UUID.randomUUID().variant(), false);
        }
      }
    } catch (Exception e) {
      throw new QTemplateServiceException(INLINE_IMAGE_NOT_CREATED);
    }
    return inline;
  }

  /**
   * Replace image.
   *
   * @param paragraph the paragraph
   * @param elements the elements
   */
  public void replaceImage(P paragraph, List<Object> elements, String placeholder) {
    if (elements != null) {
      for (Object obj : elements) {
        if (obj instanceof Tbl) {
          Tbl table = (Tbl) obj;
          List<Object> rows = getAllElementFromObject(table, Tr.class);
          for (Object trObj : rows) {
            Tr tr = (Tr) trObj;
            List<Object> cols = getAllElementFromObject(tr, Tc.class);
            for (Object tcObj : cols) {
              Tc tc = (Tc) tcObj;
              List<Object> texts = getAllElementFromObject(tc, Text.class);
              for (Object textObj : texts) {
                Text text = (Text) textObj;
                if (text.getValue().equals(placeholder)) {
                  tc.getContent().clear();
                  tc.getContent().add(paragraph);
                  break;
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Gets all elements from object.
   *
   * @param obj the obj
   * @param toSearch the to search
   * @return the all element from object
   */
  private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
    List<Object> result = new ArrayList<>();
    if (obj instanceof JAXBElement) {
      obj = ((JAXBElement<?>) obj).getValue();
    }
    if (obj.getClass().equals(toSearch)) {
      result.add(obj);
    } else if (obj instanceof ContentAccessor) {
      List<?> children = ((ContentAccessor) obj).getContent();
      for (Object child : children) {
        result.addAll(getAllElementFromObject(child, toSearch));
      }
    }
    return result;
  }

  /**
   * Adds the inline image to paragraph.
   *
   * @param inline the inline
   * @return the p
   */
  private static P addInlineImageToParagraph(Inline inline) {
    ObjectFactory factory = new ObjectFactory();
    P paragraph = factory.createP();
    R run = factory.createR();
    paragraph.getContent().add(run);
    Drawing drawing = factory.createDrawing();
    run.getContent().add(drawing);
    drawing.getAnchorOrInline().add(inline);
    return paragraph;
  }

  @Override
  public ByteArrayOutputStream createTableInDocxDocument(InputStream inputStream,
      List<String> header, String tableTitle, List<LinkedHashMap<Map<String, String>, String>> content,
      Map<String, String> tableProperties, List<Map<byte[], String>> iconsToReplaced) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0)
          .getPageDimensions().getWritableWidthTwips();
      Tbl tblCredProg =
          TblFactory.createTable(0, header.size(), writableWidthTwips / header.size());
      removeBorders(tblCredProg, Boolean.valueOf(tableProperties.get("removeBorder")),
          tableProperties.get("borderSpace"), null);
      
      // Add table title (row).
      if (tableTitle != null) {
        Tr tTitle = factory.createTr();
        addStyledTableCell(tTitle, tableTitle, tableProperties,
          tableProperties.get("boldHeader"), null, false, null, wordMLPackage, null);
        
        if (tableProperties.get("repeatHeader") != null
            && tableProperties.get("repeatHeader").equals(Boolean.TRUE.toString())) {
          repeatTableHeader(tTitle);
        }
        tableProperties.remove("tableTitleGridSpan");
        tableProperties.remove("tableTitleFontSize");
        tblCredProg.getContent().add(tTitle);
      }
      
      // Add table header (row).
      Tr thead = factory.createTr();
      for (int num = 0; num < header.size(); num++) {
        // align right cell content if title exists
        if (num == 4 && tableTitle != null) {
          tableProperties.put("alignRight", String.valueOf(Boolean.TRUE));
          addStyledTableCell(thead, header.get(num), tableProperties,
            tableProperties.get("boldHeader"), null, true, null, wordMLPackage, null);
          tableProperties.remove("alignRight");
        } else {
          addStyledTableCell(thead, header.get(num), tableProperties,
            tableProperties.get("boldHeader"), null, false, null, wordMLPackage, null);
        }
      }
      if (tableProperties.get("repeatHeader") != null
          && tableProperties.get("repeatHeader").equals(Boolean.TRUE.toString())) {
        repeatTableHeader(thead);
      }
      tblCredProg.getContent().add(thead);

      // variable for the last row of table
      int lastRowCounter = 0;
      // Add table content (the content is added by row).
      for (Map<Map<String, String>, String> c : content) {
        Tr tr = factory.createTr();

        // prevent row from splitting to a new page
        // instead move whole row to next page
        preventRowSplit(tr);

        for (Entry<Map<String, String>, String> column : c.entrySet()) {
          addStyledTableCell(tr, column.getValue(), column.getKey(),
            column.getKey().get("boldContent"), null, false, iconsToReplaced, wordMLPackage, null);
        }
        if (lastRowCounter == content.size() - 1) {
          keepLastRowWithParagraph(tr);
        }
        tblCredProg.getContent().add(tr);
        lastRowCounter++;
      }

      if (tableProperties.get("tablePosition") != null) {
        // Set table specific position.
        wordMLPackage.getMainDocumentPart().getContent()
            .add(Integer.parseInt(tableProperties.get("tablePosition")), tblCredProg);
      } else {
        wordMLPackage.getMainDocumentPart().getContent().add(tblCredProg);
      }
      
      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  /**
   * Method that repeats table header to the next pages
   * 
   * @param thead
   */
  private void repeatTableHeader(Tr thead) {
    TrPr trpr = Context.getWmlObjectFactory().createTrPr();
    thead.setTrPr(trpr);
    // Create object for tblHeader (wrapped in JAXBElement)
    BooleanDefaultTrue booleandefaulttrue =
        Context.getWmlObjectFactory().createBooleanDefaultTrue();
    JAXBElement<org.docx4j.wml.BooleanDefaultTrue> booleandefaulttrueWrapped =
        Context.getWmlObjectFactory().createCTTrPrBaseTblHeader(booleandefaulttrue);
    trpr.getCnfStyleOrDivIdOrGridBefore().add(booleandefaulttrueWrapped);
  }

  /**
   * Method that keeps together the last row of the table with the next paragraph.
   *
   * @param tr the tr
   */
  private void keepLastRowWithParagraph(Tr tr) {
    for (Object trContent : tr.getContent()) {
      Tc tc = (Tc) trContent;
      if (tc != null) {
        for (Object tcContent : tc.getContent()) {
          if (tcContent != null && tcContent instanceof P) {
            P tcP = (P) tcContent;
            // Set property KeepNext to true.
            BooleanDefaultTrue bd = new BooleanDefaultTrue();
            bd.setVal(true);
            tcP.getPPr().setKeepNext(bd);
          }
        }
      }
    }
  }

  /**
   * Replace header placeholders.
   *
   * @param wordMLPackage the word ML package
   * @param mappings the mappings
   */
  private void replaceHeaderPlaceholders(WordprocessingMLPackage wordMLPackage,
      Map<String, String> mappings) {
    List<SectionWrapper> sectionWrappers = wordMLPackage.getDocumentModel().getSections();
    try {
      String xml = null;
      for (SectionWrapper sw : sectionWrappers) {
        HeaderFooterPolicy hfp = sw.getHeaderFooterPolicy();
        if (hfp != null) {
          HeaderPart headerPart = hfp.getFirstHeader();
          if (headerPart != null) {
            xml = XmlUtils.marshaltoString(headerPart.getContents());
          }
          Object obj = null;

          if (xml != null) {
            obj = XmlUtils.unmarshallFromTemplate(xml, mappings);
          }
          // Inject result into docx
          if (obj != null && headerPart != null) {
            headerPart.setJaxbElement((Hdr) obj);
          }

          String defaultXml = null;
          HeaderPart defaultHeader = hfp.getDefaultHeader();
          if (defaultHeader != null) {
            defaultXml = XmlUtils.marshaltoString(defaultHeader.getContents());
          }
          Object defaultObj = null;

          if (defaultXml != null) {
            defaultObj = XmlUtils.unmarshallFromTemplate(defaultXml, mappings);
          }
          // Inject result into docx
          if (defaultObj != null && defaultHeader != null) {
            defaultHeader.setJaxbElement((Hdr) defaultObj);
          }
        }
      }
    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException(
          "Error occured during placeholder replacement on header.");
    }
  }

  /**
   * Replace body placeholders.
   *
   * @param wordMLPackage the word ML package
   * @param mappings the mappings
   */
  private void replaceBodyPlaceholders(WordprocessingMLPackage wordMLPackage,
      Map<String, String> mappings) {
    try {
      MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
      documentPart.variableReplace(mappings);
      generateTextWithListedPlaceholder(documentPart);
    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException(
          "Error occured during placeholder replacement on main body.");
    }
  }

  private void replaceBodyPlaceholdersAndParagraph(WordprocessingMLPackage wordMLPackage,
      Map<String, String> mappings, List<String> paragraphList, Integer position) {
    try {
      MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

      // Nachweisbrief SB
      for (String paragraph : paragraphList) {
        if (mappings.get(paragraph) != null) {
          String as[] = StringUtils.splitPreserveAllTokens(mappings.get(paragraph), '\n');
          replaceParagraph(paragraph, as, wordMLPackage, position);
        }
      }
      
      documentPart.variableReplace(mappings);
      generateTextWithListedPlaceholder(documentPart);
    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException(
          "Error occured during placeholder replacement on main body.");
    }
  }

  private void replaceBodyPlaceholdersWithCheckbox(WordprocessingMLPackage wordMLPackage,
      Map<String, String> mappings, String checkbox, List<String> bulletList) {
    try {
      MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
      addBulletList(wordMLPackage, mappings, bulletList);

      findCheckbox(documentPart, checkbox);
      documentPart.variableReplace(mappings);
      generateTextWithListedPlaceholder(documentPart);
    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException(
          "Error occured during placeholder replacement on main body.");
    }
  }

  /**
   * Replaces body placeholders, adds bullet list and checkbox.
   *
   * @param wordMLPackage the wordMLPackage
   * @param mappings the mappings
   * @param checkbox the checkbox
   * @param bulletList the bulletList
   * @param bulletListProperties the bulletListProperties
   */
  private void replaceBodyPlaceholdersWithCheckbox(WordprocessingMLPackage wordMLPackage,
    Map<String, String> mappings, String checkbox, List<String> bulletList, Map<String, String> bulletListProperties) {
    try {
      MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
      addBulletList(wordMLPackage, mappings, bulletList, bulletListProperties);
      findCheckbox(documentPart, checkbox);
      documentPart.variableReplace(mappings);
      generateTextWithListedPlaceholder(documentPart);
    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException(
        "Error occured during placeholder replacement on main body.");
    }
  }

  private void addBulletList(WordprocessingMLPackage wordMLPackage, Map<String, String> mappings,
      List<String> bulletList) {
    for (String bullet : bulletList) {
      if (mappings.get(bullet) != null) {
        List<String> myList = new ArrayList<>(Arrays.asList(mappings.get(bullet).split("\n")));
        duplicate(wordMLPackage, myList, bullet);
        remove(wordMLPackage, bullet);
      }
    }
  }

  /**
   * Add bullet list with custom properties in placeholder.
   *
   * @param wordMLPackage the wordMLPackage
   * @param mappings the mappings
   * @param bulletList the bulletList
   * @param bulletListProperties the bulletListProperties
   */
  private void addBulletList(WordprocessingMLPackage wordMLPackage, Map<String, String> mappings,
    List<String> bulletList, Map<String, String> bulletListProperties) {
    for (String bullet : bulletList) {
      if (mappings.get(bullet) != null) {
        List<String> myList = new ArrayList<>(Arrays.asList(mappings.get(bullet).split("\n")));
        duplicate(wordMLPackage, myList, bullet, bulletListProperties);
        remove(wordMLPackage, bullet);
      }
    }
  }

  private static void replaceParagraph(String placeholder, String[] as,
      WordprocessingMLPackage template, Integer position) {
    // 1. get the paragraph
    List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);

    int index = 0;
    for (Object par : paragraphs) {
      index = 0;
      P p = (P) par;
      List list = template.getMainDocumentPart().getContent();
      // Workaround for table being wrapped in JAXBElement
      // This simple code assumes table is present and top level
      for (Object o : list) {

        if (XmlUtils.unwrap(o) == par) {
          break;
        }
        index++;
      }
    }

    P toReplace = new P();
    for (Object p : paragraphs) {
      List<Object> texts = getAllElementFromObject(p, Text.class);
      for (Object t : texts) {
        Text content = (Text) t;
        if (content.getValue().contains(placeholder)) {
          toReplace = (P) p;
          break;
        }
      }
    }

    for (int i = 0; i < as.length; i++) {
      String ptext = as[i];


      // 3. copy the found paragraph to keep styling correct
      P copy = (P) XmlUtils.deepCopy(toReplace);
      // replace the text elements from the copy
      List<Object> texts = getAllElementFromObject(copy, Text.class);

      if (texts.size() > 0) {
        Text textToReplace = (Text) texts.get(0);
        textToReplace.setValue(ptext);
      }

      template.getMainDocumentPart().getContent().add(position, copy);
    }

    // 4. remove the original one
    remove(template, placeholder);


  }



  public static void findAndReplace(WordprocessingMLPackage doc, String toFind, List<String> list) {
    List<Object> paragraphs = getAllElementFromObject(doc.getMainDocumentPart(), P.class);
    ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    for (Object par : paragraphs) {
      P p = (P) par;
      List<Object> texts = getAllElementFromObject(p, Text.class);

      for (Object text : texts) {
        Text t = (Text) text;
        for (String replacer : list) {
          if (t.getValue().contains(toFind)) {

            t.setValue(replacer);
            org.docx4j.wml.PPr ppr = factory.createPPr();

            p.setPPr(ppr);
            // Create and add <w:numPr>
            PPrBase.NumPr numPr = factory.createPPrBaseNumPr();
            ppr.setNumPr(numPr);


            // The <w:numId> element
            PPrBase.NumPr.NumId numIdElement = factory.createPPrBaseNumPrNumId();
            numPr.setNumId(numIdElement);
            numIdElement.setVal(BigInteger.valueOf(1));
          }

        }
      }

    }
  }

  private void findCheckbox(MainDocumentPart documentPart, String checkbox)
      throws XPathBinderAssociationIsPartialException, JAXBException {
    boolean found = false;
    String xpathSdt = "//w:sdt";
    List<Object> list = documentPart.getJAXBNodesViaXPath(xpathSdt, false);
    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
      Object o = XmlUtils.unwrap(it.next());

      if (o instanceof SdtElement) {
        SdtElement sdt = (SdtElement) o;
        if (checkbox != null && sdt.getSdtPr().getTag() != null
            && sdt.getSdtPr().getTag().getVal().equals(checkbox)) {
          found = true;
          for (Object o2 : sdt.getSdtPr().getRPrOrAliasOrLock()) {
            o2 = XmlUtils.unwrap(o2);
            if (o2 instanceof CTSdtCheckbox) {

              CTSdtCheckbox cTSdtCheckbox = (CTSdtCheckbox) o2;
              CTOnOff ctOnOff = new CTOnOff();
              ctOnOff.setVal("1");
              cTSdtCheckbox.setChecked(ctOnOff);
            }
          }
        }
        if (found) {
          for (Object o2 : sdt.getSdtContent().getContent()) {
            if (o2 instanceof R) {
              R r = (R) o2;
              for (Object o3 : r.getContent()) {
                List<Object> texts = getAllElementFromObject(o3, Text.class);
                for (Object t : texts) {
                  Text text = (Text) t;
                  text.setValue("☒");
                }

              }
            }
            found = false;
          }
        }

      }
    }
  }

  public static void remove(WordprocessingMLPackage wordMLPackage, String placeholder) {
    List<Object> paragraphs = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), P.class);
    for (Object par : paragraphs) {
      P p = (P) par;
      List<Object> texts = getAllElementFromObject(par, Text.class);
      for (Object t : texts) {
        Text text = (Text) t;
        if (text.getValue().contains(placeholder)) {
          ((ContentAccessor) p.getParent()).getContent().remove(p);
        }
      }
    }
  }

  public void duplicate(WordprocessingMLPackage wordMLPackage, List<String> replaceList,
      String placeholder) {
    ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    List<Object> paragraphs = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), P.class);
    for (Object par : paragraphs) {
      P p = (P) par;
      List list = wordMLPackage.getMainDocumentPart().getContent();
      // Workaround for table being wrapped in JAXBElement
      // This simple code assumes table is present and top level
      int index = 0;
      for (Object o : list) {
        if (XmlUtils.unwrap(o) == par) {
          break;
        }
        index++;
      }
      List<Object> texts = getAllElementFromObject(par, Text.class);
      for (Object t : texts) {
        Text text = (Text) t;
        if (text.getValue().contains(placeholder)) {
          for (String replacer : replaceList) {

            p = factory.createP();
            R rspc = factory.createR();

            text = factory.createText();
            text.setValue(replacer);
            rspc.getContent().add(text);

            RPr runProperties = factory.createRPr();


            setFontSize(runProperties, "18");

            rspc.setRPr(runProperties);

            p.getContent().add(rspc);

            org.docx4j.wml.PPr ppr = factory.createPPr();

            p.setPPr(ppr);
            // Create and add <w:numPr>
            PPrBase.NumPr numPr = factory.createPPrBaseNumPr();
            ParaRPr parRPr = factory.createParaRPr();

            HpsMeasure size = new HpsMeasure();
            size.setVal(new BigInteger("18"));
            runProperties.setSz(size);
            runProperties.setSzCs(size);
            parRPr.setSz(size);
            ppr.setRPr(parRPr);
            ppr.setNumPr(numPr);


            // The <w:numId> element
            PPrBase.NumPr.NumId numIdElement = factory.createPPrBaseNumPrNumId();
            numPr.setNumId(numIdElement);
            numIdElement.setVal(BigInteger.valueOf(1));
            wordMLPackage.getMainDocumentPart().getContent().add(index, p);
          }
          return;
        }
      }
    }

  }

  /**
   * Adds the bullet list with custom properties without removing the placeholder.
   *
   * @param wordMLPackage the wordMLPackage
   * @param replaceList the replaceList
   * @param placeholder the placeholder
   * @param bulletListProperties the bulletListProperties
   */
  private void duplicate(WordprocessingMLPackage wordMLPackage, List<String> replaceList,
    String placeholder, Map<String, String> bulletListProperties) {

    String DEFAULT_FONT_SIZE = "18";
    String DEFAULT_BULLET_MARGIN = "15";
    String fontsize =
      (bulletListProperties.get("fontSize") != null && !bulletListProperties.get("fontSize")
        .isEmpty())
        ? bulletListProperties.get("fontSize")
        : DEFAULT_FONT_SIZE;
    String bulletMargin =
      (bulletListProperties.get("bulletMargin") != null && !bulletListProperties.get("bulletMargin")
        .isEmpty())
        ? bulletListProperties.get("bulletMargin")
        : DEFAULT_BULLET_MARGIN;

    ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    List<Object> paragraphs = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), P.class);

    for (Object par : paragraphs) {
      P p = (P) par;
      List list = wordMLPackage.getMainDocumentPart().getContent();
      // Workaround for table being wrapped in JAXBElement
      // This simple code assumes table is present and top level
      int index = 0;
      for (Object o : list) {
        if (XmlUtils.unwrap(o) == par) {
          break;
        }
        index++;
      }
      List<Object> texts = getAllElementFromObject(par, Text.class);
      for (Object t : texts) {
        Text text = (Text) t;
        if (text.getValue().contains(placeholder)) {
          for (String replacer : replaceList) {

            p = factory.createP();
            R rspc = factory.createR();

            text = factory.createText();
            text.setValue(replacer);
            rspc.getContent().add(text);

            RPr runProperties = factory.createRPr();

            setFontSize(runProperties, fontsize);

            rspc.setRPr(runProperties);

            p.getContent().add(rspc);

            org.docx4j.wml.PPr ppr = factory.createPPr();

            p.setPPr(ppr);
            // Create and add <w:numPr>
            PPrBase.NumPr numPr = factory.createPPrBaseNumPr();
            ParaRPr parRPr = factory.createParaRPr();

            HpsMeasure size = new HpsMeasure();
            size.setVal(new BigInteger(fontsize));
            runProperties.setSz(size);
            runProperties.setSzCs(size);
            parRPr.setSz(size);
            ppr.setRPr(parRPr);

            // The <w:numId> element
            PPrBase.NumPr.NumId numIdElement = factory.createPPrBaseNumPrNumId();
            numPr.setNumId(numIdElement);
            numIdElement.setVal(new BigInteger(bulletMargin));
            ppr.setNumPr(numPr);

            // The <w:spacing> element
            if (bulletListProperties.get("line") != null
              && bulletListProperties.get("lineRule") != null) {
              Spacing spacing = new Spacing();
              spacing.setLine(new BigInteger(bulletListProperties.get("line")));
              spacing
                .setLineRule(STLineSpacingRule.fromValue(bulletListProperties.get("lineRule")));
              ppr.setSpacing(spacing);
            }
            wordMLPackage.getMainDocumentPart().getContent().add(index, p);
          }
          return;
        }
      }
    }
  }

  /**
   * Replace footer placeholders.
   *
   * @param wordMLPackage the word ML package
   * @param mappings the mappings
   */
  private void replaceFooterPlaceholders(WordprocessingMLPackage wordMLPackage,
      Map<String, String> mappings) {
    try {
      List<SectionWrapper> sectionWrappers = wordMLPackage.getDocumentModel().getSections();
      String xml = null;
      for (SectionWrapper sw : sectionWrappers) {
        HeaderFooterPolicy hfp = sw.getHeaderFooterPolicy();
        if (hfp != null) {
          FooterPart footerPart = hfp.getFirstFooter();
          if (footerPart != null) {
            xml = XmlUtils.marshaltoString(footerPart.getContents());
          }
          Object obj = null;

          if (xml != null) {
            obj = XmlUtils.unmarshallFromTemplate(xml, mappings);
          }
          // Inject result into docx
          if (obj != null && footerPart != null) {
            footerPart.setJaxbElement((Ftr) obj);
          }
          String defaultXml = null;
          FooterPart defaultFooter = hfp.getDefaultFooter();
          if (defaultFooter != null) {
            defaultXml = XmlUtils.marshaltoString(defaultFooter.getContents());
          }
          Object defaultObj = null;

          if (defaultXml != null) {
            defaultObj = XmlUtils.unmarshallFromTemplate(defaultXml, mappings);
          }
          // Inject result into docx
          if (defaultObj != null && defaultFooter != null) {
            defaultFooter.setJaxbElement((Ftr) defaultObj);
          }
        }
      }
    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException(
          "Error occured during placeholder replacement on footer.");
    }
  }

  /**
   * Replace header and footer placeholders.
   *
   * @param wordMLPackage the word ML package
   * @param mappings the mappings
   */
  private void replaceHeaderAndFooterPlaceholders(WordprocessingMLPackage wordMLPackage,
      Map<String, String> mappings) {
    replaceHeaderPlaceholders(wordMLPackage, mappings);
    replaceFooterPlaceholders(wordMLPackage, mappings);

  }

  /**
   * Generate text with listed placeholder.
   *
   * @param documentPart the document part
   */
  private void generateTextWithListedPlaceholder(MainDocumentPart documentPart) {
    String xml1;
    try {
      xml1 = XmlUtils.marshaltoString(documentPart.getContents(), true);

      Object obj1 = null;
      if (xml1 != null) {
        // replace every occurrence of \n with an new line
        xml1 = xml1.replaceAll("(\n|\\\\n)", "</w:t><w:br/><w:t>");
        obj1 = XmlUtils.unmarshalString(xml1);
      }
      if (obj1 != null) {
        documentPart.setJaxbElement((Document) obj1);
      }
    } catch (Docx4JException | JAXBException e) {
      throw new QTemplateServiceException(
          "Text in list on main document part cannot be generated.");
    }
  }

  @Override
  public ByteArrayOutputStream generateExcelSpreadsheet(List<String> xlsxHeader,
      List<LinkedHashMap<Integer, String>> xlsxContent) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // Create a new spreadsheet package
    SpreadsheetMLPackage pkg;
    try {

      pkg = SpreadsheetMLPackage.createPackage();

      // Create a new worksheet part and retrieve the sheet data
      WorksheetPart sheet =
          pkg.createWorksheetPart(new PartName("/xl/worksheets/sheet1.xml"), "Sheet 1", 1);

      setSpreadsheetFormat(sheet);

      addContent(sheet, xlsxContent, xlsxHeader);

      pkg.save(baos);

      return baos;


    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException("The excel document cannot be created!");
    }
  }

  /**
   * Adds the content.
   *
   * @param sheet the sheet
   * @param xlsxContent the xlsx content
   * @param xlsxHeader the xlsx header
   */
  private static void addContent(WorksheetPart sheet,
      List<LinkedHashMap<Integer, String>> xlsxContent, List<String> xlsxHeader) {

    // Minimal content already present
    SheetData sheetData;
    try {
      sheetData = sheet.getContents().getSheetData();

      Row rRow = org.xlsx4j.jaxb.Context.getsmlObjectFactory().createRow();
      rRow.setS((long) 0);

      for (int num = 0; num < xlsxHeader.size(); num++) {
        rRow.getC().add(createCell(xlsxHeader.get(num)));
      }
      sheetData.getRow().add(rRow);

      for (LinkedHashMap<Integer, String> map : xlsxContent) {
        Row rColumn = org.xlsx4j.jaxb.Context.getsmlObjectFactory().createRow();
        for (Integer column : map.keySet()) {
          rColumn.getC().add(createCell(map.get(column)));
        }
        sheetData.getRow().add(rColumn);
      }
    } catch (Docx4JException e) {
      throw new QTemplateServiceException("The content of excel spreadsheet cannot be added!");
    }
  }

  /**
   * Creates the cell.
   *
   * @param content the content
   * @return the cell
   */
  private static Cell createCell(String content) {

    Cell cell = org.xlsx4j.jaxb.Context.getsmlObjectFactory().createCell();

    CTXstringWhitespace ctx =
        org.xlsx4j.jaxb.Context.getsmlObjectFactory().createCTXstringWhitespace();
    ctx.setValue(content);

    CTRst ctrst = new CTRst();
    ctrst.setT(ctx);

    cell.setT(STCellType.INLINE_STR);
    cell.setIs(ctrst); // add ctrst as inline string

    return cell;

  }

  /**
   * Sets the spreadsheet format.
   *
   * @param sheet the new spreadsheet format
   */
  private void setSpreadsheetFormat(WorksheetPart sheet) {
    CTSheetFormatPr format = org.xlsx4j.jaxb.Context.getsmlObjectFactory().createCTSheetFormatPr();
    format.setDefaultColWidth(30.0);
    format.setDefaultRowHeight(16.8);
    format.setCustomHeight(Boolean.TRUE);
    try {
      sheet.getContents().setSheetFormatPr(format);
    } catch (Docx4JException e) {
      throw new QTemplateServiceException("The format of spreadsheet cannot be changed!");
    }
  }

  /**
   * Adds the bold style.
   *
   * @param runProperties the run properties
   */
  private static void addBoldStyle(RPr runProperties) {
    BooleanDefaultTrue b = new BooleanDefaultTrue();
    b.setVal(true);
    runProperties.setB(b);
  }

  private static void setItalic(RPr runProperties) {
    BooleanDefaultTrue b = new BooleanDefaultTrue();
    b.setVal(true);
    runProperties.setI(b);
  }


  /**
   * Adds the styling.
   *
   * @param tableCell the table cell
   * @param content the content
   * @param tableProperties the table properties
   * @param bold the bold
   * @param italic the italic
   * @param alignRight the align right
   * @param iconsToReplaced the icons to replaced
   * @param wordMLPackage the word ML package
   */
  private static void addStyling(Tc tableCell, String content, Map<String, String> tableProperties,
      String bold, Boolean italic, Boolean alignRight, List<Map<byte[], String>> iconsToReplaced,
      WordprocessingMLPackage wordMLPackage) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    P paragraph = factory.createP();

    Text text = factory.createText();
    text.setValue(content);
    text.setSpace("preserve");

    R run = factory.createR();
    run.getContent().add(text);

    paragraph.getContent().add(run);
    
    RPr runProperties = factory.createRPr();
    // Set bold.
    if (bold != null && Boolean.valueOf(bold)) {
      addBoldStyle(runProperties);
    }

    // Set font size of cell.
    if (tableProperties != null && tableProperties.get("fontSize") != null) {
      setFontSize(runProperties, tableProperties.get("fontSize"));
    }

    // Set font size of title cell.
    if (tableProperties != null && tableProperties.get("tableTitleFontSize") != null) {
      setFontSize(runProperties, tableProperties.get("tableTitleFontSize"));
    }
    
    // Set fonts.
    if (tableProperties != null
        && (tableProperties.get("fonts") != null || tableProperties.get("fontColor") != null)) {
      setFonts(runProperties, tableProperties.get("fonts"), tableProperties.get("fontColor"));
    }

    // Set character spacing.
    if (tableProperties != null && tableProperties.get("charSpacing") != null) {
      setCharSpacing(runProperties, tableProperties.get("charSpacing"));
    }

    // Set italic.
    if (italic != null && italic) {
      setItalic(runProperties);
    }

    paragraphStyling(paragraph, tableProperties, alignRight);
    
    run.setRPr(runProperties);
    
    if (tableProperties != null && (tableProperties.get("image") != null
        && Boolean.valueOf(tableProperties.get("image")))) {
      addImageOnTableCell(tableCell, content, iconsToReplaced, wordMLPackage, factory, paragraph,
          text, run);
    } else {
      tableCell.getContent().add(paragraph);
    }
   
  }

  private static void addImageOnTableCell(Tc tableCell, String content,
      List<Map<byte[], String>> iconsToReplaced, WordprocessingMLPackage wordMLPackage,
      ObjectFactory factory, P paragraph, Text text, R run) {
    boolean added = false;
    for (Map<byte[], String> icon : iconsToReplaced) {
      for (Entry<byte[], String> entry : icon.entrySet()) {
        if (entry.getValue().equals(content)) {
          BinaryPartAbstractImage imagePartBody = null;
          try {
            imagePartBody =
                BinaryPartAbstractImage.createImagePart(wordMLPackage, entry.getKey());
          } catch (Exception e) {
            throw new QTemplateServiceException(IMAGE_PART_NOT_CREATED);
          }
          Inline inlineBody = createInlineImage(imagePartBody, 0);
          
          String anchorXml = XmlUtils.marshaltoString(inlineBody, true, false, Context.jc,
              Namespaces.NS_WORD12, "anchor", Inline.class);

          org.docx4j.dml.ObjectFactory dmlFactory = new org.docx4j.dml.ObjectFactory();
          org.docx4j.dml.wordprocessingDrawing.ObjectFactory wordDmlFactory =
              new org.docx4j.dml.wordprocessingDrawing.ObjectFactory();
          try {
            Anchor anchor = (Anchor) XmlUtils.unmarshalString(anchorXml, Context.jc, Anchor.class);
            anchor.setSimplePos(dmlFactory.createCTPoint2D());
            anchor.getSimplePos().setX(0L);
            anchor.getSimplePos().setY(0L);
            anchor.setSimplePosAttr(false);
            anchor.setPositionH(wordDmlFactory.createCTPosH());
            anchor.getPositionH().setAlign(STAlignH.LEFT);
            anchor.getPositionH().setRelativeFrom(STRelFromH.COLUMN);
            anchor.setPositionV(wordDmlFactory.createCTPosV());
            anchor.getPositionV().setRelativeFrom(STRelFromV.PAGE);
            anchor.setWrapNone(wordDmlFactory.createCTWrapNone());
            // Now add the inline in w:p/w:r/w:drawing
            // Remove text that has been added.
            run.getContent().remove(text);
            Drawing drawing = factory.createDrawing();
            run.getContent().add(drawing);
            drawing.getAnchorOrInline().add(anchor);
            // Add new paragraph on table cell.
            tableCell.getContent().add(paragraph);
            added = true;
            replaceImageBodyWithPlaceholder(paragraph, tableCell, entry.getValue(), text);
          } catch (JAXBException e) {
            throw new QTemplateServiceException(IMAGE_PART_NOT_CREATED);
          }
          break;
        }
      }
    }
    if (!added) {
      tableCell.getContent().add(paragraph);
    }
  }

  /**
   * Sets the fonts.
   *
   * @param runProperties the run properties
   * @param font the font
   */
  private static void setFonts(RPr runProperties, String font, String fontColor) {
    if (font != null) {
      RFonts rf = new RFonts();
      rf.setAscii(font);
      runProperties.setRFonts(rf);
    }
    
    if (fontColor != null) {
      Color color = new Color();
      color.setVal(fontColor);
      runProperties.setColor(color);
    }
  }

  /**
   * Sets the font size.
   *
   * @param runProperties the run properties
   * @param fontSize the font size
   */
  private static void setFontSize(RPr runProperties, String fontSize) {
    HpsMeasure size = new HpsMeasure();
    size.setVal(new BigInteger(fontSize));
    runProperties.setSz(size);
    runProperties.setSzCs(size);
  }

  /**
   * Sets the character spacing.
   *
   * @param runProperties the run properties
   * @param spacing the character spacing
   */
  private static void setCharSpacing(RPr runProperties, String spacing) {
    CTSignedTwipsMeasure val = new CTSignedTwipsMeasure();
    val.setVal(new BigInteger(spacing));
    runProperties.setSpacing(val);
  }

 
  /**
   * Adds the styled table cell.
   *
   * @param tableRow the table row
   * @param content the content
   * @param tableProperties the table properties
   * @param bold the bold
   * @param italic the italic
   * @param alignRight the align right
   * @param iconsToReplaced the icons to replaced
   * @param wordMLPackage the word ML package
   * @param isLeftCol Boolean for left column
   */
  private static void addStyledTableCell(Tr tableRow, String content,
    Map<String, String> tableProperties, String bold, Boolean italic, Boolean alignRight,
    List<Map<byte[], String>> iconsToReplaced, WordprocessingMLPackage wordMLPackage,
    Boolean isLeftCol) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    Tc tableCell = factory.createTc();

    // Cell properties.
    addCellStyling(tableCell, tableProperties, isLeftCol);

    addStyling(tableCell, content, tableProperties, bold, italic, alignRight, iconsToReplaced,
      wordMLPackage);

    tableRow.getContent().add(tableCell);
  }

  /**
   * Adds the cell styling.
   *
   * @param tableCell the table cell
   * @param tableProperties the table properties
   * @param isLeftCol Boolean for left column
   */
  private static void addCellStyling(Tc tableCell, Map<String, String> tableProperties,
    Boolean isLeftCol) {
    // Set cell width.
    if (tableProperties != null) {
      TcPr tableCellProperties = new TcPr();
      addCellStylingWidth(tableProperties, tableCellProperties, isLeftCol);
      // Set cell margin (Top, Bottom, Right, Left).
      addCellStylingMargins(tableProperties, tableCellProperties);
      // Merge cells of table title row to avoid word wrapping
      if (tableProperties.get("tableTitleGridSpan") != null) {
        GridSpan gridSpan = new GridSpan();
        gridSpan.setVal(new BigInteger(tableProperties.get("tableTitleGridSpan")));
        tableCellProperties.setGridSpan(gridSpan);
      }
      tableCell.setTcPr(tableCellProperties);
    }
  }

  /**
   * Add the table margins.
   *
   * @param tableProperties the tableProperties
   * @param tableCellProperties the tableCellProperties
   */
  private static void addCellStylingMargins(Map<String, String> tableProperties,
    TcPr tableCellProperties) {
    TcMar tcMar = new TcMar();
    if (tableProperties.get("bottomMargin") != null) {
      TblWidth tableWidthBottom = new TblWidth();
      tableWidthBottom.setW(new BigInteger(tableProperties.get("bottomMargin")));
      tcMar.setBottom(tableWidthBottom);
    }
    if (tableProperties.get("topMargin") != null) {
      TblWidth tableWidthTop = new TblWidth();
      tableWidthTop.setW(new BigInteger(tableProperties.get("topMargin")));
      tcMar.setTop(tableWidthTop);
    }
    if (tableProperties.get("rightMargin") != null) {
      TblWidth tableWidthRight = new TblWidth();
      tableWidthRight.setW(new BigInteger(tableProperties.get("rightMargin")));
      tcMar.setRight(tableWidthRight);
    }
    if (tableProperties.get("leftMargin") != null) {
      TblWidth tableWidthLeft = new TblWidth();
      tableWidthLeft.setW(new BigInteger(tableProperties.get("leftMargin")));
      tcMar.setLeft(tableWidthLeft);
    }
    tableCellProperties.setTcMar(tcMar);
  }

  /**
   * Add the table column width.
   *
   * @param tableProperties the tableProperties
   * @param tableCellProperties the tableCellProperties
   * @param isLeftCol Boolean for left column
   */
  private static void addCellStylingWidth(Map<String, String> tableProperties,
    TcPr tableCellProperties, Boolean isLeftCol) {
    if (tableProperties.get("width") != null) {
      TblWidth tableWidth = new TblWidth();
      tableWidth.setType("dxa");
      tableWidth.setW(new BigInteger(tableProperties.get("width")));
      tableCellProperties.setTcW(tableWidth);
    }
    if (tableProperties.get("width-left") != null && Boolean.TRUE.equals(isLeftCol)) {
      TblWidth tableWidth = new TblWidth();
      tableWidth.setType("dxa");
      tableWidth.setW(new BigInteger(tableProperties.get("width-left")));
      tableCellProperties.setTcW(tableWidth);
    }
    if (tableProperties.get("width-right") != null && Boolean.FALSE.equals(isLeftCol)) {
      TblWidth tableWidth = new TblWidth();
      tableWidth.setType("dxa");
      tableWidth.setW(new BigInteger(tableProperties.get("width-right")));
      tableCellProperties.setTcW(tableWidth);
    }
  }

  /**
   * Removes the borders.
   *
   * @param table the table
   * @param removeBorder the remove border
   * @param borderSpace the border space
   */
  private static void removeBorders(Tbl table, boolean removeBorder, String borderSpace,
      String indentLeft) {
    table.setTblPr(new TblPr());
    CTBorder border = new CTBorder();
    border.setColor("auto");
    if (borderSpace != null) {
      border.setSpace(new BigInteger(borderSpace));
    }
    if (removeBorder) {
      border.setVal(STBorder.NONE);
    } else {
      border.setVal(STBorder.SINGLE);
    }
    TblBorders borders = new TblBorders();
    borders.setBottom(border);
    borders.setLeft(border);
    borders.setRight(border);
    borders.setTop(border);
    borders.setInsideH(border);
    borders.setInsideV(border);

    if (indentLeft != null) {
      TblWidth tableWidth = new TblWidth();
      tableWidth.setType("dxa");
      tableWidth.setW(new BigInteger(indentLeft));
      table.getTblPr().setTblInd(tableWidth);
    }
    table.getTblPr().setTblBorders(borders);
  }

  /**
   * Paragraph styling.
   *
   * @param paragraph the paragraph
   * @param tableProperties the table properties
   */
  private static void paragraphStyling(P paragraph, Map<String, String> tableProperties,
      Boolean alignRight) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    PPr paragraphProperties = factory.createPPr();
    BooleanDefaultTrue b = new BooleanDefaultTrue();
    paragraphProperties.setAdjustRightInd(b);
    if (tableProperties != null && tableProperties.get("spacing") != null) {
      Spacing sp = new Spacing();
      sp.setAfter(new BigInteger(tableProperties.get("spacing")));
      paragraphProperties.setSpacing(sp);
    }
    if (tableProperties != null && tableProperties.get("line") != null
      && tableProperties.get("lineRule") != null) {
      Spacing sp = new Spacing();
      sp.setLine(new BigInteger(tableProperties.get("line")));
      sp.setLineRule(STLineSpacingRule.fromValue(tableProperties.get("lineRule")));
      paragraphProperties.setSpacing(sp);
    }
    if ((tableProperties != null && Boolean.valueOf(tableProperties.get("alignRight")))
        || alignRight) {
      Jc jc = new Jc();
      jc.setVal(JcEnumeration.RIGHT);
      paragraphProperties.setJc(jc);
    }
    paragraph.setPPr(paragraphProperties);
  }

  @Override
  public ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, List<String> paragraphList, Integer position) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      // Replace placeholders and paragraph on main part.
      replaceBodyPlaceholdersAndParagraph(wordMLPackage, mappings, paragraphList, position);
      // Replace placeholders on header and footer.
      replaceHeaderAndFooterPlaceholders(wordMLPackage, mappings);
      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  @Override
  public ByteArrayOutputStream replacePlaceholdersWordDoc(InputStream inputStream,
      Map<String, String> mappings, byte[] logo, long imageWidth, List<String> paragraphList,
      Integer position) {
    ByteArrayOutputStream baos = null;
    if (logo != null) {
      baos = replacePlaceHoldersAndLogoDocx(inputStream, mappings, logo, imageWidth, paragraphList,
          position);
    } else {
      baos = replacePlaceholdersWordDoc(inputStream, mappings, paragraphList, position);
    }
    return baos;
  }

  @Override
  public ByteArrayOutputStream replacePlaceholderWithTable(InputStream inputStream,
      List<LinkedHashMap<Map<String, String>, Map<String, String>>> table, String placeholder,
      String identLeft) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      addTable(wordMLPackage, table, placeholder, identLeft);

      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  @Override
  public ByteArrayOutputStream replacePlaceholdersWithTables(InputStream inputStream,
    HashMap<String, List<LinkedHashMap<Map<String, String>, Map<String, String>>>> placeholders,
    String identLeft) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      placeholders
        .forEach((placeholder, table) -> addTable(wordMLPackage, table, placeholder, identLeft));

      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  /**
   * Adds the table.
   *
   * @param wordMLPackage the word ML package
   * @param table the table
   * @param placeholder the placeholder
   * @param identLeft the ident left
   */
  private void addTable(WordprocessingMLPackage wordMLPackage,
      List<LinkedHashMap<Map<String, String>, Map<String, String>>> table, String placeholder,
      String identLeft) {
    ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
    List<Object> paragraphs = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), P.class);
    int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0)
        .getPageDimensions().getWritableWidthTwips();
    Tbl tblCredProg = TblFactory.createTable(0, 2, writableWidthTwips / 2);
    removeBorders(tblCredProg, true, null, identLeft);
    for (Object par : paragraphs) {
      List list = wordMLPackage.getMainDocumentPart().getContent();
      int index = 0;
      for (Object o : list) {
        if (XmlUtils.unwrap(o) == par) {
          break;
        }
        index++;
      }
      List<Object> texts = getAllElementFromObject(par, Text.class);
      for (Object t : texts) {
        Text text = (Text) t;
        if (text.getValue().contains(placeholder)) {
          for (LinkedHashMap<Map<String, String>, Map<String, String>> c : table) {
            for (Entry<Map<String, String>, Map<String, String>> column : c.entrySet()) {
              Tr tr = factory.createTr();
              Map<String, String> map = column.getKey();
              for (Entry<String, String> value1 : map.entrySet()) {
                // styles for 1st column
                addStyledTableCell(tr, value1.getKey(), column.getValue(), null,
                  Boolean.valueOf(column.getValue().get("italic")), false,
                  null, wordMLPackage, Boolean.TRUE);
                // styles for 2nd column
                addStyledTableCell(tr, value1.getValue(), column.getValue(), null,
                  Boolean.valueOf(column.getValue().get("italic")), true,
                  null, wordMLPackage, Boolean.FALSE);
              }
              tblCredProg.getContent().add(tr);
            }
          }
          wordMLPackage.getMainDocumentPart().getContent().add(index, tblCredProg);
          // 4. remove the original one
          remove(wordMLPackage, placeholder);
          return;
        }
      }
    }
  }

  @Override
  public ByteArrayOutputStream replacePlaceholdersWithImage(InputStream inputStream,
      List<Map<byte[], String>> iconsToReplaced) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      // Add logo on document.
      for (Map<byte[], String> icon : iconsToReplaced) {
        for (Entry<byte[], String> entry : icon.entrySet()) {
          addHeaderImage(wordMLPackage, entry.getKey(), 0, entry.getValue());
        }
      }
      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }  

  /**
   * Method that prevents row splitting to a new page.
   *
   * @param Tr the current row
   */
  private void preventRowSplit(Tr tr) {
    TrPr trpr = Context.getWmlObjectFactory().createTrPr();
    tr.setTrPr(trpr);
    BooleanDefaultTrue booleandefaulttrue =
        Context.getWmlObjectFactory().createBooleanDefaultTrue();
    JAXBElement<org.docx4j.wml.BooleanDefaultTrue> booleandefaulttrueWrapped =
        Context.getWmlObjectFactory().createCTTrPrBaseCantSplit(booleandefaulttrue);
    Context.getWmlObjectFactory().createCTTrPrBaseTblHeader(booleandefaulttrue);
    trpr.getCnfStyleOrDivIdOrGridBefore().add(booleandefaulttrueWrapped);
  }

  @Override
  public ByteArrayOutputStream createTableInDocxWithCustomBorders(InputStream inputStream,
      List<String> header, String tableTitle,
      List<LinkedHashMap<Map<String, String>, String>> content, Map<String, String> tableProperties,
      List<Map<byte[], String>> iconsToBeReplaced) {

    TblBorders borders = new TblBorders();

    // Create custom outside border.
    CTBorder outsideBorder = new CTBorder();
    outsideBorder.setColor("auto");
    outsideBorder.setSz(BigInteger.valueOf(5));
    outsideBorder.setSpace(BigInteger.valueOf(0));
    outsideBorder.setVal(STBorder.SINGLE);

    // Set outside borders.
    borders.setBottom(outsideBorder);
    borders.setLeft(outsideBorder);
    borders.setRight(outsideBorder);
    borders.setTop(outsideBorder);

    // Create custom inside border.
    CTBorder insideBorder = new CTBorder();
    insideBorder.setColor("auto");
    insideBorder.setSz(BigInteger.valueOf(2));
    insideBorder.setSpace(BigInteger.valueOf(0));
    insideBorder.setVal(STBorder.SINGLE);

    // Set inside borders.
    borders.setInsideH(insideBorder);
    borders.setInsideV(insideBorder);

    ObjectFactory factory = Context.getWmlObjectFactory();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0)
          .getPageDimensions().getWritableWidthTwips();
      Tbl tblCredProg =
          TblFactory.createTable(0, header.size(), writableWidthTwips / header.size());
      removeBorders(tblCredProg, Boolean.valueOf(tableProperties.get("removeBorder")),
          tableProperties.get("borderSpace"), null);
      
      tblCredProg.getTblPr().setTblBorders(borders);

      // Add table title (row).
      if (tableTitle != null) {
        Tr tTitle = factory.createTr();
        addStyledTableCell(tTitle, tableTitle, tableProperties, tableProperties.get("boldHeader"),
          null, false, null, wordMLPackage, null);

        if (tableProperties.get("repeatHeader") != null
            && tableProperties.get("repeatHeader").equals(Boolean.TRUE.toString())) {
          repeatTableHeader(tTitle);
        }
        tableProperties.remove("tableTitleGridSpan");
        tableProperties.remove("tableTitleFontSize");
        tblCredProg.getContent().add(tTitle);
      }

      // Add table header (row).
      Tr thead = factory.createTr();
      for (int num = 0; num < header.size(); num++) {
        // align right cell content if title exists
        if (num == 4 && tableTitle != null) {
          tableProperties.put("alignRight", String.valueOf(Boolean.TRUE));
          addStyledTableCell(thead, header.get(num), tableProperties,
            tableProperties.get("boldHeader"), null, true, null, wordMLPackage, null);
          tableProperties.remove("alignRight");
        } else {
          addStyledTableCell(thead, header.get(num), tableProperties,
            tableProperties.get("boldHeader"), null, false, null, wordMLPackage, null);
        }
      }
      if (tableProperties.get("repeatHeader") != null
          && tableProperties.get("repeatHeader").equals(Boolean.TRUE.toString())) {
        repeatTableHeader(thead);
      }
      tblCredProg.getContent().add(thead);

      // variable for the last row of table
      int lastRowCounter = 0;
      // Add table content (the content is added by row).
      for (Map<Map<String, String>, String> c : content) {
        Tr tr = factory.createTr();

        // prevent row from splitting to a new page
        // instead move whole row to next page
        preventRowSplit(tr);

        for (Entry<Map<String, String>, String> column : c.entrySet()) {
          addStyledTableCell(tr, column.getValue(), column.getKey(),
            column.getKey().get("boldContent"), null, false, iconsToBeReplaced, wordMLPackage,
            null);
        }
        if (lastRowCounter == content.size() - 1) {
          keepLastRowWithParagraph(tr);
        }
        tblCredProg.getContent().add(tr);
        lastRowCounter++;
      }

      if (tableProperties.get("tablePosition") != null) {
        // Set table specific position.
        wordMLPackage.getMainDocumentPart().getContent()
            .add(Integer.parseInt(tableProperties.get("tablePosition")), tblCredProg);
      } else {
        wordMLPackage.getMainDocumentPart().getContent().add(tblCredProg);
      }

      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }
}
