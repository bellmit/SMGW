package star.sms.ip.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.ip.dao.IpWhiteRepository;
import star.sms.ip.domain.IpWhite;
import star.sms.ip.vo.IpWhiteParam;

/**
 * ip白名单配置
 * @author star
 */
@Slf4j
@Service
@Transactional
public class IpWhiteService extends BaseServiceProxy<IpWhite> {
	
	@Resource
	private IpWhiteRepository ipWhiteRepository;
	
	@Resource
	private EntityManager entityManager;
	
	@Override
	protected BaseRepository<IpWhite> getBaseRepository() {
		return ipWhiteRepository;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * 分页查
	 * @param keyword
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page findByPage(String keyword,Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_ip_white a where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append(" and ip like '%"+keyword+"%' ");
		}
		Page<IpWhiteParam> page = super.findPageBySql(IpWhiteParam.class, "select * ", fromWhereSql.toString(), " order by a.ip asc", null, pageable);
		return page;
	}
	
	/**
	 * 登录访问
	 * @return
	 */
	public void vist(String ip) {
		List<IpWhite> list = ipWhiteRepository.findByIp(ip);
		if(list!=null && list.size()>0) {
			for(IpWhite obj:list) {
				Integer visitCount = obj.getVisitCount()==null?0:obj.getVisitCount();
				visitCount++;
				obj.setVisitCount(visitCount);
			}
			super.save(list);
		}
	}
	
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		Query query = entityManager.createQuery("delete from IpWhite where id in ("+ids+")");
		query.executeUpdate();
	}
	
	public boolean isCanaccess(String ip){
        if ("0:0:0:0:0:0:0:1".equals(ip)||"127.0.0.1".equals(ip)||(getLoginUser()!=null&&getLoginUser().getId()==1))return true;//本机和管理员登录直接返回
		List<IpWhite> list = ipWhiteRepository.findByIp(ip);
		if(list!=null && list.size()>0) {
			for(IpWhite obj:list) {
				Integer visitCount = obj.getVisitCount()==null?0:obj.getVisitCount();
				visitCount++;
				obj.setVisitCount(visitCount);
			}
			super.save(list);
			return true;
		}else {
			return false;
		}
    }
}
