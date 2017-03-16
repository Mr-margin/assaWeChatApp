/**
 * Created by 卜海波 on 2017/3/8.
 * 帮扶通领导版主界面
 */
/**
 * 主界面变量
 * @type {number}
 */
var option_tabbar = 0;//底部导航索引
var iszk = false;//九宫格菜单是否展开
var ismove = true;//移动判断(可拖动的菜单开过按钮)
var xzqhcode = 150000000000;//行政区划编号
var xzqhname = "内蒙古自治区";//要查询的行政区划名称
var xzqh = "内蒙古自治区";//顶部显示的行政地区名
var cType = 1;//请求地区级别
var serviceurl = "http://115.29.42.107/";//服务器地址
//var serviceurl = "http://192.168.2.118:8888/";
/*var stadate = "2014-1-1", enddate = "2017-12-31";//查询起止时间*/
var rjtype = 0;
/**
 * 扶贫对象变量
 */
var fpdxdata = new Array();//扶贫对象页面数据
var zpyytop, nlfztop, jkzktop, whcdtop, tdzytop, scshtop, sljytop, pkfsltop;//扶贫对象各指标位置

/**
 * 扶贫主体变量
 */
var fpztdata = new Array();//扶贫主体页面数据
var isEnd = false;//是否结束(日记翻页)
var counter = 1;//请求页码
/*页数计数器*/
var rjoption = 0;//日记条数计数器
var isxsxq = false;//正在显示详情
var iscxbfr = false;//是否来至查询的帮扶人
var iscxdx = false;//是否是要查询帮扶人
var search_text;//查询关键字
var isrjlb = false;//正在显示日记
/**
 * 帮扶措施变量
 */


/**
 * 帮扶成效变量
 */


