package org.zkoss.zss.api;

import java.util.List;

import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.api.model.CellData;
import org.zkoss.zss.api.model.CellStyle;
import org.zkoss.zss.api.model.Color;
import org.zkoss.zss.api.model.Font;
import org.zkoss.zss.api.model.CellStyle.BorderType;
import org.zkoss.zss.api.model.Chart;
import org.zkoss.zss.api.model.Chart.Grouping;
import org.zkoss.zss.api.model.Chart.LegendPosition;
import org.zkoss.zss.api.model.Chart.Type;
import org.zkoss.zss.api.model.ChartData;
import org.zkoss.zss.api.model.Font.Boldweight;
import org.zkoss.zss.api.model.Font.TypeOffset;
import org.zkoss.zss.api.model.Font.Underline;
import org.zkoss.zss.api.model.Hyperlink;
import org.zkoss.zss.api.model.Hyperlink.HyperlinkType;
import org.zkoss.zss.api.model.Picture;
import org.zkoss.zss.api.model.Picture.Format;
import org.zkoss.zss.api.model.Sheet;

/**
 * Range that represents a cell, a row, a column, or selection of cells containing one or 
 * more contiguous blocks of cells, or a 3-D blocks of cells. <br/>
 * You have to use this class's API to do any operation of the {@link Sheet}, then the upload will sync to the UI automatically.<br/>
 * To get the instance of a {@link Range}, please use the {@link Ranges} API.
 * 
 * @author dennis
 * @see Ranges
 * @since 3.0.0
 */
public interface Range {
	
	public enum SyncLevel{
		BOOK,
		NONE//for you just visit and do nothing
	}
	
	public enum PasteType{
		ALL,
		ALL_EXCEPT_BORDERS,
		COLUMN_WIDTHS,
		COMMENTS,
		FORMATS/*all formats*/,
		FORMULAS/*include values and formulas*/,
		FORMULAS_AND_NUMBER_FORMATS,
		VALIDATAION,
		VALUES,
		VALUES_AND_NUMBER_FORMATS;
	}
	
	public enum PasteOperation{
		ADD,
		SUB,
		MUL,
		DIV,
		NONE;
	}
	
	public enum ApplyBorderType{
		FULL,
		EDGE_BOTTOM,
		EDGE_RIGHT,
		EDGE_TOP,
		EDGE_LEFT,
		OUTLINE,
		INSIDE,
		INSIDE_HORIZONTAL,
		INSIDE_VERTICAL,
		DIAGONAL,
		DIAGONAL_DOWN,
		DIAGONAL_UP
	}
	
	/** Shift direction of insert and delete**/
	public enum InsertShift{
		DEFAULT,
		RIGHT,
		DOWN
	}
	/** copy origin of insert and delete**/
	public enum InsertCopyOrigin{
		NONE,
		LEFT_ABOVE,
		RIGHT_BELOW,
	}
	/** Shift direction of insert and delete**/
	public enum DeleteShift{
		DEFAULT,
		LEFT,
		UP
	}
	
	public enum SortDataOption{
		NORMAL_DEFAULT,
		TEXT_AS_NUMBERS
	}
	
	public enum AutoFilterOperation{
		AND,
		BOTTOM10,
		BOTOOM10PERCENT,
		OR,
		TOP10,
		TOP10PERCENT,
		VALUES
	}
	
	public enum AutoFillType{
		COPY,
		DAYS,
		DEFAULT,
		FORMATS,
		MONTHS,
		SERIES,
		VALUES,
		WEEKDAYS,
		YEARS,
		GROWTH_TREND,
		LINER_TREND
	}

	public void setSyncLevel(SyncLevel syncLevel);
	
	public Book getBook();
	
	public Sheet getSheet();
	
	public int getColumn();
	public int getRow();
	public int getLastColumn();
	public int getLastRow();
	
	public CellStyleHelper getCellStyleHelper();
	
	public void sync(RangeRunner run);
	/**
	 * visit all cells in this range, make sure you call this in a limited range, 
	 * don't use it for all row/column selection, it will spend much time to iterate the cell 
	 * @param visitor the visitor 
	 * @param create create cell if it doesn't exist, if it is true, it will also lock the sheet
	 */
	public void visit(final CellVisitor visitor);

