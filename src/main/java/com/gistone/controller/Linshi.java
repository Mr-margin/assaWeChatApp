package com.gistone.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gistone.MyBatis.config.GetBySqlMapper;
import com.gistone.util.QRCodeUtil;
import com.gistone.util.Tool;

@RestController
@RequestMapping
public class Linshi {
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	/**
	 * 生成二维码
	 * @author 李永亮
	 * @date 2016年8月23日
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("getLinshi_2.do")
	public void getLinshi_2(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String text = "http://www.gistone.cn/assa/anuser.html?pkid=";
		// 文件保存目录路径  
        String savePath = request.getServletContext().getRealPath("/")+ "attached/7/";
        // 文件保存目录URL  
        String saveUrl = request.getContextPath() + "/attached/7/";
        
        
		String sql = "select pkid,v6 from da_household";
		List<Map> Patient_st_List = this.getBySqlMapper.findRecords(sql);
		if(Patient_st_List.size()>0){
			for(int i = 0;i<Patient_st_List.size();i++){
				Map Patient_st_map = Patient_st_List.get(i);
				QRCodeUtil.encode(text+Patient_st_map.get("PKID"), "c:/11.jpg", savePath, Patient_st_map.get("PKID")+"_"+Patient_st_map.get("V6")+".jpg", true);
				
				String sql_i ="INSERT INTO da_pic(pic_type,pic_pkid,pic_path,pic_format) VALUES"+
						"('7','"+Patient_st_map.get("PKID")+"','"+saveUrl+Patient_st_map.get("PKID")+"_"+Patient_st_map.get("V6")+".jpg"+"','jpg')";
				
				this.getBySqlMapper.insert(sql_i);
				System.out.println(sql_i);
			}
		}
		System.out.println("========结束========");
		
		
	}
	
	/**
	 * 生成用户密码
	 * @author 李永亮
	 * @date 2016年8月23日
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("getLinshi_3.do")
	public void getLinshi_3(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String sql = "select PKID,COL_PASSWORD from SYS_USER where pkid>8";
		List<Map> Patient_st_List = this.getBySqlMapper.findRecords(sql);
		if(Patient_st_List.size()>0){
			for(int i = 0;i<Patient_st_List.size();i++){
				Map Patient_st_map = Patient_st_List.get(i);
				
				String sql_i ="update SYS_USER set COL_PASSWORD='"+Tool.md5(Patient_st_map.get("COL_PASSWORD").toString())+"'  where pkid="+Patient_st_map.get("PKID");
				
				this.getBySqlMapper.update(sql_i);
				System.out.println(sql_i);
			}
		}
		System.out.println("========结束========");
		
		
	}
	
	
	@RequestMapping("getLinshi_4.do")
	public void getLinshi_4(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String str = "select t4.shi_name,t4.shi_code,t4.xian_name,t4.xian_code,t3.com_name as xiang_name,t3.com_code as xiang_code,t3.pkid from SYS_COMPANY t3 join (";
		str += " select t2.com_name as shi_name,t2.com_code as shi_code,t1.com_name as xian_name,t1.com_code as xian_code,t1.pkid from SYS_COMPANY t1 join ( ";
		str += " select pkid,COM_NAME,com_code from SYS_COMPANY where com_level=2 order by COM_CODE) t2 on t2.pkid=t1.com_f_pkid  ";
		str += " order by shi_code,xian_code) t4 on t4.pkid=t3.com_f_pkid  order by shi_code,xian_code,xiang_code ";
		
		
		List<Map> Patient_st_List = this.getBySqlMapper.findRecords(str);
		if(Patient_st_List.size()>0){
			for(Map Patient_st_map: Patient_st_List){
				String savePath = request.getServletContext().getRealPath("/")+ "attached/exportExcel/"+Patient_st_map.get("SHI_NAME")+"/"+Patient_st_map.get("XIAN_NAME")+"/"+Patient_st_map.get("XIANG_NAME")+"/";
				String str_path[] = {savePath+"01.贫困人口",savePath+"02.贫困户",savePath+"03.贫困村",savePath+"04.分类扶持",savePath+"05.十个全覆盖",savePath+"06.帮扶责任人"};
				
				//检查新建文件夹
				for(int i = 0;i<str_path.length;i++){
					getLinshi_6(str_path[i]);
				}
				
				String hsql = "select com_name,com_code from SYS_COMPANY where com_f_code='"+Patient_st_map.get("XIANG_CODE")+"' order by COM_CODE";
				List<Map> xiang_List = this.getBySqlMapper.findRecords(hsql);
				
				//标题样式
				WritableFont title_style =new WritableFont(WritableFont.createFont("微软雅黑"), 11 ,WritableFont.BOLD);
				WritableCellFormat tsty = new WritableCellFormat(title_style);
				tsty.setAlignment(Alignment.CENTRE);  //平行居中
				tsty.setVerticalAlignment(VerticalAlignment.CENTRE);  //垂直居中
				tsty.setBorder(Border.ALL, BorderLineStyle.THIN);
				tsty.setWrap(true);
				
				//正文样式
				WritableFont content_style =new WritableFont(WritableFont.createFont("微软雅黑"), 9 ,WritableFont.NO_BOLD);
				WritableCellFormat coty = new WritableCellFormat(content_style);
				coty.setAlignment(Alignment.CENTRE);  //平行居中
				coty.setVerticalAlignment(VerticalAlignment.CENTRE);  //垂直居中
				coty.setBorder(Border.ALL, BorderLineStyle.THIN);
				coty.setWrap(true);
				
				//第一张表------------------------------------------
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.0_贫困人口基本信息统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "女性人数（人）", tsty));
//				sheet_1_0.setColumnView(3, 30);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 30);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "少数民族人数（人）", tsty));
//				sheet_1_0.setColumnView(5, 50);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 50);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "新农合参保人数（人）", tsty));
//				sheet_1_0.setColumnView(7, 50);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 50);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "新农保参保人数（人）", tsty));
//				sheet_1_0.setColumnView(9, 50);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 50);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "持证残疾人数（人）", tsty));
//				sheet_1_0.setColumnView(11, 50);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 50);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "现役军人人数（人）", tsty));
//				sheet_1_0.setColumnView(13, 50);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 50);
//				//添加村数据
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.1_贫困人口年龄分组情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总贫困人数（人）", tsty));
//				sheet_1_0.setColumnView(2, 30);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "16岁（含）以下人数（人）", tsty));
//				sheet_1_0.setColumnView(3, 40);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "16岁（含）-30岁（不含）人数（人）", tsty));
//				sheet_1_0.setColumnView(5, 50);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "30岁（含）-40岁（不含）人数（人）", tsty));
//				sheet_1_0.setColumnView(7, 50);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "40岁（含）-50岁（不含）人数（人）", tsty));
//				sheet_1_0.setColumnView(9, 50);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "50岁（含）-60岁（不含）人数", tsty));
//				sheet_1_0.setColumnView(11, 50);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "60岁（含）以上人数（人）", tsty));
//				sheet_1_0.setColumnView(13, 50);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				//添加村数据
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//2
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.2_贫困人口身体健康情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "健康（人）", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "长期慢性病（人）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "大病（人）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "残疾（人）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
//				//3
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.3_贫困人口文化程度统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "学龄前儿童（人）", tsty));
//				sheet_1_0.setColumnView(3, 35);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "文盲半文盲（人）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "小学（人）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "初中（人）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "高中（人）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "大专及以上（人）", tsty));
//				sheet_1_0.setColumnView(13, 20);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				//添加村数据
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
			
				
				
//				//4
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.4_贫困人口劳动能力类型统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "普通劳动力（人）", tsty));
//				sheet_1_0.setColumnView(3, 35);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "技能劳动力（人）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "丧失劳动力（人）", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "无劳动力（人）", tsty));
//				sheet_1_0.setColumnView(9, 35);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//5
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.5_贫困人口上年度务工状况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "乡（镇）内务工（人）", tsty));
//				sheet_1_0.setColumnView(3, 35);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "乡（镇）外县内务工（人）", tsty));
//				sheet_1_0.setColumnView(5, 45);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "县外省内务工（人）", tsty));
//				sheet_1_0.setColumnView(7, 40);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "省外务工（人）", tsty));
//				sheet_1_0.setColumnView(9, 30);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "其他（人）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//6
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.6_贫困人口上年度务工时间情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "1个月（人）", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "2个月（人）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "3个月（人）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "4个月（人）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "5个月（人）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "6个月（人）", tsty));
//				sheet_1_0.setColumnView(13, 20);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "7个月（人）", tsty));
//				sheet_1_0.setColumnView(15, 20);
//				
//				sheet_1_0.addCell(new Label( 16 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(16, 20);
//				
//				sheet_1_0.addCell(new Label( 17 , 0 , "8个月（人）", tsty));
//				sheet_1_0.setColumnView(17, 20);
//				
//				sheet_1_0.addCell(new Label( 18 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(18, 20);
//				
//				sheet_1_0.addCell(new Label( 19 , 0 , "9个月（人）", tsty));
//				sheet_1_0.setColumnView(19, 20);
//				
//				sheet_1_0.addCell(new Label( 20 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(20, 20);
//				
//				sheet_1_0.addCell(new Label( 21 , 0 , "9个月（人）", tsty));
//				sheet_1_0.setColumnView(21, 20);
//				
//				sheet_1_0.addCell(new Label( 22 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(22, 20);
//				
//				sheet_1_0.addCell(new Label( 23 , 0 , "10个月（人）", tsty));
//				sheet_1_0.setColumnView(23, 20);
//				
//				sheet_1_0.addCell(new Label( 24, 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(24, 20);
//				
//				sheet_1_0.addCell(new Label( 25 , 0 , "11个月（人）", tsty));
//				sheet_1_0.setColumnView(25, 20);
//				
//				sheet_1_0.addCell(new Label( 26, 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(26, 20);
//				
//				sheet_1_0.addCell(new Label( 27 , 0 , "12个月（人）", tsty));
//				sheet_1_0.setColumnView(27, 20);
//				
//				sheet_1_0.addCell(new Label( 28, 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(28, 20);
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//7
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.7_贫困人口在校生情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "非在校生", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "学前教育", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "小学", tsty));
//				sheet_1_0.setColumnView(7, 10);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "七年级", tsty));
//				sheet_1_0.setColumnView(9, 15);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "八年级", tsty));
//				sheet_1_0.setColumnView(11, 15);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "九年级", tsty));
//				sheet_1_0.setColumnView(13, 15);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "高中一年级", tsty));
//				sheet_1_0.setColumnView(15, 35);
//				
//				sheet_1_0.addCell(new Label( 16 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(16, 20);
//				
//				sheet_1_0.addCell(new Label( 17 , 0 , "高中二年级", tsty));
//				sheet_1_0.setColumnView(17, 35);
//				
//				sheet_1_0.addCell(new Label( 18 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(18, 20);
//				
//				sheet_1_0.addCell(new Label( 19 , 0 , "高中三年级", tsty));
//				sheet_1_0.setColumnView(19, 35);
//				
//				sheet_1_0.addCell(new Label( 20 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(20, 20);
//				
//				sheet_1_0.addCell(new Label( 21 , 0 , "中职一年级", tsty));
//				sheet_1_0.setColumnView(21, 35);
//				
//				sheet_1_0.addCell(new Label( 22 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(22, 20);
//				
//				sheet_1_0.addCell(new Label( 23 , 0 , "中职二年级", tsty));
//				sheet_1_0.setColumnView(23, 35);
//				
//				sheet_1_0.addCell(new Label( 24 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(24, 20);
//				
//				sheet_1_0.addCell(new Label( 25 , 0 , "中职三年级", tsty));
//				sheet_1_0.setColumnView(25, 35);
//				
//				sheet_1_0.addCell(new Label( 26 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(26, 20);
//				
//				sheet_1_0.addCell(new Label( 27 , 0 , "高职一年级", tsty));
//				sheet_1_0.setColumnView(27, 35);
//				
//				sheet_1_0.addCell(new Label( 28 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(28, 20);
//				
//				sheet_1_0.addCell(new Label( 29 , 0 , "高职二年级", tsty));
//				sheet_1_0.setColumnView(29, 35);
//				
//				sheet_1_0.addCell(new Label( 30 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(30, 20);
//				
//				sheet_1_0.addCell(new Label( 31 , 0 , "高职三年级", tsty));
//				sheet_1_0.setColumnView(31, 35);
//				
//				sheet_1_0.addCell(new Label( 32 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(32, 20);
//				
//				sheet_1_0.addCell(new Label( 33 , 0 , "大专及以上", tsty));
//				sheet_1_0.setColumnView(33, 35);
//				
//				sheet_1_0.addCell(new Label( 34 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(34, 20);
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//8
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.8_贫困人口统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "一般贫困户人数", tsty));
//				sheet_1_0.setColumnView(3, 35);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "低保贫困户人数", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "五保贫困户人数", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//9
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.9_贫困人口适龄教育年龄分段情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总人数（人）", tsty));
//				sheet_1_0.setColumnView(2, 25);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "3岁（含）-6岁（不含）人数（人）", tsty));
//				sheet_1_0.setColumnView(3, 45);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "6岁（含）-15岁（不含）人数（人）", tsty));
//				sheet_1_0.setColumnView(5, 45);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "15岁（含）-18岁（不含）人数（人）", tsty));
//				sheet_1_0.setColumnView(7, 45);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "18岁（含）-22岁（不含）人数", tsty));
//				sheet_1_0.setColumnView(9, 40);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "22岁（含）-60岁（不含）人数", tsty));
//				sheet_1_0.setColumnView(11, 40);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "60岁（含）以上人数", tsty));
//				sheet_1_0.setColumnView(13, 35);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
//				
//				//10
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.10_贫困人口分布情况统计表（按行政划）.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困户", tsty));
//				sheet_1_0.setColumnView(2, 15);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "贫困人口", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "市数", tsty));
//				sheet_1_0.setColumnView(4, 10);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "总市数", tsty));
//				sheet_1_0.setColumnView(5, 15);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "县数", tsty));
//				sheet_1_0.setColumnView(6, 10);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "总县数", tsty));
//				sheet_1_0.setColumnView(7, 15);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "乡镇数", tsty));
//				sheet_1_0.setColumnView(8, 15);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "总乡镇数", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "村数", tsty));
//				sheet_1_0.setColumnView(10, 10);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "总村数", tsty));
//				sheet_1_0.setColumnView(11, 15);
//				
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
//				
//				//11
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.11_贫困人口分布情况统计表（按贫困县属性）.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困人口", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "贫困县人数（人）", tsty));
//				sheet_1_0.setColumnView(3, 30);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "重点县人数（人）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "片区县人数（人）", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "老区县人数（人）", tsty));
//				sheet_1_0.setColumnView(9, 35);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "少数民族自治县人数（人）", tsty));
//				sheet_1_0.setColumnView(11, 55);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "边境县人数（人）", tsty));
//				sheet_1_0.setColumnView(13, 35);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//12
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.12_贫困人口分布情况统计表（按片区）.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "片区县数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "贫困村数", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "贫困户数", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "贫困人口", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//13
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[0]+"/1.13_贫困人口分布情况统计表（按贫困村）.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困户", tsty));
//				sheet_1_0.setColumnView(2, 15);
//				sheet_1_0.addCell(new Label( 3 , 0 , "贫困人口", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "贫困村户数", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "非贫困村户数", tsty));
//				sheet_1_0.setColumnView(6, 35);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "贫困村人数", tsty));
//				sheet_1_0.setColumnView(8, 35);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "非贫困村人数", tsty));
//				sheet_1_0.setColumnView(10, 35);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				
				
				
//				//21
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.1_贫困户贫困属性统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 15);
//				sheet_1_0.addCell(new Label( 3 , 0 , "总人数", tsty));
//				sheet_1_0.setColumnView(3, 15);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "一般贫困户数（户）", tsty));
//				sheet_1_0.setColumnView(4, 40);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "一般贫困户人数（人）", tsty));
//				sheet_1_0.setColumnView(6, 45);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "低保贫困户数（户）", tsty));
//				sheet_1_0.setColumnView(8, 40);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "低保贫困户人数（人）", tsty));
//				sheet_1_0.setColumnView(10, 45);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "五保贫困户数（户）", tsty));
//				sheet_1_0.setColumnView(12, 40);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(13, 20);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "五保贫困户人数（人）", tsty));
//				sheet_1_0.setColumnView(14, 45);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(15, 20);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
//				
				
//				//22
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.2_贫困户收支分类统计表（按收入类型）.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				sheet_1_0.addCell(new Label( 3 , 0 , "户均家庭年人均纯收入(元)", tsty));
//				sheet_1_0.setColumnView(3, 60);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "工资性收入户数", tsty));
//				sheet_1_0.setColumnView(4, 40);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "工资性收入户均（元）", tsty));
//				sheet_1_0.setColumnView(5, 45);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "财产性收入户数", tsty));
//				sheet_1_0.setColumnView(6, 35);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "财产性收入户均（元）", tsty));
//				sheet_1_0.setColumnView(7, 45);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "生产经营性收入户数", tsty));
//				sheet_1_0.setColumnView(8, 45);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "生产经营性收入户均（元）", tsty));
//				sheet_1_0.setColumnView(9, 55);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "转移性收入户数", tsty));
//				sheet_1_0.setColumnView(10, 35);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "转移性收入户均（元）", tsty));
//				sheet_1_0.setColumnView(11, 45);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "生产经营性支出户数", tsty));
//				sheet_1_0.setColumnView(12, 45);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "生产经营性支出户均（元）", tsty));
//				sheet_1_0.setColumnView(13, 55);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "纯收入户数", tsty));
//				sheet_1_0.setColumnView(14, 25);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "纯收入户均（元）", tsty));
//				sheet_1_0.setColumnView(15, 35);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				//23
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.3_贫困户转移收收入分类统计表（按收入类型）.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "有转移性收入的贫困户数", tsty));
//				sheet_1_0.setColumnView(2, 55);
//				sheet_1_0.addCell(new Label( 3 , 0 , "户均转移性收入(元)", tsty));
//				sheet_1_0.setColumnView(3, 45);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "低保金户数", tsty));
//				sheet_1_0.setColumnView(4, 25);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "低保金户均（元）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "五保金户数", tsty));
//				sheet_1_0.setColumnView(6, 25);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "五保金户均（元）", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "养老保险户数", tsty));
//				sheet_1_0.setColumnView(8, 30);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "养老保险户均（元）", tsty));
//				sheet_1_0.setColumnView(9, 40);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "计划生育金户数", tsty));
//				sheet_1_0.setColumnView(10, 35);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "计划生育金户均（元）", tsty));
//				sheet_1_0.setColumnView(11, 45);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "生态补偿金户数", tsty));
//				sheet_1_0.setColumnView(12, 35);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "生态补偿金户均（元）", tsty));
//				sheet_1_0.setColumnView(13, 45);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "其他转移性收入户数", tsty));
//				sheet_1_0.setColumnView(14, 45);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "其他转移性收入户均（元）", tsty));
//				sheet_1_0.setColumnView(15, 45);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
//				
				
//				//24
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.4_贫困户主要致贫原因情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "因病致贫（户）", tsty));
//				sheet_1_0.setColumnView(3, 30);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "因残致贫（户）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "因学致贫（户）", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "因灾致贫（户）", tsty));
//				sheet_1_0.setColumnView(9, 35);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "缺土地（户）", tsty));
//				sheet_1_0.setColumnView(11, 25);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "缺水（户）", tsty));
//				sheet_1_0.setColumnView(13, 20);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "缺技术（户）", tsty));
//				sheet_1_0.setColumnView(15, 25);
//				
//				sheet_1_0.addCell(new Label( 16 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(16, 20);
//				
//				sheet_1_0.addCell(new Label( 17 , 0 , "缺劳力（户）", tsty));
//				sheet_1_0.setColumnView(17, 25);
//				
//				sheet_1_0.addCell(new Label( 18 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(18, 20);
//				
//				sheet_1_0.addCell(new Label( 19 , 0 , "缺资金（户）", tsty));
//				sheet_1_0.setColumnView(19, 25);
//				
//				sheet_1_0.addCell(new Label( 20 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(20, 20);
//				
//				sheet_1_0.addCell(new Label( 21 , 0 , "交通条件落后（户）", tsty));
//				sheet_1_0.setColumnView(21, 40);
//				
//				sheet_1_0.addCell(new Label( 22 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(22, 20);
//				
//				sheet_1_0.addCell(new Label( 23 , 0 , "自身发展力不足（户）", tsty));
//				sheet_1_0.setColumnView(23, 45);
//				
//				sheet_1_0.addCell(new Label( 24 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(24, 20);
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//25
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.5_贫困户其他致贫原因情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "因病致贫（户）", tsty));
//				sheet_1_0.setColumnView(3, 30);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "因残致贫（户）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "因学致贫（户）", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "因灾致贫（户）", tsty));
//				sheet_1_0.setColumnView(9, 35);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "缺土地（户）", tsty));
//				sheet_1_0.setColumnView(11, 25);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "缺水（户）", tsty));
//				sheet_1_0.setColumnView(13, 20);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "缺技术（户）", tsty));
//				sheet_1_0.setColumnView(15, 25);
//				
//				sheet_1_0.addCell(new Label( 16 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(16, 20);
//				
//				sheet_1_0.addCell(new Label( 17 , 0 , "缺劳力（户）", tsty));
//				sheet_1_0.setColumnView(17, 25);
//				
//				sheet_1_0.addCell(new Label( 18 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(18, 20);
//				
//				sheet_1_0.addCell(new Label( 19 , 0 , "缺资金（户）", tsty));
//				sheet_1_0.setColumnView(19, 25);
//				
//				sheet_1_0.addCell(new Label( 20 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(20, 20);
//				
//				sheet_1_0.addCell(new Label( 21 , 0 , "交通条件落后（户）", tsty));
//				sheet_1_0.setColumnView(21, 40);
//				
//				sheet_1_0.addCell(new Label( 22 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(22, 20);
//				
//				sheet_1_0.addCell(new Label( 23 , 0 , "自身发展力不足（户）", tsty));
//				sheet_1_0.setColumnView(23, 45);
//				
//				sheet_1_0.addCell(new Label( 24 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(24, 20);
//				
//				sheet_1_0.addCell(new Label( 25 , 0 , "因婚致贫（户）", tsty));
//				sheet_1_0.setColumnView(25, 30);
//				
//				sheet_1_0.addCell(new Label( 26 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(26, 20);
//				
//				sheet_1_0.addCell(new Label( 27 , 0 , "其他（户）", tsty));
//				sheet_1_0.setColumnView(27, 20);
//				
//				sheet_1_0.addCell(new Label( 28 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(28, 20);
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//26
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.6_贫困户帮扶责任人落实情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "落实帮扶责任人户数", tsty));
//				sheet_1_0.setColumnView(3, 45);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//27
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.7_贫困户生产生活条件困难情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "饮水困难户数", tsty));
//				sheet_1_0.setColumnView(3, 30);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "无安全饮水户数", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "未通生活用电户数", tsty));
//				sheet_1_0.setColumnView(7, 40);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "未通广播电视户数", tsty));
//				sheet_1_0.setColumnView(9, 40);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "住房是危房户数", tsty));
//				sheet_1_0.setColumnView(11, 35);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "无卫生厕所户数", tsty));
//				sheet_1_0.setColumnView(13, 35);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "人均住房面积（平方米）", tsty));
//				sheet_1_0.setColumnView(15, 55);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//28
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.8_贫困户土地资源情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "总人数（人）", tsty));
//				sheet_1_0.setColumnView(3, 25);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "耕地面积（亩）", tsty));
//				sheet_1_0.setColumnView(4, 30);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "人均（亩）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "有效灌溉面积（亩）", tsty));
//				sheet_1_0.setColumnView(6, 40);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "人均（亩）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "林地面积（亩）", tsty));
//				sheet_1_0.setColumnView(8, 30);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "人均（亩）", tsty));
//				sheet_1_0.setColumnView(9, 30);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "退耕还林面积（亩）", tsty));
//				sheet_1_0.setColumnView(10, 40);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "人均（亩）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "林果面积（亩）", tsty));
//				sheet_1_0.setColumnView(12, 30);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "人均（亩）", tsty));
//				sheet_1_0.setColumnView(13, 20);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "牧草地面积（亩）", tsty));
//				sheet_1_0.setColumnView(14, 35);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "人均（亩）", tsty));
//				sheet_1_0.setColumnView(15, 20);
//				
//				sheet_1_0.addCell(new Label( 16 , 0 , "水域面积（亩）", tsty));
//				sheet_1_0.setColumnView(16, 30);
//				
//				sheet_1_0.addCell(new Label( 17 , 0 , "人均（亩）", tsty));
//				sheet_1_0.setColumnView(17, 20);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				
//				//29
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.9_贫困户上年度人均收入分组情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "500元（不含）以下户数（户）", tsty));
//				sheet_1_0.setColumnView(3, 45);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "500-1000元户数（户）", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "1000-1500元户数（户）", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "1500-2000元户数（户）", tsty));
//				sheet_1_0.setColumnView(9, 35);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "2000-2500元户数（户）", tsty));
//				sheet_1_0.setColumnView(11, 35);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "2500-2800元户数（户）", tsty));
//				sheet_1_0.setColumnView(13, 35);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "2800元以上户数（户）", tsty));
//				sheet_1_0.setColumnView(15, 35);
//				
//				sheet_1_0.addCell(new Label( 16 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(16, 20);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				
//				//210
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.10_贫困户主要燃料分类统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数（户）", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "柴草户数（户）", tsty));
//				sheet_1_0.setColumnView(3, 30);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "干畜粪户数（户）", tsty));
//				sheet_1_0.setColumnView(5, 30);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "煤炭户数（户）", tsty));
//				sheet_1_0.setColumnView(7, 30);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "清洁能源户数（户）", tsty));
//				sheet_1_0.setColumnView(9, 40);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "其它（户）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				
				
				
//				//211
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.11_贫困户入户路情况分类统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "距离主干路（公里）", tsty));
//				sheet_1_0.setColumnView(3, 40);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "普通泥土路", tsty));
//				sheet_1_0.setColumnView(4, 25);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "砂石公路", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "水泥路面公路", tsty));
//				sheet_1_0.setColumnView(6, 30);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "沥青路面公路", tsty));
//				sheet_1_0.setColumnView(7, 30);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//212
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.12_贫困户上年度家庭收入情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "户数（户）", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				sheet_1_0.addCell(new Label( 3 , 0 , "人数（人）", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "年收入（人）", tsty));
//				sheet_1_0.setColumnView(4, 25);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "户均年收入(元)", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "生产经营性收入（元）", tsty));
//				sheet_1_0.setColumnView(6, 45);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "务工收入（元）", tsty));
//				sheet_1_0.setColumnView(7, 30);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "财产性收入（元）", tsty));
//				sheet_1_0.setColumnView(8, 35);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "转移性收入（元）", tsty));
//				sheet_1_0.setColumnView(9, 35);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "户均生产经营性支出（元）", tsty));
//				sheet_1_0.setColumnView(10, 55);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "户均纯收入（元）", tsty));
//				sheet_1_0.setColumnView(11, 35);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "人均纯收入（元）", tsty));
//				sheet_1_0.setColumnView(12, 35);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//213
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.13_贫困户上年度转移性收入构成情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "户数（户）", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				sheet_1_0.addCell(new Label( 3 , 0 , "户均转移性收入(元)", tsty));
//				sheet_1_0.setColumnView(3, 45);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "户均领取计划生育补贴金额(元)", tsty));
//				sheet_1_0.setColumnView(4, 70);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "户均领取低保金（元）", tsty));
//				sheet_1_0.setColumnView(6, 45);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "户均领取养老金（元）", tsty));
//				sheet_1_0.setColumnView(8, 45);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "户均领取生态补偿金（元）", tsty));
//				sheet_1_0.setColumnView(10, 55);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//214
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.14_贫困户人口规模统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "总户数（户）", tsty));
//				sheet_1_0.setColumnView(2, 25);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "一人户（户）", tsty));
//				sheet_1_0.setColumnView(3, 25);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "二人户（户）", tsty));
//				sheet_1_0.setColumnView(5, 25);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "三人户（户）", tsty));
//				sheet_1_0.setColumnView(7, 25);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "四人户（户）", tsty));
//				sheet_1_0.setColumnView(9, 25);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "五人户（户）", tsty));
//				sheet_1_0.setColumnView(11, 25);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "六人户（含）以上（户）", tsty));
//				sheet_1_0.setColumnView(13, 40);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//215
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.15_贫困户党员情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "户数", tsty));
//				sheet_1_0.setColumnView(2, 10);
//				sheet_1_0.addCell(new Label( 3 , 0 , "人数", tsty));
//				sheet_1_0.setColumnView(3, 10);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "有中共党员户数", tsty));
//				sheet_1_0.setColumnView(4, 35);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "中共党员人数", tsty));
//				sheet_1_0.setColumnView(6, 30);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "有预备中共党员户数", tsty));
//				sheet_1_0.setColumnView(8, 45);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "预备中共党员人数", tsty));
//				sheet_1_0.setColumnView(10, 35);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				
//				//216
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.16_贫困户住房面积情况分类统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困户户数", tsty));
//				sheet_1_0.setColumnView(2, 25);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "0平米户数", tsty));
//				sheet_1_0.setColumnView(3, 25);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "10平米以内户数", tsty));
//				sheet_1_0.setColumnView(5, 35);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "10-25平米户数", tsty));
//				sheet_1_0.setColumnView(7, 35);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "25-50平米户数", tsty));
//				sheet_1_0.setColumnView(9, 35);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "50-75平米户数", tsty));
//				sheet_1_0.setColumnView(11, 35);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "75-100平米户数", tsty));
//				sheet_1_0.setColumnView(13, 35);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				sheet_1_0.addCell(new Label( 15 , 0 , "100平米以上户数", tsty));
//				sheet_1_0.setColumnView(15, 35);
//				
//				sheet_1_0.addCell(new Label( 16 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(16, 20);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//217
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[1]+"/2.17_贫困户与村主干路距离分类统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困户户数", tsty));
//				sheet_1_0.setColumnView(2, 25);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "1公里以内(不含)户数", tsty));
//				sheet_1_0.setColumnView(3, 45);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "1(含)-3公里(不含)户数", tsty));
//				sheet_1_0.setColumnView(5, 40);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "3(含)-5公里(不含)户数", tsty));
//				sheet_1_0.setColumnView(7, 40);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(8, 20);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "5(含)-10公里(不含)户数", tsty));
//				sheet_1_0.setColumnView(9, 40);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(10, 20);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "10(含)-20公里(不含)户数", tsty));
//				sheet_1_0.setColumnView(11, 40);
//				
//				sheet_1_0.addCell(new Label( 12 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(12, 20);
//				
//				sheet_1_0.addCell(new Label( 13 , 0 , "20公里(含)以上户数", tsty));
//				sheet_1_0.setColumnView(13, 40);
//				
//				sheet_1_0.addCell(new Label( 14 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(14, 20);
//				
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
//				
				
				
//				//322
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[2]+"/22.全区各盟市建档立卡贫困村识别标准统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "行政村数", tsty));
//				sheet_1_0.setColumnView(2, 20);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "贫困村数", tsty));
//				sheet_1_0.setColumnView(3, 20);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "非贫困村数", tsty));
//				sheet_1_0.setColumnView(5, 25);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(6, 20);
//				
//			
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//323
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[2]+"/23.全区各盟市建档立卡贫困村未脱贫贫困人口统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "未脱贫贫困人口总户数", tsty));
//				sheet_1_0.setColumnView(2, 50);
//				sheet_1_0.addCell(new Label( 3 , 0 , "未脱贫贫困人口总人数", tsty));
//				sheet_1_0.setColumnView(3, 50);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "贫困村户数", tsty));
//				sheet_1_0.setColumnView(4, 25);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "贫困村人数", tsty));
//				sheet_1_0.setColumnView(6, 25);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(7, 20);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "非贫困村户数", tsty));
//				sheet_1_0.setColumnView(8, 45);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(9, 20);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "非贫困村人数", tsty));
//				sheet_1_0.setColumnView(10, 35);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(11, 20);
//				
//			
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				
//				//324
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[2]+"/24.全区各盟市建档立卡贫困村贫困发生率.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困村数", tsty));
//				sheet_1_0.setColumnView(2, 50);
//				sheet_1_0.addCell(new Label( 3 , 0 , "总人数", tsty));
//				sheet_1_0.setColumnView(3, 10);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "贫困人数", tsty));
//				sheet_1_0.setColumnView(4, 20);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "占比（%）", tsty));
//				sheet_1_0.setColumnView(5, 20);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "贫困村人数", tsty));
//				sheet_1_0.setColumnView(6, 25);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "贫困发生率（%）", tsty));
//				sheet_1_0.setColumnView(7, 30);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//416
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[3]+"/16.贫困人口分类扶持表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "贫困人口（人）", tsty));
//				sheet_1_0.setColumnView(2, 30);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "产业发展和转移就业人数", tsty));
//				sheet_1_0.setColumnView(3, 55);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "易地扶贫搬迁人数", tsty));
//				sheet_1_0.setColumnView(4, 40);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "生态补偿人数", tsty));
//				sheet_1_0.setColumnView(5, 30);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "教育扶贫人数", tsty));
//				sheet_1_0.setColumnView(6, 30);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "大病救治人数", tsty));
//				sheet_1_0.setColumnView(7, 30);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "社会保障兜底人数", tsty));
//				sheet_1_0.setColumnView(8, 40);
//				
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
//				
				
//				//64
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[5]+"/4.贫困户帮扶责任人统计表.xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "2015年底贫困户数", tsty));
//				sheet_1_0.setColumnView(2, 35);
//				
//				sheet_1_0.addCell(new Label( 3 , 0 , "落实帮扶责任人数", tsty));
//				sheet_1_0.setColumnView(3, 40);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "落实帮扶责任人比例", tsty));
//				sheet_1_0.setColumnView(4, 45);
//				
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
//				//65
//				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[5]+"/5.各级干部帮扶贫困户统计表(1).xls"));//打开文件
//				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
//				
//				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
//				sheet_1_0.setColumnView(0, 25);
//				
//				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
//				sheet_1_0.setColumnView(1, 25);
//				
//				sheet_1_0.addCell(new Label( 2 , 0 , "现有贫困户数", tsty));
//				sheet_1_0.setColumnView(2, 30);
//				sheet_1_0.addCell(new Label( 3 , 0 , "落实帮扶责任总人数", tsty));
//				sheet_1_0.setColumnView(3, 45);
//				
//				sheet_1_0.addCell(new Label( 4 , 0 , "省级领导", tsty));
//				sheet_1_0.setColumnView(4, 30);
//				
//				sheet_1_0.addCell(new Label( 5 , 0 , "省级帮扶户数", tsty));
//				sheet_1_0.setColumnView(5, 30);
//				
//				sheet_1_0.addCell(new Label( 6 , 0 , "市级领导", tsty));
//				sheet_1_0.setColumnView(6, 30);
//				
//				sheet_1_0.addCell(new Label( 7 , 0 , "市级帮扶户数", tsty));
//				sheet_1_0.setColumnView(7, 30);
//				
//				sheet_1_0.addCell(new Label( 8 , 0 , "县级领导", tsty));
//				sheet_1_0.setColumnView(8, 30);
//				
//				sheet_1_0.addCell(new Label( 9 , 0 , "县级帮扶户数", tsty));
//				sheet_1_0.setColumnView(9, 30);
//				
//				sheet_1_0.addCell(new Label( 10 , 0 , "县级以下干部", tsty));
//				sheet_1_0.setColumnView(10, 40);
//				
//				sheet_1_0.addCell(new Label( 11 , 0 , "县级以下帮扶户数", tsty));
//				sheet_1_0.setColumnView(11, 40);
//				
//			
//				
//				getWyApp_y2_6(sheet_1_0, xiang_List, coty);
//				book_1_0.write();
//				book_1_0.close();
				
				
//				//516
				WritableWorkbook book_1_0 = Workbook.createWorkbook( new File(str_path[4]+"/16.贫困人口分类扶持表.xls"));//打开文件
				WritableSheet sheet_1_0 = book_1_0.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
				sheet_1_0.setRowView(0, 500); // 设置第一行的高度
				
				sheet_1_0.addCell(new Label( 0 , 0 , "行政编码", tsty));
				sheet_1_0.setColumnView(0, 25);
				
				sheet_1_0.addCell(new Label( 1 , 0 , "嘎查村", tsty));
				sheet_1_0.setColumnView(1, 25);
				
				sheet_1_0.addCell(new Label( 2 , 0 , "贫困村数量（个）", tsty));
				sheet_1_0.setColumnView(2, 35);
				
				sheet_1_0.addCell(new Label( 3 , 0 , "贫困村总户数（户）", tsty));
				sheet_1_0.setColumnView(3, 40);
				
				sheet_1_0.addCell(new Label( 4 , 0 , "贫困户总户数", tsty));
				sheet_1_0.setColumnView(4, 30);
				
				sheet_1_0.addCell(new Label( 5 , 0 , "危房改造户数小计", tsty));
				sheet_1_0.setColumnView(5, 40);
				
				sheet_1_0.addCell(new Label( 6 , 0 , "危房改造其中贫困户", tsty));
				sheet_1_0.setColumnView(6, 45);
				
				sheet_1_0.addCell(new Label( 7 , 0 , "街巷硬化（米）", tsty));
				sheet_1_0.setColumnView(7, 30);
				
				sheet_1_0.addCell(new Label( 8 , 0 , "解决饮水安全户数小计", tsty));
				sheet_1_0.setColumnView(8, 55);
				
				sheet_1_0.addCell(new Label( 9 , 0 , "解决饮水安全户数其中贫困户", tsty));
				sheet_1_0.setColumnView(9, 65);
				
				sheet_1_0.addCell(new Label( 10 , 0 , "决通电和农网改造户数小计", tsty));
				sheet_1_0.setColumnView(10, 65);
				
				sheet_1_0.addCell(new Label( 11 , 0 , "决通电和农网改造户数其中贫困户", tsty));
				sheet_1_0.setColumnView(11, 75);
				
				sheet_1_0.addCell(new Label( 12 , 0 , "解决通广播电视和通讯户数小计", tsty));
				sheet_1_0.setColumnView(12, 70);
				
				sheet_1_0.addCell(new Label( 13 , 0 , "解决通广播电视和通讯户数其中贫困户", tsty));
				sheet_1_0.setColumnView(13, 85);
				
				sheet_1_0.addCell(new Label( 14 , 0 , "校舍建设或改造（平方米）", tsty));
				sheet_1_0.setColumnView(14, 55);
				
				sheet_1_0.addCell(new Label( 15 , 0 , "建标准卫生室（个）", tsty));
				sheet_1_0.setColumnView(15, 40);
				
				sheet_1_0.addCell(new Label( 16 , 0 , "建文化活动室（个）", tsty));
				sheet_1_0.setColumnView(16, 40);
				
				sheet_1_0.addCell(new Label( 17 , 0 , "建便民超市（个）", tsty));
				sheet_1_0.setColumnView(17, 35);
				
				sheet_1_0.addCell(new Label( 18 , 0 , "新增养老人数小计", tsty));
				sheet_1_0.setColumnView(18, 40);
				
				sheet_1_0.addCell(new Label( 19 , 0 , "新增低保人数其中贫困户", tsty));
				sheet_1_0.setColumnView(19, 55);
				
				sheet_1_0.addCell(new Label( 20 , 0 , "新增医保人数小计", tsty));
				sheet_1_0.setColumnView(20, 40);
				
				sheet_1_0.addCell(new Label( 21 , 0 , "新增低保人数其中贫困户", tsty));
				sheet_1_0.setColumnView(21, 55);
				
				sheet_1_0.addCell(new Label( 22 , 0 , "新增低保人数小计", tsty));
				sheet_1_0.setColumnView(22, 40);
				
				sheet_1_0.addCell(new Label( 23 , 0 , "新增低保人数其中贫困户", tsty));
				sheet_1_0.setColumnView(23, 55);
				
				getLinshi_5(sheet_1_0, xiang_List, coty);
				book_1_0.write();
				book_1_0.close();
				
				//第二张表--------------------------------------------
//				WritableWorkbook book_1_1 = Workbook.createWorkbook( new File(str_path[0]+"/1.1_贫困人口年龄分组情况统计表.xls"));//打开文件
//				WritableSheet sheet_1_1 = book_1_1.createSheet( "Sheet1 " , 0);//生成第一页工作表，参数0表示这是第一页
//				sheet_1_1.addCell(new Label( 0 , 0 , "行政编码", tsty1));
//				sheet_1_1.setColumnView(0, 25);
//				
//				sheet_1_1.addCell(new Label( 1 , 0 , "嘎查村", tsty1));
//				sheet_1_1.setColumnView(1, 25);
//				
//				//添加村数据
//				getWyApp_y2_6(sheet_1_1, xiang_List, coty);
//				book_1_1.write();
//				book_1_1.close();
				
				
			}
		}
		System.out.println("完成");
	}
	
	public void getLinshi_5(WritableSheet sheet, List<Map> list, WritableCellFormat coty) throws Exception{
		for (int i = 0; i < list.size(); i++) {
			Map cun_map = list.get(i);
			sheet.addCell(new Label( 0 , i+1 ,cun_map.get("COM_CODE")==null?"":cun_map.get("COM_CODE").toString() ,coty));
			sheet.addCell(new Label( 1 , i+1 ,cun_map.get("COM_NAME")==null?"":cun_map.get("COM_NAME").toString() ,coty));
			sheet.setRowView(i+1, 400); // 设置第一行的高度
		}
	}
	
	//新建文件夹
	public void getLinshi_6(String str_path) throws Exception {
		File uploadDir = new File(str_path);  
        if (!uploadDir.isDirectory()) {  
        	if(!uploadDir.exists()){
        		uploadDir.mkdirs();
        	}
        }
	}
}
