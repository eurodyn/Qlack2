package com.eurodyn.qlack2.webdesktop.apps.myphoto.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import com.eurodyn.qlack2.common.util.util.TokenHolder;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicketHolder;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.apps.myphoto.api.Constants;
import com.eurodyn.qlack2.webdesktop.apps.myphoto.api.MyPhotoService;
import com.google.common.io.ByteStreams;

@Singleton
@Transactional
@OsgiServiceProvider(classes = { MyPhotoService.class })
public class MyPhotoServiceImpl implements MyPhotoService {
	private static final Logger LOGGER = Logger.getLogger(MyPhotoServiceImpl.class.getName());
	private final String DEFAULT_AVATAR_FILE = "binary/default-user.png";

	@Inject
	@OsgiService
	private UserService userService;

	@Inject
	private BundleContext context;

	private static byte[] defaultUserAvatar;

	@PostConstruct
	public void init() {
		// Cache default user avatar.
		try (InputStream resourceAsStream = context.getBundle().adapt(BundleWiring.class).getClassLoader()
				.getResourceAsStream(DEFAULT_AVATAR_FILE)) {
			defaultUserAvatar = ByteStreams.toByteArray(resourceAsStream);
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "Could not read default user photo.", ex);
			defaultUserAvatar = new byte[0];
		}
	}

	@Override
	@ValidateTicketHolder
	public void updateMyPhoto(byte[] photo) {
		UserAttributeDTO attribute = new UserAttributeDTO();
		attribute.setUserId(SignedTicket.fromVal(TokenHolder.getToken()).getUserID());
		attribute.setName(Constants.AAA_PHOTO_ATTRIBUTE);
		attribute.setBinData(photo);
		attribute.setContentType("image/png");

		userService.updateAttribute(attribute, true);
	}

	@Override
	@ValidateTicketHolder
	public byte[] getMyPhoto() {
		// Get user photo, or a default photo in case a photo has not been
		// uploaded.
		UserAttributeDTO attribute = userService.getAttribute(SignedTicket.fromVal(TokenHolder.getToken()).getUserID(),
				Constants.AAA_PHOTO_ATTRIBUTE);
		if (attribute != null) {
			return attribute.getBinData();
		} else {
			return defaultUserAvatar;
		}
	}

}
