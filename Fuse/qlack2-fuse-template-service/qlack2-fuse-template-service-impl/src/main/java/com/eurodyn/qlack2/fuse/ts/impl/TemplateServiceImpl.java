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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.HeaderFooterPolicy;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.Document;
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
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSheetFormatPr;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.SheetData;

import com.eurodyn.qlack2.fuse.ts.api.TemplateService;
import com.eurodyn.qlack2.fuse.ts.exception.QTemplateServiceException;

public class TemplateServiceImpl implements TemplateService {

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
      throw new QTemplateServiceException("The document cannot be created!");
    }

    return baos;

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
      tblCredProg.getContent().add(thead);

      // Add table content (the content is added by row).
      for (Map<Map<String, String>, String> c : content) {
        Tr tr = factory.createTr();

        for (Entry<Map<String, String>, String> column : c.entrySet()) {
          addStyledTableCell(tr, column.getValue(), column.getKey(),
              column.getKey().get("boldContent"));
        }
        tblCredProg.getContent().add(tr);
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
      throw new QTemplateServiceException("The document cannot be created!");
    }
    return baos;
  }


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
        }
      }
    } catch (JAXBException | Docx4JException e) {
      throw new QTemplateServiceException(
          "Error occured during placeholder replacement on footer.");
    }
  }

  private void replaceHeaderAndFooterPlaceholders(WordprocessingMLPackage wordMLPackage,
      Map<String, String> mappings) {
    replaceHeaderPlaceholders(wordMLPackage, mappings);
    replaceFooterPlaceholders(wordMLPackage, mappings);

  }

  private void generateTextWithListedPlaceholder(MainDocumentPart documentPart) {
    String xml1;
    try {
      xml1 = XmlUtils.marshaltoString(documentPart.getContents(), true);

      Object obj1 = null;
      if (xml1 != null) {
        xml1 = xml1.replaceAll("\n", "</w:t><w:br/><w:t>");
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

  private static void addBoldStyle(RPr runProperties) {
    BooleanDefaultTrue b = new BooleanDefaultTrue();
    b.setVal(true);
    runProperties.setB(b);
  }

  private static void addStyling(Tc tableCell, String content, Map<String, String> tableProperties,
      String bold) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    P paragraph = factory.createP();

    Text text = factory.createText();
    text.setValue(content);

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

  private static void setFonts(RPr runProperties, String font) {
    RFonts rf = new RFonts();
    rf.setAscii(font);
    runProperties.setRFonts(rf);
  }

  private static void setFontSize(RPr runProperties, String fontSize) {
    HpsMeasure size = new HpsMeasure();
    size.setVal(new BigInteger(fontSize));
    runProperties.setSz(size);
    runProperties.setSzCs(size);
  }

  private static void addStyledTableCell(Tr tableRow, String content,
      Map<String, String> tableProperties, String bold) {
    ObjectFactory factory = Context.getWmlObjectFactory();
    Tc tableCell = factory.createTc();

    // Cell properties.
    addCellStyling(tableCell, tableProperties);

    addStyling(tableCell, content, tableProperties, bold);

    tableRow.getContent().add(tableCell);
  }

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
