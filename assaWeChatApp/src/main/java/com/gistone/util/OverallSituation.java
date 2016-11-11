package com.gistone.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 存取缓存
 * @author chendong
 * @date 2016年8月3日
 */
public class OverallSituation {
	public static List<Object[]> x_compartment_xian;
	
	public static JSONArray compartment_json;
	
	public static JSONArray compartment = null;//所有行政区划
	
	public static JSONObject comp = null;//带层级关系的行政区划树
	
	public static String index = "index.html";//系统登录界面
	public static String page = ".html";//需要过滤的页面
	public static String servlet = ".do";//需要过滤的后台处理方法
}