	/**
	 * Return a new range that shift it row and column according to the offset, but has same height and width of original range.
	 * @param rowOffset row offset of the new range, zero base
	 * @param colOffset column offset of the new range, zero base
	 * @return the new range
	 */
	public Range toShiftedRange(int rowOffset,int colOffset);
	
	/**
	 * Returns a new range according the offset, but only contains one cell 
	 * @param rowOffset row offset of the cell, zero base
	 * @param colOffset column offset of the cell, zero base
	 * @return the new range of the cell
	 */
	public Range toCellRange(int rowOffset,int colOffset);
	
	/**
	 *  Return a range that represents all columns and between the first-row and last-row of this range.
	 *  It is a useful when you want to manipulate entire row (such as delete row)
	 **/
	public Range toRowRange();
	
	/**
	 *  Return a range that represents all rows and between the first-column and last-column of this range
	 *  It is a useful when you want to manipulate entire column (such as delete column)
	 **/
	public Range toColumnRange();
	
	/**
	 * Check if this range represents a whole column, which mean all rows are included, 
	 */
	public boolean isWholeColumn();
	/**
	 * Check if this range represents a whole row, which mean all column are included, 
	 */
	public boolean isWholeRow();
	/**
	 * Check if this range represents a whole sheet, which mean all column and row are included, 
	 */
	public boolean isWholeSheet();
	
	/*
	 * ==================================================
	 * operation of cell area  relative API
	 * ==================================================
	 */
	
	public void clearContents();

	public void clearStyles();

	/**
	 * @param dest the destination 
	 * @return true if paste successfully, past to a protected sheet with any
	 *         locked cell in the destination range will always cause past fail.
	 */
	public boolean paste(Range dest);
	
	/**
	 * @param dest the destination 
	 * @param transpose TODO
	 * @return true if paste successfully, past to a protected sheet with any
	 *         locked cell in the destination range will always cause past fail.
	 */
	public boolean pasteSpecial(Range dest,PasteType type,PasteOperation op,boolean skipBlanks,boolean transpose);
	
	public void applyBorders(ApplyBorderType type,BorderType borderType,String htmlColor);

	public boolean hasMergeCell();
	
	public void merge(boolean across);
	
	public void unMerge();
	
	public void insert(InsertShift shift,InsertCopyOrigin copyOrigin);
	
	public void delete(DeleteShift shift);
	
	public void sort(boolean desc);
	
	public void sort(boolean desc,
			boolean header, 
			boolean matchCase, 
			boolean sortByRows, 
			SortDataOption dataOption);
	
	public void sort(Range index1,
			boolean desc1,
			boolean header, 
			/*int orderCustom, //not implement*/
			boolean matchCase, 
			boolean sortByRows, 
			/*int sortMethod, //not implement*/
			SortDataOption dataOption1,
			Range index2,boolean desc2,SortDataOption dataOption2,
			Range index3,boolean desc3,SortDataOption dataOption3);
	
	public void autoFill(Range dest,AutoFillType fillType);
	
	public void fillDown();
	
	public void fillLeft();
	
	public void fillUp();
	
	public void fillRight();
	
	/** shift this range with a offset row and column**/
	public void shift(int rowOffset,int colOffset);
	
	/**
	 * Sets the width(in pixel) of column in this range, it effect to whole column. 
	 * @param widthPx width in pixel
	 * @see #toColumnRange()
	 */
	public void setColumnWidth(int widthPx);
	/**
	 * Sets the height(in pixel) of row in this range, it effect to whole row.
	 * @param widthPx width in pixel
	 * @see #toRowRange()
	 */
	public void setRowHeight(int heightPx);
	
	/* 
	 * ==================================================
	 * cell relative API
	 * ==================================================
	 */
	
	public void setCellStyle(CellStyle nstyle);
	
	public void setCellEditText(String editText);
	
	public void setCellValue(Object value);
	
	public void setCellHyperlink(HyperlinkType type,String address,String display);
	
	
	/**
	 * Get the first cell(top-left) hyper-link object of this range.
	 * @return
	 */
	public Hyperlink getCellHyperlink();
	
