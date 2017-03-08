/**
 * Created by Administrator on 2017/2/9.
 */
//var serviceurl = "http://192.168.1.111:8888/";
var serviceurl = "http://115.29.42.107/";

var xzqh, datetime, xzqhcode;//行政区划
var tj_title;
var poor_sum, cadre_sum, assist_coverage, diary_sum, d_poor_sum,
    d_cadre_sum, d_poor_coverage, d_cadre_proportion, day_sum, week_sum, month_sum;
var tjdata = null;//请求的统计数据
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


window.onload = function(){
    if(window.parent.seticon){
        window.parent.seticon(0);
    }else{
        window.parent.onload=function(){
            window.parent.seticon(0);
        }
    }
}


/***********************************页面元素加载完毕开始加载数据*****************************************************/
$(document).ready(function () {

    datetime = "截至当前";//默认当前时间
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
            tj_title += "<div id='time_s' class='weui-flex__item'><div id='time_div' class='placeholder alowbtn'>" + datetime + "</div></div>";
            $("#tj_title").html(tj_title);
            initData();//初始化数据
        },
        error: function (msg) {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
        }
    });
});
/******************************************加载函数结束*************************/

/**
 * 从服务器读取数据
 */
function getdatafromnet() {
    $.ajax({
        url: serviceurl + 'assaWeChatApp/getTjSum.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {cType: cType, code: xzqhcode, stime: stadate, etime: enddate},
        success: function (data) {
            console.log(data)
            tjdata = data;
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
 * 填充数据
 */
function setData() {
    if (tjdata == null) {
        $("#tooltips_div").css("display", "block");
        $("#tooltips_div").html("数据异常");
        setTimeout(function () {
            $("#tooltips_div").css("display", "none");
        }, 2000);
        return;
    } else {
        poor_sum = formatNum(tjdata[0].poor_sum);
        cadre_sum = formatNum(tjdata[0].cadre_sum);
        diary_sum = formatNum(tjdata[0].diary_sum);
        d_poor_sum = formatNum(tjdata[0].d_poor_sum);
        d_cadre_sum = formatNum(tjdata[0].d_cadre_sum);
        day_sum = formatNum(tjdata[0].day_sum);
        week_sum = formatNum(tjdata[0].week_sum);
        month_sum = formatNum(tjdata[0].month_sum);
        assist_coverage = parseInt(tjdata[0].assist_coverage * 100);
        d_poor_coverage = parseInt(tjdata[0].d_poor_coverage * 100);
        d_cadre_proportion = parseInt(tjdata[0].d_cadre_proportion * 100);

        $("#poor__sum").html(poor_sum);
        $("#cadre__sum").html(cadre_sum);
        $("#diary__sum").html(diary_sum);
        $("#d_poor__sum").html(d_poor_sum);
        $("#d_cadre__sum").html(d_cadre_sum);
        $("#day__sum").html(day_sum);
        $("#week__sum").html(week_sum);
        $("#month__sum").html(month_sum);

        setTB();//设置图表数据
    }
}

/**
 * 设置图表
 */
function setTB() {
    /**********************结对情况环形图******************************/
    var myChart = echarts.init(document.getElementById('assist_coverage'));

// 指定图表的配置项和数据
    var option = {
        title: {
            text: '结对情况',
            subtext: '帮扶覆盖率',
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
                name: '结对情况',
                type: 'pie',
                radius: ['52%', '70%'],
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
                        value: assist_coverage, name: assist_coverage + '%', select: true,
                        itemStyle: {
                            normal: {
                                color: 'rgb(19,139,249)'
                            }
                        },
                    },
                    {
                        value: 100 - assist_coverage, name: '',
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
    /*****************************************************************************************/

    /**********************走访日记相关贫困户数环形图******************************/
    var myChart = echarts.init(document.getElementById('d_poor__coverage'));

// 指定图表的配置项和数据
    var option = {
        title: {
            text: '帮扶贫困户占总数比',
            subtext: '走访覆盖率',
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
                name: '',
                type: 'pie',
                radius: ['42%', '60%'],
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
                        value: d_poor_coverage, name: d_poor_coverage + '%', select: true,
                        itemStyle: {
                            normal: {
                                color: 'rgb(19,139,249)'
                            }
                        },
                    },
                    {
                        value: 100 - d_poor_coverage, name: '',
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
    /*****************************************************************************************/

    /**********************走访日记相关帮扶人数环形图******************************/
    var myChart = echarts.init(document.getElementById('d_cadre__coverage'));

// 指定图表的配置项和数据
    var option = {
        title: {
            text: '帮扶责任人占总数比',
            subtext: '走访覆盖率',
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
                name: '',
                type: 'pie',
                radius: ['42%', '60%'],
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
                        value: d_cadre_proportion, name: d_cadre_proportion + '%', select: true,
                        itemStyle: {
                            normal: {
                                color: 'rgb(19,139,249)'
                            }
                        },
                    },
                    {
                        value: 100 - d_cadre_proportion, name: '',
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
    /*****************************************************************************************/
    $("#loadingToast").fadeOut(600);
}
/**
 * 初始化数据
 */
function initData() {
    initHead();//初始化头部数据
    getdatafromnet();
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
                    $("#time_div").html(s_year + "年" );
                    stadate = s_year + "-1-1";
                    enddate = s_year + "-12-31";
                } else {
                    $("#time_div").html(s_year + "年" + s_month + "月");
                    stadate = s_year + "-" + s_month + "-1";
                    enddate = s_year + "-" + s_month + "-"+getMonthDays(s_year,s_month-1);
                }
                $("#loadingToast").fadeIn(100);
                setTimeout(function () {
                        $("#loadingToast").fadeOut(600);
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
                label:"截止当前",
                value:"截止当前"
            });
            for (var i = 2014 ; i <= dqyear;i++){
                year_data.push({
                    label:i,
                    value:i
                });
            }
            weui.picker(
                year_data,
                {
                    onChange: function (result) {
                    },
                    onConfirm: function (result) {
                        if (result == "截止当前"){
                            $("#select_year").html("年");
                            s_year = "年";
                        }else {
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
                label:"全年",
                value:"全年"
            });
            if (s_year == new Date().getFullYear()){
                for (var i = 1; i <= new Date().getMonth(); i++) {
                    month_data.push({
                        label:i,
                        value:i
                    });
                }
            }else {
                for (var i = 1; i <= 12; i++) {
                    month_data.push({
                        label:i,
                        value:i
                    });
                }
            }

            weui.picker(
                month_data,
                {
                    onChange: function (result) {
                    },
                    onConfirm: function (result) {
                        if(result == "全年"){
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
/*******************************************头部初始化结束*******************************************************/
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
function rearrangement(arr) {
    var centernum = parseInt(arr.length / 2);
    var temp = arr[0];
    arr[0] = arr[centernum];
    arr[centernum] = temp;
    return arr;
}