Ext.application({
	name : '区域管理',
	launch : function() {
		
		var treeNode;
		var editFlag;
		var treeStore= Ext.create('Ext.data.TreeStore', {
		    root: {
		    	text:'根节点',
		        expanded: true
		    },
		    nodeParam: 'id',
		    defaultRootId:'0',
		    fields:[{name:'id',type:'string'},
		            {name:'text',type:'string'},
		            {name:'leaf',type:'boolean'},
		            {name:'parentid',type:'string'}
		            ],
		     proxy: {
						type:'ajax',
						method:'post',
						url:'../../action/branch/queryBranchTreeByAccountId'
					}
		});
		var addAction=Ext.create('Ext.Action',{
			text:'增加',
			icon:'../../images/add.gif',
			handler:function()
			{
				editFlag=false;
				editPanel.setDisabled(false);
				editForm.setTitle('增加区域信息');
				editForm.getForm().findField('parentid').setValue(treeNode.get('id'));
				editForm.getForm().findField('parentid').setReadOnly(true);
				editForm.getForm().findField('branchid').setValue("");
				editForm.getForm().findField('branchid').setReadOnly(false);
				editForm.getForm().findField('branchname').setValue("");
				editForm.getForm().url='../../action/branch/addBranch';
			}
		});
		var delAction=Ext.create('Ext.Action',{
			text:'删除',
			icon:'../../images/del.gif',
			handler:function()
			{
				Ext.Msg.show({
					msg:'确定删除【'+treeNode.get('text')+'】?', 
					title:'删除区域', 
					buttons:Ext.Msg.OKCANCEL,
					fn:function(btn, text){
						if (btn == 'ok'){
					    	var request=Ext.Ajax.request({
								params: {
							        id: treeNode.get('id')
							    },
								url:'../../action/branch/deleteBranch',
								method:'GET',
								success:function(response,options){
									Ext.Msg.alert('信息提示', '删除区域信息成功');
									treeStore.load();
								},
								failure:function(){
									Ext.Msg.alert('信息提示', '删除区域信息失败');
								}
							});
					    }
					}
				})
			}
					
		});
		var editAction=Ext.create('Ext.Action',{
			text:'修改',
			icon:'../../images/modify.gif',
			handler:function(){
				editFlag=true;
				editPanel.setDisabled(false);
				editForm.setTitle('修改区域信息');
				editForm.getForm().findField('parentid').setValue(treeNode.get('parentid'));
				editForm.getForm().findField('branchid').setValue(treeNode.get('id'));
				editForm.getForm().findField('branchname').setValue(treeNode.get('text'));
				editForm.getForm().findField('parentid').setReadOnly(true);
				editForm.getForm().findField('branchid').setReadOnly(true);
				editForm.getForm().url='../../action/branch/modifyBranch';
			}
		});
		
	    var contextMenu = Ext.create('Ext.menu.Menu', {
		       
		    });
		var treePanel = Ext.create('Ext.tree.Panel', {
			title: '区域信息',
			flex:4,
			width:400,
			bodyPadding: '20 20 0',
			store:treeStore,
			rootVisible: false,
		    aoutLoad:true,
			listeners:{
				itemcontextmenu: function(view, rec, node, index, e) {
					e.stopEvent();
					contextMenu.removeAll(false);
					if(rec.get('leaf')==true){
						 contextMenu.insert(0,addAction);
						 contextMenu.insert(1,editAction);
						 contextMenu.insert(2,delAction);
					}
					else{
						 contextMenu.insert(0,addAction);
						 contextMenu.insert(1,editAction);	
					}
					contextMenu.showAt(e.getXY());
	                treeNode=rec;
	                return false;
                }
			}
		});
		
		var editForm = Ext.create('Ext.form.Panel',{
			title:'区域详细信息',
			height:200,
			bodyPadding:'20 20 0',
			items:[{
				xtype:'textfield',
				name:'parentid',
				fieldLabel:'上级区域ID'
			},{
				xtype:'textfield',
				name:'branchid',
				fieldLabel:'当前区域ID',
				allowBlank:false
			},{
				xtype:'textfield',
				name:'branchname',
				fieldLabel:'区域名称',
				allowBlank:false
			}],
			buttonAlign:'center',
			buttons:[{
				text:'保存',
				glyph:'xf0c7@FontAwesome',
				handler:function(){
					if(editForm.getForm().isValid()){
						editForm.getForm().submit({
							success:function(form,action){
								Ext.Msg.alert('信息提示', editFlag==false?'增加区域信息成功':'修改区域信息成功');
								treePanel.getStore().load();
							},
							failure:function(form,action){
								Ext.Msg.alert('信息提示', editFlag==false?'增加区域信息失败':'修改区域信息失败');
							}
						})
					}
				}
			},{
				text:'返回',
				glyph:'xf0e2@FontAwesome',
				handler:function(){
					editForm.getForm().reset();
					editPanel.setDisabled(true);
				}
			}]
		})
		
		var editPanel=Ext.create('Ext.panel.Panel',{
			xtype:'panel',
			flex:2,
			disabled:true,
		    rootVisible:false,
			items:[editForm]
		})
		Ext.create('Ext.container.Viewport', {
			layout:{
				type:'hbox',
				align:'stretch'
			},
			items : [treePanel,editPanel],
			renderTo : Ext.getBody()
		});
		
		
	}
});