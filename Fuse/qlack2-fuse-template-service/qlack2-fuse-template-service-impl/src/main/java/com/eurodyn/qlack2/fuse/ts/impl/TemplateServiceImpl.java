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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
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
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTBorder;
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
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcMar;
import org.docx4j.wml.TcPr;
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
import com.eurodyn.qlack2.fuse.ts.api.TemplateService;
import com.eurodyn.qlack2.fuse.ts.exception.QTemplateServiceException;

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
      Map<String, String> mappings, byte[] logo, long imageWidth) {
    ByteArrayOutputStream baos = null;
    if (logo != null) {
      baos = replacePlaceHoldersAndLogoDocx(inputStream, mappings, logo, imageWidth);
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
      Map<String, String> mappings, byte[] logo, long imageWidth) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      // Replace placeholders on main part.
      replaceBodyPlaceholders(wordMLPackage, mappings);
      // Replace placeholders on header and footer.
      replaceHeaderAndFooterPlaceholders(wordMLPackage, mappings);
      // Add logo on document.
      addHeaderImage(wordMLPackage, logo, imageWidth);
      Docx4J.save(wordMLPackage, baos);

    } catch (Docx4JException e) {
      throw new QTemplateServiceException(NO_DOCUMENT_CREATED);
    }
    return baos;
  }

  /**
   * Adds the header image.
   *
   * @param wordMLPackage the word ML package
   * @param logo the logo
   */
  private void addHeaderImage(WordprocessingMLPackage wordMLPackage, byte[] logo, long imageWidth) {
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
      setParagraphAlignment(paragraph);
    }

    Inline inlineBody = createInlineImage(imagePartBody, imageWidth);
    P paragraphBody = addInlineImageToParagraph(inlineBody);
    setParagraphAlignment(paragraphBody);

    List<Object> elements = null;
    List<Object> elementsBody = null;
    if (headerPart != null) {
      elements = getAllElementFromObject(headerPart, Tbl.class);
    }
    elementsBody = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), Tbl.class);

    replaceImage(paragraph, elements);
    replaceImage(paragraphBody, elementsBody);
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
  private void setParagraphAlignment(P paragraph) {
    ObjectFactory factory = new ObjectFactory();
    PPr paragraphProperties = factory.createPPr();
    Jc justification = factory.createJc();
    justification.setVal(JcEnumeration.CENTER);
    paragraphProperties.setJc(justification);
    paragraph.setPPr(paragraphProperties);
  }

  /**
   * Method that creates the inline image.
   *
   * @param imagePart the image part
   * @return the inline
   */
  private Inline createInlineImage(BinaryPartAbstractImage imagePart, long imageWidth) {
    int docPrId = 1;
    int cNvPrId = 2;
    Inline inline = null;
    try {
      if (imagePart != null) {
        if (imageWidth > 0) {
          inline = imagePart.createImageInline("", "Image", docPrId, cNvPrId, imageWidth, false);
        } else {
          inline = imagePart.createImageInline("", "Image", docPrId, cNvPrId, false);
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
  public void replaceImage(P paragraph, List<Object> elements) {
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
                if (text.getValue().equals(TENANT_LOGO)) {
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
  private List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
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
      List<String> header, List<LinkedHashMap<Map<String, String>, String>> content,
      Map<String, String> tableProperties) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

      int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0)
          .getPageDimensions().getWritableWidthTwips();
      Tbl tblCredProg =
          TblFactory.createTable(0, header.size(), writableWidthTwips / header.size());
      removeBorders(tblCredProg, Boolean.valueOf(tableProperties.get("removeBorder")), tableProperties.get("borderSpace"));
      // Add table header (row).
      Tr thead = factory.createTr();
      for (int num = 0; num < header.size(); num++) {
        addStyledTableCell(thead, header.get(num), tableProperties,
            tableProperties.get("boldHeader"));
      }
      if (tableProperties.get("repeatHeader") != null && tableProperties.get("repeatHeader").equals(Boolean.TRUE.toString())) {
        repeatTableHeader(thead);
      }
      tblCredProg.getContent().add(thead);

      // variable for the last row of table
      int lastRowCounter = 0;
      // Add table content (the content is added by row).
      for (Map<Map<String, String>, String> c : content) {
        Tr tr = factory.createTr();
        
        for (Entry<Map<String, String>, String> column : c.entrySet()) {
          addStyledTableCell(tr, column.getValue(), column.getKey(),
              column.getKey().get("boldContent"));
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
        xml1 = xml1.replaceAll("\\\\n", "</w:t><w:br/><w:t>");
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

  /**
   * Adds the styling.
   *
   * @param tableCell the table cell
   * @param content the content
   * @param tableProperties the table properties
   * @param bold the bold
   */
  private static void addStyling(Tc tableCell, String content, Map<String, String> tableProperties,
      String bold) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    P paragraph = factory.createP();

    Text text = factory.createText();
    text.setValue(content);
    text.setSpace("preserve");

    R run = factory.createR();
    run.getContent().add(text);

    paragraph.getContent().add(run);

    RPr runProperties = factory.createRPr();
    // Set bold
    if (bold != null && Boolean.valueOf(bold)) {
      addBoldStyle(runProperties);
    }

    // Set font size of cell.
    if (tableProperties.get("fontSize") != null) {
      setFontSize(runProperties, tableProperties.get("fontSize"));
    }

    // Set fonts.
    if (tableProperties.get("fonts") != null) {
      setFonts(runProperties, tableProperties.get("fonts"));
    }
    paragraphStyling(paragraph,tableProperties);
    
    run.setRPr(runProperties);

    tableCell.getContent().add(paragraph);
  }

  /**
   * Sets the fonts.
   *
   * @param runProperties the run properties
   * @param font the font
   */
  private static void setFonts(RPr runProperties, String font) {
    RFonts rf = new RFonts();
    rf.setAscii(font);
    runProperties.setRFonts(rf);
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
   * Adds the styled table cell.
   *
   * @param tableRow the table row
   * @param content the content
   * @param tableProperties the table properties
   * @param bold the bold
   */
  private static void addStyledTableCell(Tr tableRow, String content,
      Map<String, String> tableProperties, String bold) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    Tc tableCell = factory.createTc();

    // Cell properties.
    addCellStyling(tableCell, tableProperties);

    addStyling(tableCell, content, tableProperties, bold);

    tableRow.getContent().add(tableCell);
  }

  /**
   * Adds the cell styling.
   *
   * @param tableCell the table cell
   * @param tableProperties the table properties
   */
  private static void addCellStyling(Tc tableCell, Map<String, String> tableProperties) {
    // Set cell width.
    TcPr tableCellProperties = new TcPr();
    if (tableProperties.get("width") != null) {
      TblWidth tableWidth = new TblWidth();
      tableWidth.setType("dxa");
      tableWidth.setW(new BigInteger(tableProperties.get("width")));
      tableCellProperties.setTcW(tableWidth);
    }

    // Set cell margin (Top, Bottom, Right, Left).
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

    tableCell.setTcPr(tableCellProperties);
  }

  /**
   * Removes the borders.
   *
   * @param table the table
   * @param removeBorder the remove border
   * @param borderSpace the border space
   */
  private static void removeBorders(Tbl table, boolean removeBorder, String borderSpace) {
    table.setTblPr(new TblPr());
    CTBorder border = new CTBorder();
    border.setColor("auto");
    if (borderSpace != null) {
      border.setSpace(new BigInteger(borderSpace));
    }
    if (removeBorder) {
      border.setVal(STBorder.NONE);
    } else {
      border.setVal(STBorder.BASIC_THIN_LINES);
    }
    TblBorders borders = new TblBorders();
    borders.setBottom(border);
    borders.setLeft(border);
    borders.setRight(border);
    borders.setTop(border);
    borders.setInsideH(border);
    borders.setInsideV(border);
    table.getTblPr().setTblBorders(borders);
  }
  
  /**
   * Paragraph styling.
   *
   * @param paragraph the paragraph
   * @param tableProperties the table properties
   */
  private static void paragraphStyling(P paragraph,Map<String, String> tableProperties) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    PPr paragraphProperties = factory.createPPr();
    BooleanDefaultTrue b = new BooleanDefaultTrue();
    paragraphProperties.setAdjustRightInd(b);
    if (tableProperties.get("spacing") != null) {
      Spacing sp = new Spacing();
      sp.setAfter(new BigInteger(tableProperties.get("spacing")));
      paragraphProperties.setSpacing(sp);
    }
    if (Boolean.valueOf(tableProperties.get("alignRight"))) {
      Jc jc = new Jc();
      jc.setVal(JcEnumeration.RIGHT);
      paragraphProperties.setJc(jc);
    }
    paragraph.setPPr(paragraphProperties);
  }
}
