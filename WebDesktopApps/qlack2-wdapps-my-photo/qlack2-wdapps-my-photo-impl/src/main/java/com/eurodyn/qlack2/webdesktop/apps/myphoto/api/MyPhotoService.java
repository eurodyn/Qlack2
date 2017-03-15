package com.eurodyn.qlack2.webdesktop.apps.myphoto.api;

public interface MyPhotoService {
	void updateMyPhoto(byte[] photo);

	byte[] getMyPhoto();
}
