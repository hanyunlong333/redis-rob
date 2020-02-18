package com.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class jedisCom{

    @Bean
    public JedisPool getJedis(){
        return new JedisPool("127.0.0.1",6379);
    }
    @Autowired
    JedisPool jedisPool;

    public  boolean setnx(String key, String val){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            if(jedis == null){
                return false;
            }
            return jedis.set("NX","PX").equalsIgnoreCase("ok");
        }catch (Exception e){
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return  false;
    }
    public int delnx(String key, String val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis == null) {
                return 0;
            }

            //if redis.call('get','orderkey')=='1111' then return redis.call('del','orderkey') else return 0 end
            StringBuilder sbScript = new StringBuilder();
            sbScript.append("if redis.call('get','").append(key).append("')").append("=='").append(val).append("'").
                    append(" then ").
                    append("    return redis.call('del','").append(key).append("')").
                    append(" else ").
                    append("    return 0").
                    append(" end");

            return Integer.valueOf(jedis.eval(sbScript.toString()).toString());
        } catch (Exception ex) {
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
}
