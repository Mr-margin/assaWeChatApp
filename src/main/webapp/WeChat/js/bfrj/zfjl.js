
var html = '';

$(document).ready(function() {
	qm();
	//show_lsjl();//现在缓存的走访日记，未提交的
	show_jbqk();
});
//$(function(){
//	show_jbqk();
//})
var personal_name;//帮扶人的姓名
var personal_phone;//帮扶人的电话
var household_name;//贫困户姓名
var zjhm;//证件号码
var iszzyl = false;//正在预览中，，，
function qm(){
	$.ajax({  		       
	    url: '/assaWeChatApp/getQianming.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {url : window.location.href},
	    success: function (data) {
	    	c_time = data[0].time;
	    	sj_num = data[0].num;
	    	qianming = data[0].val;
	    	token = data[0].token;
	    },
	    error: function (data) {
	    	
	    }  
	})
}
function show_jbqk(){
	var Request = new Object();
	Request = GetRequest();//截取URL的方法
	household_name=Request['household_name']; 
	zjhm = Request['zjhm'];
	personal_name=Request['name'];
	personal_phone = Request['phone'];
	$.ajax({  		       
	    url: '/assaWeChatApp/getSaveVisit.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {household_name:household_name,household_cord:zjhm,personal_name:personal_name,personal_phone:personal_phone},
	    success: function (data) {

	    	$.each(data.data,function(i,item){
	    		html += '<div class="col-sm-12"><div class="panel panel-default" >'
	    		html += '<div class="panel-heading" style="padding-left: 0px"><img src="img/day.png" style="margin:0;vertical-align:middle;width:50px;height:29px;"><span style="font-size:16px;vertical-align: middle">时间：'+item.b+'</span>' +
					'<img style="float: right;height: 2.9rem ;vertical-align: middle" src="images/uploaded.png"></div>';
	    		html += ' <div class="panel-body"><div class="row">'
				html += '<div class="col-sm-12"><div> 帮扶责任人：<strong>'+item.e+'</strong></div><div><p style="padding-left: 25px;">贫困户：'+item.v6+'</p></div>';
				html += '<div><p style="padding-left: 12px;">走访类型：'+setzflx(item.t)+'</p></div>'
				html += '<div><p style="padding-left: 12px;">走访记录：'+item.c+'</p></div>'
	    		if(item.d == "" || item.d == null || item.d == undefined){
				}else{
					var pic = (item.d).split(",");
					var zf_photo = [];
					for (var i = 0 ; i < pic.length ; i ++){
						zf_photo[i] ='http://www.gistone.cn/'+pic[i];
					}
					for (var i = 0 ; i < pic.length ; i ++){
						html += '<div style="width:20%;height:20%;float:left">'+
						'<img src="'+pic[i]+'"style="width:95%;height:80px" onclick="yulan(\''+pic[i]+'\',\''+zf_photo+'\')"/></div>';
					}
				}
				html += '</div><div class="col-sm-12">&nbsp;</div></div></div></div></div>';
			})
			$("#zoufangqingkuang").html(html);
	    },
	    error: function (data) { 
	    	
	    }  
	})
	
}
function setzflx(p){
	if (p == 1){
		return '其他帮扶活动';
	}else if(p ==2){
		return '了解基本情况';
	}else if(p ==3){
		return '填写扶贫手册';
	}
	else if(p ==4){
		return '制定脱贫计划';
	}
	else if(p ==5){
		return '落实资金项目';
	}
	else if(p ==6){
		return '宣传扶贫政策';
	}
	else if(p ==7){
		return '节日假日慰问';
	}
}

//图片的预览功能
function yulan(p_name,pic){
	if (iszzyl){
		alert("正在开启预览图片功能，不可重复请求。");
		return;
	}
	iszzyl = true;
	var str = pic.split(",");
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			appId: 'wx4fa9e95d9af2477a', // 必填，公众号的唯一标识
			timestamp: c_time, // 必填，生成签名的时间戳
			nonceStr: sj_num, // 必填，生成签名的随机串
			signature: qianming,// 必填，签名，见附录1
			jsApiList: [
	        'chooseImage',
	        'previewImage',
	        'chooseCard',
	        'openCard'] // 必填，需要
		
			});
	wx.ready(function(){
		wx.previewImage({
		    current: 'http://www.gistone.cn/'+p_name, // 当前显示图片的http链接
		    urls: str // 需要预览的图片http链接列表
		});
		iszzyl = false;
	});
	wx.error(function(res){
		alert("微信签名失败，无法调用图片预览");
		iszzyl = false;
	});

}
/**
 * 显示缓存的数据,即未提交的走访记录
 */
