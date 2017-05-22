package com.eurodyn.qlack2.fuse.cm.it;

import com.eurodyn.qlack2.fuse.cm.api.dto.FolderDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.FileDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;

import java.util.Map;
import java.util.UUID;
import java.util.Date;
import java.util.*;

public class TestUtilities {

    public static FolderDTO createFolderDTO(){
        FolderDTO folderDTO = new FolderDTO();
        String id = UUID.randomUUID().toString();
        folderDTO.setId(id);
        folderDTO.setName(TestConst.generateRandomString());
        folderDTO.setCreatedBy(TestConst.generateRandomString());
        folderDTO.setCreatedOn(new Date().getTime());
        folderDTO.setLastModifiedBy(TestConst.generateRandomString());
        folderDTO.setLastModifiedOn(new Date().getTime());
        folderDTO.setLockable(true);
        folderDTO.setLocked(false);
        folderDTO.setParentId(id);

        createNodeDTO();

        Set<NodeDTO> child = new HashSet<>();
        child.add(createNodeDTO());

        folderDTO.setChildren(child);

        Map<String,String> attr = new HashMap<String, String>();
        attr.put("two","test-two");

        folderDTO.setAttributes(attr);

        return folderDTO;
    }

    public static FileDTO createFileDTO(){
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(UUID.randomUUID().toString());
        fileDTO.setName(TestConst.generateRandomString());
        fileDTO.setCreatedBy(TestConst.generateRandomString());
        fileDTO.setCreatedOn(new Date().getTime());
        fileDTO.setLastModifiedBy(TestConst.generateRandomString());
        fileDTO.setLastModifiedOn(new Date().getTime());
        fileDTO.setLockable(true);
        fileDTO.setLocked(false);
        fileDTO.setMimetype(TestConst.generateRandomString());

        return fileDTO;
    }

    public static NodeDTO createNodeDTO(){
        NodeDTO nodeDTO = new NodeDTO();
        nodeDTO.setId(UUID.randomUUID().toString());
        nodeDTO.setName(TestConst.generateRandomString());
        nodeDTO.setCreatedBy(TestConst.generateRandomString());
        nodeDTO.setCreatedOn(new Date().getTime());
        nodeDTO.setLastModifiedBy(TestConst.generateRandomString());
        nodeDTO.setLastModifiedOn(new Date().getTime());
        nodeDTO.setLockable(true);
        nodeDTO.setLocked(false);
        nodeDTO.setParentId(UUID.randomUUID().toString());

        return nodeDTO;
    }

    public static VersionDTO createVersionDTO(){
        VersionDTO versionDTO = new VersionDTO();
        versionDTO.setId(UUID.randomUUID().toString());
        versionDTO.setName(UUID.randomUUID().toString());
        versionDTO.setCreatedBy(TestConst.generateRandomString());
        versionDTO.setCreatedOn(new Date().getTime());
        versionDTO.setLastModifiedBy(TestConst.generateRandomString());
        versionDTO.setLastModifiedOn(new Date().getTime());
        versionDTO.setMimetype("mimetype");

        Map<String,String> atr = new HashMap<String, String>();
        atr.put("1","attr1-test");

        versionDTO.setAttributes(atr);

        return versionDTO;
    }

}
