var zfsj; //走访时间
var bfgb;//帮扶干部
var weizhi; //走访位置
var jl; //走访记录
var pic_path;//照片
var yulan_photo = [];
$(function (){
	var Request = new Object();
	Request = GetRequest();
	zfsj = Request['zfsj'];
	bfgb = Request['bfgb'];
	weizhi = Request['weizhi'];
	jl = Request['jl'];
	pic_path = Request['pic_path'];
	sid = Request['sid'];
	qm();
	zfxq();
})
var qianming ;//签名
var c_time;//时间戳
var sj_num ;//随机数
//获取签名
function qm(){
	$.ajax({  		       
	    url: '/assa/getQianming.do',
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
//走訪記錄詳情
function zfxq(){
	var html = '<ul class="dorm-book mt3">'+
					'<li class="basic-tit">'+
					    '走访记录详情'+
					'</li>'+
					'<li>'+
						'<span class="book-tit" style="width:30%;">'+
					    '<span class="stu-name">走访时间</span></span>'+
					    '<span class="info-middle">'+zfsj+'</span> '+ 
					'</li>'+
					'<li>'+
						'<span class="book-tit" style="width:30%;">'+
					   ' <span class="stu-name">帮扶干部</span></span>'+
					    '<span class="info-middle">'+bfgb+'</span>  '+
					'</li>'+
					'<li>'+
						'<span class="book-tit" style="width:30%;">'+
					    '<span class="stu-name">走访位置</span></span>'+
					    '<span class="info-middle">'+weizhi+'</span>  '+
					'</li>'+
					'<li>'+
						'<span class="book-tit" style="width:30%;">'+
					    '<span class="stu-name">走访记录</span></span>'+
					    '<span class="info-middle">'+jl+'</span>  '+
					'</li>'+
					'<li>'+
						'<span class="book-tit" style="width:30%;">'+
					    '<span class="stu-name">走访照片</span></span>'+
					    '<span class="info-middle"></span> '+ 
					'</li>'+
					'<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#dddddd SIZE=1>'+
					'<div style="width:100%; padding:10px;display:inline;">';
					if(pic_path == "" || pic_path == null || pic_path == undefined){
						
					}else{
						var pic = pic_path.split(",");
						for (var i = 0 ; i < pic.length ; i ++){
							html += '<div style="width:20%;height:20%;float:left"><img src="'+pic[i]+'" style="width:95%;height:80px" onclick="yulan(\''+pic[i]+'\',\''+pic+'\')"/></div>';
							yulan_photo[i]='http://www.gistone.cn/'+pic[i];
						}
					}
					html += '</div>'+
			'</ul>';
	$("#zfxq").html(html);
}

//图片的预览功能
function yulan(name,pic){
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			appId: 'wx4fa9e95d9af2477a', // 必填，公众号的唯一标识
			timestamp: c_time, // 必填，生成签名的时间戳
			nonceStr: sj_num, // 必填，生成签名的随机串
			signature: qianming,// 必填，签名，见附录1
			jsApiList: ['checkJsApi',
	        'onMenuShareTimeline',
	        'onMenuShareAppMessage',
	        'onMenuShareQQ',
	        'onMenuShareWeibo',
	        'hideMenuItems',
	        'showMenuItems',
	        'hideAllNonBaseMenuItem',
	        'showAllNonBaseMenuItem',
	        'translateVoice',
	        'startRecord',
	        'stopRecord',
	        'onRecordEnd',
	        'playVoice',
	        'pauseVoice',
	        'stopVoice',
	        'uploadVoice',
	        'downloadVoice',
	        'chooseImage',
	        'previewImage',
	        'uploadImage',
	        'downloadImage',
	        'getNetworkType',
	        'openLocation',
	        'getLocation',
	        'hideOptionMenu',
	        'showOptionMenu',
	        'closeWindow',
	        'scanQRCode',
	        'chooseWXPay',
	        'openProductSpecificView',
	        'addCard',
	        'chooseCard',
	        'openCard'] // 必填，需要
		
			});
	wx.ready(function(){
		wx.previewImage({
			
		    current: 'http://www.gistone.cn/'+name, // 当前显示图片的http链接
		    urls: yulan_photo // 需要预览的图片http链接列表
		});
	});

}