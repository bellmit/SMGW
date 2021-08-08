package star.sms.sysconfig.service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.sysconfig.dao.SysConfigRepository;
import star.sms.sysconfig.domain.SysConfig;

/**
 * 系统配置表
 * @author star
 */
@Slf4j
@Service
@Transactional
public class SysConfigService extends BaseServiceProxy<SysConfig> {
	
	@Resource
	private SysConfigRepository sysConfigRepository;
	
	@Override
	protected BaseRepository<SysConfig> getBaseRepository() {
		return sysConfigRepository;
	}
	
	/**
	 * 获取系统配置表
	 * @return
	 */
	public SysConfig getConfig() {
		SysConfig sysConfig = this.findOne(1);
		if(sysConfig==null) {
			sysConfig = new SysConfig();
			sysConfig.setId(1);
			sysConfig.setTaskCount(5);
			this.save(sysConfig);
		}
		return sysConfig;
	}
}
