
insert into SYS_BRANCH (BRANCHID, BRANCHNAME, PARENTID, PHONE, ADDRESS, ALIAS)
values ('0001', '铜梁区', '0000', null, null, null);
insert into SYS_BRANCH (BRANCHID, BRANCHNAME, PARENTID, PHONE, ADDRESS, ALIAS)
values ('0000', '重庆市', '0', null, null, null);
insert into SYS_BRANCH (BRANCHID, BRANCHNAME, PARENTID, PHONE, ADDRESS, ALIAS)
values ('0003', '永川区', '0000', null, null, null);
insert into SYS_BRANCH (BRANCHID, BRANCHNAME, PARENTID, PHONE, ADDRESS, ALIAS)
values ('0002', '合川区', '0000', null, null, null);
commit;

insert into SYS_ACCOUNT (ACCOUNTID, ACCOUNTNAME, PASSWORD, PROPERTY, BRANCHID, AREAID, BIRTHDAY, ADDRESS, PHONE, EMAIL, STATUS, EXPIREDATE)
values ('xiaoqu2', 'xiaoqu2', '111111', '1', '0003', '29c2f5e5-92b8-4a0d-9eb1-bbd00477a394', null, null, null, null, null, null);
insert into SYS_ACCOUNT (ACCOUNTID, ACCOUNTNAME, PASSWORD, PROPERTY, BRANCHID, AREAID, BIRTHDAY, ADDRESS, PHONE, EMAIL, STATUS, EXPIREDATE)
values ('admin', '系统管理员', '111111', '0', '0000', null, null, null, null, null, null, null);
insert into SYS_ACCOUNT (ACCOUNTID, ACCOUNTNAME, PASSWORD, PROPERTY, BRANCHID, AREAID, BIRTHDAY, ADDRESS, PHONE, EMAIL, STATUS, EXPIREDATE)
values ('developer', '邓攀', '1111', '0', '0000', null, null, null, null, null, null, null);
insert into SYS_ACCOUNT (ACCOUNTID, ACCOUNTNAME, PASSWORD, PROPERTY, BRANCHID, AREAID, BIRTHDAY, ADDRESS, PHONE, EMAIL, STATUS, EXPIREDATE)
values ('xiaoqu1', 'xiaoqu1', '111111', '1', '0001', 'b1318483-1e8d-416e-98d9-d4c2d0c792aa', null, null, null, null, null, null);
commit;

insert into SYS_ROLE (ROLEID, ROLENAME)
values ('3', '小区管理人员');
insert into SYS_ROLE (ROLEID, ROLENAME)
values ('4', '系统管理员');
insert into SYS_ROLE (ROLEID, ROLENAME)
values ('1', '开发人员');
commit;

insert into SYS_ACCOUNT_ROLE (ACCOUNTID, ROLEID)
values ('admin', '4');
insert into SYS_ACCOUNT_ROLE (ACCOUNTID, ROLEID)
values ('developer', '1');
insert into SYS_ACCOUNT_ROLE (ACCOUNTID, ROLEID)
values ('xiaoqu1', '3');
insert into SYS_ACCOUNT_ROLE (ACCOUNTID, ROLEID)
values ('xiaoqu2', '3');
commit;

insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('22', '文件上传', '2', 'page/fileupload/fileupload.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('23', '文档检索', '2', 'page/search/docsearch.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('32', '设备信息管理', '3', 'page/devicemanager/devicemanager.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('33', '业主绑定', '3', 'page/bindmanager/bindmanager.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('1', '系统管理', '0', null, '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('11', '区域管理', '3', 'page/branchmanager/branchmanager.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('12', '角色管理', '1', 'page/rolemanager/rolemanager.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('13', '用户管理', '1', 'page/accountmanager/accountmanager.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('2', '数据查询', '0', null, '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('21', '自助POS交易明细查询', '2', 'page/querymisposdetail/querymisposdetail.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('34', '注册业主管理', '3', 'page/registerownermanager/registerownermanager.html', '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('3', '基础数据设置', '0', null, '0', null);
insert into SYS_MENU (MENUID, MENUNAME, PARENTMENUID, PAGEURL, ISACTION, ACTIONNAME)
values ('31', '小区信息管理', '3', 'page/areamanager/areamanager.html', '0', null);
commit;

insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '1');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '11');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '12');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '13');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '3');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '31');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '32');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '33');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('1', '34');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '1');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '11');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '12');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '13');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '2');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '21');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '22');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '23');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '3');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '31');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '32');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '33');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('3', '34');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '1');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '11');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '12');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '13');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '3');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '31');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '32');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '33');
insert into SYS_ROLE_MENU (ROLEID, MENUID)
values ('4', '34');
commit;

