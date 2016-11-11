package com.gistone.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


import java.io.ByteArrayInputStream;  

 
  




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gistone.MyBatis.config.GetBySqlMapper;
import com.gistone.util.CaptchaUtil;
import com.gistone.util.OverallSituation;
import com.gistone.util.Tool;

@RestController
@RequestMapping
public class Index_Controller{
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	private ByteArrayInputStream inputStream; 
	
	//获取验证码的方法
	@RequestMapping("img.do")
	@ResponseBody
	public void execute(HttpServletRequest request , HttpServletResponse response) throws Exception {  
       
		CaptchaUtil.outputCaptcha(request, response);
        
    }  
	@RequestMapping("yzm.do")
	public void yzm(HttpServletRequest request , HttpServletResponse response) throws Exception {  
	       
		String zhi= request.getParameter("zhi");
		HttpSession session = request.getSession();//取session
		String randomString=session.getAttribute("randomString").toString();
		zhi = zhi.toUpperCase();
		if(zhi.equals(randomString)){
			response.getWriter().write("1");
		}else{
			response.getWriter().write("0");
		}
    }  
	
	//判断单位层级，生成单位树结构
	public Object[] get_company_tree(String com_level, String sys_company_id, String pkid, String com_name){
		Object[] obj = new Object[2];
		JSONObject company_tree = new JSONObject();
		if(com_level.equals("1")){
			Map Com = new HashMap();
			Com.put("id_1", pkid);
			Com.put("name_1", com_name);
			obj[0] = Com;
			
			company_tree = OverallSituation.comp;
			
		}else if(com_level.equals("2")){
			
			String sql_3 = "select t2.pkid id_1,t2.com_name name_1,t1.pkid id_2,t1.com_name name_2 from sys_company t1 "
					+ "join sys_company t2 on t1.com_f_pkid=t2.pkid where t1.pkid="+sys_company_id;
			Map Com = this.getBySqlMapper.findRecords(sql_3).get(0);
			obj[0] = Com;
			
			JSONArray ja = JSONArray.fromObject(OverallSituation.comp.get("nodes"));//所有的市
			for (int i = 0; i < ja.size(); i++) {
				JSONObject val = JSONObject.fromObject(ja.get(i));
				if(val.get("com_name").toString().equals(Com.get("NAME_2").toString())){
					company_tree = val;
					break;
				}
			}
			
		}else if(com_level.equals("3")){
			
			String sql_3 = "select t3.pkid id_1,t3.com_name name_1,t2.pkid id_2,t2.com_name name_2,t1.pkid id_3,t1.com_name name_3 from sys_company t1 "
					+ "join sys_company t2 on t1.com_f_pkid=t2.pkid join sys_company t3 on t2.com_f_pkid=t3.pkid where t1.pkid="+sys_company_id;
			Map Com = this.getBySqlMapper.findRecords(sql_3).get(0);
			obj[0] = Com;
			
			JSONArray ja = JSONArray.fromObject(OverallSituation.comp.get("nodes"));//所有的市
			for (int i = 0; i < ja.size(); i++) {
				JSONObject val = JSONObject.fromObject(ja.get(i));
				if(val.get("com_name").toString().equals(Com.get("NAME_2").toString())){
					JSONArray jb = JSONArray.fromObject(val.get("nodes"));//所有的县
					for (int j = 0; j < jb.size(); j++) {
						JSONObject val_3 = JSONObject.fromObject(jb.get(j));
						if(val_3.get("com_name").toString().equals(Com.get("NAME_3").toString())){
							company_tree = val_3;
							break;
						}
					}
					break;
				}
			}
			
		}else if(com_level.equals("4")){
			
			String sql_3 = "select t4.pkid id_1,t4.com_name name_1,t3.pkid id_2,t3.com_name name_2,t2.pkid id_3,t2.com_name name_3,t1.pkid id_4,t1.com_name name_4 from "
					+ "sys_company t1 join sys_company t2 on t1.com_f_pkid=t2.pkid join sys_company t3 on t2.com_f_pkid=t3.pkid "
					+ "join sys_company t4 on t3.com_f_pkid=t4.pkid where t1.pkid="+sys_company_id;
			Map Com = this.getBySqlMapper.findRecords(sql_3).get(0);
			obj[0] = Com;
			
			JSONArray ja = JSONArray.fromObject(OverallSituation.comp.get("nodes"));//所有的市
			for (int i = 0; i < ja.size(); i++) {
				JSONObject val = JSONObject.fromObject(ja.get(i));
				if(val.get("com_name").toString().equals(Com.get("NAME_2").toString())){
					JSONArray jb = JSONArray.fromObject(val.get("nodes"));//所有的县
					for (int j = 0; j < jb.size(); j++) {
						JSONObject val_3 = JSONObject.fromObject(jb.get(j));
						if(val_3.get("com_name").toString().equals(Com.get("NAME_3").toString())){
							JSONArray jc = JSONArray.fromObject(val_3.get("nodes"));//所有的乡
							for (int k = 0; k < jc.size(); k++) {
								JSONObject val_4 = JSONObject.fromObject(jc.get(k));
								if(val_4.get("com_name").toString().equals(Com.get("NAME_4").toString())){
									company_tree = val_4;
									break;
								}
							}
							break;
						}
					}
					break;
				}
			}
			
		}else if(com_level.equals("5")){
			
			String sql_3 = "select t4.pkid id_1,t4.com_name name_1,t3.pkid id_2,t3.com_name name_2,t2.pkid id_3,t2.com_name name_3,t1.pkid id_4,t1.com_name name_4 from "
					+ "sys_company t1 join sys_company t2 on t1.com_f_pkid=t2.pkid join sys_company t3 on t2.com_f_pkid=t3.pkid "
					+ "join sys_company t4 on t3.com_f_pkid=t4.pkid where t1.pkid="+sys_company_id;
			Map Com = this.getBySqlMapper.findRecords(sql_3).get(0);
			obj[0] = Com;
			
			JSONArray ja = JSONArray.fromObject(OverallSituation.comp.get("nodes"));//所有的市
			for (int i = 0; i < ja.size(); i++) {
				JSONObject val = JSONObject.fromObject(ja.get(i));
				if(val.get("com_name").toString().equals(Com.get("NAME_2").toString())){
					JSONArray jb = JSONArray.fromObject(val.get("nodes"));//所有的县
					for (int j = 0; j < jb.size(); j++) {
						JSONObject val_3 = JSONObject.fromObject(jb.get(j));
						if(val_3.get("com_name").toString().equals(Com.get("NAME_3").toString())){
							JSONArray jc = JSONArray.fromObject(val_3.get("nodes"));//所有的乡
							for (int k = 0; k < jc.size(); k++) {
								JSONObject val_4 = JSONObject.fromObject(jc.get(k));
								if(val_4.get("com_name").toString().equals(Com.get("NAME_4").toString())){
									JSONArray jd = JSONArray.fromObject(val_4.get("nodes"));//所有的乡
									for (int m = 0; m < jd.size(); m++) {
										JSONObject val_5 = JSONObject.fromObject(jd.get(m));
										if(val_5.get("com_name").toString().equals(Com.get("NAME_5").toString())){
											company_tree = val_5;
											break;
										}
									}
									break;
								}
							}
							break;
						}
					}
					break;
				}
			}
			
		}
		JSONArray val = new JSONArray();
		val.add(company_tree);
		obj[1] = company_tree;
		return obj;
	}
	
