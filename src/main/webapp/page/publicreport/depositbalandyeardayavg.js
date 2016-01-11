
Ext.application({
	name : '对公存款年日均余额查询',
	launch : function() {
		var editAction;
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'10 20 0',
			anchor:'100% 30%',
			paramsAsHash: true,
			layout : 'form',
			items : [{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.35,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '查询类型',
						value:'1',
						name:'accounttype',
						width:300,
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:true,
				        allowBlank:true,
				        blankText:'请选择查询类型',
			            emptyText:'请选择查询类型',
			            store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[
				                ['1','对公活期'],  
				                ['2','对公定期']
				            ] 
				        }
					}]
				},{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					hidden:true,
					items:[{
						xtype:'textfield',
						fieldLabel : '机构ID',
						name:'branchid'
						}]
				},{
					columnWidth:0.35,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '归属机构',
						name:'branchname',
						width:300,
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
											queryForm.getForm().findField('branchid').setValue(record.get('id'));
											queryForm.getForm().findField('branchname').setValue(record.get('text'));
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
								win.show(e.getX(),e.getY());
							}
						
							}]
				},{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '客户经理',
						name:'managerid',
						width:300,
						displayField:'accountname',  
				        valueField:'accountid',
				        forceSelection: false,
				        editable:true,
				        allowBlank:true,
				        value:'',
				        blankText:'请选择客户经理',
			            emptyText:'请选择客户经理',
			            store:{
				        	fields: ['accountid','accountname']
				        },
				        listeners:{
				        	expand:function(){
				        		var branchId=queryForm.getForm().findField('branchid').getValue();
				        		if(branchId==''){
				        			return;
				        		}else{
				        			var propertys='1,2,3,5,6';
				        			queryForm.getForm().findField('managerid').getStore().setProxy({
				        				type:'ajax',
										url:'../../action/account/queryAccountList',
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
				        			});
					        		queryForm.getForm().findField('managerid').getStore().getProxy().extraParams = {
										'branchid' : branchId,
										'propertys':propertys
									};
					        		queryForm.getForm().findField('managerid').getStore().load();
				        		}						        		
				        	}
				        }
					}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.35,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '客户账号',
						name:'accountid',
						width:300
					}]
				},{
					columnWidth:0.35,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '客户名称',
						name:'accountname',
						width:300
					}]
				},{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '客户号',
						name:'customno',
						width:300
					}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.35,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '科目号',
						name:'subcode',
						width:300
					}]
				},{
					columnWidth:0.35,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						xtype:'datefield',
						fieldLabel : '截止日期',
						name:'enddate',
						format:'Ymd',
						width:300,
						allowBlank:true
					}]
				},{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '账号性质',
						name:'property',
						width:300
					}]
				}]
			},{
			xtype:'panel',
			border:false,
			layout:'column',
			items:[{
				columnWidth:0.35,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '账户状态',
					name:'status',
					width:300
				}]
			}]
		}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				glyph:'xf002@FontAwesome',
				handler:function(){
					if(queryForm.getForm().isValid()){
						var enddate = Ext.util.Format.date(queryForm.getForm().findField('enddate').getValue(),'Ymd');
						var branchid = queryForm.getForm().findField('branchid').getValue();
						var accountid = queryForm.getForm().findField('accountid').getValue();
						var accountname=queryForm.getForm().findField('accountname').getValue();
						var customno=queryForm.getForm().findField('customno').getValue();
						var subcode=queryForm.getForm().findField('subcode').getValue();
						var property=queryForm.getForm().findField('property').getValue();
						var status=queryForm.getForm().findField('status').getValue();
						var accounttype=queryForm.getForm().findField('accounttype').getValue();
						var managerid=queryForm.getForm().findField('managerid').getValue();
						gridStore.getProxy().extraParams = {
							'enddate' : enddate,
							'branchid':branchid,
							'accountid':accountid,
							'accountname':accountname,
							'customno':customno,
							'subcode':subcode,
							'property':property,
							'status':status,
							'accounttype':accounttype,
							'managerid':managerid
						};
						gridStore.currentPage=1;
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
			autoLoad:false,
			pageSize:15,
			proxy:{
				type: 'ajax',
				actionMethods:{
					create: 'POST', 
					read: 'POST', 
					update: 'POST', 
					destroy: 'POST'
				},
	            url: '../../action/query/queryPublicDepositBalAndYearDayAvg',
	            reader: {  
                    type:'json',
                    root:'items',
                    totalProperty: 'totalsize'
				}
			},
	        fields: [
	 	            {name: 'branchid',     type: 'string'},
	 	            {name: 'branchname',     type: 'string'},
	 	            {name: 'accountid', type: 'string'},
	 	            {name: 'accountname', type: 'string'},
	 	            {name: 'bal', type: 'string'},
	 	            {name: 'gendate', type: 'string'},
	 	            {name: 'subcode', type: 'string'},
	 	            {name: 'customno', type: 'string'},
	 	            {name:'yeardayavg',type:'string'},
	 	            {name:'yeardayavg1',type:'string'},
	 	            {name:'balgendate',type:'string'},
	 	            {name:'avggendate',type:'string'},
	 	            {name:'binds',type:'array'}
	 	        ]
	    });

		var queryGrid=Ext.create('Ext.grid.Panel', {
			anchor:'100% 70%',
		    title: '查询结果',
	        store: gridStore,
	        columns: [{
	            text: '机构名称',
	            width: 100,
	            dataIndex: 'branchname'
	        }, {
	            text: '客户账号',
	            width: 150,
	            dataIndex: 'accountid'
	        },{
	            text: '客户名称',
	            flex:150,
	            dataIndex: 'accountname'
	        },{
	            text: '客户号',
	            width:170,
	            dataIndex: 'customno'
	        },{
	            text: '科目号',
	            width:70,
	            dataIndex: 'subcode'
	        },{
	            text: '时点余额',
	            width: 100,
	            dataIndex: 'bal',
	            align:'right'
	        },{
	            text: '进度日均',
	            width: 100,
	            dataIndex: 'yeardayavg',
	            align:'right'
	        },{
	            text: '年日均',
	            width: 100,
	            dataIndex: 'yeardayavg1',
	            align:'right'
	        },{
	            text: '截止日期',
	            width: 80,
	            dataIndex: 'balgendate',
	            renderer:function(value){
	            	if(value=='')
	            		return '';
	            	return value.substring(0,10);
	            }
	        },{
	            text: '截止日期',
	            width: 80,
	            hidden:true,
	            dataIndex: 'avggendate',
	            renderer:function(value){
	            	if(value=='')
	            		return '';
	            	return value.substring(0,10);
	            }
	        }],
	        plugins: [{
	            ptype: 'rowexpander',
	            rowBodyTpl :['<div id="{accountid}">', 
	                         '</div>' ]
	        }],
	        collapsible: true,
	        animCollapse: false,
	        
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf067@FontAwesome',
					text:'导出EXCEL',
					handler:function(){
						var enddate = Ext.util.Format.date(queryForm.getForm().findField('enddate').getValue(),'Ymd');
						var branchid = queryForm.getForm().findField('branchid').getValue();
						var accountid = queryForm.getForm().findField('accountid').getValue();
						var accountname=queryForm.getForm().findField('accountname').getValue();
						var customno=queryForm.getForm().findField('customno').getValue();
						var subcode=queryForm.getForm().findField('subcode').getValue();
						var property=queryForm.getForm().findField('property').getValue();
						var status=queryForm.getForm().findField('status').getValue();
						var accounttype=queryForm.getForm().findField('accounttype').getValue();
						var managerid=queryForm.getForm().findField('managerid').getValue();
						var requestUrl=Ext.String.format(
					            '../../action/export/exportPublicDepositFile?enddate={0}&branchid={1}&accountid={2}'+
								'&accountname={3}&customno={4}&subcode={5}&property={6}&status={7}&accounttype={8}&managerid={9}',
								enddate,branchid,accountid,accountname,customno,subcode,property,status,accounttype,managerid);
						window.location.href=requestUrl;
						
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
			title:'对公存款年日均余额查询',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel],
			renderTo : Ext.getBody()
			
		});
		
		queryGrid.view.on('expandBody', function (rowNode, record, expandRow, eOpts) {  
			    if(record.get('binds').length>0){
			    	innerStore=Ext.create('Ext.data.Store', {
						fields:['branchname','managerid','managername','percent']
					});
					innerStore.loadData(record.get('binds'));
			        innerGrid=Ext.create('Ext.grid.Panel',{
			        	store:innerStore,
			        	columns:[{
			        		text:'归属机构',
			        		width:300,
			        		dataIndex:'branchname'
			        	},{
			        		text:'归属客户经理姓名',
			        		width:300,
			        		dataIndex:'managername'
			        	},{
			        		text:'归属客户经理工号',
			        		width:300,
			        		dataIndex:'managerid'
			        	},{
			        		text:'分配比例',
			        		width:100,
			        		dataIndex:'percent',
			        		renderer:function(value){
			        			return parseFloat(value)*100+"%";
			        		}
			        	}],
			        	width:'99%',
			        	renderTo:record.get('accountid')
			        })
			    }else{
			    	var parent = document.getElementById(record.get('accountid'));  
			    	
			    	parent.innerHTML='<span style="color:red">未分配指定客户经理</span>';
			    }
				
		});  
		queryGrid.view.on('collapsebody', function (rowNode, record, expandRow, eOpts) {  
			var parent = document.getElementById(record.get('accountid'));  
		    var child = parent.firstChild;  
		    while (child) {  
		        child.parentNode.removeChild(child);  
		        child = child.nextSibling;  
		    }  
	    });  
	}
})