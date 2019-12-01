package easyJava.dao.master;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import easyJava.entity.BaseModel;

public interface BaseDao {
	int insertBase(@Param("map") Map map);

	Map selectBaseByPrimaryKey(Long id);

	int updateBaseByPrimaryKey(@Param("map") Map map);

	List<Map> selectBaseList(@Param("map") Map map, @Param("baseModel") BaseModel baseModel);

	int selectBaseCount(@Param("map") Map map);

	List<Map> selectBaseShowTableColumns(@Param("map") Map map);

	List<Map> selectBaseTableName();

}
