//var localIds;//图片
var qianming;//签名
var c_time;//时间戳
var sj_num;//随机数
var latitude;// 纬度，浮点数，范围为90 ~ -90
var longitude;// 经度，浮点数，范围为180 ~ -180。
var token;//token

var sendlatitude;//上传时坐标
var sendlongitude;

var hhhtlat = 111.755176;//解析不到地址设置默认的呼和浩特坐标
var hhhtlng = 40.848607;


//*****缓存变量
var lsdate;//缓存记录的日期
var potion;//缓存记录的索引

//非现场签到所需的地址
var pkhadd = "";//贫困户家庭地址，用于解析坐标
var pkhcm = "";//贫困户的村名-用于检索
var qdtype = 1;//标记签到类型
var poordata;
/*$("#zftimediv").hide();//默认不让选择时间*/
var zftime = getNowFormatDate();
var zftype = 0;
var iszztj = false;
var countdown = 10;//签到倒计时
var isxcqd = false;//是否正在定位
$('#zfjlwz').focus(kstxzfjl);
$('#poor_type').change(xzzflx);
$(function () {
    var Request = new Object();
    Request = GetRequest();
    phone = Request['phone'];
    name = Request['name'];
    lsdate = Request['lsdate'];//如果是新增走访记录为0 如果是来至我的日志的编辑则带日期参数
    if (lsdate != 0) {
        //initData();//网页来至我的日志缓存记录的编辑携带了缓存记录的时间 根据缓存初始编辑页面的显示信息
    } else {
        poor_name();
    }
    qm();

});
//帮扶人名下的贫困户
function poor_name() {
    var html = '<option value="请选择">请选择</option>';
    $.ajax({
        url: '/assaWeChatApp/getSavePoorController.do',
        type: "POST",
        async: false,
        dataType: 'json',
        data: {phone: phone, name: name},
        success: function (data) {
            poordata = data.data;
            $.each(data.data, function (i, item) {
                var sftp = "（未脱贫）";
                if (item.sftp == 1) {
                    sftp = "（已脱贫）";
                }
                html += '<option value="' + item.v8 + '">' + item.v6 + sftp + '</option>';
            })
            $("#poor_name").html(html);
        },
        error: function (ret) {

        }
    })
}
//获取签名
function qm() {
    $.ajax({
        url: '/assaWeChatApp/getQianming.do',
        type: "POST",
        async: false,
        dataType: 'json',
        data: {url: window.location.href},
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
var pp = [];
var ylhtml = '';
//上传照片
function photo() {
    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: 'wx4fa9e95d9af2477a', // 必填，公众号的唯一标识
        timestamp: c_time, // 必填，生成签名的时间戳
        nonceStr: sj_num, // 必填，生成签名的随机串
        signature: qianming,// 必填，签名，见附录1
        jsApiList: [
            'chooseImage',
            'previewImage',
            'uploadImage',
            'downloadImage',
            'getNetworkType'
        ] // 必填，需要

    });
    wx.ready(function () {
        // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
        wx.chooseImage({
            count: 6, // 默认9
            sizeType: ['compressed'], // 可以指定是原图还是压缩图，默认二者都有
            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
            success: function (res) {
//	    	var html = '<tr class="row">';
                pp = [];
                localIds = res.localIds; //返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                scwxtp(localIds,0);

            }
        });
    });
    wx.error(function (res) {
//		alert("失败")
        // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
    });
}

function scwxtp(tpids,p){
    if (p == tpids.length){
        ylhtml += '<div style="width:20%;height:20%;float:left;padding-top:15px;"><img src="img/add1.png" border=0 style="width:95%;height:80%;" onclick="photo()" ></div>'
        $("#yulan").html(ylhtml);
        return;
    }
    ylhtml += '<div style="width:20%;height:20%;float:left"><img src="' + tpids[p] + '" border=0 style="width:95%;height:80%;" ></div>'
    wx.uploadImage({
        localId: tpids[p], // 需要上传的图片的本地ID，由chooseImage接口获得
        isShowProgressTips: 1, // 默认为1，显示进度提示
        success: function (res) {
            p++;
            var serverId = res.serverId; // 返回图片的服务器端ID
            // pp += "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + serverId + ",";
            pp.push("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + serverId);

            scwxtp(tpids,p);
        }
    });
}
var timeout;
function settime(val) {
    if (countdown == 0) {
        val.innerHTML = "重&nbsp;新&nbsp;签&nbsp;到";
        countdown = 10;
        clearTimeout(timeout);
        isxcqd = true;
        if (latitude == "" || latitude == null || latitude == undefined) {
            $("#print3").show();
            $("#print5").hide();
        }
        return;
    } else {
        val.innerHTML = "定&nbsp;位&nbsp;中(" + countdown + "s)";
        countdown--;
    }
    timeout = setTimeout(function () {
        settime(val)
    }, 1000)
}

//签到获取经纬度
function qiandao() {

    if (countdown != 0 && countdown != 10) {
        return;
    }
    isxcqd = false;
    var household_card = $("#poor_name").val();//贫困户证件号码
    if (household_card == "请选择" || household_card == null || household_card == "") {
        alert("请选择扶贫对象");
        return;
    }
    $("#print3").hide();
    $("#print5").show();
    settime(document.getElementById("print"));
    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: 'wx4fa9e95d9af2477a', // 必填，公众号的唯一标识
        timestamp: c_time, // 必填，生成签名的时间戳
        nonceStr: sj_num, // 必填，生成签名的随机串
        signature: qianming,// 必填，签名，见附录1
        jsApiList: [
            'getNetworkType',
            'openLocation',
            'getLocation',
            'hideOptionMenu',
            'showOptionMenu',
            'closeWindow',
            'scanQRCode'
        ] // 必填，需要

    });
    wx.getLocation({
        type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
        success: function (res) {
            if (isxcqd) {//用于判断是否点击过签到按钮并且超过十秒
                return;
            }
            latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
            longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
            var speed = res.speed; // 速度，以米/每秒计
            var accuracy = res.accuracy; // 位置精度
            qdtype = 1;
            zftime = getNowFormatDate();
            sendlatitude = latitude;
            sendlongitude = longitude;

            $("#print").hide();
            $("#print6").hide();
            $("#print1").show();
            $("#print3").hide();
            $("#print4").hide();
            $("#print5").show();
        }
    });
    /*if(latitude != "" && latitude != null && latitude != undefined){
     $("#print").hide();
     $("#print1").show();
     }*/
}
//添加走访记录 --提交到服务器
function addzfjl() {
    if (iszztj) {
        alert("正在上传日志！请勿重复提交！")
        return;
    }
    if (latitude == "" || latitude == null || latitude == undefined) {
        alert("必须签到成功才可以提交！");
        return;
    }
    if (pp.length<=0) {
        alert("必须上传图片才可以提交！");
        return;
    }
    var zfjlwz = $("#zfjlwz").val();
    if (zfjlwz.length > 300) {
        alert("走访记录文字不得超过300个，请重新输入!");
        return;
    }
    zftype = $('#poor_type').val();
    if (zftype == 0) {
        alert("请选择走访类型，并填写走访记录!");
        return;
    }

    dingewi();

    if (qdtype == 2) {
        zftime = $("#AbsentEndDate").val();
    }
    iszztj = true;
    $("#deng").show();
    $("#tijiao").hide();

    var household_card = $("#poor_name").val();//贫困户证件号码

    if (household_card == "请选择" || household_card == null || household_card == "") {
        alert("请选择扶贫对象")
        return;
    }
    var household_name = $("#poor_name").find("option:selected").text();//贫困户的姓名

    $.ajax({
        url: '/assaWeChatApp/addZfjl.do',
        type: "POST",
        async: false,
        dataType: 'json',
        traditional: true,
        data: {
            personal_name: name,
            personal_phone: phone,
            household_name: household_name.substring(0,household_name.indexOf("（")),
            household_card: household_card,
            zfjl: zfjlwz.replace(/'/g, "’"),
            "photo": pp,
            latitude: latitude,
            longitude: longitude,
            registerTime: formattime(zftime),
            sendLat: sendlatitude,
            sendLng: sendlongitude,
            registerType: qdtype,
            zfType: zftype
        },
        success: function (data) {
            if (data == "5") {
                alert('添加成功');
                location.replace(location);
                window.location.href = "bfrj.html?phone=" + phone + "&name=" + name;
                if (lsdate != 0) {
                    dellsdata(lsdate);
                }
            } else if (data == "0") {
                alert('图片上传失败，请重新上传。');
                iszztj = false;
                $("#deng").hide();
                $("#tijiao").show();
            }

        },
        error: function (data) {
            alert('添加失败');
            iszztj = false;
            location.reload(window.location.href);
        }
    })

}

/*/!**
 * 走访记录保存为缓存
 * @returns {boolean}
 *!/
function savezfjl() {
    if (!window.localStorage) {
        alert("浏览器不支持缓存请直接提交！");
        return false;
    } else {
        if (latitude == "" || latitude == null || latitude == undefined) {
            alert("必须签到成功才可以保存！");
            return;
        }
        var household_card = $("#poor_name").val();//贫困户证件号码
        var zfinfo = $("#zfjlwz").val();
        var household_name = $("#poor_name").find("option:selected").text();//贫困户的姓名
        var photopath = pp.substring(0, pp.length - 1);

        if (qdtype == 2) {
            zftime = $("#AbsentEndDate").val();
        }
        if (lsdate == 0) {//添加新的走访记录
            var currentdate = getNowFormatDate();
            var mzfjl = new myzfjl(phone, currentdate, household_name, household_card, zfinfo, photopath, latitude, longitude, qdtype, zftime);
            if (localStorage.mzfjl == null) {
                var arra = [];
                arra.push(mzfjl);
                var objStr = JSON.stringify(arra);
                localStorage.mzfjl = objStr;
                alert("保存成功！");
                location.reload(window.location.href)
            } else {
                var arra = JSON.parse(localStorage.mzfjl);
                arra.push(mzfjl);
                var objStr = JSON.stringify(arra);
                localStorage.mzfjl = objStr;
                alert("保存成功！");
                location.reload(window.location.href)
            }
        } else {//修改保存过的走访记录
            var arra = JSON.parse(localStorage.mzfjl);
            var mzfjl = new myzfjl(phone, lsdate, household_name, household_card, zfinfo, photopath, latitude, longitude, qdtype, zftime);
            arra[potion] = mzfjl;
            var objStr = JSON.stringify(arra);
            localStorage.mzfjl = objStr;
            alert("保存成功！");
            location.reload(window.location.href)
        }
    }
}*/
/**
 * 缓存保存的对象
 * @param zftime 走访时间 年月日 时分秒
 * @param p_name 帮扶对象姓名
 * @param p_card 帮扶对象编码
 * @param zfinfo 走访记录
 * @param photo  图片
 * @param lat    坐标lat
 * @param lng      坐标lng
 */
function myzfjl(uphone, zftime, p_name, p_card, zfinfo, photo, lat, lng, regtype, regtime) {
    this.uphone = uphone;
    this.zftime = zftime;
    this.p_name = p_name;
    this.p_card = p_card;
    this.zfinfo = zfinfo;
    this.photo = photo;
    this.lat = lat;
    this.lng = lng;
    this.regtype = regtype;
    this.regtime = regtime;
}
/**
 * 获取当前时间
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
/**
 * 页面来至历史记录，初始保存的数据
 */
function initData() {

    var lsdata = JSON.parse(localStorage.mzfjl);
    $.each(lsdata, function (i, item) {
        if (lsdate == lsdata[i].zftime) {
            potion = i;
        }
    })
    latitude = lsdata[potion].lat;
    longitude = lsdata[potion].lng;

    sendlatitude = latitude;
    sendlongitude = longitude;

    dingewi();

    qdtype = lsdata[potion].regtype;
    zftime = lsdata[potion].regtime;
    if (qdtype == 2) {
        $("#zftimediv").show();
        $("#AbsentEndDate").attr("value", zftime);
    } else {
        $("#zftimediv").hide()
    }
    if (lsdata[potion].lat != "" && lsdata[potion].lat != null && lsdata[potion].lat != undefined) {
        latitude = lsdata[potion].lat;
        longitude = lsdata[potion].lng;
        $("#print").hide();
        $("#print1").show();
        $("#print3").hide();
        $("#print4").show();
    }
    var html = '<option value="' + lsdata[potion].p_card + '">' + lsdata[potion].p_name + '</option>';
    $("#poor_name").html(html);
    $("#zfjlwz").html(lsdata[potion].zfinfo);

    var photohtml = '';
    var photos = lsdata[potion].photo;
    var photourl = photos.split(",");
    pp = photos + ",";
    for (var i = 0; i < photourl.length; i++) {
        photohtml += '<div style="width:20%;height:20%;float:left"><img src="' + photourl[i] + '" border=0 style="width:95%;height:80%;" ></div>'
    }
    photohtml += '<div style="width:20%;height:20%;float:left;padding-top:15px;"><img src="img/add1.png" border=0 style="width:95%;height:80%;" onclick="photo()" ></div>'
    $("#yulan").html(photohtml);
}

//非现场签到，通过贫困户地址信息解析坐标
function fqiandao() {
    var household_card = $("#poor_name").val();//贫困户证件号码
    if (household_card == "请选择" || household_card == null || household_card == "") {
        alert("请选择扶贫对象")
        return;
    }
    dingewi();
    zftime = getNowFormatDate();
    $("#print").hide();
    $("#print6").show();

    var p_city = '';

    for (var i = 0; i < poordata.length; i++) {
        if (household_card == poordata[i].v8) {
            pkhadd = poordata[i].v2 + poordata[i].v3 + poordata[i].v4 + poordata[i].v5;
            pkhcm = poordata[i].v5;
            p_city = poordata[i].v2;
        }
    }

    if (pkhadd == "") {//贫困户地址为空，设置坐标为呼和浩特市中心
        alert("贫困户没有录入地址！签到坐标设为呼和浩特市中心");
        latitude = hhhtlat;
        longitude = hhhtlng;

        sendlatitude = latitude;
        sendlongitude = longitude;

        qdtype = 3;//非现场签到并且地址未能解析
        $("#zftimediv").show();
        $("#AbsentEndDate").attr("value", zftime);

        $("#print3").hide();
        $("#print4").show();
        return;
    }

    pkhadd = qwhstr(pkhadd);
    pkhcm = qwhstr(pkhcm);
// 百度地图API功能
    var map = new BMap.Map("allmap");
    // 创建地址解析器实例
    var myGeo = new BMap.Geocoder();
    // 将地址解析结果显示在地图上,并调整地图视野
    myGeo.getPoint(pkhadd, function (point) {
        if (point != null) {
            var local = new BMap.LocalSearch(point, {
                renderOptions: {map: map}
            });
            local.search(pkhcm);
            if (local.getStatus() == BMAP_STATUS_SUCCESS) {
                local.setSearchCompleteCallback(function (results) {
                    if (results.getCurrentNumPois() <= 0) {
                        latitude = point.lat;
                        longitude = point.lng;
                        qdtype = 2;
                        sendlatitude = point.lat;
                        sendlongitude = point.lng;

                        $("#zftimediv").show();
                        $("#AbsentEndDate").attr("value", zftime);

                        $("#print3").hide();
                        $("#print4").show();
                        return;
                    } else {
                        console.log("检索到的结果" + results.getPoi(0).point.lng + "" + results.getPoi(0).point.lat);
                        latitude = results.getPoi(0).point.lat;
                        longitude = results.getPoi(0).point.lng;
                        qdtype = 2;

                        $("#zftimediv").show();
                        $("#AbsentEndDate").attr("value", zftime);

                        $("#print3").hide();
                        $("#print4").show();
                    }
                });
            } else {
                latitude = point.lat;
                longitude = point.lng;
                qdtype = 2;
                sendlatitude = point.lat;
                sendlongitude = point.lng;

                $("#zftimediv").show();
                $("#AbsentEndDate").attr("value", zftime);

                $("#print3").hide();
                $("#print4").show();
                return;
            }
        } else {
            alert("贫困户地址解析失败！签到坐标设为呼和浩特市中心,并记录为无法解析地址的日志.");
            latitude = hhhtlat;
            longitude = hhhtlng;

            sendlatitude = latitude;
            sendlongitude = longitude;

            qdtype = 3;//非现场签到并且地址未能解析
            $("#zftimediv").show();
            $("#AbsentEndDate").attr("value", zftime);

            $("#print3").hide();
            $("#print4").show();
            return;
        }
    }, p_city);
}
/**
 * 去除地址所带的委会两字(村委会，村民委员会等)
 * @param str
 * @returns {*}
 */
function qwhstr(str) {
    if ("村村民委员会" == str.substring(str.length - 6, str.length)) {
        var newstr = str.substring(0, str.length - 5);
        return newstr;
    } else if ("村民委员会" == str.substring(str.length - 5, str.length)) {
        var newstr = str.substring(0, str.length - 4);
        return newstr;
    } else if ("村村委会" == str.substring(str.length - 4, str.length)) {
        var newstr = str.substring(0, str.length - 3);
        return newstr;
    } else if ("村委会" == str.substring(str.length - 3, str.length)) {
        var newstr = str.substring(0, str.length - 2);
        return newstr;
    } else if ("委会" == str.substring(str.length - 2, str.length)) {
        var newstr = str.substring(0, str.length - 2);
        return newstr;
    } else {
        return str;
    }
}
//提交完成删除对应记录
function dellsdata(lsdate) {
    var lsdata = JSON.parse(localStorage.mzfjl);
    var potion;
    $.each(lsdata, function (i, item) {
        if (lsdate == lsdata[i].zftime) {
            potion = i;
        }
    })
    lsdata.splice(potion, 1);
    var objStr = JSON.stringify(lsdata);
    localStorage.mzfjl = objStr;
}

$(function () {
    var currYear = (new Date()).getFullYear();
    var opt = {};
    opt.date = {preset: 'date'};
    opt.datetime = {preset: 'datetime', minDate: new Date(2016, 1, 1, 1, 1), maxDate: new Date()};
    opt.time = {preset: 'time'};
    opt.default = {
        theme: 'android-ics light', //皮肤样式
        display: 'modal', //显示方式
        mode: 'scroller', //日期选择模式
        dateFormat: 'yyyy-mm-dd',
        lang: 'zh',
        showNow: true,
        nowText: "今天",
        startYear: currYear - 1, //开始年份
        endYear: currYear //结束年份
    };
    /*$("#EndDate").mobiscroll($.extend(opt['date'], opt['default']));//年月日型*/
    var optDateTime = $.extend(opt['datetime'], opt['default']);
    //var optTime = $.extend(opt['time'], opt['default']);
    $("#AbsentEndDate").mobiscroll(optDateTime).datetime(optDateTime);//年月日时分型
    /*$("#EndTime").mobiscroll(optTime).time(optTime);//时分型*/
});
/**
 * 用于非现场定位
 */
function dingewi() {
    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: 'wx4fa9e95d9af2477a', // 必填，公众号的唯一标识
        timestamp: c_time, // 必填，生成签名的时间戳
        nonceStr: sj_num, // 必填，生成签名的随机串
        signature: qianming,// 必填，签名，见附录1
        jsApiList: [
            'getNetworkType',
            'openLocation',
            'getLocation',
            'hideOptionMenu',
            'showOptionMenu',
            'closeWindow',
            'scanQRCode'
        ] // 必填，需要

    });
    wx.ready(function () {
        wx.getLocation({
            type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
            success: function (res) {
                sendlatitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                sendlongitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                var speed = res.speed; // 速度，以米/每秒计
                var accuracy = res.accuracy; // 位置精度
            }
        });
    })

}
function kstxzfjl() {
    if (zftype == 0) {
        $('#poor_type').focus();
        alert("请先选择走访类型!");
    }
}
function xzzflx() {
    zftype = $('#poor_type').val();
}
function formattime(datetime) {
    datetime = datetime.replace(/-/g, "/");
    var date = new Date(datetime);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var h = date.getHours();
    var m = date.getMinutes();
    return year + '-' + month + "-" + day + " " + h + ":" + m + ":" + "00";
}
