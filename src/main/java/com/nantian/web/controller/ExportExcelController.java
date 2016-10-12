package com.nantian.web.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.nantian.custom.AccountType;
import com.nantian.custom.CellStyleCache;
import com.nantian.custom.ExcelType;
import com.nantian.custom.Result;
import com.nantian.custom.Utils;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.NoBindAccount;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.domain.SysBranch;
import com.nantian.service.BaseAccountService;
import com.nantian.service.NoBindAccountService;
import com.nantian.service.QueryService;
import com.nantian.service.SysAccountService;
import com.nantian.service.SysBranchService;
import com.nantian.tree.PersonTree;
import com.nantian.tree.PublicTree;


@Controller
public class ExportExcelController
{
	
	@Resource
	private QueryService queryService;
	@Resource
	private SysBranchService sysBranchService;
	@Resource
	private SysAccountService sysAccountService;
	@Resource
	private NoBindAccountService noBindAccountService;
	@Resource
	private BaseAccountService baseAccountService;
	
	private CellStyleCache cellStyleCache;
	private boolean isPersonManager(List<SysAccountFeatureKey> busiFeatures)
	{
		if(busiFeatures!=null)
		{
			for(int i=0;i<busiFeatures.size();i++)
			{
				SysAccountFeatureKey feature=busiFeatures.get(i);
				if(feature.getValue().equals("2"))
				{
					return true;
				}
			}
		}
		return false;
	}
	private boolean isPublicManager(List<SysAccountFeatureKey> busiFeatures)
	{
		if(busiFeatures!=null)
		{
			for(int i=0;i<busiFeatures.size();i++)
			{
				SysAccountFeatureKey feature=busiFeatures.get(i);
				if(feature.getValue().equals("1"))
				{
					return true;
				}
			}
		}
		return false;
	}
	private void  writeCellValue(Workbook wb,Sheet sheet,Row row,int colNum,String value,Font font,short color,short align)
	{
		Cell cell=row.createCell(colNum);
		cell.setCellStyle(cellStyleCache.getCellStyle(align, color, false,false));
		cell.setCellValue(value);
	}
	private void  writeCellAmount(Workbook wb,Sheet sheet,Row row,int colNum,String value,Font font,short color,short align)
	{
		Cell cell=row.createCell(colNum);
		cell.setCellValue(Double.valueOf(value.replaceAll(",","")));
		cell.setCellStyle(cellStyleCache.getCellStyle(align, color, false,true));		
	}
	private  void writeRegionCellValue(Workbook wb,Sheet sheet,Row row,int firstRow,int lastRow,int firstCol,int lastCol,String value,short align)
	{
		Cell cell=row.createCell(firstCol);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyleCache.getCellStyle(align, IndexedColors.WHITE.getIndex(), false,false));
		CellRangeAddress range=new CellRangeAddress(firstRow,lastRow,firstCol,lastCol);
		RegionUtil.setBorderBottom(1,range, sheet, wb);
		RegionUtil.setBorderTop(1, range, sheet, wb);
		RegionUtil.setBorderLeft(1, range, sheet, wb);
		RegionUtil.setBorderRight(1, range, sheet, wb);
		sheet.addMergedRegion(range);
	}
	private  void writeRegionCellAmount(Workbook wb,Sheet sheet,Row row,int firstRow,int lastRow,int firstCol,int lastCol,String value,short align)
	{
		Cell cell=row.createCell(firstCol);
		cell.setCellValue(Double.valueOf(value.replaceAll(",","")));
		cell.setCellStyle(cellStyleCache.getCellStyle(align, IndexedColors.WHITE.getIndex(), false,true));
		CellRangeAddress range=new CellRangeAddress(firstRow,lastRow,firstCol,lastCol);
		RegionUtil.setBorderBottom(1,range, sheet, wb);
		RegionUtil.setBorderTop(1, range, sheet, wb);
		RegionUtil.setBorderLeft(1, range, sheet, wb);
		RegionUtil.setBorderRight(1, range, sheet, wb);	
		sheet.addMergedRegion(range);
	}
	
	@RequestMapping("/export/exportPublicDepositFile")
	public ResponseEntity<byte[]> exportPublicDeposiFile( String enddate,
														  String branchid,
														  String accounttype,
														  String accountid,
														  String customno,
														  String subcode,
														  String accountname,
														  String property,
														  String status,
														  String managerid,
														  HttpSession session) throws ParseException, IOException
	{
		accountname=new String(accountname.getBytes("ISO8859-1"),"UTF-8");
		property=new String(property.getBytes("ISO8859-1"),"UTF-8");
		status=new String(status.getBytes("ISO8859-1"),"UTF-8");
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		if(enddate.equals(""))
		{
			enddate=Utils.getCurrentTime("yyyyMMdd");
		}
		Date queryEndDate=Utils.str82date(enddate);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		List<Map> list=null;
		String outputName;
		ArrayList accttype=new ArrayList();
		String[] s=accounttype.split(",");
		for(int i=0;i<s.length;i++)
			accttype.add(s[i]);
		list=queryService.queryAccountBalAndYearDayAvg_1(queryEndDate, branchid, managerid,accountid, accountname, customno, subcode,null, status,accttype);
		if(accounttype.equals("2"))
		{
			outputName="对公定期存款余额明细"+enddate;
			//list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid, managerid,accountid, accountname, customno, subcode,null, status,AccountType.PUBLIC_FIXED_ACCOUNT);
		}
		else if(accounttype.equals("1"))
		{
			outputName="对公活期存款余额明细"+enddate;
			//list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid,managerid, accountid, accountname, customno, subcode,null, status,AccountType.PUBLIC_CURRENT_ACCOUNT);
		}else
		{
			outputName="对公活期+定期存款余额明细"+enddate;
		}
		Date beginDate=Utils.str82date(enddate.substring(0,4)+"0101");
		String year=enddate.substring(0, 4);
		int month=Integer.valueOf(enddate.substring(4, 6));
		 
		Workbook wb=new HSSFWorkbook();
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 1000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 5500);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 7000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 2500);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 2500);
		sheet.setColumnWidth(10, 4000);
		sheet.setColumnWidth(11, 4000);
		sheet.setColumnWidth(12, 4000);
		sheet.setColumnWidth(13, 4000);
		sheet.setColumnWidth(14, 4000);
		sheet.setColumnWidth(15, 4000);
		sheet.setColumnWidth(16, 4000);
		sheet.setColumnWidth(17, 4000);
		sheet.setColumnWidth(18, 4000);
		sheet.setColumnWidth(19, 4000);
		sheet.setColumnWidth(20, 4000);
		sheet.setColumnWidth(21, 4000);
		sheet.setColumnWidth(22, 4000);
		sheet.setColumnWidth(23, 4000);
		sheet.setColumnWidth(24, 4000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"序号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,1,"开户机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,2,"客户账号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,3,"客户名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,4,"客户号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,5,"科目号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,6,"时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,7,"截止日期",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,8,"进度日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,9,"截止日期",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,10,"1月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,11,"2月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,12,"3月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,13,"4月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,14,"5月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,15,"6月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,16,"7月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,17,"8月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,18,"9月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,19,"10月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,20,"11月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,21,"12月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,22,"归属机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,23,"归属客户经理",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,24,"分配比例",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<list.size();i++)
		{
			System.out.println((new Date()).toString()+"  "+i);
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			List<Double> monthBal=new ArrayList<Double>();
			double total=0.00;
			double m1=Double.valueOf((map.get("m1")==null)?"0.00":map.get("m1").toString());
			monthBal.add(0, m1);
			double m2=Double.valueOf((map.get("m2")==null)?"0.00":map.get("m2").toString());
			monthBal.add(1, m2);
			double m3=Double.valueOf((map.get("m3")==null)?"0.00":map.get("m3").toString());
			monthBal.add(2, m3);
			double m4=Double.valueOf((map.get("m4")==null)?"0.00":map.get("m4").toString());
			monthBal.add(3, m4);
			double m5=Double.valueOf((map.get("m5")==null)?"0.00":map.get("m5").toString());
			monthBal.add(4, m5);
			double m6=Double.valueOf((map.get("m6")==null)?"0.00":map.get("m6").toString());
			monthBal.add(5, m6);
			double m7=Double.valueOf((map.get("m7")==null)?"0.00":map.get("m7").toString());
			monthBal.add(6, m7);
			double m8=Double.valueOf((map.get("m8")==null)?"0.00":map.get("m8").toString());
			monthBal.add(7, m8);
			double m9=Double.valueOf((map.get("m9")==null)?"0.00":map.get("m9").toString());
			monthBal.add(8, m9);
			double m10=Double.valueOf((map.get("m10")==null)?"0.00":map.get("m10").toString());
			monthBal.add(9, m10);
			double m11=Double.valueOf((map.get("m11")==null)?"0.00":map.get("m11").toString());
			monthBal.add(10, m11);
			double m12=Double.valueOf((map.get("m12")==null)?"0.00":map.get("m12").toString());
			monthBal.add(11, m12);
			int[] monthDay={31,28,31,30,31,30,31,31,30,31,30,31};
			for(int j=1;j<=12;j++)
			{
				if(j==month)
				{
					total+=monthBal.get(j-1)*Utils.getSpaceDay(Utils.str82date(year+enddate.substring(4, 6)+"01"), queryEndDate);
					break;
				}
				total+=monthBal.get(j-1)*monthDay[j-1];
			}
			double yearDayAvg=total/Utils.getSpaceDay(Utils.str82date(year+"0101"), queryEndDate);
			map.put("yeardayavg", yearDayAvg);
			list.set(i, map);
			String m_branchname=map.get("branchname")==null?"未指定管理机构":map.get("branchname").toString();
			
			String m_accountid=map.get("accountid")==null?" ":map.get("accountid").toString();
			String m_accountname=map.get("accountname")==null?" ":map.get("accountname").toString();
			String m_customno=map.get("customno")==null?" ":map.get("customno").toString();
			String m_subcode=map.get("subcode")==null?" ":map.get("subcode").toString();
			String m_bal=df.format(Double.valueOf(map.get("bal").toString()));
			String m_balgendate="";
			if(map.get("balgendate")!=null)
				m_balgendate=Utils.getFormatDate((Date)map.get("balgendate"),"yyyyMMdd");
			String m_yeardayavg=df.format(Double.valueOf(map.get("yeardayavg").toString()));
			
			String m_avggendate="";
			if(map.get("avggendate")!=null)
				m_avggendate=Utils.getFormatDate((Date)map.get("avggendate"),"yyyyMMdd");
			String m_m1=df.format(Double.valueOf(map.get("m1").toString()));
			String m_m2=df.format(Double.valueOf(map.get("m2").toString()));
			String m_m3=df.format(Double.valueOf(map.get("m3").toString()));
			String m_m4=df.format(Double.valueOf(map.get("m4").toString()));
			String m_m5=df.format(Double.valueOf(map.get("m5").toString()));
			String m_m6=df.format(Double.valueOf(map.get("m6").toString()));
			String m_m7=df.format(Double.valueOf(map.get("m7").toString()));
			String m_m8=df.format(Double.valueOf(map.get("m8").toString()));
			String m_m9=df.format(Double.valueOf(map.get("m9").toString()));
			String m_m10=df.format(Double.valueOf(map.get("m10").toString()));
			String m_m11=df.format(Double.valueOf(map.get("m11").toString()));
			String m_m12=df.format(Double.valueOf(map.get("m12").toString()));
			ArrayList<Map> listBinds=(ArrayList<Map>) map.get("binds");
			for(int j=listBinds.size();j>0;j--)
			{
				HashMap<String,Object> bindMap=(HashMap<String,Object>)listBinds.get(j-1);
				String b_managerid=bindMap.get("managerid")==null?" ":bindMap.get("managerid").toString();
				String b_branchname=bindMap.get("branchname")==null?" ":bindMap.get("branchname").toString();
				String b_percent=bindMap.get("percent")==null?" ":bindMap.get("percent").toString();
				row=sheet.createRow(nextRowNum+j-1);
				writeCellValue(wb,sheet,row,22,b_branchname,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellValue(wb,sheet,row,23,b_managerid,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellValue(wb,sheet,row,24,b_percent,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			}
			if(listBinds.size()==0)
			{
				row=sheet.createRow(nextRowNum);
			}
			int regionRows=listBinds.size()==0?0:listBinds.size()-1;
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,0,0,String.valueOf(i+1),CellStyle.ALIGN_CENTER);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,1,1,m_branchname,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,2,2,m_accountid,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,3,3,m_accountname,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,4,4,m_customno,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,5,5,m_subcode,CellStyle.ALIGN_CENTER);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,6,6,m_bal,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,7,7,m_balgendate,CellStyle.ALIGN_CENTER);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,8,8,m_yeardayavg,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,9,9,m_avggendate,CellStyle.ALIGN_CENTER);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,10,10,m_m1,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,11,11,m_m2,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,12,12,m_m3,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,13,13,m_m4,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,14,14,m_m5,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,15,15,m_m6,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,16,16,m_m7,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,17,17,m_m8,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,18,18,m_m9,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,19,19,m_m10,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,20,20,m_m11,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,21,21,m_m12,CellStyle.ALIGN_RIGHT);
			
			nextRowNum=nextRowNum+regionRows+1;
