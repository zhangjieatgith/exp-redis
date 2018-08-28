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
	 * ʹ�����ӳع�������
	 */
	public static void m2() {
		//�������ӳ����ö���
		JedisPoolConfig config = new JedisPoolConfig();
		//��������
		config.setMinIdle(50);
		//���������
		config.setMaxTotal(100);
		//���ȴ�������
		config.setMaxWaitMillis(20000);
		//ͨ�����ö��󴴽����ӳض���
		JedisPool pool = new JedisPool(config,ipAddr);
		//�����ӳ��л�ȡһ������
		Jedis jedis = pool.getResource();
		System.out.println(jedis.ping());
		jedis.close();
	}
	
	/**
	 *  ��API�����Ե�ǰ��������redis��д�����ܣ�ͨ����2��࣬������ֻ��2ǧ�ࡣ���ʹ����ˮ�߼��������Դﵽÿ��10���д���ٶȣ�
	 */
	public static void m1() {
		Jedis jedis = new Jedis("192.168.1.45", 6379);
		int i = 0;	//��¼��������
		try {
			long start = System.currentTimeMillis();
			while(true) {
				long end = System.currentTimeMillis();
				if(end - start >= 1000) {		//������1000�����ʱ�򣬽�������
					break;
				}
				i++;
				jedis.set("test"+i,i+"");
			}
		} finally {
			jedis.close();
		}
		System.out.println("redis ÿ��д������"+i+" ��");	//��ӡ1���ڶ�redis�Ĳ�������
		//output:2849��
	}
}
