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
angular.module('QFormValidation', []).provider('QFormValidation', function() {
	this.$get = function($translate) {
		return new QFormValidationService($translate);
	}
});

function QFormValidationService($translate) {
	this.renderFormErrors = function($scope, frm, errors) {
//		v1 contains an object with all errors for a certain field.
//		k1 contains the index of field-errors object in the array of all
//		errors for this form.
		angular.forEach(errors.data.validationErrors, (function(v1, k1) {
			if (frm[v1.fieldName] !== void 0) {
//				Clear any validation messages already rendered for this field.
				frm[v1.fieldName].message = "";
			}
//			A field may contain multiple validation errors; iterate through
//			all errors available of the field.
//			v2 contains an object with a specific validation error for the field.
//			k2 contains the index of the specific validation error in the
//			array of all validation errors of the field.
			angular.forEach(v1.errors, (function(v2, k2) {
				// If the form contains this field set the field as invalid
				if (frm[v1.fieldName] !== void 0) {
					frm[v1.fieldName].$setValidity(v2.attributes.Message, false);
					frm[v1.fieldName].$setPristine(true);
				}
				if (v2.attributes.Raw === void 0) {
					v2.attributes.Raw = {};
				}
				v2.attributes.Raw['InvalidValue'] = v2.attributes.InvalidValue;
				$translate(v2.attributes.Message, v2.attributes.Raw).then(
						function(result) {
							var listName;
//							In case the field name contains brackets it is a list, therefore
//							the client should handle it on its own specific way.
							if ((v1.fieldName.indexOf("[", 0)) > -1
									&& (v1.fieldName.indexOf("]", 2)) > -1) {
								listName = v1.fieldName.substring(0,
										v1.fieldName.indexOf("[", 0));
								$scope.$broadcast("VALIDATION_ERROR_" + listName, {
									fieldName : v1.fieldName,
									fieldIndex : v1.fieldName.substring(
											(v1.fieldName.indexOf("[", 0)) + 1,
											v1.fieldName.indexOf("]", 0)),
									propertyName : v1.fieldName
											.substring(v1.fieldName.indexOf(
													"]", 0) + 2),
									error : v2,
									translation : result
								});
							} else {
								$scope.$broadcast(
										"VALIDATION_ERROR_" + v1.fieldName, {
											fieldName : v1.fieldName,
											error : v2,
											translation : result
										});
								if (frm[v1.fieldName] !== void 0) {
									frm[v1.fieldName].message += result;
									if (k2 + 1 < v1.errors.length) {
										frm[v1.fieldName].message += ", ";
									}
								}
							}
						});
			}));
		}));
	}
}