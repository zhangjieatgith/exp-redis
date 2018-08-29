package cn.zhang.jie.testInSpring;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

public class TestMain {
	public static void main(String[] args) {
//		m1();
		m2();
	}
	
	
	public static void m2() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		RedisTemplate<String,String> redisTemplate = context.getBean(RedisTemplate.class);
		redisTemplate.boundValueOps("ss1").set("vv1");
		System.out.println(redisTemplate.boundValueOps("ss1").get());
		context.close();
	}
	
	/**
	 * 对于redis，get和set方法可能是来自于同一个redis连接池的不同连接。为了使得所有的操作都来自于同一个连接，可以使用
     * SessionCallback（用的更多）或者RedisCallback这两个接口，通过这两个接口就可以把多个命令放入同一个redis连接中去执行
	 */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static void m1() {
    	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        RedisTemplate<String, TestMain.Person> redisTemplate = context.getBean(RedisTemplate.class);
        final Person person = new Person();
        person.setId(1002L);
        person.setName("role_name_222");
        person.setNote("note_222");
        SessionCallback<TestMain.Person> callBack = new SessionCallback<TestMain.Person>() {
			public Person execute(RedisOperations operations) throws DataAccessException {
				operations.boundValueOps("pp1").set(person);
				return (Person) operations.boundValueOps("pp1").get();
			}
        };
        Person pp = redisTemplate.execute(callBack);
        System.out.println(pp);
        context.close();
    }
	
	static class Person implements Serializable{
		private static final long serialVersionUID = 1L;
		private Long id;
		private String name;
		private String note;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getNote() {
			return note;
		}
		public void setNote(String note) {
			this.note = note;
		}
		@Override
		public String toString() {
			return "Person [id=" + id + ", name=" + name + ", note=" + note + "]";
		}
	}
}
