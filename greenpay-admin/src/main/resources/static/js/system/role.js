/**
 * 权限列表
 */
//选中的复选框


!(function () {
    let $ = layui.jquery;
        var tree;
        layui.use(['form', 'tree', 'layer', 'jquery'], function () {
            var form = layui.form;
            var layer = layui.layer;
             tree = layui.tree;
            //监听提交
            getTreeData(tree);

            form.on('submit(roleSubmit)', function (data) {

                var array = new Array();
                //获取选中的权限id
                var checkedData = tree.getChecked('demoId1'); //获取选中节点的数据

                for (let j = 0; j < checkedData.length; j++) {
                    var node = checkedData[j];
                    let childs = getChildNode(node);
                    childs.push(node.id);
                    array.push(childs.concat());
                    nodeIds.length = 0;
                }

                //校验是否授权
                var permIds = array.join(",");
                // console.log("permIds"+permIds)
                if (permIds == null || permIds == '') {
                    layer.alert("请给该角色添加权限菜单！")
                    return false;
                }

                var name = data.field.name;
                var roleCode =permIds;
                var role = {name:name,roleCode:roleCode,permIds:permIds}

                $.ajax({
                    type: "POST",
                    data: role,
                    url: "/admin/api/v1/system/roles/add",
                    success: function (data) {
                        if (data) {
                            layer.alert("操作成功", function () {
                                layer.closeAll();
                                window.location.href="/admin/system/role/list";
                            });
                        } else {
                            layer.alert("操作请求错误，请您稍后再试");
                        }
                    },
                    error: function (data) {
                        alert(data.responseJSON.message);
                        layer.closeAll();
                    }
                });
                return false;
            });

        });



    function getTreeData() {
        $.ajax({
            type: "get",
            url: "/admin/api/v1/system/menus/roleTree",
            success: function (data) {
                let menus = data.records;
                if (menus != null) {
                    initTree(menus);
                } else {
                    layer.alert(menus);
                }
            },
            error: function () {
                layer.alert("获取数据错误，请您稍后再试");
            }
        });
    }

    function initTree(data) {
        layui.use('util', function () {

                util = layui.util;

            //渲染
            var inst1 = tree.render({
                id: 'demoId1',
                elem: '#perm', //指定元素
                target: '_blank', //是否新选项卡打开（比如节点返回href才有效）
                showCheckbox: true, //是否显示复选框
                checked: function (item) {//复选框
                    // layer.msg('check当前节名称：'+ item.name + '<br>全部参数：'+ JSON.stringify(item));
                    // console.log('item is Array：'+ item instanceof Array);
                    // console.log('item is：'+ item);
                    // console.log('check当前节名称：'+ JSON.stringify(item));
                    //判断是选中还是移除选中 ,checkbox: ['&#xe626;', '&#xe627;'] //复选框
                    /*var checkFlag = data.elem.checked;
                    var cFlag = $(this).checked;
                    console.log("checkFlag:"+checkFlag)
                    console.log("cFlag:"+cFlag)*/
                    //当前节点
                    //nodeIds.push(item.id);
                    /* if( item.children.length > 0 ){
                         nodeIds= getChildNode(item);
                     }
                     console.log('nodeIds：'+ JSON.stringify(nodeIds));
                     // permArray.add(item);
                     $.unique(nodeIds);
                     console.log('check当前节名称：'+ JSON.stringify(nodeIds));*/
                    //$("#permIds").val(permIds);
                },
                click: function (item) { //点击节点回调
                    // layer.msg('click当前节名称：'+ item.name + '<br>全部参数：'+ JSON.stringify(item));
                    // console.log('click当前节名称：'+ JSON.stringify(item));
                    //treeIds+=item.id;
                },
                skin: 'shihuang',//皮肤
                //checkboxName: 'permCheck',//复选框的name属性值
                //checkboxStyle: "color: #FD482C",//设置复选框的样式，必须为字符串，css样式
                /* change: function (item){//当当前input发生变化后所执行的回调//console.log(item);
                     resourceIds=item;
                 },
                 data: {//为元素添加额外数据，即在元素上添加data-xxx="yyy"，可选
                     hasChild: true
                 }*/
                data: listToTreeJson(data)
            });

            //按钮事件
            util.event('lay-demo', {
                getChecked: function (othis) {
                    var checkedData = tree.getChecked('demoId1'); //获取选中节点的数据

                    layer.alert(JSON.stringify(checkedData), {shade: 0});
                    console.log(checkedData);
                }
                , setChecked: function () {
                    tree.setChecked('demoId1', [12, 16]); //勾选指定节点
                }
                , reload: function () {
                    //重载实例
                    tree.reload('demoId1', {});

                }
            });
        });

    }

    /**
     * 获取所有子节点的id数组
     * @param obj
     * @returns {Array}
     */
    let nodeIds = [];
    function getChildNode(obj) {

        if (obj != null) {
                if (obj.children.length > 0) {
                    $.each(obj.children, function (k, v) {
                        nodeIds.push(v.id);
                        getChildNode(v);
                    });
                }

        }
        return nodeIds;
    }

    var demoData = [
        {"id": "aaa", "pid": "account", "spType": 0, "layerId": 0, "seqId": 1, "name": "阿萨德发多少", "deleted": "0"},
        {"id": "account", "pid": "", "spType": 0, "layerId": 0, "seqId": 50, "name": "账户", "deleted": "0"},
        {"id": "bbb", "pid": "account", "spType": 0, "layerId": 0, "seqId": 2, "name": "阿萨德发多少", "deleted": "0"},
        {"id": "ccc", "pid": "account", "spType": 0, "layerId": 0, "seqId": 3, "name": "a啊都是发", "deleted": "0"},
        {"id": "ddd", "pid": "dispatch", "spType": 0, "layerId": 0, "seqId": 1, "name": "大夫", "deleted": "0"},
        {"id": "dispatch", "pid": "", "spType": 0, "layerId": 0, "seqId": 2, "name": "通知公告", "deleted": "0"},
        {"id": "eee", "pid": "dispatch", "spType": 0, "layerId": 0, "seqId": 2, "name": "；卡萨丁", "deleted": "0"},
        {"id": "fff", "pid": "gridding", "spType": 0, "layerId": 0, "seqId": 1, "name": "拉收到了", "deleted": "0"},
        {"id": "gridding", "pid": "", "spType": 0, "layerId": 0, "seqId": 1, "name": "网格化管理", "deleted": "0"},
        {"id": "portals", "pid": "", "spType": 0, "layerId": 0, "seqId": 3, "name": "综合信息门户管理", "deleted": "0"}
    ];

    /**
     * list转化为tree结构的json数据
     */
    function listToTreeJson(data) {
        //data不能为null，且是数组
        if (data != null && (data instanceof Array)) {
            //递归转化
            var getJsonTree = function (data, parentId) {
                var itemArr = [];
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    if (parentId != null && node.parentId == parentId) {
                        var newNode = {
                            title: node.title,
                            spread: true,
                            id: node.id,
                            pid: node.parentId,
                            children: getJsonTree(data, node.id)
                        };
                        itemArr.push(newNode);
                    }
                }
                return itemArr;
            }
            // return JSON.stringify(getJsonTree(data,''));
            return getJsonTree(data, -1);
        }
        //console.log(JSON.stringify(getJsonTree(data,'')));

}}());