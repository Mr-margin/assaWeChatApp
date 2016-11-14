//var localIds;//图片
var qianming ;//签名
var c_time;//时间戳
var sj_num ;//随机数
var latitude ;// 纬度，浮点数，范围为90 ~ -90
var longitude ;// 经度，浮点数，范围为180 ~ -180。
var token ;//token
$(function(){
	var Request = new Object();
	Request = GetRequest();
	sid = Request['sid'];
	poor_name();
	qm();
});
//帮扶人名下的贫困户
function poor_name(){
	var html = '<option value="请选择">请选择</option>';
	$.ajax({  		       
	    url: '/assa/getPoorName.do',  
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {sid:sid},
	    success: function (data) {
    		$.each(data,function(i,item){
    			html +='<option value="'+item.pkid+'">'+item.v6+'</option>';
    		})
    		$("#poor_name").html(html);
	    },
	    error: function (ret) { 
	    	alert('失败')
	    }  
	})
}
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
var pp='';
//上传照片
function photo(){
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
	    // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
		 wx.chooseImage({
	    count: 6, // 默认9
	    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
	    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
	    success: function (res) {
//	    	var html = '<tr class="row">';
	    	var html = '';
	    	localIds = res.localIds; //返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
	       
	    	
	    	
	    	
	        for ( var i = 0 ; i < localIds.length; i ++ ){
	        	html += '<div style="width:20%;height:20%;float:left"><img src="'+localIds[i]+'" border=0 style="width:95%;height:80%;" ></div>'
	        	
	        	wx.uploadImage({
		    	    localId: localIds[i], // 需要上传的图片的本地ID，由chooseImage接口获得
		    	    isShowProgressTips: 0, // 默认为1，显示进度提示
		    	    success: function (res) {
		    	        var serverId = res.serverId; // 返回图片的服务器端ID
		    	       
						 pp += "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+token+"&media_id="+serverId+",";
		    	    }
		    	});
	        }
	        html += '<div style="width:20%;height:20%;float:left;padding-top:15px;"><img src="img/add1.png" border=0 style="width:95%;height:80%;" onclick="photo()" ></div>'
	        $("#yulan").html(html);
	    }
		 	});
		});
	wx.error(function(res){
//		alert("失败")
	    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
	});
}
//签到获取经纬度
function qiandao (){
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
	wx.getLocation({
	    type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
	    success: function (res) {
	        latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
	        longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
	        var speed = res.speed; // 速度，以米/每秒计
	        var accuracy = res.accuracy; // 位置精度
	    }
	});
	if(latitude != "" || latitude != null){
		$("#print").hide();
		$("#print1").show();
	}
}
//添加走访记录
function  addzfjl() {
	var newstr=pp.substring(0,pp.length-1);
	var w_p = newstr.split(",");
	var poor_id = $("#poor_name").val();
	if(poor_id == "请选择" || poor_id == null || poor_id == ""){
		alert("请选择扶贫对象")
		return ;
	}
	$.ajax({  		       
	    url: '/assa/addZfjl.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    traditional : true,
	    data: {
	    	poor_id:poor_id,
	    	zfjl:$("#zfjlwz").val(),
	    	"photo":w_p,
	    	latitude: latitude,
	    	longitude:longitude,
	    	sid:sid,
	    },
	    success: function (data) {
	    	if(data == "5"){
	    		alert('添加成功');
	    		location.reload(window.location.href) 
	    	}
	    	
	    },
	    error: function (data) {
	    	
	    }  
	})

}