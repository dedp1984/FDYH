﻿
Ext.application({
	name : '文件上传',
	launch : function() {
		var depositForm=Ext.create('Ext.form.Panel',{
			bodyPadding: '10 10 0',
			anchor:'100% 40%',
			name:'上传文件',
			title:'导入文件',
			layout:'form',
			border:true,
			items:[{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.7,
					xtype: 'filefield',
			        name: 'filedqrj',
			        fieldLabel: '对公定期存款日均',
			        msgTarget: 'side',
			        labelWidth:150,
			        allowBlank: false,
			        buttonText: '选择文件...'
				},{
					columnWidth:0.3,
					xtype:'label',
					html:'&nbsp;&nbsp;&nbsp;&nbsp;<a href="../../template/对公定期存款账户日均余额模板.xlsx">对公定期存款账户日均余额模板</a>'
				}]
			},{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.7,
					xtype: 'filefield',
			        name: 'filedqsd',
			        fieldLabel: '对公定期存款时点余额',
			        msgTarget: 'side',
			        labelWidth:150,
			        allowBlank: false,
			        buttonText: '选择文件...'
				},{
					columnWidth:0.3,
					xtype:'label',
					html:'&nbsp;&nbsp;&nbsp;&nbsp;<a href="../../template/对公定期存款账户时点余额模板.xlsx">对公定期存款账户时点余额模板</a>'
				}]
			},{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.7,
					xtype: 'filefield',
			        name: 'filehqrj',
			        fieldLabel: '对公活期存款日均',
			        msgTarget: 'side',
			        labelWidth:150,
			        allowBlank: false,
			        buttonText: '选择文件...'
				},{
					columnWidth:0.3,
					xtype:'label',
					html:'&nbsp;&nbsp;&nbsp;&nbsp;<a href="../../template/对公活期存款账户日均余额模板.xlsx">对公活期存款账户日均余额模板</a>'
				}]
			},{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.7,
					xtype: 'filefield',
			        name: 'filehqsd',
			        fieldLabel: '对公活期存款时点余额',
			        msgTarget: 'side',
			        allowBlank: false,
			        labelWidth:150,
			        buttonText: '选择文件...'
				},{
					columnWidth:0.3,
					xtype:'label',
					html:'&nbsp;&nbsp;&nbsp;&nbsp;<a href="../../template/对公活期存款账户时点余额模板.xlsx">对公活期存款账户时点余额模板</a>'
				}]
			},{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.7,
					xtype: 'datefield',
			        name: 'date',
			        fieldLabel: '数据生成日期',
			        allowBlank: false,
			        labelWidth:150,
			        format:'Ymd'
				}]
			},{
				xtype:'panel',
				layout:'column',
				hidden:true,
				border:false,
				items:[{
					columnWidth:0.5,
					xtype:'combobox',
					fieldLabel : '文件类型',
					name:'type',
					displayField:'name',  
			        valueField:'value',
			        forceSelection: true,
			        editable:false,
			        allowBlank:true,
			        blankText:'请选择文件类型',
		            emptyText:'请选择文件类型',
			        store:{  
			            type:'array',  
			            fields:["value","name"],  
			            data:[  
			                ['1','对公活期账户'],  
			                ['2','对公定期账户'],
			                ['3','对公贷款账户'],
			                ['4','对公活期账户日均'],
			                ['5','对公定期账户日均 '],
			                ['6','对公短期保证贷款日均'],
			                ['7','对公短期抵质押贷款日均'],
			                ['8','对公短期信用贷款日均 '],
			                ['9','对公中长期保证贷款日均'],
			                ['10','对公中长期抵质押贷款日均'],
			                ['11','对公中长期信用贷款日均'],
			                ['12','对公单日存款变动'],
			                ['13','个人定期存款日均'],
			                ['14','个人定期存款时点'],
			                ['15','个人活期存款日均'],
			                ['16','个人活期存款时点'],
			                ['17','个人质押存款年日均']
			            ] 
			        }
				}]
			}],
			buttonAlign:'left',
			buttons:[{
				text:'上传文件',
				handler:function(){
					if(depositForm.getForm().isValid()){
						depositForm.getForm().submit({
							url:'../../action/uploadPublicDeposit',
							waitMsg:'正在上传文件。。。。',
							success:function(p,o){
								alert('上传文件成功');
								gridStore.load();
							},
							failure:function(form,action){
								
								alert('上传文件失败:'+Ext.JSON.decode(action.response.responseText).errors.errmsg);
							}
						})
					}
				}
			}]
		});
		
	
		
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'branchid','branch.branchname','accountid','accountname','accounttype'],
			pageSize:15,
			proxy : {
				type : 'ajax',
				actionMethods:{
					create: 'POST', 
					read: 'POST', 
					update: 'POST', 
					destroy: 'POST'
				},
				reader: {
		            type: 'json',
		            root:'items',
		            totalProperty: 'totalsize'
		        },
		        extraParams:{
		        	accounttypes:'1,2'
		        },
				url : '../../action/noBindAccount/queryNoBindAccountList'
			}
		});
		
		Ext.define('bindForm',{
			extend : 'Ext.form.Panel',
			border:false,
			layout:'column',
			name:'bindForm',
			items:[{
				columnWidth:0.3,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				hidden:true,
				items:[{
					xtype:'textfield',
					fieldLabel : '归属机构',
					name:'toBranchid',
					allowBlank:false
					}]
			},{
				columnWidth:0.25,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '归属机构',
					name:'toBranchname',
					allowBlank:false,
					readOnly:true
					},{
						xtype:'button',
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
											url:'../../action/branch/queryBranchTree'
										}
							});
							var treePanel = Ext.create('Ext.tree.Panel', {
								bodyPadding: '20 20 0',
								store:treeStore,
								rootVisible: false,
							    aoutLoad:true,
								listeners:{
									itemclick:function(view,record,item,index,e,opt){
										var parent=button.findParentByType('form');
										parent.getForm().findField('toBranchid').setValue(record.get('id'));
										parent.getForm().findField('toBranchname').setValue(record.get('text'));
										win.close();
									}
								}
							});
							var win = Ext.create('Ext.window.Window', {
							    title: '选择机构',
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
				columnWidth:0.25,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'combobox',
					fieldLabel : '归属客户经理',
					name:'toManager',
					displayField:'accountname',  
			        valueField:'accountid',
			        forceSelection: true,
			        editable:false,
			        allowBlank:false,
			        autoLoad:false,
			        blankText:'选择归属客户经理',
		            emptyText:'选择归属客户经理',
			        store:{
			        	fields: ['accountid','accountname'],
			        	proxy: {
							type:'ajax',
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
							url:'../../action/account/querySysAccountListByBranchId'
						}
			        },
			        listeners:{
			        	expand:function(){
			        		var parent=this.findParentByType('form');
			        		var branchId=parent.getForm().findField('toBranchid').getValue();
			        		parent.getForm().findField('toManager').getStore().getProxy().extraParams = {
								'branchid' : branchId
							};
			        		parent.getForm().findField('toManager').getStore().load();
			        	}
			        }
				}]
			},{
				columnWidth:0.2,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'numberfield',
					fieldLabel:'绩效占比',
					width:150,
					lableWidth:80,
					name:'percent',
					allowBlank:false,
					value:100,
					minValue: 1,
					maxValue:100
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
			},{
				columnWidth:0.1,  //该列占用的宽度，标识为50％
				layout:{
					type:'hbox',
					pack:'center'
				},
				border:false,
				items:[{
					xtype:'button',
					text:'增加',
					handler:function(){
						var length=bindPanel.items.length;
						var bindForm=new Ext.create('bindForm');
						bindForm.id='bindForm'+length;
						bindPanel.insert(length,bindForm);
						bindPanel.doLayout();
					}
					},{
						xtype:'button',
						text:'删除',
						handler:function(button){
							var parent=button.findParentByType('form');
							bindPanel.remove(parent);
							if(bindPanel.items.length==0){
								var bindForm=new Ext.create('bindForm');
								bindPanel.insert(length,bindForm);
							}
							bindPanel.doLayout();
						}
						}]
			}]
		});

		
		var bindPanel=Ext.create('Ext.form.FieldSet',{
			title:'归属信息',
			hidden:true,
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
				                ['7','个人理财账户'],
				                ['8','个人质押存款账户']
				           
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
						name:'branchid',
						allowBlank:false
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '管理机构',
						name:'branchname',
						width:300,
						allowBlank:false,
						readOnly:true
						},{
							xtype:'button',
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
												url:'../../action/branch/queryBranchTree'
											}
								});
								var treePanel = Ext.create('Ext.tree.Panel', {
									bodyPadding: '20 20 0',
									store:treeStore,
									rootVisible: false,
								    aoutLoad:true,
									listeners:{
										itemclick:function(view,record,item,index,e,opt){
											editForm.getForm().findField('branchid').setValue(record.get('id'));
											editForm.getForm().findField('branchname').setValue(record.get('text'));
											win.close();
										}
									}
								});
								var win = Ext.create('Ext.window.Window', {
								    title: '选择机构',
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
								win.showAt(e.getX()-80,e.getY());
							}
						
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
					//icon:'../../images/backup.gif',

					glyph:'xf0c7@FontAwesome',
					text:'保存',
					handler:function(){
						if(editForm.getForm().isValid()){
							/*
							var bindLen=bindPanel.items.length;
							var bindlist='';
							var totalPercent=0;
							for(var i=0;i<bindLen;i++){
								var bindForm=bindPanel.items.getAt(i).getForm();
								var toBranchid=bindForm.findField('toBranchid').getValue();
								var toManager=bindForm.findField('toManager').getValue();
								var percent=bindForm.findField('percent').getValue();
								totalPercent+=parseInt(percent);
								
								var type=bindForm.findField('fromType').getValue();
								bindlist+=toBranchid+','+toManager+','+percent+','+type+'#';
							}
							if(totalPercent>100){
								Ext.Msg.alert('信息提示','分配比例超过100,请重新分配');
								return;
							};
							if(totalPercent<100){
								Ext.Msg.alert('信息提示','分配比例小于100,请重新分配');
								return;
							};
							*/
							editForm.getForm().submit({
								url:'../../action/noBindAccount/submitBindAccountToBranch',
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
					text:'返回',
					handler:function(){
						editPanel.hide();
						queryPanel.anchor='100% 100%';
						queryPanel.updateLayout();
						queryPanel.show();
					}
				}]
			}]
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 60%',
			store : gridStore,
			title:'未建立客户经理关系账户',
			columns : [ {
				header : '开户机构',
				dataIndex : 'branch.branchname',
				width:250,
				hidden:true
			}, {
				header : '账号',
				width:200,
				dataIndex : 'accountid'
			},  {
				header : '户名',
				dataIndex : 'accountname',
				flex:1
			},{
				header : '账户类型',
				dataIndex : 'accounttype',
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
				},
				width:150
			}, {
				header : '分配管理机构',
				xtype : 'actioncolumn',
				width:100,
				hidden:true,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						var record=grid.getStore().getAt(rowIndex);
						editForm.getForm().reset();
  						editForm.getForm().findField('accountid').setValue(record.get('accountid'));
						editForm.getForm().findField('accountname').setValue(record.get('accountname'));	
						editForm.getForm().findField('accounttype').setValue(record.get('accounttype'));	
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						/*
						bindPanel.removeAll();
						var length=bindPanel.items.length;
						var bindForm=new Ext.create('bindForm');
						bindForm.id='bindForm'+length;
						bindPanel.insert(length,bindForm);
						bindPanel.doLayout();
						*/
					}
				} ]
			}],
			dockedItems : [ {
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ,{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf067@FontAwesome',
					text:'导出EXCEL',
					handler:function(){
						var accounttype='1,2';
						var requestUrl=Ext.String.format(
					            '../../action/export/ExportUnBindAccount?accounttypes={0}',accounttype);
						window.location.href=requestUrl;
						
					}
				}]
			}]

		});
		var queryPanel=Ext.create('Ext.panel.Panel',{
			layout:'anchor',
			anchor:'100% 100%',
			items:[depositForm,queryGrid]
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'客户经理关系建立',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'fit',
			items:[editForm]
		});
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel,editPanel],
			renderTo : Ext.getBody()
			
		});
		gridStore.load();
	}
})