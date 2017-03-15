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
package com.eurodyn.qlack2.fuse.simm.impl.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.Query;

import com.eurodyn.qlack2.fuse.simm.api.dto.FriendDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.PostItemDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupUserDTO;
import com.eurodyn.qlack2.fuse.simm.impl.mappers.SimGroupAttributeMapperImpl;
import com.eurodyn.qlack2.fuse.simm.impl.mappers.SocialGroupAttributeDTOMapperImpl;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimFriends;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroup;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroupAttribute;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroupHasUser;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimHomepageActivity;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimHomepageActivityBin;

/**
 * Utility class to convert from DTO to model bean or vice versa
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {

	private static final Logger LOGGER = Logger.getLogger(ConverterUtil.class.getName());
	public static final SimGroupAttributeMapperImpl simGroupAttributeMapper = new SimGroupAttributeMapperImpl();
	public static final SocialGroupAttributeDTOMapperImpl socialGroupAttributeDTOMapper = new SocialGroupAttributeDTOMapperImpl();

	/**
	 * Return Group DTO array for a query on SimGroup
	 *
	 * @param query
	 * @return Group DTO
	 */
	public static SocialGroupDTO[] getGroupDTOArrayForGroupQuery(Query query) {
		Collection<SocialGroupDTO> collection = getGroupResultAsDTOCollection(query);

		return collection.toArray(new SocialGroupDTO[collection.size()]);
	}

	/**
	 * Return user IDs array for a query on SimGroupHasUser
	 *
	 * @param query
	 * @return array of user IDs
	 */
	@SuppressWarnings("unchecked")
	public static String[] getUserIdStringIdsArrayForGroupQuery(Query query) {
		Collection<String> collection = new ArrayList<String>();
		for (Iterator<SimGroupHasUser> itr = query.getResultList().iterator(); itr.hasNext();) {
			SimGroupHasUser bean = itr.next();
			collection.add(bean.getUserId());
		}
		return collection.toArray(new String[collection.size()]);
	}

	/**
	 * Return group IDs array for a query on SimGroup
	 *
	 * @param query
	 * @return array of group IDs
	 */
	@SuppressWarnings("unchecked")
	public static String[] getGroupIdsArrayForGroupQuery(Query query) {
		Collection<String> collection = new ArrayList<String>();
		for (Iterator<SimGroup> itr = query.getResultList().iterator(); itr.hasNext();) {
			SimGroup bean = itr.next();
			collection.add(bean.getId());
		}
		return collection.toArray(new String[collection.size()]);
	}

	/**
	 * This method help in converting result from Query results to Collection
	 *
	 * @param query
	 * @return Collection of SocialGroupDTO
	 */
	@SuppressWarnings("unchecked")
	public static Collection<SocialGroupDTO> getGroupResultAsDTOCollection(Query query) {
		Collection<SocialGroupDTO> result = new ArrayList<SocialGroupDTO>();

		for (Iterator<SimGroup> itr = query.getResultList().iterator(); itr.hasNext();) {
			SimGroup bean = itr.next();
			result.add(convertGroupModelToDTO(bean));
		}
		return result;
	}

	/**
	 * Converts a list of SimGroup entities to a list of SocialGroupDTOs
	 * 
	 * @param entities
	 *            The entities to convert
	 * @return The resulting DTOs
	 */
	public static List<SocialGroupDTO> simGroupToSocialGroupDTOList(
			List<SimGroup> entities) {
		if (entities == null) {
			return null;
		}

		List<SocialGroupDTO> dtos = new ArrayList<SocialGroupDTO>(
				entities.size());
		for (SimGroup entity : entities) {
			dtos.add(convertGroupModelToDTO(entity));
		}
		return dtos;
	}  
	
	/**
	 * Convert SocialGroupDTO object value to SimGroup
	 *
	 * @param groupDTO
	 * @return SimGroup
	 */
	public static SimGroup convertGroupDTOToModel(SocialGroupDTO groupDTO) {
		SimGroup grp = new SimGroup();
		grp.setId(groupDTO.getId());
		grp.setDescription(groupDTO.getDescription());
		grp.setName(groupDTO.getName());
		grp.setLogo(groupDTO.getLogo());
		if (groupDTO.getCreatedOn() == 0) {
			grp.setCreatedOn(Instant.now().toEpochMilli());
		} else {
			grp.setCreatedOn(groupDTO.getCreatedOn());	
		}
		grp.setPrivacy(groupDTO.getPrivacy());
		grp.setStatus(groupDTO.getStatus());
		grp.setTags(groupDTO.getTags());
		grp.setSlugify(groupDTO.getSlugify());
		grp.setSimGroupAttributes(socialGroupAttributeDTOMapper.toSocialGroupAttributeList(groupDTO.getSocialGroupAttributes(), grp));
		return grp;
	}

	/**
	 * Convert SimGroup object value to SocialGroupDTO
	 *
	 * @param group
	 * @return SocialGroupDTO
	 */
	public static SocialGroupDTO convertGroupModelToDTO(SimGroup group) {
		SocialGroupDTO groupDTO = new SocialGroupDTO();
		groupDTO.setId(group.getId());
		groupDTO.setDescription(group.getDescription());
		groupDTO.setName(group.getName());
		groupDTO.setLogo(group.getLogo());
		groupDTO.setCreatedOn(group.getCreatedOn());
		groupDTO.setPrivacy(group.getPrivacy());
		groupDTO.setStatus(group.getStatus());
		groupDTO.setTags(group.getTags());
		groupDTO.setSlugify(group.getSlugify());
		Set<SimGroupHasUser> grpUsers = group.getSimGroupHasUsers();
		if (grpUsers != null) {
			List<SocialGroupUserDTO> guList = new ArrayList<SocialGroupUserDTO>();
			for (SimGroupHasUser groupUser : grpUsers) {
				SocialGroupUserDTO groupUserDTO = new SocialGroupUserDTO();
				groupUserDTO.setId(groupUser.getId());
				groupUserDTO.setUserID(groupUser.getUserId());
				groupUserDTO.setStatus(groupUser.getStatus());
				guList.add(groupUserDTO);
			}
			groupDTO.setSimGroupHasUsers(guList);
		}
		groupDTO.setSocialGroupAttributes(simGroupAttributeMapper.toSocialGroupAttributeDTOList(group.getSimGroupAttributes()));
		return groupDTO;
	}

	/**
	 * Convert SimGroupHasUser Object value to SocialGroupUserDTO
	 *
	 * @param groupUser
	 * @return SocialGroupUserDTO
	 */
	public static SocialGroupUserDTO convertGroupUserModelToDTO(SimGroupHasUser groupUser) {
		SocialGroupUserDTO groupUserDTO = new SocialGroupUserDTO();
		groupUserDTO.setId(groupUser.getId());
		// List<GroupDTO> list = new ArrayList<GroupDTO>();
		// list.add(convertGroupModelToDTO(groupUser.getGroupId()));
		// groupUserDTO.setGroupList(list);
		groupUserDTO.setUserID(groupUser.getUserId());
		groupUserDTO.setJoinedOnDate(groupUser.getJoinedOnDate());
		groupUserDTO.setStatus(groupUser.getStatus());
		return groupUserDTO;
	}

	// /**
	// * Convert Activity model Object value to HomePageActivity DTO
	// *
	// * @param homepageActivity
	// * @return HomePageActivityDTO
	// */
	// public static HomePageActivityDTO
	// convertActivityModelToDTO(SimHomepageActivity homepageActivity) {
	//
	// CategoryDTO categoryDTO = new CategoryDTO();
	// categoryDTO.setId(homepageActivity.getCategoryId().getId());
	// categoryDTO.setName(homepageActivity.getCategoryId().getName());
	// categoryDTO.setIcon(homepageActivity.getCategoryId().getIcon());
	// //postItemDTO.setCategoryDTO(categoryDTO);
	//
	// HomePageActivityDTO homePageActivityDTO = new HomePageActivityDTO();
	// homePageActivityDTO.setTitle(homepageActivity.getTitle());
	// homePageActivityDTO.setDescription(homepageActivity.getDescription());
	// homePageActivityDTO.setBinContent(homepageActivity.getBinContent());
	// homePageActivityDTO.setLink(homepageActivity.getLink());
	// homePageActivityDTO.setCategory(categoryDTO);
	// homePageActivityDTO.setId(homepageActivity.getId());
	// homePageActivityDTO.setCreatedOn(homepageActivity.getCreatedOn());
	// homePageActivityDTO.setCreatedByUserId(homepageActivity.getCreatedByUserId());
	//
	// homePageActivityDTO.setHomepageId(homepageActivity.getHomepageId());
	// if (homepageActivity.getParentHpageActvtId() != null) {
	// homePageActivityDTO.setParentHomePageActivityId(homepageActivity.getParentHpageActvtId().getId());
	// }
	// homePageActivityDTO.setStatus(homepageActivity.getStatus());
	// return homePageActivityDTO;
	// }

	// /**
	// * Convert HomePageActivity DTO To Activity model Object value
	// *
	// * @param homepageActivityDTO
	// * @return SimHomepageActivity
	// */
	// public static SimHomepageActivity
	// convertActivityDTOTOModel(HomePageActivityDTO homepageActivityDTO) {
	//
	// SimHomepageActivity activity = new SimHomepageActivity();
	// activity.setTitle(homepageActivityDTO.getTitle());
	// activity.setDescription(homepageActivityDTO.getDescription());
	// activity.setBinContent(homepageActivityDTO.getBinContent());
	// activity.setLink(homepageActivityDTO.getLink());
	// CategoryDTO categoryDTO = homepageActivityDTO.getCategory();
	// if (categoryDTO != null) {
	// activity.setCategoryId(convertActivityCategoryDTOToModel(categoryDTO));
	// }
	// activity.setId(homepageActivityDTO.getId());
	// activity.setCreatedOn(homepageActivityDTO.getCreatedOn());
	// activity.setCreatedByUserId(homepageActivityDTO.getCreatedByUserId());
	//
	// activity.setHomepageId(homepageActivityDTO.getHomepageId());
	// if (homepageActivityDTO.getParentHomePageActivityId() != null) {
	// SimHomepageActivity parentActivity = new SimHomepageActivity();
	// parentActivity.setId(homepageActivityDTO.getParentHomePageActivityId());
	// activity.setParentHpageActvtId(parentActivity);
	// }
	// activity.setStatus(homepageActivityDTO.getStatus());
	// return activity;
	// }

	/**
	 * Returns array of activity DO as PostItemDTO for a query on
	 * HomePageActivity
	 * 
	 * @param query
	 * @return PostItemDTOs
	 */
	// @SuppressWarnings("unchecked")
	// public static HomePageActivityDTO[]
	// getActivityDTOArrayForHomePageActivityQuery(Query query) {
	// Collection<HomePageActivityDTO> result = new
	// ArrayList<HomePageActivityDTO>();
	//
	// for (Iterator<SimHomepageActivity> itr =
	// query.getResultList().iterator(); itr.hasNext();) {
	// SimHomepageActivity bean = itr.next();
	// result.add(convertActivityModelToDTO(bean));
	// }
	// return result.toArray(new HomePageActivityDTO[result.size()]);
	// }

	/**
	 * Return GroupUserDTOs array for a query on SimGroupHasUser and each
	 * SocialGroupUserDTO contains all SocialGroupDTO for a user if any This
	 * method should be used mostly where list need user and its group list and
	 * order by on user id
	 *
	 * @param query
	 * @return array of user IDs
	 */
	@SuppressWarnings("unchecked")
	public static SocialGroupUserDTO[] sortGroupUserDTOs(Query query) {
		Collection<SocialGroupUserDTO> collection = new ArrayList<SocialGroupUserDTO>();
		// build SocialGroupUserDTO array
		SocialGroupUserDTO groupUserDTO = null;
		for (Iterator<SimGroupHasUser> itr = query.getResultList().iterator(); itr.hasNext();) {
			SimGroupHasUser bean = itr.next();
			groupUserDTO = convertGroupUserModelToDTO(bean);
			collection.add(groupUserDTO);
			
			//TODO Why so complicated
//			if (groupUserDTO == null) {
//				groupUserDTO = convertGroupUserModelToDTO(bean);
//			} else if (bean.getUserId().equals(groupUserDTO.getUserID())) {
//				// List<GroupDTO> list = groupUserDTO.getGroupList();
//				// list.add(convertGroupModelToDTO(bean.getGroupId()));
//				// groupUserDTO.setGroupList(list);
//			} else {
//				collection.add(groupUserDTO);
//				groupUserDTO = convertGroupUserModelToDTO(bean);
//			}
		}
//		// Add groupUserDTO for last iteration
//		if (groupUserDTO != null) {
//			collection.add(groupUserDTO);
//		}
		return collection.toArray(new SocialGroupUserDTO[collection.size()]);
	}

	/**
	 * This method help in converting result from Query results to Collection
	 * 
	 * @param query
	 * @return array of SocialGroupUserDTO
	 */
	public static SocialGroupUserDTO[] getGroupUserDTOArrayForGroupQuery(Query query) {
		Collection<SocialGroupUserDTO> result = new ArrayList<SocialGroupUserDTO>();
		for (Iterator<SimGroupHasUser> itr = query.getResultList().iterator(); itr.hasNext();) {
			SimGroupHasUser bean = itr.next();
			result.add(convertGroupUserModelToDTO(bean));
		}
		return result.toArray(new SocialGroupUserDTO[result.size()]);
	}

	/**
	 * Returns array of Friend DTO provided Friend query
	 * 
	 * @param query
	 * @return PostItemDTOs
	 */
	@SuppressWarnings("unchecked")
	public static FriendDTO[] getFriendDTOArrayForFriendsQuery(Query query, String userID) {
		Collection<FriendDTO> result = new ArrayList<FriendDTO>();
		Iterator<SimFriends> itr = query.getResultList().iterator();
		if (itr != null) {
			while (itr.hasNext()) {
				SimFriends bean = itr.next();
				result.add(convertFriendsModelToDTO(bean, userID));
			}
		}
		return result.toArray(new FriendDTO[result.size()]);
	}

	/**
	 * This returns Friend's IDs Array provided Query
	 * 
	 * @param query
	 * @param userID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String[] getFriendIdsArrayForFriendsQuery(Query query, String userID) {
		Collection<String> result = new ArrayList<String>();

		for (Iterator<SimFriends> itr = query.getResultList().iterator(); itr.hasNext();) {
			SimFriends bean = itr.next();
			if (bean.getUserId().equals(userID)) {
				result.add(bean.getFriendId());
			} else {
				result.add(bean.getUserId());
			}

		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * This method convert Friend model to Friend DTO
	 * 
	 * @param friends
	 * @param userID
	 * @return
	 */
	public static FriendDTO convertFriendsModelToDTO(SimFriends friends, String userID) {
		FriendDTO friendDTO = new FriendDTO();
		friendDTO.setId(friends.getId());
		if (friends.getUserId().equals(userID)) {
			friendDTO.setUserID(friends.getUserId());
			friendDTO.setFriendID(friends.getFriendId());
		} else {
			friendDTO.setUserID(friends.getFriendId());
			friendDTO.setFriendID(friends.getUserId());
		}

		friendDTO.setCreatedOn(friends.getCreatedOn());
		friendDTO.setConfirmedOn(friends.getConfirmedOn());
		return friendDTO;
	}

	/**
	 * convert HomePageActivity to PostItemDTO
	 * 
	 * @param q
	 * @param includeChildren
	 * @return
	 */
	public static PostItemDTO[] SIMHomePageActivity_PostItemDTO(Query q, boolean includeChildren,
			boolean includeBinary) {
		PostItemDTO[] retVal = null;
		List l = q.getResultList();
		if ((l != null) && (l.size() > 0)) {
			retVal = new PostItemDTO[l.size()];
			for (int i = 0; i < l.size(); i++) {
				SimHomepageActivity sha = (SimHomepageActivity) l.get(i);
				PostItemDTO piDTO = SIMHomePageActivity_PostItemDTO(sha, includeChildren, includeBinary);
				retVal[i] = piDTO;
			}

		}

		return retVal;
	}

	/**
	 * convert HomePageActivity_PostItemDTO
	 * 
	 * @param sha
	 * @param includeChildren
	 * @return
	 */
	public static PostItemDTO SIMHomePageActivity_PostItemDTO(SimHomepageActivity sha, boolean includeChildren,
			boolean includeBinary) {
		PostItemDTO piDTO = new PostItemDTO();
		if ((includeBinary) && (sha.getSimHomepageActivityBins() != null)) {
			byte[][] bins = new byte[sha.getSimHomepageActivityBins().size()][];
			int counter = 0;
			for (Iterator<SimHomepageActivityBin> i = sha.getSimHomepageActivityBins().iterator(); i.hasNext();) {
				bins[counter++] = i.next().getBinData();
			}
			piDTO.setBinContents(bins);
		}
		piDTO.setCategoryID(sha.getCategoryId() == null ? null : sha.getCategoryId());
		piDTO.setCreatedOn(sha.getCreatedOn());
		piDTO.setCreatedByUserID(sha.getCreatedByUserId());
		piDTO.setDescription(sha.getDescription());
		piDTO.setHomepageID(sha.getHomepageId());
		piDTO.setId(sha.getId());
		piDTO.setLink(sha.getLink());
		piDTO.setParentHomepageID(sha.getParentHpageActvtId() != null ? sha.getParentHpageActvtId().getId() : null);
		piDTO.setStatus(sha.getStatus());
		piDTO.setTitle(sha.getTitle());
		piDTO.setCategoryIcon(sha.getCategoryIcon());

		if (includeChildren) {
			TreeSet<PostItemDTO> ts = new TreeSet<PostItemDTO>(new Comparator<PostItemDTO>() {
				public int compare(PostItemDTO a, PostItemDTO b) {
					return Long.valueOf(a.getCreatedOn()).compareTo(Long.valueOf(b.getCreatedOn()));
				}
			});
			for (SimHomepageActivity comment : sha.getSimHomepageActivities()) {
				ts.add(SIMHomePageActivity_PostItemDTO(comment, true, includeBinary));
			}
			piDTO.setChildren((PostItemDTO[]) ts.toArray(new PostItemDTO[ts.size()]));
		}

		return piDTO;
	}
}