	//登录验证
	@RequestMapping("loginin.do")
	public void loginin(HttpServletRequest request,HttpServletResponse response) throws Exception{

		String username = request.getParameter("add_account");//获取用户名 
		String password = request.getParameter("add_password");//获取密码
		String people_sql = "select t1.pkid,t1.col_account,t1.col_password,t1.account_type,t1.sys_company_id,t3.role_name,t3.pkid role_id  from sys_user t1 "
				+ "join sys_user_role_many t2 on t1.pkid=t2.user_id join sys_role t3 on t2.role_id=t3.pkid WHERE t1.col_account = '" + username + "'";
		List<Map> Login = this.getBySqlMapper.findRecords(people_sql);
//		System.out.println(people_sql);
		if(Login.size()>0){
			Map Login_map = Login.get(0);
			if(Tool.md5(password).equals(Login_map.get("COL_PASSWORD"))==true){//密码正确
				HttpSession session = request.getSession();
				Login_map.remove("COL_PASSWORD");
				
				session.setAttribute("Login_map", Login_map);//用户信息，包括角色
				
				Map function_map = new HashMap();//功能权限
				Map company_map=new HashMap();//用户分类
				
				//权限
				String sql_1 = "select t1.page,t1.function from sys_permission t1 join sys_permission_role_many t2 on t1.pkid=t2.permission_id where t2.role_id="+Login_map.get("role_id");
				List<Map> sql_fun_list = this.getBySqlMapper.findRecords(sql_1);
				if(sql_fun_list.size()>0){
					for(int i = 0;i<sql_fun_list.size();i++){
						Map op = sql_fun_list.get(i);
						function_map.put(op.get("FUNCTION").toString(), "1");
					}
				}
				session.setAttribute("function_map", function_map);
				
				if(Login_map.get("ACCOUNT_TYPE").toString().equals("1")){//单位用户
					
					//获取单位信息
					String sql_zong = "select * from sys_company where pkid="+Login_map.get("SYS_COMPANY_ID");
					List<Map> sql_zong_list = this.getBySqlMapper.findRecords(sql_zong);
					Map zpong = sql_zong_list.get(0);
					session.setAttribute("company", zpong);//登录用户的单位信息
					
					//判断单位层级，生成单位树结构
					Object[] obj = this.get_company_tree(zpong.get("COM_LEVEL").toString(), Login_map.get("SYS_COMPANY_ID").toString(), zpong.get("PKID").toString(), zpong.get("COM_NAME").toString());
					session.setAttribute("company_tree", JSONObject.fromObject(obj[1]));//登录用户的单位信息--树结构
					session.setAttribute("company_uppe", JSONObject.fromObject(obj[0]));//登录单位的单位上下级关系
					
//					System.out.println(JSONObject.fromObject(obj[1]));
//					System.out.println(JSONObject.fromObject(obj[0]));
					
					//用户分类
					if(Login_map.get("ROLE_NAME").toString().equals("管理员")){
						company_map.put("com_type", "管理员");
						session.setAttribute("company_map", company_map);
					}else if(Login_map.get("ROLE_NAME").toString().equals("自治区扶贫办")){
						company_map.put("com_type", "单位");
						session.setAttribute("company_map", company_map);
					}else if(Login_map.get("ROLE_NAME").toString().equals("市扶贫办")){
						company_map.put("com_type", "单位");
						session.setAttribute("company_map", company_map);
					}else if(Login_map.get("ROLE_NAME").toString().equals("县扶贫办")){
						company_map.put("com_type", "单位");
						session.setAttribute("company_map", company_map);
					}else if(Login_map.get("ROLE_NAME").toString().equals("乡扶贫办")){
						company_map.put("com_type", "单位");
						session.setAttribute("company_map", company_map);
					}else if(Login_map.get("ROLE_NAME").toString().equals("驻村工作队")){
						company_map.put("com_type", "单位");
						session.setAttribute("company_map", company_map);
					}
					
				}else if(Login_map.get("ACCOUNT_TYPE").toString().equals("2")){//干部用户
					
					company_map.put("com_type", "帮扶人");
					session.setAttribute("company_map", company_map);
					
				}
				response.getWriter().print("1");//成功
				
			}else{
				response.getWriter().print("0");//密码不正确
			}
		}else{
			response.getWriter().print("2");//没有此用户
		}
	}

