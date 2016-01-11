Ext.application({
	name : '账户管理',
	launch : function() {
		var dsFrom = Ext.create('Ext.data.Store', {
	        fields: ['accountid','accountname'],
	        proxy: {
	            type: 'ajax',
	            url: '../../ajax/account/queryAccountList',
	            reader: {  
                    type:'json',
                    root:'items'
				}
	        },
	        autoLoad: false,
	        sortInfo: {
	            field: 'accountid',
	            direction: 'ASC'
	        }
	    });
		var editForm=Ext.create('Ext.form.Panel',{
			bodyPadding:'30 20 0',
			bodyBorder:false,
			paramsAsHash: true,
			layout : 'form',
			items:[{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						width:400,
						fieldLabel : '机构ID',
						name:'srcbranchid',
						allowBlank:false,
						hidden:true
						},{
						xtype:'textfield',
						fieldLabel : '变更前部门',
						width:300,
						name:'srcbranchname',
						allowBlank:false,
						readOnly:true
						},{
							xtype:'button',
							//icon:'../../images/search-trigger.gif',
							glyph:'xf002@FontAwesome',
							handler:function(button,e){
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
								var treePanel = Ext.create('Ext.tree.Panel', {
									bodyPadding: '20 20 0',
									store:treeStore,
									rootVisible: false,
								    aoutLoad:true,
									listeners:{
										itemclick:function(view,record,item,index,e,opt){
											editForm.getForm().findField('srcbranchid').setValue(record.get('id'));
											editForm.getForm().findField('srcbranchname').setValue(record.get('text'));
											editForm.getForm().findField('list').store.removeAll();
											editForm.getForm().findField('list').reset();
											dsFrom.getProxy().extraParams = {
												'branchid' : record.get('id')
											};
											
											editForm.getForm().findField('list').store.load();
											win.close();
										}
									}
								});
								var win = Ext.create('Ext.window.Window', {
								    title: '选择区域',
								    collapsible: true,
								    animCollapse: true,
								    maximizable: true,
								    closeAction:'destory',
								    width: 300,
								    height: 400,
								    minWidth: 300,
								    minHeight: 200,
								    layout: 'fit',
								    items: [treePanel]
								});
								win.showAt(e.getXY());
							}
						
							}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						width:400,
						fieldLabel : '机构ID',
						name:'destbranchid',
						allowBlank:false,
						hidden:true
						},{
						xtype:'textfield',
						fieldLabel : '变更后部门',
						width:300,
						name:'destbranchname',
						allowBlank:false,
						readOnly:true
						},{
							xtype:'button',
							//icon:'../../images/search-trigger.gif',
							glyph:'xf002@FontAwesome',
							handler:function(button,e){
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
								var treePanel = Ext.create('Ext.tree.Panel', {
									bodyPadding: '20 20 0',
									store:treeStore,
									rootVisible: false,
								    aoutLoad:true,
									listeners:{
										itemclick:function(view,record,item,index,e,opt){
											editForm.getForm().findField('destbranchid').setValue(record.get('id'));
											editForm.getForm().findField('destbranchname').setValue(record.get('text'));
											win.close();
										}
									}
								});
								var win = Ext.create('Ext.window.Window', {
								    title: '选择区域',
								    collapsible: true,
								    animCollapse: true,
								    maximizable: true,
								    closeAction:'destory',
								    width: 300,
								    height: 400,
								    minWidth: 300,
								    minHeight: 200,
								    layout: 'fit',
								    items: [treePanel]
								});
								win.showAt(e.getXY());
							}
						
							}]
				}]
			},{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					xtype : 'itemselector',
					columnWidth:0.8,
					name:'list',
		            id: 'list',
		            height:400,
		            fieldLabel: '选择角色',
		            hideLabel : false,
		            imagePath: '../ux/images/',
		            store:dsFrom,
		            displayField: 'accountname',
		            valueField: 'accountid',
		            allowBlank: false,
		            msgTarget: 'side',
		            fromTitle: '可选人员',
		            toTitle: '已选人员'
					}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'保存',
				glyph:'xf0c7@FontAwesome',
				handler:function(){
					if(editForm.getForm().isValid()){
						editForm.getForm().submit({
							url:'../../action/account/batchModifySysAccountBranch',
							success:function(form,action){
								Ext.Msg.alert('信息提示','修改归属部门成功');
							},
							failure:function(form,action){
								Ext.Msg.alert('信息提示','修改归属部门失败');
							}
						})
					}
				}
			},{
				text:'重置',
				glyph:'xf021@FontAwesome',
				handler:function(){
					editForm.getForm().reset();
				}
			}]
			
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'fit',
			items : [ editForm],
			renderTo : Ext.getBody()
			
		})
	}
})