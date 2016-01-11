import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

import com.nantian.custom.Utils;


public class DoubleTest
{
	public static void main(String[] args) throws ParseException{
//		DecimalFormat df=new DecimalFormat(".00##");
//		System.out.println(df.format(11111111313.12));
//		System.out.println(Double.valueOf("1,231,233.01".replaceAll(",","" )));
//		
//		String tmp="	12321321\r\n	    a";
//		System.out.println(tmp.replaceAll("\\s*|\t|\r|\n", ""));
//		Date realQueryDate=Utils.str82date("20151031");
//		Date firstDate=Utils.str82date("20150101");
//		int spaceDay=(int) ((realQueryDate.getTime()-firstDate.getTime())/(24*60*60*1000))+1;
//		System.out.println(spaceDay);
		Date endDate=Utils.str82date("20151130");
		Date firstDate=Utils.str82date("20150101");
		System.out.println(Utils.getSpaceDay(firstDate,endDate));
		
	}
}