	//session获取用户登陆信息
	@RequestMapping("getLogin_massage.do")
	public void getLogin_massage(HttpServletRequest request,HttpServletResponse response) throws Exception{

		HttpSession session = request.getSession();
		JSONObject json = new JSONObject();
		if(session.getAttribute("Login_map")!=null){//验证session不为空
			
			Map<String,String> Login_map = (Map)session.getAttribute("Login_map");//用户信息，包括角色
			Map<String,String> function_map = (Map)session.getAttribute("function_map");//权限
			Map<String,String> company = (Map)session.getAttribute("company");//登录用户的单位信息
			Map<String,String> company_tree = (Map)session.getAttribute("company_tree");//登录用户的单位信息--树结构
			Map<String,String> company_uppe = (Map)session.getAttribute("company_uppe");//登录单位的单位上下级关系
			Map<String,String> company_map = (Map)session.getAttribute("company_map");//用户类型

			JSONObject Login_map_json = new JSONObject();
			for(String key : Login_map.keySet()){
				Login_map_json.put(key, Login_map.get(key));
			}
			json.put("Login_map", Login_map_json);

			JSONObject company_json = new JSONObject();
			for(String key : company.keySet()){
				company_json.put(key, company.get(key));
			}
			json.put("company", company_json);

			JSONObject function_map_json = new JSONObject();
			for(String key : function_map.keySet()){
				function_map_json.put(key, function_map.get(key));
			}
			json.put("function_map", function_map_json);

			JSONObject company_tree_json = new JSONObject();
			for(String key : company_tree.keySet()){
				company_tree_json.put(key, company_tree.get(key));
			}
			json.put("company_tree", company_tree_json);
			
			JSONObject company_uppe_json = new JSONObject();
			for(String key : company_uppe.keySet()){
				company_uppe_json.put(key, company_uppe.get(key));
			}
			json.put("company_uppe", company_uppe_json);
			
			JSONObject company_map_json = new JSONObject();
			for(String key : company_map.keySet()){
				company_map_json.put(key, company_map.get(key));
			}
			json.put("company_map", company_map_json);
			
//			System.out.println(company_tree_json.toString());
			response.getWriter().write(json.toString());
		}else{
//			System.out.println("weidenglu");
			response.getWriter().print("weidenglu");
		}
	}

	//销毁session
	@RequestMapping("login_out.do")
	public void login_out(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		try{
			session.invalidate();
			response.getWriter().write("1");
		}catch (Exception e){
			response.getWriter().write("0");
		}
	}
}

