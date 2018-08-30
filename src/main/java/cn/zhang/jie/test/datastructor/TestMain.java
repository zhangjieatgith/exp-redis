package cn.zhang.jie.test.datastructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.core.RedisCommand;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 6中数据类型
 * @author zhangjie
 *
 */
public class TestMain {

	public static void main(String[] args) {
//		m1();
//		m2();
		m3();
		//加一些新的东西行吗
	}
	
	
	/**
	 * 操作集合
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void m3() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		//这里也是用的字符串序列化器
		RedisTemplate redisTemplate = context.getBean("hashRedisTemplate", RedisTemplate.class);	
		Set set = null;
		//将元素加入列表
		//boundSetOps这个方法并不会覆盖原有的数据,它和opsForSet().add类似,形式不同
		redisTemplate.boundSetOps("set1").add("v-1");
		redisTemplate.boundSetOps("set1").add("v1","v2","v3","v4","v5","v6");
		redisTemplate.boundSetOps("set2").add("v0","v2","v4","v6","v8");
		//在原来的基础上添加更多的元素
		redisTemplate.opsForSet().add("set1","v-2","v-3");
		//求集合的长度
		System.out.println(redisTemplate.opsForSet().size("set1"));
		//求差集
		set = redisTemplate.opsForSet().difference("set1","set2");
		System.out.println(set);
		//求交集
		set = redisTemplate.opsForSet().intersect("set1","set2");
		System.out.println(set);
		//判断是否是集合中的元素
		System.out.println(redisTemplate.opsForSet().isMember("set", "s9"));
		//从集合中随机弹出一个元素
		String val = (String) redisTemplate.opsForSet().pop("set1");
		System.out.println(val);
		//随机获取一个集合的元素
		val = (String) redisTemplate.opsForSet().randomMember("set1");
		System.out.println(val);
		//随机获取集合中的两个元素
		List list = redisTemplate.opsForSet().randomMembers("set1", 2);
		System.out.println(list);		//这两个元素可能是重复的
		//删除一个集合的元素，参数可以是多个
		redisTemplate.opsForSet().remove("set1", "v1");
		//求两个集合的并集
		set = redisTemplate.opsForSet().union("set1", "set2");
		System.out.println(set);
		//求两个集合的差集，并保存到集合 diff_set 中
		redisTemplate.opsForSet().differenceAndStore("set1", "set2", "diff_set");
		//求两个集合的交集，并保存到集合inter_set 中
		redisTemplate.opsForSet().intersectAndStore("set1", "set2", "inter_set");
		//求两个集合的并集，并保存到集合union_set 中
		redisTemplate.opsForSet().unionAndStore("set1", "set2", "union_set");
		context.close();
	}
	
	
	/**
	 * 处理链表结构
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void m2() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		//这里也是用的字符串序列化器
		RedisTemplate redisTemplate = context.getBean("hashRedisTemplate", RedisTemplate.class);	
		try {
			//删除链表，反复测试
			redisTemplate.delete("list");
			//向list中插入node3
			redisTemplate.opsForList().leftPush("list", "node3");
			List<String> nodeList = new ArrayList<String>();
			for(int i=2;i>=1;i--) {		//由于后面是由左向右插入的，所以注意这里list中元素的添加方向
				nodeList.add("node"+i);
			}
			//批量的从左到右插入一批数据
			redisTemplate.opsForList().leftPushAll("list", nodeList);
			//从右边插入一个节点
			redisTemplate.opsForList().rightPush("list", "node4");
			//获取下标为0的节点
			System.out.println(redisTemplate.opsForList().index("list", 0));
			//获取链表长度
			System.out.println(redisTemplate.opsForList().size("list"));
			//从左边弹出一个节点
			System.out.println(redisTemplate.opsForList().leftPop("list"));
			//从右边弹出一个节点
			System.out.println(redisTemplate.opsForList().rightPop("list"));
			//在node2前插入一个节点（需要使用更为底层的命令）
			redisTemplate.getConnectionFactory().getConnection().lInsert("list".getBytes("utf-8"),
					RedisListCommands.Position.BEFORE,
					"node2".getBytes(), 
					"before_node".getBytes("utf-8"));
			//在node2后面插入一个节点
			redisTemplate.getConnectionFactory().getConnection().lInsert("list".getBytes("utf-8"),
					RedisListCommands.Position.AFTER,
					"node2".getBytes("utf-8"),
					"after_node".getBytes("utf-8"));
			//判断list是否存在，如果存在则从左边插入head节点
			redisTemplate.opsForList().leftPushIfPresent("list","head");
			//判断list是否存在，如果存在则从右边插入end节点
			redisTemplate.opsForList().rightPushIfPresent("list", "end");
			//从左到右，获取下标为1-3的节点元素（子list）
			List valueList = redisTemplate.opsForList().range("list",1,3);
			System.out.println(valueList);
			//在链表的左边插入三个值为node的节点
			nodeList.clear();
			for(int i=0;i<3;i++) {nodeList.add("node");}
			redisTemplate.opsForList().leftPushAll("list", nodeList);
			//从左到右删除最多2个node节点
			redisTemplate.opsForList().remove("list",2,"node");
			//给链表下标为0的节点设置新值
			redisTemplate.opsForList().set("list",0, "new_head_node");
			//获取整个链表的值
			List listAll = redisTemplate.opsForList().range("list",0, redisTemplate.opsForList().size("list"));
			System.out.println(listAll);
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.close();
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
