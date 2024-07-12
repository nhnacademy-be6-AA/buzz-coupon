package store.buzzbook.coupon.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

	@Value("${spring.rabbitmq.coupon.exchange}")
	public String exchangeName;

	@Value("${spring.rabbitmq.coupon.queue}")
	public String queueName;

	@Value("${spring.rabbitmq.coupon.routing-key}")
	public String routingKey;

	@Value("${spring.rabbitmq.coupon.dlx.exchange}")
	public String dlxExchangeName;

	@Value("${spring.rabbitmq.coupon.dlx.queue}")
	public String dlxQueueName;

	@Value("${spring.rabbitmq.coupon.dlx.routing-key}")
	public String dlxRoutingKey;

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.username}")
	private String username;

	@Value("${spring.rabbitmq.password}")
	private String password;

	@Value("${spring.rabbitmq.port}")
	private int port;

	@Bean
	DirectExchange requestExchange() {
		return new DirectExchange(exchangeName, true, false, null);
	}

	@Bean
	Queue requestQueue() {
		return QueueBuilder.durable(queueName)
			.withArgument("x-dead-letter-exchange", dlxExchangeName)
			.withArgument("x-dead-letter-routing-key", dlxRoutingKey)
			.build();
	}

	@Bean
	Binding requestBinding(DirectExchange requestExchange, Queue requestQueue) {
		return BindingBuilder.bind(requestQueue).to(requestExchange).with(routingKey);
	}

	@Bean
	DirectExchange dlxExchange() {
		return new DirectExchange(exchangeName);
	}

	@Bean
	Queue dlqQueue() {
		return new Queue(dlxQueueName, true);
	}

	@Bean
	Binding dlqBinding(DirectExchange dlxExchange, Queue dlqQueue) {
		return BindingBuilder.bind(dlqQueue).to(dlxExchange).with(dlxRoutingKey);
	}

	@Bean
	ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(host);
		connectionFactory.setPort(port);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		return connectionFactory;
	}

	@Bean
	MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter);
		return rabbitTemplate;
	}
}
