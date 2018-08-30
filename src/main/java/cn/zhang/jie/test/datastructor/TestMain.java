package cn.zhang.jie.test.datastructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

/**
 * 6中数据类型
 * @author zhangjie
 *
 */
public class TestMain {

	public static void main(String[] args) {
//		m1();
//		m2();
//		m3();
		m4();
	}
	
	
	/**
	 * 操作有序集合
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public static void m4() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		//这里也是用的字符串序列化器
		RedisTemplate redisTemplate = context.getBean("hashRedisTemplate", RedisTemplate.class);
		//TypedTuple 这是一个接口，它定义了两个 方法，getValue()获取值、getScore()获取分数
		Set<TypedTuple> set1 = new HashSet<TypedTuple>();
		Set<TypedTuple> set2 = new HashSet<TypedTuple>();
		//
		int j = 9;
		for(int i=1;i<=9;i++) {
			j--;
			//计算分数和值
			Double score1 = Double.valueOf(i);
			String value1 = "x"+i;
			Double score2 = Double.valueOf(j);
			String value2 = j % 2 == 1 ? "y" + j : "x"+j;
			//使用spring提供的默认TypedTuple -> DefaultTypedTuple
			TypedTuple typedTuple1 = new DefaultTypedTuple(value1, score1);
			set1.add(typedTuple1);
			TypedTuple typedTuple2 = new DefaultTypedTuple(value2, score2);
			set2.add(typedTuple2);
		}
		System.out.println("zset1 -------> "+set1);
		System.out.println("zset2 -------> "+set2);
		redisTemplate.opsForZSet().add("zset1", set1);
		redisTemplate.opsForZSet().add("zset2", set2);
		//统计总数
		Long size = null;
		size = redisTemplate.opsForZSet().zCard("zset1");
		System.out.println(size);
		//求数量：记分数为score，那么下面的方法就是求 3<=score<=6 的元素
		size = redisTemplate.opsForZSet().count("zset1", 3, 6);
		System.out.println(size);
		//求集合：从下标一开始截取5个元素，但是不返回分数，每一个元素是string。由于分数的存在，这些值是有序的，因此可以向链表一样使用下标
		Set set = null;
		set = redisTemplate.opsForZSet().range("zset1", 1, 5);
		System.out.println(set);
		//截取集合所有元素，并且对集合按分数排序，并返回分数，每一个元素是TypedTuple。貌似range方法已经隐式的排序过了
		set = redisTemplate.opsForZSet().rangeWithScores("zset1", 0, -1);
		printTypedTuple(set);
		//将zset1和zset2两个集合的交集存放到集合inter_zset中
		size = redisTemplate.opsForZSet().intersectAndStore("zset1", "zset2", "inter_zset");
		System.out.println(size);
		//区间操作，感觉像，先取x8的索引，再取x2的索引，然后根据这两个索引，取出以这两个索引为端节点的集合
		Range range = Range.range();
		range.lt("x8");
		range.gt("x2");
		set = redisTemplate.opsForZSet().rangeByLex("zset1", range);
		System.out.println(set);
		range.lte("x8");
		range.gte("x2");
		set = redisTemplate.opsForZSet().rangeByLex("zset1", range);
		System.out.println(set);
		//限制返回个数
		Limit limit = Limit.limit();
		limit.count(4);
		limit.offset(5);
		//求区间内的元素，并限制返回4条
		set = redisTemplate.opsForZSet().rangeByLex("zset1", range, limit);
		System.out.println(set);	//这个结果是综合 limit 的count和offset得出来的
		//求排行，排名第一返回0，第二返回1
		Long rank = redisTemplate.opsForZSet().rank("zset1","x4");
		System.out.println("rank = "+rank);
		//删除元素，返回删除个数
		size = redisTemplate.opsForZSet().remove("zset1", "x5","x6");
		System.out.println(size);
		//按照排行删除从0开始算起，这里将排行第二和第3的元素删除
		size = redisTemplate.opsForZSet().removeRange("zset1", 1, 2);
		System.out.println(size);
		//获取集合中的所有元素的值和分数，-1表示全部元素
		set = redisTemplate.opsForZSet().rangeWithScores("zset2", 0, -1);
		printTypedTuple(set);
		//删除指定的元素
		size = redisTemplate.opsForZSet().remove("zset2", "y5","y3");
		System.out.println(size);
		//给集合中一个元素的score加上11,调试观察（以下三步似乎有些问题）
		Double dbl = redisTemplate.opsForZSet().incrementScore("zset1", "x1", 11);
		redisTemplate.opsForZSet().removeRangeByScore("zset1", 1, 2);
		set = redisTemplate.opsForZSet().reverseRangeWithScores("zset2", 1, 10);
		printTypedTuple(set);
		context.close();
	}
	
	@SuppressWarnings({"rawtypes"})
	private static void printTypedTuple(Set<TypedTuple>set){
		if(set != null && set.isEmpty()) {
			return;
		}
		Iterator<TypedTuple> iterator = set.iterator();
		while(iterator.hasNext()) {
			TypedTuple val = iterator.next();
			System.out.println("{value = "+val.getValue()+",score = "+val.getScore()+"}\n");
		}
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