//		
//			if(i % 1000 == 0) {
//				
//                SXSSFSheet tmp=((SXSSFSheet)sheet); // retain 100 last rows and flush all others
//                tmp.flushRows(1000);
//
//                // ((SXSSFSheet)sh).flushRows() is a shortcut for ((SXSSFSheet)sh).flushRows(0),
//                // this method flushes all rows
//           }
			
		}
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK);  
		
	}
	@RequestMapping("/export/exportPublicCreditFile")
	public ResponseEntity<byte[]> exportPublicCreditFile( String enddate,
														  String branchid,
														  String accounttype,
														  String accountid,
														  String customno,
														  String subcode,
														  String accountname,
														  String property,
														  String status,
														  String managerid,
														  HttpSession session) throws ParseException, IOException
	{
		accountname=new String(accountname.getBytes("ISO8859-1"),"UTF-8");
		property=new String(property.getBytes("ISO8859-1"),"UTF-8");
		status=new String(status.getBytes("ISO8859-1"),"UTF-8");
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		if(enddate.equals(""))
		{
			enddate=Utils.getCurrentTime("yyyyMMdd");
		}
		Date queryEndDate=Utils.str82date(enddate);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		String outputName;
		outputName="对公贷款余额明细"+enddate;
		List<Map> list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid, managerid,accountid, accountname, customno, subcode,null, status,AccountType.PUBLIC_CREDIT_ACCOUNT);
		
		Date beginDate=Utils.str82date(enddate.substring(0,4)+"0101");
		String year=enddate.substring(0, 4);
		int month=Integer.valueOf(enddate.substring(4, 6));
		
		Workbook wb = new HSSFWorkbook();  
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 1000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 6000);
		sheet.setColumnWidth(3, 7000);
		sheet.setColumnWidth(4, 7000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 2500);
		sheet.setColumnWidth(10, 4000);
		sheet.setColumnWidth(11, 4000);
		sheet.setColumnWidth(12, 4000);
		sheet.setColumnWidth(13, 4000);
		sheet.setColumnWidth(14, 4000);
		sheet.setColumnWidth(15, 4000);
		sheet.setColumnWidth(16, 4000);
		sheet.setColumnWidth(17, 4000);
		sheet.setColumnWidth(18, 4000);
		sheet.setColumnWidth(19, 4000);
		sheet.setColumnWidth(20, 4000);
		sheet.setColumnWidth(21, 4000);
		sheet.setColumnWidth(22, 4000);
		sheet.setColumnWidth(23, 4000);
		sheet.setColumnWidth(24, 4000);
		sheet.setColumnWidth(25, 4000);
		sheet.setColumnWidth(26, 4000);
		sheet.setColumnWidth(27, 4000);
		sheet.setColumnWidth(28, 4000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"序号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,1,"开户机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,2,"客户账号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,3,"客户名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,4,"客户号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,5,"科目号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,6,"月利率",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,7,"利率浮动方式",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,8,"利率浮动比例",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,9,"账户状态",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,10,"五级分类",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,11,"时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,12,"进度日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,13,"截止日期",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,14,"1月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,15,"2月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,16,"3月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,17,"4月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,18,"5月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,19,"6月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,20,"7月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,21,"8月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,22,"9月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,23,"10月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,24,"11月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,25,"12月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,26,"归属机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,27,"归属客户经理",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,28,"分配比例",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<list.size();i++)
		{
			System.out.println((new Date()).toString()+"  "+i);
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			List<Double> monthBal=new ArrayList<Double>();
			double total=0.00;
			double m1=Double.valueOf((map.get("m1")==null)?"0.00":map.get("m1").toString());
			monthBal.add(0, m1);
			double m2=Double.valueOf((map.get("m2")==null)?"0.00":map.get("m2").toString());
			monthBal.add(1, m2);
			double m3=Double.valueOf((map.get("m3")==null)?"0.00":map.get("m3").toString());
			monthBal.add(2, m3);
			double m4=Double.valueOf((map.get("m4")==null)?"0.00":map.get("m4").toString());
			monthBal.add(3, m4);
			double m5=Double.valueOf((map.get("m5")==null)?"0.00":map.get("m5").toString());
			monthBal.add(4, m5);
			double m6=Double.valueOf((map.get("m6")==null)?"0.00":map.get("m6").toString());
			monthBal.add(5, m6);
			double m7=Double.valueOf((map.get("m7")==null)?"0.00":map.get("m7").toString());
			monthBal.add(6, m7);
			double m8=Double.valueOf((map.get("m8")==null)?"0.00":map.get("m8").toString());
			monthBal.add(7, m8);
			double m9=Double.valueOf((map.get("m9")==null)?"0.00":map.get("m9").toString());
			monthBal.add(8, m9);
			double m10=Double.valueOf((map.get("m10")==null)?"0.00":map.get("m10").toString());
			monthBal.add(9, m10);
			double m11=Double.valueOf((map.get("m11")==null)?"0.00":map.get("m11").toString());
			monthBal.add(10, m11);
			double m12=Double.valueOf((map.get("m12")==null)?"0.00":map.get("m12").toString());
			monthBal.add(11, m12);
			int[] monthDay={31,28,31,30,31,30,31,31,30,31,30,31};
			for(int j=1;j<=12;j++)
			{
				if(j==month)
				{
					total+=monthBal.get(j-1)*Utils.getSpaceDay(Utils.str82date(year+enddate.substring(4, 6)+"01"), queryEndDate);
					break;
				}
				total+=monthBal.get(j-1)*monthDay[j-1];
			}
			double yearDayAvg=total/Utils.getSpaceDay(Utils.str82date(year+"0101"), queryEndDate);
			map.put("yeardayavg", yearDayAvg);
			list.set(i, map);
			String m_branchname=map.get("branchname")==null?"未指定管理机构":map.get("branchname").toString();
			String m_accountid=map.get("accountid")==null?" ":map.get("accountid").toString();
			String m_accountname=map.get("accountname")==null?" ":map.get("accountname").toString();
			String m_customno=map.get("customno")==null?" ":map.get("customno").toString();
			String m_subcode=map.get("subcode")==null?" ":map.get("subcode").toString();
			String m_bal=df.format(Double.valueOf(map.get("bal").toString()));
			String m_balgendate="";
			if(map.get("balgendate")!=null)
				m_balgendate=Utils.getFormatDate((Date)map.get("balgendate"),"yyyyMMdd");
			String m_yeardayavg=df.format(Double.valueOf(map.get("yeardayavg").toString()));
			
			String m_avggendate="";
			if(map.get("avggendate")!=null)
				m_avggendate=Utils.getFormatDate((Date)map.get("avggendate"),"yyyyMMdd");
			
			double m_mrate=Double.valueOf((map.get("mrate")==null)?"0.00":map.get("mrate").toString());
			String m_fdfs=map.get("fdfs")==null?"":map.get("fdfs").toString();
			double m_fdbl=Double.valueOf((map.get("fdbl")==null)?"0.00":map.get("fdbl").toString());
			String m_wjfl=map.get("wjfl")==null?"":map.get("wjfl").toString();
			String m_acctstatus=map.get("acctstatus")==null?"":map.get("acctstatus").toString();
			
			String m_m1=df.format(Double.valueOf(map.get("m1").toString()));
			String m_m2=df.format(Double.valueOf(map.get("m2").toString()));
			String m_m3=df.format(Double.valueOf(map.get("m3").toString()));
			String m_m4=df.format(Double.valueOf(map.get("m4").toString()));
			String m_m5=df.format(Double.valueOf(map.get("m5").toString()));
			String m_m6=df.format(Double.valueOf(map.get("m6").toString()));
			String m_m7=df.format(Double.valueOf(map.get("m7").toString()));
			String m_m8=df.format(Double.valueOf(map.get("m8").toString()));
			String m_m9=df.format(Double.valueOf(map.get("m9").toString()));
			String m_m10=df.format(Double.valueOf(map.get("m10").toString()));
			String m_m11=df.format(Double.valueOf(map.get("m11").toString()));
			String m_m12=df.format(Double.valueOf(map.get("m12").toString()));
			ArrayList<Map> listBinds=(ArrayList<Map>) map.get("binds");
			for(int j=listBinds.size();j>0;j--)
			{
				HashMap<String,Object> bindMap=(HashMap<String,Object>)listBinds.get(j-1);
				String b_managerid=bindMap.get("managerid")==null?" ":bindMap.get("managerid").toString();
				String b_branchname=bindMap.get("branchname")==null?" ":bindMap.get("branchname").toString();
				String b_percent=bindMap.get("percent")==null?" ":bindMap.get("percent").toString();
				row=sheet.createRow(nextRowNum+j-1);
				writeCellValue(wb,sheet,row,26,b_branchname,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellValue(wb,sheet,row,27,b_managerid,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellValue(wb,sheet,row,28,b_percent,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			}
			if(listBinds.size()==0)
			{
				row=sheet.createRow(nextRowNum);
			}
			int regionRows=listBinds.size()==0?0:listBinds.size()-1;
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,0,0,String.valueOf(i+1),CellStyle.ALIGN_CENTER);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,1,1,m_branchname,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,2,2,m_accountid,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,3,3,m_accountname,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,4,4,m_customno,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,5,5,m_subcode,CellStyle.ALIGN_CENTER);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,6,6,String.valueOf(m_mrate),CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,7,7,m_fdfs,CellStyle.ALIGN_CENTER);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,8,8,String.valueOf(m_fdbl),CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,9,9,m_acctstatus,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,10,10,m_wjfl,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,11,11,m_bal,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,12,12,m_yeardayavg,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,13,13,m_balgendate,CellStyle.ALIGN_CENTER);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,14,14,m_m1,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,15,15,m_m2,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,16,16,m_m3,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,17,17,m_m4,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,18,18,m_m5,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,19,19,m_m6,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,20,20,m_m7,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,21,21,m_m8,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,22,22,m_m9,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,23,23,m_m10,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,24,24,m_m11,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,25,25,m_m12,CellStyle.ALIGN_RIGHT);
			
			nextRowNum=nextRowNum+regionRows+1;
			
		}
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK);  
		
	}
	@RequestMapping("/export/exportPersonDeposiFile")
	public ResponseEntity<byte[]> exportPersonDeposiFile( String enddate,
														  String branchid,
														  String accounttype,
														  String accountid,
														  String customno,
														  String subcode,
														  String accountname,
														  String status,
														  String managerid,
														  HttpSession session) throws ParseException, IOException
	{
		accountname=new String(accountname.getBytes("ISO8859-1"),"UTF-8");
		status=new String(status.getBytes("ISO8859-1"),"UTF-8");
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		if(enddate.equals(""))
		{
			enddate=Utils.getCurrentTime("yyyyMMdd");
		}
		Date queryEndDate=Utils.str82date(enddate);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		List<Map> list=null;
		String outputName;
		if(accounttype.equals("2"))
		{
			outputName="个人定期存款余额明细"+enddate;
			list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid, managerid,accountid, accountname, customno, subcode,null, status,AccountType.PERSON_FIXED_ACCOUNT);
		}
		else
		{
			outputName="个人活期存款余额明细"+enddate;
			list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid,managerid, accountid, accountname, customno, subcode,null, status,AccountType.PERSON_CURRENT_ACCOUNT);
		}
		Date beginDate=Utils.str82date(enddate.substring(0,4)+"0101");
		String year=enddate.substring(0, 4);
		int month=Integer.valueOf(enddate.substring(4, 6));
		
		Workbook wb = new HSSFWorkbook();  
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 1000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 5500);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 7000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4500);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 4500);
		sheet.setColumnWidth(10, 4500);
		sheet.setColumnWidth(11, 4500);
		sheet.setColumnWidth(12, 4500);
		sheet.setColumnWidth(13, 4500);
		sheet.setColumnWidth(14, 4500);
		sheet.setColumnWidth(15, 4500);
		sheet.setColumnWidth(16, 4500);
		sheet.setColumnWidth(17, 4500);
		sheet.setColumnWidth(18, 4500);
		sheet.setColumnWidth(19, 4500);
		sheet.setColumnWidth(20, 4500);
		sheet.setColumnWidth(21, 4000);
		sheet.setColumnWidth(22, 4000);
		sheet.setColumnWidth(23, 4000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"序号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,1,"开户机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,2,"客户账号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,3,"客户名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,4,"客户号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,5,"科目号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,6,"时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,7,"年日均余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,8,"截止日期",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,9,"1月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,10,"2月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,11,"3月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,12,"4月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,13,"5月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,14,"6月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,15,"7月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,16,"8月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,17,"9月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,18,"10月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,19,"11月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,20,"12月日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellValue(wb,sheet,row,21,"归属机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,22,"归属客户经理",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,23,"分配比例",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			/*
			List<Double> monthBal=new ArrayList<Double>();
			double total=0.00;
			double m1=Double.valueOf((map.get("m1")==null)?"0.00":map.get("m1").toString());
			monthBal.add(0, m1);
			double m2=Double.valueOf((map.get("m2")==null)?"0.00":map.get("m2").toString());
			monthBal.add(1, m2);
			double m3=Double.valueOf((map.get("m3")==null)?"0.00":map.get("m3").toString());
			monthBal.add(2, m3);
			double m4=Double.valueOf((map.get("m4")==null)?"0.00":map.get("m4").toString());
			monthBal.add(3, m4);
			double m5=Double.valueOf((map.get("m5")==null)?"0.00":map.get("m5").toString());
			monthBal.add(4, m5);
			double m6=Double.valueOf((map.get("m6")==null)?"0.00":map.get("m6").toString());
			monthBal.add(5, m6);
			double m7=Double.valueOf((map.get("m7")==null)?"0.00":map.get("m7").toString());
			monthBal.add(6, m7);
			double m8=Double.valueOf((map.get("m8")==null)?"0.00":map.get("m8").toString());
			monthBal.add(7, m8);
			double m9=Double.valueOf((map.get("m9")==null)?"0.00":map.get("m9").toString());
			monthBal.add(8, m9);
			double m10=Double.valueOf((map.get("m10")==null)?"0.00":map.get("m10").toString());
			monthBal.add(9, m10);
			double m11=Double.valueOf((map.get("m11")==null)?"0.00":map.get("m11").toString());
			monthBal.add(10, m11);
			double m12=Double.valueOf((map.get("m12")==null)?"0.00":map.get("m12").toString());
			monthBal.add(11, m12);
			int[] monthDay={31,28,31,30,31,30,31,31,30,31,30,31};
			for(int j=1;j<=12;j++)
			{
				if(j==month)
				{
					total+=monthBal.get(j-1)*Utils.getSpaceDay(Utils.str82date(year+enddate.substring(4, 6)+"01"), queryEndDate);
					break;
				}
				total+=monthBal.get(j-1)*monthDay[j-1];
			}
			double yearDayAvg=total/Utils.getSpaceDay(Utils.str82date(year+"0101"), queryEndDate);
			map.put("yeardayavg", yearDayAvg);*/
			list.set(i, map);
			String m_branchname=map.get("accountid")==null?"未分配管理机构":map.get("branchname").toString();
			String m_accountid=map.get("accountid")==null?"":map.get("accountid").toString();
			String m_accountname=map.get("accountname")==null?"":map.get("accountname").toString();
			String m_customno=map.get("customno")==null?"":map.get("customno").toString();
			String m_subcode=map.get("subcode")==null?"":map.get("subcode").toString();
			String m_bal=df.format(Double.valueOf(map.get("bal").toString()));
			String m_balgendate="";
			if(map.get("balgendate")!=null)
				m_balgendate=Utils.getFormatDate((Date)map.get("balgendate"),"yyyyMMdd");
			String m_yeardayavg=df.format(Double.valueOf(map.get("yearavg").toString()));
			
			String m_avggendate="";
			if(map.get("avggendate")!=null)
				m_avggendate=Utils.getFormatDate((Date)map.get("avggendate"),"yyyyMMdd");
			String m_m1=df.format(Double.valueOf(map.get("m1").toString()));
			String m_m2=df.format(Double.valueOf(map.get("m2").toString()));
			String m_m3=df.format(Double.valueOf(map.get("m3").toString()));
			String m_m4=df.format(Double.valueOf(map.get("m4").toString()));
			String m_m5=df.format(Double.valueOf(map.get("m5").toString()));
			String m_m6=df.format(Double.valueOf(map.get("m6").toString()));
			String m_m7=df.format(Double.valueOf(map.get("m7").toString()));
			String m_m8=df.format(Double.valueOf(map.get("m8").toString()));
			String m_m9=df.format(Double.valueOf(map.get("m9").toString()));
			String m_m10=df.format(Double.valueOf(map.get("m10").toString()));
			String m_m11=df.format(Double.valueOf(map.get("m11").toString()));
			String m_m12=df.format(Double.valueOf(map.get("m12").toString()));
			ArrayList<Map> listBinds=(ArrayList<Map>) map.get("binds");
			for(int j=listBinds.size();j>0;j--)
			{
				HashMap<String,Object> bindMap=(HashMap<String,Object>)listBinds.get(j-1);
				String b_managerid=bindMap.get("managerid")==null?"":bindMap.get("managerid").toString();
				String b_branchname=bindMap.get("branchname")==null?"":bindMap.get("branchname").toString();
				String b_percent=bindMap.get("percent")==null?"":bindMap.get("percent").toString();
				row=sheet.createRow(nextRowNum+j-1);
				writeCellValue(wb,sheet,row,21,b_branchname,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellValue(wb,sheet,row,22,b_managerid,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellValue(wb,sheet,row,23,b_percent,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			}
			if(listBinds.size()==0)
			{
				row=sheet.createRow(nextRowNum);
			}
			int regionRows=listBinds.size()==0?0:listBinds.size()-1;
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,0,0,String.valueOf(i+1),CellStyle.ALIGN_CENTER);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,1,1,m_branchname,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,2,2,m_accountid,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,3,3,m_accountname,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,4,4,m_customno,CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,5,5,m_subcode,CellStyle.ALIGN_CENTER);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,6,6,m_bal,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,7,7,m_yeardayavg,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,8,8,m_balgendate,CellStyle.ALIGN_CENTER);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,9,9,m_m1,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,10,10,m_m2,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,11,11,m_m3,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,12,12,m_m4,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,13,13,m_m5,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,14,14,m_m6,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,15,15,m_m7,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,16,16,m_m8,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,17,17,m_m9,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,18,18,m_m10,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,19,19,m_m11,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,20,20,m_m12,CellStyle.ALIGN_RIGHT);
			
			nextRowNum=nextRowNum+regionRows+1;
		}
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK);  
		
	}
	@RequestMapping("/export/ExportPersonBusinessData")
	public ResponseEntity<byte[]> ExportPersonBusinessData(String enddate,String branchid,String managerid,HttpSession session) throws ParseException, IOException
	{
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		List<PersonTree>  totalList=new ArrayList<PersonTree>();
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==true)
			{
				SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
				branchid=tmp.getBranchid();
			}
			else
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		List<SysBranch> sysBranchList=sysBranchService.queryAllSubBranchList(branchid,true);
		List<PersonTree> listBranch=new ArrayList<PersonTree>();
		PersonTree rootNode=new PersonTree();
		double totalBal=0.00;
		double totalTimeBal=0.00;
		double totalYearAvg=0.00;
		double totalPledgeYearAvg=0.00;
		double totalFixedYearAvg=0.00;
		double totalCurrentYearAvg=0.00;
		double totalFinanceYearAvg=0.00;
		double totalFinanceBal=0.00;
		double totalPledgeBal=0.00;
		double totalFixedBal=0.00;
		double totalCurrentBal=0.00;
		for(int i=0;i<sysBranchList.size();i++)
		{
			double branchBal=0.00;
			double branchTimeBal=0.00;
			double branchYearAvg=0.00;
			double branchPledgeYearAvg=0.00;
			double branchFixedYearAvg=0.00;
			double branchCurrentYearAvg=0.00;
			double branchFinanceYearAvg=0.00;
			double branchFinanceBal=0.00;
			double branchPledgeBal=0.00;
			double branchFixedBal=0.00;
			double branchCurrentBal=0.00;
			boolean balFlag=true;
			boolean avgFlag=true;
			SysBranch branch=sysBranchList.get(i);
			if(branch.getBranchid().equals("0000"))
				continue;
			ArrayList<String> propertyList=new ArrayList<String>();
			propertyList.add("1");
			propertyList.add("2");
			propertyList.add("3");
			propertyList.add("5");
			propertyList.add("6");
			List<SysAccount> sysAccountList=sysAccountService.querySysAccountList(managerid, "", branch.getBranchid(), propertyList);
			
			PersonTree branchTreeNode=new PersonTree();
			branchTreeNode.name=branch.getBranchname();
			
			Date nearFixedBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PERSON_FIXED_ACCOUNT);
			Date nearCurrentBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PERSON_CURRENT_ACCOUNT);
			Date nearBalDate =Utils.str82date(enddate);
			if(nearFixedBalDate==null&&nearCurrentBalDate==null)
			{
				balFlag=false;
			}
			else if(nearFixedBalDate==null||nearCurrentBalDate==null)
			{
				if(nearFixedBalDate==null)
					nearBalDate=nearCurrentBalDate;
				else
					nearBalDate=nearFixedBalDate;
			}
			else
			{
				nearBalDate=nearFixedBalDate.before(nearCurrentBalDate)?nearCurrentBalDate:nearFixedBalDate;
			}

			Date nearFixedAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PERSON_FIXED_DAYAVG);
			Date nearCurrentAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PERSON_CURRENT_DAYAVG);
			Date nearAvgDate=Utils.str82date(enddate);
			if(nearFixedAvgDate==null&&nearCurrentAvgDate==null)
			{
				avgFlag=false;
			}
			else if(nearFixedAvgDate==null||nearCurrentAvgDate==null)
			{
				if(nearFixedAvgDate==null)
					nearAvgDate=nearCurrentAvgDate;				
				else
					nearAvgDate=nearFixedAvgDate;
			}
			else
			{
				nearAvgDate=nearFixedAvgDate.before(nearCurrentAvgDate)?nearCurrentAvgDate:nearFixedAvgDate;
			}
						
			List<PersonTree> listManager=new ArrayList<PersonTree>();
			for(int j=0;j<sysAccountList.size();j++)
			{
				double managerTimeBal=0.00;
				double managerYearAvg=0.00;
				double managerPledgeYearAvg=0.00;
				double managerFixedYearAvg=0.00;
				double managerCurrentYearAvg=0.00;
				double managerFinanceYearAvg=0.00;
				double managerFixedBal=0.00;
				double managerCurrentBal=0.00;
				double managerFinanceBal=0.00;
				double managerPledgeBal=0.00;
				PersonTree managerTree=new PersonTree();				
				SysAccount sysAcc=sysAccountList.get(j);
				/*
				if(isPersonManager(sysAcc.getBusiFeature())==false)
				{
					continue;
				}*/
				managerTree.name=sysAcc.getAccountname();
				
				//查询客户经理时点余额
				if(balFlag)
				{
					HashMap<String,Double> timeBalMap=(HashMap<String,Double>)queryService.queryPersonAccountTimeBalByManagerId(sysAcc.getAccountid(),nearBalDate, Utils.str82date(enddate));
					managerTimeBal+=timeBalMap.get("managerTimeBal");
					managerFixedBal+=timeBalMap.get("managerFixedBal");
					managerFinanceBal+=timeBalMap.get("managerFinanceBal");
					managerCurrentBal+=timeBalMap.get("managerCurrentBal");
				}
											
				//查询客户经理年日均
				if(avgFlag)
				{
					HashMap<String,Double> m_map=queryService.queryPersonAccountYearDayAvgByManagerId(sysAcc.getAccountid(), nearAvgDate, Utils.str82date(enddate));
					managerYearAvg+=m_map.get("yeardayavg");
					managerFixedYearAvg+=m_map.get("avgfixed");
					managerCurrentYearAvg=m_map.get("avgcurrent");
					managerFinanceYearAvg+=m_map.get("avgfinance");	
				}
									
				//查询个人质押存款年日均
				HashMap<String,Double> pledgeBalMap=(HashMap<String,Double>)queryService.queryPersonAccountPledgeAvgByManagerId(sysAcc.getAccountid(),  Utils.str82date(enddate));
				managerPledgeYearAvg+=pledgeBalMap.get("managerPledgeAvg");
				managerPledgeBal+=pledgeBalMap.get("managerPledgeBal");
				
				branchTimeBal+=managerTimeBal;
				branchCurrentBal+=managerCurrentBal;
				branchFixedBal+=managerFixedBal;
				branchFinanceBal+=managerFinanceBal;
				branchPledgeBal+=managerPledgeBal;
				branchYearAvg+=managerYearAvg;
				branchFixedYearAvg+=managerFixedYearAvg;
				branchCurrentYearAvg+=managerCurrentYearAvg;
				branchFinanceYearAvg+=managerFinanceYearAvg;
				branchPledgeYearAvg+=managerPledgeYearAvg;
				
				managerTree.timebal=df.format(managerTimeBal);
				managerTree.balfixed=df.format(managerFixedBal);
				managerTree.balcurrent=df.format(managerCurrentBal);
				managerTree.balfinance=df.format(managerFinanceBal);
				managerTree.balpledge=df.format(managerPledgeBal);
				managerTree.avg=df.format(managerYearAvg);
				managerTree.avgfixed=df.format(managerFixedYearAvg);
				managerTree.avgcurrent=df.format(managerCurrentYearAvg);
				managerTree.avgfinance=df.format(managerFinanceYearAvg);
				managerTree.pledgeavg=df.format(managerPledgeYearAvg);
				managerTree.pledgeavgenddate=enddate;
				managerTree.leaf=true;
				listManager.add(managerTree);
			}
			totalBal+=branchBal;
			totalTimeBal+=branchTimeBal;
			totalYearAvg+=branchYearAvg;
			totalPledgeYearAvg+=branchPledgeYearAvg;
			totalFixedYearAvg+=branchFixedYearAvg;
			totalCurrentYearAvg+=branchCurrentYearAvg;
			totalFinanceYearAvg+=branchFinanceYearAvg;
			totalFixedBal+=branchFixedBal;
			totalCurrentBal+=branchCurrentBal;
			totalFinanceBal+=branchFinanceBal;
			totalPledgeBal+=branchPledgeBal;
			
			branchTreeNode.bal=df.format(totalBal);
			branchTreeNode.balenddate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
			branchTreeNode.timebal=df.format(branchTimeBal);
			branchTreeNode.timebalenddate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
			branchTreeNode.avg=df.format(branchYearAvg);
			branchTreeNode.avgenddate=Utils.getFormatDate(nearAvgDate, "yyyyMMdd");
			branchTreeNode.pledgeavg=df.format(branchPledgeYearAvg);
			branchTreeNode.pledgeavgenddate=enddate;
			branchTreeNode.avgfixed=df.format(branchFixedYearAvg);
			branchTreeNode.avgcurrent=df.format(branchCurrentYearAvg);
			branchTreeNode.avgfinance=df.format(branchFinanceYearAvg);
			branchTreeNode.balfixed=df.format(branchFixedBal);
			branchTreeNode.balcurrent=df.format(branchCurrentBal);
			branchTreeNode.balfinance=df.format(branchFinanceBal);
			branchTreeNode.balpledge=df.format(branchPledgeBal);
			branchTreeNode.children=listManager;
			if(sysAccountList.size()==0)
				branchTreeNode.leaf=true;
			else
				branchTreeNode.expanded=false;
			listBranch.add(branchTreeNode);
		}
		
		Workbook wb = new HSSFWorkbook();   
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 6000);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 6000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 5000);
		sheet.setColumnWidth(9, 6000);
		sheet.setColumnWidth(10, 6000);
		sheet.setColumnWidth(11, 6000);
		sheet.setColumnWidth(12, 6000);
		sheet.setColumnWidth(13, 6000);
		sheet.setColumnWidth(14, 6000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"机构名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,1,"个人存款年日均(合计)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,2,"理财产品年日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,3,"质押存款年日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,4,"个人存款时点余额(合计)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,5,"理财产品时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,6,"质押存款时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,7,"截止日期",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,8,"客户经理名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,9,"个人存款年日均(合计)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,10,"理财产品年日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,11,"质押存款年日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,12,"个人存款时点余额(合计)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,13,"理财产品时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,14,"质押存款时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<listBranch.size();i++)
		{
			PersonTree branchTree=listBranch.get(i);
			List<PersonTree>  listManagers=branchTree.children;
			for(int j=listManagers.size();j>0;j--)
			{
				PersonTree manager=(PersonTree)listManagers.get(j-1);
				row=sheet.createRow(nextRowNum+j-1);
				writeCellValue(wb,sheet,row,8,manager.name,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellAmount(wb,sheet,row,9,manager.avg,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,10,manager.avgfinance,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,11,manager.pledgeavg,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,12,manager.timebal,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,13,manager.balfinance,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,14,manager.balpledge,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				
			}
			if(listManagers.size()==0)
			{
				row=sheet.createRow(nextRowNum);
			}
			int regionRows=listManagers.size()==0?0:listManagers.size()-1;
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,0,0,branchTree.name,CellStyle.ALIGN_LEFT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,1,1,branchTree.avg,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,2,2,branchTree.avgfinance,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,3,3,branchTree.pledgeavg,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,4,4,branchTree.timebal,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,5,5,branchTree.balfinance,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,6,6,branchTree.balpledge,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,7,7,branchTree.balenddate,CellStyle.ALIGN_CENTER);
			nextRowNum=nextRowNum+regionRows+1;
		}
		row=sheet.createRow(nextRowNum);
		writeCellValue(wb,sheet,row,0,"合计",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellAmount(wb,sheet,row,1,String.valueOf(totalYearAvg),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellAmount(wb,sheet,row,2,String.valueOf(totalFinanceYearAvg),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellAmount(wb,sheet,row,3,String.valueOf(totalPledgeYearAvg),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellAmount(wb,sheet,row,4,String.valueOf(totalTimeBal),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellAmount(wb,sheet,row,5,String.valueOf(totalFinanceBal),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
		writeCellAmount(wb,sheet,row,6,String.valueOf(totalPledgeBal),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    String outputName="个人业务统计数据";
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK); 
		
	}
	@RequestMapping("/export/ExportPublicBusinessData")
	public  ResponseEntity<byte[]> exportPublicBusinessData(String enddate,String branchid,String managerid,HttpSession session) throws ParseException, IOException
	{
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		List<PublicTree>  totalList=new ArrayList<PublicTree>();
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==true)
			{
				SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
				branchid=tmp.getBranchid();
			}
			else
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		List<SysBranch> sysBranchList=sysBranchService.queryAllSubBranchList(branchid,true);
		List<PublicTree> listBranch=new ArrayList<PublicTree>();
		PublicTree rootNode=new PublicTree();
		double totalDepositBal=0.00;
		double totalDepositTimeBal=0.00;
		double totalDepositYearAvg=0.00;
		double totalCreditTimeBal=0.00;
		double totalCreditYearAvg=0.00;
		for(int i=0;i<sysBranchList.size();i++)
		{
			double branchBal=0.00;
			double branchDepositTimeBal=0.00;
			double branchDepositYearAvg=0.00;
			double branchCreditTimeBal=0.00;
			double branchCreditYearAvg=0.00;
			boolean balFlag=true;
			boolean avgFlag=true;
			boolean creditFlag=true;
			SysBranch branch=sysBranchList.get(i);
			
			ArrayList<String> propertyList=new ArrayList<String>();
			propertyList.add("1");
			propertyList.add("2");
			propertyList.add("3");
			propertyList.add("5");
			propertyList.add("6");
			List<SysAccount> sysAccountList=sysAccountService.querySysAccountList(managerid, "", branch.getBranchid(), propertyList);
			
			PublicTree branchTreeNode=new PublicTree();
			branchTreeNode.name=branch.getBranchname();
			
			Date nearFixedBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PUBLIC_FIXED_ACCOUNT);
			Date nearCurrentBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PUBLIC_CURRENT_ACCOUNT);
			Date nearBalDate =Utils.str82date(enddate);
			if(nearFixedBalDate==null&&nearCurrentBalDate==null)
			{
				balFlag=false;
			}
			else if(nearFixedBalDate==null||nearCurrentBalDate==null)
			{
				if(nearFixedBalDate==null)
					nearBalDate=nearCurrentBalDate;
				else
					nearBalDate=nearFixedBalDate;
			}
			else
			{
				nearBalDate=nearFixedBalDate.before(nearCurrentBalDate)?nearCurrentBalDate:nearFixedBalDate;
			}

			Date nearFixedAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PUBLIC_FIXED_ACCOUNT_DAYAVG);
			Date nearCurrentAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PUBLIC_CURRENT_ACCOUNT_DAYAVG);
			Date nearAvgDate=Utils.str82date(enddate);
			if(nearFixedAvgDate==null&&nearCurrentAvgDate==null)
			{
				avgFlag=false;
			}
			else if(nearFixedAvgDate==null||nearCurrentAvgDate==null)
			{
				if(nearFixedAvgDate==null)
					nearAvgDate=nearCurrentAvgDate;				
				else
					nearAvgDate=nearFixedAvgDate;
			}
			else
			{
				nearAvgDate=nearFixedAvgDate.before(nearCurrentAvgDate)?nearCurrentAvgDate:nearFixedAvgDate;
			}
			//对公贷款最近数据导入日期
			Date nearCreditBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PUBLIC_CREDIT_ACCOUNT);
			if(nearCreditBalDate==null)
			{
				creditFlag=false;
			}
			
			
			List<PublicTree> listManager=new ArrayList<PublicTree>();
			for(int j=0;j<sysAccountList.size();j++)
			{
				
				PublicTree managerTree=new PublicTree();
				
				SysAccount sysAcc=sysAccountList.get(j);
				/*
				if(isPublicManager(sysAcc.getBusiFeature())==false)
				{
					continue;
				}*/
				managerTree.name=sysAcc.getAccountname();
				
				//查询客户经理对公存款时点余额
				if(balFlag)
				{
					HashMap<String,Double> timeBalMap=(HashMap<String,Double>)queryService.queryPublicDepositTimeBalByManagerId(sysAcc.getAccountid(),nearBalDate, Utils.str82date(enddate));
					double timeBal=timeBalMap.get("timeBal");
					branchDepositTimeBal+=timeBal;
					managerTree.depositbal=df.format(timeBal);
					//managerTree.depositbaldate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
				}
				
														
				//查询客户经理对公存款年日均
				if(avgFlag)
				{
					double yearavg=queryService.queryPublicDepositYearDayAvgByManagerId(sysAcc.getAccountid(), nearAvgDate, Utils.str82date(enddate));
					branchDepositYearAvg+=yearavg;
					managerTree.depositavg=df.format(yearavg);
					//managerTree.depositavgdate=Utils.getFormatDate(nearAvgDate, "yyyyMMdd");
				}
									
				//查询客户经理对公存贷款时点余额
				if(creditFlag)
				{
					double creditTimeBal=queryService.queryPublicCreditTimeBalByManagerId(sysAcc.getAccountid(), nearCreditBalDate, Utils.str82date(enddate));
					branchCreditTimeBal+=creditTimeBal;
					double creditYearAvg=queryService.queryPublicCreditYearDayAvgByManagerId(sysAcc.getAccountid(), nearCreditBalDate, Utils.str82date(enddate));
					branchCreditYearAvg+=creditYearAvg;
					managerTree.creditbal=df.format(creditTimeBal);
					managerTree.creditavg=df.format(creditYearAvg);
					//managerTree.creditdate=Utils.getFormatDate(nearCreditBalDate, "yyyyMMdd");
				}
				managerTree.leaf=true;				
				listManager.add(managerTree);
			}
			totalDepositTimeBal+=branchDepositTimeBal;
			totalDepositYearAvg+=branchDepositYearAvg;
			totalCreditTimeBal+=branchCreditTimeBal;
			totalCreditYearAvg+=branchCreditYearAvg;
			
			branchTreeNode.depositbal=df.format(branchDepositTimeBal);
			branchTreeNode.depositbaldate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
			branchTreeNode.depositavg=df.format(branchDepositYearAvg);
			branchTreeNode.depositavgdate=Utils.getFormatDate(nearAvgDate, "yyyyMMdd");
			branchTreeNode.creditbal=df.format(branchCreditTimeBal);
			branchTreeNode.creditavg=df.format(branchCreditYearAvg);
			branchTreeNode.creditdate=nearCreditBalDate==null?"":Utils.getFormatDate(nearCreditBalDate, "yyyyMMdd");
			branchTreeNode.children=listManager;
			if(sysAccountList.size()==0)
				branchTreeNode.leaf=true;
			else
				branchTreeNode.expanded=false;
			listBranch.add(branchTreeNode);
		}
		Workbook wb = new HSSFWorkbook();    
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 6000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 6000);
		sheet.setColumnWidth(9, 6000);
		sheet.setColumnWidth(10, 6000);
		sheet.setColumnWidth(11, 6000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"机构名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,1,"存款进度日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,2,"存款时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,3,"截止日期",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,4,"贷款进度日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,5,"贷款时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,6,"截止日期",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,7,"客户经理名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,8,"存款进度日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,9,"存款时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,10,"贷款进度日均",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,11,"贷款时点余额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<listBranch.size();i++)
		{
			PublicTree branchTree=listBranch.get(i);
			List<PublicTree>  listManagers=branchTree.children;
			for(int j=listManagers.size();j>0;j--)
			{
				PublicTree manager=(PublicTree)listManagers.get(j-1);
				row=sheet.createRow(nextRowNum+j-1);
				writeCellValue(wb,sheet,row,7,manager.name,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
				writeCellAmount(wb,sheet,row,8,manager.depositavg,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,9,manager.depositbal,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,10,manager.creditavg,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				writeCellAmount(wb,sheet,row,11,manager.creditbal,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
				
			}
			if(listManagers.size()==0)
			{
				row=sheet.createRow(nextRowNum);
			}
			int regionRows=listManagers.size()==0?0:listManagers.size()-1;
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,0,0,branchTree.name,CellStyle.ALIGN_LEFT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,1,1,branchTree.depositavg,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,2,2,branchTree.depositbal,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,3,3,branchTree.depositbaldate,CellStyle.ALIGN_CENTER);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,4,4,branchTree.creditavg,CellStyle.ALIGN_RIGHT);
			writeRegionCellAmount(wb,sheet,row,nextRowNum,nextRowNum+regionRows,5,5,branchTree.creditbal,CellStyle.ALIGN_RIGHT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,6,6,branchTree.creditdate,CellStyle.ALIGN_CENTER);
			nextRowNum=nextRowNum+regionRows+1;
		}
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    String outputName="对公业务统计数据";
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK); 
	}
	
	@RequestMapping("export/ExportFinanceDetail")
	@ResponseBody
	public ResponseEntity<byte[]> ExportFinanceDetail(String branchid, String managerid, String accounttype, String productid, String productbatch, String startdate, String enddate, String channel, String iszy,HttpSession session) throws ParseException, IOException
	{
		Date dStartDate = startdate.equals("") ? null : Utils.str82date(startdate);
		Date dEndDate = enddate.equals("") ? null : Utils.str82date(enddate);
		List<Map> list = queryService.queryFinanceDetail(branchid, managerid, accounttype, productid, productbatch, dStartDate, dEndDate, channel,iszy);
		Workbook wb = new HSSFWorkbook();   
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 3000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 3000);
		sheet.setColumnWidth(6, 3000);
		sheet.setColumnWidth(7, 2500);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 4000);
		sheet.setColumnWidth(10, 4000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"类型",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,1,"所属机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,2,"客户账号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,3,"客户名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,4,"交易金额",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,5,"起息日",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,6,"到期日",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,7,"购买渠道",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,8,"产品类型",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,9,"产品批次",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,10,"是否质押",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<list.size();i++)
		{
			row=sheet.createRow(nextRowNum);
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			String accttype=map.get("accounttype").toString();
			if(accttype.equals("6"))
			{
				writeCellValue(wb,sheet,row,0,"对公理财",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			}
			else
			{
				writeCellValue(wb,sheet,row,0,"个人理财",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			}
			writeCellValue(wb,sheet,row,1,map.get("branchname").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,2,map.get("accountid").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,3,map.get("accountname").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			String tranamt=df.format(Double.valueOf(map.get("tranamt").toString()));
			writeCellAmount(wb,sheet,row,4,tranamt,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_RIGHT);
			String formatStartDate=Utils.getFormatDate((Date)map.get("startdate"), "yyyyMMdd");
			writeCellValue(wb,sheet,row,5,map.get("startdate").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			String formatEndDate=Utils.getFormatDate((Date)map.get("enddate"), "yyyyMMdd");
			writeCellValue(wb,sheet,row,6,map.get("enddate").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			writeCellValue(wb,sheet,row,7,map.get("channelname").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,8,map.get("productname").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,9,map.get("productbatch").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			if(map.get("iszy").toString().equals("0"))
			{
				writeCellValue(wb,sheet,row,10,"是",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			}
			else
			{
				writeCellValue(wb,sheet,row,10,"否",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			}
			nextRowNum=nextRowNum+1;
		}
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    String outputName="理财产品明细";
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK); 
	}
	
	@RequestMapping("export/ExportUnBindAccount")
	@ResponseBody
	public ResponseEntity<byte[]> ExportUnBindAccount(String accountid,String accountname,String accounttypes,HttpSession session) throws IOException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		
		ArrayList<String> acctTypeList=new ArrayList<String>();
		if(accounttypes!=null&&!accounttypes.equals(""))
		{
			String[] types=accounttypes.split(",");
			for(int i=0;i<types.length;i++)
			{
				acctTypeList.add(types[i]);
			}
		}
		else
		{
			List<SysAccountFeatureKey>  busifeatures=sysAccountService.querySysAccountFeature(sysAccount.getAccountid(), "1",null);
			acctTypeList=Utils.getAccoutTypeListByFeatures(busifeatures);
		}
		String branchid="";
		if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
		{
			branchid=sysAccount.getBranchid();
		}
		List<NoBindAccount> list=noBindAccountService.queryNoBindAccountListByBranchid(branchid,accountid,accountname,acctTypeList);
		
		Workbook wb = new HSSFWorkbook();   
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 7000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 7000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 7000);
		sheet.setColumnWidth(9, 7000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"序号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,1,"所属机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,2,"机构编号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,3,"客户账号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,4,"客户名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,5,"账户类型",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,6,"归属客户经理工号(5位工号)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,7,"归属客户经理姓名",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,8,"分配比例(填0-1之间)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,9,"客户来源(自营填1,交办填2)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<list.size();i++)
		{
			row=sheet.createRow(nextRowNum);
			NoBindAccount noBindAccount=list.get(i);
			writeCellValue(wb,sheet,row,0,String.valueOf(i+1),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,1,noBindAccount.getBranch().getBranchname(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,2,noBindAccount.getBranchid(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,3,noBindAccount.getAccountid(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,4,noBindAccount.getAccountname(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			String type;
			switch(noBindAccount.getAccounttype())
			{
				case  "1":
					type="对公活期账户";
					break;
				case "2":
					type="对公定期账户";
					break;
				case "3":
					type="对公贷款账户";
					break;
				case "4":
					type="个人活期账户";
					break;
				case "5":
					type="个人定期账户";
					break;
				default:
					type="未知账户类型 ";
					break;
			}
			writeCellValue(wb,sheet,row,5,type,fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_LEFT);
			writeCellValue(wb,sheet,row,6,"",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			writeCellValue(wb,sheet,row,7,"",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			writeCellValue(wb,sheet,row,8,"1.0",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			writeCellValue(wb,sheet,row,9,"1",fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			nextRowNum=nextRowNum+1;
		}
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    String outputName="未分配客户信息-"+Utils.getCurrentTime("yyyyMMdd");
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK); 
	}
	
	@RequestMapping("export/ExportBindAccount")
	@ResponseBody
	public ResponseEntity<byte[]> ExportBindAccount(String managerid,HttpSession session) throws IOException
	{
		ArrayList<String> accountTypeList=new ArrayList<String>();
		accountTypeList.add("4");
		accountTypeList.add("5");
		accountTypeList.add("6");
		accountTypeList.add("7");
		accountTypeList.add("8");
		SysAccount manager=sysAccountService.querySysAccountByAccountId(managerid);
		String managername=manager.getAccountname();
		List<BaseAccount> list=baseAccountService.queryBaseAccountList(null, null, null, null, managerid, accountTypeList, null, null);
		
		Workbook wb = new HSSFWorkbook();   
		Sheet sheet=wb.createSheet();
		cellStyleCache=new CellStyleCache(wb);
		Font font=wb.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 7000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 7000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 7000);
		sheet.setColumnWidth(9, 4000);
		Row row=sheet.createRow(0);
		writeCellValue(wb,sheet,row,0,"序号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,1,"所属机构",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,2,"机构编号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,3,"客户账号",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,4,"客户名称",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,5,"账户类型",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,6,"归属客户经理工号(5位工号)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,7,"归属客户经理姓名",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_LEFT);
		writeCellValue(wb,sheet,row,8,"分配比例(填0-1之间)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		writeCellValue(wb,sheet,row,9,"客户来源(自营填1,交办填2)",font,IndexedColors.GREY_25_PERCENT.getIndex(),CellStyle.ALIGN_CENTER);
		Font fontNormal=wb.createFont();		
		fontNormal.setFontHeightInPoints((short)10);
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		int nextRowNum=1;
		int nextColNum=0;
		for(int i=0;i<list.size();i++)
		{
			row=sheet.createRow(nextRowNum);
			
			ArrayList<HashMap<String,Object>> listBinds=(ArrayList<HashMap<String, Object>>) list.get(i).getBinds();
			for(int j=listBinds.size();j>0;j--)
			{
				HashMap<String,Object> bind=(HashMap<String,Object>)listBinds.get(j-1);
				row=sheet.createRow(nextRowNum+j-1);
				writeCellValue(wb,sheet,row,6,bind.get("managerid").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
				writeCellValue(wb,sheet,row,7,bind.get("managername").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
				writeCellValue(wb,sheet,row,8,bind.get("percent").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
				writeCellValue(wb,sheet,row,9,bind.get("type").toString(),fontNormal,IndexedColors.WHITE.getIndex(),CellStyle.ALIGN_CENTER);
			}
			if(listBinds.size()==0)
			{
				row=sheet.createRow(nextRowNum);
			}
			int regionRows=listBinds.size()==0?0:listBinds.size()-1;
			
			BaseAccount baseAccount=list.get(i);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,0,0,String.valueOf(i+1),CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,1,1,baseAccount.getBranch().getBranchname(),CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,2,2,baseAccount.getBranch().getBranchid(),CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,3,3,baseAccount.getAccountid(),CellStyle.ALIGN_LEFT);
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,4,4,baseAccount.getAccountname(),CellStyle.ALIGN_LEFT);
			String type;
			switch(baseAccount.getAccounttype())
			{
				case  "1":
					type="对公活期账户";
					break;
				case "2":
					type="对公定期账户";
					break;
				case "3":
					type="对公贷款账户";
					break;
				case "4":
					type="个人活期账户";
					break;
				case "5":
					type="个人定期账户";
					break;
				case "6":
					type="对公理财账户";
					break;
				case "7":
					type="个人理财账户";
					break;
				case "8":
					type="个人质押存款账户";
					break;
				default:
					type="未知账户类型 ";
					break;
			}
			writeRegionCellValue(wb,sheet,row,nextRowNum,nextRowNum+regionRows,5,5,type,CellStyle.ALIGN_LEFT);
			nextRowNum=nextRowNum+regionRows+1;
		}
		String fileName=Utils.get16UUID();
		String filePath=session.getServletContext().getRealPath("/")+"download"+File.separator+fileName+".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);   
		wb.write(fileOut);
		HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    String outputName=managername+"分配客户信息-"+Utils.getCurrentTime("yyyyMMdd");
	    headers.setContentDispositionFormData("attachment", new String(outputName.getBytes("GB2312"), "ISO_8859_1")+".xls");  
	    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),  
	                                      headers, HttpStatus.OK); 
	}
}
