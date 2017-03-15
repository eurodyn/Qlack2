package com.eurodyn.qlack2.fuse.cm.impl.storage;

import java.io.IOException;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.eurodyn.qlack2.fuse.cm.api.VersionService;

@Command(scope = "qlack", name = "test")
@Service
public final class Test implements Action {

	@Reference
	private VersionService versionService;

	@Override
	public Object execute() throws IOException {
		return "ok";
	}

}
