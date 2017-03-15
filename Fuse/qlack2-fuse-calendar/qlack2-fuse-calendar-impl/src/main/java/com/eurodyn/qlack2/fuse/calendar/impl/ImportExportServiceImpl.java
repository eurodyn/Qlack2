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
package com.eurodyn.qlack2.fuse.calendar.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.FmtType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.fuse.calendar.api.ImportExportService;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarConstants;
import com.eurodyn.qlack2.fuse.calendar.api.dto.ParticipantDTO;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QCalendarException;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QCalendarNotExists;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QParsingError;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QValidationError;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalCalendar;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalItem;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalParticipant;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalSupportingObject;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class ImportExportServiceImpl implements ImportExportService {
	private static final Logger LOGGER = Logger
			.getLogger(ImportExportServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-calendar")
	private EntityManager em;
	private String[] participantStatus = { "DECLINED", "ACCEPTED",
			"NEEDS-ACTION", "TENTATIVE" };
	private static final String calSearchNode = "search_cals/";

	private String exportProdID;
	private String exportUIDSuffix;
	private String exportItemCategory;
	private String exportContactID;
	private String exportCreatedBy;
	private String exportLastModifiedBy;
	private String exportAllDay;
	private String exportParticipantID;
	private String exportSupportingObjectFilename;
	private String exportSupportingObjectObjectLink;
	private String exportSupportingObjectCreatedOn;
	private String exportSupportingObjectCreatedBy;
	private String exportSupportingObjectLastModifiedOn;
	private String exportSupportingObjectLastModifiedBy;
	private String exportSupportingObjectCategory;
	private String exportSupportingObjectObjectId;

	public void setExportProdID(String exportProdID) {
		this.exportProdID = exportProdID;
	}

	public void setExportUIDSuffix(String exportUIDSuffix) {
		this.exportUIDSuffix = exportUIDSuffix;
	}

	public void setExportItemCategory(String exportItemCategory) {
		this.exportItemCategory = exportItemCategory;
	}

	public void setExportContactID(String exportContactID) {
		this.exportContactID = exportContactID;
	}

	public void setExportCreatedBy(String exportCreatedBy) {
		this.exportCreatedBy = exportCreatedBy;
	}

	public void setExportLastModifiedBy(String exportLastModifiedBy) {
		this.exportLastModifiedBy = exportLastModifiedBy;
	}

	public void setExportAllDay(String exportAllDay) {
		this.exportAllDay = exportAllDay;
	}

	public void setExportParticipantID(String exportParticipantID) {
		this.exportParticipantID = exportParticipantID;
	}

	public void setExportSupportingObjectFilename(
			String exportSupportingObjectFilename) {
		this.exportSupportingObjectFilename = exportSupportingObjectFilename;
	}

	public void setExportSupportingObjectObjectLink(
			String exportSupportingObjectObjectLink) {
		this.exportSupportingObjectObjectLink = exportSupportingObjectObjectLink;
	}

	public void setExportSupportingObjectCreatedOn(
			String exportSupportingObjectCreatedOn) {
		this.exportSupportingObjectCreatedOn = exportSupportingObjectCreatedOn;
	}

	public void setExportSupportingObjectCreatedBy(
			String exportSupportingObjectCreatedBy) {
		this.exportSupportingObjectCreatedBy = exportSupportingObjectCreatedBy;
	}

	public void setExportSupportingObjectLastModifiedOn(
			String exportSupportingObjectLastModifiedOn) {
		this.exportSupportingObjectLastModifiedOn = exportSupportingObjectLastModifiedOn;
	}

	public void setExportSupportingObjectLastModifiedBy(
			String exportSupportingObjectLastModifiedBy) {
		this.exportSupportingObjectLastModifiedBy = exportSupportingObjectLastModifiedBy;
	}

	public void setExportSupportingObjectCategory(
			String exportSupportingObjectCategory) {
		this.exportSupportingObjectCategory = exportSupportingObjectCategory;
	}

	public void setExportSupportingObjectObjectId(
			String exportSupportingObjectObjectId) {
		this.exportSupportingObjectObjectId = exportSupportingObjectObjectId;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	private CalCalendar retrieveCalendar(String calendarId)
			throws QCalendarException {
		CalCalendar calendar = em.find(CalCalendar.class, calendarId);
		if (calendar == null) {
			throw new QCalendarNotExists(
					"Calendar with id " + calendarId + " does not exist.");
		}
		return calendar;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String exportCalendarItems(String calendarId, Date startDate,
			Date endDate, String[] categoryIds, boolean includeSupportingObjects)
			throws QCalendarException {
		LOGGER.log(
				Level.FINEST,
				"Exporting calendar items from calendar with id {0}, from {1} to {2}",
				new String[] { calendarId,
						(startDate != null ? startDate.toString() : "null"),
						(endDate != null ? endDate.toString() : "null") });

		String queryString = "SELECT i FROM CalItem i WHERE i.calendarId.id = :calendarId";
		if (categoryIds != null) {
			queryString = queryString
					.concat(" AND i.categoryId in (:categories)");
		}
		if (startDate != null) {
			queryString = queryString.concat(" AND i.startTime >= :startDate");
		}
		if (endDate != null) {
			queryString = queryString.concat(" AND i.startTime <= :endDate");
		}

		Query query = em.createQuery(queryString);
		query.setParameter("calendarId", calendarId);
		if (categoryIds != null) {
			//TODO check the conversion works
			query.setParameter("categories", Arrays.asList(categoryIds));
//					ArraysHelper.arrayToList(categoryIds));
		}
		if (startDate != null) {
			query.setParameter("startDate", startDate.getTime());
		}
		if (endDate != null) {
			query.setParameter("endDate", endDate.getTime());
		}

		List<CalItem> itemEntities = query.getResultList();
		LOGGER.log(Level.FINEST, "Retrieved {0} items", itemEntities.size());
		if (itemEntities.isEmpty()) {
			return null;
		}

		Calendar ical = new Calendar();
		ical.getProperties().add(new ProdId(exportProdID));
		ical.getProperties().add(Version.VERSION_2_0);
		ical.getProperties().add(CalScale.GREGORIAN);

		for (CalItem item : itemEntities) {
			DateTime start = new DateTime(item.getStartTime());
			DateTime end = new DateTime(item.getEndTime());
			VEvent icalEvent = new VEvent(start, end, item.getName());

			icalEvent.getStartDate().setUtc(true);
			icalEvent.getEndDate().setUtc(true);

			String uid = item.getId();
			if (!StringUtils.isEmpty(exportUIDSuffix)) {
				uid = uid.concat("@" + exportUIDSuffix);
			}
			icalEvent.getProperties().add(new Uid(uid));
			icalEvent.getProperties().add(
					new XProperty(exportItemCategory, item.getCategoryId()));
			icalEvent.getProperties().add(
					new Description(item.getDescription()));
			icalEvent.getProperties().add(new Location(item.getLocation()));
			Property contact = new Contact();
			contact.getParameters()
					.add(new XParameter(exportContactID, item.getContactId()));
			icalEvent.getProperties().add(contact);
			Property created = new Created(new DateTime(item.getCreatedOn()));
			created.getParameters()
					.add(new XParameter(exportCreatedBy, item.getCreatedBy()));
			icalEvent.getProperties().add(created);
			Property lastModified = new LastModified(new DateTime(
					item.getLastModifiedOn()));
			lastModified
					.getParameters()
					.add(new XParameter(
							exportLastModifiedBy,
							item.getLastModifiedBy()));
			icalEvent.getProperties().add(lastModified);
			icalEvent.getProperties().add(
					new XProperty(exportAllDay,
							String.valueOf(item.isAllDay())));

			if (item.getCalParticipants() != null) {
				for (CalParticipant participant : item.getCalParticipants()) {
					Property attendee = new Attendee();
					attendee.getParameters()
							.add(new XParameter(
									exportParticipantID,
									participant.getParticipantId()));
					attendee.getParameters().add(
							new PartStat(participantStatus[participant
									.getStatus()]));
					icalEvent.getProperties().add(attendee);
				}
			}

			if ((includeSupportingObjects)
					&& (item.getCalSupportingObjects() != null)) {
				for (CalSupportingObject object : item
						.getCalSupportingObjects()) {
					Property attach;
					if (object.getObjectData() != null) {
						attach = new Attach(object.getObjectData());
						attach.getParameters()
								.add(new XParameter(
										exportSupportingObjectFilename,
										object.getFilename()));
						attach.getParameters().add(
								new FmtType(object.getMimetype()));
					} else {
						attach = new Attach();
					}
					attach.getParameters()
							.add(new XParameter(
									exportSupportingObjectObjectLink,
									object.getLink()));
					attach.getParameters()
							.add(new XParameter(
									exportSupportingObjectCreatedOn,
									String.valueOf(object.getCreatedOn())));
					attach.getParameters()
							.add(new XParameter(
									exportSupportingObjectCreatedBy,
									object.getCreatedBy()));
					attach.getParameters()
							.add(new XParameter(
									exportSupportingObjectLastModifiedOn,
									String.valueOf(object.getLastModifiedOn())));
					attach.getParameters()
							.add(new XParameter(
									exportSupportingObjectLastModifiedBy,
									object.getLastModifiedBy()));
					attach.getParameters()
							.add(new XParameter(
									exportSupportingObjectCategory,
									object.getSupportingObjectCategoryId()));
					attach.getParameters()
							.add(new XParameter(
									exportSupportingObjectObjectId,
									object.getObjectId()));
					icalEvent.getProperties().add(attach);
				}
			}

			ical.getComponents().add(icalEvent);
		}

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		CalendarOutputter calendarOut = new CalendarOutputter();
		try {
			calendarOut.output(ical, outStream);
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QCalendarException(ex.getLocalizedMessage());
		} catch (ValidationException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QValidationError(ex.getLocalizedMessage());
		}
		return outStream.toString();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void importCalendarItems(String calendarId, String importData,
			short onClashBehaviour) throws QCalendarException {
		LOGGER.log(Level.FINEST,
				"Importing calendar items to calendar with id {0}", calendarId);

		try {
			CalCalendar calendarEntity = retrieveCalendar(calendarId);

			StringReader sin = new StringReader(importData);
			CalendarBuilder builder = new CalendarBuilder();
			Calendar calendar = builder.build(sin);

			if (calendar.getComponents() != null) {
				Iterator components = calendar.getComponents().iterator();
				while (components.hasNext()) {
					Object component = components.next();
					if (component instanceof VEvent) {
						VEvent icalEvent = (VEvent) component;
						boolean addItem = true;

						// Retrieve event uuid (if one exists)
						String eventUid = icalEvent.getUid().getValue();
						String uuid = null;
						if (!StringUtils
								.isEmpty(exportUIDSuffix)) {
							int uidEnd = eventUid.indexOf("@");
							if (uidEnd > -1) {
								uuid = eventUid.substring(0, uidEnd);
							}
						} else {
							uuid = eventUid;
						}

						// Retrieve existing event (if one exists)
						CalItem itemEntity = null;
						if (uuid != null) {
							itemEntity = em.find(CalItem.class, uuid);
							if ((itemEntity == null)
									|| (!itemEntity.getCalendarId().getId()
											.equals(calendarId))) {
								uuid = null; // The item does not exist in the
												// specified calendar so it will
												// be inserted with a new id
							} else {
								switch (onClashBehaviour) {
								case CalendarConstants.ON_CLASH_IGNORE:
									addItem = false;
									break;
								case CalendarConstants.ON_CLASH_REPLACE:
									break;
								case CalendarConstants.ON_CLASH_KEEP_LATEST:
									long importedModifiedOn = icalEvent
											.getLastModified().getDateTime()
											.getTime();
									long existingModifiedOn = itemEntity
											.getLastModifiedOn();
									if (importedModifiedOn <= existingModifiedOn) {
										addItem = false;
									}
									break;
								}
							}
						}

						// Add event in db
						if (addItem) {
							if (uuid == null) {
								itemEntity = new CalItem();
							}
							itemEntity.setCalendarId(calendarEntity);
							itemEntity
									.setCategoryId(icalEvent
											.getProperty(
													exportItemCategory)
											.getValue());
							Property contact = icalEvent
									.getProperty(Property.CONTACT);
							if ((contact != null)
									&& (contact
											.getParameter(exportContactID) != null)) {
								itemEntity
										.setContactId(contact
												.getParameter(
														exportContactID)
												.getValue());
							}
							itemEntity
									.setAllDay(Boolean
											.parseBoolean(icalEvent
													.getProperty(
															exportAllDay)
													.getValue()));
							itemEntity
									.setCreatedBy(icalEvent
											.getCreated()
											.getParameter(
													exportCreatedBy)
											.getValue());
							itemEntity.setCreatedOn(icalEvent.getCreated()
									.getDateTime().getTime());
							if (icalEvent.getDescription() != null) {
								itemEntity.setDescription(icalEvent
										.getDescription().getValue());
							}
							itemEntity.setEndTime(icalEvent.getEndDate()
									.getDate().getTime());
							itemEntity
									.setLastModifiedBy(icalEvent
											.getLastModified()
											.getParameter(
													exportLastModifiedBy)
											.getValue());
							itemEntity.setLastModifiedOn(icalEvent
									.getLastModified().getDateTime().getTime());
							itemEntity.setLocation(icalEvent.getLocation()
									.getValue());
							itemEntity.setName(icalEvent.getSummary()
									.getValue());
							itemEntity.setStartTime(icalEvent.getStartDate()
									.getDate().getTime());

							if (uuid == null) {
								em.persist(itemEntity);
							} else { // If the item is to be inserted remove the
										// existing participants and objects in
										// order to insert the new ones
								if (itemEntity.getCalParticipants() != null) {
									for (CalParticipant participant : itemEntity
											.getCalParticipants()) {
										em.remove(participant);
									}
								}
								if (itemEntity.getCalSupportingObjects() != null) {
									for (CalSupportingObject object : itemEntity
											.getCalSupportingObjects()) {
										em.remove(object);
									}
								}
							}

							PropertyList attendees = icalEvent
									.getProperties(Property.ATTENDEE);
							if (attendees != null) {
								Iterator it = attendees.iterator();
								while (it.hasNext()) {
									Property attendee = (Property) it.next();
									CalParticipant participantEntity = new CalParticipant();
									participantEntity.setItemId(itemEntity);
									participantEntity
											.setParticipantId(attendee
													.getParameter(
															exportParticipantID)
													.getValue());
									participantEntity
											.setStatus(ParticipantDTO.PARTICIPANT_PENDING);
									if (attendee
											.getParameter(Parameter.PARTSTAT) != null) {
										String attendeeStatus = attendee
												.getParameter(
														Parameter.PARTSTAT)
												.getValue();
										if (attendeeStatus.equals("DECLINED")) {
											participantEntity
													.setStatus(ParticipantDTO.PARTICIPANT_NOT_ATTENDING);
										} else if (attendeeStatus
												.equals("ACCEPTED")) {
											participantEntity
													.setStatus(ParticipantDTO.PARTICIPANT_ATTENDING);
										}
									}
									em.persist(participantEntity);
								}
							}

							PropertyList attachments = icalEvent
									.getProperties(Property.ATTACH);
							if (attachments != null) {
								Iterator it = attachments.iterator();
								while (it.hasNext()) {
									Attach attachment = (Attach) it.next();
									CalSupportingObject objectEntity = new CalSupportingObject();
									objectEntity.setItemId(itemEntity);
									objectEntity
											.setSupportingObjectCategoryId(attachment
													.getParameter(
															exportSupportingObjectCategory)
													.getValue());
									objectEntity.setObjectData(attachment
											.getBinary());
									if (attachment
											.getParameter(exportSupportingObjectObjectLink) != null) {
										objectEntity
												.setLink(attachment
														.getParameter(
																exportSupportingObjectObjectLink)
														.getValue());
									}
									if (attachment
											.getParameter(exportSupportingObjectCreatedOn) != null) {
										objectEntity
												.setCreatedOn(Long
														.valueOf(attachment
																.getParameter(
																		exportSupportingObjectCreatedOn)
																.getValue()));
									}
									if (attachment
											.getParameter(exportSupportingObjectCreatedBy) != null) {
										objectEntity
												.setCreatedBy(attachment
														.getParameter(
																exportSupportingObjectCreatedBy)
														.getValue());
									}
									if (attachment
											.getParameter(exportSupportingObjectLastModifiedOn) != null) {
										objectEntity
												.setLastModifiedOn(Long
														.valueOf(attachment
																.getParameter(
																		exportSupportingObjectLastModifiedOn)
																.getValue()));
									}
									if (attachment
											.getParameter(exportSupportingObjectCreatedBy) != null) {
										objectEntity
												.setLastModifiedBy(attachment
														.getParameter(
																exportSupportingObjectCreatedBy)
														.getValue());
									}
									if (attachment
											.getParameter(exportSupportingObjectFilename) != null) {
										objectEntity
												.setFilename(attachment
														.getParameter(
																exportSupportingObjectFilename)
														.getValue());
									}
									if (attachment
											.getParameter(Parameter.FMTTYPE) != null) {
										objectEntity
												.setMimetype(attachment
														.getParameter(
																Parameter.FMTTYPE)
														.getValue());
									}
									if (attachment
											.getParameter(exportSupportingObjectObjectId) != null) {
										objectEntity
												.setObjectId(attachment
														.getParameter(
																exportSupportingObjectObjectId)
														.getValue());
									}
								}
							}
						}
					}
				}
			}
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QCalendarException(ex.getLocalizedMessage());
		} catch (ParserException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QParsingError(ex.getLocalizedMessage());
		}
	}

}
