package star.sms.attachment.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.attachment.dao.AttachmentRepository;
import star.sms.attachment.domain.Attachment;

/**
 * 附件信息
 * @author star
 */
@Service
@Transactional
public class AttachmentService extends BaseServiceProxy<Attachment> {
	
	@Resource
	private AttachmentRepository attachmentRepository;
	@Resource
	private EntityManager em;
 

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	@Override
	protected BaseRepository<Attachment> getBaseRepository() {
		return attachmentRepository;
	}
	/**
	 * 相关文档-批量导出
	 */
	@SuppressWarnings("unchecked")
	public List<Attachment> findByRelevent(String att) {
		StringBuffer hql = new StringBuffer();
		hql.append(" from  Attachment    where   id in ("+att+")");
		Query query = em.createQuery(hql.toString());
		return   query.getResultList();
	}
}
