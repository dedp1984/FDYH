import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.nantian.custom.CellMergedProperty;



public class POITest {

	
	
	public static boolean isMergeCell(Sheet sheet,int  rowIndex,int cellIndex,CellMergedProperty property)
	{
		int sheetMergeCount = sheet.getNumMergedRegions(); 
		Row row=sheet.getRow(rowIndex);
		Cell cell=row.getCell(cellIndex);
		for (int i = 0; i < sheetMergeCount; i++) 
		{  
			CellRangeAddress range = sheet.getMergedRegion(i);  
			int firstColumn = range.getFirstColumn();  
			int lastColumn = range.getLastColumn();  
			int firstRow = range.getFirstRow();  
			int lastRow = range.getLastRow(); 
			int columnIndex=cell.getColumnIndex();
			if(rowIndex>=firstRow&&rowIndex<=lastRow)
			{
				if(columnIndex>=firstColumn&&columnIndex<=lastColumn)
				{
					property.setFirstColumn(firstColumn);
					property.setLastColumn(lastColumn);
					property.setFirstRow(firstRow);
					property.setLastRow(lastRow);
					property.setCellValue(cell.getStringCellValue());
					return true;
				}
					
			}
			
		}
		return false;
		
	}
	public static void main(String[] args) throws IOException, EncryptedDocumentException, InvalidFormatException {
		// TODO Auto-generated method stub

		writeExcel();
		//

		//HSSFWorkbook wb1=new HSSFWorkbook(is);
		//XSSFWorkbook wb = new XSSFWorkbook("C:\\Users\\dengpan\\Desktop\\测试数据\\模拟数据\\个人业务\\个人定期存款日均 - 副本.xls");
		//InputStream is = new FileInputStream("C:\\Users\\dengpan\\Desktop\\测试数据\\原始测试数据\\对公业务\\短期信用贷款日均.xlsx");
		
//		Workbook wb=WorkbookFactory.create(new File("C:\\Users\\dengpan\\Desktop\\测试数据\\模拟数据\\个人业务\\个人定期存款日均 - 副本.xls"));
//		Sheet sheet=wb.getSheetAt(0);
//		Row row=sheet.getRow(0);
//		Cell cell=row.getCell(0);
//		System.out.println(cell.getStringCellValue());
		/*
		int totalRows=sheet.getLastRowNum()-1;
		int startRow=3;
		while(startRow<=totalRows)
		{
			CellMergedProperty property=new CellMergedProperty();
			if(isMergeCell(sheet,startRow,0,property))
			{
				
				System.out.println(property.toString());
				Row row=sheet.getRow(startRow);
				Cell cell=row.getCell(1);
				System.out.println(cell.getStringCellValue());
				//startRow+=property.getMergedRows();
				for(int i=0;i<property.getMergedRows();i++)
				{
					Row tmpRow=sheet.getRow(startRow);
					//Cell tmpCell=tmpRow.getCell(2);
					startRow++;
				}
			}
			else
			{
				Row row=sheet.getRow(startRow);
				Cell cell=row.getCell(0);
				System.out.println(cell.getStringCellValue());
				startRow++;
			}
			
		}
		/*
		System.out.println(sheet.getLastRowNum());
		System.out.println(cell.getColumnIndex());
		System.out.println(cell.getRowIndex());
		int sheetMergeCount = sheet.getNumMergedRegions();  
		for (int i = 0; i < sheetMergeCount; i++) {  
			CellRangeAddress range = sheet.getMergedRegion(i);  
			int firstColumn = range.getFirstColumn();  
			int lastColumn = range.getLastColumn();  
			int firstRow = range.getFirstRow();  
			int lastRow = range.getLastRow();  
			System.out.println(firstColumn+" "+lastColumn+" "+firstRow+" "+lastRow+" "+range.getNumberOfCells());
		}
		*/
		//读取2003XLS
		/*
		HSSFWorkbook wb=new HSSFWorkbook(is);
		HSSFSheet sheet=wb.getSheetAt(0);
		HSSFRow row=sheet.getRow(1);
		HSSFCell cell=row.getCell(19);
		System.out.println(cell.getStringCellValue());
		System.out.println(sheet.getLastRowNum());
		wb.close();*/
	}
	public static void  writeCellValue(XSSFWorkbook wb,Sheet sheet,Row row,int colNum,String value,XSSFFont font)
	{
		XSSFCellStyle cellStyle=wb.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setFont(font);
		Cell cell=row.createCell(colNum);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}
	public static void  writeCellDoubleValue(XSSFWorkbook wb,Sheet sheet,Row row,int colNum,double value,XSSFFont font)
	{
		XSSFCellStyle cellStyle=wb.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setDataFormat(4);
		cellStyle.setFont(font);
		Cell cell=row.createCell(colNum);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}
	public static void writeRegionCellValue(XSSFWorkbook wb,Sheet sheet,Row row,int firstRow,int lastRow,int firstCol,int lastCol,String value,XSSFFont font)
	{
		XSSFCellStyle cellStyle=wb.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setFont(font);
		Cell cell=row.createCell(firstCol);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
		CellRangeAddress range=new CellRangeAddress(firstRow,lastRow,firstCol,lastCol);
		RegionUtil.setBorderBottom(1,range, sheet, wb);
		RegionUtil.setBorderTop(1, range, sheet, wb);
		RegionUtil.setBorderLeft(1, range, sheet, wb);
		RegionUtil.setBorderRight(1, range, sheet, wb);
		
		sheet.addMergedRegion(range);
	}
	public static void writeExcel() throws IOException
	{
		XSSFWorkbook wb = new XSSFWorkbook();  
		XSSFCellStyle cellStyle=wb.createCellStyle();
		Sheet sheet=wb.createSheet();
		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 4000);
		Row row=sheet.createRow(1);
		
		XSSFFont font=wb.createFont();
		font.setFontHeightInPoints((short)15);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		
		row=sheet.createRow(2);
		writeCellValue(wb,sheet,row,3,"bbbb",font);
		writeCellDoubleValue(wb,sheet,row,4,2222123.10,font);
		writeCellDoubleValue(wb,sheet,row,5,2222123.10,font);
		/*row=sheet.createRow(1);
		writeCellValue(wb,sheet,row,3,"bbbb",font);
		writeRegionCellValue(wb,sheet,row,1,2,1,1,"aaaa",font);
		writeRegionCellValue(wb,sheet,row,1,2,2,2,"aaaaaaaaaaaaaaa",font);
		row=sheet.createRow(3);
		writeRegionCellValue(wb,sheet,row,3,4,1,1,"aaaa",font);
		
		/*
		Row row=sheet.createRow(2);
		writeCellValue(wb,sheet,row,1,"aaaa");
		CellRangeAddress range=new CellRangeAddress(1,2,1,1);
		RegionUtil.setBorderBottom(1,range, sheet, wb);
		row=sheet.createRow(1);
		cell=row.createCell(1);
		cell.setCellValue("111");
		sheet.addMergedRegion(range);
		range=new CellRangeAddress(3,4,1,1);
		row=sheet.createRow(3);
		cell=row.createCell(1);
		cell.setCellValue("222");
		sheet.addMergedRegion(range);
		
		cell.setCellStyle(cellStyle);*/
		FileOutputStream fileOut = new FileOutputStream("d:\\1.xlsx");   
		wb.write(fileOut);   
	}

}
