package com.gistone.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class DateFormatUtil {
	public static String FormatD(String dateStr){
	    HashMap<String, String> dateRegFormat = new HashMap<String, String>();
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", "yyyy-MM-dd-HH-mm-ss");//2014年3月12日 13时5分34秒，2014-03-12 12:05:34，2014/3/12 12:5:34
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D*$", "yyyy-MM");//2014年3月
	    dateRegFormat.put("^\\d{4}\\D*$", "yyyy");//2014年
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{2}\\D+\\d{2}$", "yyyy-MM-dd-HH-mm");//2014-03-12 12:05
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{2}$", "yyyy-MM-dd-HH");//2014-03-12 12
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}", "yyyy-MM-dd");//2014-03-12    
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy/MM/dd");//2014/03/02 
	    dateRegFormat.put("^\\d{4}$", "yyyy");//2014年
	    dateRegFormat.put("^\\d{4}", "yyyy");//2014
	    dateRegFormat.put("^\\d{14}$", "yyyyMMddHHmmss");//20140312120534
	    dateRegFormat.put("^\\d{12}$", "yyyyMMddHHmm");//201403121205
	    dateRegFormat.put("^\\d{10}$", "yyyyMMddHH");//2014031212
	    dateRegFormat.put("^\\d{8}$", "yyyyMMdd");//20140312
	    dateRegFormat.put("^\\d{6}$", "yyyyMM");//201403
	    dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm-ss");//13:05:34 拼接当前日期
	    dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm");//13:05 拼接当前日期
	    dateRegFormat.put("^\\d{2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yy-MM-dd");//14.10.18(年.月.日)
	    dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}$", "yyyy-dd-MM");//30.12(日.月) 拼接当前年份
	    dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy");//12.21.2013(日.月.年)	    
	    dateRegFormat.put("^\\d{4}$\\D+\\d{1,2}\\D+\\d{1,2}", "yyyy-MM-dd");//2013.12.21  
	    dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy");//12.21.2013(日.月.年)	  
	    String curDate =new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	    DateFormat formatter1 =new SimpleDateFormat("yyyy-MM-dd");
	    DateFormat formatter2;
	    String dateReplace;
	    String strSuccess="";
	    try {
	      for (String key : dateRegFormat.keySet()) {
	        if (Pattern.compile(key).matcher(dateStr).matches()) {
	          formatter2 = new SimpleDateFormat(dateRegFormat.get(key));
	          if (key.equals("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$")
	              || key.equals("^\\d{2}\\s*:\\s*\\d{2}$")) {//13:05:34 或 13:05 拼接当前日期
	            dateStr = curDate + "-" + dateStr;
	          } else if (key.equals("^\\d{1,2}\\D+\\d{1,2}$")) {//21.1 (日.月) 拼接当前年份
	            dateStr = curDate.substring(0, 4) + "-" + dateStr;
	          }
	          dateReplace = dateStr.replaceAll("\\D+", "-");
	          strSuccess = formatter1.format(formatter2.parse(dateReplace));
	          break;
	        }
	      }
	    } catch (Exception e) {
	    } 
	    return strSuccess;
	}
	public static String FormatDate(String dateStr){
		  
	    HashMap<String, String> dateRegFormat = new HashMap<String, String>();
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", "yyyy-MM-dd-HH-mm-ss");//2014年3月12日 13时5分34秒，2014-03-12 12:05:34，2014/3/12 12:5:34
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D*$", "yyyy-MM");//2014年3月
	    dateRegFormat.put("^\\d{4}\\D*$", "yyyy");//2014年
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{2}\\D+\\d{2}$", "yyyy-MM-dd-HH-mm");//2014-03-12 12:05
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{2}$", "yyyy-MM-dd-HH");//2014-03-12 12
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}", "yyyy-MM-dd");//2014-03-12    
	    dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy/MM/dd");//2014/03/02 
	    dateRegFormat.put("^\\d{4}$", "yyyy");//2014年
	    dateRegFormat.put("^\\d{4}", "yyyy");//2014
	    dateRegFormat.put("^\\d{14}$", "yyyyMMddHHmmss");//20140312120534
	    dateRegFormat.put("^\\d{12}$", "yyyyMMddHHmm");//201403121205
	    dateRegFormat.put("^\\d{10}$", "yyyyMMddHH");//2014031212
	    dateRegFormat.put("^\\d{8}$", "yyyyMMdd");//20140312
	    dateRegFormat.put("^\\d{6}$", "yyyyMM");//201403
	    dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm-ss");//13:05:34 拼接当前日期
	    dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm");//13:05 拼接当前日期
	    dateRegFormat.put("^\\d{2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yy-MM-dd");//14.10.18(年.月.日)
	    dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}$", "yyyy-dd-MM");//30.12(日.月) 拼接当前年份
	    dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy");//12.21.2013(日.月.年)	    
	    dateRegFormat.put("^\\d{4}$\\D+\\d{1,2}\\D+\\d{1,2}", "yyyy-MM-dd");//2013.12.21  
	    dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy");//12.21.2013(日.月.年)	  
	    String curDate =new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	    DateFormat formatter1 =new SimpleDateFormat("yyyy-MM-dd");
	    DateFormat formatter2;
	    String dateReplace;
	    String strSuccess="error";
	    try {
	      for (String key : dateRegFormat.keySet()) {
	        if (Pattern.compile(key).matcher(dateStr).matches()) {
	          formatter2 = new SimpleDateFormat(dateRegFormat.get(key));
	          if (key.equals("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$")
	              || key.equals("^\\d{2}\\s*:\\s*\\d{2}$")) {//13:05:34 或 13:05 拼接当前日期
	            dateStr = curDate + "-" + dateStr;
	          } else if (key.equals("^\\d{1,2}\\D+\\d{1,2}$")) {//21.1 (日.月) 拼接当前年份
	            dateStr = curDate.substring(0, 4) + "-" + dateStr;
	          }
	          dateReplace = dateStr.replaceAll("\\D+", "-");
	          strSuccess = formatter1.format(formatter2.parse(dateReplace));
	          break;
	        }
	      }
	      return strSuccess;
	    } catch (Exception e) {
	      return "error";
	    } 
	  }
	

    
    /**
     * 保留两位小数，四舍五入的一个老土的方法
     * @param d
     * @return
     */
    public static double formatDouble1(double d) {
        return (double)Math.round(d*100)/100;
    }

    
    /**
     * The BigDecimal class provides operations for arithmetic, scale manipulation, rounding, comparison, hashing, and format conversion.
     * @param d
     * @return
     */
    public static double formatDouble2(double d) {
        // 旧方法，已经不再推荐使用
//        BigDecimal bg = new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP);

        
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);

        
        return bg.doubleValue();
    }

    /**
     * NumberFormat is the abstract base class for all number formats. 
     * This class provides the interface for formatting and parsing numbers.
     * @param d
     * @return
     */
    public static String formatDouble3(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        

        // 保留两位小数
        nf.setMaximumFractionDigits(2); 

        
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);

        
        return nf.format(d);
    }

    
    /**
     * 这个方法挺简单的。
     * DecimalFormat is a concrete subclass of NumberFormat that formats decimal numbers. 
     * @param d
     * @return
     */
    public static String formatDouble4(double d) {
        DecimalFormat df = new DecimalFormat("#.00");

        
        return df.format(d);
    }

    
    /**
     * 如果只是用于程序中的格式化数值然后输出，那么这个方法还是挺方便的。
     * 应该是这样使用：System.out.println(String.format("%.2f", d));
     * @param d
     * @return
     */
    public static String formatDouble5(double d) {
        return String.format("%.2f", d);
    }