	/**
	 * Get the first cell(top-left) style of this range
	 * 
	 * @return cell style if cell is exist, the check row style and column cell style if cell not found, if row and column style is not exist, then return default style of sheet
	 */
	public CellStyle getCellStyle();
	
	/**
	 * Get the first cell(top-left) data of this range
	 * @return
	 */
	public CellData getCellData();
	
	/**
	 * Get the first cell(top-left) edit text of this range
	 * @return edit text
	 * @see CellData#getEditText()
	 */
	public String getCellEditText();
	
	/**
	 * Get the first cell(top-left) format text of this range
	 * @return format text
	 * @see CellData#getFormatText()
	 */
	public String getCellFormatText();
	
	/**
	 * Get the first cell(top-left) value of this this range
	 * @return value object
	 * @see CellData#getValue()
	 */
	public Object getCellValue();
	
	/* 
	 * ==================================================
	 * sheet relative API
	 * ==================================================
	 */
	
	
	/** enable sheet protection and apply a password**/
	public void protectSheet(String password);
	
	public void setDisplaySheetGridlines(boolean enable);
	
	public boolean isDisplaySheetGridlines();
	
	/**
	 * Hide the rows or columns, the range has to be a whole row range (which mean you select some columns and all rows of these columns are included),
	 * or a whole column range. 
	 * @param hidden
	 * @see #toRowRange()
	 * @see #toColumnRange()
	 */
	public void setHidden(boolean hidden);
	
	public void setSheetName(String name);
	
	public String getSheetName();
	
	public void setSheetOrder(int pos);
	
	public int getSheetOrder();
	

	public boolean isProtected();
	
	/** check if auto filter is enable or not.**/
	public boolean isAutoFilterEnabled();
	
	/** enable/disable autofilter of the sheet**/
	public void enableAutoFilter(boolean enable);
	/** enable filter with condition **/
	//TODO have to review this after I know more detail
	public void enableAutoFilter(int field, AutoFilterOperation filterOp, Object criteria1, Object criteria2, Boolean visibleDropDown);
	
	/** clear condition of filter, show all the data**/
	public void resetAutoFilter();
	/** apply the filter to filter again**/
	public void applyAutoFilter();
	
	public Picture addPicture(SheetAnchor anchor,byte[] image,Format format);
	
	public void deletePicture(Picture picture);
	
	public void movePicture(SheetAnchor anchor,Picture picture);
	
	//currently, we only support to modify chart in XSSF
	public Chart addChart(SheetAnchor anchor,ChartData data,Type type, Grouping grouping, LegendPosition pos);
	
	//currently, we only support to modify chart in XSSF
	public void deleteChart(Chart chart);
	
	//currently, we only support to modify chart in XSSF
	public void moveChart(SheetAnchor anchor,Chart chart);
	
	public Sheet createSheet(String name);
	
	public void deleteSheet();
	
	
	/**
	 * a cell style helper to create style relative object for cell
	 * @author dennis
	 */
	public interface CellStyleHelper {

		/**
		 * create a new cell style and clone attribute from src if it is not null
		 * @param src the source to clone, could be null
		 * @return the new cell style
		 */
		public CellStyle createCellStyle(CellStyle src);

		/**
		 * create a new font and clone attribute from src if it is not null
		 * @param src the source to clone, could be null
		 * @return the new font
		 */
		public Font createFont(Font src);
		
		/**
		 * create a color object from a htmlColor expression
		 * @param htmlColor html color expression, ex. #FF00FF
		 * @return a Color object
		 */
		public Color createColorFromHtmlColor(String htmlColor);
		
		/**
		 * find the font with given condition
		 * @param boldweight
		 * @param color
		 * @param fontHeight
		 * @param fontName
		 * @param italic
		 * @param strikeout
		 * @param typeOffset
		 * @param underline
		 * @return null if not found
		 */
		public Font findFont(Boldweight boldweight, Color color,
				int fontHeight, String fontName, boolean italic,
				boolean strikeout, TypeOffset typeOffset, Underline underline);
	}

}
