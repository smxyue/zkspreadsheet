package org.zkoss.zss.ngmodel;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.Locale;

import org.junit.*;
import org.zkoss.zss.ngapi.NImporter;
import org.zkoss.zss.ngapi.impl.ExcelImportFactory;
import org.zkoss.zss.ngmodel.NCellStyle.Alignment;
import org.zkoss.zss.ngmodel.NCellStyle.VerticalAlignment;
import org.zkoss.zss.ngmodel.NFont.TypeOffset;

/**
 * @author Hawk
 */
public class ImporterTest {
	
	static private File fileUnderTest;
	private NImporter importer; 
	
	/**
	 * For exporter test to specify its exported file to test.
	 * @param file
	 */
	static public void setFileUnderTest(File file){
		fileUnderTest = file;
	}
	
	@BeforeClass
	static public void initialize(){
		try{
			fileUnderTest = new File(ImporterTest.class.getResource("book/import.xlsx").toURI());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void prepare(){
		importer= new ExcelImportFactory().createImporter();
	}
	
	//API
	
	@Test
	public void importByInputStream(){
		InputStream streamUnderTest = ImporterTest.class.getResourceAsStream("book/import.xlsx");
		NBook book = null;
		try {
			book = importer.imports(streamUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals("XSSFBook", book.getBookName());
		assertEquals(6, book.getNumOfSheet());
	}
	
	@Test
	public void importByUrl(){
		URL surlUnderTest = ImporterTest.class.getResource("book/import.xlsx");
		NBook book = null;
		try {
			book = importer.imports(surlUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals("XSSFBook", book.getBookName());
		assertEquals(6, book.getNumOfSheet());
	}
	
	@Test
	public void importByFile() {
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals("XSSFBook", book.getBookName());
		assertEquals(6, book.getNumOfSheet());
	}
	
	//content
	@Test
	public void sheet() {
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals(6, book.getNumOfSheet());

		NSheet sheet1 = book.getSheet(0);
		assertEquals("Value", sheet1.getSheetName());
		assertEquals(21, sheet1.getDefaultRowHeight());
		assertEquals(72, sheet1.getDefaultColumnWidth());
		
		NSheet sheet2 = book.getSheet(1);
		assertEquals("Style", sheet2.getSheetName());
		NSheet sheet3 = book.getSheet(2);
		assertEquals("Third", sheet3.getSheetName());
	}	

	@Test
	public void cellValueTest() {
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		NSheet sheet = book.getSheet(0);
		//text
		NRow row = sheet.getRow(0);
		assertEquals(NCell.CellType.STRING, row.getCell(1).getType());
		assertEquals("B1", row.getCell(1).getStringValue());
		assertEquals("C1", row.getCell(2).getStringValue());
		assertEquals("D1", row.getCell(3).getStringValue());
		
		//number
		NRow row1 = sheet.getRow(1);
		assertEquals(NCell.CellType.NUMBER, row1.getCell(1).getType());
		assertEquals(123, row1.getCell(1).getNumberValue().intValue());
		assertEquals(123.45, row1.getCell(2).getNumberValue().doubleValue(), 0.01);
		
		//date
		NRow row2 = sheet.getRow(2);
		assertEquals(NCell.CellType.NUMBER, row2.getCell(1).getType());
		assertEquals(41618, row2.getCell(1).getNumberValue().intValue());
		assertEquals("Dec 10, 2013", DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(row2.getCell(1).getDateValue()));
		assertEquals(0.61, row2.getCell(2).getNumberValue().doubleValue(), 0.01);
		assertEquals("2:44:10 PM", DateFormat.getTimeInstance (DateFormat.MEDIUM, Locale.US).format(row2.getCell(2).getDateValue()));
		
		//formula
		NRow row3 = sheet.getRow(3);
		assertEquals(NCell.CellType.FORMULA, row3.getCell(1).getType());
		assertEquals("SUM(10,20)", row3.getCell(1).getFormulaValue());
		assertEquals("ISBLANK(B1)", row3.getCell(2).getFormulaValue());
		assertEquals("B1", row3.getCell(3).getFormulaValue());
		
		//error
		NRow row4 = sheet.getRow(4);
		assertEquals(NCell.CellType.ERROR, row4.getCell(1).getType());
		assertEquals(ErrorValue.INVALID_NAME, row4.getCell(1).getErrorValue().getCode());
		assertEquals(ErrorValue.INVALID_VALUE, row4.getCell(2).getErrorValue().getCode());
		
		//blank
		NRow row5 = sheet.getRow(5);
		assertEquals(NCell.CellType.BLANK, row5.getCell(1).getType());
		assertEquals("", row5.getCell(1).getStringValue());
	}
	
	@Test
	public void cellStyleTest(){
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		NSheet sheet = book.getSheetByName("Style");
		assertEquals(true, sheet.getCell(24, 1).getCellStyle().isWrapText());
		//alignment
		assertEquals(VerticalAlignment.TOP, sheet.getCell(26, 1).getCellStyle().getVerticalAlignment());
		assertEquals(VerticalAlignment.CENTER, sheet.getCell(26, 2).getCellStyle().getVerticalAlignment());
		assertEquals(VerticalAlignment.BOTTOM, sheet.getCell(26, 3).getCellStyle().getVerticalAlignment());
		assertEquals(Alignment.LEFT, sheet.getCell(27, 1).getCellStyle().getAlignment());
		assertEquals(Alignment.CENTER, sheet.getCell(27, 2).getCellStyle().getAlignment());
		assertEquals(Alignment.RIGHT, sheet.getCell(27, 3).getCellStyle().getAlignment());
		//cell filled color
		assertEquals("#FF0000", sheet.getCell(11, 0).getCellStyle().getFillColor().getHtmlColor());
		assertEquals("#00FF00", sheet.getCell(11, 1).getCellStyle().getFillColor().getHtmlColor());
		assertEquals("#0000FF", sheet.getCell(11, 2).getCellStyle().getFillColor().getHtmlColor());
		
		NSheet protectedSheet = book.getSheetByName("sheet-protection");
		assertEquals(true, protectedSheet.getCell(0, 0).getCellStyle().isLocked());
		assertEquals(false, protectedSheet.getCell(1, 0).getCellStyle().isLocked());
		
		//FIXME
//		NSheet columnSheet = book.getSheetByName("column");
//		assertEquals(true, columnSheet.getColumn(4).getCellStyle().isHidden());
//		assertEquals(true, columnSheet.getCell(5, 4).getCellStyle().isHidden());
	}
		

	@Test
	public void cellFontNameTest(){
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		NSheet sheet = book.getSheetByName("Style");
		assertEquals("Arial", sheet.getCell(3, 0).getCellStyle().getFont().getName());
		assertEquals("Arial Black", sheet.getCell(3, 1).getCellStyle().getFont().getName());
		assertEquals("Calibri", sheet.getCell(3, 2).getCellStyle().getFont().getName());
	}
	
	@Test
	public void cellFontStyleTest(){
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		NSheet sheet = book.getSheetByName("Style");
		assertEquals(NFont.Boldweight.BOLD, sheet.getCell(9, 0).getCellStyle().getFont().getBoldweight());
		assertTrue(sheet.getCell(9, 1).getCellStyle().getFont().isItalic());
		assertTrue(sheet.getCell(9, 2).getCellStyle().getFont().isStrikeout());
		assertEquals(NFont.Underline.SINGLE, sheet.getCell(9, 3).getCellStyle().getFont().getUnderline());
		assertEquals(NFont.Underline.DOUBLE, sheet.getCell(9, 4).getCellStyle().getFont().getUnderline());
		assertEquals(NFont.Underline.SINGLE_ACCOUNTING, sheet.getCell(9, 5).getCellStyle().getFont().getUnderline());
		assertEquals(NFont.Underline.DOUBLE_ACCOUNTING, sheet.getCell(9, 6).getCellStyle().getFont().getUnderline());
		assertEquals(NFont.Underline.NONE, sheet.getCell(9, 7).getCellStyle().getFont().getUnderline());
		
		//height
		assertEquals(8, sheet.getCell(6, 0).getCellStyle().getFont().getHeightPoints());
		assertEquals(72, sheet.getCell(6, 3).getCellStyle().getFont().getHeightPoints());
		
		//type offset
		assertEquals(TypeOffset.SUPER, sheet.getCell(32, 1).getCellStyle().getFont().getTypeOffset());
		assertEquals(TypeOffset.SUB, sheet.getCell(32, 2).getCellStyle().getFont().getTypeOffset());
		assertEquals(TypeOffset.NONE, sheet.getCell(32, 3).getCellStyle().getFont().getTypeOffset());
	}
	
	@Test
	public void cellFontColorTest(){
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		NSheet sheet = book.getSheetByName("Style");
		assertEquals("#000000", sheet.getCell(0, 0).getCellStyle().getFont().getColor().getHtmlColor());
		assertEquals("#FF0000", sheet.getCell(1, 0).getCellStyle().getFont().getColor().getHtmlColor());
		assertEquals("#00FF00", sheet.getCell(1, 1).getCellStyle().getFont().getColor().getHtmlColor());
		assertEquals("#0000FF", sheet.getCell(1, 2).getCellStyle().getFont().getColor().getHtmlColor());
		
	}
	
	@Test
	public void rowTest(){
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		NSheet sheet = book.getSheetByName("Style");
		assertEquals(28, sheet.getRow(0).getHeight());
		assertEquals(20, sheet.getRow(1).getHeight());
		//style
		NCellStyle rowStyle1 = sheet.getRow(34).getCellStyle();
		assertEquals("#0000FF",rowStyle1.getFont().getColor().getHtmlColor());
		assertEquals(12,rowStyle1.getFont().getHeightPoints());
		NCellStyle rowStyle2 = sheet.getRow(35).getCellStyle();
		assertEquals(true,rowStyle2.getFont().isItalic());
		assertEquals(14,rowStyle2.getFont().getHeightPoints());		
		
	}

	@Test
	public void cellFormatTest(){
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		NSheet sheet = book.getSheetByName("Format");
		assertEquals("#,##0.00", sheet.getCell(1, 1).getCellStyle().getDataFormat());
		assertEquals("\"NT$\"#,##0.00", sheet.getCell(1, 2).getCellStyle().getDataFormat());
		assertEquals("yyyy/m/d", sheet.getCell(1, 4).getCellStyle().getDataFormat());
//		assertEquals("hh:mm AM/PM", sheet.getCell(1, 5).getCellStyle().getDataFormat());
		assertEquals("0.0%", sheet.getCell(1, 6).getCellStyle().getDataFormat());
		assertEquals("# ??/??", sheet.getCell(3, 1).getCellStyle().getDataFormat());
	}
	
	@Test
	public void columnTest(){
		NBook book = null;
		try {
			book = importer.imports(fileUnderTest, "XSSFBook");
		} catch (IOException e) {
			e.printStackTrace();
		}
		NSheet sheet = book.getSheetByName("Value");
		assertEquals(NFont.Boldweight.BOLD, sheet.getColumn(0).getCellStyle().getFont().getBoldweight());
		//TODO color of column style 
		//FIXME width unit issue
//		assertEquals(209, sheet.getColumn(0).getWidth());
//		assertEquals(183, sheet.getColumn(1).getWidth());
	}
}
