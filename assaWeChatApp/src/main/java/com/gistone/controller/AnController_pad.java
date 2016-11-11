package com.gistone.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gistone.MyBatis.config.GetBySqlMapper;
import com.gistone.util.OverallSituation;

@RestController
@RequestMapping
public class AnController_pad {

	@Autowired
	private GetBySqlMapper getBySqlMapper;

	/**
	 * pad接口 所有的统计
	 * 
	 * @author chendong 
	 * @date 2016年8月15日
	 * @param request 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getPadController.do")
	public void getPadController(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String time = request.getParameter("time");
		
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time1=format.format(date);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar ca = Calendar.getInstance();
		ca.setTime(sdf.parse(time1));
        ca.setTime(sdf.parse(time));
 
        long d1 = sdf.parse(time1).getTime();
        long d2 = sdf.parse(time).getTime();
        int day = (int) (Math.abs(d2-d1) / (1000 * 60 * 60 * 24));
        
		if (day <= 1) {
			String[] str = { "pkhs.json", "zpyy.json", "pkhgc.json",
					"zrzc.json", "ydbq.json", "bfr.json", "bfjh.json",
					"bfcs.json", "zfcs.json", "tz.json", "shouye.json" };
			String[] url = new String[str.length];
			JSONObject obj_url = new JSONObject();
			JSONArray url_json = new JSONArray();
			String savePath = request.getServletContext().getRealPath("/")
					+ "attached\\pad_json\\";
			// 文件保存目录URL
			String saveUrl = request.getContextPath() + "/attached/pad_json/";
			for (int j = 0; j < str.length; j++) {
				url[j] = saveUrl + str[j];
				obj_url.put("url", saveUrl + str[j]);
				url_json.add(obj_url);
			}
			response.getWriter().write("{\"result\":" + url_json.toString() + "}");
		}else{
			// 贫困分布指标-贫困户数-国贫
			String pkhs_sql = "SELECT V3,V4,V5,COUNT(*) NUM  from (SELECT V3,V4,V5  FROM DA_HOUSEHOLD  WHERE SYS_STANDARD='国家级贫困人口' ORDER BY V3) T1 LEFT JOIN "
					+ "  (SELECT A.COM_NAME ZHEN,B.COM_NAME CUN,COM_CODE FROM((SELECT COM_NAME,PKID FROM SYS_COMPANY WHERE COM_LEVEL='4')A"
					+ " LEFT JOIN (SELECT * FROM SYS_COMPANY) B ON A.PKID =B.COM_F_PKID))T2 ON T1.V5=T2.CUN AND T1.V4=T2.ZHEN GROUP BY COM_CODE,V3,V4,V5 ORDER BY V3,V4,V5";
			List<Map> pkhs_list = this.getBySqlMapper.findRecords(pkhs_sql);
			JSONArray pkhs_json = new JSONArray();
			for (int a = 0; a < pkhs_list.size(); a++) {
				JSONObject pkhs_obj = new JSONObject();
				pkhs_obj.put("xian", pkhs_list.get(a).get("V3"));// 村名
				pkhs_obj.put("zhen", pkhs_list.get(a).get("V4"));// 镇名
				pkhs_obj.put("cun", pkhs_list.get(a).get("V5"));// 村名
				pkhs_obj.put("num", pkhs_list.get(a).get("NUM"));// 数据
				pkhs_json.add(pkhs_obj);
			}
			// 贫困分布指标-贫困户数-市贫
			String pkhs_sql1 = "SELECT V3,V4,V5,COUNT(*) NUM  from (SELECT V3,V4,V5  FROM DA_HOUSEHOLD  WHERE SYS_STANDARD='市级低收入人口' ORDER BY V3) T1 LEFT JOIN "
					+ "  (SELECT A.COM_NAME ZHEN,B.COM_NAME CUN,COM_CODE FROM((SELECT COM_NAME,PKID FROM SYS_COMPANY WHERE COM_LEVEL='4')A"
					+ " LEFT JOIN (SELECT * FROM SYS_COMPANY) B ON A.PKID =B.COM_F_PKID))T2 ON T1.V5=T2.CUN AND T1.V4=T2.ZHEN GROUP BY COM_CODE,V3,V4,V5 ORDER BY V3,V4,V5";
			List<Map> pkhs_list1 = this.getBySqlMapper.findRecords(pkhs_sql1);
			JSONArray pkhs_json1 = new JSONArray();
			for (Map val : pkhs_list1) {
				JSONObject obj = new JSONObject();
				obj.put("xian","".equals(val.get("V3")) || val.get("V3") == null ? "" : val.get("V3"));
				obj.put("zhen", "".equals(val.get("V4")) || val.get("V4") == null ? "" : val.get("V4"));
				obj.put("cun", "".equals(val.get("V5")) || val.get("V5") == null ? "" : val.get("V5"));
				obj.put("num", "".equals(val.get("NUM")) || val.get("NUM") == null ? 0 : val.get("NUM"));
				pkhs_json1.add(obj);
			}
			// 贫困分布指标-致贫原因-国贫
			String zpyy_sql = "SELECT V3,V4,V23 AS ZPYY,COUNT(V23) AS NUM FROM DA_HOUSEHOLD WHERE V23 IS NOT NULL AND V23 != ''  AND SYS_STANDARD='国家级贫困人口' GROUP BY V23,V4";
			List<Map> zpyy_list = this.getBySqlMapper.findRecords(zpyy_sql);
			JSONArray zpyy_json = new JSONArray();
			for (int c = 0; c < zpyy_list.size(); c++) {
				Map zpyy_map = zpyy_list.get(c);
				JSONObject zpyy_obj = new JSONObject();
				for (Object key : zpyy_map.keySet()) {
					zpyy_obj.put(key, zpyy_map.get(key));
				}
				zpyy_json.add(zpyy_obj);
			}

			// 贫困分布指标-致贫原因-市标
			String zpyy_sql1 = "SELECT V3,V4,V23 AS ZPYY,COUNT(V23) AS NUM FROM DA_HOUSEHOLD WHERE V23 IS NOT NULL AND V23 != ''  AND SYS_STANDARD='市级低收入人口' GROUP BY V23,V4";
			List<Map> zpyy_list1 = this.getBySqlMapper.findRecords(zpyy_sql1);
			JSONArray zpyy_json1 = new JSONArray();
			for (int d = 0; d < zpyy_list1.size(); d++) {
				Map zpyy_map = zpyy_list1.get(d);
				JSONObject zpyy_obj = new JSONObject();
				for (Object key : zpyy_map.keySet()) {
					zpyy_obj.put(key, zpyy_map.get(key));
				}
				zpyy_json1.add(zpyy_obj);
			}

			// 贫困分布指标-贫困户构成-国贫
			String gc_sql = "select v3,v4, v22 as pkhlx,count(v22) as num from da_household where v22 is not null and v22 != '' and sys_standard='国家级贫困人口' group by v22,v4";
			List<Map> gc_list = this.getBySqlMapper.findRecords(gc_sql);
			JSONArray gc_jsonArray = new JSONArray();
			for (int i = 0; i < gc_list.size(); i++) {
				JSONObject gc_obj = new JSONObject();
				Map gc_map = gc_list.get(i);
				for (Object key : gc_map.keySet()) {
					gc_obj.put(key, gc_map.get(key));
				}
				gc_jsonArray.add(gc_obj);
			}
			// 贫困分布指标-贫困户构成-市贫
			String gc_sql1 = "select v3,v4, v22 as pkhlx,count(v22) as num from da_household where v22 is not null and v22 != '' and sys_standard='市级低收入人口' group by v22,v4";
			List<Map> gc_list1 = this.getBySqlMapper.findRecords(gc_sql1);
			JSONArray gc_jsonArray1 = new JSONArray();
			for (int i = 0; i < gc_list1.size(); i++) {
				JSONObject gc_obj = new JSONObject();
				Map gc_map = gc_list1.get(i);
				for (Object key : gc_map.keySet()) {
					gc_obj.put(key, gc_map.get(key));
				}
				gc_jsonArray1.add(gc_obj);
			}

			// 贫困户分布指标-家庭收支-国贫 
			String sz_sql = "select t2.v3,t2.v4,t1.jyxsr,t1.zcxsr,t1.ccxsr,t1.gzxsr,t1.qtsr,t3.csjy,t3.zczc,t3.qtzc from (select da_household_id, sum(v10) as jyxsr,sum(v22) as zcxsr,sum(v24+v26) as  "
					+ "ccxsr,sum(v28+v30) as gzxsr,sum(v32+v34) as qtsr from da_current_income group by da_household_id)as t1 left join da_household as t2 on "
					+ "t1.da_household_id=t2.pkid left join (select da_household_id, sum(v2+v4+v6+v8+v10+v12+v14+v16+v18)as csjy,sum(v20+v22) zczc,sum(v27+v30) qtzc "
					+ "from da_current_expenditure GROUP BY da_household_id) t3 on t1.da_household_id = t3.da_household_id where t2.sys_standard='市级低收入人口'  GROUP BY t2.v4";
			List<Map> sz_list = this.getBySqlMapper.findRecords(sz_sql);
			JSONArray sz_jsonArray = new JSONArray();
			for (int i = 0; i < sz_list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", sz_list.get(i).get("v3"));
				obj.put("zhen", sz_list.get(i).get("v4"));
				obj.put("jyxsr", "".equals(sz_list.get(i).get("jyxsr")) || sz_list.get(i).get("jyxsr")==null ?0:sz_list.get(i).get("jyxsr"));
				obj.put("zcxsr", "".equals(sz_list.get(i).get("zcxsr")) || sz_list.get(i).get("zcxsr")==null ?0:sz_list.get(i).get("zcxsr"));
				obj.put("ccxsr", "".equals(sz_list.get(i).get("ccxsr")) || sz_list.get(i).get("ccxsr")==null ?0:sz_list.get(i).get("ccxsr"));
				obj.put("gzxsr", "".equals(sz_list.get(i).get("gzxsr")) || sz_list.get(i).get("gzxsr")==null ?0:sz_list.get(i).get("gzxsr"));
				obj.put("qtsr", "".equals(sz_list.get(i).get("qtsr")) || sz_list.get(i).get("qtsr")==null ?0:sz_list.get(i).get("qtsr"));
				
				obj.put("csjy", "".equals(sz_list.get(i).get("csjy")) || sz_list.get(i).get("csjy")==null ?0:sz_list.get(i).get("csjy"));
				obj.put("zczc", "".equals(sz_list.get(i).get("zczc")) || sz_list.get(i).get("zczc")==null ?0:sz_list.get(i).get("qtsr"));
				obj.put("qtzc", "".equals(sz_list.get(i).get("qtzc")) || sz_list.get(i).get("qtzc")==null ?0:sz_list.get(i).get("qtzc"));
				sz_jsonArray.add(obj);
			}
			// 贫困户分布指标-家庭收支-市贫
			String sz_sql1 = "select t2.v3,t2.v4,t1.jyxsr,t1.zcxsr,t1.ccxsr,t1.gzxsr,t1.qtsr,t3.csjy,t3.zczc,t3.qtzc from (select da_household_id, sum(v10) as jyxsr,sum(v22) as zcxsr,sum(v24+v26) as  "
					+ "ccxsr,sum(v28+v30) as gzxsr,sum(v32+v34) as qtsr from da_current_income group by da_household_id)as t1 left join da_household as t2 on "
					+ "t1.da_household_id=t2.pkid left join (select da_household_id, sum(v2+v4+v6+v8+v10+v12+v14+v16+v18)as csjy,sum(v20+v22) zczc,sum(v27+v30) qtzc "
					+ "from da_current_expenditure GROUP BY da_household_id) t3 on t1.da_household_id = t3.da_household_id where t2.sys_standard='国家级贫困人口  GROUP BY t2.v4";
			List<Map> sz_list1 = this.getBySqlMapper.findRecords(sz_sql1);
			JSONArray sz_jsonArray1 = new JSONArray();
			for (int i = 0; i < sz_list1.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", sz_list1.get(i).get("v3"));
				obj.put("zhen", sz_list1.get(i).get("v4"));
				obj.put("jyxsr", "".equals(sz_list1.get(i).get("jyxsr")) || sz_list1.get(i).get("jyxsr")==null ?0:sz_list1.get(i).get("jyxsr"));
				obj.put("zcxsr", "".equals(sz_list1.get(i).get("zcxsr")) || sz_list1.get(i).get("zcxsr")==null ?0:sz_list1.get(i).get("zcxsr"));
				obj.put("ccxsr", "".equals(sz_list1.get(i).get("ccxsr")) || sz_list1.get(i).get("ccxsr")==null ?0:sz_list1.get(i).get("ccxsr"));
				obj.put("gzxsr", "".equals(sz_list1.get(i).get("gzxsr")) || sz_list1.get(i).get("gzxsr")==null ?0:sz_list1.get(i).get("gzxsr"));
				obj.put("qtsr", "".equals(sz_list1.get(i).get("qtsr")) || sz_list1.get(i).get("qtsr")==null ?0:sz_list1.get(i).get("qtsr"));
				obj.put("csjy", "".equals(sz_list1.get(i).get("csjy")) || sz_list1.get(i).get("csjy")==null ?0:sz_list1.get(i).get("csjy"));
				obj.put("zczc", "".equals(sz_list1.get(i).get("zczc")) || sz_list1.get(i).get("zczc")==null ?0:sz_list1.get(i).get("qtsr"));
				obj.put("qtzc", "".equals(sz_list1.get(i).get("qtzc")) || sz_list1.get(i).get("qtzc")==null ?0:sz_list1.get(i).get("qtzc"));
				sz_jsonArray1.add(obj);
			}
			// 帮扶情况指标-易地搬迁-国贫
			String yd_sql = "select v3,v4,v5,count(*)num from (select v3,v4,v5 from (select v3,v4,v5,pkid from da_household where sys_standard='国家级贫困人口' )"+
							" a LEFT JOIN (select da_household_id,v3 sf from da_life) b on a.pkid =b.da_household_id where b.sf='是' ) t1"+
							" left join (select a.com_name zhen,b.com_name cun,com_code from( (select com_name,pkid from sys_company where com_level='3')a"+
							" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 on t1.v5=t2.cun and t1.v4=t2.zhen group by com_code";
			List<Map> yd_list = this.getBySqlMapper.findRecords(yd_sql);
			JSONArray yd_jsonArray = new JSONArray();
			for (int i = 0; i < yd_list.size(); i++) {

				JSONObject obj = new JSONObject();

				Map yd_map = yd_list.get(i);

				for (Object key : yd_map.keySet()) {

					obj.put(key, yd_map.get(key));
				}
				yd_jsonArray.add(obj);
			}
			// 帮扶情况指标-易地搬迁-市贫
			String yd_sql1 = "select v3,v4,v5,count(*)num from (select v3,v4,v5 from (select v3,v4,v5,pkid from da_household where sys_standard='市级低收入人口' )"
					+ " a LEFT JOIN (select da_household_id,v3 sf from da_life) b on a.pkid =b.da_household_id where b.sf='是' ) t1"
					+ " left join (select a.com_name zhen,b.com_name cun,com_code from( (select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 on t1.v5=t2.cun and t1.v4=t2.zhen group by com_code";

			List<Map> yd_list1 = this.getBySqlMapper.findRecords(yd_sql1);
			JSONArray yd_jsonArray1 = new JSONArray();
			for (int i = 0; i < yd_list1.size(); i++) {

				JSONObject obj = new JSONObject();

				Map yd_map = yd_list1.get(i);

				for (Object key : yd_map.keySet()) {

					obj.put(key, yd_map.get(key));
				}
				yd_jsonArray1.add(obj);
			}

			// 帮扶情况指标-帮扶责任人落实-国贫
			String bfr_sql = " select a.v3,a.v4,a.v5,b2,b10 from "
					+ " (select v3,v4,v5,count(*) b2 from(select v3,v4,v5 from da_household where sys_standard='国家级贫困人口' ) t1 left join  "
					+ " (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 on t1.v4=t2.zhen and t1.v5=t2.cun group by com_code)a left join"
					+ " (select v3,v4,v5,count(*) b10 from (select v3,v4,v5 from b10_t  where sys_standard='国家级贫困人口' ) t10 left join (select * from "
					+ " (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t10.v4=t1.zhen and t10.v5=t1.cun "
					+ " group by com_code) g on a.v5=g.v5 and a.v4=g.v4  ";
			List<Map> bfr_list = this.getBySqlMapper.findRecords(bfr_sql);
			JSONArray bfr_json = new JSONArray();
			for (int i = 0; i < bfr_list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", bfr_list.get(i).get("v3"));
				obj.put("zhen", bfr_list.get(i).get("v4"));
				obj.put("cun", bfr_list.get(i).get("v5"));
				obj.put("zong", bfr_list.get(i).get("b2"));
				if ("".equals(bfr_list.get(i).get("b10")) || bfr_list.get(i).get("b10") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", bfr_list.get(i).get("b10"));
				}
				bfr_json.add(obj);
			}

			// 帮扶情况指标-帮扶责任人落实-市贫
			String bfr_sql1 = " select a.v3,a.v4,a.v5,b2,b10 from "
					+ " (select v3,v4,v5,count(*) b2 from(select v3,v4,v5 from da_household where sys_standard='市级低收入人口' ) t1 left join  "
					+ " (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 on t1.v4=t2.zhen and t1.v5=t2.cun group by com_code)a left join"
					+ " (select v3,v4,v5,count(*) b10 from (select v3,v4,v5 from b10_t  where sys_standard='市级低收入人口' ) t10 left join (select * from "
					+ " (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t10.v4=t1.zhen and t10.v5=t1.cun "
					+ " group by com_code) g on a.v5=g.v5 and a.v4=g.v4  ";
			List<Map> bfr_list1 = this.getBySqlMapper.findRecords(bfr_sql1);
			JSONArray bfr_json1 = new JSONArray();
			for (int i = 0; i < bfr_list1.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", bfr_list1.get(i).get("v3"));
				obj.put("zhen", bfr_list1.get(i).get("v4"));
				obj.put("cun", bfr_list1.get(i).get("v5"));
				obj.put("zong", bfr_list1.get(i).get("b2"));
				if ("".equals(bfr_list1.get(i).get("b10")) || bfr_list1.get(i).get("b10") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", bfr_list1.get(i).get("b10"));
				}
				bfr_json1.add(obj);
			}
			
			// 帮扶情况指标-帮扶计划指定情况-国贫
			String jh_sql = "select v3,v4,v5,zong,num from (select v3,v4,v5,count(*) zong from (select v3,v4, v5 from da_household where sys_standard='国家级贫困人口') a LEFT join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))b on a.v5=b.cun and a.v4=b.zhen group by com_code "
					+ ")  t2  left join (select v4 vv4, v5 vv5,count(*) num from (select v4,v5 from b11_t where sys_standard='国家级贫困人口' ) aa left join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from( (select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))bb on aa.v5=bb.cun and aa.v4=bb.zhen group by com_code "
					+ " )  t11 on t2.v5=t11.vv5 and t2.v4=t11.vv4   where  zong>0 order by zong desc";
			List<Map> jh_list = this.getBySqlMapper.findRecords(jh_sql);
			JSONArray jh_json = new JSONArray();
			for (int i = 0; i < jh_list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", jh_list.get(i).get("v3"));
				obj.put("zhen", jh_list.get(i).get("v4"));
				obj.put("cun", jh_list.get(i).get("v5"));
				obj.put("zong", jh_list.get(i).get("zong"));
				if ("".equals(jh_list.get(i).get("num")) || jh_list.get(i).get("num") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", jh_list.get(i).get("num"));
				}
				jh_json.add(obj);
			}
			// 帮扶情况指标-帮扶计划指定情况-市贫
			String jh_sql1 = "select v3,v4,v5,zong,num from (select v3,v4,v5,count(*) zong from (select v3,v4, v5 from da_household where sys_standard='市级低收入人口') a LEFT join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))b on a.v5=b.cun and a.v4=b.zhen group by com_code "
					+ ")  t2  left join (select v4 vv4, v5 vv5,count(*) num from (select v4,v5 from b11_t where sys_standard='市级低收入人口' ) aa left join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from( (select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))bb on aa.v5=bb.cun and aa.v4=bb.zhen group by com_code "
					+ " )  t11 on t2.v5=t11.vv5 and t2.v4=t11.vv4   where  zong>0 order by zong desc";
			List<Map> jh_list1 = this.getBySqlMapper.findRecords(jh_sql1);
			JSONArray jh_json1 = new JSONArray();
			for (int i = 0; i < jh_list1.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", jh_list1.get(i).get("v3"));
				obj.put("zhen", jh_list1.get(i).get("v4"));
				obj.put("cun", jh_list1.get(i).get("v5"));
				obj.put("zong", jh_list1.get(i).get("zong"));
				if ("".equals(jh_list1.get(i).get("num")) || jh_list1.get(i).get("num") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", jh_list1.get(i).get("num"));
				}
				jh_json1.add(obj);
			}

			// 帮扶情况-帮扶措施落实情况-国贫
			String cs_sql =  "select v3,v4,v5,zong,num from (select v3,v4,v5,count(*) zong from (select v3,v4, v5 from da_household where sys_standard='国家级贫困人口') a LEFT join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))b on a.v5=b.cun and a.v4=b.zhen group by com_code "
					+ ")  t2  left join (select v4 vv4, v5 vv5,count(*) num from (select v4,v5 from b12_t where sys_standard='国家级贫困人口' ) aa left join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from( (select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))bb on aa.v5=bb.cun and aa.v4=bb.zhen group by com_code "
					+ " )  t12 on t2.v5=t12.vv5 and t2.v4=t12.vv4   where  zong>0 order by zong desc";

			List<Map> cs_list = this.getBySqlMapper.findRecords(cs_sql);
			JSONArray cs_json = new JSONArray();
			for (int i = 0; i < cs_list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", cs_list.get(i).get("v3"));
				obj.put("zhen", cs_list.get(i).get("v4"));
				obj.put("cun", cs_list.get(i).get("v5"));
				obj.put("zong", cs_list.get(i).get("zong"));
				if ("".equals(cs_list.get(i).get("num")) || cs_list.get(i).get("num") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", cs_list.get(i).get("num"));
				}
				cs_json.add(obj);
			}
			// 帮扶情况-帮扶措施落实情况-市贫
			String cs_sql1 =  "select v3,v4,v5,zong,num from (select v3,v4,v5,count(*) zong from (select v3,v4, v5 from da_household where sys_standard='市级低收入人口') a LEFT join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))b on a.v5=b.cun and a.v4=b.zhen group by com_code "
					+ ")  t2  left join (select v4 vv4, v5 vv5,count(*) num from (select v4,v5 from b12_t where sys_standard='市级低收入人口' ) aa left join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from( (select com_name,pkid from sys_company where com_level='3')a"
					+ " left join (select * from sys_company) b on a.pkid =b.com_f_pkid))bb on aa.v5=bb.cun and aa.v4=bb.zhen group by com_code "
					+ " )  t12 on t2.v5=t12.vv5 and t2.v4=t12.vv4   where  zong>0 order by zong desc";

			List<Map> cs_list1 = this.getBySqlMapper.findRecords(cs_sql1);
			JSONArray cs_json1 = new JSONArray();
			for (int i = 0; i < cs_list1.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", cs_list1.get(i).get("v3"));
				obj.put("zhen", cs_list1.get(i).get("v4"));
				obj.put("cun", cs_list1.get(i).get("v5"));
				obj.put("zong", cs_list1.get(i).get("zong"));
				if ("".equals(cs_list1.get(i).get("num")) || cs_list1.get(i).get("num") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", cs_list1.get(i).get("num"));
				}
				cs_json1.add(obj);
			}
			// 工作考核指标-帮扶责任人走访次数-国贫
			String zf_sql = "select v3,v4,v5,sum(num) num from (select * from (select v3,v4,v5,pkid from da_household where sys_standard='国家级贫困人口') a left join "
					+ "(select da_household_id ,count(*) num  from da_help_visit GROUP BY da_household_id) b on a.pkid = b.da_household_id) t1 left join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a"
					+" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2  on t1.v5=t2.cun and t1.v4=t2.zhen group by com_code";
			List<Map> zf_list = this.getBySqlMapper.findRecords(zf_sql);
			JSONArray zf_json = new JSONArray();
			for (int i = 0; i < zf_list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", zf_list.get(i).get("v3"));
				obj.put("zhen", zf_list.get(i).get("v4"));
				obj.put("cun", zf_list.get(i).get("v5"));
				if ("".equals(zf_list.get(i).get("num")) || zf_list.get(i).get("num") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", zf_list.get(i).get("num"));
				}
				zf_json.add(obj);
			}
			// 工作考核指标-帮扶责任人走访次数-市贫
			String zf_sql1 = "select v3,v4,v5,sum(num) num from (select * from (select v3,v4,v5,pkid from da_household where sys_standard='市级低收入人口') a left join "
					+ "(select da_household_id ,count(*) num  from da_help_visit GROUP BY da_household_id) b on a.pkid = b.da_household_id) t1 left join "
					+ "(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a"
					+" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2  on t1.v5=t2.cun and t1.v4=t2.zhen group by com_code";
			List<Map> zf_list1 = this.getBySqlMapper.findRecords(zf_sql1);
			JSONArray zf_json1 = new JSONArray();
			for (int i = 0; i < zf_list1.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", zf_list1.get(i).get("v3"));
				obj.put("zhen", zf_list1.get(i).get("v4"));
				obj.put("cun", zf_list1.get(i).get("v5"));
				if ("".equals(zf_list1.get(i).get("num")) || zf_list1.get(i).get("num") == null) {
					obj.put("num", 0);
				} else {
					obj.put("num", zf_list1.get(i).get("num"));
				}
				zf_json1.add(obj);
			}
			
			// 工作考核指标-帮扶台账填报完成率-国贫
			String tz_sql =" select a.v3,a.v4,a.v5,b2,b4,b6,b7,b8,b9,b10,b11,b12,b13 from  "+
					" (select v3,v4,v5,count(*) b2 from(select v3,v4,v5 from da_household where sys_standard='国家级贫困人口' ) t1 left join  "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 on t1.v4=t2.zhen and t1.v5=t2.cun group by com_code)a left join "+
					" (select v3,v4,v5,count(*) b4 from (select v3,v4,v5 from b4_t  where sys_standard='国家级贫困人口' ) t4 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company  where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t4.v4=t1.zhen and t4.v5=t1.cun "+
					" group by com_code) b on a.v5=b.v5 and a.v4=b.v4  left join (select v3,v4,v5,count(*) b6 from   "+
					" (select v3,v4,v5 from b6_t  where sys_standard='国家级贫困人口' ) t6 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t6.v4=t1.zhen and t6.v5=t1.cun "+
					" group by com_code) c on a.v5=c.v5 and a.v4=c.v4  left join   "+
					" (select v3,v4,v5,count(*) b7 from (select v3,v4,v5 from b7_t  where sys_standard='国家级贫困人口' ) t7 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( "+
					" select com_name,pkid from sys_company where com_level='3')a left join ( "+
					" select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t7.v4=t1.zhen and t7.v5=t1.cun "+
					" group by com_code) d on a.v5=d.v5 and a.v4=d.v4  left join   "+
					" (select v3,v4,v5,count(*) b8 from (select v3,v4,v5 from b8_t  where sys_standard='国家级贫困人口' ) t8 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t8.v4=t1.zhen and t8.v5=t1.cun "+
					" group by com_code) e on a.v5=e.v5 and a.v4=e.v4   left join "+
					" (select v3,v4,v5,count(*) b9 from (select v3,v4,v5 from b9_t  where sys_standard='国家级贫困人口' ) t9 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t9.v4=t1.zhen and t9.v5=t1.cun "+
					" group by com_code) f on a.v5=f.v5 and a.v4=f.v4  left join "+
					" (select v3,v4,v5,count(*) b10 from (select v3,v4,v5 from b10_t  where sys_standard='国家级贫困人口' ) t10 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t10.v4=t1.zhen and t10.v5=t1.cun "+
					" group by com_code) g on a.v5=g.v5 and a.v4=g.v4 left join    "+
					" (select v3,v4,v5,count(*) b11 from(select v3,v4,v5 from b11_t  where sys_standard='国家级贫困人口' ) t11 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( select com_name,pkid from sys_company where com_level='3')a"+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t11.v4=t1.zhen and t11.v5=t1.cun "+
					" group by com_code) h on a.v5=h.v5 and a.v4=h.v4  left join  "+
					" (select v3,v4,v5,count(*) b12 from (select v3,v4,v5 from b12_t  where sys_standard='国家级贫困人口' ) t12 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t12.v4=t1.zhen and t12.v5=t1.cun "+
					" group by com_code) i on a.v5=i.v5 and a.v4=i.v4 left join   "+
					" (select v3,v4,v5,count(*) b13 from (select v3,v4,v5 from b13_t  where sys_standard='国家级贫困人口' ) t13 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( "+
					" select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t13.v4=t1.zhen and t13.v5=t1.cun "+
					" group by com_code) j on a.v5=j.v5 and a.v4=j.v4  ";
			
			List<Map> tz_list = this.getBySqlMapper.findRecords(tz_sql);
			JSONArray tz_json = new JSONArray();
			for (int i = 0; i < tz_list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", tz_list.get(i).get("v3"));
				obj.put("zhen", tz_list.get(i).get("v4"));
				obj.put("cun", tz_list.get(i).get("v5"));
				obj.put("zong", tz_list.get(i).get("b2"));
				if("".equals(tz_list.get(i).get("b4")) || tz_list.get(i).get("b4")==null){
					obj.put("jb", 0);
				}else{
					obj.put("jbqk", tz_list.get(i).get("b4"));
				}
				if("".equals(tz_list.get(i).get("b6")) || tz_list.get(i).get("b6")==null){
					obj.put("sctj", 0);
				}else{
					obj.put("sctj", tz_list.get(i).get("b6"));
				}
				if("".equals(tz_list.get(i).get("b7")) || tz_list.get(i).get("b7")==null){
					obj.put("shtj", 0);
				}else{
					obj.put("shjt", tz_list.get(i).get("b7"));
				}
				if("".equals(tz_list.get(i).get("b8")) || tz_list.get(i).get("b8")==null){
					obj.put("dqsr", 0);
				}else{
					obj.put("drsr", tz_list.get(i).get("b8"));
				}
				
				if("".equals(tz_list.get(i).get("b9")) || tz_list.get(i).get("b9")==null){
					obj.put("dqzc", 0);
				}else{
					obj.put("dqzc", tz_list.get(i).get("b9"));
				}
				if("".equals(tz_list.get(i).get("b10")) || tz_list.get(i).get("b10")==null){
					obj.put("bfr", 0);
				}else{
					obj.put("bfr", tz_list.get(i).get("b10"));
				}
				if("".equals(tz_list.get(i).get("b11")) || tz_list.get(i).get("b11")==null){
					obj.put("bfjh", 0);
				}else{
					obj.put("bfjh", tz_list.get(i).get("b11"));
				}
				if("".equals(tz_list.get(i).get("b12")) || tz_list.get(i).get("b12")==null){
					obj.put("bfcs", 0);
				}else{
					obj.put("bfcs", tz_list.get(i).get("b12"));
				}
				if("".equals(tz_list.get(i).get("b13")) || tz_list.get(i).get("b13")==null){
					obj.put("zfqk", 0);
				}else{
					obj.put("zfqk", tz_list.get(i).get("b13"));
				}
				tz_json.add(obj);
			}
			
			// 工作考核指标-帮扶台账填报完成率-市贫
			String tz_sql1 =" select a.v3,a.v4,a.v5,b2,b4,b6,b7,b8,b9,b10,b11,b12,b13 from  "+
					" (select v3,v4,v5,count(*) b2 from(select v3,v4,v5 from da_household where sys_standard='市级低收入人口' ) t1 left join  "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 on t1.v4=t2.zhen and t1.v5=t2.cun group by com_code)a left join "+
					" (select v3,v4,v5,count(*) b4 from (select v3,v4,v5 from b4_t  where sys_standard='市级低收入人口' ) t4 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company  where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t4.v4=t1.zhen and t4.v5=t1.cun "+
					" group by com_code) b on a.v5=b.v5 and a.v4=b.v4  left join (select v3,v4,v5,count(*) b6 from   "+
					" (select v3,v4,v5 from b6_t  where sys_standard='市级低收入人口' ) t6 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t6.v4=t1.zhen and t6.v5=t1.cun "+
					" group by com_code) c on a.v5=c.v5 and a.v4=c.v4  left join   "+
					" (select v3,v4,v5,count(*) b7 from (select v3,v4,v5 from b7_t  where sys_standard='市级低收入人口' ) t7 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( "+
					" select com_name,pkid from sys_company where com_level='3')a left join ( "+
					" select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t7.v4=t1.zhen and t7.v5=t1.cun "+
					" group by com_code) d on a.v5=d.v5 and a.v4=d.v4  left join   "+
					" (select v3,v4,v5,count(*) b8 from (select v3,v4,v5 from b8_t  where sys_standard='市级低收入人口' ) t8 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t8.v4=t1.zhen and t8.v5=t1.cun "+
					" group by com_code) e on a.v5=e.v5 and a.v4=e.v4   left join "+
					" (select v3,v4,v5,count(*) b9 from (select v3,v4,v5 from b9_t  where sys_standard='市级低收入人口' ) t9 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t9.v4=t1.zhen and t9.v5=t1.cun "+
					" group by com_code) f on a.v5=f.v5 and a.v4=f.v4  left join "+
					" (select v3,v4,v5,count(*) b10 from (select v3,v4,v5 from b10_t  where sys_standard='市级低收入人口' ) t10 left join (select * from "+
					" (select a.com_name zhen,b.com_name cun,com_code from((select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t10.v4=t1.zhen and t10.v5=t1.cun "+
					" group by com_code) g on a.v5=g.v5 and a.v4=g.v4 left join    "+
					" (select v3,v4,v5,count(*) b11 from(select v3,v4,v5 from b11_t  where sys_standard='市级低收入人口' ) t11 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( select com_name,pkid from sys_company where com_level='3')a"+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t11.v4=t1.zhen and t11.v5=t1.cun "+
					" group by com_code) h on a.v5=h.v5 and a.v4=h.v4  left join  "+
					" (select v3,v4,v5,count(*) b12 from (select v3,v4,v5 from b12_t  where sys_standard='市级低收入人口' ) t12 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t12.v4=t1.zhen and t12.v5=t1.cun "+
					" group by com_code) i on a.v5=i.v5 and a.v4=i.v4 left join   "+
					" (select v3,v4,v5,count(*) b13 from (select v3,v4,v5 from b13_t  where sys_standard='市级低收入人口' ) t13 left join "+
					" (select * from(select a.com_name zhen,b.com_name cun,com_code from(( "+
					" select com_name,pkid from sys_company where com_level='3')a "+
					" left join (select * from sys_company) b on a.pkid =b.com_f_pkid))t2 )t1 on t13.v4=t1.zhen and t13.v5=t1.cun "+
					" group by com_code) j on a.v5=j.v5 and a.v4=j.v4  ";
			
			List<Map> tz_list1 = this.getBySqlMapper.findRecords(tz_sql1);
			JSONArray tz_json1 = new JSONArray();
			for (int i = 0; i < tz_list1.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("xian", tz_list1.get(i).get("v3"));
				obj.put("zhen", tz_list1.get(i).get("v4"));
				obj.put("cun", tz_list1.get(i).get("v5"));
				obj.put("zong", tz_list1.get(i).get("b2"));
				if("".equals(tz_list1.get(i).get("b4")) || tz_list1.get(i).get("b4")==null){
					obj.put("jb", 0);
				}else{
					obj.put("jbqk", tz_list1.get(i).get("b4"));
				}
				if("".equals(tz_list1.get(i).get("b6")) || tz_list1.get(i).get("b6")==null){
					obj.put("sctj", 0);
				}else{
					obj.put("sctj", tz_list1.get(i).get("b6"));
				}
				if("".equals(tz_list1.get(i).get("b7")) || tz_list1.get(i).get("b7")==null){
					obj.put("shtj", 0);
				}else{
					obj.put("shjt", tz_list1.get(i).get("b7"));
				}
				if("".equals(tz_list1.get(i).get("b8")) || tz_list1.get(i).get("b8")==null){
					obj.put("dqsr", 0);
				}else{
					obj.put("drsr", tz_list1.get(i).get("b8"));
				}
				
				if("".equals(tz_list1.get(i).get("b9")) || tz_list1.get(i).get("b9")==null){
					obj.put("dqzc", 0);
				}else{
					obj.put("dqzc", tz_list1.get(i).get("b9"));
				}
				if("".equals(tz_list1.get(i).get("b10")) || tz_list1.get(i).get("b10")==null){
					obj.put("bfr", 0);
				}else{
					obj.put("bfr", tz_list1.get(i).get("b10"));
				}
				if("".equals(tz_list1.get(i).get("b11")) || tz_list1.get(i).get("b11")==null){
					obj.put("bfjh", 0);
				}else{
					obj.put("bfjh", tz_list1.get(i).get("b11"));
				}
				if("".equals(tz_list1.get(i).get("b12")) || tz_list1.get(i).get("b12")==null){
					obj.put("bfcs", 0);
				}else{
					obj.put("bfcs", tz_list1.get(i).get("b12"));
				}
				if("".equals(tz_list1.get(i).get("b13")) || tz_list1.get(i).get("b13")==null){
					obj.put("zfqk", 0);
				}else{
					obj.put("zfqk", tz_list1.get(i).get("b13"));
				}
				tz_json1.add(obj);
			}
			
			//全市 的帮扶人和帮扶单位
			String bf_sql = "select * from (select count(*) bfr from sys_personal )a ,(select count(*) dw from da_company)b";
			List<Map> bf_list = this.getBySqlMapper.findRecords(bf_sql);
			JSONArray bf_json = new JSONArray();
			if (bf_list.size() > 0) {
				JSONObject obj = new JSONObject ();
				obj.put("bfr", bf_list.get(0).get("bfr"));
				obj.put("bfdw", bf_list.get(0).get("dw"));
				bf_json.add(obj);

			}
			//全市贫困户人口数量-国贫
			String pkh_sql = "select * from ((select count(*) a1 from da_household where v9 ='1' and sys_standard='国家级贫困人口') a join "
					+ "(select count(*) a2 from da_household where v9 ='2' and sys_standard='国家级贫困人口')b  join "
					+ "(select count(*) a3 from da_household where v9 ='3' and sys_standard='国家级贫困人口')c  join "
					+ "(select count(*) a4 from da_household where v9 ='4' and sys_standard='国家级贫困人口')d  join "
					+ "(select count(*) a5 from da_household where v9 >=5 and sys_standard='国家级贫困人口')e ) ";
			List<Map> pkh_list = this.getBySqlMapper.findRecords(pkh_sql);
			JSONArray pkh_json = new JSONArray();
			for (int i = 0; i < pkh_list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("a1", pkh_list.get(i).get("a1"));
				obj.put("a2", pkh_list.get(i).get("a2"));
				obj.put("a3", pkh_list.get(i).get("a3"));
				obj.put("a4", pkh_list.get(i).get("a4"));
				obj.put("a5", pkh_list.get(i).get("a5"));
				pkh_json.add(obj);
			}
			//全市贫困户人口数量-市贫
			String pkh_sql1 = "select * from ((select count(*) a1 from da_household where v9 ='1' and sys_standard='市级低收入人口') a join "
					+ "(select count(*) a2 from da_household where v9 ='2' and sys_standard='市级低收入人口')b  join "
					+ "(select count(*) a3 from da_household where v9 ='3' and sys_standard='市级低收入人口')c  join "
					+ "(select count(*) a4 from da_household where v9 ='4' and sys_standard='市级低收入人口')d  join "
					+ "(select count(*) a5 from da_household where v9 >=5 and sys_standard='市级低收入人口')e ) ";
			List<Map> pkh_list1 = this.getBySqlMapper.findRecords(pkh_sql1);
			JSONArray pkh_json1 = new JSONArray();
			for(int i = 0; i < pkh_list1.size(); i++){
				JSONObject obj = new JSONObject();
				obj.put("a1", pkh_list1.get(i).get("a1"));
				obj.put("a2", pkh_list1.get(i).get("a2"));
				obj.put("a3", pkh_list1.get(i).get("a3"));
				obj.put("a4", pkh_list1.get(i).get("a4"));
				obj.put("a5", pkh_list1.get(i).get("a5"));
				pkh_json1.add(obj);
			}
			
			String[] str = { "pkhs.json", "zpyy.json", "pkhgc.json", "zrzc.json","ydbq.json", "bfr.json", "bfjh.json", "bfcs.json", "zfcs.json","tz.json","shouye.json" };
			String[] url = new String[str.length];
			JSONObject obj_url = new JSONObject();
			JSONArray url_json = new JSONArray();
			// 获取文件需要上传到的路径
			String savePath = request.getServletContext().getRealPath("/") + "attached\\pad_json\\";
			// 文件保存目录URL
			String saveUrl = request.getContextPath() + "/attached/pad_json/";
			FileWriter fwriter = null, fwriter2 = null, fwriter3 = null, fwriter4 = null, fwriter5 = null, fwriter6 = null, fwriter7 = null, fwriter8 = null, fwriter9 = null, fwriter10 = null, fwriter11 = null;
			try {
				fwriter = new FileWriter(savePath + "pkhs.json");
				fwriter.write("{\"result\":" + pkhs_json.toString()+ ",\"result1\":" + pkhs_json1.toString() + "}");
				
				fwriter2 = new FileWriter(savePath + "zpyy.json");
				fwriter2.write("{\"result\":" + zpyy_json.toString()+ ",\"result1\":" + zpyy_json1.toString() + "}");
				
				fwriter3 = new FileWriter(savePath + "pkhgc.json");
				fwriter3.write("{\"result\":" + gc_jsonArray.toString()+ ",\"result1\":" + gc_jsonArray1.toString() + "}");
				
				fwriter4 = new FileWriter(savePath + "zrzc.json");
				fwriter4.write("{\"result\":" + sz_jsonArray.toString()+ ",\"result1\":" + sz_jsonArray1.toString() + "}");
				
				fwriter5 = new FileWriter(savePath + "ydbq.json");
				fwriter5.write("{\"result\":" + yd_jsonArray.toString()+ ",\"result1\":" + yd_jsonArray1.toString() + "}");
				
				fwriter6 = new FileWriter(savePath + "bfr.json");
				fwriter6.write("{\"result\":" + bfr_json.toString()+ ",\"result1\":" + bfr_json1.toString() + "}");
				
				fwriter7 = new FileWriter(savePath + "bfjh.json");
				fwriter7.write("{\"result\":" + jh_json.toString()+ ",\"result1\":" + jh_json1.toString() + "}");
				
				fwriter8 = new FileWriter(savePath + "bfcs.json");
				fwriter8.write("{\"result\":" + cs_json.toString()+ ",\"result1\":" + cs_json1.toString()+ "}");
				
				fwriter9 = new FileWriter(savePath + "zfcs.json");
				fwriter9.write("{\"result\":" + zf_json.toString()+ ",\"result1\":" + zf_json1.toString() + "}");
				
				fwriter10 = new FileWriter(savePath + "tz.json");
				fwriter10.write("{\"result\":" + tz_json.toString()+ ",\"result1\":" + tz_json1.toString() + "}");
				
				fwriter11 = new FileWriter(savePath + "shouye.json");
				fwriter11.write("{\"result\":" + bf_json.toString()+ ",\"result1\":"+pkh_json.toString()+",\"result2\":"+pkh_json1.toString()+"}");

				for (int j = 0; j < str.length; j++) {
					url[j]=saveUrl+str[j];
					obj_url.put("url", saveUrl+str[j]);
					url_json.add(obj_url);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fwriter.flush();
					fwriter.close();
					fwriter2.flush();
					fwriter2.close();

					fwriter3.flush();
					fwriter3.close();

					fwriter4.flush();
					fwriter4.close();

					fwriter5.flush();
					fwriter5.close();
					fwriter6.flush();
					fwriter6.close();
					fwriter7.flush();
					fwriter7.close();
					fwriter8.flush();
					fwriter8.close();
					fwriter9.flush();
					fwriter9.close();
					fwriter10.flush();
					fwriter10.close();
					fwriter11.flush();
					fwriter11.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			response.getWriter().write("{\"result\":" + url_json.toString() + "}");
			
		}
		
	}
}
