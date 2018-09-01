package cn.zhang.jie.auxcls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


/**
 * 这是一个接受消息的类（订阅监听类），它需要实现 MessageListener 接口
 * @author zhangjie
 *
 */
public class RedisMessageListener implements MessageListener{

	private RedisTemplate redisTemplate;
	
	public void onMessage(Message message, byte[] pattern) {
		//获取消息
		byte [] body = message.getBody();
		//使用值序列器进行转换
		String msgBody = (String) getRedisTemplate().getValueSerializer().deserialize(body);
		System.out.println("msgBody : "+msgBody);
		//获取channel（渠道）
		byte [] channel = message.getChannel();
		//使用字符串序列化器转换
		String channelStr = (String) getRedisTemplate().getStringSerializer().deserialize(channel);
		System.out.println("channelStr : "+channelStr);
		//渠道名称转换
		String bytesStr = new String(pattern);
		System.out.println("渠道名称转换："+bytesStr);
	}

	
	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
