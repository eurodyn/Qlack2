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
package com.eurodyn.qlack2.fuse.lexicon.api;

import com.eurodyn.qlack2.fuse.lexicon.api.criteria.KeySearchCriteria;
import com.eurodyn.qlack2.fuse.lexicon.api.criteria.KeySearchCriteria.SortType;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface KeyService {
	/**
     * Creates a new key into Lexicon. If the caller has populated the 
     * translations in the KeyDTO object passed to this object then this method
     * also creates the corresponding translations for this key. 
     * for which a translation is not specified by the caller.
     * @param key The information of the key to be created.
     * @param createDefaultTranslations If true default translations
     * using the key name as translation are created for all existing languages.
     * @return The ID of the newly created key.
     * does not exist. Please note that specifying a group for a lexicon key is optional.
     */
    String createKey(KeyDTO key, boolean createDefaultTranslations);
    
    /**
     * Creates new keys into Lexicon. If the caller has populated the 
     * translations in the KeyDTO object passed to this object then this method
     * also creates the corresponding translations for this key. 
     * for which a translation is not specified by the caller.
     * @param keys The information of the keys to be created.
     * @param createDefaultTranslations If true default translations
     * using the keys name as translation are created for all existing languages.
     * @return The IDs of the newly created keys.
     * does not exist. Please note that specifying a group for a lexicon key is optional.
     */
    List<String> createKeys(List<KeyDTO> keys, boolean createDefaultTranslations);

    /**
	 * Deletes a key from the Lexicon. Note that all translations for this key are also
	 * deleted permanently.
	 * @param keyID The id of the key to delete.
	 */
	void deleteKey(String keyID);
	
	/**
	 * Deletes a collection of keys from the Lexicon. Note that all translations for these keys are also
	 * deleted permanently.
	 * @param keyIDs The ids of the key to delete.
	 */
	void deleteKeys(Collection<String> keyIDs);
	
	/**
	 * Deletes all keys of group with given id. Note that all translations for these keys are also
	 * deleted permanently.
	 * @param groupId The id of group.
	 */
	void deleteKeysByGroupId(String groupId);

	/**
	 * Renames an Lexicon key.
	 * @param keyID The ID of the key to be renamed.
	 * @param newName The new name of the key to be renamed to.
	 */
	void renameKey(String keyID, String newName);
	
	/**
	 * Moves a key under a new translation group
	 * @param keyID The ID of the key to move
	 * @param newGroupId The ID of the new group of the key or null if
	 * the key should be left without a group
	 */
	void moveKey(String keyID, String newGroupId);
	
	/**
	 * Moves a collection of keys under a new translation group
	 * @param keyIDs The IDs of the keys to move
	 * @param newGroupId The ID of the new group of the keys or null if
	 * the keys should be left without a group
	 */
	void moveKeys(Collection<String> keyIDs, String newGroupId);

	/**
	 * Returns the information held about a particular key.
	 * @param keyID The id of the key to return the information about.
	 * @param includeTranslations Whether to also retrieve the translations
	 * available for this key.
	 * @return The specified key information.
	 */
	KeyDTO getKeyByID(String keyID, boolean includeTranslations);

	/**
	 * Returns the information held about a particular key.
	 * @param keyName The name of the key to return the information about.
	 * @param groupId The ID of the group in which to search for the key.
	 * Keep in mind that key names are unique only within a translation group.
	 * @param includeTranslations Whether to also retrieve the translations
	 * available for this key.
	 * @return The specified key information.
	 */
	KeyDTO getKeyByName(String keyName, String groupId, boolean includeTranslations);

	/**
	 * Finds and retrieves Lexicon keys based on a set of search criteria
	 * @param criteria The search criteria based on which to retrieve the keys
	 * @param includeTranslations Whether to also retrieve the translations
	 * available for this key.
	 * @return A list of the retrieved keys sorted by key name (ascending
	 * or descending based on the search criteria).
	 */
	List<KeyDTO> findKeys(KeySearchCriteria criteria, boolean includeTranslations);

	/**
     * Updates an existing translation to a new value. If a translation for the 
     * specified key/language combination does not already exist then a new
     * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
     * @param keyID The ID of the key to be updated.
     * @param languageID The ID of the language for which the translation is updated.
     * @param value The new value of the translation.

     */
    void updateTranslation(String keyID, String languageID, String value);
    
    /**
     * Updates an existing translation to a new value. If a translation for the 
     * specified key/language combination does not already exist then a new
     * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
     * @param keyName The name of the key to be updated.
     * @param groupID The ID of the group in which the key will be updated
     * @param languageID The ID of the language for which the translation is updated.
     * @param value The new value of the translation.

     */
    void updateTranslationByKeyName(String keyName, String groupID, String languageID, String value);
    
    /**
     * Updates existing translation to a new value. If translation for the 
     * specified groupId/key/language combination does not already exist then a new
     * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
     * @param keyName The name of the key to be updated.
     * @param value The new value of the translation.
     * @param groupId The ID of the group in which the key will be updated
     * @param languageId The ID of the language for which the translation is updated.
     */
    void updateTranslationByGroupId(String keyName, String value, String groupId, String languageId);
    
    /**
     * Updates existing translations to a new value. If translation for the 
     * specified groupId/key/language combination does not already exist then a new
     * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
     * @param keys List of keys to be updated.
     * @param groupId The ID of the group in which the key will be updated
     * @param languageId The ID of the language for which the translation is
	 *                      updated.
     */
    void updateTranslationsByGroupId(Map<String, String> keys, String groupId, String languageId);
    
    /**
     * Updates an existing translation to a new value. If a translation for the 
     * specified key/language combination does not already exist then a new
     * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
     * @param keyID The ID of the key to be updated.
     * @param locale The locale of the language for which the translation is updated.
     * @param value The new value of the translation.

     */
    void updateTranslationByLocale(String keyID, String locale, String value);
    
    /**
	 * Updates a set of existing translations to new values. If a translation for the 
	 * specified key/language combination does not already exist then a new
	 * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
	 * @param keyID The ID of the key for which the translations are updated.
	 * @param translations The translations to be updated. The key of the translations
	 * map is the ID of the language to update while the value is the new translation.
	
	 */
	void updateTranslationsForKey(String keyID, Map<String, String> translations);
	
	/**
	 * Updates a set of existing translations to new values. If a translation for the 
	 * specified key/language combination does not already exist then a new
	 * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
	 * @param keyID The ID of the key for which the translations are updated.
	 * @param translations The translations to be updated. The key of the translations
	 * map is the locale to update while the value is the new translation.
	
	 */
	void updateTranslationsForKeyByLocale(String keyID, Map<String, String> translations);

	/**
     * Updates a set of existing translations to new values. If a translation for the 
     * specified key/language combination does not already exist then a new
     * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
     * @param languageID The ID of the language for which the translations are updated.
     * @param translations The translations to be updated. The key of the translations
     * map is the ID of the key to update while the value is the new translation.

     */
    void updateTranslationsForLanguage(String languageID, Map<String, String> translations);
    
    /**
     * Updates a set of existing translations to new values. If a translation for the 
     * specified key/language combination does not already exist then a new
     * translation will be created (provided that the key and language already exist in the
     * Lexicon DB).
     * @param languageID The ID of the language for which the translations are updated.
     * @param groupID The ID of the group for which the translations are updated.
     * @param translations The translations to be updated. The key of the translations
     * map is the name of the key to update while the value is the new translation.

     */
    void updateTranslationsForLanguageByKeyName(String languageID, String groupID, Map<String, String> translations);
    
    /**
	 * Returns the translation of a particular key for a specific locale.
	 * @param keyName The name of the key to lookup.
	 * @param locale The specific locale value for the requested key.
	 * @return The value of they key on the specific locale or null if a translation
	 * does not exist for the specified key/locale pair.
	 */
	String getTranslation(String keyName, String locale);

	/**
     * Finds all the translations (i.e. in all active locales) for a particular key.
     * @param keyName The name of the key to return its translations.
     * @param groupID The ID of the group in which we will search for the key
     * @return A Map consisting of (locale, translation_value) pairs.
     */
    Map<String, String> getTranslationsForKeyName(String keyName, String groupID);

    /**
     * Finds all the available translations for a particular locale.
     * @param locale The locale to return to translations for.
     * @return A Map consisting of (key_name, translation_value) pairs for the particular locale.
     */
    Map<String, String> getTranslationsForLocale(String locale);


    /**
     * Finds all the available translations for a particular group and locale
     * @param groupId The id of the group of which to retrieve translations
     * @param locale The locale for which to retrieve the translations
     * @return A Map consisting of (key_name, translation_value) for the specified group and locale.
     */
    Map<String, String> getTranslationsForGroupAndLocale(String groupId, String locale);
    
    /**
     * Finds all the available translations for a particular group and locale
     * @param groupName The name of the group of which to retrieve translations
     * @param locale The locale for which to retrieve the translations
     * @return A Map consisting of (key_name, translation_value) for the specified group and locale.
     */
    Map<String, String> getTranslationsForGroupNameAndLocale(String groupName, String locale);

    /**
     * Finds all the available translations for a particular group and locale, sorted by the translations
     * @param groupName The name of the group of which to retrieve translations
     * @param locale The locale for which to retrieve the translations
     * @param sortType The sort type
     * @return A Map consisting of (key_name, translation_value) for the specified group and locale, sorted by translation_value.
     */
	Map<String, String> getTranslationsForGroupNameAndLocaleSorted(String groupName, String locale, SortType sortType);

    /**
     * Returns the keys ordered by the translations for a particular group and locale
     * @param groupName The name of the group of which to retrieve translations
     * @param locale The locale for which to retrieve the translations
     * @param sortType The sort type to be used
     * @return A List of (key_name) ordered by the translation_value for the specified group and locale.
     */
	List<String> getKeysSortedByTranslation(String groupName, String locale, SortType sortType);
}