insert into T_AREA (AREAID, AREANAME, ADDRESS, AREAINFO, PHONE, LASTMODIFYUSER, LASTMODIFYDATE, BRANCHID)
values ('e6e7bd19-aab0-4833-8964-0baca6657819', '国宾豪庭', '11', '111', '111', 'developer', '20150416224912', '0000');
insert into T_AREA (AREAID, AREANAME, ADDRESS, AREAINFO, PHONE, LASTMODIFYUSER, LASTMODIFYDATE, BRANCHID)
values ('eae12d58-80d8-433e-a8fc-2c7889b7ed36', '八一小区', '渝中区大坪', '八一小区', '110', 'developer', '20150416224854', '0000');
insert into T_AREA (AREAID, AREANAME, ADDRESS, AREAINFO, PHONE, LASTMODIFYUSER, LASTMODIFYDATE, BRANCHID)
values ('b1318483-1e8d-416e-98d9-d4c2d0c792aa', '营盘新苑', '1123', '营盘新苑', '31321', 'developer', '20150426010109', '0001');
insert into T_AREA (AREAID, AREANAME, ADDRESS, AREAINFO, PHONE, LASTMODIFYUSER, LASTMODIFYDATE, BRANCHID)
values ('29c2f5e5-92b8-4a0d-9eb1-bbd00477a394', '皇宫', '11', '11', '11', 'developer', '20150421002605', '0003');
commit;

insert into T_ENTRY (AREAID, ENTRYID, ENTRYNAME, ENTRYTYPE, FLOORCNT, FLOORROOMCNT, LASTMODIFYUSER, LASTMODIFYTIME)
values ('29c2f5e5-92b8-4a0d-9eb1-bbd00477a394', 'ac5b28c1-3bc9-4a59-8eb3-fc64a652d749', '1栋', '0', '6', '16', 'developer', '20150421002624');
insert into T_ENTRY (AREAID, ENTRYID, ENTRYNAME, ENTRYTYPE, FLOORCNT, FLOORROOMCNT, LASTMODIFYUSER, LASTMODIFYTIME)
values ('b1318483-1e8d-416e-98d9-d4c2d0c792aa', '172d29cb-abfb-40ee-b81e-a355eca9e192', '1栋', '0', '5', '5', 'developer', '20150421002457');
insert into T_ENTRY (AREAID, ENTRYID, ENTRYNAME, ENTRYTYPE, FLOORCNT, FLOORROOMCNT, LASTMODIFYUSER, LASTMODIFYTIME)
values ('eae12d58-80d8-433e-a8fc-2c7889b7ed36', '94ef3c2c-c885-4634-b1cb-346772c079b0', '大门', '1', null, null, 'developer', '20150419171718');
insert into T_ENTRY (AREAID, ENTRYID, ENTRYNAME, ENTRYTYPE, FLOORCNT, FLOORROOMCNT, LASTMODIFYUSER, LASTMODIFYTIME)
values ('eae12d58-80d8-433e-a8fc-2c7889b7ed36', '2a58093e-5c4e-4120-90f6-19342f291e19', '1栋', '0', '10', '8', 'developer', '20150419171601');
insert into T_ENTRY (AREAID, ENTRYID, ENTRYNAME, ENTRYTYPE, FLOORCNT, FLOORROOMCNT, LASTMODIFYUSER, LASTMODIFYTIME)
values ('eae12d58-80d8-433e-a8fc-2c7889b7ed36', 'e0638f2f-8bee-4923-ae56-5c1cbef53c5e', '2栋', '0', '10', '8', 'developer', '20150419171615');
insert into T_ENTRY (AREAID, ENTRYID, ENTRYNAME, ENTRYTYPE, FLOORCNT, FLOORROOMCNT, LASTMODIFYUSER, LASTMODIFYTIME)
values ('e6e7bd19-aab0-4833-8964-0baca6657819', '5829f131-d55f-4968-8a44-587f5f6b0a91', '1栋', '0', '4', '4', 'developer', '20150420224705');
insert into T_ENTRY (AREAID, ENTRYID, ENTRYNAME, ENTRYTYPE, FLOORCNT, FLOORROOMCNT, LASTMODIFYUSER, LASTMODIFYTIME)
values ('e6e7bd19-aab0-4833-8964-0baca6657819', 'f1705cef-e1f1-4d73-ad87-cadda3995df1', '后门', '1', null, null, 'developer', '20150419222601');
commit;

insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('b1318483-1e8d-416e-98d9-d4c2d0c792aa', '172d29cb-abfb-40ee-b81e-a355eca9e192', 'cfe809a9-63c8-42b1-89d2-08d5fe58502a', '11', '11', '11', 'developer', '20150420223439', '11');
insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('29c2f5e5-92b8-4a0d-9eb1-bbd00477a394', 'ac5b28c1-3bc9-4a59-8eb3-fc64a652d749', 'b0081414-9345-4d9b-b68c-a230690aa47e', '11', '11', '1', 'developer', '20150421002632', '11');
insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('e6e7bd19-aab0-4833-8964-0baca6657819', '5829f131-d55f-4968-8a44-587f5f6b0a91', '635e4829-ec2a-4dd8-b310-bdfdf538c7b5', '333', '33111', '33', 'developer', '20150423112714', '1111');
insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('eae12d58-80d8-433e-a8fc-2c7889b7ed36', '2a58093e-5c4e-4120-90f6-19342f291e19', '1e1fb360-4b23-477e-ad15-79c3857a6dd5', 'xiangma', 'android111', 'ios111', 'developer', '20150419171703', '11');
insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('eae12d58-80d8-433e-a8fc-2c7889b7ed36', 'e0638f2f-8bee-4923-ae56-5c1cbef53c5e', '81cb10e0-57c2-4158-b064-ac47c1e54c71', 'xiangma', 'android112', 'ios112', 'developer', '20150419171738', '111');
insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('eae12d58-80d8-433e-a8fc-2c7889b7ed36', '94ef3c2c-c885-4634-b1cb-346772c079b0', '50fc04e9-733e-4f4b-9756-d872ac580cef', 'nantian', 'android113', 'ios113', 'developer', '20150419171759', '11');
insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('e6e7bd19-aab0-4833-8964-0baca6657819', '5829f131-d55f-4968-8a44-587f5f6b0a91', '5aa9ee59-8f80-424f-a425-cbe87e4bc9e3', 'xiangma', '1122', '212', 'developer', '20150419185725', '11');
insert into T_DEVICE (AREAID, ENTRYID, DEVID, DEVNAME, ADRZONEID, IOSZONEID, LASTMODIFYUSER, LASTMODIFYTIME, DEVADDRESS)
values ('e6e7bd19-aab0-4833-8964-0baca6657819', 'f1705cef-e1f1-4d73-ad87-cadda3995df1', '21be24ed-52a9-4c0c-80c4-a26b0e213db3', 'xiangma', '1111', '1111', 'developer', '20150419185745', '11');
commit;

insert into T_OWNER (OWNERID, PASSWORD, CREATETIME)
values ('1111', '12321', '20150111123456');
commit;

insert into T_OWNER_AREA (OWNERID, AREAID, ENTRYID, FLOORNUM, FLOORROOMNUM)
values ('1111', '29c2f5e5-92b8-4a0d-9eb1-bbd00477a394', 'ac5b28c1-3bc9-4a59-8eb3-fc64a652d749', '4', '2');
insert into T_OWNER_AREA (OWNERID, AREAID, ENTRYID, FLOORNUM, FLOORROOMNUM)
values ('1111', 'e6e7bd19-aab0-4833-8964-0baca6657819', '5829f131-d55f-4968-8a44-587f5f6b0a91', '1', '1');
commit;

