package cn.zhang.jie.test.datastructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 6中数据类型
 * @author zhangjie
 *
 */
public class TestMain {

	public static void main(String[] args) {
		m1();
	}
	
	/**
	 * 处理hash结构
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void m1() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		RedisTemplate redisTemplate = context.getBean("hashRedisTemplate", RedisTemplate.class);
		String key = "hash";
		Map<String,String> map =  new HashMap<String,String>();
		map.put("f1", "val1");
		map.put("f2", "val2");
		//hmset命令:向redis中添加一个Map
		redisTemplate.opsForHash().putAll(key, map);
		//hset命令：向redis中已经存在的map，添加一个属性
		redisTemplate.opsForHash().put(key, "f3", "6");
		//hexists命令：判断指定的map中是否存在某个属性
		System.out.println(redisTemplate.opsForHash().hasKey(key, "f3"));
		//hgetall命令：获取redis中存在的map，并转换为java的Map类型
		Map keyValMap = redisTemplate.opsForHash().entries(key);
		System.out.println(keyValMap);
		//hincrby命令：对map的某个属性值做加法操作
		redisTemplate.opsForHash().increment(key, "f3", 12);
		//hincrbyfloat命令：对map的某个属性做浮点加法运算
		redisTemplate.opsForHash().increment(key, "f3", 3.14159);
		//hvals命令：获取map中所有值的集合
		List valueList = redisTemplate.opsForHash().values(key);
		System.out.println(valueList);
		//hkeys命令：获取map中所有键的集合
		Set keySet = redisTemplate.opsForHash().keys(key);
		System.out.println(keySet);
		//hmget命令：获取map中指定的多个属性对应的值
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("f1");
		fieldList.add("f2");
		List valueList2 = redisTemplate.opsForHash().multiGet(key, fieldList);
		System.out.println(valueList2);
		//hsetnx命令：如果map中不存在这个属性，则添加该属性的值，否则不添加
		System.out.println(redisTemplate.opsForHash().putIfAbsent(key, "f4", "val4"));
		//hdel命令：删除某个属性,成功删除返回1，否则返回0
		System.out.println(redisTemplate.opsForHash().delete(key, "f6"));
		context.close();
	}
}
