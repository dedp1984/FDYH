

var loginForm=Ext.create('Ext.form.Panel', {
	columnWidth:0.2,
	height:200,
	width:400,
	title:'请登录',
	bodyPading:'100 100 100 100',
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
		labelAlign:'left',
		labelStyle:'font-weight:bold;font-size:15',
		name:'accountid',
		margin:'10 20 10 0',
		fieldLabel:'用户名:',
		allowBlank:false,
		labelWidth:70,
		listeners: {
            specialkey: function(field, e){
                // e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
                // e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP, e.DOWN
                if (e.getKey() == e.ENTER) {
                	loginForm.getForm().findField('password').setValue('');
                	loginForm.getForm().findField('password').focus(true, true); 
                }
            }
        }
	},{
		xtype:'textfield',
		name:'password',
		labelStyle:'font-weight:bold;font-size:15',
		labelAlign:'left',
		fieldLabel:'密码:',
		inputType:'password',
		margin:'10 20 0 0',
		allowBlank:false,
		labelWidth:70,
		listeners: {
            specialkey: function(field, e){
                // e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
                // e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP, e.DOWN
                if (e.getKey() == e.ENTER) {
                	if(loginForm.isValid()){
        				loginForm.submit({
        					waitMsg:'正在登录中',
        					success:function(form, action){
        						window.location = 'index.html' 
        					}
        				});
        			}
                }
            }
        }
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
						window.location = 'index.html';
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
	listeners:{
	    'render' : function() {
	        loginForm.getForm().findField('accountid').focus(true, true); 
	     }
	},
	renderTo : Ext.getBody()
});
var imagePanel=new Ext.create('Ext.panel.Panel',{
	columnWidth:0.8,
	width:400,
	height:250,
	layout:{
		type:'hbox',
		 align: 'center',
	        pack:'center'
	},
	border:false,
	//bodyStyle:'background-color:#B4DE37;',
	items:[loginForm]
});
var headerPanel=new Ext.create('Ext.panel.Panel',{
	anchor:'100% 10%',
	layout:'column',
	border:true,
	width:'100%',
	height:'100%',
	frame:true,
	//bodyStyle:'background-image:url(images//background.png);background-size: 100% 100%;',
	bodyStyle:'background-color:#DFE9F6;',
	items:[ {
    	xtype:'label',
    	glyph:'xf007@FontAwesome',
    	style:'font-size:35px;'
    },{
    	xtype:'label',
    	text:'富滇银行重庆分行客户经理绩效考核系统',
    	margin:'10 0 0 0',
    	style:'font-size:20px;color:#000000;font-weight:bold;font-family:SimHei' 
    }
	       ]
});
var centerPanel=new Ext.create('Ext.panel.Panel',{
	bodyPadding:'50 50 50 50',
	anchor:'100% 80%',
	layout:'column',
	border:false,
	width:'100%',
	height:'100%',
	layout:{
		type:'hbox',
		align:'middle',
		pack:'center'
	},
	items:[loginForm]
});
var footPanel=new Ext.create('Ext.panel.Panel',{
	anchor:'100% 10%',
	layout:'fit',
	border:true,
	width:'100%',
	height:'100%',
	bodyStyle:'background-color:#DFE9F6;',
	items:[{
		xtype:'container',
		layout:{
			type:'vbox',
			align:'center',
			pack:'center'
		},
		items:[{
			xtype:'label',
			text:'建议使用chrome浏览器浏览'
			},{
			xtype:'label',
			html:'<a href="http://172.171.20.10/phpa/chrome.exe">chrome浏览器下载</a>'
			//html:'<div style="font-size:20;"><a href=http://172.171.20.10/phpa/chrome.exe><img src="images/icon-chrome.png" width="17"  height="17"></img>下载</a></div>',
		}]
	}]
	
});
Ext.application({ 
	name : 'HelloExt',
	launch : function() {
		Ext.create('Ext.container.Viewport', {
			layout:'anchor',
			items:[headerPanel,centerPanel,footPanel]
		});
	}
});