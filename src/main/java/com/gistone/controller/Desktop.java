package com.gistone.controller;

import java.io.IOException;
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

@RestController
@RequestMapping
public class Desktop {
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	/**
	 * 显示瀑布流数据
	 */
	@RequestMapping("desktop_photo.do")
	public void desktop_photo(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String pageSize = request.getParameter("pageSize");
		String pageNumber = request.getParameter("pageNumber");
		
		String level = request.getParameter("level");
		String xzqh = request.getParameter("xzqh");
		
		int size = Integer.parseInt(pageSize);
		int number = Integer.parseInt(pageNumber)*size;
		int page = number == 0 ? 1 : (number/size)+1;
		String whereSQL = "select v10 from SYS_COM where v"+(Integer.parseInt(level)*2)+"='"+xzqh+"' group by v10";
		
		String sql = "SELECT * FROM (select ROWNUM AS rowno,HOUSEHOLD_NAME,PERSONAL_NAME,PERSONAL_PHONE,V1,V3,LNG,LAT,ADDRESS,PIC_PATH,AAR008 from ( "
				+ " select d1.*,d2.pic_path from DA_HELP_VISIT d1 join (select RANDOM_NUMBER,max(pic_path) pic_path from DA_PIC_VISIT d2 group by RANDOM_NUMBER) d2 "
				+ " on d1.random_number=d2.random_number where AAR008 in("+whereSQL+") order by v1 desc "
				+ " ) t1 where ROWNUM <= "+(number+size)+") table_alias WHERE table_alias.rowno > "+number+"  ORDER BY v1 desc ";
		
		String count_sql = " select count(*) from DA_HELP_VISIT d1 join (select RANDOM_NUMBER,max(pic_path) pic_path from DA_PIC_VISIT d2 group by RANDOM_NUMBER) d2 on d1.random_number=d2.random_number where AAR008 in("+whereSQL+")";
		
		String sheji_count_sql = "select count(*) from (select HOUSEHOLD_NAME,HOUSEHOLD_CARD from DA_HELP_VISIT d1 "
				+ "join DA_PIC_VISIT d2 on d1.random_number=d2.random_number where AAR008 in("+whereSQL+") group by HOUSEHOLD_NAME,HOUSEHOLD_CARD) t1 ";
		int count = this.getBySqlMapper.findrows(count_sql);//总记录条数
		int shejicount = this.getBySqlMapper.findrows(sheji_count_sql);//涉及的贫困户数
		List<Map> Patient_st_List = this.getBySqlMapper.findRecords(sql);
		if(Patient_st_List.size()>0){
			JSONArray jsa=new JSONArray();
			for(int i = 0;i<Patient_st_List.size();i++){
				Map st_map = Patient_st_List.get(i);
				JSONObject val = new JSONObject();
				
				val.put("title", "".equals(st_map.get("PERSONAL_NAME")) || st_map.get("PERSONAL_NAME") == null ? "" : st_map.get("PERSONAL_NAME").toString());
				val.put("intro", "".equals(st_map.get("V3")) || st_map.get("V3") == null ? "" : st_map.get("V3").toString());
				val.put("src", "".equals(st_map.get("PIC_PATH")) || st_map.get("PIC_PATH") == null ? "" : st_map.get("PIC_PATH").toString());
				val.put("date", "".equals(st_map.get("V1")) || st_map.get("V1") == null ? "" : st_map.get("V1").toString());
				
				if ( st_map.get("ADDRESS") == null || "".equals(st_map.get("ADDRESS")) ) {
					String cha_sql = "select v9,lng,lat from sys_com where v10='"+st_map.get("AAR008")+"'";
					List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
					val.put("writer", "".equals(cha_list.get(0).get("V9")) || cha_list.get(0).get("V9") == null ? "" : cha_list.get(0).get("V9").toString());
				} else {
					val.put("writer", "".equals(st_map.get("ADDRESS")) || st_map.get("ADDRESS") == null ? "" : st_map.get("ADDRESS").toString());
				}
				
				if( "".equals(st_map.get("LNG")) || st_map.get("LNG") == null ) {
					String cha_sql = "select v9,lng,lat from sys_com where v10='"+st_map.get("AAR008")+"'";
					List<Map> cha_list = this.getBySqlMapper.findRecords(cha_sql);
					val.put("lng", "".equals(cha_list.get(0).get("LNG")) || cha_list.get(0).get("LNG") == null ? "" : cha_list.get(0).get("LNG").toString());
					val.put("lat", "".equals(cha_list.get(0).get("LAT")) || cha_list.get(0).get("LAT") == null ? "" : cha_list.get(0).get("LAT").toString());
				} else {
					val.put("lng", "".equals(st_map.get("LNG")) || st_map.get("LNG") == null ? "" : st_map.get("LNG").toString());
					val.put("lat", "".equals(st_map.get("LAT")) || st_map.get("LAT") == null ? "" : st_map.get("LAT").toString());
				}
				
				val.put("phone", "".equals(st_map.get("PERSONAL_PHONE")) || st_map.get("PERSONAL_PHONE") == null ? "" : st_map.get("PERSONAL_PHONE").toString());
				val.put("house", "".equals(st_map.get("HOUSEHOLD_NAME")) || st_map.get("HOUSEHOLD_NAME") == null ? "" : st_map.get("HOUSEHOLD_NAME").toString());
				jsa.add(val);
			}
			
			JSONObject val_ret = new JSONObject();
			val_ret.put("data1", jsa);
			val_ret.put("data2", count);//日记数量
			val_ret.put("data3", shejicount);//涉及到的贫困户数
			response.getWriter().write(val_ret.toString());
		}else{
			JSONObject val_ret = new JSONObject();
			val_ret.put("error", "本地区暂无帮扶日记！");
			response.getWriter().write(val_ret.toString());
		}
	}
	/**
	 * 查询行政区划
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("xzqh.do")
	public void xzqh(HttpServletRequest request,HttpServletResponse response ) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String level = request.getParameter("level");//级别
		String v1 = request.getParameter("v1");//省
		String v3 = request.getParameter("v3");//市
		String v5 = request.getParameter("v5");//县
		String v7 = request.getParameter("v7");//乡
		String v9 = request.getParameter("v9");//村
		String  sql = " ";
		if ( "1".equals(level) ) {
			sql += "select v2 code from sys_com where v1='"+v1+"'";
		}else if ("2".equals(level)){
			sql += "select v4 code from sys_com where v1='"+v1+"' and v3='"+v3+"'";
		} else if ("3".equals(level)) {
			sql += "select v8 code from sys_com where v1='"+v1+"' and v3='"+v3+"' and v5='"+v5+"' ";
		} else if ("4".equals(level)) {
			sql += "select v6 code from sys_com where v1='"+v1+"' and v3='"+v3+"' and v5='"+v5+"' and v7='"+v7+"'";
		} else if ("5".equals(level)) {
			sql += "select v10 code from sys_com where v1='"+v1+"' and v3='"+v3+"' and v5='"+v5+"' and v7='"+v7+"' and v9='"+v9+"'";
		}
		try {
			List<Map> list = this.getBySqlMapper.findRecords(sql);
			response.getWriter().write(list.get(0).get("CODE").toString());
			
		} catch (Exception e) {
			response.getWriter().write("0");
		}
		
	}
	/**
	 * 到帮扶人数据
	 * @param request    and AAR010 in ('0','3')  过滤贫困户是否脱贫
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("dao_personal_household.do")
	public void dao_personal_household(HttpServletRequest request , HttpServletResponse response ) throws IOException {
		String sql = "select household_name,household_card,personal_name,personal_phone from  "+
					"(select AAK110,AAC001 from NEIMENG0117_AC08 where AAR100='1') aa LEFT JOIN "+
					" (select AAK110,AAB002 personal_name,AAR012 personal_phone from  NEIMENG0117_AK11 ) bb on aa.AAK110=bb.AAK110 LEFT JOIN ( "+
					" select AAB002 household_name,AAB004 household_card,q1.AAC001 from  (select AAC001 from  NEIMENG0117_AC01 where AAR100='1') q1 LEFT JOIN ( "+
					" select AAC001,AAB002,AAB004 from NEIMENG0117_AB01 where AAB006='01' ) q2 on  q1.aac001=q2.aac001 "+
					" where AAB002 is not null) cc on aa.AAc001 =cc.AAc001  where household_name is not null and household_card is not null  and personal_name is not null "+
					" GROUP BY household_name,household_card,personal_name,personal_phone";
		List<Map> list = this.getBySqlMapper.findRecords(sql);
		for ( int i = 0 ; i < list.size() ; i ++ ) {
			String  household_name = "".equals(list.get(i).get("HOUSEHOLD_NAME")) || list.get(i).get("HOUSEHOLD_NAME") == null ? "": list.get(i).get("HOUSEHOLD_NAME").toString();
			String  household_card = "".equals(list.get(i).get("HOUSEHOLD_CARD")) || list.get(i).get("HOUSEHOLD_CARD") == null ? "": list.get(i).get("HOUSEHOLD_CARD").toString().replace("'","");
			String  personal_name = "".equals(list.get(i).get("PERSONAL_NAME")) || list.get(i).get("PERSONAL_NAME") == null ? "": list.get(i).get("PERSONAL_NAME").toString();
			String  personal_phone = "".equals(list.get(i).get("PERSONAL_PHONE")) || list.get(i).get("PERSONAL_PHONE") == null ? "": list.get(i).get("PERSONAL_PHONE").toString();
			String  insert_sql = " insert into SYS_HOUSEHOLD_MANY_TEST (PERSONAL_NAME,HOUSEHOLD_NAME,PERSONAL_PHONE,HOUSEHOLD_CARD) VALUES "+
									"('"+personal_name+"','"+household_name+"','"+personal_phone+"','"+household_card+"')";
			this.getBySqlMapper.insert(insert_sql);			
			System.out.println("共有记录："+list.size()+"条，已插入："+(i+1)+"条");
		}
		response.getWriter().write("共有记录："+list.size()+"条，已插入："+list.size()+"条");
	}
}
