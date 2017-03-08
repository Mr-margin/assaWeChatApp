/**
 * Created by Administrator on 2017/2/9.
 */

//var serviceurl = "http://192.168.1.111:8888/";
var serviceurl = "http://115.29.42.107/";
var xzqh, datetime, xzqhcode;//行政区划
var tj_title;
var rjhtml = "";
var zfdata;
var cType = getUrlParam("cType");
xzqh = decodeURI(getUrlParam("xzqh"));
if (xzqh == 0) {
    xzqh = "内蒙古自治区";
}
xzqhcode = getUrlParam("code");
var stadate, enddate;
stadate = "2014-1-1";
enddate = "2017-12-31";
var mydate = new Date();
enddate = [mydate.getFullYear(), mydate.getMonth() + 1, mydate.getDate()].join('-');
var isEnd = false;//是否结束
var counter = 1;
/*页数计数器*/
var isloading = false;//正在加载更多
var rjoption = 0;//日记条数计数器
var isxsxq = false;//正在显示详情
var iscxbfr = false;//是否来至查询的帮扶人
var iscxdx = false;//是否是要查询帮扶人

window.onload = function(){
    if(window.parent.seticon){
        window.parent.seticon(2);
    }else{
        window.parent.onload=function(){
            window.parent.seticon(2);
        }
    }
}


$(document).ready(function () {


    datetime = "截至当前";
    $.ajax({
        url: serviceurl + 'assaWeChatApp/getXzqh.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {cType: 1},
        success: function (data) {
            tj_title = "<div id='city_s' class='weui-flex__item'>";
            tj_title += "<div id='xzqh_div'  class='placeholder alowbtn'>" + xzqh + "</div></div>";
            tj_title += "<div class='vertical_parting'></div>";
            tj_title += "<div id='time_s' class='weui-flex__item'><div id='time_div' class='placeholder alowbtn'>" + datetime + "</div></div>";
            $("#tj_title").html(tj_title);
            initData();//初始化数据
        },
        error: function (msg) {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
        }
    });
});

function initData() {
    initHead();
    getdatafromnet();
    /*监听加载更多*/
    $("#jzgd").click(function () {
        if(isxsxq == true){
            return;
        }
        if(isEnd == true){

            return;
        }
        if (isloading == true){
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("正在加载" );
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
            return;
        }
        $("#jzgd").addClass("weui-btn_loading");
        $("#jzgd").html("<i class='weui-loading'></i>"+"加载中...");
        counter ++;
        isloading = true;
        $("#loadingToast").fadeIn(100);
        setTimeout(function () {
                $("#loadingToast").fadeOut(600);
                if (iscxdx == true){
                    start_search();
                }else {
                    getdatafromnet();
                }

                window.parent.code = xzqhcode;
            }
            , 300);
    });
    /*$(window).bind("scroll",function(){

     if(isxsxq == true){
     return;
     }
     if(isEnd == true){
     return;
     }
     if (isloading == true){
     return;
     }
     if ($(document).scrollTop() >= $(document).height() - $(window).height()) {
     alert("滚动条已经到达底部为" + $(document).scrollTop());
     }
     // 当滚动到最底部以上100像素时， 加载新内容
     // 核心代码
     if ($(document).height() - $(this).scrollTop() - $(this).height()<100){
     counter ++;
     isloading = true;
     $("#loadingToast").fadeIn(100);
     setTimeout(function () {
     $("#loadingToast").fadeOut(600);
     getdatafromnet();
     window.parent.code = xzqhcode;
     }
     , 300);
     }
     });*/
}

function getdatafromnet() {
    /*请求日记列表*/
    $.ajax({
        url: serviceurl + 'assaWeChatApp/getRjll.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {cType: cType, code: xzqhcode, pageNum: counter, stime: stadate, etime: enddate},
        success: function (data) {
            console.log(data);
            initRJcells(data);
            if (counter == 1) {
                zfdata = data;
            } else {
                zfdata = zfdata.concat(data);
            }

        },
        error: function (msg) {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
        }
    });
}

