
Ext.application({
	name : '理财客户关系管理',
	launch : function() {
		var editAction;
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
		
		
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : ['branchid','branch.branchname','accountid','accountname','accounttype','submitid','submitdate','binds','branch','financeDetail'],
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
				url : '../../action/financeProduct/queryFinanceDetailList'
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
						return;
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
			border:true
		});
		var editForm =Ext.create('Ext.form.Panel',{
			bodyBorder:false,
			paramsAsHash: true,
			anchor:'100% 50%',
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
						fieldLabel : '客户账号',
						width:300,
						name:'accountid',
						allowBlank:false,
						listeners:{
							blur:function(cmp){
								Ext.Ajax.request({
								    url: '../../action/baseAccount/queryBaseAccountByAccountId',
								    params: {
								        'accountid':cmp.getValue()
								    },
								    success: function(response){
					
								        var data = Ext.JSON.decode(response.responseText);
								        if(data.success==false)
								        	return;
								        bindPanel.removeAll();
								        editForm.getForm().findField('accountname').setValue(data.items.accountname);
								        editForm.getForm().findField('type').getStore().load();
								        editForm.getForm().findField('type').setValue(data.items.accounttype);
								        var binds=data.items.binds;
										for(var i=0;i<binds.length;i++){
											var bind=binds[i];
											var bindForm=new Ext.create('bindForm');
											var toBranchid=bind.branchid;
											var toBranchname=bind.branchname;
											var toManager=bind.managerid;
											var percent=parseFloat(bind.percent)*100;
											var type=bind.type;
											
							        		bindForm.getForm().findField('toManager').getStore().getProxy().extraParams = {
												'branchid' : toBranchid
											};
							        		bindForm.getForm().findField('toManager').getStore().load();
							        		
											bindForm.getForm().findField('toBranchid').setValue(toBranchid);
											bindForm.getForm().findField('toBranchname').setValue(toBranchname);
											bindForm.getForm().findField('fromType').setValue(type);
											bindForm.getForm().findField('toManager').setValue(toManager);
											bindForm.getForm().findField('percent').setValue(percent);
											bindForm.getForm().findField('fromType').setValue(type);
											bindForm.id='bindForm'+i;
											bindPanel.insert(i,bindForm);
										}
										bindPanel.setDisabled(true);
								    }
								});
							}
						}
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
			},{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.5,
					layout:'hbox',
					border:false,
					items:[{
						width:300,
						xtype:'combobox',
						fieldLabel : '业务类型',
						name:'type',
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:false,
				        allowBlank:false,
				        blankText:'请选择业务类型',
			            emptyText:'请选择业务类型',
				        store:{  
				            type:'array',  
				            fields:["value","name"],
							proxy:{
								type:'ajax',
								url:'../../action/financeProduct/queryFinanceTypeList',
								actionMethods:{
									create: 'POST', 
									read: 'POST', 
									update: 'POST', 
									destroy: 'POST'
								},
								reader: {  
					                    type:'json',
					                    root:'items'
					            }
							}
				        },
				        listeners:{
				        	expand:function(){
				        		editForm.getForm().findField('type').getStore().load();
					        		
				        	}
				        }
					},{
						width:300,
						xtype:'textfield',
						fieldLabel : '明细ID',
						name:'saleid',
						hidden:true
					}]
					
				},{
					columnWidth:0.5,
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						width:300,
						fieldLabel : '购买渠道',
						name:'channel',
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:false,
				        allowBlank:false,
				        blankText:'请选择购买渠道',
			            emptyText:'请选择购买渠道',
				        store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[  
				                ['1','手机银行'],  
				                ['2','网上银行'],
				                ['3','柜面']
				            ] 
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
						xtype:'combobox',
						fieldLabel : '产品系列',
						name:'productid',
						width:300,
						displayField:'comment',  
				        valueField:'id',
				        forceSelection: true,
				        editable:false,
				        allowBlank:false,
				        blankText:'请选择产品系列',
			            emptyText:'请选择产品系列',
			            store:{
				        	fields: ['id','comment'],
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
								url:'../../action/financeProduct/queryFinanceProductTypeList'
							}
				        },
				        listeners:{
				        	expand:function(){
				        		if(editForm.getForm().findField('type').getValue()=='6'){
				        			editForm.getForm().findField('productid').getStore().getProxy().extraParams = {
										'type' : '1'
									};
				        		}else{
				        			editForm.getForm().findField('productid').getStore().getProxy().extraParams = {
										'type' : '2'
									};
				        		}
				        		editForm.getForm().findField('productid').getStore().load();
				        	}
				        }
					}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '产品批次',
						name:'productbatch',
						width:300,
						allowBlank:false
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
						fieldLabel : '购买金额',
						width:300,
						name:'tranamt',
						allowBlank:false
						},{
							xtype:'label',
							text:'(元)'
						}]
				},{
					columnWidth:0.5,
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						width:300,
						fieldLabel : '是否质押',
						name:'iszy',
						value:'1',
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:false,
				        allowBlank:false,
				        blankText:'请选择是否质押',
			            emptyText:'请选择是否质押',
				        store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[  
				                ['0','是'],  
				                ['1','否']
				            ] 
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
						xtype:'datefield',
						fieldLabel : '起息日',
						name:'startdate',
						allowBlank:false,
						format:'Ymd',
						width:300
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'datefield',
						fieldLabel : '到期日',
						name:'enddate',
						allowBlank:false,
						format:'Ymd',
						width:300
						}]
				}]
			},bindPanel],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf0c7@FontAwesome', 
					text:'保存',
					handler:function(){
						if(editForm.getForm().isValid()){
							var bindLen=bindPanel.items.length;
							var bindlist='';
							var accountid=editForm.getForm().findField('accountid').getValue();
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
							}
							if(totalPercent<100){
								Ext.Msg.alert('信息提示','账户分配比例小于100,请重新分配');
								return;
							}
							editForm.getForm().submit({
								url:editAction==true?'../../action/financeProduct/updateFinanceDetail':'../../action/financeProduct/addFinanceDetail',
								params:{
									bindlist:bindlist,
									accountid:accountid
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
			anchor:'100% 80%',
			store : gridStore,
			flex:2,
			columns : [ Ext.create('Ext.grid.RowNumberer', {
				width : 30
			}), {
				header : '开户机构',
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
				header:'总笔数',
				dataIndex:'financeDetail',
				width:50,
				renderer:function(value){
					return value.length;
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
						
						editForm.getForm().findField('accountid').setDisabled(true);
						var record=grid.getStore().getAt(rowIndex);
						
						var deepCopy= function(source) { 
							var result={};
							for (var key in source) {
							     // result[key] = typeof source[key]==='object'? deepCopy(source[key]): source[key];
								result[key]=source[key];
							   } 
							   return result; 
						}
						var extend=function(o,n,override){
							   for(var p in n)if(n.hasOwnProperty(p) && (!o.hasOwnProperty(p) || override))o[p]=n[p];
						};
						
						var detailList=record.get('financeDetail');
						detailStore.removeAll();
						for(var i=0;i<detailList.length;i++){
							var tmpRecord=deepCopy(record.data);
							tmpRecord.financeDetail=[];
							tmpRecord.financeDetail=detailList[i];
							extend(tmpRecord,detailList[i]);
							detailStore.add(tmpRecord);
						}
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						detailGrid.show();
					}
				} ]
			}],
			dockedItems : [ {
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf067@FontAwesome',
					text:'录入理财产品信息',
					handler:function(){
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						editForm.getForm().reset();
						editForm.getForm().findField('accountid').setDisabled(false);
						bindPanel.removeAll();
						var length=bindPanel.items.length;
						var bindForm=new Ext.create('bindForm');
						bindForm.id='bindForm'+length;
						bindPanel.insert(length,bindForm);
						bindPanel.setDisabled(false);
						bindPanel.doLayout();
						detailGrid.hide();
						editAction=false;
					}
				}]
			},{
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		});
		var queryPanel=Ext.create('Ext.panel.Panel',{
			layout:'anchor',
			title:'理财产品销售信息管理',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		var detailStore = Ext.create('Ext.data.Store', {
			fields : ['branchid','branch.branchname','accountid','accountname','accounttype','submitid','submitdate','binds','branch','financeDetail',
			          'channel','enddate','startdate','tranamt','productbatch','productid','saleid','iszy']
	    });
		var detailGrid=Ext.create('Ext.grid.Panel',{
			anchor:'100% 50%',
			store:detailStore,
			columns:[{
				header : '开户机构',
				dataIndex : 'branch.branchname',
				width:150
			},{
				header : '账号',
				dataIndex : 'accountid',
				width:150
			},{
				header : '户名',
				dataIndex : 'accountname',
				flex:1
			},{
				header:'交易金额',
				dataIndex:'tranamt',
				width:100
			},{
				header:'产品编号',
				dataIndex:'productbatch',
				width:100
			},{
				header:'是否质押',
				dataIndex:'iszy',
				renderer:function(value){
					if(value=='1'){
						return '否';
					}else{
						return '是';
					}
				},
				width:100
			},{
				header:'起息日',
				dataIndex:'startdate',
				width:100,
				 renderer:function(value){
		            	if(value=='')
		            		return '';
		            	
		            	return value.substring(0,10);
		            }
			},{
				header:'到期日',
				dataIndex:'enddate',
				width:100,
				 renderer:function(value){
		            	if(value=='')
		            		return '';
		            	return value.substring(0,10);
		            }
			},{
				header : '编辑理财产品信息',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().findField('accountid').setDisabled(true);
						editForm.getForm().findField('type').getStore().load();
						var record=grid.getStore().getAt(rowIndex);
						editForm.getForm().findField('type').setValue(record.get('accounttype'));
						editForm.getForm().findField('channel').setValue(record.get('financeDetail').channel);
						editForm.getForm().findField('accountid').setValue(record.get('financeDetail').accountid);
						editForm.getForm().findField('accountname').setValue(record.get('accountname'));
						editForm.getForm().findField('productid').getStore().load();
						editForm.getForm().findField('productid').setValue(record.get('financeDetail').productid);
						editForm.getForm().findField('productbatch').setValue(record.get('financeDetail').productbatch);
						editForm.getForm().findField('tranamt').setValue(record.get('financeDetail').tranamt);
						editForm.getForm().findField('iszy').setValue(record.get('financeDetail').iszy);
						editForm.getForm().findField('saleid').setValue(record.get('financeDetail').saleid);
						//var startdate = new Date(record.get('financeDetail').startdate);
						editForm.getForm().findField('startdate').setValue(record.get('financeDetail').startdate.substring(0,10));
						//var enddate = new Date(record.get('financeDetail').enddate);
						editForm.getForm().findField('enddate').setValue(record.get('financeDetail').enddate.substring(0,10));
						bindPanel.removeAll();
						var binds=grid.getStore().getAt(rowIndex).get('binds');
						for(var i=0;i<binds.length;i++){
							var bind=binds[i];
							var bindForm=new Ext.create('bindForm');
							var toBranchid=bind.branchid;
							var toBranchname=bind.branchname;
							var toManager=bind.managerid;
							var percent=parseFloat(bind.percent)*100;
							var type=bind.type;
							
			        		bindForm.getForm().findField('toManager').getStore().getProxy().extraParams = {
								'branchid' : toBranchid
							};
			        		bindForm.getForm().findField('toManager').getStore().load();
			        		
							bindForm.getForm().findField('toBranchid').setValue(toBranchid);
							bindForm.getForm().findField('toBranchname').setValue(toBranchname);
							bindForm.getForm().findField('fromType').setValue(type);
							bindForm.getForm().findField('toManager').setValue(toManager);
							bindForm.getForm().findField('percent').setValue(percent);
							bindForm.getForm().findField('fromType').setValue(type);
							bindForm.id='bindForm'+i;
							bindPanel.insert(i,bindForm);
						}
						bindPanel.setDisabled(true);
						editAction=true;
						//queryPanel.hide();
						//editPanel.anchor='100% 100%';
						//editPanel.updateLayout();
						//editPanel.show();
					}
				} ]
			},{
				header : '删除',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del3.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
					var currentAccountId=grid.getStore().getAt(rowIndex).get('accountid');
					var currentAccountName=grid.getStore().getAt(rowIndex).get('accountname');
					var saleId=grid.getStore().getAt(rowIndex).get('financeDetail').saleid;
					Ext.Msg.show({
						msg:'确定删除【'+currentAccountName+'】?', 
						title:'信息提示', 
						buttons:Ext.Msg.OKCANCEL,
						fn:function(btn, text){
							if (btn == 'ok'){
						    	var request=Ext.Ajax.request({
									params: {
								        accountid: currentAccountId,
								        saleid:saleId
								    },
									url:'../../action/financeProduct/deleteFinanceDetail',
									success:function(response,options){
										Ext.Msg.alert('信息提示', '删除成功');
										editPanel.hide();
										queryPanel.anchor='100% 100%';
										queryPanel.updateLayout();
										queryPanel.show();
										gridStore.load();
									}
								});
						    }
						}
					})
					}
				} ]
			}]
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'理财产品销售信息录入',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'anchor',
			items:[editForm,detailGrid]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel,editPanel],
			renderTo : Ext.getBody()
			
		})
	}
})