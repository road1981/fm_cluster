package com.cloud.fmnode.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 说明：日志处理
 */

@Slf4j
public class Logger {
	public static Logger getLogger(Class classObject) {
		return new Logger();
	}

	public void debug(Object object) {
		log.debug(object.toString());
	}

	public void info(Object object) {
		log.info(object.toString());
	}

	public void warn(Object object) {
		log.warn(object.toString());
	}

	public void error(Object object) {
		log.error(object.toString());
	}
}