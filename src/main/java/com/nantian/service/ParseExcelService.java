package com.nantian.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.proxy.jdbc.JdbcParameter.TYPE;
import com.nantian.custom.AccountType;
import com.nantian.custom.CellMergedProperty;
import com.nantian.custom.ExcelType;
import com.nantian.custom.Utils;
import com.nantian.dao.AccountBalRecordMapper;
import com.nantian.dao.BaseAccountMapper;
import com.nantian.dao.BindAccountToManagerMapper;
import com.nantian.dao.BranchPubAcctMapper;
import com.nantian.dao.ImportRecordMapper;
import com.nantian.dao.NoBindAccountMapper;
import com.nantian.dao.PublicDepositChangeMapper;
import com.nantian.domain.AccountBalRecord;
import com.nantian.domain.AccountBalRecordKey;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.BranchPubAcct;
import com.nantian.domain.BranchPubAcctKey;
import com.nantian.domain.ImportRecord;
import com.nantian.domain.ImportRecordKey;
import com.nantian.domain.NoBindAccount;
import com.nantian.domain.PublicDepositChange;
import com.nantian.domain.PublicDepositChangeKey;
import com.nantian.domain.SysAccount;

@Service
@Transactional(rollbackFor=Exception.class) 
public class ParseExcelService
{
	@Resource
	private BaseAccountMapper baseAccountMapper;
	@Resource
	private NoBindAccountMapper noBindAccountMapper;
	@Resource
	private BindAccountToManagerMapper bindAccountToManagerMapper;
	@Resource
	private ImportRecordMapper importRecordMapper;
	@Resource
	private PublicDepositChangeMapper  publicDepositChangeMapper;
	@Resource
	private AccountBalRecordMapper accountBalRecordDao;
	@Resource
	private BindAccountToManagerMapper atmMapper;
	@Resource
	private SysAccountService sysAccountDao;
	@Resource
	private BranchPubAcctMapper branchPubAcctMapper;
	public void loadFile(String date,MultipartFile file,String type,HttpSession session) throws Exception
	{
			SysAccount sysAccount=(SysAccount)session.getAttribute("user"); 
			String jobId=Utils.get16UUID();
			String newFileName=jobId+Utils.getFileSuffix(file.getOriginalFilename());
			String absFilePath=session.getServletContext().getRealPath("/")+"upload"+File.separator+newFileName;
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(absFilePath));
			switch(type)
			{
				//对公活期账户导入
				case ExcelType.PUBLIC_CURRENT_ACCOUNT:
					importExcelFile1(absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//对公定期账户导入
				case ExcelType.PUBLIC_FIXED_ACCOUNT:
					importExcelFile2(absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//对公贷款账户导入
				case ExcelType.PUBLIC_CREDIT_ACCOUNT:
					importExcelFile3(absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//对公活期账户日均
				case ExcelType.PUBLIC_CURRENT_ACCOUNT_DAYAVG:
				//对公定期账户日均 
				case ExcelType.PUBLIC_FIXED_ACCOUNT_DAYAVG:
					importExcelFile4to5(type,absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//对公短期保证贷款日均
				case ExcelType.PUBLIC_DQBZDK_DAYAVG:
				//对公短期抵质押贷款日均
				case ExcelType.PUBLIC_DQDYDK_DAYAVG:
				//对公短期信用贷款日均 
				case ExcelType.PUBLIC_DQXYDK_DAYAVG:
				//对公中长期保证贷款日均
				case ExcelType.PUBLIC_CQBZDK_DAYAVG:
				//对公中长期抵质押贷款日均
				case ExcelType.PUBLIC_CQDYDK_DAYAVG:
				//对公中长期信用贷款日均
				case ExcelType.PUBLIC_CQXYDK_DAYAVG:
					importExcelFile6to11(type,absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//个人定期存款日均
				case ExcelType.PERSON_FIXED_DAYAVG:
				case ExcelType.PERSON_CURRENT_DAYAVG:
					importExcelFile13_15(type, absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//个人定期存款时点
				case ExcelType.PERSON_FIXED_ACCOUNT:
					importExcelFile14(absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//个人定期存款时点
				case ExcelType.PERSON_CURRENT_ACCOUNT:
					importExcelFile16(absFilePath, date, sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
				//对公存款变动表
				case ExcelType.PUBLIC_CKBD:
					importExcelFile12(absFilePath, date,sysAccount.getBranchid(),sysAccount.getAccountid());
					break;
			}
	}
	public void personBatchImportFile(String date,MultipartFile filedqrj,MultipartFile filedqsd,MultipartFile filehqrj,MultipartFile filehqsd,HttpSession session) throws Exception
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		accountBalRecordDao.deleteImportedData(Utils.str82date(date), AccountType.PERSON_FIXED_ACCOUNT,sysAccount.getBranchid());
		loadFile(date,filedqrj,ExcelType.PERSON_FIXED_DAYAVG,session);
		loadFile(date,filedqsd,ExcelType.PERSON_FIXED_ACCOUNT,session);
	
		accountBalRecordDao.deleteImportedData(Utils.str82date(date), AccountType.PERSON_CURRENT_ACCOUNT,sysAccount.getBranchid());
		loadFile(date,filehqrj,ExcelType.PERSON_CURRENT_DAYAVG,session);
		loadFile(date,filehqsd,ExcelType.PERSON_CURRENT_ACCOUNT,session);
		
	}
	public void publicDepositBatchImportFile(String date,MultipartFile filedqrj,MultipartFile filedqsd,MultipartFile filehqrj,MultipartFile filehqsd,HttpSession session) throws Exception
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		accountBalRecordDao.deleteImportedData(Utils.str82date(date), AccountType.PUBLIC_FIXED_ACCOUNT,sysAccount.getBranchid());
		loadFile(date,filedqrj,ExcelType.PUBLIC_FIXED_ACCOUNT_DAYAVG,session);
		loadFile(date,filedqsd,ExcelType.PUBLIC_FIXED_ACCOUNT,session);		
		
		accountBalRecordDao.deleteImportedData(Utils.str82date(date), AccountType.PUBLIC_CURRENT_ACCOUNT,sysAccount.getBranchid());
		loadFile(date,filehqrj,ExcelType.PUBLIC_CURRENT_ACCOUNT_DAYAVG,session);
		loadFile(date,filehqsd,ExcelType.PUBLIC_CURRENT_ACCOUNT,session);
		
	}
	
	public void managerAllocateImportFile(MultipartFile file,String fileType,HttpSession session) throws Exception
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user"); 
		String jobId=Utils.get16UUID();
		String newFileName=jobId+Utils.getFileSuffix(file.getOriginalFilename());
		String absFilePath=session.getServletContext().getRealPath("/")+"upload"+File.separator+newFileName;
		FileUtils.copyInputStreamToFile(file.getInputStream(), new File(absFilePath));
		Workbook wb = null;
		wb = WorkbookFactory.create(new File(absFilePath));
		Sheet sheet = wb.getSheetAt(0);
		int totalRows = sheet.getLastRowNum();
		int startRow=1;
		int totalAccount=0;
		while(startRow<=totalRows)
		{
			System.out.println("startRow="+startRow);
			HashMap<String,String> managerRepeatTest=new HashMap<String,String>();
			Double totalPercent=0.00;
			CellMergedProperty property=new CellMergedProperty();
			List<BindAccountToManager> bindList=new ArrayList<BindAccountToManager>();
			String accountid;
			String belongBranch;
			if(isMergeCell(sheet,startRow,0,property))
			{
				Row row=sheet.getRow(startRow);
				accountid=getCellValue(row.getCell(3));
				belongBranch=getCellValue(row.getCell(2));
				if(accountid.trim().length()==0)
				{
					throw new Exception("第"+(startRow+1)+"行客户账号为空,请检查文件");
				}
				for(int i=0;i<property.getMergedRows();i++)
				{
					Row tmpRow=sheet.getRow(startRow);			
					String toManagerId=getCellValue(tmpRow.getCell(6));
					String toManagerName=getCellValue(tmpRow.getCell(7));
					Double percent=Double.valueOf(getCellValue(tmpRow.getCell(8)));
					totalPercent+=percent;
					String type=getCellValue(tmpRow.getCell(9));
					if(managerRepeatTest.containsKey(toManagerId))
					{
						throw new Exception("第"+(startRow+1)+"行归属客户经理分配重复,请检查文件");
					}
					managerRepeatTest.put(toManagerId, toManagerName);
					if(toManagerId.trim().length()==0)
					{
						throw new Exception("第"+(startRow+1)+"行归属客户经理工号为空,请检查文件");
					}
					if(percent==null)
					{
						throw new Exception("第"+(startRow+1)+"行分配比例为空,请检查文件");
					}
					if(!type.equals("1")&&!type.equals("2"))
					{
						throw new Exception("第"+(startRow+1)+"行账户来源错误,自营填1或交办填2");
					}
					if(sysAccountDao.querySysAccountByAccountId(toManagerId)==null)
					{
						throw new Exception("第"+(startRow+1)+"行归属客户经理【"+toManagerName+"】工号【"+toManagerId+"】系统未录入,请与系统管理员联系");
					}
					
					BindAccountToManager batm=new BindAccountToManager();
					batm.setAccountid(accountid);
					batm.setManagerid(toManagerId);
					batm.setPercent(percent);
					batm.setType(type);
					bindList.add(batm);
					startRow++;
				}
			}
			else
			{
				Row tmpRow=sheet.getRow(startRow);
				accountid=getCellValue(tmpRow.getCell(3));
				belongBranch=getCellValue(tmpRow.getCell(2));
				String toManagerId=getCellValue(tmpRow.getCell(6));
				String toManagerName=getCellValue(tmpRow.getCell(7));
				Double percent=Double.valueOf(getCellValue(tmpRow.getCell(8)));
				totalPercent+=percent;
				String type=getCellValue(tmpRow.getCell(9));
				if(accountid.trim().length()==0)
				{
					throw new Exception("第"+(startRow+1)+"行客户账号为空,请检查文件");
				}
				if(toManagerId.trim().length()==0)
				{
					throw new Exception("第"+(startRow+1)+"行归属客户经理工号为空,请检查文件");
				}
				if(percent==null)
				{
					throw new Exception("第"+(startRow+1)+"行分配比例为空,请检查文件");
				}
				toManagerId=toManagerId.replaceAll("\\s*|\t|\r|\n", "");
				if(sysAccountDao.querySysAccountByAccountId(toManagerId)==null)
				{
					throw new Exception("第"+(startRow+1)+"行归属客户经理【"+toManagerName+"】工号【"+toManagerId+"】系统未录入,请与系统管理员联系");
				}
				
				BindAccountToManager batm=new BindAccountToManager();
				batm.setAccountid(accountid);
				batm.setManagerid(toManagerId);
				batm.setPercent(percent);
				batm.setType(type);
				bindList.add(batm);
				startRow++;
			}
			
			if(totalPercent-1.0<-0.0001)
			{
				if(fileType.equals("2"))
				{
					BranchPubAcctKey key=new BranchPubAcctKey();
					key.setBranchid(belongBranch);
					key.setType("2");
					BranchPubAcct pubAcct=branchPubAcctMapper.selectByPrimaryKey(key);
					if(pubAcct==null)
					{
						throw new Exception("第"+(startRow)+"行"+belongBranch+"机构没有设置虚拟对公账号");
					}
					else
					{
						BindAccountToManager batm=new BindAccountToManager();
						batm.setAccountid(accountid);
						batm.setManagerid(pubAcct.getAccountid());
						batm.setPercent(1-totalPercent);
						batm.setType("2");
						bindList.add(batm);
					}
				}
				else
				{
					throw new Exception("第"+(startRow)+"行"+accountid+"分配比例总和不等于100%");
				}
			}
			if(totalPercent>1.0)
			{
				throw new Exception("第"+(startRow)+"行"+accountid+"分配比例总和大于100%");
			}
			totalAccount++;
			NoBindAccount noBindAccount=noBindAccountMapper.selectByPrimaryKey(accountid);
			if(noBindAccount!=null)
			{
				BaseAccount baseAccount=new BaseAccount();
				baseAccount.setBranchid(noBindAccount.getBranchid());
				baseAccount.setAccountid(noBindAccount.getAccountid());
				baseAccount.setAccountname(noBindAccount.getAccountname());
				baseAccount.setAccounttype(noBindAccount.getAccounttype());
				baseAccount.setMaccountid(noBindAccount.getMaccountid());
				baseAccount.setCustomno(noBindAccount.getCustomno());
				baseAccount.setCurrency(noBindAccount.getCurrency());
				baseAccount.setOpendate(noBindAccount.getOpendate());
				baseAccount.setEnddate(noBindAccount.getEnddate());
				baseAccount.setSubcode(noBindAccount.getSubcode());
				baseAccount.setProperty(noBindAccount.getProperty());
				baseAccount.setAcctstatus(noBindAccount.getAcctstatus());
				if(baseAccountMapper.selectByPrimaryKey(accountid)!=null)
				{
					baseAccountMapper.updateByPrimaryKeySelective(baseAccount);
				}
				else
				{
					baseAccountMapper.insert(baseAccount);
				}
				noBindAccountMapper.deleteByPrimaryKey(accountid);
				bindAccountToManagerMapper.deleteByAccountId(accountid);
				for(int i=0;i<bindList.size();i++)
				{
					BindAccountToManager bind=bindList.get(i);
					atmMapper.insert(bind);
				}
			}
			else
			{
				bindAccountToManagerMapper.deleteByAccountId(accountid);
				for(int i=0;i<bindList.size();i++)
				{
					BindAccountToManager bind=bindList.get(i);
					atmMapper.insert(bind);
				}
			}
		}
		this.saveImportRecord(Utils.getCurrentTime("yyyyMMdd"), absFilePath, sysAccount.getBranchid(), sysAccount.getAccountid(), ExcelType.PERSON_MANAGER_ALLOC);
		
	}
	public void publicCreditBatchImportFile(String date,
												MultipartFile filedqdk,
												MultipartFile filedqbzdk,
												MultipartFile filedqdydk,
												MultipartFile filedqxydk,
												MultipartFile filezcqbzdk,
												MultipartFile filezcqdydk,
												MultipartFile filezcqxydk,
												HttpSession session) throws Exception
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		accountBalRecordDao.deleteImportedData(Utils.str82date(date), AccountType.PUBLIC_CREDIT_ACCOUNT,sysAccount.getBranchid());

		loadFile(date,filedqbzdk,ExcelType.PUBLIC_DQBZDK_DAYAVG,session);
		loadFile(date,filedqdydk,ExcelType.PUBLIC_DQDYDK_DAYAVG,session);
		loadFile(date,filedqxydk,ExcelType.PUBLIC_DQXYDK_DAYAVG,session);
		loadFile(date,filezcqbzdk,ExcelType.PUBLIC_CQBZDK_DAYAVG,session);
		loadFile(date,filezcqdydk,ExcelType.PUBLIC_CQDYDK_DAYAVG,session);
		loadFile(date,filezcqxydk,ExcelType.PUBLIC_CQXYDK_DAYAVG,session);
		loadFile(date,filedqdk,ExcelType.PUBLIC_CREDIT_ACCOUNT,session);
	}
	/**
	 * 功能：记录导入数据相关信息
	 * **/
	private void saveImportRecord(String date,String filePath,String operBranchId,String operId,String fileType) throws ParseException
	{
		ImportRecord record=new ImportRecord();
		record.setBranchid(operBranchId);
		record.setImportdate(Utils.str82date(date));
		record.setFilename(filePath);
		record.setFiletype(fileType);
		record.setOperid(operId);
		ImportRecordKey key=new ImportRecordKey();
		key.setBranchid(operBranchId);
		key.setFiletype(fileType);
		key.setImportdate(Utils.str82date(date));
		if(importRecordMapper.selectByPrimaryKey(key)!=null)
		{
			importRecordMapper.updateByPrimaryKey(record);
		}
		else
		{
			importRecordMapper.insert(record);
		}
	}
	private void insertAccountBalRecord(AccountBalRecord record)
	{
		BaseAccount baseAccount=baseAccountMapper.selectByPrimaryKey(record.getAccountid());
		if (baseAccount == null)
		{
			NoBindAccount noBindAccount = new NoBindAccount();
			noBindAccount.setBranchid(record.getBranchid());
			noBindAccount.setAccountid(record.getAccountid());
			noBindAccount.setAccountname(record.getAccountname());
			noBindAccount.setAccounttype(record.getAccounttype());
			noBindAccount.setMaccountid(record.getMaccountid());
			noBindAccount.setCustomno(record.getCustomno());
			noBindAccount.setCurrency(record.getCurrency());
			noBindAccount.setSubcode(record.getSubcode());
			noBindAccount.setProperty(record.getProperty());
			noBindAccount.setAcctstatus(record.getAcctstatus());
			if (noBindAccountMapper.selectByPrimaryKey(record.getAccountid()) == null)
			{
				//账户状态为销户，余额为0。00,，同时没有日均余额信息的记录不导入进去
				if(record.getAcctstatus()!=null)
				{
					if(record.getAcctstatus().trim().equals("销户")&&record.getBal()==0.00)
					{
						return;
					}
				}
					
				noBindAccountMapper.insert(noBindAccount);
			}
			else
			{
				noBindAccountMapper.updateByPrimaryKeySelective(noBindAccount);
			}
		}
		else
		{
			baseAccount.setMaccountid(record.getMaccountid());
			baseAccount.setCustomno(record.getCustomno());
			baseAccount.setCurrency(record.getCurrency());
			baseAccount.setSubcode(record.getSubcode());
			baseAccount.setAcctstatus(record.getAcctstatus());
			baseAccount.setProperty(record.getProperty());
			baseAccountMapper.updateByPrimaryKeySelective(baseAccount);
		}
		
		AccountBalRecordKey key=new AccountBalRecordKey();
		key.setAccountid(record.getAccountid());
		key.setGendate(record.getGendate());
		AccountBalRecord tmp=accountBalRecordDao.selectByPrimaryKey(key);
		if(tmp!=null)
		{
			tmp.setBal(record.getBal()+tmp.getBal());
			tmp.setYearavg(record.getYearavg()+tmp.getYearavg());
			tmp.setM1(record.getM1()+tmp.getM1());
			tmp.setM2(record.getM2()+tmp.getM2());
			tmp.setM3(record.getM3()+tmp.getM3());
			tmp.setM4(record.getM4()+tmp.getM4());
			tmp.setM5(record.getM5()+tmp.getM5());
			tmp.setM6(record.getM6()+tmp.getM6());
			tmp.setM7(record.getM7()+tmp.getM7());
			tmp.setM8(record.getM8()+tmp.getM8());
			tmp.setM9(record.getM9()+tmp.getM9());
			tmp.setM10(record.getM10()+tmp.getM10());
			tmp.setM11(record.getM11()+tmp.getM11());
			tmp.setM12(record.getM12()+tmp.getM12());
			
			accountBalRecordDao.updateByPrimaryKey(tmp);
		}
		else
		{
			accountBalRecordDao.insert(record);
		}
		
		
		
	}
	/**
	 * 功能名称：导入对公定期账户余额明细
	 * 输入参数：filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile2(String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum();
		Row header=sheet.getRow(0);
		String title=getCellValue(header.getCell(0));
		if(!title.trim().equals("对公定期存款账户查询"))
			throw new Exception("非法的对公定期存款账户余额明细文件");
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			record.setBranchid(operBranchId);
			record.setGendate(Utils.str82date(date));
			record.setAccountid(getCellValue(row.getCell(7)));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("对公定期账户余额明细文件【"+(i+1)+"行,8列客户账号为空】");
			}
			record.setAccountname(getCellValue(row.getCell(6)));
			record.setAccounttype(AccountType.PUBLIC_FIXED_ACCOUNT);
			record.setMaccountid(getCellValue(row.getCell(2)));
			record.setCurrency(getCellValue(row.getCell(3)));
			record.setSubcode(getCellValue(row.getCell(4)));
			record.setCustomno(getCellValue(row.getCell(5)));
			double bal = Double.valueOf(getCellValue(row.getCell(8)));
			record.setBal(bal);
			record.setProperty(getCellValue(row.getCell(13)));
			record.setAcctstatus(getCellValue(row.getCell(14)));
			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, ExcelType.PUBLIC_FIXED_ACCOUNT);
		wb.close();
	}
	/**
	 * 功能名称：导入对公活期账户余额明细
	 * 输入参数：filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile1(String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		//InputStream is = new FileInputStream(filePath);
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum();
		Row header=sheet.getRow(0);
		String title=getCellValue(header.getCell(0));
		if(!title.trim().equals("对公活期存款账户查询"))
			throw new Exception("非法的对公活期存款账户余额明细文件");
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			record.setBranchid(operBranchId);
			record.setGendate(Utils.str82date(date));
			record.setAccountid(getCellValue(row.getCell(7)));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("对公活期账户余额明细文件【"+(i+1)+"行,8列客户账号为空】");
			}
			record.setAccountname(getCellValue(row.getCell(6)));
			record.setAccounttype(AccountType.PUBLIC_CURRENT_ACCOUNT);
			record.setMaccountid(getCellValue(row.getCell(2)));
			record.setCurrency(getCellValue(row.getCell(3)));
			record.setSubcode(getCellValue(row.getCell(4)));
			record.setCustomno(getCellValue(row.getCell(5)));	
			double bal = Double.valueOf(getCellValue(row.getCell(8)));
			record.setBal(bal);
			record.setProperty(getCellValue(row.getCell(11)));
			record.setAcctstatus(getCellValue(row.getCell(12)));

			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, ExcelType.PUBLIC_CURRENT_ACCOUNT);
		wb.close();
	}
	/**
	 * 功能名称：导入对公贷款账户余额明细
	 * 输入参数：filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile3(String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		//InputStream is = new FileInputStream(filePath);
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum()-1;
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			record.setBranchid(operBranchId);
			record.setGendate(Utils.str82date(date));
			record.setAccountid(getCellValue(row.getCell(4)));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("对公贷款账户余额明细文件【"+(i+1)+"行,5列客户账号为空】");
			}
			record.setCustomno(getCellValue(row.getCell(2)));
			record.setAccountname(getCellValue(row.getCell(3)));
			record.setAccounttype(AccountType.PUBLIC_CREDIT_ACCOUNT);
			record.setSubcode(getCellValue(row.getCell(9)));
			double creditBal = Double.valueOf(getCellValue(row.getCell(11)));
			record.setBal(creditBal);
			record.setMrate(Double.valueOf(getCellValue(row.getCell(18))));
			record.setFdfs(getCellValue(row.getCell(19)));
			record.setFdbl(Double.valueOf(getCellValue(row.getCell(21))));
			record.setWjfl(getCellValue(row.getCell(26)));
			record.setAcctstatus(getCellValue(row.getCell(25)));
			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, ExcelType.PUBLIC_CREDIT_ACCOUNT);
		wb.close();
	}
	/**
	 * 功能名称：导入对公定期、对公活期账户日均明细
	 * 输入参数：type  文件类型
	 * 		 filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile4to5(String type,String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum();
		Row header=sheet.getRow(0);
		String accountType;
		String title=getCellValue(header.getCell(0));
		if(type==ExcelType.PUBLIC_FIXED_ACCOUNT_DAYAVG)
		{
			rowCnt=rowCnt-1;
			accountType=AccountType.PUBLIC_FIXED_ACCOUNT;
			if(!title.trim().equals("对公定期存款账户日均余额"))
				throw new Exception("非法的对公定期存款账户日均余额明细文件");
			//对公定期账户日均倒数第二行为空
			
		}
		else
		{
			accountType=AccountType.PUBLIC_CURRENT_ACCOUNT;
			if(!title.trim().equals("对公活期存款账户日均余额"))
				throw new Exception("非法的对公活期存款账户日均余额明细文件");
		}
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			
			record.setBranchid(operBranchId);
			record.setGendate(Utils.str82date(date));
			record.setAccounttype(accountType);
			record.setAccountid(getCellValue(row.getCell(2)));
			record.setAccountname(getCellValue(row.getCell(4)));
			record.setCustomno(getCellValue(row.getCell(3)));
			record.setSubcode(getCellValue(row.getCell(6)));
			record.setCurrency(getCellValue(row.getCell(5)));
			record.setYearavg(Double.valueOf(getCellValue(row.getCell(7))));
			record.setM1(Double.valueOf(getCellValue(row.getCell(8))));
			record.setM2(Double.valueOf(getCellValue(row.getCell(9))));
			record.setM3(Double.valueOf(getCellValue(row.getCell(10))));
			record.setM4(Double.valueOf(getCellValue(row.getCell(11))));
			record.setM5(Double.valueOf(getCellValue(row.getCell(12))));
			record.setM6(Double.valueOf(getCellValue(row.getCell(13))));
			record.setM7(Double.valueOf(getCellValue(row.getCell(14))));
			record.setM8(Double.valueOf(getCellValue(row.getCell(15))));
			record.setM9(Double.valueOf(getCellValue(row.getCell(16))));
			record.setM10(Double.valueOf(getCellValue(row.getCell(17))));
			record.setM11(Double.valueOf(getCellValue(row.getCell(18))));
			record.setM12(Double.valueOf(getCellValue(row.getCell(19))));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("对公存款日均余额明细文件【"+(i+1)+"行,3列客户账号为空】");
			}
			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, type);
		wb.close();
	}
	/**
	 * 功能名称：导入对公贷款日均明细
	 * 输入参数：type  文件类型
	 * 		 filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile6to11(String type,String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		//InputStream is = new FileInputStream(filePath);
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum();
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			record.setBranchid(operBranchId);
			record.setGendate(Utils.str82date(date));
			record.setAccountid(getCellValue(row.getCell(4)));
			record.setAccountname(getCellValue(row.getCell(3)));
			record.setAccounttype(AccountType.PUBLIC_CREDIT_ACCOUNT);
			record.setSubcode(getCellValue(row.getCell(5)));
			record.setYearavg(Double.valueOf(getCellValue(row.getCell(6))));
			record.setBadyearavg(Double.valueOf(getCellValue(row.getCell(7))));
			record.setM1(Double.valueOf(getCellValue(row.getCell(8))));
			record.setM2(Double.valueOf(getCellValue(row.getCell(9))));
			record.setM3(Double.valueOf(getCellValue(row.getCell(10))));
			record.setM4(Double.valueOf(getCellValue(row.getCell(11))));
			record.setM5(Double.valueOf(getCellValue(row.getCell(12))));
			record.setM6(Double.valueOf(getCellValue(row.getCell(13))));
			record.setM7(Double.valueOf(getCellValue(row.getCell(14))));
			record.setM8(Double.valueOf(getCellValue(row.getCell(15))));
			record.setM9(Double.valueOf(getCellValue(row.getCell(16))));
			record.setM10(Double.valueOf(getCellValue(row.getCell(17))));
			record.setM11(Double.valueOf(getCellValue(row.getCell(18))));
			record.setM12(Double.valueOf(getCellValue(row.getCell(19))));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("对公贷款日均余额明细文件【"+(i+1)+"行,3列客户账号为空】");
			}
			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, type);
		wb.close();
	}
	/**
	 * 功能名称：导入个人定期、个人活期存款日均明细
	 * 输入参数：type  文件类型
	 * 		 filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile13_15(String type,String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum();
		Row header=sheet.getRow(0);
		String title=getCellValue(header.getCell(0));
		String accountType="";
		if(type==ExcelType.PERSON_CURRENT_DAYAVG)
		{
			accountType=AccountType.PERSON_CURRENT_ACCOUNT;
			if(!title.trim().equals("个人活期存款账户日均余额"))
				throw new Exception("非法的个人活期存款账户日均余额明细文件");
		}
		else
		{
			accountType=AccountType.PERSON_FIXED_ACCOUNT;
			if(!title.trim().equals("个人定期存款账户日均余额"))
				throw new Exception("非法的个人定期存款账户日均余额明细文件");
		}
		
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			record.setGendate(Utils.str82date(date));
			record.setAccountid(getCellValue(row.getCell(2)));
			record.setBranchid("0023"+record.getAccountid().substring(0,5));
			record.setAccountname(getCellValue(row.getCell(4)));
			record.setAccounttype(accountType);
			record.setCustomno(getCellValue(row.getCell(3)));
			record.setCurrency(getCellValue(row.getCell(5)));
			record.setSubcode(getCellValue(row.getCell(6)));
			record.setYearavg(Double.valueOf(getCellValue(row.getCell(7))));
			record.setBadyearavg(0.00);
			record.setM1(Double.valueOf(getCellValue(row.getCell(8))));
			record.setM2(Double.valueOf(getCellValue(row.getCell(9))));
			record.setM3(Double.valueOf(getCellValue(row.getCell(10))));
			record.setM4(Double.valueOf(getCellValue(row.getCell(11))));
			record.setM5(Double.valueOf(getCellValue(row.getCell(12))));
			record.setM6(Double.valueOf(getCellValue(row.getCell(13))));
			record.setM7(Double.valueOf(getCellValue(row.getCell(14))));
			record.setM8(Double.valueOf(getCellValue(row.getCell(15))));
			record.setM9(Double.valueOf(getCellValue(row.getCell(16))));
			record.setM10(Double.valueOf(getCellValue(row.getCell(17))));
			record.setM11(Double.valueOf(getCellValue(row.getCell(18))));
			record.setM12(Double.valueOf(getCellValue(row.getCell(19))));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("个人日均账户余额明细文件【"+(i+1)+"行,3列客户账号为空】");
			}
			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, type);
		wb.close();
	}
	private String getCellValue(Cell cell)
	{
		switch (cell.getCellType())
		{
		case Cell.CELL_TYPE_STRING:
			return cell.getRichStringCellValue().getString();
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell))
			{
				Date date=cell.getDateCellValue();
				return Utils.getFormatDate(date,"yyyyMMdd");
			}
			else
			{
				BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
				return bd.toPlainString();
			}
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}
	/**
	 * 功能名称：导入个人定期存款时点账户余额明细
	 * 输入参数：filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile14(String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
	
		Workbook wb = null;
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum();
		Row header=sheet.getRow(0);
		String title=getCellValue(header.getCell(0));
		if(!title.trim().equals("个人定期存款账户查询"))
			throw new Exception("非法的个人定期存款账户余额明细文件");
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			
			record.setGendate(Utils.str82date(date));
			record.setAccountid(getCellValue(row.getCell(6)));
			record.setBranchid("0023"+record.getAccountid().substring(0,5));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("个人定期存款账户余额明细文件【"+(i+1)+"行,7列客户账号为空】");
			}
			record.setAccountname(getCellValue(row.getCell(8)));
			if(record.getAccountname().trim().equals(""))
			{
				throw new Exception("个人定期存款账户余额明细文件【"+(i+1)+"行,9列客户名称为空】");
			}
			record.setAccounttype(AccountType.PERSON_FIXED_ACCOUNT);
			record.setMaccountid(getCellValue(row.getCell(3)));
			record.setCurrency(getCellValue(row.getCell(4)));
			record.setSubcode(getCellValue(row.getCell(5)));
			record.setCustomno(getCellValue(row.getCell(7)));
			double bal = Double.valueOf(getCellValue(row.getCell(9)));
			record.setBal(bal);
			record.setAcctstatus(getCellValue(row.getCell(13)));
			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, ExcelType.PERSON_FIXED_ACCOUNT);
		wb.close();
	}
	/**
	 * 功能名称：导入个人活期存款时点账户余额明细
	 * 输入参数：filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * @throws Exception 
	 * **/
	public void importExcelFile16(String filePath, String date, String operBranchId, String operId) throws Exception
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		int rowCnt = sheet.getLastRowNum();
		Row header=sheet.getRow(0);
		String title=getCellValue(header.getCell(0));
		if(!title.trim().equals("个人活期存款账户查询（有户名）"))
			throw new Exception("非法的个人活期存款账户余额明细文件");
		for (int i = 3; i < rowCnt; i++)
		{
			// 解析EXCEL文件
			Row row = sheet.getRow(i);
			AccountBalRecord record=new AccountBalRecord();
			
			record.setGendate(Utils.str82date(date));
			record.setAccountid(getCellValue(row.getCell(7)));
			record.setBranchid("0023"+record.getAccountid().substring(0,5));
			if(record.getAccountid().trim().equals(""))
			{
				throw new Exception("个人活期存款账户余额明细文件【"+(i+1)+"行,8列客户账号为空】");
			}
			record.setAccountname(getCellValue(row.getCell(4)));
			if(record.getAccountname().trim().equals(""))
			{
				throw new Exception("个人活期存款账户余额明细文件【"+(i+1)+"行,5列客户名称为空】");
			}
			record.setAccounttype(AccountType.PERSON_CURRENT_ACCOUNT);
			record.setCurrency(getCellValue(row.getCell(8)));
			record.setSubcode(getCellValue(row.getCell(9)));
			record.setCustomno(getCellValue(row.getCell(3)));
			double bal = Double.valueOf(getCellValue(row.getCell(11)));
			record.setBal(bal);
			record.setAcctstatus(getCellValue(row.getCell(12)));
			insertAccountBalRecord(record);
		}
		this.saveImportRecord(date, filePath, operBranchId, operId, ExcelType.PERSON_CURRENT_ACCOUNT);
		wb.close();
	}
	
	/**
	 * 功能名称：导入个人定期存款变动明细
	 * 输入参数：filePath 文件路径
	 * 		 date  数据产生日期
	 * 		 operBranchId 操作机构
	 *       operId 操作员工号
	 * **/
	public void importExcelFile12(String filePath, String tmpDate,String operBranchId, String operId) throws EncryptedDocumentException, InvalidFormatException, IOException, ParseException
	{
		// TODO Auto-generated method stub
		Workbook wb = null;
		wb = WorkbookFactory.create(new File(filePath));
		Sheet sheet = wb.getSheetAt(0);
		Row dataRow=sheet.getRow(1);
		String cellDate=getCellValue(dataRow.getCell(0)).replaceAll("：", ":");
		String date=cellDate.split(":")[1].trim();
		publicDepositChangeMapper.deleteByGendate(Utils.str82date(date));
		int totalRows=sheet.getLastRowNum()-1;
		int startRow=3;
		while(startRow<=totalRows)
		{
			PublicDepositChange record=new PublicDepositChange();
			CellMergedProperty property=new CellMergedProperty();
			if(isMergeCell(sheet,startRow,0,property))
			{
				Row row=sheet.getRow(startRow);
				String branchId=property.getCellValue();
				String branchName=getCellValue(row.getCell(1));
				for(int i=0;i<property.getMergedRows();i++)
				{
					Row tmpRow=sheet.getRow(startRow);					
					double dqye=Double.valueOf(getCellValue(tmpRow.getCell(2)));
					double ncye=Double.valueOf(getCellValue(tmpRow.getCell(3)));
					double toLastDay=Double.valueOf(getCellValue(tmpRow.getCell(4)));
					double toLastYear=Double.valueOf(getCellValue(tmpRow.getCell(5)));
					String accountName=getCellValue(tmpRow.getCell(6));
					String subCode=getCellValue(tmpRow.getCell(7));
					double inAmt=Double.valueOf(getCellValue(tmpRow.getCell(8)));
					double outAmt=Double.valueOf(getCellValue(tmpRow.getCell(9)));
					double netInOut=Double.valueOf(getCellValue(tmpRow.getCell(10)));
					String currency=getCellValue(tmpRow.getCell(11));		
					record.setGenyear(date.substring(0, 4));
					record.setGendate(Utils.str82date(date));
					record.setBranchid(branchId);
					record.setBranchname(branchName);
					record.setDqye(dqye);
					record.setNcye(ncye);
					record.setTolastday(toLastDay);
					record.setTolastyear(toLastYear);
					record.setAccountname(accountName);
					record.setSubcode(subCode);
					record.setInamt(inAmt);
					record.setOutamt(outAmt);
					record.setNetinout(netInOut);
					record.setCurrency(currency);
					PublicDepositChangeKey key=new PublicDepositChangeKey();
					key.setGendate(Utils.str82date(date));
					key.setAccountname(accountName);
					if(publicDepositChangeMapper.selectByPrimaryKey(key)!=null)
					{
						publicDepositChangeMapper.updateByPrimaryKey(record);
					}
					else
					{
						publicDepositChangeMapper.insert(record);
					}
					startRow++;
				}
			}
			else
			{
				Row tmpRow=sheet.getRow(startRow);
				String branchId=getCellValue(tmpRow.getCell(0));
				String branchName=getCellValue(tmpRow.getCell(1));
				double dqye=Double.valueOf(getCellValue(tmpRow.getCell(2)));
				double ncye=Double.valueOf(getCellValue(tmpRow.getCell(3)));
				double toLastDay=Double.valueOf(getCellValue(tmpRow.getCell(4)));
				double toLastYear=Double.valueOf(getCellValue(tmpRow.getCell(5)));
				String accountName=getCellValue(tmpRow.getCell(6));
				String subCode=getCellValue(tmpRow.getCell(7));
				double inAmt=Double.valueOf(getCellValue(tmpRow.getCell(8)));
				double outAmt=Double.valueOf(getCellValue(tmpRow.getCell(9)));
				double netInOut=Double.valueOf(getCellValue(tmpRow.getCell(10)));
				String currency=getCellValue(tmpRow.getCell(11));
				record.setGenyear(date.substring(0, 4));
				record.setGendate(Utils.str82date(date));
				record.setBranchid(branchId);
				record.setBranchname(branchName);
				record.setDqye(dqye);
				record.setNcye(ncye);
				record.setTolastday(toLastDay);
				record.setTolastyear(toLastYear);
				record.setAccountname(accountName);
				record.setSubcode(subCode);
				record.setInamt(inAmt);
				record.setOutamt(outAmt);
				record.setNetinout(netInOut);
				record.setCurrency(currency);
				PublicDepositChangeKey key=new PublicDepositChangeKey();
				key.setGendate(Utils.str82date(date));
				key.setAccountname(accountName);
				if(publicDepositChangeMapper.selectByPrimaryKey(key)!=null)
				{
					publicDepositChangeMapper.updateByPrimaryKey(record);
				}
				else
				{
					publicDepositChangeMapper.insert(record);
				}
				startRow++;
			}
			
		}
		
		this.saveImportRecord(date, filePath, operBranchId, operId, ExcelType.PUBLIC_CKBD);
		wb.close();
	}
	
	private boolean isMergeCell(Sheet sheet,int  rowIndex,int cellIndex,CellMergedProperty property)
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
					property.setCellValue(getCellValue(cell));
					return true;
				}
					
			}
			
		}
		return false;
		
	}

}
