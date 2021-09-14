layui.use(['form','layuimini'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery;

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        $.ajax({
            type:"post",
            url:ctx+"/user/updateUserInfo",
            data:data.field,
            dataType:"json",
            success:function (data) {
                if(data.code==200){
                    console.log(data);
                    layer.msg(data.message, function() { // layer展示完毕之后执行这个函数
                        // alert("cishuxianshi")
                        // 同时更新父页面和子页面
                        window.parent.location.reload();
                        // location.href = "setting";    // 只能在当前iframe中重新加载setting页面
                    });
                    // alert("cishuxianshi"); // 这个执行完毕之后，执行上面的展示和后段函数
                }else{
                    layer.msg(data.message);
                }
            }
        });
        return false;

    });
});