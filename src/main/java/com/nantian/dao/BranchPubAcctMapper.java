package com.nantian.dao;

import com.nantian.domain.BranchPubAcct;
import com.nantian.domain.BranchPubAcctKey;

public interface BranchPubAcctMapper {
    int deleteByPrimaryKey(BranchPubAcctKey key);

    int insert(BranchPubAcct record);

    int insertSelective(BranchPubAcct record);

    BranchPubAcct selectByPrimaryKey(BranchPubAcctKey key);

    int updateByPrimaryKeySelective(BranchPubAcct record);

    int updateByPrimaryKey(BranchPubAcct record);
}