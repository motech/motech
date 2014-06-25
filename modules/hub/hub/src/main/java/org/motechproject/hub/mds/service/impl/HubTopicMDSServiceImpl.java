package org.motechproject.hub.mds.service.impl;

import java.util.List;

import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubTopicMDSService;

//TODO: is this implementation class required??
public class HubTopicMDSServiceImpl {

	private HubTopicMDSService hubTopicMDSService;

	public HubTopicMDSService getHubTopicService() {
		return hubTopicMDSService;
	}

	public void setHubTopicService(HubTopicMDSService hubTopicService) {
		this.hubTopicMDSService = hubTopicService;
	}

	public void save(String topicUrl) {
		HubTopic hubTopic = new HubTopic(topicUrl);
		hubTopicMDSService.create(hubTopic);
	}
	
	public long count() {
		long count = hubTopicMDSService.count();
		return count;
	}
	
	public List<HubTopic> fetch(String topicUrl) {
		List<HubTopic> topics = hubTopicMDSService.findByTopicUrl(topicUrl);
		return topics;
	}
	
	public long countFind(String topicUrl)	{
		long count = hubTopicMDSService.countFind(topicUrl);
		return count;
	}

}
