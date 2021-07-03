package star.sms._frame.base;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import star.sms._frame.config.LoginUser;
import star.sms._frame.utils.Testtrans;
import star.sms.platmanager.domain.PlatManager;

/**
 * @author star
 */
public abstract class BaseServiceProxy<T extends BaseModel> implements PagingAndSortingRepository<T, Integer> {

	protected PlatManager getLoginUser(){
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth!=null){
				if(auth.getPrincipal() instanceof LoginUser) {
					PlatManager loginUser = ((LoginUser)auth.getPrincipal()).getPlatManager();
					return loginUser;
				}else {
					PlatManager loginUser = (PlatManager)auth.getPrincipal();
					return loginUser;
				}
			}else{
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	protected void fillModel(BaseModel model){
		
	}
	
	@Override
	public <S extends T> S save(S entity) {
		this.fillModel(entity);
		return getBaseRepository().save(entity);
	}
	

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		return getBaseRepository().save(entities);
	}

	@Override
	public T findOne(Integer id) {
		return getBaseRepository().findOne(id);
	}

	@Override
	public boolean exists(Integer id) {
		return getBaseRepository().exists(id);
	}

	@Override
	public Iterable<T> findAll() {
		return getBaseRepository().findAll();
	}

	@Override
	public Iterable<T> findAll(Iterable<Integer> ids) {
		return getBaseRepository().findAll(ids);
	}

	@Override
	public long count() {
		return getBaseRepository().count();
	}

	@Override
	public void delete(Integer id) {
		getBaseRepository().delete(id);
	}

	@Override
	public void delete(T entity) {
		getBaseRepository().delete(entity);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		getBaseRepository().delete(entities);
	}

	@Override
	public void deleteAll() {
		getBaseRepository().deleteAll();
	}

	@Override
	public Iterable<T> findAll(Sort sort) {
		return getBaseRepository().findAll(sort);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return getBaseRepository().findAll(pageable);
	}
	
	abstract protected BaseRepository<T> getBaseRepository();
	
	protected EntityManager getEntityManager() {
		throw new RuntimeException("先重写这个方法");
	}
	
