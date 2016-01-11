
Ext.application({
	name : '理财产品类型管理',
	launch : function() {
		var editAction;
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'20 20 0',
			anchor:'100% 20%',
			paramsAsHash: true,
			layout : 'column',
			items : [ {
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '产品名称',
					name:'name'
					}]
			},{
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'combobox',
					fieldLabel : '所属类型',
					name:'type',
					width:300,
					displayField:'name',  
			        valueField:'value',
			        forceSelection: true,
			        editable:false,
			        blankText:'请选择产品所属类型',
		            emptyText:'请选择产品所属类型',
			        store:{  
			            type:'array',  
			            fields:["value","name"],  
			            data:[  
			                ['1','对公'],  
			                ['2','个人']
			            ] 
			        }
				}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				glyph:'xf002@FontAwesome',
				handler:function(){
					if(queryForm.getForm().isValid()){
						var name = queryForm.getForm().findField('name').getValue();
						var type = queryForm.getForm().findField('type').getValue();
						gridStore.getProxy().extraParams = {
							'name' : name,
							'type':type
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
			fields : ['id','name','type','isjsckye','isjsrj'],
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
				url : '../../action/financeProduct/queryFinanceProductTypeList'
			}
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
					xtype:'textfield',
					name:'id',
					hidden:true
				},{
					columnWidth:0.8,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '所属类型',
						name:'type',
						width:300,
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:false,
				        allowBlank:false,
				        labelWidth:150,
				        blankText:'请选择产品所属类型',
			            emptyText:'请选择产品所属类型',
				        store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[  
				                ['1','对公'],  
				                ['2','个人']
				            ] 
				        }
					}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.8,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '产品名称',
						labelWidth:150,
						name:'name',
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
						xtype:'checkbox',
						fieldLabel : '是否计算余额',
						labelWidth:150,
						boxLabel:'是 ',
						name:'isjsckye',
						inputValue: '1'
						}]
				}]
			}],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf0c7@FontAwesome',
					text:'保存',
					handler:function(){
						if(editForm.getForm().isValid()){
							editForm.getForm().submit({
								url:editAction==true?'../../action/financeProduct/updateFinanceProductType':'../../action/financeProduct/addFinanceProductType',
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
			columns : [  {
				header : 'id',
				dataIndex : 'id',
				hidden:true,
				width:150
			}, {
				header : '所属类型',
				dataIndex : 'type',
				width:200,
				renderer:function(value){
					if(value=='1'){
						return '对公';
					}else{
						return '个人';
					}
				}
			}, {
				header : '产品名称',
				flex:1,
				dataIndex : 'name'
			},{
				header : '是否计算余额',
				dataIndex : 'isjsckye',
				width:150,
				renderer:function(value){
					if(value==true){
						return '是';
					}else{
						return '否';
					}
				}
			},{
				header : '是否计算日均',
				dataIndex : 'isjsrj',
				width:150,
				hidden:true,
				renderer:function(value){
					if(value==true){
						return '是';
					}else{
						return '否';
					}
				}
			},{
				header : '编辑',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().reset();
						editForm.getForm().loadRecord(grid.getStore().getAt(rowIndex));
						editAction=true;
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
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
					var id=grid.getStore().getAt(rowIndex).get('id');
					var name=grid.getStore().getAt(rowIndex).get('name');
					Ext.Msg.show({
						msg:'确定删除【'+name+'】?', 
						title:'信息提示', 
						buttons:Ext.Msg.OKCANCEL,
						fn:function(btn, text){
							if (btn == 'ok'){
						    	var request=Ext.Ajax.request({
									params: {
								        'id': id
								    },
									url:'../../action/financeProduct/deleteFinanceProductType',
									success:function(response,options){
										Ext.Msg.alert('信息提示', '删除成功');
										gridStore.load();
									}
								});
						    }
						}
					})
					}
				} ]
			}],
			
			dockedItems : [ {
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf067@FontAwesome',
					text:'增加理财产品类型',
					handler:function(){
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						editForm.getForm().reset();
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
			title:'理财产品类型管理',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'详细信息',
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
			
		})
	}
})