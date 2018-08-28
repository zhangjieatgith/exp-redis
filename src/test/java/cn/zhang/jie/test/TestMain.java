package cn.zhang.jie.test;

import redis.clients.jedis.Jedis;

public class TestMain {
	public static void main(String[] args) {
		m1();
	}
	
	public static void m1() {
		Jedis jedis = new Jedis("192.168.1.45",6379);
		System.out.println(jedis.ping());
		jedis.close();
/*		int i = 0;
		try {
			long start = System.currentTimeMillis();
			while(true) {
				long end = System.currentTimeMillis();
				if(end - start >= 1000) {	//当大于1000毫秒时，结束操作
					break;
				}
				i++;
				jedis.set("test"+i,i+"");
			}
		} finally {
			jedis.close();
		}
		//打印1秒内对redis的操作次数
		System.out.println("redis 每秒操作： "+i+" 次"); */
	}
}
