/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015-04-27 12:53:23                          */
/*==============================================================*/


drop table if exists SYS_ACCOUNT;

drop table if exists SYS_ACCOUNT_ROLE;

drop table if exists SYS_BRANCH;

drop table if exists SYS_MENU;

drop table if exists SYS_ROLE;

drop table if exists SYS_ROLE_MENU;

drop table if exists T_AREA;

drop table if exists T_DEVICE;

drop table if exists T_ENTRY;

drop table if exists T_OWNER;

drop table if exists T_OWNER_AREA;

/*==============================================================*/
/* Table: SYS_ACCOUNT                                           */
/*==============================================================*/
create table SYS_ACCOUNT
(
   ACCOUNTID            varchar(20) not null,
   ACCOUNTNAME          varchar(40) not null,
   PASSWORD             varchar(20) not null,
   PROPERTY             varchar(5),
   BRANCHID             varchar(20) not null,
   AREAID               varchar(36),
   BIRTHDAY             date,
   ADDRESS              varchar(100),
   PHONE                varchar(20),
   EMAIL                varchar(40),
   STATUS               varchar(2),
   EXPIREDATE           date,
   primary key (ACCOUNTID)
);

alter table SYS_ACCOUNT comment '账户信息';

/*==============================================================*/
/* Table: SYS_ACCOUNT_ROLE                                      */
/*==============================================================*/
create table SYS_ACCOUNT_ROLE
(
   ACCOUNTID            varchar(20) not null,
   ROLEID               varchar(10) not null,
   primary key (ACCOUNTID, ROLEID)
);

alter table SYS_ACCOUNT_ROLE comment '用户角色信息表';

/*==============================================================*/
/* Table: SYS_BRANCH                                            */
/*==============================================================*/
create table SYS_BRANCH
(
   BRANCHID             varchar(20) not null,
   BRANCHNAME           varchar(100) not null,
   PARENTID             varchar(20) not null,
   PHONE                varchar(20),
   ADDRESS              varchar(100),
   ALIAS                varchar(50),
   primary key (BRANCHID)
);

alter table SYS_BRANCH comment '机构信息表';

/*==============================================================*/
/* Table: SYS_MENU                                              */
/*==============================================================*/
create table SYS_MENU
(
   MENUID               varchar(20) not null,
   MENUNAME             varchar(40) not null,
   PARENTMENUID         varchar(20) not null,
   PAGEURL              varchar(100),
   ISACTION             varchar(2) not null,
   ACTIONNAME           varchar(100),
   primary key (MENUID)
);

alter table SYS_MENU comment '系统菜单';

/*==============================================================*/
/* Table: SYS_ROLE                                              */
/*==============================================================*/
create table SYS_ROLE
(
   ROLEID               varchar(10) not null,
   ROLENAME             varchar(40) not null,
   primary key (ROLEID)
);

alter table SYS_ROLE comment '角色信息表';

/*==============================================================*/
/* Table: SYS_ROLE_MENU                                         */
/*==============================================================*/
create table SYS_ROLE_MENU
(
   ROLEID               varchar(10) not null,
   MENUID               varchar(20) not null,
   primary key (ROLEID, MENUID)
);

alter table SYS_ROLE_MENU comment '角色权限表';

/*==============================================================*/
/* Table: T_AREA                                                */
/*==============================================================*/
create table T_AREA
(
   AREAID               varchar(36) not null,
   BRANCHID             varchar(20) not null,
   AREANAME             varchar(40) not null,
   ADDRESS              varchar(100),
   AREAINFO             varchar(1000),
   PHONE                varchar(100),
   LASTMODIFYUSER       varchar(40),
   LASTMODIFYDATE       varchar(14),
   primary key (AREAID)
);

alter table T_AREA comment '小区信息';

/*==============================================================*/
/* Table: T_DEVICE                                              */
/*==============================================================*/
create table T_DEVICE
(
   AREAID               varchar(36) not null,
   ENTRYID              varchar(36) not null,
   DEVID                varchar(36) not null,
   DEVNAME              varchar(40),
   ADRZONEID            varchar(40),
   IOSZONEID            varchar(40),
   DEVADDRESS           varchar(50) not null,
   LASTMODIFYUSER       varchar(40),
   LASTMODIFYTIME       varchar(14),
   primary key (AREAID, ENTRYID, DEVID)
);

alter table T_DEVICE comment '蓝牙设备管理';

/*==============================================================*/
/* Table: T_ENTRY                                               */
/*==============================================================*/
create table T_ENTRY
(
   AREAID               varchar(36) not null,
   ENTRYID              varchar(36) not null,
   ENTRYNAME            varchar(40) not null,
   ENTRYTYPE            varchar(1) not null,
   FLOORCNT             varchar(2),
   FLOORROOMCNT         varchar(2),
   LASTMODIFYUSER       varchar(40),
   LASTMODIFYTIME       varchar(14),
   primary key (ENTRYID),
   key AK_Key_2 (AREAID, ENTRYNAME)
);

alter table T_ENTRY comment '小区入口管理';

/*==============================================================*/
/* Table: T_OWNER                                               */
/*==============================================================*/
create table T_OWNER
(
   OWNERID              varchar(40) not null,
   PASSWORD             varchar(40) not null,
   CREATETIME           varchar(14) not null,
   primary key (OWNERID)
);

alter table T_OWNER comment '业主信息';

/*==============================================================*/
/* Table: T_OWNER_AREA                                          */
/*==============================================================*/
create table T_OWNER_AREA
(
   OWNERID              varchar(40) not null,
   AREAID               varchar(36) not null,
   ENTRYID              varchar(36) not null,
   FLOORNUM             varchar(2) not null,
   FLOORROOMNUM         varchar(2) not null,
   primary key (OWNERID, AREAID, ENTRYID, FLOORNUM, FLOORROOMNUM)
);

alter table SYS_ACCOUNT add constraint FK_Reference_1 foreign key (BRANCHID)
      references SYS_BRANCH (BRANCHID) on delete restrict on update restrict;

alter table SYS_ACCOUNT_ROLE add constraint FK_Reference_3 foreign key (ROLEID)
      references SYS_ROLE (ROLEID) on delete restrict on update restrict;

alter table SYS_ACCOUNT_ROLE add constraint FK_Reference_6 foreign key (ACCOUNTID)
      references SYS_ACCOUNT (ACCOUNTID) on delete restrict on update restrict;

alter table SYS_ROLE_MENU add constraint FK_Reference_4 foreign key (ROLEID)
      references SYS_ROLE (ROLEID) on delete restrict on update restrict;

alter table SYS_ROLE_MENU add constraint FK_Reference_5 foreign key (MENUID)
      references SYS_MENU (MENUID) on delete restrict on update restrict;

alter table T_DEVICE add constraint FK_ReferenceDeviceEntry foreign key (ENTRYID)
      references T_ENTRY (ENTRYID) on delete restrict on update restrict;

alter table T_ENTRY add constraint FK_ReferenceEntryArea foreign key (AREAID)
      references T_AREA (AREAID) on delete restrict on update restrict;

alter table T_OWNER_AREA add constraint FK_ReferenceOwnerArea foreign key (AREAID)
      references T_AREA (AREAID) on delete restrict on update restrict;

alter table T_OWNER_AREA add constraint FK_ReferenceOwnerEntry foreign key (ENTRYID)
      references T_ENTRY (ENTRYID) on delete restrict on update restrict;

alter table T_OWNER_AREA add constraint FK_ReferenceOwnerOwner foreign key (OWNERID)
      references T_OWNER (OWNERID) on delete restrict on update restrict;

