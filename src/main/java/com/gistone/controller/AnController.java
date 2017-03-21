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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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

import com.gistone.WeChatApp;
import com.gistone.MyBatis.config.GetBySqlMapper;
import com.gistone.util.DateFormatUtil;
import com.gistone.util.MapUtil;
import com.gistone.util.Tool;

@RestController
@RequestMapping
public class AnController{
	
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	private JSONArray getPaixu(JSONArray jsa, String name) {
		if (name.equals("内蒙古自治区")) {
			JSONArray val = new JSONArray();
			String str[] = { "呼和浩特市", "包头市", "呼伦贝尔市", "兴安盟", "通辽市", "赤峰市",
					"锡林郭勒盟", "乌兰察布市", "鄂尔多斯市", "巴彦淖尔市", "乌海市", "阿拉善盟" };
			for (int k = 0; k < str.length; k++) {
				for (int i = 0; i < jsa.size(); i++) {
					JSONObject jo = JSONObject.fromObject(jsa.get(i));
					if (jo.get("V1").toString().equals(str[k])) {
						val.add(jo);
					}
				}
			}
			return val;
		} else {
			return jsa;
		}
	}
	/**
	 * 安卓登录接口 
	 * @author 太年轻
	 * @date 2016年9月6日
	 * @param request
	 * @param response
	 * @throws Exception
	 * 微信和app公用
	 */
	@RequestMapping("getAnLoginController.do")
	public void getAnLoginController(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
		List<Map> list;
		String phone = request.getParameter("phone");//电话13904794720
		String password = request.getParameter("password");//密码
		
		
		//SYS_PERSONAL_LD表废弃
		//String sqlLd = "select * from SYS_PERSONAL_LD where PERSONAL_PHONE ='"+phone+"'";
		
		//查询pc端的用户表
		String sqlLd = "select t1.pkid,t1.col_account,t1.col_password,t1.sys_com_code,t1.login_count,t1.LOGIN_TIME,t1.com_vd,t1.com_vs,t3.role_name,t3.pkid as role_id  from sys_user t1 "
				+ "LEFT JOIN sys_user_role_many t2 on t1.SYS_ROLE_ID=t2.user_id "
				+ "LEFT JOIN sys_role t3 on t2.role_id=t3.pkid WHERE t1.col_account = '"+phone+"'";
		
		
		if(this.getBySqlMapper.findRecords(sqlLd).size()>0){
			List<Map> Login = this.getBySqlMapper.findRecords(sqlLd);
			String sql_com = "";
			int lev =1;
			if(Login.get(0).get("COM_VD").equals("V1")){
				lev =1;
				sql_com = "select v1,v2 from SYS_COM where v2='"+Login.get(0).get("SYS_COM_CODE")+"' GROUP BY v1,v2";
			}else if(Login.get(0).get("COM_VD").equals("V3")){
				lev =2;
				sql_com = "select v1,v2,v3,v4 from SYS_COM where v4='"+Login.get(0).get("SYS_COM_CODE")+"' GROUP BY v1,v2,v3,v4";
			}else if(Login.get(0).get("COM_VD").equals("V5")){
				lev =3;
				sql_com = "select v1,v2,v3,v4,v5,v6 from SYS_COM where v6='"+Login.get(0).get("SYS_COM_CODE")+"' GROUP BY v1,v2,v3,v4,v5,v6";
			}else if(Login.get(0).get("COM_VD").equals("V7")){
				lev =4;
				sql_com = "select v1,v2,v3,v4,v5,v6,v7,v8 from SYS_COM where v8='"+Login.get(0).get("SYS_COM_CODE")+"' GROUP BY v1,v2,v3,v4,v5,v6,v7,v8";
			}else if(Login.get(0).get("COM_VD").equals("V9")){
				lev =5;
				sql_com = "select * from SYS_COM where v10='"+Login.get(0).get("SYS_COM_CODE")+"'";
			}
			
			if(Tool.md5(password).equals(Login.get(0).get("COL_PASSWORD"))==true){//密码正确
				if(!sql_com.equals("")){
					List<Map> user_list = this.getBySqlMapper.findRecords(sql_com);
					Map us = user_list.get(0);
					String name,code;
					if(lev==2){
						name=us.get("V3").toString();
						code=us.get("V4").toString();
					}else if(lev==3){
						name=us.get("V5").toString();
						code=us.get("V6").toString();
					}else if(lev==4){
						name=us.get("V7").toString();
						code=us.get("V8").toString();
					}else if(lev==5){
						name=us.get("V9").toString();
						code=us.get("V10").toString();
					}else{
						name=us.get("V1").toString();
						code=us.get("V2").toString();
					}
					response.getWriter().write("{\"success\":0,\"message\":\"0\",\"data\":{\"level\":"+lev+",\"name\":\""+name+"\",\"code\":\""+code+"\"}}");//登录成功
				}else{
					response.getWriter().write("{\"success\":1,\"message\":\"没有单位\",\"data\":\"\"}");
				}
			}else{
				response.getWriter().write("{\"success\":1,\"message\":\"密码错误\",\"data\":\"\"}");
			}
			/*if (this.getBySqlMapper.findRecords(sqlLd).get(0).get("PASSWORD")!=null&&!"".equals(this.getBySqlMapper.findRecords(sqlLd).get(0).get("PASSWORD"))){
				if (password!=null&&!"".equals(password)&&password.equals(this.getBySqlMapper.findRecords(sqlLd).get(0).get("PASSWORD"))){
					response.getWriter().write("{\"success\":0,\"message\":\"0\",\"data\":\"\"}");//登录成功
				}else{
					response.getWriter().write("{\"success\":1,\"message\":\"密码错误\",\"data\":\"\"}");
				}
			} else {
				if (password!=null&&!"".equals(password)&& password.equals(phone.substring(5,11))){
					response.getWriter().write("{\"success\":0,\"message\":\"0\",\"data\":\"\"}");//登录成功
				}else {
					response.getWriter().write("{\"success\":1,\"message\":\"密码错误\",\"data\":\"\"}");
				}
			}*/
			return;
		}
		String sql = "select * from SYS_PERSONAL_HOUSEHOLD_MANY where PERSONAL_PHONE ='"+phone+"'";
		list = this.getBySqlMapper.findRecords(sql);
		if ( list.size() > 0 ) {
			if (list.get(0).get("PASSWORD")!=null&&!"".equals(list.get(0).get("PASSWORD"))){
				if (password!=null&&!"".equals(password)&&password.equals(list.get(0).get("PASSWORD"))){
					response.getWriter().write("{\"success\":0,\"message\":\"登录成功\",\"data\":{\"phone\":"+phone+",\"name\":\""+list.get(0).get("PERSONAL_NAME").toString()+"\"}}");//登录成功
				}else{
					response.getWriter().write("{\"success\":1,\"message\":\"密码错误\",\"data\":\"\"}");
				}
			} else {
				if ( password!=null&&!"".equals(password)&&password.equals(phone.substring(5,11))){
					response.getWriter().write("{\"success\":0,\"message\":\"登录成功\",\"data\":{\"phone\":"+phone+",\"name\":\""+list.get(0).get("PERSONAL_NAME").toString()+"\"}}");//登录成功
				}else {
					response.getWriter().write("{\"success\":1,\"message\":\"密码错误\",\"data\":\"\"}");
				}
			}
		} else {
			response.getWriter().write("{\"success\":1,\"message\":\"用户不存在\",\"data\":\"\"}");
		}
		
	}
	/**
	 *根据帮夫人进行查询帮扶对象（公用）
	 * @author 太年轻
	 * @date 2016年9月6日
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getSavePoorController.do")
	public void getSavePoorController (HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
		String phone = request.getParameter("phone");
		String name = request.getParameter("name");
		String sql = "SELECT AAB002,AAR008,BB.AAC001,BB.AAB001,AAB004,AAR012,AAQ002,AAC004,AAC005,AAC006,AAC007,AAC008,AAC012,NUM,V1,V3,V5,V7,V9,PIC_PATH FROM  " +
					"(SELECT HOUSEHOLD_NAME ,HOUSEHOLD_CARD FROM SYS_PERSONAL_HOUSEHOLD_MANY WHERE PERSONAL_PHONE ='"+phone+"' AND PERSONAL_NAME='"+name+"' GROUP BY HOUSEHOLD_NAME,HOUSEHOLD_CARD )AA "+
					"LEFT JOIN (SELECT AAB002,AAB001,AAC001,AAB004 FROM NEIMENG0117_AB01 WHERE AAR040 ='2016')BB ON AA.HOUSEHOLD_NAME=BB.AAB002 AND AA.HOUSEHOLD_CARD=BB.AAB004 LEFT JOIN "+
					"(SELECT AAC001,AAR008,AAR012,AAQ002,AAC004,AAC005,AAC006,AAC007,AAC008,AAC012 FROM NEIMENG0117_AC01 WHERE AAR040 ='2016') CC "+
					"ON BB.AAC001 = CC.AAC001 LEFT JOIN (SELECT AAC001, COUNT(*) NUM FROM NEIMENG0117_AB01 WHERE AAR040='2016' GROUP BY AAC001  ) DD "+
					"ON BB.AAC001=DD.AAC001 LEFT JOIN SYS_COM EE ON CC.AAR008=EE.v10  "+
					"LEFT JOIN	(SELECT AAB001,HOUSEHOLD_NAME,HOUSEHOLD_CARD,PIC_PATH FROM DA_PIC_HOUSEHOLD)FF  "+
					"ON BB.AAB002=FF.HOUSEHOLD_NAME AND BB.AAB004 =FF.HOUSEHOLD_CARD AND BB.AAB001=FF.AAB001";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		JSONArray json = new JSONArray ();
		if ( list.size() > 0 ) {
			for ( int i = 0 ; i < list.size() ; i ++ ) {
				JSONObject obj = new JSONObject () ;
				obj.put("v0", "".equals(list.get(i).get("AAC001")) || list.get(i).get("AAC001") == null ? "" : list.get(i).get("AAC001").toString());//贫困户编号
				obj.put("d1", "".equals(list.get(i).get("AAR008")) || list.get(i).get("AAR008") == null ? "" : list.get(i).get("AAR008").toString());//村行政区划
				obj.put("v1", "".equals(list.get(i).get("V1")) || list.get(i).get("V1") == null ? "" : list.get(i).get("V1").toString());//省（自治区、直辖市）
				obj.put("v2", "".equals(list.get(i).get("V3")) || list.get(i).get("V3") == null ? "" : list.get(i).get("V3").toString());//	市（盟、州）
				obj.put("v3", "".equals(list.get(i).get("V5")) || list.get(i).get("V5") == null ? "" : list.get(i).get("V5").toString());//	县(市、区、旗)
				obj.put("v4", "".equals(list.get(i).get("V7")) || list.get(i).get("V7") == null ? "" : list.get(i).get("V7").toString());//镇(乡)
				obj.put("v5", "".equals(list.get(i).get("V9")) || list.get(i).get("V9") == null ? "" : list.get(i).get("V9").toString());//	行政村
				obj.put("v6", "".equals(list.get(i).get("AAB002")) || list.get(i).get("AAB002") == null ? "" : list.get(i).get("AAB002").toString());//	姓名
				obj.put("v9", "".equals(list.get(i).get("NUM")) || list.get(i).get("NUM") == null ? "" : list.get(i).get("NUM").toString());//人数
				obj.put("v21", "".equals(list.get(i).get("AAC006")) || list.get(i).get("AAC006") == null ? "" : list.get(i).get("AAC006").toString());//贫困户属性	
				obj.put("v23", "".equals(list.get(i).get("AAC007")) || list.get(i).get("AAC007") == null ? "" : list.get(i).get("AAC007").toString());//主要致贫原因	
				obj.put("v25", "".equals(list.get(i).get("AAR012")) || list.get(i).get("AAR012") == null ? "" : list.get(i).get("AAR012").toString());//联系电话	
				obj.put("v26", "".equals(list.get(i).get("AAQ002")) || list.get(i).get("AAQ002") == null ? "" : list.get(i).get("AAQ002").toString());//开户银行名称	
				obj.put("v27", "".equals(list.get(i).get("AAC004")) || list.get(i).get("AAC004") == null ? "" : list.get(i).get("AAC004").toString());//银行卡号	
				obj.put("v29", "".equals(list.get(i).get("AAC012")) || list.get(i).get("AAC012") == null ? "" : list.get(i).get("AAC012").toString());//是否军烈属	
				obj.put("v8", "".equals(list.get(i).get("AAB004")) || list.get(i).get("AAB004")==null ? "" : list.get(i).get("AAB004").toString());//证件号码
				obj.put("v33", "".equals(list.get(i).get("AAC008")) || list.get(i).get("AAC008") == null ? "" : list.get(i).get("AAC008").toString());//其他致贫原因	
				obj.put("v34", "".equals(list.get(i).get("AAC005")) || list.get(i).get("AAC005") == null ? "" : list.get(i).get("AAC005").toString());//识别标准 国家标准 市级标准	
				obj.put("pic_path", "".equals(list.get(i).get("PIC_PATH")) ||list.get(i).get("PIC_PATH") == null ? "" : list.get(i).get("PIC_PATH").toString());//户主头像
				
				json.add(obj);
			}
			response.getWriter().write("{\"success\":0,\"message\":\"1\",\"data\":"+json.toString()+"}");
		}else {
			response.getWriter().write("{\"success\":1,\"message\":\"该帮扶人没有贫困户\",\"data\":\"\"}");//该帮扶人没有贫困户
		}
	}
	/**
	 * 查看家庭成员（公用）
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getSaveFamily.do")
	public void getSaveFamily(HttpServletRequest request,HttpServletResponse response) throws IOException{ 
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String AAC001 = request.getParameter("pkid");//贫困户编号
		
		String sql = "select AAB001,AAB002,AAB003, AAB004,AAB006,AAB007,AAB008,AAB009,AAB010,AAB011,AAB012,AAB017,AAB019 "+
					"from  NEIMENG0117_AB01 where AAC001='"+AAC001+"' and AAR040='2016' ORDER BY AAB006";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		JSONArray json = new JSONArray () ;
		if ( list.size() > 0 ) {
			for ( int i = 0 ; i < list.size() ; i ++ ) {
				String  cha_sql = " select PIC_PATH from DA_PIC_HOUSEHOLD where AAB001='"+list.get(i).get("AAB001")+"' AND HOUSEHOLD_NAME='"+list.get(i).get("AAB002")+"' AND HOUSEHOLD_CARD='"+list.get(i).get("AAB004")+"' ";
				List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
				JSONObject obj = new JSONObject () ;
				obj.put("v1", "".equals(list.get(i).get("AAB001")) || list.get(i).get("AAB001")==null ? "" : list.get(i).get("AAB001").toString());//贫困人口编号
				obj.put("v2", "".equals(list.get(i).get("AAB010")) || list.get(i).get("AAB010")==null ? "" : list.get(i).get("AAB010").toString());//劳动技能
				obj.put("v6", "".equals(list.get(i).get("AAB002")) || list.get(i).get("AAB002")==null ? "" : list.get(i).get("AAB002").toString());//姓名
				obj.put("v7", "".equals(list.get(i).get("AAB003")) || list.get(i).get("AAB003")==null ? "" : list.get(i).get("AAB003").toString());//性别
				obj.put("v8", "".equals(list.get(i).get("AAB004")) || list.get(i).get("AAB004")==null ? "" : list.get(i).get("AAB004").toString());//证件号码
				obj.put("v10", "".equals(list.get(i).get("AAB006")) || list.get(i).get("AAB006")==null ? "" : list.get(i).get("AAB006").toString());//与户主的关系
				obj.put("v11", "".equals(list.get(i).get("AAB007")) || list.get(i).get("AAB007")==null ? "" : list.get(i).get("AAB007").toString());//民族
				obj.put("v12", "".equals(list.get(i).get("AAB008")) || list.get(i).get("AAB008")==null ? "" : list.get(i).get("AAB008").toString());//文化程度
				obj.put("v13", "".equals(list.get(i).get("AAB009")) || list.get(i).get("AAB009")==null ? "" : list.get(i).get("AAB009").toString());//在校生情况
				obj.put("v15", "".equals(list.get(i).get("AAB017")) || list.get(i).get("AAB017")==null ? "" : list.get(i).get("AAB017").toString());//健康状态
				obj.put("v16", "".equals(list.get(i).get("AAB011")) || list.get(i).get("AAB011")==null ? "" : list.get(i).get("AAB011").toString());//务工情况
				obj.put("v17", "".equals(list.get(i).get("AAB012")) || list.get(i).get("AAB012")==null ? "" : list.get(i).get("AAB012").toString());//务工时间
				obj.put("v32", "".equals(list.get(i).get("AAB019")) || list.get(i).get("AAB019")==null ? "" : list.get(i).get("AAB019").toString());//是否现役军人
				if ( cha_list.size()>0 ) {
					obj.put("pic_path", "".equals(cha_list.get(0).get("PIC_PATH")) || cha_list.get(0).get("PIC_PATH")==null ? "" : cha_list.get(0).get("PIC_PATH").toString());//照片
				} else {
					obj.put("pic_path", "");//照片
				}
				json.add(obj);
			}
			response.getWriter().write("{\"success\":0,\"message\":\"1\",\"data\":"+json.toString()+"}");
		}else {
			response.getWriter().write("{\"success\":0,\"message\":\"没有\",\"data\":\"\"}");
		}
	}
	/**
	 * 查询贫困户信息（公用）
	 * 家庭某一个成员详细信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("getCha_huController.do")
	public void getCha_huController (HttpServletRequest request,HttpServletResponse response) throws IOException{
		String name = request.getParameter("name");//贫困户姓名
		String AAC001 = request.getParameter("AAC001");//贫困户的编号3000498544
		String AAB004 = request.getParameter("AAB004");//贫困户的证件号码
		//贫困户的基本信息
		String cha_sql = "select * from (select AAB002,AAB001,AAC001,AAB004 from NEIMENG0117_AB01 where ";
		if (!"".equals(name) && name != null) {
			cha_sql += "AAB002='"+name+"'";
		} else if(!"".equals(AAC001) && AAC001!=null){
			cha_sql += "AAC001='"+AAC001+"'";
		} else if(!"".equals(AAB004) && AAB004!=null){
			cha_sql += "AAB004='"+AAB004+"'";
		}
		cha_sql += " and AAB006='01'  and AAR040='2016' and AAB015='1' group by AAB002,AAB001,AAC001,AAB004)a left join (select AAC001,AAR008,AAR012,AAQ002,AAC004,AAC005,AAC006,AAC007,AAC008,AAC012,max(AAR040) nian"+
					" from NEIMENG0117_AC01 group BY  AAC001,AAR008,AAR012,AAQ002,AAC004,AAC005,AAC006,AAC007,AAC008,AAC012) b ON a.AAC001 = b.AAC001";
		List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
		JSONArray json = new JSONArray ();
		if ( cha_list.size() > 0 ) {
			for ( int i = 0 ; i < cha_list.size() ; i ++ ) {
			
				//贫困户的人口
				String cha_sql1 = "select count(*) num from (select AAB001,max(AAR040) from NEIMENG0117_AB01 where AAC001='"+cha_list.get(i).get("AAC001")+"' group by AAB001)";
				List<Map> cha_list1 = this.getBySqlMapper.findRecords(cha_sql1);
				//贫困户的地址
				String cha_sql2 = "select v1 sheng,v3 shi,v5 xian,v7 xiang,v9 cun from  SYS_COM where v10='"+cha_list.get(i).get("AAR008")+"'";
				List<Map> cha_list2 = this.getBySqlMapper.findRecords(cha_sql2);
				//户主头像
				String cha_sql3 = "SELECT PIC_PATH from DA_PIC_HOUSEHOLD where AAB001 ='"+cha_list.get(i).get("AAB001")+"' and  HOUSEHOLD_NAME='"+cha_list.get(i).get("AAB002")+"' AND HOUSEHOLD_CARD ='"+cha_list.get(i).get("AAB004")+"' ";
				List<Map> cha_list3 = this.getBySqlMapper.findRecords(cha_sql3);
				JSONObject obj = new JSONObject () ;
				obj.put("v0", "".equals(cha_list.get(i).get("AAC001")) || cha_list.get(i).get("AAC001") == null ? "" : cha_list.get(i).get("AAC001").toString());//贫困户编号
				obj.put("v1", "".equals(cha_list2.get(0).get("SHENG")) || cha_list2.get(0).get("SHENG") == null ? "" : cha_list2.get(0).get("SHENG").toString());//省（自治区、直辖市）
				obj.put("v2", "".equals(cha_list2.get(0).get("SHI")) || cha_list2.get(0).get("SHI") == null ? "" : cha_list2.get(0).get("SHI").toString());//	市（盟、州）
				obj.put("v3", "".equals(cha_list2.get(0).get("XIAN")) || cha_list2.get(0).get("XIAN") == null ? "" : cha_list2.get(0).get("XIAN").toString());//	县(市、区、旗)
				obj.put("v4", "".equals(cha_list2.get(0).get("XIANG")) || cha_list2.get(0).get("XIANG") == null ? "" : cha_list2.get(0).get("XIANG").toString());//镇(乡)
				obj.put("v5", "".equals(cha_list2.get(0).get("CUN")) || cha_list2.get(0).get("CUN") == null ? "" : cha_list2.get(0).get("CUN").toString());//	行政村
				obj.put("v6", "".equals(cha_list.get(i).get("AAB002")) || cha_list.get(i).get("AAB002") == null ? "" : cha_list.get(i).get("AAB002").toString());//	姓名
				obj.put("v9", "".equals(cha_list1.get(0).get("NUM")) || cha_list1.get(0).get("NUM") == null ? "" : cha_list1.get(0).get("NUM").toString());//人数
				obj.put("v21", "".equals(cha_list.get(i).get("AAC006")) || cha_list.get(i).get("AAC006") == null ? "" : cha_list.get(i).get("AAC006").toString());//贫困户属性	
				obj.put("v23", "".equals(cha_list.get(i).get("AAC007")) || cha_list.get(i).get("AAC007") == null ? "" : cha_list.get(i).get("AAC007").toString());//主要致贫原因	
				obj.put("v25", "".equals(cha_list.get(i).get("AAR012")) || cha_list.get(i).get("AAR012") == null ? "" : cha_list.get(i).get("AAR012").toString());//联系电话	
				obj.put("v26", "".equals(cha_list.get(i).get("AAQ002")) || cha_list.get(i).get("AAQ002") == null ? "" : cha_list.get(i).get("AAQ002").toString());//开户银行名称	
				obj.put("v27", "".equals(cha_list.get(i).get("AAC004")) || cha_list.get(i).get("AAC004") == null ? "" : cha_list.get(i).get("AAC004").toString());//银行卡号	
				obj.put("v29", "".equals(cha_list.get(i).get("AAC012")) || cha_list.get(i).get("AAC012") == null ? "" : cha_list.get(i).get("AAC012").toString());//是否军烈属	
				obj.put("v8", "".equals(cha_list.get(i).get("AAB004")) || cha_list.get(i).get("AAB004")==null ? "" : cha_list.get(i).get("AAB004").toString());//证件号码
				obj.put("v33", "".equals(cha_list.get(i).get("AAC008")) || cha_list.get(i).get("AAC008") == null ? "" : cha_list.get(i).get("AAC008").toString());//其他致贫原因	
				obj.put("v34", "".equals(cha_list.get(i).get("AAC005")) || cha_list.get(i).get("AAC005") == null ? "" : cha_list.get(i).get("AAC005").toString());//识别标准 国家标准 市级标准	
				if (cha_list3.size() > 0 ) {
					obj.put("pic_path", "".equals(cha_list3.get(0).get("PIC_PATH")) || cha_list3.get(0).get("PIC_PATH") == null ? "" : cha_list3.get(0).get("PIC_PATH").toString());//户主头像
				} else {
					obj.put("pic_path", "");//户主头像
				}

				json.add(obj);
			}
			response.getWriter().write("{\"success\":0,\"message\":\"1\",\"data\":"+json.toString()+"}");
		}else {
			response.getWriter().write("{\"success\":1,\"message\":\"没有该贫困户\",\"data\":\"\"}");//该帮扶人没有贫困户
		}
	}
	
	/**
	 * 查看走访记录情况（公用）
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getSaveVisit.do")
	public void getSaveVisit(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String  personal_name = request.getParameter("personal_name");//帮扶人姓名
		String personal_phone = request.getParameter("personal_phone");//帮扶人电话
		String household_name = request.getParameter("household_name");//贫困户姓名
		String household_cord = request.getParameter("household_cord");//贫困人证件号码
		
		String sql = "";
		
		if ("".equals(personal_name) || personal_name == null ) {
			
			sql = " select REGISTERTIME,v3,address,pic_path,random_number,aa.household_name,aa.household_card,personal_name,REGISTERTYPE,ZFTYPE　from "+
					"(select RANDOM_NUMBER nn, personal_name,household_name,household_card,personal_phone from DA_HELP_VISIT where "+
					" household_name='"+household_name+"' and household_card='"+household_cord+"')aa LEFT JOIN ( "+
					"select REGISTERTIME,v3,ADDRESS,wmsys.wm_concat(PIC_PATH) PIC_PATH,a.RANDOM_number,household_name,household_card,REGISTERTYPE,ZFTYPE from "+
					"  (select * from DA_HELP_VISIT where household_name='"+household_name+"' AND household_card='"+household_cord+"') a left join "+
					" (select * from DA_PIC_VISIT) b on a.RANDOM_NUMBER = b.RANDOM_NUMBER GROUP BY REGISTERTIME,v3,ADDRESS,a.RANDOM_number, "+
					" household_name,household_card,REGISTERTYPE,ZFTYPE  )bb on aa.nn=bb.RANDOM_NUMBER ORDER BY REGISTERTIME DESC";
			
		} else if ("".equals(household_name) || household_name == null ) {
			
			sql = "select REGISTERTIME,v3,address,pic_path,random_number,aa.household_name,aa.household_card,personal_name,REGISTERTYPE,ZFTYPE　from "+
					"(select RANDOM_NUMBER nn,  personal_name,household_name,household_card,personal_phone from DA_HELP_VISIT where "+
					"  personal_name='"+personal_name+"' and personal_phone='"+personal_phone+"')aa LEFT JOIN ( "+
					"select REGISTERTIME,v3,ADDRESS,wmsys.wm_concat(PIC_PATH) PIC_PATH,a.RANDOM_number,household_name,household_card,REGISTERTYPE,ZFTYPE from "+
					"  (select * from DA_HELP_VISIT where PERSONAL_NAME='"+personal_name+"' AND PERSONAL_PHONE='"+personal_phone+"') a left join "+
					" (select * from DA_PIC_VISIT) b on a.RANDOM_NUMBER = b.RANDOM_NUMBER GROUP BY REGISTERTIME,v3,ADDRESS,a.RANDOM_number, "+
					" household_name,household_card,REGISTERTYPE,ZFTYPE  )bb on aa.nn=bb.RANDOM_NUMBER ORDER BY REGISTERTIME DESC";
		} 
		JSONArray jsonArray = new JSONArray();
		try {
			List<Map> list = this.getBySqlMapper.findRecords(sql);
			for ( int i = 0 ; i < list.size() ; i ++ ) {
//				System.out.println(list.get(i).get("REGISTERTIME")+"---"+list.get(i).get("V3")+"---"+list.get(i).get("PIC_PATH")+"---"+list.get(i).get("ADDRESS")+"---"+list.get(i).get("PERSONAL_NAME")+"---"+list.get(i).get("HOUSEHOLD_NAME"));
				JSONObject obj = new JSONObject ();
				obj.put("b", "".equals(list.get(i).get("REGISTERTIME")) || list.get(i).get("REGISTERTIME") == null ? "" : list.get(i).get("REGISTERTIME").toString());//走访时间
				obj.put("c","".equals(list.get(i).get("V3")) || list.get(i).get("V3") == null ? "" : list.get(i).get("V3").toString());//走访情况记录
				obj.put("d","".equals(list.get(i).get("PIC_PATH")) || list.get(i).get("PIC_PATH") == null ? "" : list.get(i).get("PIC_PATH").toString());//走访情况图片
				obj.put("f", "".equals(list.get(i).get("ADDRESS")) || list.get(i).get("ADDRESS") == null ? "" : list.get(i).get("ADDRESS").toString());//地址
				obj.put("e", "".equals(list.get(i).get("PERSONAL_NAME")) || list.get(i).get("PERSONAL_NAME") == null ? "" : list.get(i).get("PERSONAL_NAME").toString());//帮扶干部名称
				obj.put("v6", "".equals(list.get(i).get("HOUSEHOLD_NAME")) || list.get(i).get("HOUSEHOLD_NAME") == null ? "" : list.get(i).get("HOUSEHOLD_NAME").toString());
				obj.put("t", "".equals(list.get(i).get("ZFTYPE")) || list.get(i).get("ZFTYPE") == null ? "" : list.get(i).get("ZFTYPE").toString());
				jsonArray.add(obj);
			}
			response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"data\":"+jsonArray.toString()+"}");
		} catch (Exception e) {
			response.getWriter().write("{\"success\":\"1\",\"message\":\"失败\",\"data\":\"\"}");
		}
		
	}
	/**
	 * 添加走访情况2.02（app第二步）
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getAddVisitController_2.do")
	public void getAddVisitController_2(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String random_number = request.getParameter("random_number");//随机数
		String registerTime = request.getParameter("registerTime");//签到时间
		String sendLat = request.getParameter("sendLat");//上传维度
		String sendLng = request.getParameter("sendLng");//上传经度
		String registerType  = request.getParameter("registerType");//签到类型
		/*1、其他帮扶活动
		2、了解基本情况
		3、填写扶贫手册
		4、制定脱贫计划
		5、落实资金项目
		6、宣传扶贫政策
		7、节日假日慰问*/
		String zfType = request.getParameter("zfType");//走访类型
		if(zfType!=null&&!"".equals(zfType)){
		}else{
			zfType="1";
		}
		
