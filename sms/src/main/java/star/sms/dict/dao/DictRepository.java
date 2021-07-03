package star.sms.dict.dao;

import java.util.List;

import star.sms._frame.base.BaseRepository;
import star.sms.dict.domain.Dict;

public interface DictRepository extends BaseRepository<Dict> {
	
	List<Dict> findBySysTagOrderByDictIndex(Integer sysTag);
	List<Dict> findBySysTagAndDictTypeOrderByDictIndex(Integer sysTag,String dictType);
}
