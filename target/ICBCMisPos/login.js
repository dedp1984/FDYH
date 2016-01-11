

var loginForm=Ext.create('Ext.form.Panel', {
	columnWidth:0.2,
	height:180,
	width:400,
	title:'安思达门禁管理系统',
	
	border:false,
	 frame: true,
	//bodyStyle:'background-color:#B4DE37;',
	url:'action/login',
	layout :  {
        type: 'vbox',
        align: 'center',
        pack:'center'
    },
	buttonAlign:'center' , 
	items : [{
		xtype:'textfield',
		labelAlign:'right',
		labelStyle:'font-weight:bold;font-size:15',
		name:'accountid',
		margin:'10 20 10 0',
		fieldLabel:'用户名:',
		allowBlank:false,
		labelWidth:70
	},{
		xtype:'textfield',
		name:'password',
		labelStyle:'font-weight:bold;font-size:15',
		labelAlign:'right',
		fieldLabel:'密码:',
		inputType:'password',
		margin:'10 20 0 0',
		allowBlank:false,
		labelWidth:70
	}],
	buttons:[{
		text:'登录',
		width:80,
		iconAlign:'left',
		//iconCls:'fa fa-power-off',
		glyph:'xf007@FontAwesome',
		handler:function(){
			if(loginForm.isValid()){
				loginForm.submit({
					waitMsg:'正在登录中',
					success:function(form, action){
						window.location = 'index.html' 
					}
				});
			}
		}
	},{
		text:'重置',
		width:80,
		glyph:'xf021@FontAwesome',
		handler:function(){
			loginForm.getForm().reset();
		}
	}],
	renderTo : Ext.getBody()
});
var imagePanel=new Ext.create('Ext.panel.Panel',{
	columnWidth:0.8,
	width:400,
	height:250,
	title:'安思达门禁管理系统',
	layout:{
		type:'hbox',
		 align: 'center',
	        pack:'center'
	},
	border:false,
	//bodyStyle:'background-color:#B4DE37;',
	items:[loginForm]
});
var centerPanel=new Ext.create('Ext.panel.Panel',{
	layout:'column',
	border:false,
	width:'100%',
	height:'100%',
	bodyStyle:'background-image:url(images//background.png);background-size: 100% 100%;',
	layout:{
		type:'hbox',
		align:'middle',
		pack:'center'
	},
	items:[loginForm]
});
Ext.application({ 
	name : 'HelloExt',
	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout:{
				type:'hbox',
				align:'middle',
				pack:'end'
			},
			items:[centerPanel]
		});
	}
});