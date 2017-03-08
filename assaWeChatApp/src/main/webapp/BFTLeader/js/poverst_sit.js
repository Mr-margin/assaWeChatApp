/**
 * Created by Administrator on 2017/2/14.
 */

//var serviceurl = "http://192.168.1.111:8888/";
var serviceurl = "http://115.29.42.107/";
var xzqh, datetime, xzqhcode;//行政区划
var tj_title;
var poor_sum, poor_pop, rid_poor, rid_pop, general_poor, low_poor, five_poor;
var xzdata;
var cType = getUrlParam("cType");
xzqh = decodeURI(getUrlParam("xzqh"));
if (xzqh == 0) {
    xzqh = "内蒙古自治区";
}
xzqhcode = getUrlParam("code");

window.onload = function(){
    if(window.parent.seticon){
        window.parent.seticon(1);
    }else{
        window.parent.onload=function(){
            window.parent.seticon(1);
        }
    }
}


$(document).ready(function () {

    datetime = "截至当前";//默认当前时间
    //首先请求行政区划
    //首先请求行政区划
    $.ajax({
        url: serviceurl + 'assaWeChatApp/getXzqh.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {cType: 1},//默认请求内蒙古自治区
        success: function (data) {
            tj_title = "<div id='city_s' class='weui-flex__item'>";
            tj_title += "<div id='xzqh_div'  class='placeholder alowbtn'>" + xzqh + "</div></div>";
            tj_title += "<div class='vertical_parting'></div>";
            tj_title += "<div id='time_s' class='weui-flex__item'><div id='time_div' class='placeholder'>" + datetime + "</div></div>";
            $("#tj_title").html(tj_title);
            initData();//初始化数据
        },
        error: function (msg) {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
        }
    });
});
/**
 * 初始化数据
 */
function initData() {
    initHead();//初始化头部数据
    getdatafromnet();
}

/**
 * 填充数据
 */
function setData() {
    poor_sum = xzdata.qt.pkzhs;
    poor_pop = xzdata.qt.pkzrk;
    rid_poor = xzdata.qt.ytphs;
    rid_pop = xzdata.qt.ytpr;
    general_poor = xzdata.qt.ybpkh;
    low_poor = xzdata.qt.dbpkh;
    five_poor = xzdata.qt.wbpkh;

    $("#poor__sum").html(formatNum(poor_sum));
    $("#poor_pop_sum").html(formatNum(poor_pop));
    $("#rid_poor").html(formatNum(rid_poor));
    $("#rid_pop").html(formatNum(rid_pop));
    $("#general_poor").html(formatNum(general_poor));
    $("#low_poor").html(formatNum(low_poor));
    $("#five_poor").html(formatNum(five_poor));

    setBar();
}
/**
 * 设置图表
 */
function setBar() {

    //脱贫比例****************************************
    var myChart = echarts.init(document.getElementById('rid_ratio'));
    var tpbl = 0;
    if (xzdata.qt.ytphs>0){
        tpbl = (xzdata.qt.ytphs / (xzdata.qt.ytphs + xzdata.qt.pkzhs)) * 100;
    }


// 指定图表的配置项和数据
    var option = {
        title: {
            text: '脱贫比例',
            subtext: '脱贫情况',
            x: 'center',
            y: 'bottom',
            textStyle: {
                fontSize: 12,
                color: '#6f6f6f'
            },
            subtextStyle: {
                fontSize: 8
            }
        },

        series: [
            {
                name: '脱贫比例',
                type: 'pie',
                radius: ['62%', '80%'],
                center: ['50%', '40%'],
                avoidLabelOverlap: false,
                hoverAnimation: false,
                label: {
                    normal: {
                        show: true,
                        position: 'center',
                        textStyle: {
                            fontSize: '14',
                            fontWeight: 'bold'
                        }
                    },
                    emphasis: {
                        show: true,
                        textStyle: {
                            fontSize: '18',
                            fontWeight: 'bold'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data: [
                    {
                        value: xzdata.qt.ytphs, name: parseInt(tpbl) + '%', select: true,
                        itemStyle: {
                            normal: {
                                color: 'rgb(19,139,249)'
                            }
                        },
                    },
                    {
                        value: xzdata.qt.pkzhs, name: '',
                        itemStyle: {
                            normal: {
                                color: 'rgb(206,232,255)'
                            }
                        },
                    }
                ]
            }
        ]
    };

// 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);

    //致贫原因****************************************
    var myBarChart = echarts.init(document.getElementById('cause_bar'));
    var yaxisdata = new Array();
    var zpdata = new Array();
    var p = 0;

    for (var tmp in xzdata.zpyy) {
        yaxisdata.push(tmp);
        zpdata[p] = xzdata.zpyy[tmp];
        p++;
    }
    option1 = {
        title: {
            text: '主要致贫原因（顺位前四）',
            textStyle: {
                color: '#6f6f6f',
                fontFamily: '黑体',
                fontSize: '15px'
            },
            padding: 25
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            show: false
        },
        grid: {
            left: '3%',
            right: '10%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            show: false,
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: yaxisdata.reverse()
        },
        itemStyle: {
            normal: {
                color: 'rgb(19,139,249)'
            }
        },
        series: [
            {
                name: '贫困户数',
                type: 'bar',
                label: {
                    normal: {
                        show: true,
                        position: 'right',
                        /*formatter: '{c}户'*/
                        formatter:function(data){
                            return formatNum(data.value)+"户";
                        }
                    }
                },
                data: [
                    {
                        value: zpdata[3]

                    },
                    {
                        value: zpdata[2]
                    }
                    ,
                    {
                        value: zpdata[1]
                    }
                    ,
                    {
                        value: zpdata[0],
                        itemStyle: {
                            normal: {
                                color: 'rgb(249,25,46)'
                            }
                        }

                    }
                ]

            }
        ]
    };
    myBarChart.setOption(option1);
    $("#loadingToast").fadeOut(600);
}
/**
 * 从服务器读取数据
 */
function getdatafromnet() {
    $.ajax({
        url: serviceurl + 'assaWeChatApp/getFpxz.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {cType: cType, code: xzqhcode},
        success: function (data) {
            xzdata = data;
            setData();
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
/**
 * 初始化头部数据
 */
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
}
/*******************************************头部初始化结束*******************************************************/




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
};

function formatNum(nStr) {
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
}
function rearrangement(arr){
    var centernum = parseInt(arr.length/2);
    var temp = arr[0];
    arr[0] = arr[centernum];
    arr[centernum] = temp;
    return arr;
}