		String AAR008 = request.getParameter("AAR008");//村行政编码
		String personal_name = request.getParameter("personal_name");//帮扶人姓名
		String personal_phone = request.getParameter("personal_phone");//帮扶人电话
		String household_name = request.getParameter("household_name");//贫苦户姓名
		String household_card = request.getParameter("household_card");//贫苦户证件号码
		String lng = request.getParameter("lng");//经度
		String lat = request.getParameter("lat");//维度
		String address = request.getParameter("address");//地点
		String v3=request.getParameter("record");//走访情况记录-
		if ("".equals(random_number) || "null".equals(random_number) || random_number == null ) {
			response.getWriter().write("{\"success\":\"1\",\"message\":\"失败\",\"data\":\"\"}");
		} else {
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
			SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
//	        String random_number = sf.format(date)+"_"+new Random().nextInt(1000);//时间戳+随机数
			String insert_sql = "insert into DA_HELP_VISIT (household_name,personal_name,v1,v3,lng,lat,address,household_card,personal_phone,random_number,AAR008,REGISTERTIME,SENDLAT,SENDLNG,REGISTERTYPE,TYPE,ZFTYPE)"+
					" values ('"+household_name+"','"+personal_name+"','"+simpleDate.format(new Date())+"','"+v3+"','"+lng+"','"+lat+"','"+address+"','"+household_card+"','"+personal_phone+"','"+random_number+"','"+AAR008+"','"+registerTime+"','"+sendLat+"','"+sendLng+"','"+registerType+"','手机APP','"+zfType+"')";
			try {
				this.getBySqlMapper.findRecords(insert_sql);
				response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"data\":{\"random_number\":\""+random_number+"\"}}");
			} catch (Exception e) {
				response.getWriter().write("{\"success\":\"1\",\"message\":\"失败\",\"data\":\"\"}");
				response.getWriter().close();
			}
		}
		
		
	}
	/**
	 * 上传走访记录图片2.02（app第一步）
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getAddZfPhoto_2.do")
	public void getAddZfPhoto_2(@RequestParam("image") MultipartFile file,HttpServletRequest request,HttpServletResponse response) throws IOException{
		String random_number = request.getParameter("random_number");//随机数
		String  size = request.getParameter("size");//图片大小
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
		String pic = sf.format(date)+"_"+new Random().nextInt(1000);
		if (!file.isEmpty()) {
			// 文件保存目录路径
			String savePath = "E:/attached/2/";
			// 文件保存目录URL 
			String saveUrl1 = request.getContextPath() + "/attached/2/";
			String saveUrl = saveUrl1.replaceAll("assaWeChatApp", "assa");
			
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
			try {
				File uploadedFile = new File(savePath, pic+"."+fileExt);
				byte[] bytes = file.getBytes();  
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));  
				stream.write(bytes);  
				stream.close(); 
				
               File f= new File(savePath+pic+"."+fileExt); 
   				if (f.exists() && f.isFile()){ 
   					if(f.length() > 10240 ){//数据库存照片地址
   						String sql="INSERT INTO DA_PIC_VISIT (random_number,PIC_PATH) VALUES"+
   								"('"+random_number+"','"+saveUrl+pic+"."+fileExt+"')";
   						int insert_num1 = this.getBySqlMapper.insert(sql);
   						response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"data\":\"\"}");
   					}else {
   						response.getWriter().write("{\"success\":\"1\",\"message\":\"图片损坏\",\"data\":\"\"}");
   					}
   				}else {
   					response.getWriter().write("{\"success\":\"1\",\"message\":\"图片不存在\",\"data\":\"\"}");
   				}
			} catch (Exception e) {  
				response.getWriter().write("{\"success\":\"1\",\"message\":\"失败\",\"data\":\"\"}");
			}
		} else {  
			response.getWriter().write("{\"success\":\"1\",\"message\":\"没有图片\",\"data\":\"\"}");
		}
	}
	/**
	 * 检测app版本是否进行更新
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("getSaveVersion.do")
	public void getSaveVersion(HttpServletRequest request,HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String v=request.getParameter("v");//版本
		String sql="SELECT * FROM APP_VERSION WHERE PKID=1";
		List<Map> list=this.getBySqlMapper.findRecords(sql);
		if(list.size()>0){
			String version=(String) list.get(0).get("VERSION");
			if(v.toString().equals(version)){
				response.getWriter().write("{\"success\":\"0\",\"message\":\"不需要更新\",\"data\":{\"version\":\""+list.get(0).get("VERSION")+"\"}}");
			}else{
				String url=(String) list.get(0).get("APP_PATH");
				response.getWriter().write("{\"success\":\"0\",\"message\":\"需要更新\",\"data\":{\"version\":\""+list.get(0).get("VERSION")+"\",\"url\":\""+url+"\"}}");
			}
		}
	}
	/**
	 * 上传用户头像（app)
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
		String AAB001 = request.getParameter("AAB001");//贫困人口编号
		String household_name = request.getParameter("household_name");//贫困户名称
		String household_card = request.getParameter("household_card");//贫困户证件号码
		
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
		String v1=simpleDate.format(new Date());
		String img1=request.getParameter("img");//图片名称
		String img = img1.replaceAll("/", "");
		String type=request.getParameter("type");//户主还是家庭成员
		SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd"); 
		try {//户主  	
			String cha_sql="SELECT * FROM DA_PIC_HOUSEHOLD WHERE AAB001="+AAB001+"";
			List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
			String Url = "/assa/attached/"+type+"/"+dfs.format(new Date())+"/";
			if(null==cha_list||cha_list.size()==0){
				String sql="INSERT INTO DA_PIC_HOUSEHOLD (AAB001,HOUSEHOLD_NAME,HOUSEHOLD_CARD,PIC_PATH) VALUES ('"+AAB001+"','"+household_name+"','"+household_card+"','"+Url+img+".jpg')";
				this.getBySqlMapper.insert(sql);
			}else{
				String sql="UPDATE DA_PIC_HOUSEHOLD SET PIC_PATH='"+Url+img+".jpg'  WHERE AAB001='"+AAB001+"' and HOUSEHOLD_NAME='"+household_name+"' and HOUSEHOLD_CARD='"+household_card+"' ";
				this.getBySqlMapper.update(sql);
			}
			response.getWriter().write("{\"isError\":\"0\",\"result\":\"\"}");
		} catch (Exception e) {
			response.getWriter().write("{\"isError\":\"1\",\"result\":\"\"}");
		}
		if (!file.isEmpty()) {
			// 文件保存目录路径 
			String savePath = "E:/attached/"+type+"/";                                      
//	        String savePath = savePath1.replaceAll("assaWeChatApp", "assa Maven Webapp");
	        // 文件保存目录URL  
//	        String saveUrl = request.getContextPath() + "/attached/"+type+"/";
	        
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
//            saveUrl += ymd + "/";
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
	 * 根据帮扶人查相应的贫困户（公用select)下拉框
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getPoorName.do")
	public void getPoorName(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String personal_name = request.getParameter("name");//帮扶人姓名
		String personal_phone = request.getParameter("phone");//帮扶人电话
		JSONArray json = new JSONArray();
		String  sql = "select household_name,household_card from SYS_PERSONAL_HOUSEHOLD_MANY  where PERSONAL_NAME = '"+personal_name+"' "+
						"AND PERSONAL_PHONE='"+personal_phone+"' and HOUSEHOLD_CARD is NOT null GROUP BY household_name,household_card ";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		if( list.size() > 0){
			for ( int i = 0 ; i < list.size() ; i ++){
				JSONObject obj = new JSONObject();
				obj.put("pkid","".equals(list.get(i).get("HOUSEHOLD_CARD")) || list.get(i).get("HOUSEHOLD_CARD")==null?"": list.get(i).get("HOUSEHOLD_CARD").toString());
				obj.put("v6","".equals(list.get(i).get("HOUSEHOLD_NAME")) || list.get(i).get("HOUSEHOLD_NAME")==null?"": list.get(i).get("HOUSEHOLD_NAME").toString());
				json.add(obj);
			}
			response.getWriter().write(json.toString());
		}else{
			response.getWriter().write("0");
		}
	}
	
	
	
	/*****************************************微信跟照片有关的***********************************************************************/
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
		
		
		//1、获取AccessToken  
	   String  accessToken = WeChatApp.tokenn;  
	      
	    //2、获取Ticket  
	   String  jsapi_ticket = WeChatApp.ticket;  
	      
	    //3、时间戳和随机字符串  
	    String noncestr = WeChatApp.f_noncestr;//随机字符串  
	    String timestamp = WeChatApp.f_timestamp;//时间戳  
	      
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
	    obj.put("token", accessToken);//
