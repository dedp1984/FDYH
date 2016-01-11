Ext.application({
	name : '机构管理',
	launch : function() {
		
		var treeNode;
		var treeStore= Ext.create('Ext.data.TreeStore', {
		    root: {
		    	text:'根节点',
		        expanded: true
		    },
		    nodeParam: 'id',
		    defaultRootId:0,
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
			handler:function()
			{
				editPanel.setDisabled(false);
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
			handler:function()
			{
				Ext.Msg.show({
					msg:'确定删除【'+treeNode.get('text')+'】?', 
					title:'删除机构', 
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
									alert('删除机构成功');
									treeStore.load();
								}
							});
					    }
					}
				})
			}
					
		});
		var editAction=Ext.create('Ext.Action',{
			text:'修改',
			handler:function(){
				editPanel.setDisabled(false);
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
			title: '机构信息',
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
			title:'机构详细信息',
			height:170,
			bodyPadding:'20 20 0',
			items:[{
				xtype:'textfield',
				name:'parentid',
				fieldLabel:'上级机构ID'
			},{
				xtype:'textfield',
				name:'branchid',
				fieldLabel:'机构ID'
			},{
				xtype:'textfield',
				name:'branchname',
				fieldLabel:'机构名称'
			}],
			buttonAlign:'center',
			buttons:[{
				text:'保存',
				handler:function(){
					if(editForm.getForm().isValid()){
						editForm.getForm().submit({
							success:function(form,action){
								treePanel.getStore().load();
							},
							failure:function(form,action){
								alert(action);
							}
						})
					}
				}
			},{
				text:'返回',
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