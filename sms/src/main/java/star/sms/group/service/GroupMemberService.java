package star.sms.group.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.group.dao.GroupMemberDao;
import star.sms.group.domain.GroupMember;
import star.sms.group.vo.GroupMemberParam;

/**
 * 通讯录
 */
@Service
@Transactional
public class GroupMemberService extends BaseServiceProxy<GroupMember>{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private EntityManager entityManager;
	@Override
	protected BaseRepository<GroupMember> getBaseRepository() {
		return groupMemberDao;
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
		fromWhereSql.append(" FROM tb_group_member a,tb_group b where a.groupId=b.id ");
		fromWhereSql.append(" and a.createUserId='"+getLoginUser().getId()+"' and b.createUserId='"+getLoginUser().getId()+"' ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and (a.phone like '%"+keyword+"%' or a.name like '%"+keyword+"%') ");
		}
		Page<GroupMemberParam> page = super.findPageBySql(GroupMemberParam.class, "select a.*,b.groupName ", fromWhereSql.toString(), " order by a.createTime desc", null, pageable);
		return page;
	}
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_group_member where id in ("+ids+")");
	}
    
	/**
     * 批量导入
     * @param list
     */
    public void batchSave(List<GroupMember> list) {
        if(list!=null && list.size()>0){
            final List<GroupMember> tempList = list;
            StringBuffer sb = new StringBuffer();
            sb.append("insert into tb_group_member(groupId, phone, name, birthday, address, ");
    		sb.append("company,qq,memo,createTime,createUserId) " );
			
			sb.append(" values(?,?,?,?,?");
			sb.append(",?,?,?,?,?)");
            jdbcTemplate.batchUpdate(sb.toString(),
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i)
                                throws SQLException {
                        	GroupMember info = tempList.get(i);
                            ps.setInt(1, info.getGroupId()==null?0:info.getGroupId());
                            ps.setString(2, info.getPhone()==null?"":info.getPhone().trim());
                            ps.setString(3, info.getName()==null?"":info.getName().trim());
                            ps.setTimestamp(4, info.getBirthday()==null?null:info.getBirthday());
                            ps.setString(5, info.getAddress()==null?"":info.getAddress().trim());
                            
                            ps.setString(6, info.getCompany()==null?"":info.getCompany().trim());
                            ps.setString(7, info.getQq()==null?"":info.getQq().trim());
                            ps.setString(8, info.getMemo()==null?"":info.getMemo().trim());
                            ps.setTimestamp(9, info.getCreateTime()==null?null:info.getCreateTime());
                            ps.setInt(10, info.getCreateUserId()==null?0:info.getCreateUserId());
                        }
                        public int getBatchSize() {
                            return tempList.size();
                        }
                    });

        }
    }
    
    
    public List<GroupMember> findByGoupIds(String groupIds){
    	return jdbcTemplate.query("select * from tb_group_member where groupId in ("+groupIds+") ", new BeanPropertyRowMapper<GroupMember>(GroupMember.class));
    }
}
