package star.sms._frame.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


/**
 * 数据分页
 * @author REN
 *
 */
@Service
public class BaseDao{
	@Autowired
	private JdbcTemplate jdbcTemplate;
    /**
     * 获取sql结果集总数
     * @param sql
     * @return
     */
    public int getCountBySql(String sql) {
        int count = 0;
        count=jdbcTemplate.queryForObject(sql,Integer.class);
        return count;
    }
	/**
	 * 分页
	 */
	public Page findByPage(Pageable pageable, String searchSql,String fileds,String orderStr, Integer totalCount,RowMapper<?> rowMapper) {
		int pageSize=pageable.getPageSize(); //显示条数
		String sql="select "+fileds+" "+ searchSql+" "+orderStr +"limit "+(pageable.getPageNumber())*pageSize+","+(pageable.getPageNumber()+1)*pageSize;
		List<?> list=jdbcTemplate.query(sql, rowMapper);
		Page page = new PageImpl(list, pageable, totalCount.longValue());
		return page;
    }
	/**
	 * 分页
	 */
	public Page findByPage(Pageable pageable, String searchSql,String fileds,String orderStr, Integer totalCount) {
		int pageSize=pageable.getPageSize(); //显示条数
		String sql="select "+fileds+" "+ searchSql+" "+orderStr +"limit "+(pageable.getPageNumber())*pageSize+","+(pageable.getPageNumber()+1)*pageSize;
		List<?> list=jdbcTemplate.queryForList(sql);
		Page page = new PageImpl(list, pageable, totalCount.longValue());
		return page;
	}
}