function show_lsjl(){

	var currentdate = getNowFormatDate();
	var Request = new Object();
	Request = GetRequest();//截取URL的方法
	household_name=Request['household_name'];
	zjhm = Request['zjhm'];
	personal_name=Request['name'];
	personal_phone = Request['phone'];

	var lsdata;
	if(!window.localStorage){
		alert("浏览器不支持缓存无法读取未提交的记录！");
		return false;
	}else{
		if(!(localStorage.mzfjl == null)){
			lsdata = JSON.parse(localStorage.mzfjl);
			lsdata.reverse();
			var qdtypeimg = "images/offsite.png";
			$.each(lsdata,function(i,item){
				/*var iscs = dateFormatter(lsdata[i].zftime);*/
				if (lsdata[i].uphone == personal_phone){
					html += '<div class="col-sm-12"><div class="panel panel-default" >'

					if (lsdata[i].regtype == 1){
						qdtypeimg = "images/site.png";
					}
					html +='<div id="zfjl_heading" class="panel-heading" style="min-height: 50px;padding-left: 0px"><img src="img/day.png" style="margin:0;vertical-align:middle;width:40px;height:23px;">' +
						'<span style="font-size:16px;vertical-align: middle">'+lsdata[i].zftime+'</span>'+
						'<img style="float: right;height: 2.9rem ;vertical-align: middle" onclick="bjlsdata(\''+lsdata[i].zftime+'\')"  src="images/upload.png">'+
						'<img style="float: right;height: 2.9rem ;vertical-align: middle"  src="'+qdtypeimg+'">'+
						'</img><img style="float: right;height: 2.9rem ;vertical-align: middle" src="images/delete.png" onclick="dellsdata(\''+lsdata[i].zftime+'\')">'+'</img></div>';

					html += ' <div class="panel-body"><div class="row">'
					html += '<div class="col-sm-12"><div> 帮扶责任人：<strong>'+personal_name+'</strong></div><div><p style="padding-left: 25px;">贫困户：'+lsdata[i].p_name+'</p></div>';
					html += '<div><p style="padding-left: 12px;">走访记录：'+lsdata[i].zfinfo+'</p></div>'
					if(lsdata[i].photo == "" || lsdata[i].photo == null ||lsdata[i].photo == undefined){
					}else{
						var pic = (lsdata[i].photo).split(",");
						var zf_photo = [];
						for (var i = 0 ; i < pic.length ; i ++){
							zf_photo[i] ='http://www.gistone.cn/'+pic[i];
						}
						for (var i = 0 ; i < pic.length ; i ++){
							html += '<div style="width:20%;height:20%;float:left">'+
								'<img src="'+pic[i]+'"style="width:95%;height:80px" onclick="yulan(\''+pic[i]+'\',\''+zf_photo+'\')"/></div>';
						}
					}
					html += '</div><div class="col-sm-12">&nbsp;</div></div></div></div></div>';
				}

			})
			$("#zoufangqingkuang").html(html);
		}
	}
}

/**
 * 编辑按钮的跳转
 * @param lsdate
 */
function bjlsdata(lsdate){
	var Request = new Object();
	Request = GetRequest();//截取URL的方法
	personal_name=Request['name'];
	personal_phone = Request['phone'];
	window.location.href = "tjzf.html?phone="+personal_phone+"&name="+personal_name+"&lsdate="+lsdate;
}
/**
 *
 * @param lsdate
 */
function dellsdata(lsdate){
	if(confirm("你确要删除吗？"))

	{
		var lsdata = JSON.parse(localStorage.mzfjl);
		var potion;
		$.each(lsdata,function(i,item){
			if (lsdate == lsdata[i].zftime){
				potion = i;
			}
		})
		lsdata.splice(potion,1);
		var objStr=JSON.stringify(lsdata);
		localStorage.mzfjl = objStr;
		alert("删除成功！");
		location.reload(window.location.href);
	}
	else
	{
	}
}

/**
 *
 * @returns {string}
 */
function getNowFormatDate() {
	var date = new Date();
	var seperator1 = "-";
	var seperator2 = ":";
	var month = date.getMonth() + 1;
	var strDate = date.getDate();
	if (month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if (strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
		+ " " + date.getHours() + seperator2 + date.getMinutes()
		+ seperator2 + date.getSeconds();
	return currentdate;
}

//判断保存的记录据当前多久
function dateFormatter(value) {
//value 值格式 "2015-12-07 19:09:09"

	var today = getNowFormatDate(),
		hour,//比小时大的数
		minute, //小于 小时的时候判断值
		str;
	hour = parseInt(((Date.parse(today) - Date.parse(value)) / 1000 / 3600), 10); //取得整数的小时
		if (hour < 24){
			return true;
		}else {
			return false;
		}

	/*if (hour > 0) {
		if (hour < 24) {
			str = hour + "小时前";
		} else if (hour >= 24 && hour <= 720) {
			str = parseInt((hour / 24), 10) + "天前";
		} else if (hour > 720) {
			str = parseInt((hour / 720), 10) + "月前";
		}
	} else {
		minute = parseInt(((Date.parse(today) - Date.parse(value)) / 1000 / 60), 10);//获得分钟数
		if (minute > 0 && minute < 60) {
			str = minute + "分钟前";
		} else {
			str = " 1 分钟前";
		}
	}
	return str;*/
}