package com.nantian.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nantian.custom.ExcelType;
import com.nantian.custom.FormPostResult;
import com.nantian.custom.Result;
import com.nantian.custom.Utils;
import com.nantian.domain.NoBindAccount;
import com.nantian.domain.SysAccount;
import com.nantian.service.BaseAccountService;
import com.nantian.service.NoBindAccountService;
import com.nantian.service.ParseExcelService;

@Controller
public class ImportExcelController 
{
	@Resource
	private ParseExcelService  parseExcelService;
	@Resource
	private NoBindAccountService noBindAccountService;
	@Resource
	private BaseAccountService  baseAccountService;
	
	
	@RequestMapping("/uploadPerson")
	@ResponseBody
	public FormPostResult fileUploadPerson(String date,MultipartFile filedqrj,MultipartFile filedqsd,MultipartFile filehqrj,MultipartFile filehqsd,HttpSession session)
	{
		if(filedqrj==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的个人定期日均文件后再上传");
			return result;
		}
		if(filedqsd==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的个人定期日均文件后再上传");
			return result;
		}
		if(filehqrj==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的个人定期日均文件后再上传");
			return result;
		}
		if(filehqsd==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的个人定期日均文件后再上传");
			return result;
		}
		try
		{
			parseExcelService.personBatchImportFile(date, filedqrj, filedqsd, filehqrj, filehqsd, session);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg",e.getMessage());
			return result;
		}
		return new FormPostResult(true);
	}

	@RequestMapping("/uploadPublicDeposit")
	@ResponseBody
	public FormPostResult fileUploadPublicDeposit(String date,MultipartFile filedqrj,MultipartFile filedqsd,MultipartFile filehqrj,MultipartFile filehqsd,HttpSession session)
	{
		if(filedqrj==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公定期日均文件后再上传");
			return result;
		}
		if(filedqsd==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公定期日均文件后再上传");
			return result;
		}
		if(filehqrj==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公定期日均文件后再上传");
			return result;
		}
		if(filehqsd==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公定期日均文件后再上传");
			return result;
		}
		try
		{
			parseExcelService.publicDepositBatchImportFile(date, filedqrj, filedqsd, filehqrj, filehqsd, session);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg",e.getMessage());
			return result;
		}
		return new FormPostResult(true);
	}
	
	@RequestMapping("/uploadPublicCredit")
	@ResponseBody
	public FormPostResult fileUploadPublicCredit(String date,
												MultipartFile filedqdk,
												MultipartFile filedqbzdk,
												MultipartFile filedqdydk,
												MultipartFile filedqxydk,
												MultipartFile filezcqbzdk,
												MultipartFile filezcqdydk,
												MultipartFile filezcqxydk,
												HttpSession session)
	{
		if(filedqdk==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公定期 贷款文件后再上传");
			return result;
		}
		if(filedqbzdk==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公贷款短期保证贷款日均文件后再上传");
			return result;
		}
		if(filedqdydk==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公贷款短期抵押贷款日均文件后再上传");
			return result;
		}
		if(filedqxydk==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公贷款短期信用贷款日均文件后再上传");
			return result;
		}
		if(filezcqbzdk==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公贷款中长期保证贷款日均文件后再上传");
			return result;
		}
		if(filezcqdydk==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公贷款中长期抵押贷款日均文件后再上传");
			return result;
		}
		if(filezcqxydk==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的对公贷款中长期信用贷款日均文件后再上传");
			return result;
		}
		try
		{
			parseExcelService.publicCreditBatchImportFile(date, filedqdk, filedqbzdk, filedqdydk, filedqxydk, filezcqbzdk, filezcqdydk, filezcqxydk, session);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg",e.getMessage());
			return result;
		}
		return new FormPostResult(true);
	}
	
	@RequestMapping("/uploadDepoistChange")
	@ResponseBody
	public FormPostResult uploadDepoistChange(String date,MultipartFile filedqckbd,HttpSession session)
	{
		if(filedqckbd==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的个人定期日均文件后再上传");
			return result;
		}
		
		try
		{
			parseExcelService.loadFile(date, filedqckbd, ExcelType.PUBLIC_CKBD, session);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg",e.getMessage());
			return result;
		}
		return new FormPostResult(true);
	}
	@RequestMapping("uploadManagerAllocate")
	@ResponseBody
	public FormPostResult uploadManagerAllocate(MultipartFile filekhfp,String fileType,HttpSession session)
	{
		if(filekhfp==null)
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg", "选择的账户分配文件再上传");
			return result;
		}
		try
		{
			parseExcelService.managerAllocateImportFile(filekhfp, "2" ,session);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			FormPostResult result=new FormPostResult(false);
			result.addErros("errmsg",e.getMessage());
			return result;
		}
		return new FormPostResult(true);
	}
	
}
