package com.nantian.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nantian.domain.PersonPledgeAccount;

public interface PersonPledgeAccountMapper {

	int deleteByPrimaryKey(String saleid);

    int insert(PersonPledgeAccount record);

    int insertSelective(PersonPledgeAccount record);

    PersonPledgeAccount selectByPrimaryKey(String saleid);

    int updateByPrimaryKeySelective(PersonPledgeAccount record);

    int updateByPrimaryKey(PersonPledgeAccount record);
    
    List<PersonPledgeAccount> selectList(@Param("accountid")String accountid,
    								     @Param("accountname")String accountname,
    								     @Param("tobranchid")String toBranchid,
    								     @Param("tomanagerid")String toManagerid,
    								     @Param("modifybranch")String modifyBranch,
    								     @Param("modifyuser")String modifyUser
    								     );
}