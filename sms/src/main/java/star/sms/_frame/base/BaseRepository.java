package star.sms._frame.base;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author star
 */
@NoRepositoryBean
public interface BaseRepository<T> extends PagingAndSortingRepository<T, Integer>,JpaSpecificationExecutor<T> {
}