	/**
	 * 不分页列表查询
	 * @param clazz
	 * @param selectSql
	 * @param fromWhereSql
	 * @param orderBySql
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	protected <T> List<T> findListBySql(Class<T> clazz, String selectSql, String fromWhereSql, String orderBySql, List<Object> params) {
		StringBuffer sql = new StringBuffer(fromWhereSql);
		
		// 查询分页内容
		Query contentQuery = getEntityManager().createNativeQuery(selectSql + sql.toString() + orderBySql);
		if (!params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				contentQuery.setParameter(i+1, params.get(i));
			}
		}
		
		contentQuery.unwrap(SQLQuery.class).setResultTransformer(new Testtrans(clazz));
		List<T> content = contentQuery.getResultList();
		
		// 封装并返回
		return content;
	}
	
	/**
	 * 分页列表查询
	 * @param clazz
	 * @param selectSql
	 * @param fromWhereSql
	 * @param orderBySql
	 * @param params
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	protected <T> Page<T> findPageBySql(Class<T> clazz, String selectSql, String fromWhereSql, String orderBySql, List<Object> params, Pageable pageable) {
		StringBuffer sql = new StringBuffer(fromWhereSql);
		
		// 查询总条数
		Query countQuery = getEntityManager().createNativeQuery("SELECT count(1) as total " + sql.toString());
		if (params!=null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				countQuery.setParameter(i+1, params.get(i));
			}
		}
		Number total = (Number) countQuery.getSingleResult();
		// 校正页码
//		int realPageNum = this.correctionPageNum(total.intValue(), pageable.getPageSize(), pageable.getPageNumber());
		int realPageNum = pageable.getPageNumber();
		
		// 查询分页内容
		Query contentQuery = getEntityManager().createNativeQuery(selectSql + sql.toString() + orderBySql);
		if (params!=null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				contentQuery.setParameter(i+1, params.get(i));
			}
		}
		contentQuery.setFirstResult(realPageNum * pageable.getPageSize());
		contentQuery.setMaxResults(pageable.getPageSize());
		contentQuery.unwrap(SQLQuery.class).setResultTransformer(new Testtrans(clazz));
		 
		
		List<T> content = contentQuery.getResultList();
		
		PageSupport newPageSupport = new PageSupport();
		newPageSupport.setPageNumber(realPageNum);
		newPageSupport.setPageSize(pageable.getPageSize());
		// 封装并返回
		return new PageImpl<T>(content, pageable, total.longValue());
	}
	
	/**
	 * 分页列表查询
	 * @param clazz
	 * @param selectSql
	 * @param fromWhereSql
	 * @param orderBySql
	 * @param params
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	protected <T> Page<T> findPageBySql1(Class<T> clazz, String selectSql, String fromWhereSql, String orderBySql, List<Object> params, Pageable pageable) {
		StringBuffer sql = new StringBuffer(fromWhereSql);
		
		// 查询总条数
		Query countQuery = getEntityManager().createNativeQuery("SELECT count(1) as total from ( " + sql.toString()+") as v");
		if (params!=null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				countQuery.setParameter(i+1, params.get(i));
			}
		}
		Number total = (Number) countQuery.getSingleResult();
		// 校正页码
//		int realPageNum = this.correctionPageNum(total.intValue(), pageable.getPageSize(), pageable.getPageNumber());
		int realPageNum = pageable.getPageNumber();
		
		// 查询分页内容
		Query contentQuery = getEntityManager().createNativeQuery(selectSql + sql.toString() + orderBySql);
		if (params!=null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				contentQuery.setParameter(i+1, params.get(i));
			}
		}
		contentQuery.setFirstResult(realPageNum * pageable.getPageSize());
		contentQuery.setMaxResults(pageable.getPageSize());
		contentQuery.unwrap(SQLQuery.class).setResultTransformer(new Testtrans(clazz));
		 
		
		List<T> content = contentQuery.getResultList();
		
		PageSupport newPageSupport = new PageSupport();
		newPageSupport.setPageNumber(realPageNum);
		newPageSupport.setPageSize(pageable.getPageSize());
		// 封装并返回
		return new PageImpl<T>(content, pageable, total.longValue());
	}
	
	
	/**
	 * 分页列表查询groupBy则需要调用此方法
	 * @param clazz
	 * @param selectSql
	 * @param fromWhereSql
	 * @param orderBySql
	 * @param params
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	protected <T> Page<T> findPageBySql4GroupBy(Class<T> clazz, String selectSql, String fromWhereSql, String orderBySql, List<Object> params, Pageable pageable) {
		try {
			StringBuffer sql = new StringBuffer(selectSql+fromWhereSql);
			
			// 查询总条数
			Query countQuery = getEntityManager().createNativeQuery("SELECT count(1) as total from (" + sql.toString()+") tot");
			if (!params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					countQuery.setParameter(i+1, params.get(i));
				}
			}
			Number total = (Number) countQuery.getSingleResult();
			// 校正页码
			int realPageNum = this.correctionPageNum(total.intValue(), pageable.getPageSize(), pageable.getPageNumber());
			
			// 查询分页内容
			Query contentQuery = getEntityManager().createNativeQuery(selectSql + fromWhereSql + orderBySql);
			if (!params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					contentQuery.setParameter(i+1, params.get(i));
				}
			}
			contentQuery.setFirstResult(realPageNum * pageable.getPageSize());
			contentQuery.setMaxResults(pageable.getPageSize());
			contentQuery.unwrap(SQLQuery.class).setResultTransformer(new Testtrans(clazz));
			List<T> content = contentQuery.getResultList();
			
			PageSupport newPageSupport = new PageSupport();
			newPageSupport.setPageNumber(realPageNum);
			newPageSupport.setPageSize(pageable.getPageSize());
			// 封装并返回
			return new PageImpl<T>(content, pageable, total.longValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 校正页码
	 * @param total 总条数
	 * @param pageSize 每页显示多少条
	 * @param pageNum 传过来的页码
	 * @return 真正有意义的页码
	 */
	protected int correctionPageNum(int total, int pageSize, int pageNum) {
		int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
		if(totalPage == 0) {
			return 0;
		}else if (pageNum > (totalPage-1)) {
			return totalPage-1;
		}else {
			return pageNum;
		}
	}
	
	/**
	 * 拼出SQL的select部分
	 * @param clazz 类
	 * @param alias 别名
	 * @return
	 */
	protected String fieldSelectSql(Class<?> clazz, String alias) {
		StringBuffer sql = new StringBuffer();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue ;
			}
			String columnName = column.name();
			
			if (alias == null) {
				sql.append(String.format(", %s AS %s", columnName, fieldName));
			} else {
				sql.append(String.format(", %s.%s AS %s", alias, columnName, fieldName));
			}
		}
		if (sql.length() > 0) {
			sql.deleteCharAt(0);
			sql.append(" ");
		}
		return sql.toString();
	}
}
