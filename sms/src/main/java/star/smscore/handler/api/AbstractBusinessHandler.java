package star.smscore.handler.api;

import io.netty.channel.ChannelDuplexHandler;

import star.smscore.connect.manager.EndpointEntity;

public abstract class AbstractBusinessHandler extends ChannelDuplexHandler implements BusinessHandlerInterface, Cloneable {

	private EndpointEntity entity;

	public void setEndpointEntity(EndpointEntity entity) {
		this.entity = entity;
	}

	public EndpointEntity getEndpointEntity() {
		return entity;
	}

	public abstract String name();

	public AbstractBusinessHandler clone() throws CloneNotSupportedException {
		return (AbstractBusinessHandler) super.clone();
	}
}
