package com.nantian.dao;

import com.nantian.domain.ImportRecord;
import com.nantian.domain.ImportRecordKey;

public interface ImportRecordMapper {
    int deleteByPrimaryKey(ImportRecordKey key);

    int insert(ImportRecord record);

    int insertSelective(ImportRecord record);

    ImportRecord selectByPrimaryKey(ImportRecordKey key);

    int updateByPrimaryKeySelective(ImportRecord record);

    int updateByPrimaryKey(ImportRecord record);
}