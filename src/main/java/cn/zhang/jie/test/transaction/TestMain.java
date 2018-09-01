package cn.zhang.jie.test.transaction;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import com.sun.xml.internal.bind.v2.model.core.ID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

public class TestMain {

	private static final String ipAddr = "192.168.1.45"; 
	
	public static void main(String[] args) throws Exception {
//		m1();
//		m2();
//		m3();
//		m4();
		m5();
	}
	
	
	/**
	 * 在spring中测试redis的超时功能
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public static void m5() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		RedisTemplate redisTemplate = context.getBean("hashRedisTemplate", RedisTemplate.class);
		redisTemplate.execute(new SessionCallback() {
			public Object execute(RedisOperations ops) throws DataAccessException {
				ops.boundValueOps("k1").set("v1");
				String keyValue = (String) ops.boundValueOps("k1").get();
				//获取键的超时时间，-1表示不会超时
				Long expSecond = ops.getExpire("k1");
				System.out.println("expSecond : "+expSecond);
				boolean b = false;
				//设置超时时间是接下来的30秒
				b = ops.expire("k1", 30L, TimeUnit.SECONDS);
				//取消键的超时时间
				b = ops.persist("k1");
				Long l = 0L;
				l = ops.getExpire("k1");
				Long now = System.currentTimeMillis();
				Date date = new Date();
				date.setTime(now+30*1000);
				//设置某个具体时间作为超时时间
				ops.expireAt("k1", date);
				return null;
			}
		});
		context.close();
	}
	
	/**
	 * 测试发布订阅消息
	 * @throws Exception 
	 */
	public static void m4() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		RedisTemplate redisTemplate = context.getBean("hashRedisTemplate", RedisTemplate.class);
		String channel = "chat";
		for(int i =0;i<5;i++) {
			Thread.sleep(1000);
			redisTemplate.convertAndSend(channel, "I'm lazy!"+i);
		}
		context.close();
	}
	
	/**
	 * 在spring中使用流水线技术
	 */
	@SuppressWarnings({"unchceked","rawtypes"})
	public static void m3() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		RedisTemplate redisTemplate = context.getBean("hashRedisTemplate",RedisTemplate.class);
		SessionCallback callback = new SessionCallback() {

			public Object execute(RedisOperations ops) throws DataAccessException {
				for(int i=0;i<100000;i++) {
					int j = i +1 ;
					ops.boundValueOps("pipeline_key_"+i).set("pipeline_value_"+j);
					ops.boundValueOps("pipeline_key_"+j).get();
				}
				return null;
			}
		};
		long start = System.currentTimeMillis();
		List resultList = redisTemplate.executePipelined(callback);
		long end = System.currentTimeMillis();
		System.out.println("耗时："+(end-start)+" 毫秒");	//我的机器耗时1330毫秒
		context.close();
	}
	
	
	/**
	 * 测试redis中的“流水线”技术
	 */
	public static void m2() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		JedisPoolConfig poolConfig = context.getBean(JedisPoolConfig.class);
		JedisPool pool = new JedisPool(poolConfig,ipAddr);
		Jedis jedis = pool.getResource();
		long startTime = System.currentTimeMillis();
		//开启流水线
		Pipeline pipeline = jedis.pipelined();
		//这里测试10万条的读/写2个操作
		for(int i=0;i<100000;i++) {
			int j = i + 1;
			pipeline.set("pipeline_key_"+i, "pipeline_value_"+j);
			pipeline.get("pipeline_key_"+j);
		}
		//这里只执行同步，但是不反悔结果
//		pipeline.sync();
		//将返回执行过的命令返回的list列表结果
		List result = pipeline.syncAndReturnAll();
		System.out.println(result);
		long endtime = System.currentTimeMillis();
		System.out.println("耗时："+(endtime-startTime)+" 毫秒");	//我的机器耗时1009毫秒
		context.close();
	}
	
	
	/**
	 * 测试redis下的事务，事务开启、事务的返回值
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public static void m1() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		//这里也是用的字符串序列化器
		final RedisTemplate redisTemplate = context.getBean("hashRedisTemplate", RedisTemplate.class);
		SessionCallback callback = new SessionCallback() {

			public Object execute(RedisOperations ops) throws DataAccessException {
				ops.multi();
				ops.boundValueOps("k11").set("v11");
				//由于只是进入执行队列，因此这里的返回值是null
				String value = (String) ops.boundValueOps("k11").get();
				ops.boundValueOps("k11").get();
				ops.boundValueOps("k12").get();
				System.out.println(value);
				//这里的list会保存之前进入队列的所有命令的结果
				List list = ops.exec();
				System.out.println(list);
				//事务结束之后，获取value
				value = (String) redisTemplate.opsForValue().get("k11");
				return value;
			}
		};
		String value = (String) redisTemplate.execute(callback);
		System.out.println(value);
		context.close();
	}
}