//	  public static void main(String[] args) {
//    double d = 12345.67890;
//    
//    System.out.println(formatDouble1(d));
//    System.out.println(formatDouble2(d));
//    System.out.println(formatDouble3(d));
//    System.out.println(formatDouble4(d));
//    System.out.println(formatDouble5(d));
//	    String[] dateStrArray=new String[]{
//	        "2014-3-12 12:05:34",
//	        "2014-03-12 12:05",
//	        "2014-03-12 12",
//	        "2014-03-12",
//	        "2014-03",
//	        "2014/04",
//	        "20140312120534",
//	        "2014/03/12 12:05:34",
//	        "2014/3/12 12:5:34",
//	        "2014/3/12",
//	        "2014年3月12日 13时5分34秒",
//	        "2014年",
//	        "2018",
//	        "2012年8月",
//	        "201403121205",
//	        "1234567890",
//	        "20140312",
//	        "201403",
//	        "2000 13 33 13 13 13",
//	        "30.12.2013",
//	        "12.21.2013",
//	        "21.1",
//	        "13:05:34",
//	        "12:05",
//	        "14.1.8",
//	        "14.10.18",
//	        "2014.10.18",
//	        "2014.10",
//	        "www",
//	        "2014.1"
//	    };
//	    for(int i=0;i<dateStrArray.length;i++){
//	      System.out.println(dateStrArray[i] +"------------------------------".substring(1,30-dateStrArray[i].length())+ FormatDate(dateStrArray[i]));
//	    }
//	      
//	  }
}
