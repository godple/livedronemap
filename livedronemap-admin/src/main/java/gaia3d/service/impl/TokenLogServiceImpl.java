package gaia3d.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gaia3d.domain.TokenLog;
import gaia3d.persistence.TokenLogMapper;
import gaia3d.service.TokenLogService;

@Service
public class TokenLogServiceImpl implements TokenLogService {

	@Autowired
	private TokenLogMapper tokenLogMapper;
	
	/**
	 * token 정보를 취득
	 * @param api_key
	 * @return
	 */
	@Transactional
	public TokenLog getToken(TokenLog tokenLog) {
		tokenLog.setToken(generateToken());
		return tokenLogMapper.insertTokenLog(tokenLog);
	}
	
	/**
	 * 토근 로그 취득
	 * @param tokenLog
	 * @return
	 */
	@Transactional(readOnly=true)
	public TokenLog getTokenLog(TokenLog tokenLog) {
		return tokenLogMapper.getTokenLog(tokenLog);
	}
	
	/**
	 * token 발행
	 * @return
	 */
	private String generateToken() {
		//return UUID.randomUUID().toString();
		return Long.toString(System.nanoTime()).substring(4, 12);
	}
}