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
package com.eurodyn.qlack2.fuse.componentlibrary.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.componentlibrary.api.PermissionsService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentPermissionDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.api.exception.QComponentLibraryException;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadget;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadgetHasPermission;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.util.ConverterUtil;

/**
 * A Stateless Session EJB providing services to manage the permissions a gadget may require. For details regarding the
 * functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA
 */
@Transactional
public class PermissionsServiceImpl implements PermissionsService {
	public static final Logger LOGGER = Logger.getLogger(PermissionsServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-componentlibrary")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

    /**
     * {@inheritDoc}
     * @param gadgetPermission {@inheritDoc}
     * @param gadgetID {@inheritDoc}
     * @param userID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void requestPermission(String gadgetPermission, String gadgetID, String userID) {
        requestPermissions(new String[]{gadgetPermission}, gadgetID, userID);
    }


    /**
     * {@inheritDoc}
     * @param gadgetPermissions {@inheritDoc}
     * @param gadgetID {@inheritDoc}
     * @param userID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void requestPermissions(String[] gadgetPermissions, String gadgetID, String userID) {
        LOGGER.log(Level.FINEST, "Requesting permissions ''{0}'' on behalf of gadget ''{1}'' from user ''{2}''.",
                new String[]{Arrays.deepToString(gadgetPermissions), gadgetID, userID});
        ApiGadget gadget = (ApiGadget) em.find(ApiGadget.class, gadgetID);
    
        for (String permission : gadgetPermissions) {
            em.persist(new ApiGadgetHasPermission(gadget, userID, permission, null));
        }
    }


    /**
     * {@inheritDoc}
     * @param gadgetPermission {@inheritDoc}
     * @param gadgetID {@inheritDoc}
     * @param userID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void grantPermission(String gadgetPermission, String gadgetID, String userID) throws QComponentLibraryException {
        grantPermissions(new String[]{gadgetPermission}, gadgetID, userID);
    }


    /**
     * {@inheritDoc}
     * @param permissionRequestID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void grantPermission(String permissionRequestID) throws QComponentLibraryException {
        ApiGadgetHasPermission ghp = getPermissionRequestByID(permissionRequestID);
        ghp.setEnabled(Boolean.TRUE);
    }


    /**
     * {@inheritDoc}
     * @param permissionRequestID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void revokePermission(String permissionRequestID) throws QComponentLibraryException {
        ApiGadgetHasPermission ghp = getPermissionRequestByID(permissionRequestID);
        ghp.setEnabled(Boolean.FALSE);
    }



    /**
     * {@inheritDoc}
     * @param gadgetPermissions {@inheritDoc}
     * @param gadgetID {@inheritDoc}
     * @param userID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void grantPermissions(String[] gadgetPermissions, String gadgetID, String userID) throws QComponentLibraryException {
        LOGGER.log(Level.FINEST, "Granting permissions {0} to gadget {1} for user {2}.",
                new String[]{Arrays.deepToString(gadgetPermissions), gadgetID, userID});
        for (String permission : gadgetPermissions) {
            ApiGadgetHasPermission ghp = getPermissionRequestByName(permission, gadgetID, userID);
            ghp.setEnabled(Boolean.TRUE);
        }
    }


    /**
     * {@inheritDoc}
     * @param gadgetPermission {@inheritDoc}
     * @param gadgetID {@inheritDoc}
     * @param userID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void revokePermission(String gadgetPermission, String gadgetID, String userID) throws QComponentLibraryException {
        revokePermissions(new String[]{gadgetPermission}, gadgetID, userID);
    }


    /**
     * {@inheritDoc}
     * @param gadgetPermissions {@inheritDoc}
     * @param gadgetID {@inheritDoc}
     * @param userID {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public void revokePermissions(String[] gadgetPermissions, String gadgetID, String userID) throws QComponentLibraryException {
        LOGGER.log(Level.FINEST, "Revoking permissions {0} from gadget {1} for user {2}.",
                new String[]{Arrays.deepToString(gadgetPermissions), gadgetID, userID});
        for (String permission : gadgetPermissions) {
            ApiGadgetHasPermission ghp = getPermissionRequestByName(permission, gadgetID, userID);
            ghp.setEnabled(Boolean.FALSE);
        }
    }


    /**
     * {@inheritDoc}
     * @param gadgetPermission {@inheritDoc}
     * @param gadgetID {@inheritDoc}
     * @param userID {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @Transactional(TxType.REQUIRED)
    public boolean hasPermission(String gadgetPermission, String gadgetID, String userID) {
        LOGGER.log(Level.FINE, "Checking if gadget ''{0}'' has permission ''{1}'' for user ''{2}''.",
                new String[]{gadgetID, gadgetPermission, userID});
        ApiGadget gadget = (ApiGadget) em.find(ApiGadget.class, gadgetID);
    
        Query grantQry = em.createQuery("select gp from ApiGadgetHasPermission gp where "
                + "gp.gadget = :gadget and gp.userId = :user and "
                + "gp.permission = :permission and gp.enabled = :enabled");
        grantQry.setParameter("gadget", gadget);
        grantQry.setParameter("user", userID);
        grantQry.setParameter("permission", gadgetPermission);
        grantQry.setParameter("enabled", true);

        return (grantQry.getResultList().size() == 1);
    }

    @Override
    @Transactional(TxType.REQUIRED)
    public ComponentPermissionDTO[] getPendingPermissionRequests(String userID) {
        List retVal = new ArrayList();
        Query q = em.createQuery("select ghp from ApiGadgetHasPermission ghp "
                               + "inner join ghp.gadget g "
                               + " where ghp.userId = :userID "
                               + "   and ghp.enabled is null "
                               + "order by g.id");
        q.setParameter("userID", userID);
        List<ApiGadgetHasPermission> l = q.getResultList();
        Iterator<ApiGadgetHasPermission> i = l.iterator();
        while (i.hasNext()) {
            ApiGadgetHasPermission apiGadgetHasPermission = i.next();
            ComponentPermissionDTO ComponentPermissionDTO =
                    ConverterUtil.apiGadgetHasPermissionToComponentPermissionDTO(apiGadgetHasPermission);
            ComponentPermissionDTO.setGadgetTitle(apiGadgetHasPermission.getGadget().getTitle());
            ComponentPermissionDTO.setGadgetID(apiGadgetHasPermission.getGadget().getId());

            retVal.add(ComponentPermissionDTO);
        }

        return (ComponentPermissionDTO[]) retVal.toArray(new ComponentPermissionDTO[retVal.size()]);
    }


    @Override
    @Transactional(TxType.REQUIRED)
    public ComponentPermissionDTO getPermissionRequest(String permissionRequestID) {
        return ConverterUtil.apiGadgetHasPermissionToComponentPermissionDTO(em.find(ApiGadgetHasPermission.class, permissionRequestID));
    }

    private ApiGadgetHasPermission getPermissionRequestByID(String permissionRequestID) throws QComponentLibraryException {
        Query q = em.createQuery("select ghp from ApiGadgetHasPermission ghp where ghp.id = :id");
        q.setParameter("id", permissionRequestID);
        List l = q.getResultList();
        if (l.size() > 0) {
            return (ApiGadgetHasPermission)l.get(0);
        } else {
            throw new QComponentLibraryException("Permission request could not be found");
        }
    }

    private ApiGadgetHasPermission getPermissionRequestByName(String permission, String gadgetID, String userID) throws QComponentLibraryException {
        Query q = em.createQuery("select gp from ApiGadgetHasPermission gp where "
                + "gp.gadget.id = :gadgetID and gp.userId = :user and gp.permission = :permission");
        q.setParameter("gadgetID", gadgetID);
        q.setParameter("user", userID);
        q.setParameter("permission", permission);
        List l = q.getResultList();
        if (l.size() > 0) {
            return (ApiGadgetHasPermission)l.get(0);
        } else {
            throw new QComponentLibraryException("Permission request could not be found.");
        }
    }
}