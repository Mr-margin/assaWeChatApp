var qianming ;//签名
var c_time;//时间戳
var sj_num ;//随机数
$(function(){
	qianming();
})
//获取数字签名
function qianming(){
	$.ajax({
	  url: '/assa/getQianming.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {url : window.location.href},
		success : function(data){
	    	c_time = data[0].time;
	    	sj_num = data[0].num;
	    	qianming = data[0].val;
	    	token = data[0].token;
	    },
		error:function(){
			
		}
		
	})
}
//扫一扫
function sm(){
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
		wx.scanQRCode({
    	    needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
    	    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
    	    success: function (res) {
    	    	var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
    	    	var a = result.split("=")
//    	    	savePoor(a[a.length-1]);
    	    }
    	});
	});
	wx.error(function(res){
		
	});
}
//扫一扫返回的贫困户 信息 
function savePoor(pkid){
	$.ajax({
		 url: '/assa/getCha_huController.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {pkid :pkid},
	    success :function (data){
	    	if(data.message==5){
	    		$.each(data.result,function(i,item){
	    			if ( i == 0 ) {
	    				window.location.href = 'fpdxjx.html?dz='+item.v1+item.v2+item.v3+item.v4+item.v5+'&renshu='+item.v9+'&phone='+item.v25+'&khyh='+item.v26+'&yhzh='+item.v27+'&'+
			    		'sbbz='+item.v34+'&shuxing='+item.v21+'&jls='+item.v29+'&zpyy='+item.v23+'&qtzp='+item.v33+'&rjsr='+item.v24+'&url='+item.pic_path+'&v6='+item.v6+'&poor_id='+item.pkid+'&sid='+sid+'&pkid='+pkid+'&bottom=0';
	    			}
	    		})
	    	}
	    }
		
	})
}