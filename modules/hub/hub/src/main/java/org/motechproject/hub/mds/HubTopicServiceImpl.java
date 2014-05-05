package org.motechproject.hub.mds;

import java.util.List;

public class HubTopicServiceImpl {

	private HubTopicService hubTopicService;

	public HubTopicService getHubTopicService() {
		return hubTopicService;
	}

	public void setHubTopicService(HubTopicService hubTopicService) {
		this.hubTopicService = hubTopicService;
	}

	public void save(String topicUrl) {
		HubTopic hubTopic = new HubTopic(topicUrl);
		hubTopicService.create(hubTopic);
	}
	
	public long count() {
		long count = hubTopicService.count();
		return count;
	}
	
	public List<HubTopic> fetch(String topicUrl) {
		List<HubTopic> topics = hubTopicService.findByTopicUrl(topicUrl);
		return topics;
	}
	
	public long countFind(String topicUrl)	{
		long count = hubTopicService.countFind(topicUrl);
		return count;
	}

}
