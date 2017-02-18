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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.gistone.WeChatApp;
import com.gistone.MyBatis.config.GetBySqlMapper;

@RestController
@RequestMapping
public class AnController{
	
	@Autowired
	private GetBySqlMapper getBySqlMapper;
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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String phone = request.getParameter("phone");//电话13904794720
		String password = request.getParameter("password");//密码
		String str = phone.substring(5,11);
		String sql = "select * from SYS_PERSONAL_HOUSEHOLD_MANY where PERSONAL_PHONE ='"+phone+"'";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		if ( list.size() > 0 ) {
			if ( "".equals(list.get(0).get("PASSWORD")) || list.get(0).get("PASSWORD") == null ){
				if ( password.equals(phone.substring(5,11))){
					response.getWriter().write("{\"success\":0,\"message\":\"登录成功\",\"data\":{\"phone\":"+phone+",\"name\":\""+list.get(0).get("PERSONAL_NAME").toString()+"\"}}");//登录成功
				}else {
					response.getWriter().write("{\"success\":1,\"message\":\"密码错误\",\"data\":\"\"}");
				}
			} else {
				if (password.equals(list.get(0).get("PASSWORD"))){
					response.getWriter().write("{\"success\":0,\"message\":\"登录成功\",\"data\":{\"phone\":"+phone+",\"name\":\""+list.get(0).get("PERSONAL_NAME").toString()+"\"}}");//登录成功
				}else{
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
			
			sql = " select REGISTERTIME,v3,address,pic_path,random_number,aa.household_name,aa.household_card,personal_name,REGISTERTYPE　from "+
					"(select RANDOM_NUMBER nn, personal_name,household_name,household_card,personal_phone from DA_HELP_VISIT where "+
					" household_name='"+household_name+"' and household_card='"+household_cord+"')aa LEFT JOIN ( "+
					"select REGISTERTIME,v3,ADDRESS,wmsys.wm_concat(PIC_PATH) PIC_PATH,a.RANDOM_number,household_name,household_card,REGISTERTYPE from "+
					"  (select * from DA_HELP_VISIT where household_name='"+household_name+"' AND household_card='"+household_cord+"') a left join "+
					" (select * from DA_PIC_VISIT) b on a.RANDOM_NUMBER = b.RANDOM_NUMBER GROUP BY REGISTERTIME,v3,ADDRESS,a.RANDOM_number, "+
					" household_name,household_card,REGISTERTYPE  )bb on aa.nn=bb.RANDOM_NUMBER ORDER BY REGISTERTIME DESC";
			
		} else if ("".equals(household_name) || household_name == null ) {
			
			sql = "select REGISTERTIME,v3,address,pic_path,random_number,aa.household_name,aa.household_card,personal_name,REGISTERTYPE　from "+
					"(select RANDOM_NUMBER nn,  personal_name,household_name,household_card,personal_phone from DA_HELP_VISIT where "+
					"  personal_name='"+personal_name+"' and personal_phone='"+personal_phone+"')aa LEFT JOIN ( "+
					"select REGISTERTIME,v3,ADDRESS,wmsys.wm_concat(PIC_PATH) PIC_PATH,a.RANDOM_number,household_name,household_card,REGISTERTYPE from "+
					"  (select * from DA_HELP_VISIT where PERSONAL_NAME='"+personal_name+"' AND PERSONAL_PHONE='"+personal_phone+"') a left join "+
					" (select * from DA_PIC_VISIT) b on a.RANDOM_NUMBER = b.RANDOM_NUMBER GROUP BY REGISTERTIME,v3,ADDRESS,a.RANDOM_number, "+
					" household_name,household_card,REGISTERTYPE  )bb on aa.nn=bb.RANDOM_NUMBER ORDER BY REGISTERTIME DESC";
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
			String insert_sql = "insert into DA_HELP_VISIT (household_name,personal_name,v1,v3,lng,lat,address,household_card,personal_phone,random_number,AAR008,REGISTERTIME,SENDLAT,SENDLNG,REGISTERTYPE)"+
					" values ('"+household_name+"','"+personal_name+"','"+simpleDate.format(new Date())+"','"+v3+"','"+lng+"','"+lat+"','"+address+"','"+household_card+"','"+personal_phone+"','"+random_number+"','"+AAR008+"','"+registerTime+"','"+sendLat+"','"+sendLng+"','"+registerType+"')";
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
			String hql = "INSERT INTO DA_HELP_VISIT(HOUSEHOLD_NAME,PERSONAL_NAME,V1,V3,LNG,LAT,HOUSEHOLD_CARD,PERSONAL_PHONE,RANDOM_NUMBER,AAR008,REGISTERTIME,SENDLAT,SENDLNG,REGISTERTYPE,TYPE)"+
					" VALUES('"+household_name+"','"+personal_name+"','"+simpleDate.format(new Date())+"','"+zfjl+"','"+longitude+"','"+latitude+"','"+household_card+"','"+personal_phone+"','"+random_number+"','"+AAR008+"','"+registerTime+"','"+sendLat+"','"+sendLng+"','"+registerType+"','微信')";
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
	
	
	/***************************精准扶贫帮扶通领导版接口*******************************/
	
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
		if(cType!=null&&!"".equals(cType)&&Integer.valueOf(cType)==1){//省
			sql="select V1 as V1,V2 as V2 from sys_com GROUP BY V1,V2";
			list = this.getBySqlMapper.findRecords(sql);
		}else if(cType!=null&&!"".equals(cType)&&Integer.valueOf(cType)==2){//查询所有市级
			sql="select V3 as V1,V4 as V2 from sys_com GROUP BY V3,V4";
			list = this.getBySqlMapper.findRecords(sql);
		}else if(cType!=null&&!"".equals(cType)&&Integer.valueOf(cType)==3){//查询所有县级
			sql="select V4,V5 as V1,V6 as V2 from sys_com ";
			if(code!=null&&!"".equals(code)){
				sql+=" where V4="+code;
			}
			sql+=" GROUP BY V4,V5,V6 ";
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
		}else if(Integer.valueOf(ctype)==3){
			codes=code;
		}
		return codes;
	}
	/**获取日记统计参数
	 * 1、当前帮扶日记总条数：diary_sum					int
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
	    System.out.println("进入接口时间："+startTime);
	    JSONArray jn = new JSONArray();
	    String cType = request.getParameter("cType");//查询类别1、省2、市3、县
	    String code = request.getParameter("code");//当前查询的区域范围如默认内蒙古自治区
	    if(Integer.valueOf(cType)>1){
	    	code=this.getXjcode(cType,code);//根据当前传的行政区划code获取所有村级行政区划
	    }
	    String sTime = request.getParameter("stime");//开始时间
	    String eTime = request.getParameter("etime");//结束时间
	    //日记总条数、走访贫困户总数、上传走访记录干部总数、当天日记数、本周日记数、本月日记数 DA_HELP_VISIT
	    String sqlx = "SELECT	COUNT (*) AS diary_sum,	COUNT (DISTINCT(household_card)) AS d_poor_sum,	COUNT (DISTINCT(personal_phone)) AS d_cadre_sum,	count(case when to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') then 'a00' end)day,	count(	CASE when to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'iw')=to_char(sysdate,'iw') THEN 'a01' end)week,	count(	CASE when to_char(to_date(registertime,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm')=to_char(sysdate,'yyyy-mm') THEN 'a02' end)month	FROM	xxx  WHERE 1=1";

	    String sql1 = "select count(*) as poor_sum from NEIMENG0117_AC01 WHERE 1=1";//总贫困户数
	    String sql2 = "select COUNT(*) AS CADRE_SUM from NEIMENG0117_AK11 T1 JOIN (select AAK110 from NEIMENG0117_AC08  WHERE AAR100=1 GROUP BY AAK110) T2 ON T1.AAK110=T2.AAK110 WHERE 1=1";//总帮扶干部数
	    String sqlc = "select * from DA_HELP_VISIT where 1=1 ";//计算走访覆盖率子查询语句
	    
	    if(code!=null&&!"".equals(code)&&Integer.valueOf(cType)>1){//按区域查
	    	sqlx+=" and AAR008 like('%"+code+"%')";
	    	sql1+=" and AAR008 like('%"+code+"%')";
	    	sql2+=" and T1.AAR008 like('%"+code+"%')";
	    	sqlc+=" and AAR008 like('%"+code+"%')";
	    }
	    if(sTime!=null&&!"".equals(sTime)){//按时间查
	    	sqlx+=" and to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') >= to_date('"+sTime+"','yyyy-mm-dd')";
	    	sqlc+=" and to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') >= to_date('"+sTime+"','yyyy-mm-dd')";
	    }
		if(eTime!=null&&!"".equals(eTime)){//按时间查
			sqlx+=" AND to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') <= to_date('"+eTime+"','yyyy-mm-dd')";	
			sqlc+=" AND to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') <= to_date('"+eTime+"','yyyy-mm-dd')";
	    }
		//计算走访覆盖率 分组前
		String sql3 = "select count(*)  as summ from ("+sqlc+") tt left join SYS_PERSONAL_HOUSEHOLD_MANY ti  on tt.PERSONAL_NAME = ti.PERSONAL_NAME and tt.HOUSEHOLD_NAME = ti.HOUSEHOLD_NAME and tt.PERSONAL_PHONE = ti.PERSONAL_PHONE and tt.HOUSEHOLD_CARD = ti.HOUSEHOLD_CARD ";
		//计算走访覆盖率 分组后
	    String sql4 = "select count(count(*))  as summ from ("+sqlc+") tt left join SYS_PERSONAL_HOUSEHOLD_MANY ti  on tt.PERSONAL_NAME = ti.PERSONAL_NAME and tt.HOUSEHOLD_NAME = ti.HOUSEHOLD_NAME and tt.PERSONAL_PHONE = ti.PERSONAL_PHONE and tt.HOUSEHOLD_CARD = ti.HOUSEHOLD_CARD GROUP BY tt.PERSONAL_NAME,tt.PERSONAL_PHONE,tt.HOUSEHOLD_NAME,tt.HOUSEHOLD_CARD";
	    sql1+=" and AAR010 IN (0,3) and AAR100=1";//未脱贫 有效性
		JSONObject jb = new JSONObject();
	    List<Map> listx = this.getBySqlMapper.findRecords(sqlx);
	    List<Map> list1 = this.getBySqlMapper.findRecords(sql1);
	    List<Map> list2 = this.getBySqlMapper.findRecords(sql2);
	    List<Map> list3 = this.getBySqlMapper.findRecords(sql3);
	    List<Map> list4 = this.getBySqlMapper.findRecords(sql4);
	    //统计数
	    if(listx.size()>0){
	    	jb.put("diary_sum", listx.get(0).get("DIARY_SUM")==null?0:listx.get(0).get("DIARY_SUM"));//总日记条数
	    	jb.put("d_poor_sum", listx.get(0).get("D_POOR_SUM")==null?0:listx.get(0).get("D_POOR_SUM"));//走访相关贫困户数
	    	jb.put("d_cadre_sum", listx.get(0).get("D_CADRE_SUM")==null?0:listx.get(0).get("D_CADRE_SUM"));//走访记录干部总数
	    	jb.put("day_sum", listx.get(0).get("DAY")==null?0:listx.get(0).get("DAY"));//当天日记条数
	    	jb.put("week_sum", listx.get(0).get("WEEK")==null?0:listx.get(0).get("WEEK"));//当周日记条数
	    	jb.put("month_sum", listx.get(0).get("MONTH")==null?0:listx.get(0).get("MONTH"));//当月日记条数
	    }else{
	    	jb.put("diary_sum", 0);
	    	jb.put("d_poor_sum", 0);
	    	jb.put("d_cadre_sum", 0);
	    	jb.put("day_sum", 0);
	    	jb.put("week_sum", 0);
	    	jb.put("month_sum", 0);
	    }
	    //总贫困户数
	    if(list1.size()>0){
	    	jb.put("poor_sum", list1.get(0).get("POOR_SUM")==null?0:list1.get(0).get("POOR_SUM"));
	    }else{
	    	jb.put("poor_sum", 0);
	    }
	    //总帮扶干部数
	    if(list2.size()>0){
	    	jb.put("cadre_sum", list2.get(0).get("CADRE_SUM")==null?0:list2.get(0).get("CADRE_SUM"));
	    }else{
	    	jb.put("cadre_sum", 0);
	    }
	    DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
	    //落实责任人比例 帮扶贫困户/总贫困户
	    if(list1.size()>0&&listx.size()>0){
	    	jb.put("assist_coverage",  df.format((float)Integer.valueOf(listx.get(0).get("D_POOR_SUM").toString())/Integer.valueOf(list1.get(0).get("POOR_SUM").toString())));
	    }
	    
	    //走访覆盖率   登录表（即结对表）关联除以  走访表=走访覆盖率
	    jb.put("d_poor_coverage",  df.format((float)Integer.valueOf(list4.get(0).get("SUMM").toString())/Integer.valueOf(list3.get(0).get("SUMM").toString())));
	    //上传走访记录干部占总数比 走访干部/总干部
	    
	    jb.put("d_cadre_proportion",  df.format((float)Integer.valueOf(listx.get(0).get("D_CADRE_SUM").toString())/Integer.valueOf(list2.get(0).get("CADRE_SUM").toString())));
	    jn.add(jb);
	    long endTime = System.currentTimeMillis();    //获取结束时间
	    System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
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
	    if(Integer.valueOf(cType)>1){
	    	code=this.getXjcode(cType,code);//根据当前传的行政区划code获取所有村级行政区划
	    }
	    String pageNum = request.getParameter("pageNum");//分页页码
	    String sTime = request.getParameter("stime");//开始时间
	    String eTime = request.getParameter("etime");//结束时间
	    String phone = request.getParameter("phone");//干部电话
	    String name = request.getParameter("name");//干部名称
	    int start =0;
	    if(Integer.valueOf(pageNum)>1){
	    	for(int i=0;i<Integer.valueOf(pageNum)-1;i++){
	    		start+=20;
	    	}
	    }
	    int end = 20*Integer.valueOf(pageNum);
	    JSONArray jn = new JSONArray();
	    String sqlX="select * from (";
	    String sql = "select t1.*,ROWNUM as rn,t2.pic_path from DA_HELP_VISIT t1 left join DA_PIC_VISIT t2 on t1.random_number=t2.random_number where 1=1 ";//DA_HELP_VISIT
	    if(code!=null&&!"".equals(code)&&Integer.valueOf(cType)>1){
	    	sql+=" and AAR008 like('%"+code+"%')";
	    }
	    if(sTime!=null&&!"".equals(sTime)){
	    	sql+=" and to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') >= to_date('"+sTime+"','yyyy-mm-dd')";
	    }
		if(eTime!=null&&!"".equals(eTime)){
			sql+=" AND to_date(REGISTERTIME,'yyyy-mm-dd hh24:mi:ss') <= to_date('"+eTime+"','yyyy-mm-dd')";	
	    }
		if(phone!=null&&!"".equals(phone)){
			sql+=" and PERSONAL_PHONE LIKE '%"+phone+"%'";	
		}
		if(name!=null&&!"".equals(name)){
			sql+=" and PERSONAL_NAME LIKE '%"+name+"%'";	
		}
		sqlX+=sql +") rr";
		sqlX+=" where rr.rn <="+end+" and rr.rn>"+start+" order by v1 desc";
		
		List<Map> list = this.getBySqlMapper.findRecords(sqlX);
	    if(list.size()>0){
	    	for(int l=0;l<list.size();l++){
	    		JSONObject jb = new JSONObject();
	    		jb.put("name", list.get(l).get("PERSONAL_NAME"));//帮扶人名称
	    		jb.put("hname", list.get(l).get("HOUSEHOLD_NAME"));//贫困户名称
	    		jb.put("phone", list.get(l).get("PERSONAL_PHONE"));//帮扶人电话
	    		jb.put("content", list.get(l).get("V3"));//帮扶内容
	    		jb.put("time", list.get(l).get("REGISTERTIME"));//帮扶日期
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
	    	sqlc+=" and PERSONAL_NAME like '%"+name+"%' ";
	    }
	    if(phone!=null&&!"".equals(phone)){
	    	sqlc+=" and PERSONAL_PHONE like '%"+phone+"%' ";
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
	    JSONArray jn = new JSONArray();
	    JSONObject jb = new JSONObject();
	    //贫困户总数、已脱贫户数、因病、因残、缺土地、缺资金、一般贫困户、低保贫困户、五保贫困户
	    String sql1 = "select count(a.AAC001) AS 贫困户总数,count(CASE when a.AAR010=1 then 'a01'end) 已脱贫户数,count(CASE when a.AAC007=1 then 'a02'end) 因病,count(CASE when a.AAC007=2 then 'a03'end) 因残,count(CASE when a.AAC007=5 then 'a04'end) 缺土地,count(CASE when a.AAC007=9 then 'a05'end) 缺资金,count(CASE when a.AAC006=1 then 'a06'end) 一般贫困户,count(CASE when a.AAC006=4 then 'a07'end) 低保贫困户,count(CASE when a.AAC006=6 then 'a08'end) 五保贫困户 from NEIMENG0117_AC01 a where 1=1";
	    String sqlc = "select AAC001 from NEIMENG0117_Ac01  where AAR010 in (0,3) and AAR100=1 ";
	    if(code!=null&&!"".equals(code)&&Integer.valueOf(cType)>1){
	    	sql1+=" and AAR008 like('%"+code+"%')";
	    	sqlc+=" and AAR008 like('%"+code+"%')";
	    }
	    sql1+=" and AAR010 IN(0,3) and AAR100=1";
	    String sql0 = "select count(*) from( select q1.AAC001 from ("+sqlc+")q LEFT JOIN  ( select AAC001 from  NEIMENG0117_AB01 where aab015=1 )q1 on q.AAC001= q1.AAC001)";//贫困人口
	    List<Map> list1 = this.getBySqlMapper.findRecords(sql1);
	    List<Map> list0 = this.getBySqlMapper.findRecords(sql0);
	    if(list0.size()>0){
	    	jb.put("pkrk", list0.get(0).get("pkrk"));//贫困人口
	    }
	    if(list1.size()>0){
	    	jb.put("pkhzs", list1.get(0).get("贫困户总数"));
	    	jb.put("ytphs", list1.get(0).get("已脱贫户数"));
	    	jb.put("yb", list1.get(0).get("因病"));
	    	jb.put("yc", list1.get(0).get("因残"));
	    	jb.put("qtd", list1.get(0).get("缺土地"));
	    	jb.put("qzj", list1.get(0).get("缺资金"));
	    	jb.put("ybpkh", list1.get(0).get("一般贫困户"));
	    	jb.put("dbpkh", list1.get(0).get("低保贫困户"));
	    	jb.put("wbpkh", list1.get(0).get("五保贫困户"));
	    }
	    jn.add(jb);
	    response.getWriter().write(jn.toString());
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
		response.getWriter().write("111111111111111111111");

	}
	

}
