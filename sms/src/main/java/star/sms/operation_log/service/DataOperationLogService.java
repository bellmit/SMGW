package star.sms.operation_log.service;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.utils.StringUtils;
import star.sms.operation_log.dao.DataOperationLogDao;
import star.sms.operation_log.pojo.DataOperationLog;
import star.sms.platmanager.domain.PlatManager;

/**
 * @author star
 */
@Service
public class DataOperationLogService extends BaseServiceProxy<DataOperationLog> {
	@Resource
	private EntityManager em;
	
	@Autowired
	private DataOperationLogDao dataOperationLogDao;
	
	@Override
	protected BaseRepository<DataOperationLog> getBaseRepository() {
		return dataOperationLogDao;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	/**
	 * 增加
	 * @param dataOperationLog
	 */
	public void add(DataOperationLog dataOperationLog) {
		dataOperationLogDao.save(dataOperationLog);
	}

	/**
	 * 增加
	 * @param dataOperationLog
	 */
	public void add(Integer  module_flag,Integer action_type,String operation, PlatManager platManager) {
		DataOperationLog dataOperationLog = new DataOperationLog();
		dataOperationLog.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
		if(platManager!=null) dataOperationLog.setCreateUserId(platManager.getId());//创建人
		dataOperationLog.setCreateUserName(platManager.getLoginName());//创建人姓名
		dataOperationLog.setActionType(action_type);//操作类型
		dataOperationLog.setModuleFlag(module_flag);
		dataOperationLog.setIsDelete(0);
		dataOperationLog.setOperation(operation);
		dataOperationLogDao.save(dataOperationLog);
	}
	/**
	 * 日志管理
	 * @param keywordType
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	public Page<DataOperationLog> findByPage(Pageable pageable,String keyword, String beginTime,String endTime) {
		List<Object> params = new LinkedList<Object>();
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append("FROM tb_data_operation_log where 1=1 ");
		if (!keyword.equals("all")) {

			fromWhereSql.append(" and   action_type =  " + keyword + "   ");
		}
		if(StringUtils.isNotEmpty(beginTime)) {
			fromWhereSql.append(" and create_time >= '"+beginTime+"'");
		}
		if(StringUtils.isNotEmpty(endTime)) {
			fromWhereSql.append(" and create_time <= '"+endTime+"'");
		}
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT * ");
		String orderBySql = "ORDER BY create_time DESC ";
		// 封装并返回
		return super.findPageBySql(DataOperationLog.class, selectSql.toString(), fromWhereSql.toString(), orderBySql, params, pageable);
	}
}
