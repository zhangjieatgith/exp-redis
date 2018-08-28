package cn.zhang.jie.testapi;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestApi {

	private static final String ipAddr = "192.168.1.45"; 
	
	public static void main(String[] args) {
//		m1();
//		m2();
		m3();
	}
	
	
	/**
	 * 
	 */
	public static void m3() {
		JedisConnectionFactory  a =null;
	}
	
	/**
	 * 使用连接池管理连接
	 */
	public static void m2() {
		//创建连接池配置对象
		JedisPoolConfig config = new JedisPoolConfig();
		//最大空闲数
		config.setMinIdle(50);
		//最大连接数
		config.setMaxTotal(100);
		//最大等待毫秒数
		config.setMaxWaitMillis(20000);
		//通过配置对象创建连接池对象
		JedisPool pool = new JedisPool(config,ipAddr);
		//从连接池中获取一个连接
		Jedis jedis = pool.getResource();
		System.out.println(jedis.ping());
		jedis.close();
	}
	
	/**
	 *  简单API，测试当前服务器上redis的写入性能（通常会2万多，我这里只有2千多。如果使用流水线技术，可以达到每秒10万的写入速度）
	 */
	public static void m1() {
		Jedis jedis = new Jedis("192.168.1.45", 6379);
		int i = 0;	//记录操作次数
		try {
			long start = System.currentTimeMillis();
			while(true) {
				long end = System.currentTimeMillis();
				if(end - start >= 1000) {		//当大于1000毫秒的时候，结束操作
					break;
				}
				i++;
				jedis.set("test"+i,i+"");
			}
		} finally {
			jedis.close();
		}
		System.out.println("redis 每秒写操作："+i+" 次");	//打印1秒内对redis的操作次数
		//output:2849次
	}
}