function initRJcells(data) {
    if (data.length < 20) {
        $("#jzgd").hide();
        $("#weuitips").html("没有更多了");
        isEnd = true;
    } else {
        $("#jzgd").show();
        $("#weuitips").html("暂无数据");
        isEnd = false;
    }
    if (iscxbfr == true) {
        rjoption = 0;
        rjhtml = "";
        $("#rjcells").html("");
    }
    rjhtml = "";
    $.each(data, function (i, item) {
        var datetime = "未填写";
        var bfrname = "未填写";
        var pkhname = "未填写";
        datetime = (item.time != undefined) ? formatDate(item.time) : "未填写";
        bfrname = (item.name != undefined) ? item.name : "未填写";
        pkhname = (item.hname != undefined) ? item.hname : "未填写";
        rjhtml += "<a  class='weui-cell weui-cell_access alink' name=" + rjoption + ">";
        rjoption++;
        rjhtml += "<div class='weui-cell__hd'>";
        rjhtml += "<img src='images/dp.png' alt='' style='width:20px;margin-right:5px;display:block'>";
        rjhtml += "</div><div class='weui-cell__bd'>";
        rjhtml += "<p>" + bfrname + "走访" + pkhname + "</p>";
        rjhtml += " </div> <div class='weui-cell__ft'>";
        rjhtml += datetime;
        rjhtml += "</div></a>";
    })
    $("#rjcells").append(rjhtml);
    $("#loadingToast").fadeOut(600);
    $("a.alink").click(lookdetail);
    $("#jzgd").removeClass("weui-btn_loading");
    $("#jzgd").html("加载更多");
    isloading = false;
}


function lookdetail() {
    var option = this.name;
    if (checkMobile(option)) {
        search_text = option;
        counter = 1;
        start_search();
    } else if (option != undefined && option < 1000) {

        console.log(zfdata[option].name);

        var bfrname = (zfdata[option].name != undefined) ? zfdata[option].name : "未填写";
        var pkhname = (zfdata[option].hname != undefined) ? zfdata[option].hname : "未填写";
        var zfsj = (zfdata[option].time != undefined) ? zfdata[option].time : "未填写";
        var zfjl = (zfdata[option].content != undefined) ? zfdata[option].content : "未填写";
        var detailhtml = "<div class='weui-flex'><div class='weui-flex__item'><div class='placeholder'> 走访详情</div> </div></div>";
        detailhtml += "<div style='height:100%;line-height: 100%;padding-top: 2em'><div style='width: 100%;overflow: auto'>";
        detailhtml += "<div class='zftp'><img width='50%' src='http://www.gistone.cn" + zfdata[option].pic + "'></div>";
        detailhtml += "<div class='weui-flex'><div class='weui-flex__item'><div class='detailtext' style='text-align: left;padding-left: 4em'>帮扶人：" + bfrname + "</div> </div></div>";
        detailhtml += "<div class='weui-flex'><div class='weui-flex__item'><div class='detailtext' style='text-align: left;padding-left: 4em'>贫困户：" + pkhname + "</div> </div></div>";
        detailhtml += "<div class='weui-flex'><div class='weui-flex__item'><div class='detailtext' style='text-align: left;padding-left: 4em'>走访时间：" + zfsj + "</div> </div></div>";
        detailhtml += "<div class='weui-flex'><div class='weui-flex__item'><div class='detailtext' style='text-align: left;padding-left: 4em;width: 70%;_height:200px; min-height:200px;' >走访记录：" + zfjl + "</div> </div></div>";

        detailhtml += "</div></div>";
        detailhtml += "<div class='left_black'><img src='images/black.png' style='text-align: center' height='100%'></div>";
        $("#zfdetail").html(detailhtml);
        $("#zfdetail").css("display", "block");
        $("div.left_black").click(function () {
            $("#zfdetail").css("display", "none");
            isxsxq = false;
        });
        isxsxq = true;
    } else {
        $("#tooltips_div").css("display", "block");
        $("#tooltips_div").html("帮扶人信息有误，请查证!");
        setTimeout(function () {
            $("#tooltips_div").css("display", "none");
        }, 2000);
    }
}

