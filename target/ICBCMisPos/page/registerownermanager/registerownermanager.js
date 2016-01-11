Ext.application({
	name : '查询注册用户信息',
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
				url : '../../action/ownermanager/queryOwnerList'
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
				header : '编辑',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						bindGridStore.getProxy().extraParams={
							'ownerid':grid.getStore().getAt(rowIndex).get('ownerid')
						};
						bindGridStore.load();
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
					icon:'../../images/del.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						var ownerid=grid.getStore().getAt(rowIndex).get('ownerid');
						Ext.Msg.show({
							msg:'确定删除【'+ownerid+'】?', 
							title:'信息提示', 
							buttons:Ext.Msg.OKCANCEL,
							fn:function(btn, text){
								if (btn == 'ok'){
							    	var request=Ext.Ajax.request({
										params: {
									        'ownerid': ownerid
									    },
										url:'../../action/ownermanager/deleteOwner',
										success:function(response,options){
											Ext.Msg.alert('信息提示', '删除用户成功');
											gridStore.load();
										},
										failure:function(response,options){
											Ext.Msg.alert('信息提示', '删除用户失败');
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
			anchor:'100% 100%',
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
			} ,{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
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
			boder:false,
			layout : 'form',
			items : [{
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.4,
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '注册用户名',
						name:'ownerid',
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
			title : '查询注册用户信息',
			anchor:'100% 100%',
			layout : {
				type:'anchor'
			},
			items : [queryForm,queryGrid]
		});
		
		
	    
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'用户绑定关系',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'anchor',
			items:[bindGrid]
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