package star.sms.sms.service;

import java.sql.Timestamp;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.sms.dao.SmsBatchDao;
import star.sms.sms.domain.SmsBatch;

/**
 * 短信批次
 */
@Service
@Transactional
public class SmsBatchService extends BaseServiceProxy<SmsBatch>{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SmsBatchDao smsBatchDao;
	@Override
	protected BaseRepository<SmsBatch> getBaseRepository() {
		return smsBatchDao;
	}
	/**
	 * 插入短信批次表
	 * @param smsBatch
	 */
	public void insertSmsBatch(SmsBatch smsBatch) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		StringBuilder sbInsert= new StringBuilder("");
		sbInsert.append(" insert into tb_sms_batch(id,taskId,content,sendStatus,sendTime,createTime,createUserId,updateTime,updateUserId) values(?,?,?,?,?,?,?,?,?)");
		Object[] smsBatchArray= {smsBatch.getId(),smsBatch.getTaskId(),smsBatch.getContent(),0,now,now,smsBatch.getCreateUserId(),now,smsBatch.getCreateUserId()};
		jdbcTemplate.update(sbInsert.toString(), smsBatchArray);
	}
	/**
	 * 更新批次表
	 * @param smsBatch
	 */
	public void updateSmsBatch(SmsBatch smsBatch) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		StringBuilder sbUpdate= new StringBuilder("");
		sbUpdate.append("update tb_sms_batch set sendStatus=?,sendResult=?,updateTime=? where id=? ");
		Object[] smsBatchArray= {smsBatch.getSendStatus(),smsBatch.getSendResult(),now,smsBatch.getId()};
		jdbcTemplate.update(sbUpdate.toString(), smsBatchArray);
	}
	
}