//	    obj.put("token", map.get("access_token").toString());//
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
	 * 添加走访记录（微信）
	 * @param request
	 * @param response
	 * @throws DigestException
	 * @throws IOException
	 */
	@RequestMapping("addZfjl.do")
	public void addZfjl(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String registerTime = request.getParameter("registerTime");//签到时间
		String sendLat = request.getParameter("sendLat");//上传维度
		String sendLng = request.getParameter("sendLng");//上传经度
		String registerType  = request.getParameter("registerType");//签到类型
		
		String household_name = request.getParameter("household_name");//贫困户的姓名
		String household_card = request.getParameter("household_card");//贫困户证件号码
		String personal_name = request.getParameter("personal_name");//帮扶人姓名
		String personal_phone = request.getParameter("personal_phone");//帮扶人电话
		String zfjl = request.getParameter("zfjl");//走访记录
		String latitude = request.getParameter("latitude");//维度
		String longitude = request.getParameter("longitude");//经度
		String[] photo = request.getParameterValues("photo");//图片
		/*1、其他帮扶活动
		2、了解基本情况
		3、填写扶贫手册
		4、制定脱贫计划
		5、落实资金项目
		6、宣传扶贫政策
		7、节日假日慰问*/
		String zfType = request.getParameter("zfType");//走访类型
		
		Date date = new Date();
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
	    String random_number = sf.format(date)+"_"+new Random().nextInt(1000);//时间戳+随机数
	    String AAR008 ="";//村的行政区划代码
	    
	    List cun_list = new ArrayList();
	    
	    try {
	    	if (photo.length>0 && photo[0]!=""){
				String saveUrl1 = request.getContextPath() + "/attached/2/";
				String savePath = "E:/attached/2/";
				String saveUrl = saveUrl1.replaceAll("assaWeChatApp", "assa");
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
					
					File f= new File(savePath+res); 
					File f1 = new File (photo[i]);
					if (f.exists() && f.isFile()){ 
//						if(f.length() == f1.length()){//判断两个文件的大小是否相等
						if(f.length() >10240){//判断两个文件的大小是否相等
							//相等添给list添加地址名称
							cun_list.add(saveUrl+res);
						}else {
							response.getWriter().write("0");
							return ;
						}
					}
		            
				}
//				System.out.println("###########"+cun_list.size());
				for (int a =0; a < cun_list.size(); a++ ) {
					//储存照片地址
		            String sql="INSERT INTO DA_PIC_VISIT (RANDOM_NUMBER,PIC_PATH)"+
		    				" VALUES('"+random_number+"','"+cun_list.get(a)+"')";
		            int insert_photo = this.getBySqlMapper.insert(sql);
				}
			}
	    	//查询行政编码
		    String cha_sql = "select AAR008 from (select AAC001,max(AAR040) nian from NEIMENG0117_AB01 where AAB002='"+household_name+"' AND AAB004='"+household_card+"' group by AAC001) "+
		    				"  a left join (select AAR008,AAC001,AAR040 from NEIMENG0117_AC01 ) b on a.AAC001=b.AAC001 and a.nian=b.AAR040";
		    List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
		    if ( cha_list.size() > 0 ) {
		    	AAR008 = cha_list.get(0).get("AAR008").toString();
		    }
		    //添加走访记录文字信息
			String hql = "INSERT INTO DA_HELP_VISIT(HOUSEHOLD_NAME,PERSONAL_NAME,V1,V3,LNG,LAT,HOUSEHOLD_CARD,PERSONAL_PHONE,RANDOM_NUMBER,AAR008,REGISTERTIME,SENDLAT,SENDLNG,REGISTERTYPE,TYPE,ZFTYPE)"+
					" VALUES('"+household_name+"','"+personal_name+"','"+simpleDate.format(new Date())+"','"+zfjl+"','"+longitude+"','"+latitude+"','"+household_card+"','"+personal_phone+"','"+random_number+"','"+AAR008+"','"+registerTime+"','"+sendLat+"','"+sendLng+"','"+registerType+"','微信','"+zfType+"')";
			int insert_num = this.getBySqlMapper.insert(hql);
			response.getWriter().write("5");
		} catch (Exception e) {
			response.getWriter().write("0");
		}
	    
	   
	}
	/**
	 * 添加户主照片（微信）
	 * @param request
	 * @param response
	 * @throws DigestException
	 * @throws IOException
	 */
	@RequestMapping("getAdd_jttx.do")
	public void getAdd_jttx(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String household_name = request.getParameter("household_name");//贫困户姓名
		String household_card = request.getParameter("household_card");//贫困户编号
		String AAB001 = request.getParameter("AAB001");//贫苦户编号
		String photo = request.getParameter("photo");//照片
		String type = request.getParameter("type");//类型
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd"); 
		String saveUrl1 = request.getContextPath() + "/attached/"+type+"/";
		String savePath = "E:/attached/"+type+"/";
		String saveUrl = saveUrl1.replaceAll("assaWeChatApp", "assa");
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
		try {
			String res = downloadFromUrl(photo, savePath,name);
			
			File f= new File(savePath);  
		    if (f.exists() && f.isFile()){
		    	if ( f.length() > 0 ){
		    		 String sql="INSERT INTO DA_PIC_HOUSEHOLD (AAB001,HOUSEHOLD_NAME,HOUSEHOLD_CARD,PIC_PATH)"+
		 					" VALUES('"+AAB001+"','"+household_name+"','"+household_card+"','"+saveUrl+res+"')";
		 	        int insert_photo = this.getBySqlMapper.insert(sql);
		 		
		 			response.getWriter().write("5");
		    	}
		    }
		} catch (Exception e) {
			response.getWriter().write("0");
		}
		
		
      
	
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
	 * 微信,app 修改密码(公用）
	 * @param request
	 * @param response
	 * @throws NoSuchAlgorithmException 
	 * @throws DigestException
	 * @throws IOException
	 */
	@RequestMapping("getUpdatePassword.do")
	public void getUpdatePassword(HttpServletRequest request,HttpServletResponse response) throws NoSuchAlgorithmException,IOException {
		String  personal_name = request.getParameter("name");//帮扶人姓名
		String personal_phone = request.getParameter("phone");//帮扶人电话
		String old_password = request.getParameter("old_password");//旧密码
		String new_password = request.getParameter("new_password");//新密码
		String sql = "select * from SYS_PERSONAL_HOUSEHOLD_MANY where PERSONAL_NAME ='"+personal_name+"' and PERSONAL_PHONE='"+personal_phone+"'";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		if ("".equals(list.get(0).get("PASSWORD")) || list.get(0).get("PASSWORD") == null ) {
			if (old_password.equals(personal_phone.substring(5,11))) {
				String  update_sql = "update SYS_PERSONAL_HOUSEHOLD_MANY set PASSWORD = '"+new_password+"' where PERSONAL_NAME='"+personal_name+"' and PERSONAL_PHONE='"+personal_phone+"'";
				this.getBySqlMapper.update(update_sql);
				response.getWriter().write("5");
			} else {
				response.getWriter().write("0");
			}
		} else if ( !"".equals(list.get(0).get("PASSWORD")) && list.get(0).get("PASSWORD") != null) {
			if ( old_password.equals(list.get(0).get("PASSWORD"))) {
				String  update_sql = "update SYS_PERSONAL_HOUSEHOLD_MANY set PASSWORD = '"+new_password+"' where PERSONAL_NAME='"+personal_name+"' and PERSONAL_PHONE='"+personal_phone+"'";
				this.getBySqlMapper.update(update_sql);
				response.getWriter().write("5");
			} else {
				response.getWriter().write("0");
			}
		}
	}
	
	/*帮扶通领导版本第二版接口--扶贫对象*/
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--1.贫困概况
	 * @throws IOException
	 */
	@RequestMapping("getFpdx1.do")
	public void getFpdx1(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    int pkrk=0;
	    int pkh=0;
	    int pkc=0;
	    int ybpkh=0;
	    int dbpkh=0;
	    int wbpkh=0;
	    //查询贫困人口
	    String sqlPkr = "";
	    //查询贫困户
	    String sqlPkh = "";
	    //查询贫困村
	    String sqlPkc = "";
	    
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	//贫困人口
	    	sqlPkr = "SELECT a2.com_name as V1,a1.V2,a2.com_level FROM("
					+ "SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=("
					+ "SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='" + name + "' and rownum = 1 "
					+ ")) a2 LEFT JOIN (select * from PKC_1_1_0 where V9=0 ) a1 ON a1.V10=A2.COM_CODE ";
	    	//贫困户
	    	sqlPkh="SELECT a2.com_name,a1.z_hu,a1.z_ren,a1.v1,a1.v5,a1.v9  FROM("
					+ "SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=("
					+ "SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"' and rownum = 1 "
					+ ")) a2 LEFT JOIN (SELECT * FROM PKC_1_2_1  WHERE  TYPE = '0') a1 ON a1.COM_CODE=A2.COM_CODE  ";
	    	//贫困村
	    	sqlPkc = "select b.com_name as v1,v3 from  (select * from SYS_COMPANY where COM_F_PKID=(SELECT PKID from SYS_COMPANY where COM_NAME='"+name+"' and rownum = 1 ) ) "+
 					"b left join (select * from PKC_1_3_1 WHERE  COM_PIN = '0') c on c.V10=b.COM_CODE ";
	    }else{//如果都为空则查询自治区统计数据
	    	name="内蒙古自治区";
	    	//贫困人口
	    	sqlPkr = "SELECT a2.com_name as V1,a1.V2,a2.com_level FROM("
					+ "SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=("
					+ "SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区'"
					+ ")) a2 LEFT JOIN (select * from PKC_1_1_0 where V9=0 ) a1 ON a1.V10=A2.COM_CODE";
	    	//贫困户
	    	sqlPkh="SELECT a2.com_name,a1.z_hu,a1.z_ren,a1.v1,a1.v5,a1.v9  FROM("
					+ "SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=("
					+ "SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区'"
					+ ")) a2 LEFT JOIN (SELECT * FROM PKC_1_2_1  WHERE  TYPE = '0') a1 ON a1.COM_CODE=A2.COM_CODE ";
	    	//贫困村
	    	sqlPkc = "select b.com_name as v1,v3 from (select * from SYS_COMPANY where COM_F_PKID=(SELECT PKID from SYS_COMPANY where COM_NAME='内蒙古自治区') ) "+
 					"b left join (select * from PKC_1_3_1 WHERE  COM_PIN = '0') c  on c.V10=b.COM_CODE  ";
	    }
	    List<Map> listPkr = this.getBySqlMapper.findRecords(sqlPkr);
	    List<Map> listPkh = this.getBySqlMapper.findRecords(sqlPkh);
	    List<Map> listPkc = this.getBySqlMapper.findRecords(sqlPkc);
	    if(listPkr.size()>0){
			for (int i = 0; i < listPkr.size(); i++) {
				Map Pkr_map = listPkr.get(i);
				JSONObject obj = new JSONObject();
				for (Object key : Pkr_map.keySet()) {
					if(key.equals("COM_LEVEL")){
					}else{
						obj.put(key, Pkr_map.get(key));
					}
				}
				pkrk+=(listPkr.get(i).get("V2")!=null)&&(listPkr.get(i).get("V2")!="")?Integer.valueOf(listPkr.get(i).get("V2").toString()):0;//总贫困人口
				chartJson.add(obj);
			}
			if(Integer.valueOf(listPkr.get(0).get("COM_LEVEL").toString())-1==1){//静态获取全区统计数据
		    	jb.put("pkc", 2834);
				jb.put("pkh", 249506);
				jb.put("pkr", 555563);
				jb.put("ybpkh", 147081);
				jb.put("dbpkh", 92930);
				jb.put("wbpkh", 5684);
		    }else{//动态查询
		    	for (int i = 0; i < listPkh.size(); i++) {
		    		pkh+=(listPkh.get(i).get("Z_HU")!=null)&&(listPkh.get(i).get("Z_HU")!="")?Integer.valueOf(listPkh.get(i).get("Z_HU").toString()):0;
		    		ybpkh+=(listPkh.get(i).get("V1")!=null)&&(listPkh.get(i).get("V1")!="")?Integer.valueOf(listPkh.get(i).get("V1").toString()):0;
		    		dbpkh+=(listPkh.get(i).get("V5")!=null)&&(listPkh.get(i).get("V5")!="")?Integer.valueOf(listPkh.get(i).get("V5").toString()):0;
		    		wbpkh+=(listPkh.get(i).get("V9")!=null)&&(listPkh.get(i).get("V9")!="")?Integer.valueOf(listPkh.get(i).get("V9").toString()):0;
		    	}
		    	for (int j = 0; j < listPkc.size(); j++) {
		    		pkc+=(listPkc.get(j).get("V3")!=null)&&(listPkc.get(j).get("V3")!="")?Integer.valueOf(listPkc.get(j).get("V3").toString()):0;
		    	}
		    	jb.put("pkc", pkc);
				jb.put("pkh", pkh);
				jb.put("pkr", pkrk);
				jb.put("ybpkh", ybpkh);
				jb.put("dbpkh", dbpkh);
				jb.put("wbpkh", wbpkh);
		    }
			tjJson.add(jb);
			response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+getPaixu(chartJson, name).toString()+",\"tjSum\":"+tjJson+"}");
	    }else{//返回缺省值0
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--2.致贫原因
	 * @throws IOException
	 */
	@RequestMapping("getFpdx2.do")
	public void getFpdx2(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    int ybzp=0;//因病致贫
	    int qzj=0;//缺资金
	    int yxzp=0;//因学致贫
	    
	    int ybzpCh=0;//因病致贫
	    int yczpCh=0;//因残致贫
	    int yxzpCh=0;//因学致贫
	    int yzzpCh=0;//因灾致贫
	    int qtdCh=0;//缺土地
	    int qsCh=0;//缺水
	    int qjsCh=0;//缺技术
	    int qllCh=0;//缺劳力
	    int qzjCh=0;//缺资金
	    int jtlhCh=0;//交通条件落后
	    int zsfzCh=0;//自身发展力不足
	    String sqlZpyy = "";
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    JSONObject obj = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlZpyy="SELECT a2.com_name,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 )) a2 LEFT JOIN (select * from PKC_1_2_2  WHERE  TYPE = '0') a1 ON a1.COM_CODE=A2.COM_CODE ";
	    }else{
	    	sqlZpyy="SELECT a2.com_name,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN (select * from PKC_1_2_2  WHERE  TYPE = '0') a1 ON a1.COM_CODE=A2.COM_CODE  ";
	    }
	    List<Map> listZpyy = this.getBySqlMapper.findRecords(sqlZpyy);
	    if(listZpyy.size()>0){
	    	for (int i = 0; i < listZpyy.size(); i++) {
	    		Map Zpyy_map = listZpyy.get(i);
	    		
				/*obj.put("V0", Zpyy_map.get("COM_NAME"));//区域名
				obj.put("V1", Zpyy_map.get("V1"));//因病致贫
				obj.put("V2", Zpyy_map.get("V3"));//因残致贫
				obj.put("V3", Zpyy_map.get("V5"));//因学致贫
				obj.put("V4", Zpyy_map.get("V7"));//因灾致贫
				obj.put("V5", Zpyy_map.get("V9"));//缺土地
				obj.put("V6", Zpyy_map.get("V11"));//缺水
				obj.put("V7", Zpyy_map.get("V13"));//缺技术
				obj.put("V8", Zpyy_map.get("V15"));//缺劳力
				obj.put("V9", Zpyy_map.get("V17"));//缺资金
				obj.put("V10", Zpyy_map.get("V19"));//交通条件落后
				obj.put("V11", Zpyy_map.get("V21"));//自身发展力不足*/
				
				ybzpCh+=(Zpyy_map.get("V1")!=null)&&(Zpyy_map.get("V1")!="")?Integer.valueOf(Zpyy_map.get("V1").toString()):0;
				yczpCh+=(Zpyy_map.get("V3")!=null)&&(Zpyy_map.get("V3")!="")?Integer.valueOf(Zpyy_map.get("V3").toString()):0;
				yxzpCh+=(Zpyy_map.get("V5")!=null)&&(Zpyy_map.get("V5")!="")?Integer.valueOf(Zpyy_map.get("V5").toString()):0;
				yzzpCh+=(Zpyy_map.get("V7")!=null)&&(Zpyy_map.get("V7")!="")?Integer.valueOf(Zpyy_map.get("V7").toString()):0;
				qtdCh+=(Zpyy_map.get("V9")!=null)&&(Zpyy_map.get("V9")!="")?Integer.valueOf(Zpyy_map.get("V9").toString()):0;
				qsCh+=(Zpyy_map.get("V11")!=null)&&(Zpyy_map.get("V11")!="")?Integer.valueOf(Zpyy_map.get("V11").toString()):0;
				qjsCh+=(Zpyy_map.get("V13")!=null)&&(Zpyy_map.get("V13")!="")?Integer.valueOf(Zpyy_map.get("V13").toString()):0;
				qllCh+=(Zpyy_map.get("V15")!=null)&&(Zpyy_map.get("V15")!="")?Integer.valueOf(Zpyy_map.get("V15").toString()):0;
				qzjCh+=(Zpyy_map.get("V17")!=null)&&(Zpyy_map.get("V17")!="")?Integer.valueOf(Zpyy_map.get("V17").toString()):0;
				jtlhCh+=(Zpyy_map.get("V19")!=null)&&(Zpyy_map.get("V19")!="")?Integer.valueOf(Zpyy_map.get("V19").toString()):0;
				zsfzCh+=(Zpyy_map.get("V21")!=null)&&(Zpyy_map.get("V21")!="")?Integer.valueOf(Zpyy_map.get("V21").toString()):0;
				
				
				ybzp+=(Zpyy_map.get("V1")!=null)&&(Zpyy_map.get("V1")!="")?Integer.valueOf(Zpyy_map.get("V1").toString()):0;
				qzj+=(Zpyy_map.get("V17")!=null)&&(Zpyy_map.get("V17")!="")?Integer.valueOf(Zpyy_map.get("V17").toString()):0;
				yxzp+=(Zpyy_map.get("V5")!=null)&&(Zpyy_map.get("V5")!="")?Integer.valueOf(Zpyy_map.get("V5").toString()):0;
				
	    	}
	    	obj.put("V1", ybzpCh);//因病致贫
			obj.put("V2", yczpCh);//因残致贫
			obj.put("V3", yxzpCh);//因学致贫
			obj.put("V4", yzzpCh);//因灾致贫
			obj.put("V5", qtdCh);//缺土地
			obj.put("V6", qsCh);//缺水
			obj.put("V7", qjsCh);//缺技术
			obj.put("V8", qllCh);//缺劳力
			obj.put("V9", qzjCh);//缺资金
			obj.put("V10", jtlhCh);//交通条件落后
			obj.put("V11", zsfzCh);//自身发展力不足
	    	chartJson.add(obj);
	    	
	    	jb.put("ybzpTotal", ybzp);
	    	jb.put("qzjTotal", qzj);
	    	jb.put("yxzpTotal", yxzp);
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--3.年龄分组
	 * @throws IOException
	 */
	@RequestMapping("getFpdx3.do")
	public void getFpdx3(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    int ageOne=0;//16岁以下
	    int ageTwo=0;//16-30岁
	    int ageThree=0;//30-40岁
	    int ageFour=0;//40-50岁
	    int ageFive=0;//50-60岁
	    int ageSix=0;//60岁以上
	    String sqlAge = "";
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlAge="SELECT a2.com_name as v1,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 )) a2 LEFT JOIN (select * from PKC_1_1_1  WHERE  V9 = '0') a1 ON a1.V10=A2.COM_CODE  ";
	    }else{
	    	sqlAge="SELECT a2.com_name as v1,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN (select * from PKC_1_1_1  WHERE  V9 = '0') a1 ON a1.V10=A2.COM_CODE  ";
	    }
	    List<Map> listAge = this.getBySqlMapper.findRecords(sqlAge);
	    if(listAge.size()>0){
	    	for (int i = 0; i < listAge.size(); i++) {
	    		Map Age_map = listAge.get(i);
				JSONObject obj = new JSONObject();
				obj.put("V0", Age_map.get("V1"));//区域名
				obj.put("V1", (Age_map.get("V8")!=null)&&(Age_map.get("V8")!="")?Age_map.get("V8"):0);//60岁以上
				
				ageOne+=(Age_map.get("V3")!=null)&&(Age_map.get("V3")!="")?Integer.valueOf(Age_map.get("V3").toString()):0;
				ageTwo+=(Age_map.get("V4")!=null)&&(Age_map.get("V4")!="")?Integer.valueOf(Age_map.get("V4").toString()):0;
				ageThree+=(Age_map.get("V5")!=null)&&(Age_map.get("V5")!="")?Integer.valueOf(Age_map.get("V5").toString()):0;
				ageFour+=(Age_map.get("V6")!=null)&&(Age_map.get("V6")!="")?Integer.valueOf(Age_map.get("V6").toString()):0;
				ageFive+=(Age_map.get("V7")!=null)&&(Age_map.get("V7")!="")?Integer.valueOf(Age_map.get("V7").toString()):0;
				ageSix+=(Age_map.get("V8")!=null)&&(Age_map.get("V8")!="")?Integer.valueOf(Age_map.get("V8").toString()):0;
				chartJson.add(obj);
	    	}
	    	jb.put("ageOneTotal", ageOne);
	    	jb.put("ageTwoTotal", ageTwo);
	    	jb.put("ageThreeTotal", ageThree);
	    	jb.put("ageFourTotal", ageFour);
	    	jb.put("ageFiveTotal", ageFive);
	    	jb.put("ageSixTotal", ageSix);
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--5.健康状况
	 * @throws IOException
	 */
	@RequestMapping("getFpdx4.do")
	public void getFpdx4(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    int illC=0;//长期慢性病
	    int illD=0;//大病
	    int illCJ=0;//残疾
	    String sqlIll = "";
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlIll="SELECT  a2.com_name as v1,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 )) a2 LEFT JOIN (select * from PKC_1_1_2  WHERE  V9 = '0' ) a1 ON a1.V10=A2.COM_CODE ";
	    }else{
	    	sqlIll="SELECT  a2.com_name as v1,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN (select * from PKC_1_1_2  WHERE  V9 = '0' ) a1 ON a1.V10=A2.COM_CODE ";
	    }
	    List<Map> listIll = this.getBySqlMapper.findRecords(sqlIll);
	    if(listIll.size()>0){
	    	for (int i = 0; i < listIll.size(); i++) {
	    		Map Ill_map = listIll.get(i);
				JSONObject obj = new JSONObject();
				obj.put("V0", Ill_map.get("V1"));//区域名
				obj.put("V1", (Ill_map.get("V4")!=null)&&(Ill_map.get("V4")!="")?Ill_map.get("V4"):0);//长期慢性病
				
				illC+=(Ill_map.get("V4")!=null)&&(Ill_map.get("V4")!="")?Integer.valueOf(Ill_map.get("V4").toString()):0;
				illD+=(Ill_map.get("V5")!=null)&&(Ill_map.get("V5")!="")?Integer.valueOf(Ill_map.get("V5").toString()):0;
				illCJ+=(Ill_map.get("V6")!=null)&&(Ill_map.get("V6")!="")?Integer.valueOf(Ill_map.get("V6").toString()):0;
				chartJson.add(obj);
	    	}
	    	jb.put("illCTotal", illC);
	    	jb.put("illDTotal", illD);
	    	jb.put("illCJTotal", illCJ);
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--5.文化程度
	 * @throws IOException
	 */
	@RequestMapping("getFpdx5.do")
	public void getFpdx5(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    int eduOne=0;//文盲
	    int eduTwo=0;//小学文化程度
	    int eduThree=0;//初中文化程度
	    int eduFour=0;//高中文化程度
	    int eduFive=0;//大专以上文化程度
	    String sqlEdu = "";
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlEdu="SELECT a2.com_name as v1,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 )) a2 LEFT JOIN (select * from PKC_1_1_3  WHERE  V9 = '0') a1 ON a1.V10=A2.COM_CODE ";
	    }else{
	    	sqlEdu="SELECT a2.com_name as v1,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN (select * from PKC_1_1_3  WHERE  V9 = '0') a1 ON a1.V10=A2.COM_CODE ";
	    }
	    List<Map> listEdu = this.getBySqlMapper.findRecords(sqlEdu);
	    if(listEdu.size()>0){
	    	for (int i = 0; i < listEdu.size(); i++) {
	    		Map Edu_map = listEdu.get(i);
				JSONObject obj = new JSONObject();
				obj.put("V0", Edu_map.get("V1"));//区域名
				obj.put("V1", (Edu_map.get("V4")!=null)&&(Edu_map.get("V4")!="")?Edu_map.get("V4"):0);//文盲
				
				eduOne+=(Edu_map.get("V4")!=null)&&(Edu_map.get("V4")!="")?Integer.valueOf(Edu_map.get("V4").toString()):0;
				eduTwo+=(Edu_map.get("V5")!=null)&&(Edu_map.get("V5")!="")?Integer.valueOf(Edu_map.get("V5").toString()):0;
				eduThree+=(Edu_map.get("V6")!=null)&&(Edu_map.get("V6")!="")?Integer.valueOf(Edu_map.get("V6").toString()):0;
				eduFour+=(Edu_map.get("V7")!=null)&&(Edu_map.get("V7")!="")?Integer.valueOf(Edu_map.get("V7").toString()):0;
				eduFive+=(Edu_map.get("V8")!=null)&&(Edu_map.get("V8")!="")?Integer.valueOf(Edu_map.get("V8").toString()):0;
				chartJson.add(obj);
	    	}
	    	jb.put("eduOneTotal", eduOne);
	    	jb.put("eduTwoTotal", eduTwo);
	    	jb.put("eduThreeTotal", eduThree);
	    	jb.put("eduFourTotal", eduFour);
	    	jb.put("eduFiveTotal", eduFive);
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--6.	土地资源
	 * @throws IOException
	 */
	@RequestMapping("getFpdx6.do")
	public void getFpdx6(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    double landOne=0;//耕地面积
	    double landTwo=0;//有效灌溉面积
	    double landThree=0;//林地面积
	    double landFour=0;//退耕还林面积
	    double landFive=0;//林果面积
	    double landSix=0;//牧草地面积
	    String sqlLand = "";
//	    String sqlRjLand = "";//人均耕地面积
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlLand="SELECT a2.com_name as v01,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 )) a2 LEFT JOIN (select * from PKC_1_2_6  WHERE  TYPE = '0' ) a1 ON a1.COM_CODE=A2.COM_CODE   ";
//	    	sqlRjLand="SELECT a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"')) a2 LEFT JOIN PKC_1_2_6 a1 ON a1.COM_CODE=A2.COM_CODE  WHERE  TYPE = '0' ";
	    }else{
	    	sqlLand="SELECT a2.com_name as v01,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN (select * from PKC_1_2_6  WHERE  TYPE = '0' ) a1 ON a1.COM_CODE=A2.COM_CODE ";
//	    	sqlRjLand="SELECT a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN PKC_1_2_6 a1 ON a1.COM_CODE=A2.COM_CODE  WHERE  TYPE = '0' ";
	    }
	    List<Map> listLand = this.getBySqlMapper.findRecords(sqlLand);
//	    List<Map> listRjLand = this.getBySqlMapper.findRecords(sqlRjLand);
	    if(listLand.size()>0){
	    	for (int i = 0; i < listLand.size(); i++) {
//	    		Map RjLand_map = listRjLand.get(i);
	    		Map Land_map = listLand.get(i);
				JSONObject obj = new JSONObject();
				obj.put("V0", Land_map.get("V01"));//区域名
				obj.put("V1", (Land_map.get("V2")!=null)&&(Land_map.get("V2")!="")?Land_map.get("V2"):0);//人均耕地面积
				
				landOne+=(Land_map.get("V1")!=null)&&(Land_map.get("V1")!="")?Double.parseDouble(Land_map.get("V1").toString()):0;
				landTwo+=(Land_map.get("V3")!=null)&&(Land_map.get("V3")!="")?Double.valueOf(Land_map.get("V3").toString()):0;
				landThree+=(Land_map.get("V5")!=null)&&(Land_map.get("V5")!="")?Double.valueOf(Land_map.get("V5").toString()):0;
				landFour+=(Land_map.get("V7")!=null)&&(Land_map.get("V7")!="")?Double.valueOf(Land_map.get("V7").toString()):0;
				landFive+=(Land_map.get("V9")!=null)&&(Land_map.get("V9")!="")?Double.valueOf(Land_map.get("V9").toString()):0;
				landFive+=(Land_map.get("V11")!=null)&&(Land_map.get("V11")!="")?Double.valueOf(Land_map.get("V11").toString()):0;
				landSix+=(Land_map.get("V13")!=null)&&(Land_map.get("V13")!="")?Double.valueOf(Land_map.get("V13").toString()):0;
				
				chartJson.add(obj);
	    	}
	    	jb.put("landOneTotal", DateFormatUtil.formatDouble1(landOne));
	    	jb.put("landTwoTotal", DateFormatUtil.formatDouble1(landTwo));
	    	jb.put("landThreeTotal", DateFormatUtil.formatDouble1(landThree));
	    	jb.put("landFourTotal", DateFormatUtil.formatDouble1(landFour));
	    	jb.put("landFiveTotal", DateFormatUtil.formatDouble1(landFive));
	    	jb.put("landSixTotal", DateFormatUtil.formatDouble1(landSix));
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--7.	生产生活条件
	 * @throws IOException
	 */
	@RequestMapping("getFpdx7.do")
	public void getFpdx7(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    int proLifeOne=0;//饮水困难
	    int proLifeTwo=0;//无安全饮水
	    int proLifeThree=0;//未通生活用电
	    int proLifeFour=0;//未通广播电视
	    int proLifeFive=0;//住房是危房
	    int proLifeSix=0;//无卫生厕所
	    String sqlProLife = "";
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlProLife="SELECT a2.com_name as v01,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 )) a2 LEFT JOIN (select * from PKC_1_2_5  WHERE  TYPE = '0') a1 ON a1.COM_CODE=A2.COM_CODE";
	    }else{
	    	sqlProLife="SELECT a2.com_name as v01,a1.* FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN (select * from PKC_1_2_5  WHERE  TYPE = '0') a1 ON a1.COM_CODE=A2.COM_CODE ";
	    }
	    List<Map> listProLife = this.getBySqlMapper.findRecords(sqlProLife);
	    if(listProLife.size()>0){
	    	for (int i = 0; i < listProLife.size(); i++) {
	    		Map ProLife_map = listProLife.get(i);
				JSONObject obj = new JSONObject();
				obj.put("V0", ProLife_map.get("V01"));//区域名
				obj.put("V1", (ProLife_map.get("V13")!=null)&&(ProLife_map.get("V13")!="")?ProLife_map.get("V13"):0);//人均住房面积
				
				proLifeOne+=(ProLife_map.get("V1")!=null)&&(ProLife_map.get("V1")!="")?Integer.valueOf(ProLife_map.get("V1").toString()):0;
				proLifeTwo+=(ProLife_map.get("V3")!=null)&&(ProLife_map.get("V3")!="")?Integer.valueOf(ProLife_map.get("V3").toString()):0;
				proLifeThree+=(ProLife_map.get("V5")!=null)&&(ProLife_map.get("V5")!="")?Integer.valueOf(ProLife_map.get("V5").toString()):0;
				proLifeFour+=(ProLife_map.get("V7")!=null)&&(ProLife_map.get("V7")!="")?Integer.valueOf(ProLife_map.get("V7").toString()):0;
				proLifeFive+=(ProLife_map.get("V9")!=null)&&(ProLife_map.get("V9")!="")?Integer.valueOf(ProLife_map.get("V9").toString()):0;
				proLifeSix+=(ProLife_map.get("V11")!=null)&&(ProLife_map.get("V11")!="")?Integer.valueOf(ProLife_map.get("V11").toString()):0;
				
				chartJson.add(obj);
	    	}
	    	jb.put("proLifeOneTotal", proLifeOne);
	    	jb.put("proLifeTwoTotal", proLifeTwo);
	    	jb.put("proLifeThreeTotal", proLifeThree);
	    	jb.put("proLifeFourTotal", proLifeFour);
	    	jb.put("proLifeFiveTotal", proLifeFive);
	    	jb.put("proLifeSixTotal", proLifeSix);
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--8.	适龄教育
	 * @throws IOException
	 */
	@RequestMapping("getFpdx8.do")
	public void getFpdx8(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    double ageEduOne=0;//3-6岁
	    double ageEduTwo=0;//6-15岁
	    double ageEduThree=0;//15-18岁
	    double ageEduFour=0;//18-22岁
	    String sqlAgeEdu = "";
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlAgeEdu="SELECT a2.com_name as v01,a1.*,GJZDQX,ZZQZDQX,GMLQQX,MYQX,BJQX FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 )) a2 LEFT JOIN (select * from PKC_1_1_9 WHERE  V9 = '0' ) a1 ON a1.V10=A2.COM_CODE ";
	    }else{
	    	sqlAgeEdu="SELECT a2.com_name as v01,a1.*,GJZDQX,ZZQZDQX,GMLQQX,MYQX,BJQX FROM(SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=(SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='内蒙古自治区')) a2 LEFT JOIN (select * from PKC_1_1_9 WHERE  V9 = '0' ) a1 ON a1.V10=A2.COM_CODE ";
	    }
	    List<Map> listAgeEdu = this.getBySqlMapper.findRecords(sqlAgeEdu);
	    if(listAgeEdu.size()>0){
	    	for (int i = 0; i < listAgeEdu.size(); i++) {
	    		Map AgeEdu_map = listAgeEdu.get(i);
				JSONObject obj = new JSONObject();
				obj.put("V0", AgeEdu_map.get("V01"));//区域名
				obj.put("V1", (AgeEdu_map.get("V4")!=null)&&(AgeEdu_map.get("V4")!="")?AgeEdu_map.get("V4").toString():0);//6-15岁
				
				ageEduOne+=(AgeEdu_map.get("V3")!=null)&&(AgeEdu_map.get("V3")!="")?Integer.valueOf(AgeEdu_map.get("V3").toString()):0;
				ageEduTwo+=(AgeEdu_map.get("V4")!=null)&&(AgeEdu_map.get("V4")!="")?Integer.valueOf(AgeEdu_map.get("V4").toString()):0;
				ageEduThree+=(AgeEdu_map.get("V5")!=null)&&(AgeEdu_map.get("V5")!="")?Integer.valueOf(AgeEdu_map.get("V5").toString()):0;
				ageEduFour+=(AgeEdu_map.get("V6")!=null)&&(AgeEdu_map.get("V6")!="")?Integer.valueOf(AgeEdu_map.get("V6").toString()):0;
				
				chartJson.add(obj);
	    	}
	    	jb.put("ageEduOneTotal", ageEduOne);
	    	jb.put("ageEduTwoTotal", ageEduTwo);
	    	jb.put("ageEduThreeTotal", ageEduThree);
	    	jb.put("ageEduFourTotal", ageEduFour);
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--9.	贫困发生率
	 * @throws IOException
	 */
	@RequestMapping("getFpdx9.do")
	public void getFpdx9(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");//行政区划名称
	    int fslZhs=0;//总户数
	    int fslZrs=0;//总人数
	    int fslChs=0;//贫困村户数
	    int fslCrs=0;//贫困村人数
	    int fslFchs=0;//非贫困村户数
	    int fslFcrs=0;//非贫困村人数
	    String sqlFsl = "";
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	sqlFsl="select b.com_name as vf1,vf2,vf3,vf4,NVL(vf41, '0') vf41,vf5,(vf2-vf4) as vf6,(vf3-vf5) as vf7 from (select * from SYS_COMPANY where COM_F_PKID=(SELECT PKID from SYS_COMPANY where COM_NAME='"+name+"'  and rownum = 1 ) ) b left join  (select * from PKC_1_3_3 WHERE  COM_PIN = '0') c   on c.v10=b.COM_CODE ";
	    }else{
	    	sqlFsl="select b.com_name as vf1,vf2,vf3,vf4,NVL(vf41, '0') vf41,vf5,(vf2-vf4) as vf6,(vf3-vf5) as vf7 from  (select * from SYS_COMPANY where COM_F_PKID=(SELECT PKID from SYS_COMPANY where COM_NAME='内蒙古自治区') ) b left join (select * from PKC_1_3_3 WHERE  COM_PIN = '0') c  on c.v10=b.COM_CODE  ";
	    }
	    List<Map> listFsl = this.getBySqlMapper.findRecords(sqlFsl);
	    if(listFsl.size()>0){
	    	for (int i = 0; i < listFsl.size(); i++) {
	    		Map Fsl_map = listFsl.get(i);
				JSONObject obj = new JSONObject();
				obj.put("V0", Fsl_map.get("VF1"));//区域名
				obj.put("V1", (Fsl_map.get("VF41")!=null)&&(Fsl_map.get("VF41")!="")?Fsl_map.get("VF41").toString():0);//长期慢性病
				
				fslZhs+=(Fsl_map.get("VF2")!=null)&&(Fsl_map.get("VF2")!="")?Integer.valueOf(Fsl_map.get("VF2").toString()):0;
				fslZrs+=(Fsl_map.get("VF3")!=null)&&(Fsl_map.get("VF3")!="")?Integer.valueOf(Fsl_map.get("VF3").toString()):0;
				fslChs+=(Fsl_map.get("VF4")!=null)&&(Fsl_map.get("VF4")!="")?Integer.valueOf(Fsl_map.get("VF4").toString()):0;
				fslCrs+=(Fsl_map.get("VF5")!=null)&&(Fsl_map.get("VF5")!="")?Integer.valueOf(Fsl_map.get("VF5").toString()):0;
				fslFchs+=(Fsl_map.get("VF6")!=null)&&(Fsl_map.get("VF6")!="")?Integer.valueOf(Fsl_map.get("VF6").toString()):0;
				fslFcrs+=(Fsl_map.get("VF7")!=null)&&(Fsl_map.get("VF7")!="")?Integer.valueOf(Fsl_map.get("VF7").toString()):0;
				chartJson.add(obj);
	    	}
	    	//贫困人口
	    	String sqlPkr = "SELECT a2.com_name as V1,a1.V2,a2.com_level FROM("
					+ "SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=("
					+ "SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='" + name + "'"
					+ ")) a2 LEFT JOIN (select * from PKC_1_1_0 where V9=0 ) a1 ON a1.V10=A2.COM_CODE ";
	    	List<Map> listPkr = this.getBySqlMapper.findRecords(sqlPkr);
	    	if(Integer.valueOf(listPkr.get(0).get("COM_LEVEL").toString())-1==1){//静态获取全区统计数据
	    		jb.put("fslZhsTotal", 249506);
		    	jb.put("fslZrsTotal", 555563);
	    	}else{
	    		jb.put("fslZhsTotal", fslZhs);
		    	jb.put("fslZrsTotal", fslZrs);
	    	}
	    	
	    	jb.put("fslChsTotal", fslChs);
	    	jb.put("fslCrsTotal", fslCrs);
	    	jb.put("fslFchsTotal", fslFchs);
	    	jb.put("fslFcrsTotal", fslFcrs);
	    	tjJson.add(jb);
	    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    }else{
	    	response.getWriter().write("0");
	    }
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--10.	帮扶概况
	 * @throws IOException
	 * 
	 * 帮扶单位、驻村工作队、驻村工作干部、落实帮扶责任人（分行政化区划）、帮扶户数
	 */
	@RequestMapping("getFpdx10.do")
	public void getFpdx10(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String code = request.getParameter("code");
		String level = request.getParameter("level");//2省 3市 4 县 5乡 6村
		
	    
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    
	    //省级固定数据
	    int bfdw = 6099;
	    int zcgzd = 3159;
	    int zcgzgb = 14134;
	    int lsbfzrr = 152041;
	    int bfhs = 249506;
	    
	    
	    String dw_sql="";
	    String zcd_sql="";
	    String zcgb_sql="";
	    String sql = "";
	    
	    List<Map> dw_list;
	    List<Map> zcd_list;
	    List<Map> zcgb_list;
	    List<Map> list;
	    
	    String str[] = { "呼和浩特市", "包头市", "呼伦贝尔市", "兴安盟", "通辽市", "赤峰市",
				"锡林郭勒盟", "乌兰察布市", "鄂尔多斯市", "巴彦淖尔市", "乌海市", "阿拉善盟" };
	    String val[] = { "25359", "4470", "14575", "19235", "5411", "39080",
				"3510", "25345", "4508", "8826", "606", "1148" };
	    //判断参数是否为空
	    if(code!=null&&!"".equals(code)&&level!=null&&!"".equals(level)){
	    	if(Integer.valueOf(level)>1){//省级以下
	    		level=Integer.valueOf(level)+1+"";
	    		int v1 = Integer.parseInt(level)+1;
	    		int v3 = Integer.parseInt(level)*2;
	    		int v2 = v3 -1;
	    		bfdw = 0;
	    	    zcgzd = 0;
	    	    zcgzgb = 0;
	    	    lsbfzrr = 0;
	    	    bfhs = 0;
	    		//帮扶单位数
				dw_sql = "select count(*) num  from (select AP110 from  (select AAR008 from NEIMENG0117_AC01  where AAR100= '1' and AAR040='2016' and AAR00"+level+" ='"+code+"' GROUP BY AAR008  ) a "+
								" LEFT JOIN ( select AAD001,AAP110 from AD07 ) b on a.AAR008=b.AAD001  left join ( "+
								" select AAP110 Ap110,AAP001 from AP11)c ON b.AAP110=c.AP110 where AAD001 is not null and AP110 is not null  GROUP BY AP110)"; 
				dw_list = this.getBySqlMapper.findRecords(dw_sql);
				//驻村对
				zcd_sql = "SELECT count(*) num from (select * from ( "+
								" select AP110,AAP001 from  (select AAR008 from NEIMENG0117_AC01  where AAR100= '1' and AAR040='2016'and AAR00"+level+" ='"+code+"' GROUP BY AAR008   ) a "+
								" LEFT JOIN ( select AAD001,AAP110 from AD07 ) b on a.AAR008=b.AAD001  left join ( "+
								" select AAP110 Ap110,AAP001 from AP11)c ON b.AAP110=c.AP110 where AAD001 is not null and AP110 is not null  GROUP BY AP110,AAP001"+
								") aa LEFT JOIN (select AAP001 p001 from NEIMENG0117_ap01 ) bb on aa.AAP001=bb.p001 where p001 is not null)";
				zcd_list = this.getBySqlMapper.findRecords(zcd_sql);
				//驻村干部
				zcgb_sql = "select count(*) num from (select AAB002,AAK030,p011 from (select * from ("+
									"select AP110,AAP001 from  (select AAR008 from NEIMENG0117_AC01  where AAR100= '1' and AAR040='2016' and AAR00"+level+" ='"+code+"' GROUP BY AAR008   ) a "+
									" LEFT JOIN ( select AAD001,AAP110 from AD07 ) b on a.AAR008=b.AAD001  left join ("+
									" select AAP110 Ap110,AAP001 from AP11)c ON b.AAP110=c.AP110 where AAD001 is not null and AP110 is not null  GROUP BY AP110,AAP001"+
									") aa LEFT JOIN (select AAP001 p001,AAP011 from NEIMENG0117_ap01 ) bb on aa.AAP001=bb.p001 where p001 is not null) w0"+
									" left join (select  AAK030,AAB002,AAP011 p011 from NEIMENG0117_AK03)w1 on w0.AAP011=W1.p011 where p011 is not null)";
				zcgb_list = this.getBySqlMapper.findRecords(zcgb_sql);
				//帮扶户数
				sql ="select num ,xzqh,bfr from  ";
				sql+= " ( SELECT NUM,AAR00"+v1+" xz,xzqh FROM (  select  COUNT(*) NUM,AAR00"+v1+" from NEIMENG0117_AC01  where AAR100= '1'  and AAR040='2016' and AAR00"+level+"='"+code+"' GROUP BY AAR00"+v1+"  )AA left join ( ";
				sql += "  select v"+v2+" xzqh,v"+v3+" from SYS_COM GROUP BY v"+v2+",v"+v3+" )bb ON AA.AAR00"+v1+"=bb.v"+v3+"  where xzqh is not null)w0 LEFT JOIN (";
				sql +=" select count(AAC001) bfr ,AAR00"+v1+" from (select a.AAC001,AAR00"+v1+" from (select AAC001,AAR00"+v1+" from NEIMENG0117_AC01 where AAR100= '1' and AAR040='2016' ";
				sql +=") a left join (select * from NEIMENG0117_AC08)  b on a.AAC001=b.AAC001 where SUBSTR(b.AAR020, 0, 4) <='2016' AND SUBSTR(b.AAR021, 0, 4) >='2016' ";
				sql+=" )t1 group BY AAR00"+v1+" )w1 on w0.xz=w1.AAR00"+v1+"";
				
				list = this.getBySqlMapper.findRecords(sql);
				
				if( dw_list.size() > 0 ) {
			    	jb.put("bfdwTotal", "".equals(dw_list.get(0).get("NUM")) || dw_list.get(0).get("NUM") == null ? "0" : dw_list.get(0).get("NUM").toString());
				} else {
					jb.put("bfdwTotal", "0");
				}
				if( zcd_list.size() > 0 ) {
					jb.put("zcgzdTotal", "".equals(zcd_list.get(0).get("NUM")) || zcd_list.get(0).get("NUM") == null ? "0" : zcd_list.get(0).get("NUM").toString());
				} else {
					jb.put("zcgzdTotal", "0");
				}
				if( zcgb_list.size() > 0 ) {
					jb.put("zcgzgbTotal", "".equals(zcgb_list.get(0).get("NUM")) || zcgb_list.get(0).get("NUM") == null ? "0" : zcgb_list.get(0).get("NUM").toString());
				} else {
					jb.put("zcgzgbTotal", "0");
				}
				if ( list.size() > 0 ) {
					for ( int i = 0 ; i < list.size() ; i++ ) {
						JSONObject ob = new JSONObject ();
						bfhs+=(list.get(i).get("NUM")!=null)&&(list.get(i).get("NUM")!="")?Integer.valueOf(list.get(i).get("NUM").toString()):0;
						lsbfzrr+=(list.get(i).get("BFR")!=null)&&(list.get(i).get("BFR")!="")?Integer.valueOf(list.get(i).get("BFR").toString()):0;
					
						ob.put("V0", list.get(i).get("XZQH"));//区域名
						ob.put("V1", (list.get(i).get("BFR")!=null)&&(list.get(i).get("BFR")!="")?Integer.valueOf(list.get(i).get("BFR").toString()):0);//帮扶责任人数量
						chartJson.add(ob);
					}
					jb.put("lsbfzrrTotal", lsbfzrr);
					jb.put("bfhsTotal", bfhs);
				}
	    	}else{
	    		jb.put("bfdwTotal", bfdw);
	    	    jb.put("zcgzdTotal", zcgzd);
	    	    jb.put("zcgzgbTotal", zcgzgb);
	    	    jb.put("lsbfzrrTotal", lsbfzrr);
	    	    jb.put("bfhsTotal", bfhs);
	    	    
				for (int k = 0; k < str.length; k++) {
					JSONObject ob = new JSONObject ();
					ob.put("V0", str[k]);//区域名
					ob.put("V1", val[k]);//帮扶责任人数量
					chartJson.add(ob);
				}
	    	}
	    }else{
	    	jb.put("bfdwTotal", bfdw);
    	    jb.put("zcgzdTotal", zcgzd);
    	    jb.put("zcgzgbTotal", zcgzgb);
    	    jb.put("lsbfzrrTotal", lsbfzrr);
    	    jb.put("bfhsTotal", bfhs);
    	    for (int k = 0; k < str.length; k++) {
				JSONObject ob = new JSONObject ();
				ob.put("V0", str[k]);//区域名
				ob.put("V1", val[k]);//值
				chartJson.add(ob);
			}
	    }
	    tjJson.add(jb);
	    response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--11.	落实情况
	 * @throws IOException
	 * 
	 * 总户数、帮扶责任人落实户数、落实帮扶比例
	 */
	@RequestMapping("getFpdx11.do")
	public void getFpdx11(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String code = request.getParameter("code");
	    String name = request.getParameter("name");//行政区划名称
	    String level = request.getParameter("level");//2省 3市 4 县 5乡 6村
	    double Zhs=249506;//总户数
	    double lsHs=249506;//帮扶责任人落实户数
	    int lsBl=1;//落实帮扶比例
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    String str[] = { "呼和浩特市", "包头市", "呼伦贝尔市", "兴安盟", "通辽市", "赤峰市",
				"锡林郭勒盟", "乌兰察布市", "鄂尔多斯市", "巴彦淖尔市", "乌海市", "阿拉善盟" };
	    String val[] = { "25357", "4469", "606", "39070", "5406", "4508",
				"14571", "8825", "25337", "19230", "3499", "1147" };
	    String zhsSql = "";String bfhsSql = "";
	    if(name!=null&&!"".equals(name)){//判断是否为空
	    	zhsSql="select b.com_name as vf1,vf2,vf3,vf4,NVL(vf41, '0') vf41,vf5,(vf2-vf4) as vf6,(vf3-vf5) as vf7 from  (select * from SYS_COMPANY where COM_F_PKID=(SELECT PKID from SYS_COMPANY where COM_NAME='"+name+"'  and rownum = 1 ) ) b left join (select * from PKC_1_3_3   WHERE  COM_PIN = '0') c on c.v10=b.COM_CODE  ";
	    }else{
	    	zhsSql="select b.com_name as vf1,vf2,vf3,vf4,NVL(vf41, '0') vf41,vf5,(vf2-vf4) as vf6,(vf3-vf5) as vf7 from  (select * from SYS_COMPANY where COM_F_PKID=(SELECT PKID from SYS_COMPANY where COM_NAME='内蒙古自治区') ) b left join (select * from PKC_1_3_3   WHERE  COM_PIN = '0') c on c.v10=b.COM_CODE ";
	    }
		
	    if(Integer.valueOf(level)>1){//省级以下
	    	level=Integer.valueOf(level)+1+"";
	    	
	    	int v1 = Integer.parseInt(level)+1;
			int v3 = Integer.parseInt(level)*2;
			int v2 = v3 -1;
			
			 //帮扶户数
		    bfhsSql ="select num ,xzqh,bfr from  ";
		    bfhsSql+= " ( SELECT NUM,AAR00"+v1+" xz,xzqh FROM (  select  COUNT(*) NUM,AAR00"+v1+" from NEIMENG0117_AC01  where AAR100= '1'  and AAR040='2016' and AAR00"+level+"='"+code+"' GROUP BY AAR00"+v1+"  )AA left join ( ";
		    bfhsSql += "  select v"+v2+" xzqh,v"+v3+" from SYS_COM GROUP BY v"+v2+",v"+v3+" )bb ON AA.AAR00"+v1+"=bb.v"+v3+"  where xzqh is not null)w0 LEFT JOIN (";
		    bfhsSql +=" select count(AAC001) bfr ,AAR00"+v1+" from (select a.AAC001,AAR00"+v1+" from (select AAC001,AAR00"+v1+" from NEIMENG0117_AC01 where AAR100= '1' and AAR040='2016' ";
		    bfhsSql +=") a left join (select * from NEIMENG0117_AC08)  b on a.AAC001=b.AAC001 where SUBSTR(b.AAR020, 0, 4) <='2016' AND SUBSTR(b.AAR021, 0, 4) >='2016' ";
		    bfhsSql+=" )t1 group BY AAR00"+v1+" )w1 on w0.xz=w1.AAR00"+v1+"";
		    
	    	Zhs=0;
	    	lsHs=0;
	    	lsBl=0;
	    	List<Map> listBfhs = this.getBySqlMapper.findRecords(bfhsSql);
		    List<Map> listZhs = this.getBySqlMapper.findRecords(zhsSql);
	    	for (int i = 0; i < listZhs.size(); i++) {
	    		Map Fsl_map = listZhs.get(i);
				
				Zhs+=(Fsl_map.get("VF2")!=null)&&(Fsl_map.get("VF2")!="")?Integer.valueOf(Fsl_map.get("VF2").toString()):0;
	    	}
	    	
	    	for(int ik = 0; ik < listBfhs.size(); ik++){
	    		JSONObject obj = new JSONObject();
	    		obj.put("V0", listBfhs.get(ik).get("XZQH"));//区域名
				obj.put("V1", (listBfhs.get(ik).get("BFR")!=null)&&(listBfhs.get(ik).get("BFR")!="")?Integer.valueOf(listBfhs.get(ik).get("BFR").toString()):0);//帮扶责任人数量
				
	    		lsHs+=(listBfhs.get(ik).get("NUM")!=null)&&(listBfhs.get(ik).get("NUM")!="")?Integer.valueOf(listBfhs.get(ik).get("NUM").toString()):0;
				chartJson.add(obj);
	    	}
	    	jb.put("ZhsTotal", Zhs);
    	    jb.put("lsHsTotal", lsHs);
    	    jb.put("lsBlTotal", (lsHs/Zhs)>1?1:DateFormatUtil.formatDouble1(lsHs/Zhs));
	    }else{
	    	jb.put("ZhsTotal", Zhs);
    	    jb.put("lsHsTotal", lsHs);
    	    jb.put("lsBlTotal", (lsHs/Zhs)>1?1:DateFormatUtil.formatDouble1(lsHs/Zhs));
    	    for (int k = 0; k < str.length; k++) {
				JSONObject ob = new JSONObject ();
				ob.put("V0", str[k]);//区域名
				ob.put("V1", val[k]);//值
				chartJson.add(ob);
			}
	    }
	    
	    tjJson.add(jb);
    	response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    
	}
	/**
	 * @param request
	 * @param response 获取扶贫对象菜单栏统计数据--12.	入户帮扶
	 * @throws IOException
	 * 
	 * 贫困户、走访贫困户、走访比例、当日走访、本周走访、本月走访、近三月走访、全部走访（分行政区划）
	 */
	@RequestMapping("getFpdx12.do")
	public void getFpdx12(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String name = request.getParameter("name");
	    String level = request.getParameter("level");//2省 3市 4 县 5乡 6村
	    level=Integer.valueOf(level)+1+"";
	    String code = request.getParameter("code");//当前查询的区域范围如默认内蒙古自治区
	    if(Integer.valueOf(Integer.valueOf(level)-1)>1){
	    	code=this.getXjcode((Integer.valueOf(level)-1)+"",code);//根据当前传的行政区划code获取所有村级行政区划
	    }
	    double pkh=0;
	    double zfpkh=0;
	    int drzf=0;
	    int bzzf=0;
	    int byzf=0;
	    int jsyzf=0;
	    int zfAll=0;
	    
	    JSONArray chartJson = new JSONArray();
	    JSONArray tjJson = new JSONArray();
	    JSONObject jb = new JSONObject();
	    
	    int t = 0;//用于截取行政区划code时的长度
		String sqlTj = "select * from (";//拼接的sql条件
		//查询行政区划,获取行政区划code
		String sql = "SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=("
		+ "SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='" + name + "'  and rownum = 1 "
		+ ") ";
		
		 //查询贫困户
	    String sqlPkh = "";
	    
	    //贫困户
    	sqlPkh="SELECT a2.com_name as v1,a1.z_hu  FROM("
				+ "SELECT * FROM SYS_COMPANY WHERE COM_F_PKID=("
				+ "SELECT PKID FROM SYS_COMPANY WHERE COM_NAME='"+name+"'  and rownum = 1 "
				+ ")) a2 LEFT JOIN (select * from PKC_1_2_1  WHERE  TYPE = '0' ) a1 ON a1.COM_CODE=A2.COM_CODE ";
    	
    	List<Map> listPkh = this.getBySqlMapper.findRecords(sqlPkh);
    	 //走访贫困户总数
	    String sqlx1 = "SELECT	COUNT (DISTINCT(household_card)) AS d_poor_sum	FROM	DA_HELP_VISIT  WHERE 1=1";
	    if(code!=null&&!"".equals(code)&&Integer.valueOf(level)-1>1){//按区域查
	    	sqlx1+=" and AAR008 like('"+code+"%')";
	    	for (int i = 0; i < listPkh.size(); i++) {
	    		pkh+=(listPkh.get(i).get("Z_HU")!=null)&&(listPkh.get(i).get("Z_HU")!="")?Integer.valueOf(listPkh.get(i).get("Z_HU").toString()):0;
	    	}
	    }else{
	    	pkh=249506;
	    }
	    List<Map> listx1 = this.getBySqlMapper.findRecords(sqlx1);
	    if(listx1.size()>0){
	    	zfpkh=listx1.get(0).get("D_POOR_SUM")==null?0:Integer.valueOf(listx1.get(0).get("D_POOR_SUM").toString());
	    }else{
	    	zfpkh=0;
	    }
		List l = new ArrayList();//存储地区名
		List lev = new ArrayList();//存储地区所属层级
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		for (int i = 0; i < list.size(); i++) {
			Map Patient_st_map = list.get(i);
			l.add(Patient_st_map.get("COM_NAME"));
			lev.add(Patient_st_map.get("COM_LEVEL"));
			//判断当前下钻层级，截取code
			if(Integer.valueOf(Patient_st_map.get("COM_LEVEL").toString())==2){
				t=4;
			}else if(Integer.valueOf(Patient_st_map.get("COM_LEVEL").toString())==3){
				t=7;
			}else if(Integer.valueOf(Patient_st_map.get("COM_LEVEL").toString())==4){
				t=9;
			}else if(Integer.valueOf(Patient_st_map.get("COM_LEVEL").toString())==5){
				t=12;
			}else{
				t=4;
			}
			if(i<list.size()-1){
				sqlTj+="SELECT	count(*) AS THE_ALL,COUNT (		CASE		WHEN TO_CHAR (			TO_DATE (				registertime,				'yyyy-mm-dd hh24:mi:ss'			),			'yyyy-mm-dd'		) = TO_CHAR (SYSDATE, 'yyyy-mm-dd') THEN			'a00'		END	) the_day,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-7), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a01'		END	) the_one_week,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-14), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a02'		END	) the_two_week,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-30), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a03'		END	) the_one_month,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-90), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a04'		END	) the_three_month FROM	DA_HELP_VISIT where 1=1  and aar008 like '"+Patient_st_map.get("COM_CODE").toString().substring(0, t)+"%' union all ";
			}else{
				sqlTj+="SELECT	count(*) AS THE_ALL,COUNT (		CASE		WHEN TO_CHAR (			TO_DATE (				registertime,				'yyyy-mm-dd hh24:mi:ss'			),			'yyyy-mm-dd'		) = TO_CHAR (SYSDATE, 'yyyy-mm-dd') THEN			'a00'		END	) the_day,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-7), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a01'		END	) the_one_week,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-14), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a02'		END	) the_two_week,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-30), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a03'		END	) the_one_month,	COUNT (		CASE		WHEN TO_CHAR(TO_DATE (	registertime,	'yyyy-mm-dd hh24:mi:ss'	),'yyyy-mm-dd')> TO_CHAR (trunc(sysdate-90), 'yyyy-mm-dd') and TO_CHAR(TO_DATE (registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')<= TO_CHAR (sysdate, 'yyyy-mm-dd') THEN			'a04'		END	) the_three_month FROM	DA_HELP_VISIT where 1=1  and aar008 like '"+Patient_st_map.get("COM_CODE").toString().substring(0, t)+"%' ";
			}
		}
		sqlTj+=")";
		List<Map> listAll = this.getBySqlMapper.findRecords(sqlTj);
		for(int a =0;a<listAll.size();a++){
			JSONObject obj = new JSONObject();
			obj.put("V0", l.get(a));
//			obj.put("V8", lev.get(a));
			
			//按日期条件过滤
			obj.put("V1", listAll.get(a).get("THE_ALL"));//所有
			
			drzf+=(listAll.get(a).get("THE_DAY")!=null)&&(listAll.get(a).get("THE_DAY")!="")?Integer.valueOf(listAll.get(a).get("THE_DAY").toString()):0;
		    bzzf+=(listAll.get(a).get("THE_ONE_WEEK")!=null)&&(listAll.get(a).get("THE_ONE_WEEK")!="")?Integer.valueOf(listAll.get(a).get("THE_ONE_WEEK").toString()):0;
		    byzf+=(listAll.get(a).get("THE_TWO_WEEK")!=null)&&(listAll.get(a).get("THE_TWO_WEEK")!="")?Integer.valueOf(listAll.get(a).get("THE_TWO_WEEK").toString()):0;
		    jsyzf+=(listAll.get(a).get("THE_ONE_MONTH")!=null)&&(listAll.get(a).get("THE_ONE_MONTH")!="")?Integer.valueOf(listAll.get(a).get("THE_ONE_MONTH").toString()):0;
		    zfAll+=(listAll.get(a).get("THE_ALL")!=null)&&(listAll.get(a).get("THE_ALL")!="")?Integer.valueOf(listAll.get(a).get("THE_ALL").toString()):0;
		    
		    chartJson.add(obj);
		}
		jb.put("pkhTotal", pkh);
	    jb.put("zfpkhTotal", zfpkh);
	    jb.put("zfblTotal", (zfpkh/pkh)>1?1:DateFormatUtil.formatDouble1(zfpkh/pkh));
	    
		jb.put("drzfTotal", drzf);
	    jb.put("bzzfTotal", bzzf);
	    jb.put("byzfTotal", byzf);
	    jb.put("jsyzfTotal", jsyzf);
	    jb.put("zfAllTotal", zfAll);
	   
	    tjJson.add(jb);
	    response.getWriter().write("{\"success\":\"0\",\"message\":\"成功\",\"chartData\":"+chartJson.toString()+",\"tjSum\":"+tjJson+"}");
	    
	}
	/***************************精准扶贫帮扶通领导版第一版接口*******************************/
	
	/**行政区划获取接口，cType:1、获取省份及code2、获取市级3、获取县区，此时需要带上市级code参数获取该市级下的县
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("getXzqh.do")
	public void getXzqh(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
		String sql = "";
		String cType = request.getParameter("cType");//查询类别1、省2、市3、县
		String code = request.getParameter("code");//市级CODE，根据市级CODE获取该市级下的县
		List<Map> list = null;
		if(cType!=null&&!"".equals(cType)){
			if(MapUtil.isNumeric(cType)){
				if(Integer.valueOf(cType)==1){//省
					sql="select V1 as V1,V2 as V2 from sys_com GROUP BY V1,V2";
				}else if(Integer.valueOf(cType)==2){//查询所有市级
					sql="select V3 as V1,V4 as V2 from sys_com GROUP BY V3,V4";
				}else if(Integer.valueOf(cType)==3){//查询市级下的县级
					sql="select V4,V5 as V1,V6 as V2 from sys_com ";
					if(code!=null&&!"".equals(code)){
						sql+=" where V4="+code;
					}
					sql+=" GROUP BY V4,V5,V6 ";
				}else if(Integer.valueOf(cType)==4){//查询县级下的乡级
					sql="select V6,V7 as V1,V8 as V2 from sys_com ";
					if(code!=null&&!"".equals(code)){
						sql+=" where V6="+code;
					}
					sql+=" GROUP BY V6,V7,V8 ";
				}else if(Integer.valueOf(cType)==5){//查询乡级下的村
					sql="select V8,V9 as V1,V10 as V2 from sys_com ";
					if(code!=null&&!"".equals(code)){
						sql+=" where V8="+code;
					}
					sql+=" GROUP BY V8,V9,V10 ";
				}else{
					sql="select V1 as V1,V2 as V2 from sys_com GROUP BY V1,V2";
				}
				list = this.getBySqlMapper.findRecords(sql);
			}else{
				sql="select V1 as V1,V2 as V2 from sys_com GROUP BY V1,V2";
				list = this.getBySqlMapper.findRecords(sql);
			}
		}else{
			sql="select V1 as V1,V2 as V2 from sys_com GROUP BY V1,V2";
			list = this.getBySqlMapper.findRecords(sql);
		}
		JSONArray jn = new JSONArray();
		if(list.size()>0){
			for(int j=0;j<list.size();j++){
				JSONObject jb = new JSONObject();
				jb.put("name", list.get(j).get("V1"));
				jb.put("code", list.get(j).get("V2"));
				jn.add(jb);
			}
		}
		response.getWriter().write(jn.toString());
	}
	/**根据区划级别截取code
	 * @param ctype
	 * @param code
	 * @return
	 */
	public String getXjcode(String ctype,String code){
		String codes="";
		if(Integer.valueOf(ctype)==2){
			codes=code.substring(0, 4);
		}else if(Integer.valueOf(ctype)==3){//县
			codes=code.substring(0, 6);
		}else if(Integer.valueOf(ctype)==4){
			codes=code.substring(0, 8);
		}
		return codes;
	}
	/**获取日记统计参数
	 * 1、当前帮扶日记总条数：diary_sum					int
	 * 
		2、走访相关贫困户数：d_poor_sum					int
		
		3、走访覆盖率：d_poor_coverage					double
		
		4、上传走访记录干部总数：d_cadre_sum				int
		
		
		5、上传走访记录干部占总数比：d_cadre_proportion	double
		
		
		6、全区帮扶干部总数:：cadre_sum					int
		7、全区贫困户总数：poor_sum						int
		8、落实责任人比例：assist_coverage				double
		
		9、当日日记条数：day_sum						int
		10、本周日记条数：week_sum						int
		11、本月日记条数：month_sum						int
	 * @param request
	 * @param response
	 * @throws IOException
	 * 
	 * 
	 * 	    //单个
	    String sql0 = "SELECT COUNT(*) AS diary_sum,COUNT (DISTINCT(household_card)) AS d_poor_sum,COUNT (DISTINCT(personal_phone)) AS d_cadre_sum FROM DA_HELP_VISIT";//日记总条数、走访贫困户总数、上传走访记录干部总数
	    String sql3 = "select count(*) from DA_HELP_VISIT where registertime is not null and to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')";//当天日记数
	    String sql4 = "select count(*) from DA_HELP_VISIT where to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'iw')=to_char(sysdate,'iw')";//本周日记数
	    String sql5 = "select count(*) from DA_HELP_VISIT where to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm')=to_char(sysdate,'yyyy-mm')";//本月日记数
	    
	 * 
	 * 
	 */
	@RequestMapping("getTjSum.do")
	public void getTjSum(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    long startTime = System.currentTimeMillis();    //获取开始时间
//	    System.out.println("进入接口时间："+startTime);
	    JSONArray jn = new JSONArray();
	    String cType = request.getParameter("cType");//查询类别1、省2、市3、县
	    String code = request.getParameter("code");//当前查询的区域范围如默认内蒙古自治区
	    if(Integer.valueOf(cType)>1){
	    	code=this.getXjcode(cType,code);//根据当前传的行政区划code获取所有村级行政区划
	    }
	    String sTime = request.getParameter("stime");//开始时间
	    String eTime = request.getParameter("etime");//结束时间
	    //不跟随传递的时间变，当天日记数、本周日记数、本月日记数 DA_HELP_VISIT
	    String sqlx = "SELECT	count(case when to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') then 'a00' end)day,	count(	CASE when to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'iw')=to_char(sysdate,'iw') and TO_NUMBER(sysdate-to_date(registertime,'yyyy-mm-dd hh24:mi:ss'))<10 THEN 'a01' end)week,	count(	CASE when to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm')=to_char(sysdate,'yyyy-mm') THEN 'a02' end)month	FROM	DA_HELP_VISIT  WHERE 1=1";
	    //跟随传递的时间变 日记总条数、走访贫困户总数、上传走访记录干部总数、
	    String sqlx1 = "SELECT	COUNT (*) AS diary_sum,	COUNT (DISTINCT(household_card)) AS d_poor_sum,	COUNT (DISTINCT(personal_phone)) AS d_cadre_sum	FROM	DA_HELP_VISIT  WHERE 1=1";
	  //不跟随传递的时间变
	    String sql1 = "select count(*) as poor_sum from NEIMENG0117_AC01 WHERE 1=1";//总贫困户数
	  //不跟随传递的时间变 总帮扶干部数
	    String sql2 = "select COUNT(*) AS CADRE_SUM from NEIMENG0117_AK11 T1 JOIN (select AAK110 from NEIMENG0117_AC08  WHERE AAR100=1 GROUP BY AAK110) T2 ON T1.AAK110=T2.AAK110 WHERE 1=1";
	    String sqlc = "select * from DA_HELP_VISIT where 1=1 ";//计算走访覆盖率子查询语句
	    String sqlc3 = "select * from DA_HELP_VISIT where 1=1 ";//计算走访覆盖率子查询语句
	    if(code!=null&&!"".equals(code)&&Integer.valueOf(cType)>1){//按区域查
	    	sqlx+=" and AAR008 like('"+code+"%')";
	    	sqlx1+=" and AAR008 like('"+code+"%')";
	    	sql1+=" and AAR008 like('"+code+"%')";
	    	sql2+=" and T1.AAR008 like('"+code+"%')";
	    	sqlc+=" and AAR008 like('"+code+"%')";
	    	sqlc3+=" and AAR008 like('"+code+"%')";
	    }
	    if(sTime!=null&&!"".equals(sTime)){//按时间查
	    	sqlx1+=" and to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') >= to_date('"+sTime+"','yyyy-mm-dd')";
	    	sqlc+=" and to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') >= to_date('"+sTime+"','yyyy-mm-dd')";
	    }
		if(eTime!=null&&!"".equals(eTime)){//按时间查
			sqlx1+=" AND to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') <= to_date('"+eTime+"','yyyy-mm-dd')";	
			sqlc+=" AND to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') <= to_date('"+eTime+"','yyyy-mm-dd')";
	    }
		//计算走访覆盖率 分组前
		String sql3 = "select count(*)  as summ from ("+sqlc3+") tt left join SYS_PERSONAL_HOUSEHOLD_MANY ti  on tt.PERSONAL_NAME = ti.PERSONAL_NAME and tt.HOUSEHOLD_NAME = ti.HOUSEHOLD_NAME and tt.PERSONAL_PHONE = ti.PERSONAL_PHONE and tt.HOUSEHOLD_CARD = ti.HOUSEHOLD_CARD ";
		//计算走访覆盖率 分组后
	    String sql4 = "select count(count(*))  as summ from ("+sqlc+") tt left join SYS_PERSONAL_HOUSEHOLD_MANY ti  on tt.PERSONAL_NAME = ti.PERSONAL_NAME and tt.HOUSEHOLD_NAME = ti.HOUSEHOLD_NAME and tt.PERSONAL_PHONE = ti.PERSONAL_PHONE and tt.HOUSEHOLD_CARD = ti.HOUSEHOLD_CARD GROUP BY tt.PERSONAL_NAME,tt.PERSONAL_PHONE,tt.HOUSEHOLD_NAME,tt.HOUSEHOLD_CARD";
	    sql1+=" and AAR010 IN (0,3) and AAR100=1";//未脱贫 有效性
		//合并查询
	    String sqlUnion = sql1+" union all "+sql2+" union all "+sql3+" union all "+sql4;
	    
	    JSONObject jb = new JSONObject();
	    List<Map> listx = this.getBySqlMapper.findRecords(sqlx);
	    List<Map> listx1 = this.getBySqlMapper.findRecords(sqlx1);
	    
	    List<Map> listUnion = this.getBySqlMapper.findRecords(sqlUnion);
	    
	    /*List<Map> list1 = this.getBySqlMapper.findRecords(sql1);
	    List<Map> list2 = this.getBySqlMapper.findRecords(sql2);
	    List<Map> list3 = this.getBySqlMapper.findRecords(sql3);
	    List<Map> list4 = this.getBySqlMapper.findRecords(sql4);*/
	    //统计数
	    if(listx.size()>0){
	    	jb.put("day_sum", listx.get(0).get("DAY")==null?0:listx.get(0).get("DAY"));//当天日记条数
	    	jb.put("week_sum", listx.get(0).get("WEEK")==null?0:listx.get(0).get("WEEK"));//当周日记条数
	    	jb.put("month_sum", listx.get(0).get("MONTH")==null?0:listx.get(0).get("MONTH"));//当月日记条数
	    }else{
	    	jb.put("day_sum", 0);
	    	jb.put("week_sum", 0);
	    	jb.put("month_sum", 0);
	    }
	    if(listx1.size()>0){
	    	jb.put("diary_sum", listx1.get(0).get("DIARY_SUM")==null?0:listx1.get(0).get("DIARY_SUM"));//总日记条数
	    	jb.put("d_poor_sum", listx1.get(0).get("D_POOR_SUM")==null?0:listx1.get(0).get("D_POOR_SUM"));//走访相关贫困户数
	    	jb.put("d_cadre_sum", listx1.get(0).get("D_CADRE_SUM")==null?0:listx1.get(0).get("D_CADRE_SUM"));//走访记录干部总数
	    }else{
	    	jb.put("diary_sum", 0);
	    	jb.put("d_poor_sum", 0);
	    	jb.put("d_cadre_sum", 0);
	    }
	    
	    //总贫困户数  不跟随时间变化
	    if(listUnion.size()>0){
	    	jb.put("poor_sum", listUnion.get(0).get("POOR_SUM")==null?0:listUnion.get(0).get("POOR_SUM"));
	    }else{
	    	jb.put("poor_sum", 0);
	    }
	    //总帮扶干部数 不跟随时间变化
	    if(listUnion.size()>0){
	    	jb.put("cadre_sum", listUnion.get(1).get("POOR_SUM")==null?0:listUnion.get(1).get("POOR_SUM"));
	    }else{
	    	jb.put("cadre_sum", 0);
	    }
	    DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
	    //落实责任人比例 帮扶贫困户/总贫困户
	    if(listUnion.size()>0){
	    	String d = "0";
	    	if(Integer.valueOf(listUnion.get(0).get("POOR_SUM").toString())>0){
	    		d=df.format((float)Integer.valueOf(listUnion.get(2).get("POOR_SUM").toString())/Integer.valueOf(listUnion.get(0).get("POOR_SUM").toString()));
	    	}
	    	if(Double.parseDouble(d)>1){
	    		jb.put("assist_coverage",  1);//listx.get(0).get("D_POOR_SUM").toString()
	    	}else{
	    		if(Integer.valueOf(listUnion.get(0).get("POOR_SUM").toString())>0){
	    			jb.put("assist_coverage",  df.format((float)Integer.valueOf(listUnion.get(2).get("POOR_SUM").toString())/Integer.valueOf(listUnion.get(0).get("POOR_SUM").toString())));//listx.get(0).get("D_POOR_SUM").toString()
		    	}else{
		    		jb.put("assist_coverage",  0);//listx.get(0).get("D_POOR_SUM").toString()
		    	}
	    		
	    	}
	    }
	    
	    //走访覆盖率   登录表（即结对表）关联除以  走访表=走访覆盖率
	    if(Integer.valueOf(listUnion.get(2).get("POOR_SUM").toString())>0){
	    	jb.put("d_poor_coverage",  df.format((float)Integer.valueOf(listUnion.get(3).get("POOR_SUM").toString())/Integer.valueOf(listUnion.get(2).get("POOR_SUM").toString())));
	    }else{
	    	jb.put("d_poor_coverage",  0);
	    }
	    
	    //上传走访记录干部占总数比 走访干部/总干部
	    
	    jb.put("d_cadre_proportion",  df.format((float)Integer.valueOf(listx1.get(0).get("D_CADRE_SUM").toString())/Integer.valueOf(listUnion.get(1).get("POOR_SUM").toString())));
	    jn.add(jb);
	    long endTime = System.currentTimeMillis();    //获取结束时间
//	    System.out.println("程序运行时间：" + (endTime - startTime)/1000 + "s");    //输出程序运行时间
	    response.getWriter().write(jn.toString());
	}
	
	/** 查询日记浏览中查询日记
	 * @param request
	 * @param response
	 * @throws IOException   DA_HELP_VISIT
	 */
	@RequestMapping("getRjll.do")
	public void getRjll(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String cType = request.getParameter("cType");//查询类别1、省2、市3、县
	    String code = request.getParameter("code");//当前查询的区域范围如默认内蒙古自治区
	    String type = request.getParameter("type");
	    if(Integer.valueOf(cType)>1){
	    	code=this.getXjcode(cType,code);//根据当前传的行政区划code获取所有村级行政区划
	    }
	    String pageNum = request.getParameter("pageNum");//分页页码
	    String sTime = request.getParameter("stime");//开始时间
	    String eTime = request.getParameter("etime");//结束时间
	    
	    Date date;
	    Calendar cal = null;
	    if(eTime!=null&&!"".equals(eTime)){
	    	try {
				date = (new SimpleDateFormat("yyyy-MM-dd")).parse(eTime);
				cal = Calendar.getInstance();
			    cal.setTime(date);
			    cal.add(Calendar.DATE, 1);//加一天
			} catch (ParseException e) {
				e.printStackTrace();
			}
	    }
		
	    
	    String phone = request.getParameter("phone");//干部电话
	    String name = request.getParameter("name");//干部名称
	    int start =0;
	    //当pageNum参数为空时，自动赋值为1
	    if(pageNum!=null&&!"".equals(pageNum)){
	    }else{
	    	pageNum="1";
	    }
	    if(Integer.valueOf(pageNum)>1){
	    	for(int i=0;i<Integer.valueOf(pageNum)-1;i++){
	    		start+=20;
	    	}
	    }
	    int end = 20*Integer.valueOf(pageNum);
	    JSONArray jn = new JSONArray();
	    String sqlX="select * from (select rownum as num,ts.* from (";
	    String sql = "SELECT	PERSONAL_NAME,	HOUSEHOLD_NAME,	PERSONAL_PHONE,	V3,zftype,	REGISTERTIME,	max(t2.PIC_PATH) as PIC_PATH FROM	DA_HELP_VISIT t1 LEFT JOIN (select DISTINCT(random_number),pic_path from DA_PIC_VISIT) t2 ON t1.random_number = t2.random_number WHERE	1 = 1 ";//DA_HELP_VISIT
	    if(code!=null&&!"".equals(code)&&Integer.valueOf(cType)>1){
	    	sql+=" and AAR008 like('"+code+"%')";
	    }
	    if(sTime!=null&&!"".equals(sTime)){
	    	sql+=" and to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') >= to_date('"+sTime+"','yyyy-mm-dd')";
	    }
		if(eTime!=null&&!"".equals(eTime)){
			sql+=" AND to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') < to_date('"+(new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime())+"','yyyy-mm-dd')";	
	    }
		if(phone!=null&&!"".equals(phone)){
			sql+=" and PERSONAL_PHONE LIKE '"+phone+"%'";	
		}
		if(name!=null&&!"".equals(name)){
			sql+=" and PERSONAL_NAME LIKE '"+name+"%'";	
		}
		String sqlC="";
		if(Integer.valueOf(type)==0){//全部
		}else if(Integer.valueOf(type)==1){//今日
			sqlC=" and to_char(to_date(t1.registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')";
		}else if(Integer.valueOf(type)==2){//本周
			sqlC=" and to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'iw')=to_char(sysdate,'iw') and TO_NUMBER(sysdate-to_date(registertime,'yyyy-mm-dd hh24:mi:ss'))<10";
		}else if(Integer.valueOf(type)==3){//本月
			sqlC=" and to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm')=to_char(sysdate,'yyyy-mm')";
		}
		sql+=" and t1.registertime is not null "+sqlC+" GROUP BY PERSONAL_NAME,HOUSEHOLD_NAME,PERSONAL_PHONE,V3,zftype,REGISTERTIME ORDER BY TO_DATE (REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') desc";
		sqlX+=sql +") ts )tt";
		sqlX+=" where tt.num <="+end+" and tt.num>"+start+"";
		
		List<Map> list = this.getBySqlMapper.findRecords(sqlX);
	    if(list.size()>0){
	    	for(int l=0;l<list.size();l++){
	    		JSONObject jb = new JSONObject();
	    		jb.put("name", list.get(l).get("PERSONAL_NAME"));//帮扶人名称
	    		jb.put("hname", list.get(l).get("HOUSEHOLD_NAME"));//贫困户名称
	    		jb.put("phone", list.get(l).get("PERSONAL_PHONE"));//帮扶人电话
	    		jb.put("content", list.get(l).get("V3"));//帮扶内容
	    		jb.put("time", list.get(l).get("REGISTERTIME"));//帮扶日期
	    		jb.put("zftype", list.get(l).get("ZFTYPE"));//走访类型
	    		jb.put("pic", list.get(l).get("PIC_PATH")==null?"":list.get(l).get("PIC_PATH"));//帮扶照片
	    		jn.add(jb);
	    	}
	    }
	    response.getWriter().write(jn.toString());
	}
	/** 查询日记浏览中按帮扶人姓名、电话查询
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("getRjllByName.do")
	public void getRjllByName(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    JSONArray jn = new JSONArray();
	    String name = request.getParameter("name");//干部名称
	    String phone = request.getParameter("phone");//干部名称
	    String sqlc = "select personal_phone,personal_name from SYS_PERSONAL_HOUSEHOLD_MANY where 1=1 ";
	    if(name!=null&&!"".equals(name)){
	    	sqlc+=" and PERSONAL_NAME like '"+name+"%' ";
	    }
	    if(phone!=null&&!"".equals(phone)){
	    	sqlc+=" and PERSONAL_PHONE like '"+phone+"%' ";
	    }
	    sqlc+=" GROUP BY personal_phone,personal_name ";
	    String sql = "select personal_phone,personal_name,AAP001 from (select t1.personal_phone,t1.personal_name,t2.AAP001  from("+sqlc+") t1 left join (select AAR012,AAP001 from NEIMENG0117_AK11 where AAP001 is not null) t2 on t1.personal_phone=t2.AAR012)GROUP BY personal_phone,personal_name,AAP001";
	    List<Map> list = this.getBySqlMapper.findRecords(sql);
	    if(list.size()>0){
	    	for(int l=0;l<list.size();l++){
	    		JSONObject jb = new JSONObject();
	    		jb.put("name", list.get(l).get("PERSONAL_NAME"));
	    		jb.put("phone", list.get(l).get("PERSONAL_PHONE"));
	    		jb.put("dep", list.get(l).get("AAP001"));
	    		jn.add(jb);
	    	}
	    }
	    response.getWriter().write(jn.toString());
	}
	/** 查询扶贫现状
	 * @param request
	 * @param response AAR010  0未脱贫1已脱贫
	 * //	    String sTime = request.getParameter("stime");//开始时间
//	    String eTime = request.getParameter("etime");//结束时间
	 * @throws IOException
	 */
	@RequestMapping("getFpxz.do")
	public void getFpxz(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    String cType = request.getParameter("cType");//查询类别1、省2、市3、县
	    String code = request.getParameter("code");//当前查询的区域范围如默认内蒙古自治区
	    if(Integer.valueOf(cType)>1){
	    	code=this.getXjcode(cType,code);//根据当前传的行政区划code获取所有村级行政区划
	    }
	    JSONObject qt = new JSONObject();
	    JSONObject jb = new JSONObject();
	    JSONObject jbzpyy = new JSONObject();
	    //已脱贫户数、
	    String sqltph = "select count(CASE when a.AAR010=1 then 'a01'end) 已脱贫户数 from NEIMENG0117_AC01 a where 1=1  and AAR100=1";
	    //贫困户总数、因病、因残、缺土地、缺资金、一般贫困户、低保贫困户、五保贫困户
	    String sql1 = "select count(a.AAC001) AS 贫困户总数,count(CASE when a.AAC007=1 then 'a02'end) 因病,count(CASE when a.AAC007=2 then 'a03'end) 因残,count(CASE when a.AAC007=3 then 'a003'end) 因学,count(CASE when a.AAC007=4 then 'a004'end) 因灾,count(CASE when a.AAC007=5 then 'a04'end) 缺土地,count(CASE when a.AAC007=6 then 'a006'end) 缺水,count(CASE when a.AAC007=7 then 'a007'end) 缺技术,count(CASE when a.AAC007=8 then 'a008'end) 缺劳力,count(CASE when a.AAC007=9 then 'a05'end) 缺资金,count(CASE when a.AAC007=10 then 'a0010'end) 交通条件落后,count(CASE when a.AAC007=11 then 'a0011'end) 自身发展力不足,count(CASE when a.AAC007=99 then 'a0099'end) 其他,count(CASE when a.AAC006=1 then 'a06'end) 一般贫困户,count(CASE when a.AAC006=4 then 'a07'end) 低保贫困户,count(CASE when a.AAC006=6 then 'a08'end) 五保贫困户 from NEIMENG0117_AC01 a where 1=1";
	    String sqlc = "select AAC001 from NEIMENG0117_Ac01  where AAR010 in (0,3) and AAR100=1 ";//贫困人口
	    String sqlct = "select AAC001 from NEIMENG0117_Ac01  where AAR010 in (1) and AAR100=1 ";//已脱贫贫困人口
	    String sqlpkc = "select count(*) as pkc from NEIMENG0117_AD01 where 1=1 and aar010 in(1) ";//贫困村
	    if(code!=null&&!"".equals(code)&&Integer.valueOf(cType)>1){
	    	sql1+=" and AAR008 like('"+code+"%')";
	    	sqlc+=" and AAR008 like('"+code+"%')";
	    	sqlct+=" and AAR008 like('"+code+"%')";
	    	sqltph+=" and AAR008 like('"+code+"%')";
	    	sqlpkc+=" and AAR008 like('"+code+"%')";
	    }
	    sql1+=" and AAR010 IN(0,3) and AAR100=1";
	    String sql0 = "select count(*) as pkrk from( select q1.AAC001 from ("+sqlc+")q left JOIN  ( select AAC001 from  NEIMENG0117_AB01 where aab015=1 )q1 on q.AAC001= q1.AAC001)";//贫困人口
	    String sqltpr = "select count(*) as ytpr from( select q1.AAC001 from ("+sqlct+")q left JOIN  ( select AAC001 from  NEIMENG0117_AB01 where aab015=1 )q1 on q.AAC001= q1.AAC001)";//已脱贫贫困人口
	    List<Map> list1 = this.getBySqlMapper.findRecords(sql1);
	    List<Map> list0 = this.getBySqlMapper.findRecords(sql0);
	    
	    List<Map> listYtph = this.getBySqlMapper.findRecords(sqltph);//已脱贫户
	    List<Map> listYtpr = this.getBySqlMapper.findRecords(sqltpr);//已脱贫人
	    List<Map> listPkc = this.getBySqlMapper.findRecords(sqlpkc);//贫困村
    	jb.put("pkzrk", list0.get(0).get("PKRK"));//贫困人口
    	
    	jb.put("ytphs", listYtph.get(0).get("已脱贫户数"));
    	
    	jb.put("pkzhs", list1.get(0).get("贫困户总数"));
    	
    	Map m = new HashMap();
    	m.put("因病", list1.get(0).get("因病"));
    	m.put("因残", list1.get(0).get("因残"));
    	m.put("因学", list1.get(0).get("因学"));
    	m.put("因灾", list1.get(0).get("因灾"));
    	m.put("缺土地", list1.get(0).get("缺土地"));
    	m.put("缺水", list1.get(0).get("缺水"));
    	m.put("缺技术", list1.get(0).get("缺技术"));
    	m.put("缺劳力", list1.get(0).get("缺劳力"));
    	m.put("缺资金", list1.get(0).get("缺资金"));
    	m.put("交通条件落后", list1.get(0).get("交通条件落后"));
    	m.put("自身发展力不足", list1.get(0).get("自身发展力不足"));
    	m.put("其他", list1.get(0).get("其他"));
    	
    	m=MapUtil.sortByValue(m);//倒序排序
    	
    	Set<String> key = m.keySet();
    	int sm=0;
        for (Iterator it = key.iterator(); it.hasNext();) {
        	if(sm<4){
        		String s = (String) it.next();
        		jbzpyy.put(s, m.get(s));
        	}else{
        		break;
        	}
            sm++;
        }
        
    	jb.put("ybpkh", list1.get(0).get("一般贫困户"));
    	jb.put("dbpkh", list1.get(0).get("低保贫困户"));
    	jb.put("wbpkh", list1.get(0).get("五保贫困户"));
    	
    	jb.put("ytpr", listYtpr.get(0).get("YTPR"));
    	jb.put("pkc", listPkc.get(0).get("PKC"));
    	
    	qt.put("zpyy", jbzpyy);//致贫原因
    	qt.put("qt", jb);//其他
	    response.getWriter().write(qt.toString());
	}
	/***************************以下所有方法为测试时使用*******************************/
	
	
	
	
	/**
	 * 导入鄂尔多斯走访记录
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("getVisit.do")
	public void getVisit(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		String  sql = "SELECT DD.PKID,V6,V8,V1,V2,V3,AAC001 FROM (SELECT PKID,V6,V8 FROM DA_HOUSEHOLD_F)AA LEFT JOIN "+
						" (SELECT AAC001,AAB002,AAB004 FROM NEIMENG0117_AB01 WHERE AAR040='2016' and AAB006='01') BB ON AA.V6=BB.AAB002 AND AA.V8=BB.AAB004"+
						" LEFT JOIN (SELECT PKID,HOUSEHOLD_ID,v1,v2,v3 FROM DA_HELP_VISIT_F)DD ON AA.PKID=DD.HOUSEHOLD_ID  WHERE BB.AAC001 IS NOT NULL AND DD.PKID IS NOT NULL";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		for (int i = 0; i < list.size(); i++) {
				Date date = new Date();
				SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
				String random_number = sf.format(date)+"_"+new Random().nextInt(1000);//时间戳+随机数
				String cha_sql = " SELECT COL_NAME,TELEPHONE FROM SYS_PERSONAL_F where col_name='"+list.get(i).get("V2")+"'";
				List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
				String  phone="";
				String col_name="";
				if ( cha_list.size() > 0 ) {
					phone = "".equals(cha_list.get(0).get("TELEPHONE")) || cha_list.get(0).get("TELEPHONE") == null ? "" : cha_list.get(0).get("TELEPHONE").toString();
					col_name = "".equals(cha_list.get(0).get("COL_NAME")) || cha_list.get(0).get("COL_NAME") == null ? "" : cha_list.get(0).get("COL_NAME").toString();
				}
				String  cha_sql1 = "SELECT AAR008 FROM NEIMENG0117_AC01 WHERE AAC001='"+list.get(i).get("AAC001")+"' AND AAR040='2016'";
				List<Map> cha_list1 = this.getBySqlMapper.findRecords(cha_sql1);
				String AAR008 = "";
				if ( cha_list1.size() > 0 ) {
					AAR008=cha_list1.get(0).get("AAR008").toString();
				}
				String inset_sql ="INSERT INTO DA_HELP_VISIT(HOUSEHOLD_NAME,PERSONAL_NAME,V1,V3,HOUSEHOLD_CARD,PERSONAL_PHONE,RANDOM_NUMBER,AAR008)"+
						" VALUES('"+list.get(i).get("V6")+"','"+col_name+"','"+list.get(i).get("V1")+"','"+list.get(i).get("V3")+"','"+list.get(i).get("V8")+"','"+phone+"','"+random_number+"','"+AAR008+"')";
				this.getBySqlMapper.insert(inset_sql);
				
				String  pic_sql = "select pic_path from DA_PIC_F WHERE pic_type='2' and pic_pkid='"+list.get(i).get("PKID")+"'";
				List<Map> pic_list = this.getBySqlMapper.findRecords(pic_sql);
				if ( pic_list.size() > 0 ) {
					for ( int j = 0 ; j < pic_list.size() ; j++ ) {
						String in_sql = "INSERT INTO DA_PIC_VISIT (RANDOM_NUMBER,PIC_PATH) VALUES ('"+random_number+"','"+pic_list.get(j).get("PIC_PATH")+"')";
						this.getBySqlMapper.insert(in_sql);
					}
				}
			
			}
			response.getWriter().write("111111111111111111111");
	}
	/**
	 * 导入阿荣旗的走访记录以及走访记录照片
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("gethou.do")
	public void gethou(HttpServletRequest request,HttpServletResponse response ) throws IOException {

		String  sql = "SELECT A4,A6,A37,A41,AAB002,AAB004, GPS,CONTENT,time,pic,BB.AAC001,tel,AAR008 FROM (SELECT NID,A4,A6,A37,A41 FROM AR_DAKL WHERE A7='户主' "+
						")AA LEFT JOIN   (SELECT AAC001,AAB002,AAB004,AAR008 FROM NEIMENG0117_AB01 WHERE AAR040='2016' and AAB006='01' and AAB015='1' "+
						") BB ON AA.A4=BB.AAB002 AND AA.A6=BB.AAB004   LEFT JOIN ("+
						"SELECT GPS,CONTENT,time,YN,PICTURE pic,KEYWORD FROM AR_KL )DD ON AA.NID=DD.YN "+
						" LEFT JOIN (select name gname,tel ,id from AR_GB_KL ) ee on dd.KEYWORD=ee.id WHERE BB.AAC001 IS NOT NULL and CONTENT is not null";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
			for ( int i = 1 ; i < list.size() ; i++ ) {
				Date date = new Date();
				SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
				String random_number = sf.format(date)+"_"+new Random().nextInt(1000);//时间戳+随机数
				String household_name = "".equals(list.get(i).get("A4")) || list.get(i).get("A4") == null ? "" : list.get(i).get("A4").toString();//贫困户
				String household_crad = "".equals(list.get(i).get("A6")) || list.get(i).get("A6") == null ? "" : list.get(i).get("A6").toString();//贫困户证件号码
				String  personal_phone = "".equals(list.get(i).get("TEL")) || list.get(i).get("TEL") == null ? "" : list.get(i).get("TEL").toString();//帮扶人电话
				String personal_name = "".equals(list.get(i).get("A37")) || list.get(i).get("A37") == null ? "" : list.get(i).get("A37").toString();//帮扶人
				String v1 = "".equals(list.get(i).get("TIME")) || list.get(i).get("TIME") == null ? "" : list.get(i).get("TIME").toString();//时间
				String time = v1.replaceAll("/", "-");
				String v3 = "".equals(list.get(i).get("CONTENT")) || list.get(i).get("CONTENT") == null ? "" : list.get(i).get("CONTENT").toString();//走访记录
				String pic = "".equals(list.get(i).get("PIC")) || list.get(i).get("PIC") == null ? "" : list.get(i).get("PIC").toString();//照片
				String gps = "".equals(list.get(i).get("GPS")) || list.get(i).get("GPS") == null ? "" : list.get(i).get("GPS").toString();//位置
				String lag[] = gps.split(",");
				String lng = "";
				String lat = "";
				if ( lag.length > 0 && lag[0] != "") {
					 lng = lag[1];
					 lat = lag[0];
				}
				String AAR008 = "".equals(list.get(i).get("AAR008")) || list.get(i).get("AAR008") == null ? "" : list.get(i).get("AAR008").toString();//村的编码
				String cha_sql = "select * from DA_HELP_VISIT where HOUSEHOLD_NAME='"+household_name+"' and PERSONAL_NAME='"+personal_name+"' and V1='"+v1+"' and "+
								" LNG='"+lng+"' and lat='"+lat+"' and v3='"+v3+"' and HOUSEHOLD_CARD='"+household_crad+"' and PERSONAL_PHONE ='"+personal_phone+"' and "+
								" AAR008='"+AAR008+"'";
				List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
				if ( cha_list.size() > 0 ) {
					
				} else {
					String inset_sql ="INSERT INTO DA_HELP_VISIT(HOUSEHOLD_NAME,PERSONAL_NAME,V1,LNG,LAT,V3,HOUSEHOLD_CARD,PERSONAL_PHONE,RANDOM_NUMBER,AAR008)"+
							" VALUES('"+household_name+"','"+personal_name+"','"+time+"','"+lng+"','"+lat+"','"+v3+"','"+household_crad+"','"+personal_phone+"','"+random_number+"','"+AAR008+"')";
					this.getBySqlMapper.insert(inset_sql);
					String in_sql = "INSERT INTO DA_PIC_VISIT (RANDOM_NUMBER,PIC_PATH) VALUES ('"+random_number+"','"+pic+"')";
					this.getBySqlMapper.insert(in_sql);
				}
			}
			response.getWriter().write("111111111111111111111");
	}
	/**
	 * 导入帮扶人
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("perspnalHouserhold.do")
	public void perspnalHouserhold(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		String sql = "select NAME,TEL,A4,A6 from (SELECT NAME,tel,id  from AR_GB_KL ) a left join  (select KEYWORD,YN from AR_KL ) b on  a.id= b.keyword LEFT JOIN("+
					" select nid,A4,A6 from AR_DAKL)c on  b.yn=c.nid where KEYWORD is not null GROUP BY NAME,TEL,A4,A6 ";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		for ( int i = 0 ; i < list.size() ; i++) {
			String  household_name = "".equals(list.get(i).get("A4")) || list.get(i).get("A4") == null ? "" : list.get(i).get("A4").toString();
			String  household_card = "".equals(list.get(i).get("A6")) || list.get(i).get("A6") == null ? "" : list.get(i).get("A6").toString();
			String personal_name = "".equals(list.get(i).get("NAME")) || list.get(i).get("NAME") == null ? "" : list.get(i).get("NAME").toString();
			String personal_phone = "".equals(list.get(i).get("TEL")) || list.get(i).get("TEL") == null ? "" : list.get(i).get("TEL").toString();
			
			String cha_sql = "select * from  SYS_PERSONAL_HOUSEHOLD_MANY where PERSONAL_NAME='"+personal_name+"' and HOUSEHOLD_NAME='"+household_name+"' and "+
							" PERSONAL_PHONE='"+personal_phone+"' and HOUSEHOLD_CARD='"+household_card+"'";
			List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
			if ( cha_list.size() > 0 && cha_list.get(0)!=null ) {
				
			}else {
				String in_sql = " insert into SYS_PERSONAL_HOUSEHOLD_MANY (PERSONAL_NAME,HOUSEHOLD_NAME,PERSONAL_PHONE,HOUSEHOLD_CARD) VALUES"+
						" ('"+personal_name+"','"+household_name+"','"+personal_phone+"','"+household_card+"')";
				this.getBySqlMapper.insert(in_sql);
			}
			
			
		}
		response.getWriter().write("111111111111111111111");
	}
	/**
	 * 导入阿荣旗的走访记录以及走访记录照片1
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("gethou1.do")
	public void gethou1(HttpServletRequest request,HttpServletResponse response ) throws IOException {

		String  sql = "SELECT A4,A6,A37,A41,AAB002,AAB004, GPS,CONTENT,time,pic,BB.AAC001,A41 tel,AAR008 FROM (SELECT NID,A4,A6,A37,A41 FROM AR_DAKL1 WHERE A7='户主'"+
						" )AA LEFT JOIN   (SELECT AAC001,AAB002,AAB004,AAR008 FROM NEIMENG0117_AB01 WHERE AAR040='2016' and AAB006='01' and AAB015='1' "+
						"	) BB ON AA.A4=BB.AAB002 AND AA.A6=BB.AAB004   LEFT JOIN ("+
						"		SELECT GPS,CONTENT,time,YN,PICTURE pic,KEYWORD FROM AR_KL1 )DD ON AA.NID=DD.YN "+
						"  WHERE BB.AAC001 IS NOT NULL and CONTENT is not null";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
			for ( int i = 1 ; i < list.size() ; i++ ) {
				Date date = new Date();
				SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
				String random_number = sf.format(date)+"_"+new Random().nextInt(1000);//时间戳+随机数
				String household_name = "".equals(list.get(i).get("A4")) || list.get(i).get("A4") == null ? "" : list.get(i).get("A4").toString();//贫困户
				String household_crad = "".equals(list.get(i).get("A6")) || list.get(i).get("A6") == null ? "" : list.get(i).get("A6").toString();//贫困户证件号码
				String  personal_phone = "".equals(list.get(i).get("TEL")) || list.get(i).get("TEL") == null ? "" : list.get(i).get("TEL").toString();//帮扶人电话
				String personal_name = "".equals(list.get(i).get("A37")) || list.get(i).get("A37") == null ? "" : list.get(i).get("A37").toString();//帮扶人
				String v1 = "".equals(list.get(i).get("TIME")) || list.get(i).get("TIME") == null ? "" : list.get(i).get("TIME").toString();//时间
				String time = v1.replaceAll("/", "-");
				String v3 = "".equals(list.get(i).get("CONTENT")) || list.get(i).get("CONTENT") == null ? "" : list.get(i).get("CONTENT").toString();//走访记录
				String pic = "".equals(list.get(i).get("PIC")) || list.get(i).get("PIC") == null ? "" : list.get(i).get("PIC").toString();//照片
				String gps = "".equals(list.get(i).get("GPS")) || list.get(i).get("GPS") == null ? "" : list.get(i).get("GPS").toString();//位置
				String lag[] = gps.split(",");
				String lng = "";
				String lat = "";
				if ( lag.length > 0 && lag[0] != "") {
					 lng = lag[1];
					 lat = lag[0];
				}
				String AAR008 = "".equals(list.get(i).get("AAR008")) || list.get(i).get("AAR008") == null ? "" : list.get(i).get("AAR008").toString();//村的编码
				String cha_sql = "select * from DA_HELP_VISIT where HOUSEHOLD_NAME='"+household_name+"' and PERSONAL_NAME='"+personal_name+"' and V1='"+v1+"' and "+
								" LNG='"+lng+"' and lat='"+lat+"' and v3='"+v3+"' and HOUSEHOLD_CARD='"+household_crad+"' and PERSONAL_PHONE ='"+personal_phone+"' and "+
								" AAR008='"+AAR008+"'";
				List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
				if ( cha_list.size() > 0 ) {
					
				} else {
					String inset_sql ="INSERT INTO DA_HELP_VISIT(HOUSEHOLD_NAME,PERSONAL_NAME,V1,LNG,LAT,V3,HOUSEHOLD_CARD,PERSONAL_PHONE,RANDOM_NUMBER,AAR008)"+
							" VALUES('"+household_name+"','"+personal_name+"','"+time+"','"+lng+"','"+lat+"','"+v3+"','"+household_crad+"','"+personal_phone+"','"+random_number+"','"+AAR008+"')";
					this.getBySqlMapper.insert(inset_sql);
					String in_sql = "INSERT INTO DA_PIC_VISIT (RANDOM_NUMBER,PIC_PATH) VALUES ('"+random_number+"','"+pic+"')";
					this.getBySqlMapper.insert(in_sql);
				}
			}
			response.getWriter().write("111111111111111111111");
	}
	/**
	 * 导入帮扶人1
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("perspnalHouserhold1.do")
	public void perspnalHouserhold1(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		String sql ="SELECT A4,A6,A37 name ,A41 tel FROM AR_DAKL1 WHERE A7='户主' ";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		for ( int i = 0 ; i < list.size() ; i++) {
			String  household_name = "".equals(list.get(i).get("A4")) || list.get(i).get("A4") == null ? "" : list.get(i).get("A4").toString();
			String  household_card = "".equals(list.get(i).get("A6")) || list.get(i).get("A6") == null ? "" : list.get(i).get("A6").toString();
			String personal_name = "".equals(list.get(i).get("NAME")) || list.get(i).get("NAME") == null ? "" : list.get(i).get("NAME").toString();
			String personal_phone = "".equals(list.get(i).get("TEL")) || list.get(i).get("TEL") == null ? "" : list.get(i).get("TEL").toString();
			
			String cha_sql = "select * from  SYS_PERSONAL_HOUSEHOLD_MANY where PERSONAL_NAME='"+personal_name+"' and HOUSEHOLD_NAME='"+household_name+"' and "+
							" PERSONAL_PHONE='"+personal_phone+"' and HOUSEHOLD_CARD='"+household_card+"'";
			List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
			if ( cha_list.size() > 0 && cha_list.get(0)!=null ) {
				
			}else {
				String in_sql = " insert into SYS_PERSONAL_HOUSEHOLD_MANY (PERSONAL_NAME,HOUSEHOLD_NAME,PERSONAL_PHONE,HOUSEHOLD_CARD) VALUES"+
						" ('"+personal_name+"','"+household_name+"','"+personal_phone+"','"+household_card+"')";
				this.getBySqlMapper.insert(in_sql);
			}
			
			
		}
		response.getWriter().write("111111111111111111111");
	}

	@RequestMapping("fei_pic.do")
	public void fei_pic(HttpServletRequest request,HttpServletResponse response ) {
		String sql ="select pic_path,household_name,household_card,personal_name,personal_phone,v1,v3 from ( "+
					" select household_name,household_card,personal_name,personal_phone, random_number,v1,v3 FROM DA_HELP_VISIT ) a LEFT JOIN ( "+
					" select random_number b_num,pic_path from DA_PIC_VISIT)b on a.random_number = b.b_num where pic_path is not null";
		
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		for( int i =0;i<list.size();i++ ) {
			
			String PERSONAL_NAME = "".equals(list.get(i).get("PERSONAL_NAME")) || list.get(i).get("PERSONAL_NAME") == null ? "" : list.get(i).get("PERSONAL_NAME").toString();
			String PERSONAL_PHONE = "".equals(list.get(i).get("PERSONAL_PHONE")) || list.get(i).get("PERSONAL_PHONE") == null ? "" : list.get(i).get("PERSONAL_PHONE").toString();
			String HOUSEHOLD_NAME = "".equals(list.get(i).get("HOUSEHOLD_NAME")) || list.get(i).get("HOUSEHOLD_NAME") == null ? "" : list.get(i).get("HOUSEHOLD_NAME").toString();
			String HOUSEHOLD_CARD = "".equals(list.get(i).get("HOUSEHOLD_CARD")) || list.get(i).get("HOUSEHOLD_CARD") == null ? "" : list.get(i).get("HOUSEHOLD_CARD").toString();
			String V3 = "".equals(list.get(i).get("V3")) || list.get(i).get("V3") == null ? "" : list.get(i).get("V3").toString();
			String PIC_PATH = "".equals(list.get(i).get("PIC_PATH")) || list.get(i).get("PIC_PATH") == null ? "" : list.get(i).get("PIC_PATH").toString();
			String V1 = "".equals(list.get(i).get("V1")) || list.get(i).get("V1") == null ? "" : list.get(i).get("V1").toString();
			File f= new File(list.get(i).get("PIC_PATH").toString().replace("/assa/attached", "E:/attached")); 
			if (f.exists() && f.isFile()){ 
				 if(f.length()>1024){
					
				 } else {//图片破损
					 String in_sql = "insert into PIC_FEI_VISIT (PERSONAL_NAME,PERSONAL_PHONE,HOUSEHOLD_NAME,HOUSEHOLD_CARD,V3,V1,PIC_PATH,TYPE) VALUES "+
						 		" ('"+PERSONAL_NAME+"','"+PERSONAL_PHONE+"','"+HOUSEHOLD_NAME+"','"+HOUSEHOLD_CARD+"','"+V3+"','"+V1+"','"+PIC_PATH+"','图片损坏')";
					 this.getBySqlMapper.insert(in_sql);
				 }
			}else {//图片不存在
				 String in_sql = "insert into PIC_FEI_VISIT (PERSONAL_NAME,PERSONAL_PHONE,HOUSEHOLD_NAME,HOUSEHOLD_CARD,V3,V1,PIC_PATH,TYPE) VALUES "+
					 		" ('"+PERSONAL_NAME+"','"+PERSONAL_PHONE+"','"+HOUSEHOLD_NAME+"','"+HOUSEHOLD_CARD+"','"+V3+"','"+V1+"','"+PIC_PATH+"','图片不存在')";
			 this.getBySqlMapper.insert(in_sql);
				
			}
		}
	}
	@RequestMapping("fei_url.do")
	public void fei_url(HttpServletRequest request,HttpServletResponse response) throws IOException {
		long startTime = System.currentTimeMillis();    //获取开始时间
		String  sql = " select pic_path from DA_PIC_VISIT";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		
		for ( int i = 0 ; i < list.size() ; i ++ ) {
			String pic_path = "".equals(list.get(i).get("PIC_PATH")) || list.get(i).get("PIC_PATH")==null ?"": list.get(i).get("PIC_PATH").toString();
			String pkid = "".equals(list.get(i).get("PKID")) || list.get(i).get("PKID")==null ?"": list.get(i).get("PKIDs").toString();
			
			File f= new File(list.get(i).get("PIC_PATH").toString().replace("/assa/attached", "E:/attached")); 
			
			if (f.exists() && f.isFile()){ //存在
				
				
			}else { //不存在
				  String[] ary = list.get(i).get("PIC_PATH").toString().replace("/assa/attached", "E:/attached").split(".");//
				  String[] pic_type = {"gif","jpg","jpeg","png","bmp"};
				  for ( int j = 0 ; j < pic_type.length;j++ ) {
					  File f1= new File(ary[0]+pic_type[j]);
					  if ( f1.exists() && f1.isFile() ) {
						  String in_sql = "insert into pic_fei_url (pic_id,pic_path,d_path,y_h,x_h) values "+
								  			"('"+pkid+"','"+pic_path+"','"+ary[0]+pic_type[j]+"','"+ary[0]+"','"+pic_type[j]+"')";
						  this.getBySqlMapper.insert(in_sql);
					  }
				  }
			}
		}
		long endTime = System.currentTimeMillis();    //获取结束时间
	    System.out.println("程序运行时间：" + (endTime - startTime)/1000 + "s");    //输出程序运行时间
		response.getWriter().write("程序运行时间：" + (endTime - startTime)/1000 + "s");

	}
	

}
