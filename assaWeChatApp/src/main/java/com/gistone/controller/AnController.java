package com.gistone.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gistone.MyBatis.config.GetBySqlMapper;
import com.gistone.util.Tool;
//import com.google.common.collect.ObjectArrays;

@RestController
@RequestMapping
public class AnController{
	
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	//定义地图中的全局参数
	String char_name;
	String char_type;
	String char_standard;
	
	String accessToken;//获取AccessToken 
	String jsapi_ticket;//2、获取Ticket
	/**
	 * 激活
	 * @author 太年轻
	 * @date 2016年9月6日
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getActivateController.do")
	public void getActivate_Controller(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String phone = request.getParameter("phone");//帮扶人电话
		String name = request.getParameter("name");//帮扶人姓名
		String password = request.getParameter("password");
		String cha_p_sql = "SELECT PKID FROM SYS_USER WHERE COL_ACCOUNT='"+phone+"' AND ACCOUNT_TYPE='2'";
		List<Map> cha_p_list = this.getBySqlMapper.findRecords(cha_p_sql);
		//判断账号是否存在，是否可以激活
		if(cha_p_list.size() > 0){
			response.getWriter().write("{\"result\":\"\",message:\"0\"}");//账户已激活
		}else{
			String cha_sql = "select pkid from sys_personal where col_name = '"+name+"' and telephone = '"+phone+"'";
			List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
			if(cha_list.size() > 0 && cha_list.get(0) != null){
				String sql = "insert into sys_user (col_account,col_password,account_type,account_state,sys_personal_id,sys_role_id) "+
						" values ('"+phone+"',md5('"+password+"'),'2','1','"+cha_list.get(0).get("pkid")+"','7')";//添加账号
				this.getBySqlMapper.insert(sql);
				response.getWriter().write("{\"result\":\"\",\"message\":\"1\"}");//激活成功
			}else{
				response.getWriter().write("{\"result\":\"\",\"message\":\"2\"}");//尚未检测到该账户，请联系当地扶贫办。
			}
		}
	}
	
	/**
	 * 安卓登录接口 
	 * @author 太年轻
	 * @date 2016年9月6日
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getAnLoginController.do")
	public void getAnLoginController(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String phone = request.getParameter("phone");//电话
		String password = request.getParameter("password");//密码
		String cha_sql = "SELECT PKID,SYS_PERSONAL_ID,ACCOUNT_STATE,COL_PASSWORD FROM SYS_USER WHERE COL_ACCOUNT='"+phone+"' AND ACCOUNT_TYPE='2'";
		List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
		JSONArray jsonArray =new JSONArray();
		if(cha_list.size()>0){
			Map Login_map = cha_list.get(0);
			HttpSession session = request.getSession();
			session.setAttribute("Login_map", Login_map);
			if(Login_map.get("ACCOUNT_STATE").toString().equals("1")){//状态正常
				if(Tool.md5(password).equals(Login_map.get("COL_PASSWORD"))==true){//密码正确
					String sid = "".equals(Login_map.get("SYS_PERSONAL_ID")) || Login_map.get("SYS_PERSONAL_ID") == null ?"" : Login_map.get("SYS_PERSONAL_ID").toString();
					String  pkid =  "".equals(Login_map.get("PKID")) || Login_map.get("PKID") == null ?"" : Login_map.get("PKID").toString();
					String bfr_sql = "SELECT COL_NAME FROM SYS_PERSONAL WHERE PKID="+sid;
					List<Map> bfr_list = this.getBySqlMapper.findRecords(bfr_sql);
					String bfr = "";
					if(bfr_list.size() > 0){
						bfr = bfr_list.get(0).get("COL_NAME").toString();
					}
					response.getWriter().write("{\"sid\":"+sid+",\"pkid\":"+pkid+",\"message\":\"5\",\"name\":\""+bfr+"\"}");
				}else{
					response.getWriter().write("{\"result\":\"\",\"message\":\"0\"}");//密码错误 
				}
			}
		}else{
			response.getWriter().write("{\"result\":\"\",\"message\":\"1\"}");//账号尚未激活
		}
	}
	/**
	 * 根据帮扶人查看相应的贫困户的详细信息
	 * @author 太年轻
	 * @date 2016年9月6日
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getSavePoorController.do")
	public void getSavePoorController (HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String  personal_id = request.getParameter("personal_id");//帮扶人的id
		
		String cha_sql = "SELECT T2.*,PIC_PATH FROM SYS_PERSONAL_HOUSEHOLD_MANY T1 LEFT JOIN DA_HOUSEHOLD T2 ON T1.DA_HOUSEHOLD_ID=T2.PKID LEFT JOIN "+
						"(SELECT PIC_PATH,PIC_PKID FROM DA_PIC WHERE PIC_TYPE='4' )T3 ON T2.PKID = T3.PIC_PKID WHERE T1.SYS_PERSONAL_ID ='"+personal_id+"'  AND T2.PKID IS NOT NULL";
		
   		List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
		
		JSONArray json = new JSONArray();
		
		if (cha_list.size() > 0){
			
			for(int i = 0; i < cha_list.size(); i ++){
				
				JSONObject obj = new JSONObject();
				obj.put("pkid", cha_list.get(i).get("PKID"));
				obj.put("v1", "".equals(cha_list.get(i).get("V1")) || cha_list.get(i).get("V1") == null ? "" : cha_list.get(i).get("V1").toString());//省（自治区、直辖市）
				obj.put("v2", "".equals(cha_list.get(i).get("v2")) || cha_list.get(i).get("V2") == null ? "" : cha_list.get(i).get("V2").toString());//	市（盟、州）
				obj.put("v3", "".equals(cha_list.get(i).get("V3")) || cha_list.get(i).get("V3") == null ? "" : cha_list.get(i).get("V3").toString());//	县(市、区、旗)
				obj.put("v4", "".equals(cha_list.get(i).get("V4")) || cha_list.get(i).get("V4") == null ? "" : cha_list.get(i).get("V4").toString());//镇(乡)
				obj.put("v5", "".equals(cha_list.get(i).get("V5")) || cha_list.get(i).get("V5") == null ? "" : cha_list.get(i).get("V5").toString());//	行政村
				obj.put("v6", "".equals(cha_list.get(i).get("V6")) || cha_list.get(i).get("V6") == null ? "" : cha_list.get(i).get("V6").toString());//	姓名
				obj.put("v7", "".equals(cha_list.get(i).get("V7")) || cha_list.get(i).get("V7") == null ? "" : cha_list.get(i).get("V7").toString());//	性别
				obj.put("v8", "".equals(cha_list.get(i).get("V8")) || cha_list.get(i).get("V8") == null ? "" : cha_list.get(i).get("V8").toString());//	证件号码
				obj.put("v9", "".equals(cha_list.get(i).get("V9")) || cha_list.get(i).get("V9") == null ? "" : cha_list.get(i).get("V9").toString());//人数
				obj.put("v10", "".equals(cha_list.get(i).get("V10")) || cha_list.get(i).get("V10") == null ? "" : cha_list.get(i).get("V10").toString());//与户主关系
				obj.put("v11", "".equals(cha_list.get(i).get("V11")) || cha_list.get(i).get("V11") == null ? "" : cha_list.get(i).get("V11").toString());//	民族
				obj.put("v12", "".equals(cha_list.get(i).get("V12")) || cha_list.get(i).get("V12") == null ? "" : cha_list.get(i).get("V12").toString());//	文化程度
				obj.put("v13", "".equals(cha_list.get(i).get("V13")) || cha_list.get(i).get("V13") == null ? "" : cha_list.get(i).get("V13").toString());//	在校生状况
				obj.put("v14", "".equals(cha_list.get(i).get("V14")) || cha_list.get(i).get("V14") == null ? "" : cha_list.get(i).get("V14").toString());//健康状况	
				obj.put("v15", "".equals(cha_list.get(i).get("V15")) || cha_list.get(i).get("V15") == null ? "" : cha_list.get(i).get("V15").toString());//劳动能力	
				obj.put("v16", "".equals(cha_list.get(i).get("V16")) || cha_list.get(i).get("V16") == null ? "" : cha_list.get(i).get("V16").toString());//务工状况	
				obj.put("v17", "".equals(cha_list.get(i).get("V17")) || cha_list.get(i).get("V17") == null ? "" : cha_list.get(i).get("V17").toString());//务工时间（月）	
				obj.put("v18", "".equals(cha_list.get(i).get("V18")) || cha_list.get(i).get("V18") == null ? "" : cha_list.get(i).get("V18").toString());//是否参加新农合	
				obj.put("v19", "".equals(cha_list.get(i).get("V19")) || cha_list.get(i).get("V19") == null ? "" : cha_list.get(i).get("V19").toString());//是否参加新型养老保险	
				obj.put("v20", "".equals(cha_list.get(i).get("V20")) || cha_list.get(i).get("V20") == null ? "" : cha_list.get(i).get("V20").toString());//是否参加城镇职工基本养老保险	
				obj.put("v21", "".equals(cha_list.get(i).get("V21")) || cha_list.get(i).get("V21") == null ? "" : cha_list.get(i).get("V21").toString());//脱贫属性	
				obj.put("v22", "".equals(cha_list.get(i).get("V22")) || cha_list.get(i).get("V22") == null ? "" : cha_list.get(i).get("V22").toString());//贫困户属性	
				obj.put("v23", "".equals(cha_list.get(i).get("V23")) || cha_list.get(i).get("V23") == null ? "" : cha_list.get(i).get("V23").toString());//主要致贫原因	
				obj.put("v24", "".equals(cha_list.get(i).get("V24")) || cha_list.get(i).get("V24") == null ? "" : cha_list.get(i).get("V24").toString());//人均纯收入	
				obj.put("v25", "".equals(cha_list.get(i).get("V25")) || cha_list.get(i).get("V25") == null ? "" : cha_list.get(i).get("V25").toString());//联系电话	
				obj.put("v26", "".equals(cha_list.get(i).get("V26")) || cha_list.get(i).get("V26") == null ? "" : cha_list.get(i).get("V26").toString());//开户银行名称	
				obj.put("v27", "".equals(cha_list.get(i).get("V27")) || cha_list.get(i).get("V27") == null ? "" : cha_list.get(i).get("V27").toString());//银行卡号	
				obj.put("v28", "".equals(cha_list.get(i).get("V28")) || cha_list.get(i).get("V28") == null ? "" : cha_list.get(i).get("V28").toString());//政治面貌	
				obj.put("v29", "".equals(cha_list.get(i).get("V29")) || cha_list.get(i).get("V29") == null ? "" : cha_list.get(i).get("V29").toString());//是否军烈属	
				obj.put("v30", "".equals(cha_list.get(i).get("V30")) || cha_list.get(i).get("V30") == null ? "" : cha_list.get(i).get("V30").toString());//是否独生子女户	
				obj.put("v31", "".equals(cha_list.get(i).get("V31")) || cha_list.get(i).get("V31") == null ? "" : cha_list.get(i).get("V31").toString());//是否双女户	
				obj.put("v32", "".equals(cha_list.get(i).get("V32")) || cha_list.get(i).get("V32") == null ? "" : cha_list.get(i).get("V32").toString());//是否现役军人	
				obj.put("v33", "".equals(cha_list.get(i).get("V33")) || cha_list.get(i).get("V33") == null ? "" : cha_list.get(i).get("V33").toString());//其他致贫原因	
				obj.put("v34", "".equals(cha_list.get(i).get("SYS_STANDARD")) || cha_list.get(i).get("SYS_STANDARD") == null ? "" : cha_list.get(i).get("SYS_STANDARD").toString());//识别标准 国家标准 市级标准	
				obj.put("pic_path", "".equals(cha_list.get(i).get("PIC_PATH")) || cha_list.get(i).get("PIC_PATH") == null ? "" : cha_list.get(i).get("PIC_PATH").toString());//户主头像
				
				json.add(obj);
			}
			response.getWriter().write("{\"result\":"+json.toString()+",\"message\":\"5\"}");//没有帮扶的贫困户
		}else{
			response.getWriter().write("{\"result\":"+json.toString()+",\"message\":\"0\"}");//没有帮扶的贫困户
		}
	}
	
	
	/**
	 * 查看家庭成员
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getSaveFamily.do")
	public void getSaveFamily(HttpServletRequest request,HttpServletResponse response) throws IOException{ 
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String pkid = request.getParameter("pkid");//贫困户id
		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject obj = new JSONObject ();
			String hz_pic = "SELECT PKID,V6,V7,V8,V10,V11,V12,V13,V14,V15,V16,V17,V19,V28,V32,PIC_PATH FROM  DA_HOUSEHOLD A  LEFT JOIN " +
					" (SELECT PIC_PATH,PIC_PKID FROM DA_PIC WHERE PIC_TYPE='4')B ON A.PKID=B.PIC_PKID WHERE A.PKID='"+pkid+"'";
			List<Map> hz_list = this.getBySqlMapper.findRecords(hz_pic);
			
			obj.put("v6", "".equals(hz_list.get(0).get("V6")) || hz_list.get(0).get("V6")==null ? "" : hz_list.get(0).get("V6").toString());//姓名
			obj.put("v7", "".equals(hz_list.get(0).get("V7")) || hz_list.get(0).get("V7")==null ? "" : hz_list.get(0).get("V7").toString());//性别
			obj.put("v8", "".equals(hz_list.get(0).get("V8")) || hz_list.get(0).get("V8")==null ? "" : hz_list.get(0).get("V8").toString());//证件号码
			obj.put("v10", "".equals(hz_list.get(0).get("V10")) || hz_list.get(0).get("V10")==null ? "" : hz_list.get(0).get("V10").toString());//与户主的关系
			obj.put("v11", "".equals(hz_list.get(0).get("V11")) || hz_list.get(0).get("V11")==null ? "" : hz_list.get(0).get("V11").toString());//民族
			obj.put("v28", "".equals(hz_list.get(0).get("V28")) || hz_list.get(0).get("V28")==null ? "" : hz_list.get(0).get("V28").toString());//政治面貌
			obj.put("v12", "".equals(hz_list.get(0).get("V12")) || hz_list.get(0).get("V12")==null ? "" : hz_list.get(0).get("V12").toString());//文化程度
			obj.put("v13", "".equals(hz_list.get(0).get("V13")) || hz_list.get(0).get("V13")==null ? "" : hz_list.get(0).get("V13").toString());//在校生情况
			obj.put("v14", "".equals(hz_list.get(0).get("V14")) || hz_list.get(0).get("V14")==null ? "" : hz_list.get(0).get("V14").toString());//民族
			obj.put("v15", "".equals(hz_list.get(0).get("V15")) || hz_list.get(0).get("V15")==null ? "" : hz_list.get(0).get("V15").toString());//健康状态
			obj.put("v16", "".equals(hz_list.get(0).get("V16")) || hz_list.get(0).get("V16")==null ? "" : hz_list.get(0).get("V16").toString());//务工情况
			obj.put("v17", "".equals(hz_list.get(0).get("V17")) || hz_list.get(0).get("V17")==null ? "" : hz_list.get(0).get("V17").toString());//务工时间
			obj.put("v32", "".equals(hz_list.get(0).get("V32")) || hz_list.get(0).get("V32")==null ? "" : hz_list.get(0).get("V32").toString());//是否现役军人
			obj.put("v19", "".equals(hz_list.get(0).get("V19")) || hz_list.get(0).get("V19")==null ? "" : hz_list.get(0).get("V19").toString());//是否参加大病医疗保险
			obj.put("pic_path", "".equals(hz_list.get(0).get("PIC_PATH")) || hz_list.get(0).get("PIC_PATH")==null ? "" : hz_list.get(0).get("PIC_PATH").toString());//是否现役军人
			obj.put("pid", hz_list.get(0).get("PKID"));
			jsonArray.add(obj);
			//取家家庭成员信息
			String sql="SELECT pkid,v6,v7,v8,v10,v11,v12,v13,v14,v15,v16,v17,v19,v28,v32,pic_path from  da_member a  LEFT JOIN " +
					" (SELECT pic_path,pic_pkid FROM da_pic where pic_type='5')b ON a.pkid=b.pic_pkid where a.da_household_id='"+pkid+"'";
			List<Map> list = getBySqlMapper.findRecords(sql);
			for(int i=0;i<list.size();i++){
				obj.put("v6", "".equals(list.get(i).get("V6")) || list.get(i).get("V6")==null ? "" : list.get(i).get("V6").toString());//姓名
				obj.put("v7", "".equals(list.get(i).get("V7")) || list.get(i).get("V7")==null ? "" : list.get(i).get("V7").toString());//性别
				obj.put("v8", "".equals(list.get(i).get("V8")) || list.get(i).get("V8")==null ? "" : list.get(i).get("V8").toString());//证件号码
				obj.put("v10", "".equals(list.get(i).get("V10")) || list.get(i).get("V10")==null ? "" : list.get(i).get("V10").toString());//与户主的关系
				obj.put("v11", "".equals(list.get(i).get("V11")) || list.get(i).get("V11")==null ? "" : list.get(i).get("V11").toString());//民族
				obj.put("v28", "".equals(list.get(i).get("V28")) || list.get(i).get("V28")==null ? "" : list.get(i).get("V28").toString());//政治面貌
				obj.put("v12", "".equals(list.get(i).get("V12")) || list.get(i).get("V12")==null ? "" : list.get(i).get("V12").toString());//文化程度
				obj.put("v13", "".equals(list.get(i).get("V13")) || list.get(i).get("V13")==null ? "" : list.get(i).get("V13").toString());//在校生情况
				obj.put("v14", "".equals(list.get(i).get("V14")) || list.get(i).get("V14")==null ? "" : list.get(i).get("V14").toString());//民族
				obj.put("v15", "".equals(list.get(i).get("V15")) || list.get(i).get("V15")==null ? "" : list.get(i).get("V15").toString());//健康状态
				obj.put("v16", "".equals(list.get(i).get("V16")) || list.get(i).get("V16")==null ? "" : list.get(i).get("V16").toString());//务工情况
				obj.put("v17", "".equals(list.get(i).get("V17")) || list.get(i).get("V17")==null ? "" : list.get(i).get("V17").toString());//务工时间
				obj.put("v32", "".equals(list.get(i).get("V32")) || list.get(i).get("V32")==null ? "" : list.get(i).get("V32").toString());//是否现役军人
				obj.put("v19", "".equals(list.get(i).get("V19")) || list.get(i).get("V19")==null ? "" : list.get(i).get("V19").toString());//是否参加大病医疗保险
				obj.put("pic_path", "".equals(list.get(i).get("PIC_PATH")) || list.get(i).get("PIC_PATH")==null ? "" : list.get(i).get("PIC_PATH").toString());//是否现役军人
				obj.put("pid", list.get(i).get("PKID"));
				jsonArray.add(obj);
			}
			response.getWriter().write("{\"result\":"+jsonArray.toString()+",\"message\":\"5\"}");
			
			} catch (Exception e) {
				response.getWriter().write("{\"result\":\"\",\"message\":\"0\"}");
			}
	}
	/**
	 * 查询贫困户信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("getCha_huController.do")
	public void getCha_huController (HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String name = request.getParameter("name");
		String pkid = request.getParameter("pkid");
		
		String sql = "SELECT A.*,PIC_PATH FROM  DA_HOUSEHOLD A  LEFT JOIN " +
				" (SELECT PIC_PATH,PIC_PKID FROM DA_PIC WHERE PIC_TYPE='4')B ON A.PKID=B.PIC_PKID WHERE ";
		if("".equals(name) || name == null){
			sql +="  A.PKID='"+pkid+"'";
		}else{
			sql +="  A.V6='"+name+"'";
		}
		List<Map> cha_list = this.getBySqlMapper.findRecords(sql);
		JSONArray json = new JSONArray();
		if(cha_list.size()>0 && cha_list.get(0)!=null){
			for(int i = 0; i < cha_list.size(); i ++){
				
				JSONObject obj = new JSONObject();
				obj.put("pkid", cha_list.get(i).get("PKID"));
				obj.put("v1", "".equals(cha_list.get(i).get("V1")) || cha_list.get(i).get("V1") == null ? "" : cha_list.get(i).get("V1").toString());//省（自治区、直辖市）
				obj.put("v2", "".equals(cha_list.get(i).get("v2")) || cha_list.get(i).get("V2") == null ? "" : cha_list.get(i).get("V2").toString());//	市（盟、州）
				obj.put("v3", "".equals(cha_list.get(i).get("V3")) || cha_list.get(i).get("V3") == null ? "" : cha_list.get(i).get("V3").toString());//	县(市、区、旗)
				obj.put("v4", "".equals(cha_list.get(i).get("V4")) || cha_list.get(i).get("V4") == null ? "" : cha_list.get(i).get("V4").toString());//镇(乡)
				obj.put("v5", "".equals(cha_list.get(i).get("V5")) || cha_list.get(i).get("V5") == null ? "" : cha_list.get(i).get("V5").toString());//	行政村
				obj.put("v6", "".equals(cha_list.get(i).get("V6")) || cha_list.get(i).get("V6") == null ? "" : cha_list.get(i).get("V6").toString());//	姓名
				obj.put("v7", "".equals(cha_list.get(i).get("V7")) || cha_list.get(i).get("V7") == null ? "" : cha_list.get(i).get("V7").toString());//	性别
				obj.put("v8", "".equals(cha_list.get(i).get("V8")) || cha_list.get(i).get("V8") == null ? "" : cha_list.get(i).get("V8").toString());//	证件号码
				obj.put("v9", "".equals(cha_list.get(i).get("V9")) || cha_list.get(i).get("V9") == null ? "" : cha_list.get(i).get("V9").toString());//人数
				obj.put("v10", "".equals(cha_list.get(i).get("V10")) || cha_list.get(i).get("V10") == null ? "" : cha_list.get(i).get("V10").toString());//与户主关系
				obj.put("v11", "".equals(cha_list.get(i).get("V11")) || cha_list.get(i).get("V11") == null ? "" : cha_list.get(i).get("V11").toString());//	民族
				obj.put("v12", "".equals(cha_list.get(i).get("V12")) || cha_list.get(i).get("V12") == null ? "" : cha_list.get(i).get("V12").toString());//	文化程度
				obj.put("v13", "".equals(cha_list.get(i).get("V13")) || cha_list.get(i).get("V13") == null ? "" : cha_list.get(i).get("V13").toString());//	在校生状况
				obj.put("v14", "".equals(cha_list.get(i).get("V14")) || cha_list.get(i).get("V14") == null ? "" : cha_list.get(i).get("V14").toString());//健康状况	
				obj.put("v15", "".equals(cha_list.get(i).get("V15")) || cha_list.get(i).get("V15") == null ? "" : cha_list.get(i).get("V15").toString());//劳动能力	
				obj.put("v16", "".equals(cha_list.get(i).get("V16")) || cha_list.get(i).get("V16") == null ? "" : cha_list.get(i).get("V16").toString());//务工状况	
				obj.put("v17", "".equals(cha_list.get(i).get("V17")) || cha_list.get(i).get("V17") == null ? "" : cha_list.get(i).get("V17").toString());//务工时间（月）	
				obj.put("v18", "".equals(cha_list.get(i).get("V18")) || cha_list.get(i).get("V18") == null ? "" : cha_list.get(i).get("V18").toString());//是否参加新农合	
				obj.put("v19", "".equals(cha_list.get(i).get("V19")) || cha_list.get(i).get("V19") == null ? "" : cha_list.get(i).get("V19").toString());//是否参加新型养老保险	
				obj.put("v20", "".equals(cha_list.get(i).get("V20")) || cha_list.get(i).get("V20") == null ? "" : cha_list.get(i).get("V20").toString());//是否参加城镇职工基本养老保险	
				obj.put("v21", "".equals(cha_list.get(i).get("V21")) || cha_list.get(i).get("V21") == null ? "" : cha_list.get(i).get("V21").toString());//脱贫属性	
				obj.put("v22", "".equals(cha_list.get(i).get("V22")) || cha_list.get(i).get("V22") == null ? "" : cha_list.get(i).get("V22").toString());//贫困户属性	
				obj.put("v23", "".equals(cha_list.get(i).get("V23")) || cha_list.get(i).get("V23") == null ? "" : cha_list.get(i).get("V23").toString());//主要致贫原因	
				obj.put("v24", "".equals(cha_list.get(i).get("V24")) || cha_list.get(i).get("V24") == null ? "" : cha_list.get(i).get("V24").toString());//人均纯收入	
				obj.put("v25", "".equals(cha_list.get(i).get("V25")) || cha_list.get(i).get("V25") == null ? "" : cha_list.get(i).get("V25").toString());//联系电话	
				obj.put("v26", "".equals(cha_list.get(i).get("V26")) || cha_list.get(i).get("V26") == null ? "" : cha_list.get(i).get("V26").toString());//开户银行名称	
				obj.put("v27", "".equals(cha_list.get(i).get("V27")) || cha_list.get(i).get("V27") == null ? "" : cha_list.get(i).get("V27").toString());//银行卡号	
				obj.put("v28", "".equals(cha_list.get(i).get("V28")) || cha_list.get(i).get("V28") == null ? "" : cha_list.get(i).get("V28").toString());//政治面貌	
				obj.put("v29", "".equals(cha_list.get(i).get("V29")) || cha_list.get(i).get("V29") == null ? "" : cha_list.get(i).get("V29").toString());//是否军烈属	
				obj.put("v30", "".equals(cha_list.get(i).get("V30")) || cha_list.get(i).get("V30") == null ? "" : cha_list.get(i).get("V30").toString());//是否独生子女户	
				obj.put("v31", "".equals(cha_list.get(i).get("V31")) || cha_list.get(i).get("V31") == null ? "" : cha_list.get(i).get("V31").toString());//是否双女户	
				obj.put("v32", "".equals(cha_list.get(i).get("V32")) || cha_list.get(i).get("V32") == null ? "" : cha_list.get(i).get("V32").toString());//是否现役军人	
				obj.put("v33", "".equals(cha_list.get(i).get("V33")) || cha_list.get(i).get("V33") == null ? "" : cha_list.get(i).get("V33").toString());//其他致贫原因	
				obj.put("v34", "".equals(cha_list.get(i).get("SYS_STANDARD")) || cha_list.get(i).get("SYS_STANDARD") == null ? "" : cha_list.get(i).get("SYS_STANDARD").toString());//识别标准 国家标准 市级标准	
				obj.put("pic_path", "".equals(cha_list.get(i).get("PIC_PATH")) || cha_list.get(i).get("PIC_PATH") == null ? "" : cha_list.get(i).get("PIC_PATH").toString());//户主头像
				
				json.add(obj);
			}
			response.getWriter().write("{\"result\":"+json.toString()+",\"message\":\"5\"}");
		}else{
			response.getWriter().write("{\"result\":"+json.toString()+",\"message\":\"0\"}");//没有该用户
		}
		
	}
	
	/**
	 * 查看走访记录情况
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getSaveVisit.do")
	public void getSaveVisit(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String poor_id = request.getParameter("pid");//贫困户id
		String persion_id = request.getParameter("persion_id");//帮扶人的id
		JSONArray jsonArray = new JSONArray();
		String sys_str = "";
		try {
			String sql="SELECT DA_HOUSEHOLD_ID,V1,V2,to_char(V3) V3,ADDRESS,wmsys.wm_concat(to_char(PIC_PATH)) PIC_PATH,PKID FROM DA_HELP_VISIT A LEFT JOIN ("+
					"SELECT PIC_PATH,PIC_PKID FROM DA_PIC WHERE PIC_TYPE='2') B ON A.PKID=B.PIC_PKID  WHERE ";
			if("".equals(poor_id) || poor_id == null){
				sql += " SYS_PERSONAL_ID ='"+persion_id+"'";
				sys_str += "where  sys_personal_id='"+persion_id+"'";
			}else{
				sql += " DA_HOUSEHOLD_ID ='"+poor_id+"'";
				sys_str += " where  DA_HOUSEHOLD_ID='"+poor_id+"'";
			}
			sql += " GROUP BY PKID, DA_HOUSEHOLD_ID,V1,V2,to_char(V3),ADDRESS ORDER BY V1 DESC";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		for(Map val:list){
			JSONObject obj = new JSONObject ();
			obj.put("a",val.get("PKID")==null?"-":val.get("PKID"));
			obj.put("b", "".equals(val.get("V1")) || val.get("V1") == null ? "" : val.get("V1").toString());//走访时间
			obj.put("c","".equals(val.get("V3")) || val.get("V3") == null ? "" : val.get("V3").toString());//走访情况记录
			obj.put("d","".equals(val.get("PIC_PATH")) || val.get("PIC_PATH") == null ? "" : val.get("PIC_PATH").toString());//走访情况图片
			obj.put("f", "".equals(val.get("ADDRESS")) || val.get("ADDRESS") == null ? "" : val.get("ADDRESS").toString());//地址
			String cha_sql = "SELECT V6 FROM DA_HOUSEHOLD WHERE PKID ="+val.get("DA_HOUSEHOLD_ID");
			String per_sql = "select COL_NAME from SYS_PERSONAL a LEFT JOIN (select sys_personal_id,DA_HOUSEHOLD_ID "+
							" from SYS_PERSONAL_HOUSEHOLD_MANY)b ON a.pkid = B.sys_personal_id "+sys_str;
			List<Map> per_list = this.getBySqlMapper.findRecords(per_sql);
			if(per_list.size()>0){
				obj.put("e", per_list.get(0).get("COL_NAME"));//帮扶干部名称
			}else{
				obj.put("e", "");//帮扶干部名称
			}
			List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
			if(cha_list.size() > 0){
				obj.put("v6", cha_list.get(0).get("V6"));//户主姓名
			}else{
				obj.put("v6", "");
			}
			jsonArray.add(obj);
		}
		response.getWriter().write("{\"message\":\"5\",\"result\":"+jsonArray.toString()+"}");
		response.getWriter().close();
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"0\",\"result\":"+jsonArray.toString()+"}");
			response.getWriter().close();
		}
	}
	/**
	 * 帮扶人结对
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("getBfrjd.do")
	public void getBfrjd(HttpServletRequest request , HttpServletResponse response) throws IOException {
		String  pkid = request.getParameter("da_household_id");//贫苦户id
		String  persion_id = request.getParameter("persion_id");//帮扶人的id
		String  sql = "INSERT INTO SYS_PERSONAL_HOUSEHOLD_MANY(SYS_PERSONAL_ID,DA_HOUSEHOLD_ID) VALUES ('"+persion_id+"','"+pkid+"')";
		int a = this.getBySqlMapper.insert(sql);
		if(a != 0){
			response.getWriter().write("{\"message\":\"5\"}");//结对成功
		}else{
			response.getWriter().write("{\"message\":\"0\"}");//结对失败
		}
		
	}
	
	/**
	 * 添加走访情况
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getAddVisitController.do")
	public void getAddVisitController(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String da_household_id=request.getParameter("pid");//贫困户id
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
		String v=simpleDate.format(new Date());
		String v2=request.getParameter("name");//帮扶干部
		String v3=request.getParameter("record");//走访情况记录-
		String phone=request.getParameter("phone");//电话
		String  persion_id = request.getParameter("sid");//帮扶人id
		String lng = request.getParameter("lng");//经度
		String lat = request.getParameter("lat");//维度
		String address = request.getParameter("address");//地点
		try {

			String hql="INSERT INTO DA_HELP_VISIT(DA_HOUSEHOLD_ID,SYS_PERSONAL_ID,V1,V2,V3,LNG,LAT,ADDRESS)"+
					" VALUES('"+da_household_id+"','"+persion_id+"','"+simpleDate.format(new Date())+"','"+v2+"','"+v3+"','"+lng+"','"+lat+"','"+address+"')";
			JSONArray jsonArray =new JSONArray();
			
			//insert_num:添加成功的数据返回值
			int insert_num = this.getBySqlMapper.insert(hql);
			
			String cha_sql="SELECT MAX(PKID) PKID FROM DA_HELP_VISIT WHERE V1='"+v+"' AND V3= '"+v3+"' AND V2='"+v2+"' AND DA_HOUSEHOLD_ID="+da_household_id;
			List<Map> list = this.getBySqlMapper.findRecords(cha_sql);
			String main = "" ;
			if(list.size()>0){
				main=list.get(0).get("PKID").toString();    
				Map limap = list.get(0);
					//添加记录表
					String hql1="INSERT INTO DA_RECORD(RECORD_TABLE,RECORD_PKID,RECORD_TYPE,RECORD_P_T,RECORD_PHONE,RECORD_TIME,RECORD_MOU_1,RECORD_MOU_2)"+
							" VALUES ('DA_HOUSEHOLD','"+da_household_id+"','添加','1','"+phone+"','"+v+"','基本信息','走访记录')";
					int insert_num2 = this.getBySqlMapper.insert(hql1);
			}
			//添加走访记录图片
			response.getWriter().write("{\"message\":\"0\",\"result\":"+main+"}");
			response.getWriter().close();
		
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"1\",\"result\":\"\"}");
			response.getWriter().close();
		}
	}
	/**
	 * 根据帮扶人查相应的贫困户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getPoorName.do")
	public void getPoorName(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String sid = request.getParameter("sid");
		JSONArray json = new JSONArray();
		String  sql = "SELECT pkid,v6 FROM DA_HOUSEHOLD WHERE PKID IN(select DA_HOUSEHOLD_ID from SYS_PERSONAL_HOUSEHOLD_MANY where SYS_PERSONAL_ID = "+sid+")";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		if(list.size()>0){
			for ( int i = 0 ; i < list.size() ; i ++){
				JSONObject obj = new JSONObject();
				obj.put("pkid",list.get(i).get("PKID").toString());
				obj.put("v6",list.get(i).get("V6").toString());
				json.add(obj);
			}
			response.getWriter().write(json.toString());
		}else{
			response.getWriter().write("0");
		}
	}
	/**
	 * 上传走访记录图片
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getAddZfPhoto.do")
	public void getAddZfPhoto(@RequestParam("image") MultipartFile file,HttpServletRequest request,HttpServletResponse response){
		String pkid = request.getParameter("mid");
		String img1=request.getParameter("imgname");//图片的名称
		String img =  img1.replaceAll("/", "");
		String size=request.getParameter("length");//图片大小
		if (!file.isEmpty()) {
			// 文件保存目录路径 
	        String savePath = request.getServletContext().getRealPath("/")+ "attached/2/";
	        // 文件保存目录URL  
	        String saveUrl = request.getContextPath() + "/attached/2/";
	        
	        // 定义允许上传的文件扩展名  
	        HashMap<String, String> extMap = new HashMap<String, String>();  
	        extMap.put("image", "gif,jpg,jpeg,png,bmp");
	        extMap.put("excel", "xls");
	        String dirName = "image";
	        // 检查目录  
            File uploadDir = new File(savePath);  
            if (!uploadDir.isDirectory()) {  
            	if(!uploadDir.exists()){
            		uploadDir.mkdirs();
            	}
            }
            //创建文件夹 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
            String ymd = sdf.format(new Date());  
            savePath += ymd + "\\";  
            saveUrl += ymd + "/";
            File dirFile = new File(savePath);  
            if (!dirFile.exists()) {  
                dirFile.mkdirs();  
            }
            
            // 检查扩展名 
            String filename_hout = file.getOriginalFilename();
            String fileExt = filename_hout.substring(filename_hout.lastIndexOf(".") + 1).toLowerCase(); 
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");  
//            String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt; 
        
            try {
            	 String sql="INSERT INTO DA_PIC(PIC_TYPE,PIC_PKID,PIC_PATH,PIC_SIZE,PIC_FORMAT) VALUES"+
     					"('2','"+pkid+"','"+saveUrl+img+".jpg"+"','"+size+"','jpg')";
     			int insert_num1 = this.getBySqlMapper.insert(sql);
            	File uploadedFile = new File(savePath, img+"."+fileExt);
                byte[] bytes = file.getBytes();  
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));  
                stream.write(bytes);  
                stream.close();
                response.getWriter().write(img);
            } catch (Exception e) {  
                return ;
            }
        } else {  
        	return ;
        }
	}
	/**
	 * 检测app版本是否进行更新
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getSaveVersionx.do")
	public void getSaveVersionx(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String v=request.getParameter("v");//版本
		String ipad_v=request.getParameter("version");
		String sql="";
		if("".equals(v)||v==null){
			sql="SELECT * FROM APP_VERSION WHERE PKID=2";
			List<Map> list=this.getBySqlMapper.findRecords(sql);
			if(list.size()>0){
				String version=(String) list.get(0).get("VERSION");
				if(ipad_v.toString().equals(version)){
					response.getWriter().write("");
				}else{
					response.getWriter().write( list.get(0).get("APP_PATH").toString());
				}
			}
		}else{
			sql="SELECT * FROM APP_VERSION WHERE PKID=1";
			List<Map> list=this.getBySqlMapper.findRecords(sql);
			if(list.size()>0){
				String version=(String) list.get(0).get("VERSION");
				if(v.toString().equals(version)){
					response.getWriter().write("{\"isError\":\"0\",\"result\":\"n\"}");
				}else{
					String url=(String) list.get(0).get("APP_PATH");
					response.getWriter().write("{\"isError\":\"0\",\"result\":\""+url.substring(6)+"\"}");
				}
			}
		}
	}
	
	/**
	 * 上传用户头像
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="getAddhuzhuPhoto.do", method=RequestMethod.POST)
	@ResponseBody
	public void getAddhuzhuPhoto(@RequestParam("image") MultipartFile file,HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String da_household_id=request.getParameter("pid");
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
		String v1=simpleDate.format(new Date());
		String img1=request.getParameter("img");//图片名称
		String img = img1.replaceAll("/", "");
		String size=request.getParameter("size");//图片的大小
		String type=request.getParameter("type");
		SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd"); 
		try {
			if(type.toString().equals("4")){//户主  	
				String cha_sql="SELECT * FROM DA_PIC WHERE PIC_PKID="+da_household_id+" AND PIC_TYPE='4'";
				List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
				String Url = "/assa/attached/4/"+dfs.format(new Date())+"/";
				if(null==cha_list||cha_list.size()==0){
					String sql="INSERT INTO DA_PIC (PIC_TYPE,PIC_PKID,PIC_PATH,PIC_SIZE,PIC_FORMAT) VALUES ('4','"+da_household_id+"','"+Url+img+".jpg','"+size+"','jpg')";
					this.getBySqlMapper.insert(sql);
				}else{
					String sql="UPDATE DA_PIC SET PIC_TYPE='4', PIC_PATH='"+Url+img+".jpg',PIC_SIZE='"+size+"', PIC_FORMAT='jpg'  WHERE PIC_PKID="+da_household_id;
					this.getBySqlMapper.update(sql);
				}
			}if(type.toString().equals("5")){//家庭成员
				String cha_sql="SELECT * FROM DA_PIC WHERE PIC_PKID="+da_household_id+" and PIC_TYPE='5'";
				List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
				String Url = "/assa/attached/5/"+dfs.format(new Date())+"/";
				if(null==cha_list||cha_list.size()==0){
					String sql="INSERT INTO DA_PIC (PIC_TYPE,PIC_PKID,PIC_PATH,PIC_SIZE,PIC_FORMAT) VALUES ('5','"+da_household_id+"','"+Url+img+".jpg','"+size+"','jpg')";
					this.getBySqlMapper.insert(sql);
				}else{
					String sql="UPDATE DA_PIC SET PIC_TYPE='5', PIC_PATH='"+Url+img+".jpg',PIC_SIZE='"+size+"', PIC_FORMAT='jpg'  WHERE PIC_PKID="+da_household_id;
					this.getBySqlMapper.update(sql);
				}
			}else{
				
			}
			response.getWriter().write("{\"isError\":\"0\",\"result\":\"\"}");
		} catch (Exception e) {
			response.getWriter().write("{\"isError\":\"1\",\"result\":\"\"}");
		}
		if (!file.isEmpty()) {
			// 文件保存目录路径  
	        String savePath = request.getServletContext().getRealPath("/")+ "attached/"+type+"/";
	        // 文件保存目录URL  
	        String saveUrl = request.getContextPath() + "/attached/"+type+"/";
	        
	        // 定义允许上传的文件扩展名  
	        HashMap<String, String> extMap = new HashMap<String, String>();  
	        extMap.put("image", "gif,jpg,jpeg,png,bmp");
	        extMap.put("excel", "xls");
	        String dirName = "image";
	        // 最大文件大小  
            long maxSize = 1000000; 
            
	        // 检查目录  
            File uploadDir = new File(savePath);  
            if (!uploadDir.isDirectory()) {  
            	//response.getWriter().write(getError("上传目录不存在。"));
            	if(!uploadDir.exists()){
            		uploadDir.mkdirs();
            	}
               // return null;
            }
            
            // 检查目录写权限  
            if (!uploadDir.canWrite()) {}
            
            //创建文件夹
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
            String ymd = sdf.format(new Date());  
            savePath += ymd + "\\";  
            saveUrl += ymd + "/";
            File dirFile = new File(savePath);  
            if (!dirFile.exists()) {  
                dirFile.mkdirs();  
            }
            
            // 检查扩展名 
            String filename_hout = file.getOriginalFilename();
            String fileExt = filename_hout.substring(filename_hout.lastIndexOf(".") + 1).toLowerCase(); 
            
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");  
            String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt; 
        
            try {
            	File uploadedFile = new File(savePath, img + "."+ fileExt);
                byte[] bytes = file.getBytes();  
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));  
                stream.write(bytes);  
                stream.close();
                
            } catch (Exception e) {  
//            	response.getWriter().write(getMessage("1","上传文件失败。",""));  
                return ;
            }
        } else {  
//        	response.getWriter().write(getMessage("1","请选择文件。",""));
        	return ;
        }
	}
	/**
	 * 数据统计_户详情指标
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getHuMessageController.do")
	public void getHuMessageController(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String str=request.getParameter("str");
		String sql="SELECT * FROM DA_HOUSEHOLD A LEFT JOIN (SELECT PIC_PKID,PIC_PATH FROM DA_PIC WHERE PIC_TYPE='4') B ON A.PKID=B.PIC_PKID WHERE V6='"+str+"' OR V8='"+str+"' ";
		List<Map> list=this.getBySqlMapper.findRecords(sql);
		JSONArray jsonArray=new JSONArray();
		JSONArray jsonArray2=new JSONArray();
		JSONArray jsonArray3=new JSONArray();
		JSONArray jsonArray4=new JSONArray();
		JSONArray jsonArray5=new JSONArray();
		JSONArray jsonArray6=new JSONArray();
		JSONArray jsonArray7=new JSONArray();
		
		JSONObject obj=new JSONObject();
		JSONObject obj2=new JSONObject();
		JSONObject obj3=new JSONObject();
		JSONObject obj4=new JSONObject();
		JSONObject obj5=new JSONObject();
		JSONObject obj6=new JSONObject();
		JSONObject obj7=new JSONObject();
		if(list.size()>0){
			for(Map val:list){
				obj.put("v3",val.get("V3")==null?"":val.get("V3"));
				obj.put("v4",val.get("V4")==null?"":val.get("V4"));
				obj.put("v5",val.get("V5")==null?"":val.get("V5"));
				obj.put("v6","".equals(val.get("V6"))||val.get("V6")==null?"":val.get("V6"));
				obj.put("v7","".equals(val.get("V7"))||val.get("V7")==null?"":val.get("V7"));
				obj.put("v8","".equals(val.get("V8"))||val.get("V8")==null?"":val.get("V8"));
				obj.put("v9","".equals(val.get("V9"))||val.get("V9")==null?"":val.get("V9"));
				obj.put("v10","".equals(val.get("V10"))||val.get("V10")==null?"户主":val.get("V10"));
				obj.put("v11","".equals(val.get("V11"))||val.get("V11")==null?"":val.get("V11"));
				obj.put("v12","".equals(val.get("V12"))||val.get("V12")==null?"":val.get("V12"));
				obj.put("v13","".equals(val.get("V13"))||val.get("V13")==null?"":val.get("V13"));
				obj.put("v14","".equals(val.get("V14"))||val.get("V14")==null?"":val.get("V14"));
				obj.put("v15","".equals(val.get("v15"))||val.get("V15")==null?"":val.get("V15"));
				obj.put("v16","".equals(val.get("V16"))||val.get("V16")==null?"":val.get("V16"));
				obj.put("v17","".equals(val.get("V17"))||val.get("V17")==null?"":val.get("V17"));
				obj.put("v18","".equals(val.get("V18"))||val.get("V18")==null?"":val.get("V18"));
				obj.put("v19","".equals(val.get("V19"))||val.get("V19")==null?"":val.get("V19"));
				obj.put("v20","".equals(val.get("V20"))||val.get("V20")==null?"":val.get("V20"));
				obj.put("v21","".equals(val.get("V21"))||val.get("V21")==null?"":val.get("V21"));
				obj.put("v22","".equals(val.get("V22"))||val.get("V22")==null?"":val.get("V22"));
				obj.put("v23","".equals(val.get("V23"))||val.get("V23")==null?"":val.get("V23"));
				obj.put("v25","".equals(val.get("V25"))||val.get("V25")==null?"":val.get("V25"));
				obj.put("v28","".equals(val.get("V28"))||val.get("V28")==null?"":val.get("V28"));
				obj.put("sys_standard","".equals(val.get("SYS_STANDARD"))||val.get("SYS_STANDARD")==null?"":val.get("SYS_STANDARD"));
				obj.put("v29","".equals(val.get("V29"))||val.get("V29")==null?"":val.get("V29"));
				obj.put("v30","".equals(val.get("V30"))||val.get("V30")==null?"":val.get("V30"));
				obj.put("v31","".equals(val.get("V31"))||val.get("V31")==null?"":val.get("V31"));
				obj.put("v32","".equals(val.get("V32"))||val.get("V32")==null?"":val.get("V32"));
				obj.put("v33","".equals(val.get("V33"))||val.get("V33")==null?"":val.get("V33"));
				obj.put("pic_path","".equals(val.get("PIC_PATH"))||val.get("PIC_PATH")==null?"":val.get("PIC_PATH"));
			}
			jsonArray.add(obj);
			//家庭成员
			String sql2="SELECT * FROM DA_MEMBER A LEFT JOIN (SELECT PIC_PATH,PIC_PKID FROM  DA_PIC WHERE PIC_TYPE='5')B "+
						" ON A.PKID =B.PIC_PKID WHERE DA_HOUSEHOLD_ID="+list.get(0).get("PKID");
			List<Map> list2=this.getBySqlMapper.findRecords(sql2);
			if(list2.size()>0){
				for(Map val:list2){
					obj2.put("v6","".equals(val.get("V6"))||val.get("V6")==null?"":val.get("V6"));
					obj2.put("v7","".equals(val.get("V7"))||val.get("V7")==null?"":val.get("V7"));
					obj2.put("v8","".equals(val.get("V8"))||val.get("V8")==null?"":val.get("V8"));
					obj2.put("v10","".equals(val.get("V10"))||val.get("V10")==null?"":val.get("V10"));
					obj2.put("v11","".equals(val.get("V11"))||val.get("V11")==null?"":val.get("V11"));
					obj2.put("v12","".equals(val.get("V12"))||val.get("V12")==null?"":val.get("V12"));
					obj2.put("v13","".equals(val.get("V13"))||val.get("V13")==null?"":val.get("V13"));
					obj2.put("v14","".equals(val.get("V14"))||val.get("V14")==null?"":val.get("V14"));
					obj2.put("v15","".equals(val.get("V15"))||val.get("V15")==null?"":val.get("V15"));
					obj2.put("v16","".equals(val.get("V16"))||val.get("V16")==null?"":val.get("V16"));
					obj2.put("v17","".equals(val.get("V17"))||val.get("V17")==null?"":val.get("V17"));
					obj2.put("v18","".equals(val.get("V18"))||val.get("V18")==null?"":val.get("V18"));
					obj2.put("v19","".equals(val.get("V19"))||val.get("V19")==null?"":val.get("V19"));
					obj2.put("v20","".equals(val.get("V20"))||val.get("V20")==null?"":val.get("V20"));
					obj2.put("v28","".equals(val.get("V28"))||val.get("V28")==null?"":val.get("V28"));
					obj2.put("v32","".equals(val.get("V32"))||val.get("V32")==null?"":val.get("V32"));
					obj2.put("pic_path","".equals(val.get("PIC_PATH"))||val.get("PIC_PATH")==null?"":val.get("PIC_PATH"));
					jsonArray2.add(obj2);
				}
			}
			//帮扶计划
			String sql3="SELECT * FROM DA_HELP_INFO WHERE DA_HOUSEHOLD_ID="+list.get(0).get("PKID");
			List<Map> list3=this.getBySqlMapper.findRecords(sql3);
			if(list3.size()>0){
				for(Map val:list3){
					obj3.put("v1","".equals(val.get("V1"))||val.get("V1")==null?"":val.get("V1"));
					obj3.put("v2","".equals(val.get("V2"))||val.get("V2")==null?"":val.get("V2"));
					obj3.put("v3","".equals(val.get("V3"))||val.get("V3")==null?"":val.get("V3"));
					jsonArray3.add(obj3);
				}
			}
			//帮扶措施
			String sql4="SELECT V1,V2,V3,DA_HOUSEHOLD_ID , MAX(CASE V7 WHEN '2016' THEN V4 ELSE '' END ) V4_2016, "+
	        		"MAX(CASE V7 WHEN '2016' THEN V5 ELSE '' END ) V5_2016,MAX(CASE V7 WHEN '2016' THEN V6 ELSE '' END ) V6_2016,  "+
	        		"MAX(CASE V7 WHEN '2017' THEN V4 ELSE '' END ) V4_2017,MAX(CASE V7 WHEN '2017' THEN V5 ELSE '' END ) V5_2017,"+
	        		"MAX(CASE V7 WHEN '2017' THEN V6 ELSE '' END ) V6_2017,MAX(CASE V7 WHEN '2018' THEN V4 ELSE '' END ) V4_2018, "+
	        		"MAX(CASE V7 WHEN '2018' THEN V5 ELSE '' END ) V5_2018,MAX(CASE V7 WHEN '2018' THEN V6 ELSE '' END ) V6_2018,"+
	        		"MAX(CASE V7 WHEN '2019' THEN V4 ELSE '' END ) V4_2019,MAX(CASE V7 WHEN '2019' THEN V5 ELSE '' END ) V5_2019, "+
	        		"MAX(CASE V7 WHEN '2019' THEN V6 ELSE '' END ) V6_2019 FROM DA_HELP_TZ_MEASURES WHERE DA_HOUSEHOLD_ID="+list.get(0).get("PKID")+" GROUP  BY V1,V2,V3 ";
			List<Map> list4=this.getBySqlMapper.findRecords(sql4);
			if(list4.size()>0){
				for(Map val:list4){
					obj4.put("v1","".equals(val.get("V1"))||val.get("V1")==null?"":val.get("V1"));
					obj4.put("v2","".equals(val.get("V2"))||val.get("V2")==null?"":val.get("V2"));
					obj4.put("v3","".equals(val.get("V3"))||val.get("V3")==null?"":val.get("V3"));
					obj4.put("v4_2016","".equals(val.get("V4_2016"))||val.get("V4_2016")==null?"":val.get("V4_2016"));
					obj4.put("v5_2016","".equals(val.get("V5_2016"))||val.get("V5_2016")==null?"":val.get("V5_2016"));
					obj4.put("v6_2016","".equals(val.get("V6_2016"))||val.get("V6_2016")==null?"":val.get("V6_2016"));
					obj4.put("v4_2017","".equals(val.get("V4_2017"))||val.get("V4_2017")==null?"":val.get("V4_2017"));
					obj4.put("v5_2017","".equals(val.get("V5_2017"))||val.get("V5_2017")==null?"":val.get("V5_2017"));
					obj4.put("v6_2017","".equals(val.get("V6_2017"))||val.get("V6_2017")==null?"":val.get("V6_2017"));
					obj4.put("v4_2018","".equals(val.get("V4_2018"))||val.get("V4_2018")==null?"":val.get("V4_2018"));
					obj4.put("v5_2018","".equals(val.get("V5_2018"))||val.get("V5_2018")==null?"":val.get("V5_2018"));
					obj4.put("v6_2018","".equals(val.get("V6_2018"))||val.get("V6_2018")==null?"":val.get("V6_2018"));
					obj4.put("v4_2019","".equals(val.get("V4_2019"))||val.get("V4_2019")==null?"":val.get("V4_2019"));
					obj4.put("v5_2019","".equals(val.get("V5_2019"))||val.get("V5_2019")==null?"":val.get("V5_2019"));
					obj4.put("v6_2019","".equals(val.get("V6_2019"))||val.get("V6_2019")==null?"":val.get("V6_2019"));
					jsonArray4.add(obj4);
				}
			}
			//走访记录
			String sql5="SELECT V1,V2,V3, GROUP_CONCAT(PIC_PATH ORDER BY PIC_PATH SEPARATOR ',') PATH FROM ("+
						"	SELECT *  FROM DA_HELP_VISIT A LEFT JOIN (SELECT PIC_PATH,PIC_PKID FROM DA_PIC WHERE PIC_TYPE='2' ) B ON A.PKID=B.PIC_PKID"+
						" WHERE A.DA_HOUSEHOLD_ID="+list.get(0).get("PKID")+" )AA GROUP BY PKID ORDER BY V1 DESC";
			List<Map> list5=this.getBySqlMapper.findRecords(sql5);
			if(list5.size()>0){
				for(Map val:list5){
					obj5.put("v1","".equals(val.get("V1"))||val.get("V1")==null?"":val.get("V1"));
					obj5.put("v2","".equals(val.get("V2"))||val.get("V2")==null?"":val.get("V2"));
					obj5.put("v3","".equals(val.get("V3"))||val.get("V3")==null?"":val.get("V3"));
					obj5.put("path","".equals(val.get("PATH"))||val.get("PATH")==null?"":val.get("PATH"));
					jsonArray5.add(obj5);
				}
			}
			//当前人均纯收入
			String sql6="SELECT  ROUND((DQSZ-DQZC)/V9,2) BFQ,ROUND((DQSZH-DQZCH)/V9,2) BFH FROM  DA_HOUSEHOLD A LEFT JOIN "+
						"(SELECT V39 DQSZ,DA_HOUSEHOLD_ID FROM DA_CURRENT_INCOME)D ON A.PKID=D.DA_HOUSEHOLD_ID LEFT JOIN  "+
						"(SELECT V31 DQZC,DA_HOUSEHOLD_ID FROM DA_CURRENT_EXPENDITURE ) E ON A.PKID=E.DA_HOUSEHOLD_ID LEFT JOIN"+
						" (SELECT V39 DQSZH,DA_HOUSEHOLD_ID FROM DA_HELPBACK_INCOME)F ON A.PKID=F.DA_HOUSEHOLD_ID LEFT JOIN "+
						"(SELECT V31 DQZCH,DA_HOUSEHOLD_ID FROM DA_HELPBACK_EXPENDITURE)G ON A.PKID =G.DA_HOUSEHOLD_ID WHERE A.PKID="+list.get(0).get("PKID")+"";
			List<Map> list6=this.getBySqlMapper.findRecords(sql6);
			if(list6.size()>0){
				for(Map val:list6){
					obj6.put("bfq","".equals(val.get("BFQ"))||val.get("BFQ")==null?0:val.get("BFQ"));
					obj6.put("bfh","".equals(val.get("BFH"))||val.get("BFH")==null?0:val.get("BFH"));
					jsonArray6.add(obj6);
				}
			}
			
			//生产生活条件
			String sql7="SELECT * FROM (SELECT DA_HOUSEHOLD_ID,V1,V2,V3,V4,V5,V13,V14 FROM DA_PRODUCTION)A LEFT JOIN "+
						"(SELECT V1 FV1,V5 FV5,V6 FV6,V7 FV7,V8 FV8,V9 FV9,V10 FV10,V11 FV11,V12 FV12,"+
						"DA_HOUSEHOLD_ID FID FROM DA_LIFE)B ON A.DA_HOUSEHOLD_ID=B.FID WHERE A.DA_HOUSEHOLD_ID="+list.get(0).get("PKID")+"";
			List<Map> list7=this.getBySqlMapper.findRecords(sql7);
			if(list7.size()>0){
				for(Map val:list7){
					obj7.put("v1","".equals(val.get("V1"))||val.get("V1")==null?0:val.get("V1"));
					obj7.put("v2","".equals(val.get("V2"))||val.get("V2")==null?0:val.get("V2"));
					obj7.put("v3","".equals(val.get("V3"))||val.get("V3")==null?0:val.get("V3"));
					obj7.put("v4","".equals(val.get("V4"))||val.get("V4")==null?0:val.get("V4"));
					obj7.put("v5","".equals(val.get("V5"))||val.get("V5")==null?0:val.get("V5"));
					obj7.put("v13","".equals(val.get("V13"))||val.get("V13")==null?0:val.get("V13"));
					obj7.put("v14","".equals(val.get("V14"))||val.get("V14")==null?0:val.get("V14"));
					obj7.put("fv1","".equals(val.get("FV1"))||val.get("FV1")==null?0:val.get("FV1"));
					obj7.put("fv5","".equals(val.get("FV5"))||val.get("FV5")==null?"":val.get("FV5"));
					obj7.put("fv6","".equals(val.get("FV6"))||val.get("FV6")==null?"":val.get("FV6"));
					obj7.put("fv7","".equals(val.get("FV7"))||val.get("FV7")==null?0:val.get("FV7"));
					obj7.put("fv8","".equals(val.get("FV8"))||val.get("FV8")==null?"":val.get("FV8"));
					obj7.put("fv9","".equals(val.get("FV9"))||val.get("FV9")==null?"":val.get("FV9"));
					obj7.put("fv10","".equals(val.get("FV10"))||val.get("FV10")==null?"":val.get("FV10"));
					obj7.put("fv11","".equals(val.get("FV11"))||val.get("FV11")==null?"":val.get("FV11"));
					obj7.put("fv12","".equals(val.get("FV12"))||val.get("FV12")==null?"":val.get("FV12"));
					jsonArray7.add(obj7);
					
				}
			}
			response.getWriter().write("{\"result\":"+jsonArray.toString()+",\"result1\":"+jsonArray2.toString()+",\"result2\":"+jsonArray3.toString()+","+
					"\"result3\":"+jsonArray4.toString()+",\"result4\":"+jsonArray5.toString()+",\"result5\":"+jsonArray6.toString()+",\"result6\":"+jsonArray7.toString()+"}");
			
		}else{
			response.getWriter().write("0");
		}
	}
	/**
	 * 接收移动端关于地图的参数
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getCharParameter.do")
	public void getCharParameter(HttpServletRequest request,HttpServletResponse response) throws IOException{
		char_type=request.getParameter("type");
		String standard=request.getParameter("standard");
		if("0".equals(standard)){
			char_standard="国家级贫困人口";
		}else if("1".equals(standard)){
			char_standard="市级低收入人口";
		}
		char_name=request.getParameter("name");
		if(char_type==""||char_standard==""||char_name==""){
			response.getWriter().write("0");
		}else{
			response.getWriter().write("1");
		}
	}
	
	/**
	 * 往前台js传参数
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getReceiveParameter.do")
	public void getReceiveParameter(HttpServletRequest request,HttpServletResponse response ) throws IOException{
		JSONObject obj=new JSONObject();
		String sql = "SELECT COM_CODE FROM SYS_COMPANY WHERE COM_NAME='"+char_name+"'";
		List<Map> list=this.getBySqlMapper.findRecords(sql);
		if(list.size()>0){
			obj.put("code", list.get(0).get("COM_CODE"));
		}else{
			obj.put("code", "");
		}
		obj.put("stardand",char_standard);
		response.getWriter().write(obj.toString());
	}
	
	/**
	 * 地图
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getCharController.do")
	public void getCharController(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String sql = "";
		if("0".equals(char_type)){//市级
			sql="SELECT V3,COUNT(*) AS COUNT FROM DA_HOUSEHOLD  WHERE SYS_STANDARD='"+char_standard+"' GROUP BY V3";
		}else if ("1".equals(char_type)){//县级
			sql="SELECT A1.V4 AS V3,COUNT(*) AS COUNT FROM DA_HOUSEHOLD A1 JOIN SYS_COMPANY A2 ON A1.V3 = A2.COM_NAME WHERE A2.COM_NAME='"+char_name+"' AND A1.SYS_STANDARD='"+char_standard+"' GROUP BY A1.V4";
		}else if ("2".equals(char_type)){//乡级
			sql="SELECT A1.V5 AS V3,COUNT(*) AS COUNT FROM DA_HOUSEHOLD A1 JOIN SYS_COMPANY A2 ON A1.V4 = A2.COM_NAME WHERE A2.COM_NAME='"+char_name+"' AND A1.SYS_STANDARD='"+char_standard+"' GROUP BY A1.V5";
		}else if ("3".equals(char_type)){//村级
			 sql="SELECT A1.V5 AS V3,COUNT(*) AS COUNT FROM DA_HOUSEHOLD A1 JOIN SYS_COMPANY A2 ON A1.V4 = A2.COM_NAME WHERE A2.COM_NAME='"+char_name+"' AND A1.SYS_STANDARD='"+char_standard+"' GROUP BY A1.V5";
		}
		List<Map> sql_list = this.getBySqlMapper.findRecords(sql);
		JSONObject val = new JSONObject();
		if(sql_list.size()>0){
			JSONArray jsa=new JSONArray();
			for(int i = 0;i<sql_list.size();i++){
				Map Admin_st_map = sql_list.get(i);
				for(int j = 0; j<Admin_st_map.size(); j++){
					val.put("name", Admin_st_map.get("V3"));
					val.put("value", Admin_st_map.get("COUNT"));
				}
				jsa.add(val);
			}
			response.getWriter().write(jsa.toString());
		}else{
			response.getWriter().write(0);
		}
	}
	
	
	
	/**
	 * 获取签名
	 * @param request
	 * @param response
	 * @throws DigestException 
	 * @throws IOException 
	 */
	@RequestMapping("getQianming.do")
	public void getQianming(HttpServletRequest request, HttpServletResponse response) throws DigestException, IOException{
		String http_url = request.getParameter("url");
		
		String url1 = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx4fa9e95d9af2477a&secret=d0fa7c87870507490f4bedc671bcbc14";
		String json = loadJSON(url1);
		JSONObject  jasonObject = JSONObject.fromObject(json);
		Map map = (Map)jasonObject;
		String  str1 = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+map.get("access_token").toString()+"&type=jsapi";
		String token = loadJSON(str1); 
		
		
		JSONObject  ticket_obj = JSONObject.fromObject(token);
		Map ticket_map = (Map)ticket_obj;
		String ticket = ticket_map.get("ticket").toString();
		
		//1、获取AccessToken  
	    accessToken = token;  
	      
	    //2、获取Ticket  
	    jsapi_ticket = ticket;  
	      
	    //3、时间戳和随机字符串  
	    String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串  
	    String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳  
	      
//	    System.out.println("accessToken:"+accessToken+"\njsapi_ticket:"+jsapi_ticket+"\n时间戳："+timestamp+"\n随机字符串："+noncestr);  
	    //4、获取url  
	    String url = http_url;  
	      
	    //5、将参数排序并拼接字符串  
	    String str = "jsapi_ticket="+jsapi_ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;  
	     
	    //6、将字符串进行sha1加密  
	    String signature =SHA1(str);  
//	    System.out.println("参数："+str+"\n签名："+signature);  
	    JSONArray jsonArray = new JSONArray ();
	    JSONObject obj = new JSONObject();
	    obj.put("time", timestamp);//时间戳
	    obj.put("num", noncestr);//16位的随机数
	    obj.put("val", signature);//加密后的
	    obj.put("token", map.get("access_token").toString());//
	    jsonArray.add(obj);
	    response.getWriter().write(jsonArray.toString());
	}
	/**
	 * 请求参数
	 * @param url
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	 public static String loadJSON (String url) {
	        StringBuilder json = new StringBuilder();
	        try {
	            URL oracle = new URL(url);
	            URLConnection yc = oracle.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	            String inputLine = null;
	            while ( (inputLine = in.readLine()) != null) {
	                json.append(inputLine);
	            }
	            in.close();
	        } catch (MalformedURLException e) {
	        } catch (IOException e) {
	        }
	        return json.toString();
	    }
	 
	 /**
	  * SHA加密
	  * @param decript
	  * @return
	  */
	 public static String SHA1(String decript) {  
		    try {  
		        MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");  
		        digest.update(decript.getBytes());  
		        byte messageDigest[] = digest.digest();  
		        // Create Hex String  
		        StringBuffer hexString = new StringBuffer();  
		        // 字节数组转换为 十六进制 数  
		            for (int i = 0; i < messageDigest.length; i++) {  
		                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);  
		                if (shaHex.length() < 2) {  
		                    hexString.append(0);  
		                }  
		                hexString.append(shaHex);  
		            }  
		            return hexString.toString();  
		   
		        } catch (NoSuchAlgorithmException e) {  
		            e.printStackTrace();  
		        }  
		        return "";  
		} 
	 
	/**
	 * 添加走访记录
	 * @param request
	 * @param response
	 * @throws DigestException
	 * @throws IOException
	 */
	@RequestMapping("addZfjl.do")
	public void addZfjl(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String poor_id = request.getParameter("poor_id");
		String zfjl = request.getParameter("zfjl");
		String sid = request.getParameter("sid");
		String latitude = request.getParameter("latitude");
		String longitude = request.getParameter("longitude");
		String[] photo = request.getParameterValues("photo");
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
		String hql="INSERT INTO DA_HELP_VISIT(DA_HOUSEHOLD_ID,SYS_PERSONAL_ID,V1,V2,V3,LNG,LAT,ADDRESS)"+
				" VALUES('"+poor_id+"','"+sid+"','"+simpleDate.format(new Date())+"','','"+zfjl+"','"+longitude+"','"+latitude+"','')";
		
		int insert_num = this.getBySqlMapper.insert(hql);
		
		String cha_sql="SELECT MAX(PKID) PKID FROM DA_HELP_VISIT WHERE V1='"+simpleDate.format(new Date())+"' AND V3= '"+zfjl+"' AND DA_HOUSEHOLD_ID="+poor_id +" AND SYS_PERSONAL_ID ='"+sid+"'";
		List<Map> list = this.getBySqlMapper.findRecords(cha_sql);
		String main = "" ;
		if(list.size()>0){
			main=list.get(0).get("PKID").toString();    
		}
		String saveUrl = request.getContextPath() + "/attached/2/";
		String savePath = request.getServletContext().getRealPath("/")+ "attached/2/";
		// 创建文件夹
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String ymd = sdf.format(new Date());
		savePath += ymd + "\\";
		saveUrl += ymd + "/";
		File dirFile = new File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		String name = "";
		for (int i = 0; i < photo.length; i++) {
			String res = downloadFromUrl(photo[i], savePath,name);
            String sql="INSERT INTO DA_PIC (PIC_TYPE,PIC_PKID,PIC_FORMAT,PIC_PATH)"+
    				" VALUES('2','"+main+"','jpg','"+saveUrl+res+"')";
            int insert_photo = this.getBySqlMapper.insert(sql);
		}
		response.getWriter().write("5");
	}
	/**
	 * 添加走访记录
	 * @param request
	 * @param response
	 * @throws DigestException
	 * @throws IOException
	 */
	@RequestMapping("getAdd_jttx.do")
	public void getAdd_jttx(HttpServletRequest request, HttpServletResponse response) throws IOException{

		String poor_id = request.getParameter("poor_id");//贫困户id
		String photo = request.getParameter("photo");//照片
		String type = request.getParameter("type");//类型
		
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
		String saveUrl = request.getContextPath() + "/attached/"+type+"/";
		String savePath = request.getServletContext().getRealPath("/")+ "attached/"+type+"/";
		// 创建文件夹
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String ymd = sdf.format(new Date());
		savePath += ymd + "\\";
		saveUrl += ymd + "/";
		File dirFile = new File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		String name = "";

		String res = downloadFromUrl(photo, savePath,name);
        String sql="INSERT INTO DA_PIC (PIC_TYPE,PIC_PKID,PIC_FORMAT,PIC_PATH)"+
				" VALUES('"+type+"','"+poor_id+"','jpg','"+saveUrl+res+"')";
        int insert_photo = this.getBySqlMapper.insert(sql);
	
		response.getWriter().write("5");
	
	}
	/**
	 * 把图片存到本地指定位置
	 * @param url
	 * @param dir
	 * @param name
	 * @return
	 */
	public static String downloadFromUrl(String url, String dir,String name) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");  
	        String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + ".jpg"; 
			URL httpurl = new URL(url);  
            String fileName = newFileName;  
            name = newFileName;
            File f = new File(dir + fileName);  
            FileUtils.copyURLToFile(httpurl, f);  
		} catch (Exception e) {
			e.printStackTrace();
			return "Fault!";
		}
		return name;
	}
	/**
	 * 微信修改密码
	 * @param request
	 * @param response
	 * @throws NoSuchAlgorithmException 
	 * @throws DigestException
	 * @throws IOException
	 */
	@RequestMapping("getUpdatePassword.do")
	public void getUpdatePassword(HttpServletRequest request,HttpServletResponse response) throws NoSuchAlgorithmException,IOException {
		String old_password = request.getParameter("old_password");
		old_password = Tool.md5(old_password);
		String new_password = request.getParameter("new_password");
		String pkid = request.getParameter("pkid");
		try {
			String sql = "select COL_PASSWORD from SYS_USER where pkid ="
					+ pkid;
			List<Map> list = this.getBySqlMapper.findRecords(sql);

			if (old_password.equals(list.get(0).get("COL_PASSWORD"))) {
				String update_sql = "update sys_user set col_password='"
						+ Tool.md5(new_password) + "' where pkid=" + pkid;
				this.getBySqlMapper.update(update_sql);
				response.getWriter().write("5");
			} else {

				response.getWriter().write("0");
			}
		} catch (Exception e) {
			
		  }

	}
}