/**************************************************Home(主界面操作部分)**************************************************************************************/
//文档加载完毕执行JS
$(document).ready(function () {
    zpyytop = $("#zpyys").offset().top - 100;
    nlfztop = $("#nlfzs").offset().top - 100;
    jkzktop = $("#jkzks").offset().top - 100;
    whcdtop = $("#whcds").offset().top - 100;
    tdzytop = $("#tdzys").offset().top - 100;
    scshtop = $("#scshtjs").offset().top - 100;
    sljytop = $("#sljys").offset().top - 100;
    pkfsltop = $("#pkfsls").offset().top - 100;

    initxzqh();//初始化要查询的地区
    initpage(option_tabbar);//初始化页面
    /**
     * 底部导航点击切换页面与自身样式
     */
    $(".weui-tabbar__item").click(function () {
        $('#cir').show();
        isrjlb = false;
        if (!$(this).is(".weui-bar__item_on")) {//点击的按钮非正在选择状态
            $(".weui-tabbar__item").removeClass("weui-bar__item_on")//清除所有选择状态的按钮
            $(this).addClass("weui-bar__item_on");//设置点击的按钮为选择状态
        }
        option_tabbar = $(this).attr("name");//获取按钮的名称（页面的索引）
        $(".weui-tab__panel").children().hide();//隐藏所有子页面
        $(".weui-tab__panel").children().eq(option_tabbar).show();//显示对应的子页面
        initpage(option_tabbar);//初始所选择的页面
        initjgg(option_tabbar);//初始化菜单
    });
    /**
     * 初始化行政区划选择器
     */
    function initxzqh() {
        $("#xzms").empty();
        $.ajax({
            url: serviceurl + 'assaWeChatApp/getXzqh.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {cType: 2},
            success: function (data) {
                $("#xzms").append("<option value='1'></option>");
                $.each(data, function (i, item) {
                    $("#xzms").append("<option value='" + item.code + "'>" + item.name + "</option>"); //为Select追加一个Option(下拉项)
                });
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("请求盟市列表失败，服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 3000);
            }
        });
    }

    $('#ggdq').click(xzdq);
});

/**
 * 监听盟市设置旗县
 */
function setqx() {
    if ($("#xzms").val() != 1) {
        cType = 2;
        $("#xzqx").empty();
        xzqhcode = $("#xzms").val();
        /*请求旗县列表*/
        $.ajax({
            url: serviceurl + 'assaWeChatApp/getXzqh.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {cType: 3, code: xzqhcode},
            success: function (data) {
                console.log(data);
                $("#xzqx").append("<option value='1'></option>");
                $.each(data, function (i, item) {
                    $("#xzqx").append("<option value='" + item.code + "'>" + item.name + "</option>"); //为Select追加一个Option(下拉项)
                });
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("请求旗县列表失败,服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
    } else {//没有选择盟市
        $("#xzqx").empty();
        cType = 1;
        xzqhcode = 150000000000;
    }
}
/**
 * 选择地区
 */
function xzdq() {
    $('.weui-mask').show();
    $('#xzqhxz').show();
    $('.weui-mask').animate({opacity: '1'}, 500);
    $('#xzqhxz').animate({width: '80%'}, 500);
}
/**
 * 重置地区
 */
function dqresetting() {
    $("#xzms").val("1");
    $("#xzqx").val("1");
    cType = 1;
    xzqhcode = 150000000000;
    xzqhname = "内蒙古自治区"
}
/**
 * 提交地区
 */
function submitdq() {
    $('.weui-mask').css("opacity", "0");
    $('#xzqhxz').css("width", "0");
    $('.weui-mask').hide();
    $('#xzqhxz').hide();
    var ms = $("#xzms").find("option:selected").text();
    var qx = $("#xzqx").find("option:selected").text();
    xzqh = ms + qx;
    $("#xzqh").html(xzqh);
    if (qx == "") {
        xzqhname = ms;
    } else {
        xzqhname = qx;
    }
    if (xzqh == "" || xzqh == null) {
        $("#xzqh").html("内蒙古自治区");
        xzqhname = "内蒙古自治区";
    }
    if (isrjlb) {
        counter = 1;
        rjoption = 0;
        $("#rjcells").html("");
        $('#loadingToast').fadeIn(80);
        setTimeout(function(){
            getrjdata();
        },100);
        return;
    }
    initpage(option_tabbar);
}
/**
 * 设置旗县编码(监听旗县选择器)
 */
function setqxcode() {
    if ($("#xzqx").val() != 1) {
        cType = 3;
        xzqhcode = $("#xzqx").val();
    } else {
        cType = 2;
    }
}
/*监听可拖动的按钮（九宫格开关）*/
var _x_start, _y_start, _x_move, _y_move, _x_end, _y_end, left_start, top_start;

document.getElementById("cir").addEventListener("touchstart", function (e) {//手指按下事件
    _x_start = e.touches[0].pageX;
    _y_start = e.touches[0].pageY;
    left_start = $("#cir").css("left");
    top_start = $("#cir").css("top");
})
document.getElementById("cir").addEventListener("touchmove", function (e) {//手指滑动事件
    _x_move = e.touches[0].pageX;
    _y_move = e.touches[0].pageY;
    $("#cir").css("left", parseFloat(_x_move) - parseFloat(_x_start) + parseFloat(left_start) + "px");
    $("#cir").css("top", parseFloat(_y_move) - parseFloat(_y_start) + parseFloat(top_start) + "px");
    ismove = false;
})
document.getElementById("cir").addEventListener("touchend", function (e) {//手指离开事件
    var _x_end = e.changedTouches[0].pageX;
    var _y_end = e.changedTouches[0].pageY;
    if (ismove) {//没有移动
        if (iszk) {
            //$('#cir').html('+');
            $('#anzk').removeClass('andhgb');
            $('#anzk').removeClass('andhzk');
            $('#anzk').addClass('andhgb');
            $('.weui-mask').animate({opacity: '0'}, 500);
            $('#jgg').animate({width: '0'}, 500);
            setTimeout(function () {
                $('.weui-mask').hide();
                $('#jgg').hide();
            }, 500);
            iszk = false;
        } else {
            //$('#cir').html('-');
            $('#anzk').removeClass('andhgb');
            $('#anzk').removeClass('andhzk');
            $('#anzk').addClass('andhzk');
            iszk = true;
            $('.weui-mask').show();
            $('#jgg').show();
            $('.weui-mask').animate({opacity: '1'}, 500);
            $('#jgg').animate({width: '80%'}, 500);
        }
    }
    ismove = true;
});
/**
 * 初始化内容页面
 * @param option 导航索引
 */
function initpage(option) {
    $('#loadingToast').fadeIn(100);
    $(".mytab").animate({scrollTop: 0}, 1000);
    setTimeout(function () {
        if (option == 0) {
            $('#page_title').html("扶贫对象")
            setTimeout(function () {
                initfpdx();
                //$('#loadingToast').fadeOut(100);
                $("#tooltips_div").css("display", "none");
            }, 50);
        } else if (option == 1) {
            $('#page_title').html("扶贫主体")
            setTimeout(function () {
                initfpzt();
                $("#tooltips_div").css("display", "none");
            }, 50);

        } else if (option) {
            $('#page_title').html("帮扶措施")

            initbfcs();
        } else {
            $('#page_title').html("帮扶成效")
            initbfcx();
        }
    }, 100);
}

function initjgg(option) {
    var jgghtml = '';
    if (option == 0) {
        jgghtml = '<div id="bfdxcd" class="weui-grids">' +
            '<a href="javascript:tzpkgk();" class="weui-grid">' +
            ' <div class="weui-grid__icon">' +
            '<img src="./images/f1.png" alt="">' +
            '</div>' +
            ' <p class="weui-grid__label">贫困概况</p>' +
            '</a>' +
            '<a href="javascript:tzzpyy();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            ' <img src="./images/f2.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">致贫原因</p>' +
            '</a>' +
            '<a href="javascript:tznlfz();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/f3.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">年龄分组</p>' +
            '</a>' +
            '<a href="javascript:tzjkzk();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/f4.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">健康状况</p>' +
            '</a>' +
            '<a href="javascript:tzwhcd();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/f5.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">文化程度</p>' +
            '</a>' +
            '<a href="javascript:tztdzy();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/f6.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">土地资源</p>' +
            '</a>' +
            '<a href="javascript:tzscsh();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/f7.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">生产生活条件</p>' +
            '</a>' +
            '<a href="javascript:tzsljy();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/f8.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">适龄教育</p>' +
            '</a>' +
            '<a href="javascript:tzpkfsl();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/f9.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">贫困发生率</p>' +
            '</a>' +
            '</div>';
    } else if (option == 1) {
        jgghtml = '<div id="bfdxcd" class="weui-grids">' +
            '<a href="javascript:tzpkgk();" class="weui-grid">' +
            ' <div class="weui-grid__icon">' +
            '<img src="./images/g1.png" alt="">' +
            '</div>' +
            ' <p class="weui-grid__label">帮扶概况</p>' +
            '</a>' +
            '<a href="javascript:tzzpyy();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            ' <img src="./images/g2.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">落实情况</p>' +
            '</a>' +
            '<a href="javascript:tznlfz();" class="weui-grid">' +
            '<div class="weui-grid__icon">' +
            '<img src="./images/g3.png" alt="">' +
            '</div>' +
            '<p class="weui-grid__label">入户帮扶</p>' +
            '</a>' +
            '</div>';
    }
    $('#jgg').html(jgghtml);
}
/*******************************************HOME-END*********************************************************/
/*******************************************InitFPDX（扶贫对象模块操作）*********************************************************/
/**
 * 初始化扶贫对象
 */
function initfpdx() {

    $.ajax({//请求概况数据
        url: serviceurl + 'assaWeChatApp/getFpdx1.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {name: xzqhname},
        success: function (data) {
            fpdxdata['pkgk'] = data;
            $('#loadingToast').fadeOut(400);
            setpkgk(fpdxdata['pkgk']);
        },
        error: function (msg) {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
        }
    });

    function setpkgk(data) {
        $('#pkc__sum').html(formatNum(data.tjSum[0].pkc));
        $('#pkh__sum').html(formatNum(data.tjSum[0].pkh));
        $('#pkrk__sum').html(formatNum(data.tjSum[0].pkr));
        $('#general_poor').html(formatNum(data.tjSum[0].ybpkh));
        $('#low_poor').html(formatNum(data.tjSum[0].dbpkh));
        $('#five_poor').html(formatNum(data.tjSum[0].wbpkh));
        setgkbar(data);
    }

    function setzpyy(data) {
        $('#ybzp').html(formatNum(data.tjSum[0].ybzpTotal));
        $('#qzj').html(formatNum(data.tjSum[0].qzjTotal));
        $('#yxzp').html(formatNum(data.tjSum[0].yxzpTotal));
        setzpyybar(data);
    }

    function setnlfz(data) {
        $('#nlfz1').html(formatNum(data.tjSum[0].ageOneTotal));
        $('#nlfz2').html(formatNum(data.tjSum[0].ageTwoTotal));
        $('#nlfz3').html(formatNum(data.tjSum[0].ageThreeTotal));
        $('#nlfz4').html(formatNum(data.tjSum[0].ageFourTotal));
        $('#nlfz5').html(formatNum(data.tjSum[0].ageFiveTotal));
        $('#nlfz6').html(formatNum(data.tjSum[0].ageSixTotal));
        setnlfzbar(data);
    }

    function setjkzk(data) {
        $('#cqmxb').html(formatNum(data.tjSum[0].illCTotal));
        $('#dbrs').html(formatNum(data.tjSum[0].illDTotal));
        $('#cjrs').html(formatNum(data.tjSum[0].illCJTotal));
        setjkzkbar(data);
    }

    function setwhcd(data) {
        $('#wm').html(formatNum(data.tjSum[0].eduOneTotal));
        $('#xxwh').html(formatNum(data.tjSum[0].eduTwoTotal));
        $('#czwh').html(formatNum(data.tjSum[0].eduThreeTotal));
        $('#gzwh').html(formatNum(data.tjSum[0].eduFourTotal));
        $('#dxwh').html(formatNum(data.tjSum[0].eduFiveTotal));
        setwhcdbar(data);
    }

    function settdzy(data) {
        $('#gdmj').html(formatNum(parseInt(data.tjSum[0].landOneTotal) || 0));
        $('#yxgg').html(formatNum(parseInt(data.tjSum[0].landTwoTotal) || 0));
        $('#ldmj').html(formatNum(parseInt(data.tjSum[0].landThreeTotal) || 0));
        $('#tghl').html(formatNum(parseInt(data.tjSum[0].landFourTotal) || 0));
        $('#lgmj').html(formatNum(parseInt(data.tjSum[0].landFiveTotal) || 0));
        $('#mcdmj').html(formatNum(parseInt(data.tjSum[0].landSixTotal) || 0));
        settdzybar(data);
    }

    function setscsh(data) {
        $('#yskn').html(formatNum(data.tjSum[0].proLifeOneTotal));
        $('#waqys').html(formatNum(data.tjSum[0].proLifeTwoTotal));
        $('#wtshyd').html(formatNum(data.tjSum[0].proLifeThreeTotal));
        $('#wtgbds').html(formatNum(data.tjSum[0].proLifeFourTotal));
        $('#zfwf').html(formatNum(data.tjSum[0].proLifeFiveTotal));
        $('#wwscs').html(formatNum(data.tjSum[0].proLifeSixTotal));
        setscshbar(data);
    }

    function setsljy(data) {
        $('#sljy1').html(formatNum(data.tjSum[0].ageEduOneTotal));
        $('#sljy2').html(formatNum(data.tjSum[0].ageEduTwoTotal));
        $('#sljy3').html(formatNum(data.tjSum[0].ageEduThreeTotal));
        $('#sljy4').html(formatNum(data.tjSum[0].ageEduFourTotal));

        setsljybar(data);
    }

    function setpkfsl(data) {
        $('#pkfsl1').html(formatNum(data.tjSum[0].fslZhsTotal));
        $('#pkfsl2').html(formatNum(data.tjSum[0].fslZrsTotal));
        $('#pkfsl3').html(formatNum(data.tjSum[0].fslChsTotal));
        $('#pkfsl4').html(formatNum(data.tjSum[0].fslCrsTotal));
        $('#pkfsl5').html(formatNum(data.tjSum[0].fslFchsTotal));
        $('#pkfsl6').html(formatNum(data.tjSum[0].fslFcrsTotal));
        setpkfslbar(data);
    }

    /**
     * 设置人口分布柱状图
     */
    function setgkbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('gk_bar'));
        var gkdata = sortByKey(data.chartData, 'V2');
        var yaxisdata = new Array();
        var gkdatavalue = new Array;

        for (var tmp in gkdata) {
            yaxisdata.push(gkdata[tmp].V1);
            gkdatavalue.push(gkdata[tmp].V2)

        }
        option1 = {
            title: {
                text: '贫困人口分布',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: gkdatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == gkdatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
        setTimeout(function () {
            getdatao();
        }, 500);
    }

    function getdatao() {//请求其他指标项数据
        $.ajax({//请求致贫原因数据
            url: serviceurl + 'assaWeChatApp/getFpdx2.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['zpyy'] = data;
                setzpyy(fpdxdata['zpyy']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求年龄分组数据
            url: serviceurl + 'assaWeChatApp/getFpdx3.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['nlfz'] = data;
                setnlfz(fpdxdata['nlfz']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求健康状况数据
            url: serviceurl + 'assaWeChatApp/getFpdx4.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['jkzk'] = data;
                setjkzk(fpdxdata['jkzk']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求文化程度数据
            url: serviceurl + 'assaWeChatApp/getFpdx5.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['whcd'] = data;
                setwhcd(fpdxdata['whcd']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求土地资源数据
            url: serviceurl + 'assaWeChatApp/getFpdx6.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['tdzy'] = data;
                settdzy(fpdxdata['tdzy']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求生产生活条件数据
            url: serviceurl + 'assaWeChatApp/getFpdx7.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['scsh'] = data;
                setscsh(fpdxdata['scsh']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求适龄教育数据
            url: serviceurl + 'assaWeChatApp/getFpdx8.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['sljy'] = data;
                setsljy(fpdxdata['sljy']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求贫困发生率数据
            url: serviceurl + 'assaWeChatApp/getFpdx9.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname},
            success: function (data) {
                fpdxdata['pkfsl'] = data;
                setpkfsl(fpdxdata['pkfsl']);
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
     * 设置致贫原因柱状图
     */
    function setzpyybar(data) {
        //行政区划贫困人口数统计****************************************
        var zpyybt = ['因病致贫', '因残致贫', '因学致贫', '因灾致贫', '缺土地', '缺水', '缺技术', '缺劳力', '缺资金', '交通条件落后', '自身发展力不足'];
        var zpyyval = [data.chartData[0].V1,data.chartData[0].V2,data.chartData[0].V3,data.chartData[0].V4,data.chartData[0].V5,data.chartData[0].V6,
            data.chartData[0].V7,data.chartData[0].V8,data.chartData[0].V9,data.chartData[0].V10,data.chartData[0].V11];
        var zpyydata = new Array();
        for(var i = 0;i < zpyybt.length; i++){
            zpyydata[i] = {'V0':zpyybt[i],'V1':zpyyval[i]};
        }
        zpyydata = sortByKey(zpyydata,'V1')
        var myBarChart = echarts.init(document.getElementById('cause_bar'));
        var yaxisdata = new Array();
        var zpyydatavalue = new Array;
        for (var tmp in zpyydata) {
            yaxisdata.push(zpyydata[tmp].V0);
            zpyydatavalue.push(zpyydata[tmp].V1)

        }
        option1 = {
            title: {
                text: '主要致贫原因',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: zpyydatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == zpyydatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }
                }
            ]
        };
        myBarChart.setOption(option1);

    }

    /**
     * 设置年龄分组柱状图
     */
    function setnlfzbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('nlfz_bar'));
        var nlfzdata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var nlfzdatavalue = new Array;

        for (var tmp in nlfzdata) {
            yaxisdata.push(nlfzdata[tmp].V0);
            nlfzdatavalue.push(nlfzdata[tmp].V1)

        }
        option1 = {
            title: {
                text: '60岁以上人口数量',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: nlfzdatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == nlfzdatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    /**
     * 设置健康状况柱状图
     */
    function setjkzkbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('cqmxb_bar'));
        var jkzkdata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var jkzkdatavalue = new Array;

        for (var tmp in jkzkdata) {
            yaxisdata.push(jkzkdata[tmp].V0);
            jkzkdatavalue.push(jkzkdata[tmp].V1)

        }
        option1 = {
            title: {
                text: '长期慢性病人口数量',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: jkzkdatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == jkzkdatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    /**
     * 设置文化程度柱状图
     */
    function setwhcdbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('wmrs_bar'));
        var whcddata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var whcddatavalue = new Array;

        for (var tmp in whcddata) {
            yaxisdata.push(whcddata[tmp].V0);
            whcddatavalue.push(whcddata[tmp].V1);
        }
        option1 = {
            title: {
                text: '文盲人口数量',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: whcddatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == whcddatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    /**
     * 设置土地资源柱状图
     */
    function settdzybar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('tdzy_bar'));
        var tdzydata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var tdzydatavalue = new Array;

        for (var tmp in tdzydata) {
            yaxisdata.push(tdzydata[tmp].V0);
            tdzydatavalue.push(tdzydata[tmp].V1)

        }
        option1 = {
            title: {
                text: '人均耕地面积',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "亩";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: tdzydatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == tdzydatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    /**
     * 设置生产生活条件柱状图
     */
    function setscshbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('rjzf_bar'));
        var scshdata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var scshdatavalue = new Array;

        for (var tmp in scshdata) {
            yaxisdata.push(scshdata[tmp].V0);
            scshdatavalue.push(scshdata[tmp].V1)

        }
        option1 = {
            title: {
                text: '人均住房面积(平方米)',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "m²";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: scshdatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == scshdatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    /**
     * 设置适龄教育柱状图
     */
    function setsljybar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('sljy_bar'));
        var sljydata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var sljydatavalue = new Array;

        for (var tmp in sljydata) {
            yaxisdata.push(sljydata[tmp].V0);
            sljydatavalue.push(sljydata[tmp].V1)

        }
        option1 = {
            title: {
                text: '6-15岁人口',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: sljydatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == sljydatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    /**
     * 设置贫困发生率柱状图
     */
    function setpkfslbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('pkfsl_bar'));
        var pkfsldata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var pkfsldatavalue = new Array;

        for (var tmp in pkfsldata) {
            yaxisdata.push(pkfsldata[tmp].V0);
            pkfsldatavalue.push(pkfsldata[tmp].V1)

        }
        option1 = {
            title: {
                text: '贫困发生率(%)',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "%";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: pkfsldatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == pkfsldatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

}

function tzpkgk() {
    $(".mytab").animate({scrollTop: 0}, 1000);
    $('.andh').attr('background-image', 'url("../images/k1.gif")');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tzzpyy() {
    $(".mytab").animate({scrollTop: zpyytop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tznlfz() {
    $(".mytab").animate({scrollTop: nlfztop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tzjkzk() {
    $(".mytab").animate({scrollTop: jkzktop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tzwhcd() {
    $(".mytab").animate({scrollTop: whcdtop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tztdzy() {
    $(".mytab").animate({scrollTop: tdzytop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tzscsh() {
    $(".mytab").animate({scrollTop: scshtop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tzsljy() {
    $(".mytab").animate({scrollTop: sljytop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
function tzpkfsl() {
    $(".mytab").animate({scrollTop: pkfsltop}, 1000);
    $('#anzk').removeClass('andhgb');
    $('#anzk').removeClass('andhzk');
    $('#anzk').addClass('andhgb');
    $('.weui-mask').animate({opacity: '0'}, 500);
    $('#jgg').animate({width: '0'}, 500);
    setTimeout(function () {
        $('.weui-mask').hide();
        $('#jgg').hide();
    }, 500);
    iszk = false;
}
/**************************************FPDX-END（扶贫对象部分完毕）***********************************************/
/**************************************FPZT-END（扶贫主体部分）***********************************************/
/**
 * 初始化扶贫主体
 */
function initfpzt() {
    $('#gdrj').click(function () {
        $('#loadingToast').fadeIn(20);
        $('#cir').hide();
        setTimeout(function () {
            $(".mytab").animate({scrollTop: 0}, 1000);
            rjtype = 0;
            counter = 1;
            rjoption = 0;
            $("#rjcells").html("");
            jrrj();
        }, 110);
    });
    $.ajax({//请求概况数据
        url: serviceurl + 'assaWeChatApp/getFpdx10.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {name: xzqhname, code: xzqhcode, level: cType},
        success: function (data) {
            fpztdata['bfgk'] = data;
            $('#loadingToast').fadeOut(400);
            setbfgk(fpztdata['bfgk']);
        },
        error: function (msg) {
            $("#tooltips_div").css("display", "block");
            $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
            setTimeout(function () {
                $("#tooltips_div").css("display", "none");
            }, 2000);
        }
    });
    function setbfgk(data) {
        $('#bfdw__sum').html(formatNum(data.tjSum[0].bfdwTotal));
        $('#zcgzd__sum').html(formatNum(data.tjSum[0].zcgzdTotal));
        $('#zcgzgb__sum').html(formatNum(data.tjSum[0].zcgzgbTotal));
        $('#lsbfzrr_sum').html(formatNum(data.tjSum[0].lsbfzrrTotal));
        $('#bfhs_sum').html(formatNum(data.tjSum[0].bfhsTotal));
        setbfgkbar(data);
    }

    function setlsqk(data) {
        $('#lszhs__sum').html(formatNum(data.tjSum[0].ZhsTotal));
        $('#bfzrrlshs__sum').html(formatNum(data.tjSum[0].lsHsTotal));
        $('#lsbfbl__sum').html((data.tjSum[0].lsBlTotal * 100) + '%');
        setlsqkbar(data);
    }

    function setrhbf(data) {
        $('#rh-pkh__sum').html(formatNum(data.tjSum[0].pkhTotal));
        $('#zfpkh__sum').html(formatNum(data.tjSum[0].zfpkhTotal));
        $('#zfbl_rh').html((data.tjSum[0].zfblTotal * 100) + '%');
        $('#day_sum').html(formatNum(data.tjSum[0].drzfTotal));
        $('#week_sum').html(formatNum(data.tjSum[0].bzzfTotal));
        $('#month_sum').html(formatNum(data.tjSum[0].byzfTotal));
        $('#jsy_sum').html(formatNum(data.tjSum[0].jsyzfTotal));
        $('#qbzf_sum').html(formatNum(data.tjSum[0].zfAllTotal));
        setrhbfbar(data);
    }

    /**
     * 设置帮扶概况柱状图
     */
    function setbfgkbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('bfgk_bar'));
        var bfgkdata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var bfgkdatavalue = new Array;

        for (var tmp in bfgkdata) {
            yaxisdata.push(bfgkdata[tmp].V0);
            bfgkdatavalue.push(bfgkdata[tmp].V1)
        }
        option1 = {
            title: {
                text: '帮扶责任人数量',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: bfgkdatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == bfgkdatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
        setTimeout(function () {
            getdataobfzt();
        }, 500);
    }

    /**
     * 设置落实情况柱状图
     */
    function setlsqkbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('lsqk_bar'));
        var lsqkdata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var lsqkdatavalue = new Array;

        for (var tmp in lsqkdata) {
            yaxisdata.push(lsqkdata[tmp].V0);
            lsqkdatavalue.push(lsqkdata[tmp].V1)
        }
        option1 = {
            title: {
                text: '落实帮扶责任人数',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "人";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: lsqkdatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == lsqkdatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    /**
     * 设置入户走访柱状图
     */
    function setrhbfbar(data) {
        //行政区划贫困人口数统计****************************************
        var myBarChart = echarts.init(document.getElementById('rhbf_bar'));
        var rhbfdata = sortByKey(data.chartData, 'V1');
        var yaxisdata = new Array();
        var rhbfdatavalue = new Array;

        for (var tmp in rhbfdata) {
            yaxisdata.push(rhbfdata[tmp].V0);
            rhbfdatavalue.push(rhbfdata[tmp].V1)
        }
        option1 = {
            title: {
                text: '走访次数',
                textStyle: {
                    color: '#6f6f6f',
                    fontFamily: '黑体',
                    fontSize: '15px'
                },
                padding: [30, 5, 5, 50]
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
                right: '20%',
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

            series: [
                {
                    name: '贫困人口',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'right',
                            /*formatter: '{c}户'*/
                            formatter: function (data) {
                                return formatNum(data.value) + "次";
                            }
                        }
                    },
                    barCategoryGap: '18px',
                    barWidth: '16px',
                    data: rhbfdatavalue.reverse(),
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var ls = 'rgb(19,139,249)';
                                var hs = 'rgb(249,25,46)';
                                if (params.dataIndex == rhbfdatavalue.length - 1) {
                                    return hs;
                                } else {
                                    return ls;
                                }
                            }
                        }
                    }

                }
            ]
        };
        myBarChart.setOption(option1);
    }

    function getdataobfzt() {
        $.ajax({//请求落实情况数据
            url: serviceurl + 'assaWeChatApp/getFpdx11.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname, code: xzqhcode, level: cType},
            success: function (data) {
                fpztdata['lsqk'] = data;
                setlsqk(fpztdata['lsqk']);
            },
            error: function (msg) {
                $("#tooltips_div").css("display", "block");
                $("#tooltips_div").html("服务器异常，错误码：" + msg.status);
                setTimeout(function () {
                    $("#tooltips_div").css("display", "none");
                }, 2000);
            }
        });
        $.ajax({//请求走访日记数据
            url: serviceurl + 'assaWeChatApp/getFpdx12.do',
            type: 'POST',
            async: false,
            dataType: 'json',
            data: {name: xzqhname, code: xzqhcode, level: cType},
            success: function (data) {
                fpztdata['rhbf'] = data;
                setrhbf(fpztdata['rhbf']);
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
 * 显示今日日记
 */
function jrrj() {
    $(".weui-tab__panel").children().hide();//隐藏所有子页面
    $(".weui-tab__panel").children().eq(4).show();//显示对应的子页面
    isrjlb = true;
    getrjdata();
    $('#jrrj').click(function () {
        rjtype = 1;
        counter = 1;
        rjoption = 0;
        $("#rjcells").html("");
        $('#rjlbbq').children().removeClass('rjbqax');
        $('#jrrj').addClass('rjbqax');
        $('#loadingToast').fadeIn(80);
        setTimeout(function(){
            getrjdata();
        },100);
    });
    $('#bzrj').click(function () {
        rjtype = 2;
        $('#rjlbbq').children().removeClass('rjbqax');
        $('#bzrj').addClass('rjbqax');
        counter = 1;
        rjoption = 0;
        $("#rjcells").html("");
        $('#loadingToast').fadeIn(80);
        setTimeout(function(){
            getrjdata();
        },100);
    });
    $('#byrj').click(function () {
        rjtype = 3;
        $('#rjlbbq').children().removeClass('rjbqax');
        $('#byrj').addClass('rjbqax');
        counter = 1;
        rjoption = 0;
        $("#rjcells").html("");
        $('#loadingToast').fadeIn(80);
        setTimeout(function(){
            getrjdata();
        },100);
    });
    $('#qbrj').click(function () {

        $('#rjlbbq').children().removeClass('rjbqax');
        $('#qbrj').addClass('rjbqax');
        rjtype = 0;
        counter = 1;
        rjoption = 0;
        $("#rjcells").html("");
        $('#loadingToast').fadeIn(80);
        setTimeout(function(){
            getrjdata();
        },100);
    });
    /*监听加载更多*/
    $("#jzgd").click(function () {
        if (isxsxq == true) {
            return;
        }
        if (isEnd == true) {

            return;
        }
        $("#jzgd").addClass("weui-btn_loading");
        $("#jzgd").html("<i class='weui-loading'></i>" + "加载中...");
        counter++;
        $("#loadingToast").fadeIn(100);
        setTimeout(function () {
                $("#loadingToast").fadeOut(600);
                if (iscxdx == true) {
                    start_search();
                } else {
                    jrrj();
                }

                window.parent.code = xzqhcode;
            }
            , 300);
    });

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
}

function getrjdata() {
    /*请求日记列表*/
    $.ajax({
        url: serviceurl + 'assaWeChatApp/getRjll.do',
        type: 'POST',
        async: false,
        dataType: 'json',
        data: {cType: cType, code: xzqhcode, pageNum: counter, type: rjtype},
        success: function (data) {
            console.log(data);
            if (counter == 1) {
                zfdata = data;
            } else {
                zfdata = zfdata.concat(data);
            }
            initRJcells(data);
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
        $("#weuitips").html("点击加载更多");
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
    $("#loadingToast").fadeOut(100);
    $("a.alink").click(lookdetail);
    $("#jzgd").removeClass("weui-btn_loading");
    $("#jzgd").html("加载更多");
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
        var zflx = zfdata[option].zftype;
        if (zflx != undefined){
            zflx = getzflx(zflx);
        }
        var detailhtml = "<div class='weui-flex'><div class='left_black'><img src='images/black.png' style='text-align: center' height='100%'></div>";
        detailhtml += "<div class='weui-flex__item subhead' style='padding-top: 0;height: 2.3em;line-height: 2.5em;background: transparent;color: #6f6f6f'>走访详情</div>";
        detailhtml += "<div style='width: 60px'></div></div>"
        detailhtml += "<div class='zftp'><img style='border: 3px solid ghostwhite' height='100%' src='http://www.gistone.cn" + zfdata[option].pic + "'></div>";
        detailhtml += "<div class='weui-cells' style='margin-top: 0'><div class='weui-cell'><div class='detailtext'>帮扶人：" + bfrname + "</div></div>";
        detailhtml += "<div class='weui-cell'><div class='detailtext' >贫困户：" + pkhname + "</div></div>";
        detailhtml += "<div class='weui-cell'><div class='detailtext' >走访时间：" + zfsj + "</div></div>";
        if (zflx != undefined){
            detailhtml += "<div class='weui-cell'><div class='detailtext' >走访类型：" + zflx + "</div></div>";
        }
        detailhtml += "<div class='weui-cell'><div class='detailtext' style='_height:200px; min-height:200px;padding-left: 10px' >走访记录：" + zfjl + "</div> </div></div>";

        detailhtml += "<div style='height: 100px'></div>"
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

function getzflx(p){
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
                        $("#weuitips").html("点击加载更多");
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
/**************************************FPZT-END(扶贫主体结束)***********************************************/

/**************************************BFCS(帮扶措施部分)***********************************************/
function initbfcs() {
    $('#loadingToast').fadeOut(600);
}
/**************************************BFCS-END(扶贫主体结束)***********************************************/

/**************************************BFCX(帮扶成效部分)***********************************************/
function initbfcx() {
    $('#loadingToast').fadeOut(600);
}
/**************************************BFCX-END(扶贫主体结束)***********************************************/

/**************************************Utils(通用工具部分)***********************************************/
/**
 * 格式化数字（每三位加 ，号）
 * @param nStr
 * @returns {*}
 */
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
/**
 * 格式化日期2017-1-1
 * @param datetime
 * @returns {string}
 */
function formatDate(datetime) {
    datetime = datetime.replace(/-/g, "/");
    var newdate = new Date(datetime);
    return newdate.getFullYear() + "-" + (newdate.getMonth() + 1) + "-" + newdate.getDate();
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
/**
 * 获取URL的参数
 * @param name
 * @returns {*}
 */
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
/**
 *重新排列数组(把第一个放到中间去)
 * @param arr
 * @returns {*}
 */
function rearrangement(arr) {
    var centernum = parseInt(arr.length / 2);
    var temp = arr[0];
    arr[0] = arr[centernum];
    arr[centernum] = temp;
    return arr;
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
/**
 * 从大到小
 * @param array
 * @param key
 * @returns {Array.<T>|*}
 */
function sortByKey(array, key) {
    return array.sort(function (a, b) {
        var y = a[key] * 1;
        var x = b[key] * 1;
        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    });
}

/**********************************************Utils-END****************************************************************************/