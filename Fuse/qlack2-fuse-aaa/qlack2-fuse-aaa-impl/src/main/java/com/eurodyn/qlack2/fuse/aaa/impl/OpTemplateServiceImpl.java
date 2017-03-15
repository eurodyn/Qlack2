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
package com.eurodyn.qlack2.fuse.aaa.impl;

import com.eurodyn.qlack2.fuse.aaa.api.OpTemplateService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplate;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplateHasOperation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Operation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Resource;
import com.eurodyn.qlack2.fuse.aaa.impl.util.ConverterUtil;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * @author European Dynamics SA
 */
@Transactional
@Singleton
@OsgiServiceProvider(classes = {OpTemplateService.class})
public class OpTemplateServiceImpl implements OpTemplateService {
    @PersistenceContext(unitName = "fuse-aaa")
    private EntityManager em;

    @Override
    public String createTemplate(OpTemplateDTO templateDTO) {
        OpTemplate template = new OpTemplate();
        template.setName(templateDTO.getName());
        template.setDescription(templateDTO.getDescription());
        em.persist(template);
        return template.getId();
    }

    @Override
    public void deleteTemplateByID(String templateID) {
        em.remove(OpTemplate.find(templateID, em));
    }

    @Override
    public void deleteTemplateByName(String templateName) {
        em.remove(OpTemplate.findByName(templateName, em));
    }

    @Override
    public OpTemplateDTO getTemplateByID(String templateID) {
        return ConverterUtil.opTemplateToOpTemplateDTO(OpTemplate.find(templateID, em));
    }

    @Override
    public OpTemplateDTO getTemplateByName(String templateName) {
        return ConverterUtil.opTemplateToOpTemplateDTO(OpTemplate.findByName(templateName, em));
    }

    @Override
    public void addOperation(String templateID, String operationName,
                             boolean isDeny) {
        OpTemplateHasOperation tho = OpTemplateHasOperation.findByTemplateIDAndOperationName(templateID, operationName, em);
        if (tho != null) {
            tho.setDeny(isDeny);
        } else {
            OpTemplate template = OpTemplate.find(templateID, em);
            Operation operation = Operation.findByName(operationName, em);
            tho = new OpTemplateHasOperation();
            tho.setDeny(isDeny);
            template.addOpTemplateHasOperation(tho);
            operation.addOpTemplateHasOperation(tho);
            em.persist(tho);
        }
    }

    @Override
    public void addOperation(String templateID, String operationName,
                             String resourceID, boolean isDeny) {
        OpTemplateHasOperation tho = OpTemplateHasOperation.findByTemplateAndResourceIDAndOperationName(
                templateID, operationName, resourceID, em);
        if (tho != null) {
            tho.setDeny(isDeny);
        } else {
            OpTemplate template = OpTemplate.find(templateID, em);
            Operation operation = Operation.findByName(operationName, em);
            Resource resource = Resource.find(resourceID, em);
            tho = new OpTemplateHasOperation();
            tho.setDeny(isDeny);
            template.addOpTemplateHasOperation(tho);
            operation.addOpTemplateHasOperation(tho);
            resource.addOpTemplateHasOperation(tho);
            em.persist(tho);
        }
    }

    @Override
    public void removeOperation(String templateID, String operationName) {
        OpTemplateHasOperation tho = OpTemplateHasOperation.findByTemplateIDAndOperationName(
                templateID, operationName, em);
        em.remove(tho);
    }

    @Override
    public void removeOperation(String templateID, String operationName,
                                String resourceID) {
        OpTemplateHasOperation tho = OpTemplateHasOperation.findByTemplateAndResourceIDAndOperationName(
                templateID, operationName, resourceID, em);
        em.remove(tho);
    }

    @Override
    public Boolean getOperationAccess(String templateID, String operationName) {
        Boolean retVal = null;

        OpTemplateHasOperation tho = OpTemplateHasOperation.findByTemplateIDAndOperationName(
                templateID, operationName, em);
        if (tho != null) {
            retVal = tho.isDeny();
        }

        return retVal;
    }

    @Override
    public Boolean getOperationAccess(String templateID, String operationName,
                                      String resourceID) {
        Boolean retVal = null;

        OpTemplateHasOperation tho = OpTemplateHasOperation.findByTemplateAndResourceIDAndOperationName(
                templateID, operationName, resourceID, em);
        if (tho != null) {
            retVal = tho.isDeny();
        }

        return retVal;
    }

    @Override
    public boolean updateTemplate(OpTemplateDTO templateDTO) {
        boolean retVal = false;
        OpTemplate template = em.find(OpTemplate.class, templateDTO.getId());
        if (template != null) {
            template.setDescription(templateDTO.getDescription());
            template.setName(templateDTO.getName());
            retVal = true;
        }

        return retVal;
    }
}