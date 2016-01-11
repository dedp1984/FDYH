Ext.application({
	name : '业主绑定管理',
	launch : function() {
		
		var editAction;
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'ownerid'],
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
		            totalProperty: 'totalsize',
		            root:'items'
		        },
				url : '../../action/ownermanager/queryOwnerByOwnerId'
			}
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 80%',
			store : gridStore,
			flex:2,
			columns : [ Ext.create('Ext.grid.RowNumberer', {
				width : 30
			}), {
				header : '用户名',
				dataIndex : 'ownerid',
				width:300
			},{
				header : '建立绑定',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().reset();
						editForm.getForm().loadRecord(grid.getStore().getAt(rowIndex));
						bindGridStore.getProxy().extraParams={
							'ownerid':editForm.getForm().findField('ownerid').getValue()
						};
						bindGridStore.load();
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
		
		
		var editForm =Ext.create('Ext.form.Panel',{
			anchor:'100% 40%',
			bodyBorder:false,
			paramsAsHash: true,
			layout : 'form',
			bodyPadding:'5 5 0 10',
			items:[{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.3,
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						name:'ownerid',
						readOnly:true,
						fieldLabel:'绑定用户'
					}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'combobox',
						fieldLabel : '所属小区',
						name:'areaid',
						allowBlank:false,
						displayField:'areaname',  
				        valueField:'areaid',
						blankText:'请选择绑定小区',
				        emptyText:'请选择绑定小区',
				        allowBlank:false,
						store:{
					        	fields: ['areaid','areaname'],
					        	proxy: {
									type:'ajax',
									actionMethods:{
										create: 'POST', 
										read: 'POST', 
										update: 'POST', 
										destroy: 'POST'
									},
									reader: {  
						                    type:'json'
						            },
									url:'../../action/areamanager/queryAllSubAreaListByBranchId'
								}
					        },
					        listeners:{
					        	expand:function(){
					        		editForm.getForm().findField('areaid').getStore().load();
					        	},
					        	select:function(){
					        		editForm.getForm().findField('entryid').clearValue();
					        		editForm.getForm().findField('floornum').clearValue();
					        		editForm.getForm().findField('floorroomnum').clearValue();
					        	}
					        }
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'combobox',
						fieldLabel : '所属楼栋',
						name:'entryid',
						displayField:'entryname',  
				        valueField:'entryid',
						blankText:'请选择绑定楼栋',
				        emptyText:'请选择绑定楼栋',
				        allowBlank:false,
						store:{
					        	fields: ['entryid','entryname'],
					        	proxy: {
									type:'ajax',
									actionMethods:{
										create: 'POST', 
										read: 'POST', 
										update: 'POST', 
										destroy: 'POST'
									},
									reader: {  
						                    type:'json'
						            },
									url:'../../action/entrymanager/queryEntryListByAreaId'
								}
					        },
					        listeners:{
					        	expand:function(){
					        		
					        		var areaId=editForm.getForm().findField('areaid').getValue();
					        		editForm.getForm().findField('entryid').getStore().getProxy().extraParams = {
										'areaid' : areaId,
										'entrytype':0
									};
					        		editForm.getForm().findField('entryid').getStore().load();
					        	},
					        	select:function(){
					        		editForm.getForm().findField('floornum').clearValue();
					        		editForm.getForm().findField('floorroomnum').clearValue();
					        	}
					        }
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'combobox',
						fieldLabel : '所属楼层',
						name:'floornum',
						displayField:'floorname',  
				        valueField:'floornum',
						blankText:'请选择所属楼层',
				        emptyText:'请选择所属楼层',
				        allowBlank:false,
						store:{
					        	fields: ['floornum','floorname'],
					        	proxy: {
									type:'ajax',
									actionMethods:{
										create: 'POST', 
										read: 'POST', 
										update: 'POST', 
										destroy: 'POST'
									},
									reader: {  
						                    type:'json'
						            },
									url:'../../action/entrymanager/queryFloorListByEntryId'
								}
					        },
					        listeners:{
					        	expand:function(){
					        		var entryId=editForm.getForm().findField('entryid').getValue();
					        		editForm.getForm().findField('floornum').getStore().getProxy().extraParams = {
										'entryid' : entryId
									};
					        		editForm.getForm().findField('floornum').getStore().load();
					        	},
					        	select:function(){
					        		editForm.getForm().findField('floorroomnum').clearValue();
					        	}
					        }
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'combobox',
						fieldLabel : '所属房号',
						name:'floorroomnum',
						displayField:'floorroomname',  
				        valueField:'floorroomnum',
						blankText:'请选择所属房号',
				        emptyText:'请选择所属房号',
				        allowBlank:false,
						store:{
					        	fields: ['floorroomnum','floorroomname'],
					        	proxy: {
									type:'ajax',
									actionMethods:{
										create: 'POST', 
										read: 'POST', 
										update: 'POST', 
										destroy: 'POST'
									},
									reader: {  
						                    type:'json'
						            },
									url:'../../action/entrymanager/queryFloorRoomListByEntryId'
								}
					        },
					        listeners:{
					        	expand:function(){
					        		var entryId=editForm.getForm().findField('entryid').getValue();
					        		editForm.getForm().findField('floorroomnum').getStore().getProxy().extraParams = {
										'entryid' : entryId
									};
					        		editForm.getForm().findField('floorroomnum').getStore().load();
					        	}
					        }
						}]
				}]
			}],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf0c7@FontAwesome',
					text:'保存绑定关系',
					handler:function(){
						if(editForm.getForm().isValid()){
							editForm.getForm().submit({
								url:'../../action/ownermanager/bindOwnerToEntry',
								success:function(form,action){
									Ext.Msg.alert("信息提示",'绑定成功');
									/*editPanel.hide();
									queryPanel.anchor='100% 100%';
									queryPanel.updateLayout();
									queryPanel.show();
									gridStore.load();*/
									bindGridStore.load();
								},
								failure:function(form,action){
									Ext.Msg.alert('信息提示', action.result.errors.errmsg);
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
		
		var bindGridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'areaid','areaname','entryid','entryname','ownerid','floornum','floorroomnum'],
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
		            totalProperty: 'totalsize',
		            root:'items'
		        },
				url : '../../action/ownermanager/queryOwnerBindList'
			}
		});
		var bindGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 60%',
			store : bindGridStore,
			flex:2,
			columns : [ Ext.create('Ext.grid.RowNumberer', {
				width : 30
			}), {
				header : '小区名称',
				dataIndex : 'areaname',
				flex:1
			}, {
				header : '楼栋',
				dataIndex : 'entryname',
				width:100
			}, {
				header : '楼层单元',
				dataIndex : 'floornum',
				width:100,
				renderer:function(value,metaData,record){
					return record.get('floornum')+'-'+record.get('floorroomnum');
				}
			},{
				header : '删除',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						var areaid=grid.getStore().getAt(rowIndex).get('areaid');
						var entryid=grid.getStore().getAt(rowIndex).get('entryid');
						var ownerid=grid.getStore().getAt(rowIndex).get('ownerid');
						var floornum=grid.getStore().getAt(rowIndex).get('floornum');
						var floorroomnum=grid.getStore().getAt(rowIndex).get('floorroomnum');
						Ext.Msg.show({
							msg:'确定删除【'+ownerid+'】绑定关系?', 
							title:'信息提示', 
							buttons:Ext.Msg.OKCANCEL,
							fn:function(btn, text){
								if (btn == 'ok'){
							    	var request=Ext.Ajax.request({
										params: {
									        'areaid': areaid,
									        'entryid':entryid,
									        'ownerid':ownerid,
									        'floornum':floornum,
									        'floorroomnum':floorroomnum
									    },
										url:'../../action/ownermanager/deleteOwnerBindEntry',
										success:function(response,options){
											Ext.Msg.alert('信息提示', '删除用户绑定关系成功');
											bindGridStore.load();
										},
										failure:function(form,action){
											Ext.Msg.alert('信息提示', action.result.errors.errmsg);
										}
									});
							    }
							}
						})
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
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'0 20 0',
			anchor:'100% 35%',
			paramsAsHash: true,
			boder:false,
			layout : 'form',
			items : [{
				xtype:'fieldset',
				title: '绑定步骤',
				height:80,
			   // collapsible: true,
			    layout: 'anchor',
				items:[{
						anchor:'100% 50%',
						xtype:'text',
						text:'第一步：输入用户注册名点击查询'
					},{
						anchor:'100% 50%',
						xtype:'text',
						text:'第二步：点击绑定执行业主绑定'
					}]
			},{
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.3,
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '注册用户名',
						name:'ownerid',
						allowBlank:false
					}]
				}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				glyph:'xf002@FontAwesome',
				handler:function(){
					if(queryForm.getForm().isValid()){
						var ownerid = queryForm.getForm().findField('ownerid').getValue();
						gridStore.getProxy().extraParams = {
							'ownerid' : ownerid
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
		var queryPanel = Ext.create('Ext.panel.Panel', {
			name:'queryPanel',
			title : '业主绑定',
			anchor:'100% 100%',
			layout : {
				type:'anchor'
			},
			items : [queryForm,queryGrid]
		});
		
		
	    
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'业主绑定',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'anchor',
			items:[editForm,bindGrid]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :{
				type:'anchor'
			},
			items : [ queryPanel, editPanel ],
			renderTo : Ext.getBody()
			
		})
	}
})