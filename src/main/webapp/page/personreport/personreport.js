Ext.tip.QuickTipManager.init();
Ext.application({
	name : '绑定客户管理',
	launch : function() {
		var editAction;
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'10 20 0',
			anchor:'100% 20%',
			paramsAsHash: true,
			layout : 'form',
			items : [{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
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
						allowBlank:false
					}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					hidden:true,
					items:[{
						xtype:'textfield',
						fieldLabel : '机构ID',
						name:'branchid'
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '统计机构',
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
											queryForm.getForm().findField('accountid').getStore().removeAll();
											queryForm.getForm().findField('accountid').setValue('');
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
								win.show(e.getX()-80,e.getY());
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
					fieldLabel : '客户经理',
					name:'accountid',
					width:300,
					displayField:'accountname',  
			        valueField:'accountid',
			        forceSelection: false,
			        value:'',
			        editable:true,
			        allowBlank:true,
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
			        			queryForm.getForm().findField('accountid').getStore().setProxy({
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
				        		queryForm.getForm().findField('accountid').getStore().getProxy().extraParams = {
									'branchid' : branchId,
									'propertys':propertys
								};
				        		queryForm.getForm().findField('accountid').getStore().load();
			        		}						        		
			        	}
			        }
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
						gridStore.setProxy({
							type: 'ajax',
							timeout:300000,
				            url: '../../action/query/QueryPersonBusinessData',
				            reader: {  
			                    type:'json'
							}
						});
						gridStore.getProxy().extraParams = {
							'enddate' : enddate,
							'branchid':branchid,
							'managerid':accountid
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
			fields : ['branchid','branch.branchname','accountid','accountname','accounttype','submitid','submitdate','checkid','checkdate','binds','branch','status'],
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
				url : '../../action/nobindaccount/queryCheckdBindAccountList'
			}
		});

		var gridStore = Ext.create('Ext.data.TreeStore', {
			autoLoad:false,
	        
	        fields: [
	 	            {name: 'name',     type: 'string'},
	 	            {name: 'bal',     type: 'string'},
	 	            {name: 'timebal', type: 'string'},
	 	            {name: 'avg', type: 'string'},
	 	            {name: 'pledgeavg', type: 'string'},
	 	            {name: 'balenddate', type: 'string'},
	 	            {name: 'timebalenddate', type: 'string'},
	 	            {name: 'avgenddate', type: 'string'},
	 	            {name: 'pledgeavgenddate', type: 'string'} ,
	 	            {name: 'avgfixed', type: 'string'} ,
	 	            {name: 'avgcurrent', type: 'string'} ,
	 	            {name: 'avgfinance', type: 'string'} ,
	 	            {name: 'balfixed', type: 'string'} ,
	 	            {name: 'balcurrent', type: 'string'} ,
	 	            {name: 'balfinance', type: 'string'} ,
	 	            {name: 'balpledge', type: 'string'}
	 	        ]
	    });
		var pluginExpanded = true;
		var queryGrid=Ext.create('Ext.tree.Panel', {
			anchor:'100% 80%',
		    title: '查询结果',
		    collapsible: false,
	        useArrows: true,
	        rootVisible: false,
	        store: gridStore,
	        multiSelect: true,
	        columnLines:true,
	        rowLines:true,
	        columns: [{
	            xtype: 'treecolumn', //this is so we know which column will show the tree
	            text: '机构名称',
	            width:200,
	            sortable: true,
	            dataIndex: 'name',
	            locked: true
	        }, {
	            text: '个人存款年日均(合计)',
	            width: 150,
	            dataIndex: 'avg',
	            align:'right',
	            sortable: true,
	            renderer: function (value, meta, record) {
                    meta.tdAttr = 'data-qtip="' + '定期日均:'+record.get('avgfixed') +'活期日均:'+record.get('avgcurrent')+'理财日均:'+record.get('avgfinance') +'"';
                    return value;
                }
	        },{
	            text: '理财存款年日均',
	            width: 120,
	            dataIndex: 'avgfinance',
	            align:'right',
	            sortable: true
	        },{
	            text: '质押存款年日均',
	            width: 120,
	            dataIndex: 'pledgeavg',
	            align:'right',
	            sortable: true
	        },{
	            text: '个人存款时点余额(合计)',
	            width: 150,
	            dataIndex: 'timebal',
	            align:'right',
	            renderer: function (value, meta, record) {
                    meta.tdAttr = 'data-qtip="' + '定期余额:'+record.get('balfixed') +'活期余额:'+record.get('balcurrent')+'理财余额:'+record.get('balfinance')+'"';
                    return value;
                },
	            sortable: true
	        },{
	            text: '理财存款时点余额',
	            width: 120,
	            dataIndex: 'balfinance',
	            align:'right',
	            sortable: true
	        },{
	            text: '质押存款时点余额',
	            width: 120,
	            dataIndex: 'balpledge',
	            align:'right',
	            sortable: true
	        },{
	            text: '截止日期',
	            flex:1,
	            dataIndex: 'timebalenddate',
	            sortable: true
	        }],
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
						var requestUrl=Ext.String.format(
					            '../../action/export/ExportPersonBusinessData?enddate={0}&branchid={1}&managerid={2}',
								enddate,branchid,accountid);
						window.location.href=requestUrl;
						
					}
				}]
			}]
		    
		});
		
		var queryPanel=Ext.create('Ext.panel.Panel',{
			layout:'anchor',
			title:'个人业务统计数据',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel],
			renderTo : Ext.getBody()
			
		});
		
	}
})