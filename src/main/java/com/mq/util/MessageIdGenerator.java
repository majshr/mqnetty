package com.mq.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * messageId生成策略
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午4:48:12
 */
public enum MessageIdGenerator {

    UUID{
        @Override
        public String generate() {
            return java.util.UUID.randomUUID().toString();
        }
    },
    RandomDigital {
        @Override
        public String generate() {
            SecureRandom secureRandom = new SecureRandom();
            String id = new BigInteger(130, secureRandom).toString(10);
            return id;
        }
    };

    abstract public String generate();
}
