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
package com.eurodyn.qlack2.fuse.chatim.api;

import com.eurodyn.qlack2.fuse.chatim.api.dto.VoteResultDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.VotingDTO;

/**
 *
 * @author European Dynamics SA
 */
public interface VotingService {
	String startVoting(VotingDTO votingDTO);
	String stopVoting(String votingID);
	String vote(String userID, VoteResultDTO voteResultDTO);
	VoteResultDTO[] checkVoting(String votingID);
}
