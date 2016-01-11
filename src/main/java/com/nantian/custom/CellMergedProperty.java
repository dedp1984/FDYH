package com.nantian.custom;

public class CellMergedProperty
{
	private int firstColumn=0;
	private int lastColumn=0;
	private int firstRow=0;
	private int lastRow=0;
	private String cellValue;
	public int getFirstColumn()
	{
		return firstColumn;
	}
	public void setFirstColumn(int firstColumn)
	{
		this.firstColumn = firstColumn;
	}
	public int getLastColumn()
	{
		return lastColumn;
	}
	public void setLastColumn(int lastColumn)
	{
		this.lastColumn = lastColumn;
	}
	public int getFirstRow()
	{
		return firstRow;
	}
	public void setFirstRow(int firstRow)
	{
		this.firstRow = firstRow;
	}
	public int getLastRow()
	{
		return lastRow;
	}
	public void setLastRow(int lastRow)
	{
		this.lastRow = lastRow;
	}
	public String getCellValue()
	{
		return cellValue;
	}
	public void setCellValue(String cellValue)
	{
		this.cellValue = cellValue;
	}
	
	public int getMergedRows()
	{
		return this.lastRow-this.firstRow+1;
	}
	public String toString()
	{
		return "firstRow:"+this.firstRow+"lastRow:"+this.lastRow+" firstColumn:"+this.firstColumn+" lastColumn:"+this.lastColumn+
				"cellValue:"+this.cellValue;
	}
}
