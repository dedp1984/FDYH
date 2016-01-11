
Ext.application({
	name : '对公存款变动明细',
	launch : function() {
		var editAction;
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'20 20 0',
			anchor:'100% 25%',
			paramsAsHash: true,
			layout : 'form',
			items : [{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
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
						fieldLabel : '查询机构',
						labelWidth:120,
						name:'branchname',
						width:300,
						readOnly:true,
						allowBlank:true
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
								win.showAt(e.getXY());
							}
						
							}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '账户名称',
						name:'accountname',
						width:300
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
					fieldLabel : '开始日期',
					labelWidth:120,
					name:'startdate',
					format:'Ymd',
					width:300
				}]
			},{
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'datefield',
					fieldLabel : '结束日期',
					name:'enddate',
					format:'Ymd',
					width:300
				}]
			}]
		},{
			xtype:'panel',
			border:false,
			layout:'column',
			items:[{
				columnWidth:1,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '最小净流入流出金额',
					name:'minnetamt',
					labelWidth:120,
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
						var startdate= Ext.util.Format.date(queryForm.getForm().findField('startdate').getValue(),'Ymd');
						var enddate = Ext.util.Format.date(queryForm.getForm().findField('enddate').getValue(),'Ymd');
						var branchid = queryForm.getForm().findField('branchid').getValue();
						var accountname = queryForm.getForm().findField('accountname').getValue();
						var minnetamt = queryForm.getForm().findField('minnetamt').getValue();
						gridStore.getProxy().extraParams = {
							'branchid':branchid,
							'accountname':accountname,
							'minnetamt':minnetamt,
							'startdate':startdate,
							'enddate' : enddate
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
		
		var detailGridStore=Ext.create('Ext.data.Store', {
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
	            url: '../../action/query/queryPublicDepositChangeDetail',
	            reader: {  
                    type:'json',
                    root:'items',
                    totalProperty: 'totalsize'
				}
			},
	        fields: [
	 	            {name: 'branchname', type: 'string'},
	 	            {name: 'accountname', type: 'string'},
	 	            {name: 'dqye',     type: 'string'},
	 	            {name: 'netinout', type: 'string'},
	 	            {name: 'enddate', type: 'string'},
	 	            {name: 'tolastday', type: 'string'},
	 	            {name: 'tolastyear', type: 'string'},
	 	            {name: 'inamt', type: 'string'},
	 	            {name: 'outamt', type: 'string'}
	 	        ]
	    });
		var detailGrid=Ext.create('Ext.grid.Panel', {
			anchor:'100% 100%',
	        store: detailGridStore,
	        columns: [{
	            text: '机构名称',
	            width: 150,
	            sortable: true,
	            dataIndex: 'branchname'
	        }, {
	            text: '户名',
	            flex:1,
	            dataIndex: 'accountname',
	            sortable: true,
	            border:true
	        },{
	            text: '当前余额',
	            width: 120,
	            dataIndex: 'dqye',
	            sortable: true,
	            border:true,
	            align:'right',
	            hidden:true
	        },{
	            text: '比上日',
	            width: 120,
	            dataIndex: 'tolastday',
	            sortable: true,
	            align:'right',
	            hidden:true
	        },{
	            text: '比上年',
	            width: 120,
	            dataIndex: 'tolastyear',
	            sortable: true,
	            align:'right',
	            hidden:true
	        }, {
	            text: '流入',
	            width: 120,
	            dataIndex: 'inamt',
	            sortable: true,
	            align:'right'
	        },{
	            text: '流出',
	            width: 120,
	            dataIndex: 'outamt',
	            sortable: true,
	            align:'right'
	        },{
	            text: '净流入流出金额',
	            width: 120,
	            dataIndex: 'netinout',
	            sortable: true,
	            align:'right'
	        },{
	            text: '日期',
	            width: 80,
	            dataIndex: 'enddate',
	            renderer:function(value){
	            	return value.substring(0,10);
	            },
	            sortable: true
	        }],
	        dockedItems : [{
	       				xtype:'toolbar',
	    				dock:'top',
	    				items:['->',{
	    					glyph:'xf0e2@FontAwesome',
	    					text:'返回',
	    					handler:function(){
	    						editPanel.hide();
	    						queryPanel.anchor='100% 100%';
	    						queryPanel.updateLayout();
	    						queryPanel.show();
	    					}
	    				}]
	    			},{
				xtype : 'pagingtoolbar',
				store : detailGridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		    
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
	            url: '../../action/query/queryPublicDepositChangeList',
	            reader: {  
                    type:'json',
                    root:'items',
                    totalProperty: 'totalsize'
				}
			},
	        fields: [
	 	            {name: 'branchname', type: 'string'},
	 	            {name: 'accountname', type: 'string'},
	 	            {name: 'dqye',     type: 'string'},
	 	            {name: 'netinout', type: 'string'},
	 	            {name: 'enddate', type: 'string'},
	 	            {name: 'inamt', type: 'string'},
	 	            {name: 'outamt', type: 'string'}
	 	        ]
	    });

		var queryGrid=Ext.create('Ext.grid.Panel', {
			anchor:'100% 75%',
		    title: '查询结果',
	        store: gridStore,
	        columns: [{
	            text: '机构名称',
	            width: 150,
	            sortable: true,
	            dataIndex: 'branchname'
	        }, {
	            text: '户名',
	            flex:1,
	            dataIndex: 'accountname',
	            sortable: true,
	            border:true
	        },{
	            text: '当前余额',
	            width: 200,
	            dataIndex: 'dqye',
	            sortable: true,
	            border:true,
	            align:'right',
	            hidden:true
	        },{
	            text: '流入合计',
	            width: 170,
	            dataIndex: 'inamt',
	            sortable: true,
	            align:'right'
	        },{
	            text: '流出合计',
	            width: 170,
	            dataIndex: 'outamt',
	            sortable: true,
	            align:'right'
	        }, {
	            text: '净流入流出金额合计',
	            width: 170,
	            dataIndex: 'netinout',
	            sortable: true,
	            align:'right'
	        },{
	            text: '截止日期',
	            width: 100,
	            dataIndex: 'enddate',
	            renderer:function(value){
	            	return value.substring(0,10);
	            },
	            sortable: true
	        },{
				header : '查看详细',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						var record=grid.getStore().getAt(rowIndex);
						var branchid=record.get("branchid");
						var accountname=record.get("accountname");
						var startdate= Ext.util.Format.date(queryForm.getForm().findField('startdate').getValue(),'Ymd');
						var enddate = Ext.util.Format.date(queryForm.getForm().findField('enddate').getValue(),'Ymd');
						var minnetamt = queryForm.getForm().findField('minnetamt').getValue();
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						detailGrid.show();
						detailGridStore.getProxy().extraParams = {
							'branchid':branchid,
							'accountname':accountname,
							'minnetamt':minnetamt,
							'startdate':startdate,
							'enddate' :enddate
						};
						detailGridStore.load();
					}
				} ]
			}],
	        dockedItems : [{
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		    
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'对公存款余额变更明细',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'anchor',
			items:[detailGrid]
		});
		var queryPanel=Ext.create('Ext.panel.Panel',{
			layout:'anchor',
			title:'对公存款变动明细查询',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel,editPanel],
			renderTo : Ext.getBody()
			
		})
	}
})