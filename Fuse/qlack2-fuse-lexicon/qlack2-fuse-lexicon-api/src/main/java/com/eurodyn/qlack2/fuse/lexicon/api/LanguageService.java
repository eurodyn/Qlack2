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

import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.exception.QLanguageProcessingException;

import java.util.List;

public interface LanguageService {
  /**
   * Creates a new language in Lexicon and without initialising the language
   * translations. This means that the new language will have no translations
   * available.
   *
   * @param language
   *            The details of the language to create.
   * @return The ID of the new language.
   */
  String createLanguage(LanguageDTO language);

  /**
   * Creates a new language in Lexicon and initialises translations for all
   * existing keys for this language by using the key name as translation.
   *
   * @param language
   *            The details of the language to create.
   * @param translationPrefix
   *            An optional prefix to be prepended to the key names in order
   *            to create the default translations for the new language.
   * @return The ID of the new language.
   */
  String createLanguage(LanguageDTO language, String translationPrefix);

  /**
   * Creates a new language in Lexicon and initialises translations for all
   * existing keys for this language by using the translations of an existing
   * language.
   *
   * @param language
   *            The details of the language to create.
   * @param sourceLanguageId
   *            The id of the language from which to use the translations in
   *            order to initialise the translations of the new language
   * @param translationPrefix
   *            An optional prefix to be prepended to the source language
   *            translations in order to create the default translations for
   *            the new language.
   * @return The ID of the new language.
   */
  String createLanguage(LanguageDTO language, String sourceLanguageId, String translationPrefix);

  /**
   * Updates a language name or locale
   *
   * @param language
   *            The details of the language to update. The ID is used to
   *            identify the language to update while the name and locale are
   *            the new values which will be saved for this language. Please
   *            note that the "active" property is not taken into account and
   *            that Lexicon clients should use the
   *            activeLanguage/deactivateLanguage methods instead when wishing
   *            to update the status of a language.
   *
   */
  void updateLanguage(LanguageDTO language);

  /**
   * Deletes a language.
   *
   * @param languageID
   *            The ID of the language to delete.
   */
  void deleteLanguage(String languageID);

  /**
   * Activates a language
   *
   * @param languageID
   *            The ID of the language to activate
   */
  void activateLanguage(String languageID);

  /**
   * Deactivates a language
   *
   * @param languageID
   *            The ID of the language to deactivate
   */
  void deactivateLanguage(String languageID);

  /**
   * Retrieves a language
   *
   * @param languageID
   *            The ID of the language to retrieve
   * @return The details of the specified language or null if the language
   *         does not exist.
   */
  LanguageDTO getLanguage(String languageID);

  /**
   * Retrieves a language
   *
   * @param locale
   *            The locale of the language to retrieve
   * @return The details of the specified language or null if the language
   *         does not exist.
   */
  LanguageDTO getLanguageByLocale(String locale);

  /**
   * Retrieves a language
   *
   * @param locale
   *           The locale of the language to retrieve
   * @param fallback if a fallback should be made when the requested language doesn't exist
   * @return The details of the specified language or null if the language
   *         does not exist.
   */
  LanguageDTO getLanguageByLocale(String locale, boolean fallback);

  /**
   * Retrieves all languages available in Lexicon
   *
   * @param includeInactive
   *            Whether to also retrieve inactive languages.
   * @return All (active) available languages ordered by language name
   *         ascending.
   */
  List<LanguageDTO> getLanguages(boolean includeInactive);

  /**
   * This method returns the locale of an active language based on an initial
   * and default locale. This method returns the locale passed to it as
   * argument if an active language with this locale exists or else reduces
   * the locale (ex. from en_US to en) in an attempt to find an active
   * language. If an active language can still not be found then the default
   * locale passed to the method is returned.
   *
   * @param locale
   *            The initial requested locale
   * @param defaultLocale
   *            The default locale to return if a language corresponding to
   *            the initial requested locale cannot be found.
   * @return The effective locale based on the one passed as parameter. If
   *         none of the checked languages exists or is active then this
   *         method will return null.
   */
  String getEffectiveLanguage(String locale, String defaultLocale);

  /**
   * Creates an Excel file with the translations of the requested language.
   * The returned Excel file is based on Excel '97(-2007) file format (.xls)
   * and has one sheet for each translation group available in the Lexicon DB.
   *
   * @param languageID
   *            The ID of the language for which to retrieve all translations.
   * @return A byte array representing an Excel '97 (.xls) file with the
   *         requested translations.
   * @throws QLanguageProcessingException
   *             If an error occurs during the processing of the language
   *             translations and the creation of the Excel file.
   */
  byte[] downloadLanguage(String languageID);

  /**
   * Updates the system translations with the translations found on the
   * provided Excel. It is assumed that each sheet of the uploaded Excel file
   * corresponds to a translation group and is named after the group title.
   * Keys which can not be found in the Lexicon database are simply ignored.
   *
   * @param languageID
   *            The locale for which the translations are uploaded.
   * @param lgXL
   *            The Excel file as uploaded by the user.
   * @throws QLanguageProcessingException
   *             If an error occurs during the processing of the Excel file.
   */
  void uploadLanguage(String languageID, byte[] lgXL);

  /**
   * A helper method to identify whether the requested local is for an RTL
   * language.
   *
   * @param locale The locale of the language.
   * @return
   */
  boolean isLocaleRTL(String locale);
}
