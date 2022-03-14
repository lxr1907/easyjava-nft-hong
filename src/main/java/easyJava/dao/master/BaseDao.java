package easyJava.dao.master;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import easyJava.entity.BaseModel;

public interface BaseDao {
    int insertBase(@Param("map") Map map);

    int insertUpdateBase(@Param("map") Map map);

    int insertIgnoreBase(@Param("map") Map map);

    Map selectBaseByPrimaryKey(@Param("id")Long id,@Param("map") Map map);

    int updateBaseByPrimaryKey(@Param("map") Map map);

    List<Map> selectBaseList(@Param("map") Map map, @Param("baseModel") BaseModel baseModel);

    int selectBaseCount(@Param("map") Map map);

    List<Map> selectBaseListOr(@Param("map") Map map, @Param("baseModel") BaseModel baseModel);

    int selectBaseCountOr(@Param("map") Map map);

    List<Map> selectBaseShowTableColumns(@Param("map") Map map);

    List<Map> selectBaseTableName();

}
