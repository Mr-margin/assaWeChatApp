$(document).ready(function() {
	qm();
	show_jbqk();
});
var poor_id;//贫困户id
var sid;//帮扶人的id
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
function show_jbqk(){
	var Request = new Object();
	Request = GetRequest();//截取URL的方法
	sid=Request['sid']; 
	poor_id = Request['poor_id'];
	$.ajax({  		       
	    url: '/assa/getSaveVisit.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {persion_id:sid,pid:poor_id},
	    success: function (data) {
	    	var html = ''
	    	$.each(data.result,function(i,item){
	    		html += '<div class="col-sm-12"><div class="panel panel-default" >'
	    		html += '<div class="panel-heading"><img src="img/day.png" style="margin:0;vertical-align:middle;width:50px;height:29px;"><span style="font-size:17px">时间：'+item.b+'</span></div>';
	    		html += ' <div class="panel-body"><div class="row">'
				html += '<div class="col-sm-12"><div> 帮扶责任人：<strong>'+item.e+'</strong></div><div><p style="padding-left: 25px;">贫困户：'+item.v6+'</p></div>';
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
//图片的预览功能
function yulan(p_name,pic){
	var str = pic.split(",");
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
			
		    current: 'http://www.gistone.cn/'+p_name, // 当前显示图片的http链接
		    urls: str // 需要预览的图片http链接列表
		});
	});

}