package star.sms.group.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.group.dao.GroupDao;
import star.sms.group.domain.Group;

/**
 * 通讯录-通讯录分组
 */
@Service
@Transactional
public class GroupService extends BaseServiceProxy<Group>{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private EntityManager entityManager;
	@Override
	protected BaseRepository<Group> getBaseRepository() {
		return groupDao;
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
	public Page findByPage(String keyword, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_group a where createUserId='"+getLoginUser().getId()+"' ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and groupName like '%"+keyword+"%' ");
		}
		Page<Group> page = super.findPageBySql(Group.class, "select * ", fromWhereSql.toString(), " order by createTime desc", null, pageable);
		return page;
	}
	
	public List<Group> getGroup(){
		return jdbcTemplate.query("select id,groupName from tb_group where createUserId='"+getLoginUser().getId()+"' order by createTime asc ", new BeanPropertyRowMapper<Group>(Group.class));
	}
	
	public List<Group> findByGroupName(String groupName){
		return jdbcTemplate.query("select * from tb_group where groupName='"+groupName+"' and createUserId='"+getLoginUser().getId()+"' order by createTime asc ", new BeanPropertyRowMapper<Group>(Group.class));
	}
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_group where id in ("+ids+")");
		jdbcTemplate.update("delete from tb_group_member where groupId in ("+ids+")");
	}
}
