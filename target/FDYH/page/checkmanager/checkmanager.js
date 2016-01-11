Ext.application({
	name : '审批账户绑定',
	launch : function() {
		
		var editAction;
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : ['branchid','branch.branchname','accountid','accountname','accounttype','submitid','submitdate','binds'],
			proxy : {
				type : 'ajax',
				actionMethods:{
					create: 'POST', 
					read: 'POST', 
					update: 'POST', 
					destroy: 'POST'
				},
				reader: {  
                    type:'json',
                    root:'items'
     
				},
				url : '../../action/nobindaccount/queryUnCheckBindAccountList'
			}
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 80%',
			store : gridStore,
			flex:2,
			columns : [ {
				header : '管理机构',
				dataIndex : 'branch.branchname',
				width:150
			}, {
				header : '客户账号',
				dataIndex : 'accountid',
				width:200
			}, {
				header : '客户名称',
				flex:1,
				dataIndex : 'accountname'
			},{
				header : '类型',
				dataIndex : 'accounttype',
				width:100,
				renderer:function(value){
					if(value=='1'){
						return '对公活期账户';
					}else if(value=='2'){
						return '对公定期账户';
					}else if(value=='3'){
						return '对公贷款账户 ';
					}else if(value=='4'){
						return '个人活期账户';
					}else if(value=='5'){
						return '个人定期账户';
					}else if(value=='6'){
						return '对公理财账户';
					}else if(value=='7'){
						return '个人理财账户';
					}
				}
			},{
				header : '查看详细',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().reset();
						editForm.getForm().loadRecord(grid.getStore().getAt(rowIndex));
						var binds=grid.getStore().getAt(rowIndex).get('binds');
						bindPanel.removeAll();
						for(var i=0;i<binds.length;i++){
							var bind=binds[i];
							var bindForm=new Ext.create('bindForm');
							var toManager=bind.managername;
							var percent=bind.percent*100;
							var type=bind.type;
							var toBranchname=bind.branchname;
							bindForm.getForm().findField('toManager').setValue(toManager);
							bindForm.getForm().findField('percent').setValue(percent);
							bindForm.getForm().findField('fromType').setValue(type);
							bindForm.getForm().findField('toBranchname').setValue(toBranchname);
							bindForm.id='bindForm'+i;
							bindPanel.insert(i,bindForm);
						}
						editAction=true;
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
					}
				} ]
			}],
			
			dockedItems : [ {
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		});
		Ext.define('bindForm',{
			extend : 'Ext.form.Panel',
			border:false,
			layout:'column',
			name:'bindForm',
			items:[{
				columnWidth:0.25,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '归属机构',
					name:'toBranchname',
					allowBlank:true,
					readOnly:true
					}]
			},{
				columnWidth:0.25,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '归属客户经理',
					readOnly:true,
					name:'toManager'
				}]
			},{
				columnWidth:0.2,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel:'绩效占比',
					width:150,
					lableWidth:80,
					readOnly:true,
					name:'percent'
				}]
			},{
				columnWidth:0.2,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'combobox',
					fieldLabel : '账户来源',
					name:'fromType',
					width:200,
					labelWidth:80,
					displayField:'name',  
			        valueField:'value',
			        forceSelection: true,
			        editable:false,
			        allowBlank:false,
			        readOnly:true,
			        blankText:'请选择账户来源',
		            emptyText:'请选择账户来源',
			        store:{  
			            type:'array',  
			            fields:["value","name"],  
			            data:[  
			                ['1','自营'],  
			                ['2','交办']
			            ] 
			        }
				}]
			}]
		});
		var bindPanel=Ext.create('Ext.form.FieldSet',{
			title:'归属信息',
			border:true
		});
		
		var editForm =Ext.create('Ext.form.Panel',{
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
						xtype:'combobox',
						fieldLabel : '账户类型',
						name:'accounttype',
						width:300,
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:false,
				        allowBlank:false,
				        blankText:'请选择账户类型',
			            emptyText:'请选择账户类型',
			            readOnly:true,
				        store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[  
				                 ['1','对公活期账户'],  
				                ['2','对公定期账户'],
				                ['3','对公贷款账户'],
				                ['4','个人活期账户'],
				                ['5','个人定期账户'],
				                ['6','对公理财账户'],
				                ['7','个人理财账户']
				            ] 
				        }
					}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					hidden:true,
					items:[{
						xtype:'textfield',
						fieldLabel : '机构ID',
						name:'branch.branchid',
						allowBlank:true
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '开户机构',
						name:'branch.branchname',
						width:300,
						allowBlank:false,
						readOnly:true
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '客户账号',
						readOnly:true,
						width:300,
						name:'accountid',
						allowBlank:false
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '账户名称',
						readOnly:true,
						width:300,
						name:'accountname',
						allowBlank:false
						}]
				}]
			},bindPanel],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf00c@FontAwesome',
					text:'审批通过',
					handler:function(){
						if(editForm.getForm().isValid()){
							editForm.getForm().submit({
								url:'../../action/nobindaccount/checkpass',
								params:{
									passed:true
								},
								success:function(form,action){
									Ext.Msg.alert('信息提示', '操作成功');
									editPanel.hide();
									queryPanel.anchor='100% 100%';
									queryPanel.updateLayout();
									queryPanel.show();
									gridStore.load();
								},
								failure:function(form,action){
									Ext.Msg.alert('信息提示','操作失败');
								}
							})
						}
						
					}
				},{
					glyph:'xf00d@FontAwesome',
					text:'审批拒绝',
					handler:function(){
						if(editForm.getForm().isValid()){
							editForm.getForm().submit({
								url:'../../action/nobindaccount/checkpass',
								params:{
									passed:false
								},
								success:function(form,action){
									Ext.Msg.alert('信息提示', '操作成功');
									editPanel.hide();
									queryPanel.anchor='100% 100%';
									queryPanel.updateLayout();
									queryPanel.show();
									gridStore.load();
								},
								failure:function(form,action){
									Ext.Msg.alert('信息提示','操作失败');
								}
							})
						}
						
					}
				},{
					glyph:'xf0e2@FontAwesome',
					text:'返回查询',
					handler:function(){
						editPanel.hide();
						queryPanel.anchor='100% 100%';
						queryPanel.updateLayout();
						queryPanel.show();
					}
				}]
			}]
		});
		
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'20 20 0',
			anchor:'100% 20%',
			paramsAsHash: true,
			layout : 'column',
			items : [ {
				columnWidth:0.3,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '客户账号',
					name:'accountid'
					}]
			},{
				columnWidth:0.3,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '客户名称',
					name:'accountname'
					}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				glyph:'xf002@FontAwesome',
				handler:function(){
					if(queryForm.getForm().isValid()){
						var accountId = queryForm.getForm().findField('accountid').getValue();
						var accountName = queryForm.getForm().findField('accountname').getValue();
						gridStore.getProxy().extraParams = {
							'accountid' : accountId,
							'accountname':accountName
						};
						gridStore.load();
					}
				}
			},{
				text:'重置',
				glyph:'xf021@FontAwesome',
				handler:function(){
					queryForm.getForm().reset();
				}
			}]
		});
		//角色信息列表显示
		var queryPanel = Ext.create('Ext.panel.Panel', {
			name:'queryPanel',
			title : '审批客户经理关系',
			anchor:'100% 100%',
			layout : {
				type:'anchor'
			},
			items : [queryForm,queryGrid]
		});
		
		
	    
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'用户信息管理',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'fit',
			items:[editForm]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [ queryPanel, editPanel ],
			renderTo : Ext.getBody()
			
		})
	}
})