function initHead() {//初始化头部选择器
    var ms_data = new Array();//盟市列表
    var qx_data = new Array();//旗县列表
    var iszk = false;//是否展开了
    var s_ms = "选择盟市";//选择的盟市名称
    var s_qx = "选择旗县";//选择的旗县名称
    $("#selector_head .placeholder").height(0);//初始高度
    var ms_data_net = new Array();//得到盟市的源数据
    /*请求盟市列表*/
    $.ajax({
        url: serviceurl + 'assaWeChatApp/getXzqh.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {cType: 2},
        success: function (data) {
            ms_data_net = data;
            ms_data[0] = {
                label: "全区",
                value: "全区"
            }
            $.each(data, function (i, item) {
                ms_data[i + 1] = {
                    label: item.name,
                    value: item.name
                };
            });
            ms_data = rearrangement(ms_data);
            initXzqh();
            initDateTime();
        },
        error: function (msg) {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
        }
    });
    /********************************************************/
    /*初始化行政区划选择器*/
    function initXzqh() {
        $("#city_s").click(function () {
            if (!iszk) {
                $("#selector_head .placeholder").animate({"height": "2.3em",}, 500)
                $("#select_ms").html(s_ms);
                $("#select_qx").html(s_qx);
                iszk = true;
                $("#select_ms").click(select__ms);
                $("#select_qx").click(select__qx);
                /*$("#selector_head").css({
                 "position":"relative",
                 "animation":"show_selector 2s  forwards",
                 "-webkit-animation":"show_selector 2s forwards"
                 });*/
                $("#xzqh_div").removeClass("alowbtn");
                $("#xzqh_div").addClass("upward");
            } else {
                $("#loadingToast").fadeIn(100);
                $("#select_ms").html("");
                $("#select_qx").html("");
                $("#selector_head .placeholder").animate({"height": "0"}, 300);
                $("#xzqh_div").removeClass("upward");
                $("#xzqh_div").addClass("alowbtn");
                if (s_qx != "选择旗县") {
                    $("#xzqh_div").html(s_ms + s_qx);
                    window.parent.ctype = 3;
                    window.parent.xzqh = s_ms + s_qx;
                    cType = 3;
                } else if (s_ms != "选择盟市") {
                    $("#xzqh_div").html(s_ms);
                    window.parent.xzqh = s_ms;
                    window.parent.ctype = 2;
                    cType = 2;
                } else {
                    $("#xzqh_div").html("内蒙古自治区");
                    window.parent.xzqh = "内蒙古自治区";
                    window.parent.ctype = 1;
                    cType = 1;
                }
                iszk = false;
                $("#loadingToast").fadeIn(100);
                setTimeout(function () {
                        $("#loadingToast").fadeOut(600);
                        counter = 1;
                        iscxdx = false;
                        iscxbfr = false;
                        rjoption = 0;
                        rjhtml = "";
                        $("#rjcells").html("");
                        getdatafromnet();
                        window.parent.code = xzqhcode;
                    }
                    , 300);

            }
        });
    }

    var qxcodes = new Array();
    /********************************************************/
    //选择旗县
    function select__qx() {
        if (s_ms == "选择盟市") {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("请先选择盟市");
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
            return;
        }

        /*请求旗县列表*/
        $.ajax({
            url: serviceurl + 'assaWeChatApp/getXzqh.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {cType: 3, code: xzqhcode},
            success: function (data) {
                qx_data[0] = {
                    label: "全市",
                    value: "全市"
                }
                $.each(data, function (i, item) {
                    qx_data[i + 1] = {
                        label: item.name,
                        value: item.name
                    };
                    qxcodes[i] = {
                        qxname: item.name,
                        qxcode: item.code
                    };
                });
                qx_data = rearrangement(qx_data);
                weui.picker(
                    qx_data,
                    {
                        onChange: function (result) {
                        },

                        onConfirm: function (result) {
                            $("#select_qx").html((result == "全市") ? "选择旗县" : result);
                            s_qx = (result == "全市") ? "选择旗县" : result;
                            $.each(qxcodes, function (i, item) {
                                if (s_qx = item.qxname) {
                                    xzqhcode = item.qxcode;
                                }
                            });
                        }
                    });

            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
    }

    /*************************************/
    //选择盟市
    function select__ms() {
        var picker = weui.picker(
            ms_data,
            {
                onChange: function (result) {
                },
                onConfirm: function (result) {
                    $("#select_ms").html((result == "全区") ? "选择盟市" : result);
                    s_ms = (result == "全区") ? "选择盟市" : result;
                    if (result == "全区") {
                        $("#select_qx").html("选择旗县");
                        s_qx = "选择旗县";
                    }
                    $.each(ms_data_net, function (i, item) {
                        if (s_ms == item.name) {
                            xzqhcode = item.code;
                        }
                    });
                }
            });
    }

    /*****************************************/
    //选择时间段
    function initDateTime() {
        var isdt = false;//时间选择是否展开
        var s_year = "年";
        var s_month = "月";
        var dqyear = new Date().getFullYear();
        var dqmonth = new Date().getMonth();
        var dqday = new Date().getDate;
        $("#time_s").click(function () {
            if (!isdt) {
                $("#selector_time .placeholder").animate({"height": "2.3em",}, 500)
                $("#select_year").html("年");
                $("#select_month").html("月");
                isdt = true;
                $("#select_year").click(select_year);
                $("#select_month").click(select_month);
                $("#time_div").removeClass("alowbtn");
                $("#time_div").addClass("upward");

            } else {
                $("#select_year").html("");
                $("#select_month").html("");
                $("#selector_time .placeholder").animate({"height": "0"}, 300);
                $("#time_div").removeClass("upward");
                $("#time_div").addClass("alowbtn");
                if (s_year == '年') {
                    $("#time_div").html("截至当前");
                } else if (s_month == '月') {
                    $("#time_div").html(s_year + "年");
                    stadate = s_year + "-1-1";
                    enddate = s_year + "-12-31";
                } else {
                    $("#time_div").html(s_year + "年" + s_month + "月");
                    stadate = s_year + "-" + s_month + "-1";
                    enddate = s_year + "-" + s_month + "-" + getMonthDays(s_year, s_month - 1);
                }
                $("#loadingToast").fadeIn(100);
                setTimeout(function () {
                        $("#loadingToast").fadeOut(600);
                        counter = 1;
                        iscxdx = false;
                        iscxbfr = false;
                        rjoption = 0;
                        rjhtml = "";
                        $("#rjcells").html("");
                        getdatafromnet();
                        window.parent.code = xzqhcode;
                    }
                    , 300);
                isdt = false;
            }
        });

        function select_year() {
            var dqyear = new Date().getFullYear();
            var year_data = new Array();
            year_data.push({
                label: "截止当前",
                value: "截止当前"
            });
            for (var i = 2014; i <= dqyear; i++) {
                year_data.push({
                    label: i,
                    value: i
                });
            }
            weui.picker(
                year_data,
                {
                    onChange: function (result) {
                    },
                    onConfirm: function (result) {
                        if (result == "截止当前") {
                            $("#select_year").html("年");
                            s_year = "年";
                        } else {
                            $("#select_year").html(result + "年");
                            s_year = result;
                        }

                    }
                });
        }

        function select_month() {

            if (s_year == "年") {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("请先选择年份");
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
                return;
            }
            var month_data = new Array();
            month_data.push({
                label: "全年",
                value: "全年"
            });
            if (s_year == new Date().getFullYear()) {
                for (var i = 1; i <= new Date().getMonth(); i++) {
                    month_data.push({
                        label: i,
                        value: i
                    });
                }
            } else {
                for (var i = 1; i <= 12; i++) {
                    month_data.push({
                        label: i,
                        value: i
                    });
                }
            }

            weui.picker(
                month_data,
                {
                    onChange: function (result) {
                    },
                    onConfirm: function (result) {
                        if (result == "全年") {
                            $("#select_month").html("月");
                            s_month = "月";
                        }
                        $("#select_month").html(result + "月");
                        s_month = result;
                    }
                });
        }
    }
}

var search_text;
/**
 * 搜索帮扶人
 */
$(function () {
    var $searchBar = $('#searchBar'),
        $searchResult = $('#searchResult'),
        $searchText = $('#searchText'),
        $searchInput = $('#searchInput'),
        $searchClear = $('#searchClear'),
        $searchCancel = $('#searchCancel');

    function hideSearchResult() {
        $searchResult.hide();
        $searchInput.val('');
    }

    function cancelSearch() {
        hideSearchResult();
        $searchBar.removeClass('weui-search-bar_focusing');
        $searchText.show();
    }

    $searchText.on('click', function () {
        $searchBar.addClass('weui-search-bar_focusing');
        $searchInput.focus();
    });
    $searchInput
        .on('blur', function () {
            if (!this.value.length) cancelSearch();
        })
        .on('input', function () {
            if (this.value.length) {
                $searchResult.show();
                search_text = this.value;
            } else {
                $searchResult.hide();
            }
        })
    ;
    $searchClear.on('click', function () {
        hideSearchResult();
        $searchInput.focus();
    });
    $searchCancel.on('click', function () {
        cancelSearch();
        $searchInput.blur();
    });
});

function start_search() {
    if (checkNum(search_text)) {
        if (checkMobile(search_text)) {//如果输入的是手机号
            var mydate = new Date();
            var dqdate = [mydate.getFullYear(), mydate.getMonth() + 1, mydate.getDate()].join('-');
            console.log(dqdate);
            /*请求日记列表*/
            $.ajax({
                url: serviceurl + 'assaWeChatApp/getRjll.do',
                type: 'POST',
                async: false,
                dataType: 'json',
                data: {cType: 1, code: xzqhcode, pageNum: counter, phone: search_text},
                success: function (data) {
                    if (data == null || data.length <= 0 || data == "") {
                        $("#jzgd").hide();
                        $("#weuitips").html("没有更多了");
                        isEnd = true;
                        $("#tooltips_div").css("display", "block");
                        $("#tooltips_div").html("查询的对象没有日志记录");
                        setTimeout(function () {
                            $("#tooltips_div").css("display", "none");
                        }, 2000);
                        return;
                    }
                    if (data.length < 20) {
                        $("#jzgd").hide();
                        $("#weuitips").html("没有更多了");
                        isEnd = true;
                    } else {
                        $("#jzgd").show();
                        $("#weuitips").html("暂无数据");
                        isEnd = false;
                    }

                    $("#jzgd").hide();
                    rjhtml = "";
                    rjoption = 0;
                    if (counter == 1) {
                        zfdata = data;
                    } else {
                        zfdata = zfdata.concat(data);
                    }
                    initRJcells(data);
                    iscxdx = true;
                    isEnd = false;
                },
                error: function (msg) {
                    $("#tooltips_div").css("display", "block");
                    $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                    setTimeout(function () {
                        $("#tooltips_div").css("display", "none");
                    }, 2000);
                }
            });
        } else {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("请输入正确的手机号码");
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
        }
    } else {//查询姓名再选择号码
        counter = 1;
        $.ajax({
            url: serviceurl + 'assaWeChatApp/getRjllByName.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: search_text},
            success: function (data) {
                if (data == null || data.lenth <= 0 || data == "") {
                    $("#tooltips_div").css("display", "block");
                    $("#tooltips_div").html("找不到" + search_text);
                    setTimeout(function () {
                        $("#tooltips_div").css("display", "none");
                    }, 2000);
                } else {
                    rjhtml = "";
                    $.each(data, function (i, item) {
                        var bfrname = (item.name != undefined) ? item.name : "未填写";
                        var danwei = (item.dep != undefined) ? item.dep : "未填写";
                        var sjh = (item.phone != undefined) ? item.phone : "未填写";
                        rjhtml += "<a  class='weui-cell weui-cell_access alink' name=" + item.phone + ">";
                        rjhtml += "<div class='weui-cell__hd'>";
                        rjhtml += "<img src='images/bfr.png' alt='' style='width:20px;margin-right:5px;display:block'>";
                        rjhtml += "</div><div class='weui-cell__bd'>";
                        rjhtml += "<p>" + bfrname + "(单位：" + danwei + ")</p>";
                        rjhtml += " </div> <div class='weui-cell__ft'>手机号：";
                        rjhtml += sjh;
                        rjhtml += "</div></a>";
                    })
                    iscxbfr = true;
                    $("#rjcells").html(rjhtml);
                    $("a.alink").click(lookdetail);
                    $("#jzgd").hide();
                    $("#weuitips").html("没有更多了");
                    isEnd = true;
                }
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
    }
}
/**
 * 是不是手机号
 * @param s
 * @returns {boolean}
 */
function checkMobile(s) {
    var length = s.length;
    if (length == 11 && /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(14[0-9]{1})|)+\d{8})$/.test(s)) {
        return true;
    } else {
        return false;
    }
}
/**
 * 是不是数字
 * @param obj
 */
function checkNum(obj) {
    var re = /^[0-9]+.?[0-9]*$/; //判断字符串是否为数字 //判断正整数 /^[1-9]+[0-9]*]*$/
    if (!re.test(obj)) {
        return false;
    } else {
        return true;
    }
}
function getUrlParam(name) {
    var url = location.search;
    var url = document.URL.toString();
    var tmpStr = name + "=";
    var tmp_reg = eval("/[\?&]" + tmpStr + "/i");
    if (url.search(tmp_reg) == -1) return null;
    else {
        var a = url.split(/[\?&]/);
        for (var i = 0; i < a.length; i++)
            if (a[i].search(eval("/^" + tmpStr + "/i")) != -1)return a[i].substring(tmpStr.length);
    }
}
function rearrangement(arr) {
    var centernum = parseInt(arr.length / 2);
    var temp = arr[0];
    arr[0] = arr[centernum];
    arr[centernum] = temp;
    return arr;
}
function formatDate(datetime) {
    datetime = datetime.replace(/-/g, "/");
    var newdate = new Date(datetime);
    return newdate.getFullYear() + "-" + (newdate.getMonth()+1) + "-" + newdate.getDate();
}
/**
 * 判断是否是闰年
 * @param year
 * @returns {boolean}
 */
function isLeapYear(year) {
    return (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0);
}
/**
 * 计算某月有几天
 * @param year
 * @param month
 * @returns {*|number}
 */
function getMonthDays(year, month) {
    return [31, null, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month] ||
        (isLeapYear(year) ? 29 : 28);
}