package cn.zhang.jie.test.transaction;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

public class TestMain {

	public static void main(String[] args) {
		m1();
	}
	
	
	/**
	 * 探索事务的回滚
	 */
	public static void m2() {
		
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
