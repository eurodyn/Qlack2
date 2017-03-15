/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.chatim.impl;

import com.eurodyn.qlack2.fuse.chatim.api.VotingService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.VoteResultDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.VotingDTO;

/**
 *
 * @author European Dynamics SA
 */
public class VotingServiceImpl implements VotingService {

	@Override
	public String startVoting(VotingDTO votingDTO) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String stopVoting(String votingID) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String vote(String userID, VoteResultDTO voteResultDTO) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public VoteResultDTO[] checkVoting(String votingID) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}