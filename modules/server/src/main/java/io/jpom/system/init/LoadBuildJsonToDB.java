package io.jpom.system.init;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.ds.GlobalDSFactory;
import cn.jiangzeyin.common.DefaultSystemLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jpom.common.Const;
import io.jpom.model.data.BuildInfoModel;
import io.jpom.model.data.RepositoryModel;
import io.jpom.model.vo.BuildModelVo;
import io.jpom.system.ConfigBean;
import io.jpom.system.ServerConfigBean;
import io.jpom.util.JsonFileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hotstrip
 * @date 2021-08-02
 * Auto import build.json data to DB
 */
public class LoadBuildJsonToDB {
	private LoadBuildJsonToDB() {

	}

	/**
	 * 静态内部类实现单例模式
	 */
	public static class LoadBuildJsonToDBHolder {
		private static final LoadBuildJsonToDB INSTANCE = new LoadBuildJsonToDB();
	}

	public static LoadBuildJsonToDB getInstance() {
		return LoadBuildJsonToDBHolder.INSTANCE;
	}

	/**
	 * 把 build.json 文件内容写入到数据库
	 */
	public void doJsonToSql() {
		// 读取 build.json 文件内容
		List<Object> list = readBuildJsonFileToList();
		// 判断 list 是否为空
		if (null == list) {
			DefaultSystemLog.getLog().warn("There is no any data, the build.json file maybe no content or file is not exist...");
			return;
		}
		// 转换成 SQL 执行
		initSql(list);
	}

	/**
	 * list data to SQL
	 * @param list data from build.json
	 */
	private void initSql(List<Object> list) {
		// 加载类里面的属性，用反射获取
		final List<String> repositoryFieldList = getRepositoryFieldList();
		final List<String> buildInfoFieldList = getBuildInfoFieldList();

		// 遍历对象集合
		list.forEach(item -> {
			BuildModelVo buildModelVo = JSON.parseObject(item.toString(), BuildModelVo.class);
			DefaultSystemLog.getLog().info("buildModelVo: {}", JSON.toJSONString(buildModelVo));

			// 拿到构造 SQL 的参数
			Map<String, Object> repositoryParamMap = initSqlParamMap(repositoryFieldList, buildModelVo);
			Map<String, Object> buildInfoParamMap = initSqlParamMap(buildInfoFieldList, buildModelVo);

			// 构造 insert SQL 语句
			String insertRepositorySql = initInsertSql(repositoryParamMap, RepositoryModel.TABLE_NAME);
			String insertBuildInfoSql = initInsertSql(buildInfoParamMap, BuildInfoModel.TABLE_NAME);

			// 插入数据库
			insertToDB(insertRepositorySql);
			insertToDB(insertBuildInfoSql);
		});
	}

	/**
	 * exec insert SQL to DB
	 * @param sql SQL for insert
	 */
	private void insertToDB(String sql) {
		DSFactory dsFactory = GlobalDSFactory.get();
		int rows = 0;
		try {
			rows = Db.use(dsFactory.getDataSource()).execute(sql);
		} catch (SQLException e) {
			DefaultSystemLog.getLog().warn("exec SQL: {} failed, caused: {}...message: {}",
					sql, e.getCause(), e.getMessage());
		}
		DefaultSystemLog.getLog().info("exec SQL: {} complete, and affected rows is: {}",
				sql, rows);
	}

	/**
	 * init insert SQL with param map and table name
	 * @param paramMap
	 * @param tableName
	 * @return
	 */
	private String initInsertSql(Map<String, Object> paramMap, String tableName) {
		// 构造 insert SQL 语句
		StringBuffer sqlBuffer = new StringBuffer("insert into {} ( ");
		StringBuilder sqlFieldNameBuffer = new StringBuilder();
		StringBuilder sqlFieldValueBuffer = new StringBuilder();
		for (int i = 0; i < paramMap.size(); i++) {
			sqlFieldNameBuffer.append("`{}`,");
			sqlFieldValueBuffer.append("'{}',");
		}
		sqlBuffer.append(sqlFieldNameBuffer.substring(0, sqlFieldNameBuffer.length() - 1))
				.append(" )")
				.append(" values ( ")
				.append(sqlFieldValueBuffer.substring(0, sqlFieldValueBuffer.length() - 1))
				.append(" )");

		// 构造 SQL 参数
		List<Object> params = new ArrayList<>();
		params.add(tableName);
		params.addAll(paramMap.keySet());
		params.addAll(paramMap.values());
		return StrUtil.format(sqlBuffer, params.toArray());
	}

	/**
	 * init param map for create insert SQL
	 * @param fieldList
	 * @param buildModelVo
	 * @return
	 */
	private Map<String, Object> initSqlParamMap(List<String> fieldList, BuildModelVo buildModelVo) {
		Map<String, Object> map = new HashMap<>();

		fieldList.forEach(fieldName -> {
			// 判断类里面是否有这个属性
			if (ReflectUtil.hasField(BuildModelVo.class, fieldName)) {
				final String getMethodName = StrUtil.upperFirstAndAddPre(StrUtil.toCamelCase(fieldName), Const.GET_STR);
				Object filedValue = ReflectUtil.invoke(buildModelVo, getMethodName);

				// 添加到参数对象中
				String sqlFiledName = StrUtil.toUnderlineCase(fieldName).toUpperCase();
				map.put(sqlFiledName, filedValue);
			}
		});
		return map;
	}

	/**
	 * read build.json file to list
	 * @return List<Object>
	 */
	private List<Object> readBuildJsonFileToList() {
		File file = FileUtil.file(ConfigBean.getInstance().getDataPath(), ServerConfigBean.BUILD);
		if (!file.exists()) {
			DefaultSystemLog.getLog().error("there is no build.json file...");
			return null;
		}
		try {
			// 读取 build.json 文件里面的内容，转换成实体对象集合
			JSONObject jsonObject = (JSONObject) JsonFileUtil.readJson(file.getAbsolutePath());
			return jsonObject.keySet().stream()
					.map(jsonObject::get)
					.collect(Collectors.toList());
		} catch (FileNotFoundException e) {
			DefaultSystemLog.getLog().error("read build.json file failed...caused: {}...message: {}", e.getCause(), e.getMessage());
		}
		return null;
	}

	/**
	 * 获取 RepositoryModel 类里面的属性，转换成集合返回
	 * @return List<String>
	 */
	private List<String> getRepositoryFieldList() {
		final Field[] fields = ReflectUtil.getFieldsDirectly(RepositoryModel.class, true);
		return Arrays.stream(fields)
				.filter(field -> Modifier.isPrivate(field.getModifiers()))
				.flatMap(field -> Arrays.stream(new String[]{field.getName()}))
				.collect(Collectors.toList());
	}

	/**
	 * 获取 BuildInfoModel 类里面的属性，转换成集合返回
	 * @return List<String>
	 */
	private List<String> getBuildInfoFieldList() {
		final Field[] fields = ReflectUtil.getFieldsDirectly(BuildInfoModel.class, true);
		return Arrays.stream(fields)
				.filter(field -> Modifier.isPrivate(field.getModifiers()))
				.flatMap(field -> Arrays.stream(new String[]{field.getName()}))
				.collect(Collectors.toList());
	}

}
