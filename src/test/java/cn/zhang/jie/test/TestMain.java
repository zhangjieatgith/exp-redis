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
				if(end - start >= 1000) {	//������1000����ʱ����������
					break;
				}
				i++;
				jedis.set("test"+i,i+"");
			}
		} finally {
			jedis.close();
		}
		//��ӡ1���ڶ�redis�Ĳ�������
		System.out.println("redis ÿ������� "+i+" ��"); */
	}
}
