layui.use(['laydate', 'table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        laydate = layui.laydate
    table = layui.table;

    laydate.render({
        elem: '#startDate'
    });
    laydate.render({
        elem: '#endDate'
    });


    var tableIns = table.render({
        elem: '#leftList',
        height: "full-125",
        page: true,
        limits: [10, 15, 20, 25],
        cols: [[
            {title: '单号', minWidth: 50, align: "center"},
            {title: '日期', minWidth: 50, align: "center"},
            {title: '类型', minWidth: 100, align: 'center'},
            {title: '操作员', minWidth: 100, align: 'center'},
            {title: '备注', minWidth: 100, align: 'center'},
            {minWidth: 150, templet: '#leftListBar', fixed: "right", align: "center"}
        ]],
        id: "leftListTable",
        data: []
    });


    table.render({
        elem: '#rightList',
        height: "full-125",
        page: true,
        limits: [10, 15, 20, 25],
        id: "rightListTable",
        cols: [[
            {field: 'code', title: '商品编码', minWidth: 50, align: "center"},
            {field: 'name', title: '商品名称', minWidth: 50, align: "center"},
            {field: 'model', title: '商品型号', minWidth: 100, align: 'center'},
            {field: 'price', title: '单价', minWidth: 100, align: 'center'},
            {field: 'num', title: '数量', minWidth: 100, align: 'center'},
            {field: 'unit', title: '单位', minWidth: 100, align: 'center'},
            {field: 'total', title: '总金额', minWidth: 100, align: 'center'}
        ]],
        data: []
    });


    // 多条件搜索
    $(".search_btn").on("click", function () {
        console.log("点击搜索按钮");
        var type = $('select[name="type"] option:selected').val();
        if (type == 1) {
            loadDamageDatas($("input[name='startDate']").val(), $("input[name='endDate']").val());
            loadDamageListGoodsDatas();
        } else if (type == 2) {
            loadOverflowDatas($("input[name='startDate']").val(), $("input[name='endDate']").val());
            loadOverflowListGoodsDatas();
        }
    });

    $(".search_btn").click();

    /**
     * 报损单数据
     */
    function loadDamageDatas(startDate, endDate) {
        $.ajax({
            type: "post",
            url: ctx + "/damage/list",
            data: {
                startDate: startDate,
                endDate: endDate
            },
            success: function (datas) {
                console.log(datas);
                table.reload("leftListTable", {
                    cols: [[
                        {field: 'damageNumber', title: '单号', minWidth: 50, align: "center"},
                        {field: 'damageDate', title: '日期', minWidth: 50, align: "center"},
                        {
                            field: 'type', title: '类型', minWidth: 100, align: 'center', templet: function (d) {
                                return "报损单"
                            }
                        },
                        {field: 'userName', title: '操作员', minWidth: 100, align: 'center'},
                        {field: 'remarks', title: '备注', minWidth: 100, align: 'center'},
                        {title: '操作', minWidth: 150, templet: '#leftListBar', fixed: "right", align: "center"}
                    ]],
                    data: datas.data
                })
            }
        })
    }

    /**
     * 报溢单
     * @param startDate
     * @param endDate
     */
    function loadOverflowDatas(startDate, endDate) {
        $.ajax({
            type: "post",
            url: ctx + "/overflow/list",
            data: {
                startDate: startDate,
                endDate: endDate
            },
            success: function (datas) {
                table.reload("leftListTable", {
                    cols: [[
                        {field: 'overflowNumber', title: '单号', minWidth: 50, align: "center"},
                        {field: 'overflowDate', title: '日期', minWidth: 50, align: "center"},
                        {
                            field: 'type', title: '类型', minWidth: 100, align: 'center', templet: function (d) {
                                return "报溢单"
                            }
                        },
                        {field: 'userName', title: '操作员', minWidth: 100, align: 'center'},
                        {field: 'remarks', title: '备注', minWidth: 100, align: 'center'},
                        {title: '操作', minWidth: 150, templet: '#leftListBar', fixed: "right", align: "center"}
                    ]],
                    data: datas.data
                })
            }
        })
    }


    /**
     * 报损单商品查询
     */
    function loadDamageListGoodsDatas(damageListId) {
        $.ajax({
            type: "post",
            url: ctx + "/damageListGoods/list",
            data: {
                damageListId: damageListId
            },
            success: function (datas) {
                table.reload("rightListTable", {
                    data: datas.data
                })
            }
        })
    }

    /**
     * 报溢单商品查询
     */
    function loadOverflowListGoodsDatas(overflowListId) {
        $.ajax({
            type: "post",
            url: ctx + "/overflowListGoods/list",
            data: {
                overflowListId: overflowListId
            },
            success: function (datas) {
                table.reload("rightListTable", {
                    data: datas.data
                })
            }
        })
    }


    /**
     * 行监听
     */
    table.on("tool(leftList)", function (obj) {
        var layEvent = obj.event;
        console.log(obj.data);
        if (layEvent === "search") {
            /**
             * 填充右侧输入框数据
             */
            var type = $('select[name="type"] option:selected').val();
            if (type == 1) {
                $("#number_").val(obj.data.damageNumber);
                $("#date_").val(obj.data.damageDate);
                $("#typeName_").val("报损单");
                $("#userName_").val(obj.data.userName);
                loadDamageListGoodsDatas(obj.data.id);
            } else if (type == 2) {
                $("#number_").val(obj.data.overflowNumber);
                $("#date_").val(obj.data.overflowDate);
                $("#typeName_").val("报溢单");
                $("#userName_").val(obj.data.userName);
                loadOverflowListGoodsDatas(obj.data.id);
            }
        }

        /**
         * 删除
         */
        if (layEvent === "del") {
            var type = $('select[name="type"] option:selected').val();
            var typename;
            if (type == 1) {
                typename = "damage";
            } else if (type == 2) {
                typename = "overflow";
            }

            layer.confirm('确定删除当前单据？', {icon: 3, title: "删除单据"}, function (index) {
                $.post(ctx + "/" + typename + "/delete", {id: obj.data.id}, function (data) {
                    if (data.code == 200) {
                        layer.msg("操作成功！");
                        // 重新进行查询，从而更新数据
                        $(".search_btn").click();
                    } else {
                        layer.msg(data.message, {icon: 5});
                    }
                });
            })
        }


    });


    // 重置
    $(".search_btn02").on("click", function () {
        resetListGoodsTab();
    });

    function resetListGoodsTab() {
        $("#number_").val("");
        $("#date_").val("");
        $("#typeName").val("");
        $("#userName_").val("");
        table.reload("rightListTable", {
            data: []
        })
    }

})



