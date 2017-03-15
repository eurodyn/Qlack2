package com.eurodyn.qlack2.util.cluster.hazelcast;

import com.eurodyn.qlack2.util.cluster.core.QlackClusterListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class HazelcastMessageListener implements MessageListener<Object> {
	private QlackClusterListener qcl;

	public HazelcastMessageListener(QlackClusterListener qcl) {
		this.qcl = qcl;
	}

	@Override
	public void onMessage(Message<Object> msg) {
		qcl.onMessage((String)msg.getMessageObject());
	}

}
