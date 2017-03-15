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
package com.eurodyn.qlack2.fuse.lexicon.impl.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "lex_template")
public class Template {
	@Id
	private String id;
	@Version
	private long dbversion;
	private String name;
	private String content;
	@ManyToOne
	@JoinColumn(name = "language_id")
	private Language language;

	public Template() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public static Template find(String templateID, EntityManager em) {
		return em.find(Template.class, templateID);
	}
	
	public static List<Template> findByName(String templateName, EntityManager em) {
		Query q = em.createQuery("SELECT t FROM Template t WHERE t.name = :name");
        q.setParameter("name", templateName);
        List<Template> resultList = q.getResultList();
        return resultList;
	}

	public static Template findByNameAndLanguageId(String templateName, String languageId, EntityManager em) {
		Query q;
		if (languageId == null) {
			q = em.createQuery("SELECT t FROM Template t WHERE t.name = :name AND t.language.id is null");
		} else {
			q = em.createQuery("SELECT t FROM Template t WHERE t.name = :name AND t.language.id = :languageId");
			q.setParameter("languageId", languageId);
		}
		
        q.setParameter("name", templateName);        
        List<Template> resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
	}
	
	public static Template findByNameAndLocale(String templateName, String locale, EntityManager em) {
		Query q = em.createQuery("SELECT t FROM Template t WHERE t.name = :name AND t.language.locale = :locale");
        q.setParameter("name", templateName);
        q.setParameter("locale", locale);
        List<Template> resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
	}

}
