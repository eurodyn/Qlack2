package com.eurodyn.qlack2.util.rest;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * A helper class to produce a RequestBuilder for resources that should be
 * cached on the client-side.
 */
public class HttpCacheHelper {

	/**
	 * Produces a RequestBuilder for resources that should be cached on the
	 * client-side for a specific amount of time.
	 * 
	 * @param timeValue
	 *            The amount of time to cache the resource for.
	 * @param timeUnit
	 *            The unit of time used when specifying the amount of time.
	 * @param content
	 *            The content to be included as part of the resource.
	 * @return A ResponseBuilder having the 'Cache-Control' header set and its
	 *         content populated.
	 */
	public static ResponseBuilder expiringResponse(long timeValue, TimeUnit timeUnit, Object content) {
		CacheControl cc = new CacheControl();
		cc.setMaxAge((int) TimeUnit.SECONDS.convert(timeValue, timeUnit));

		return Response.ok(content).cacheControl(cc);
	}

	/**
	 * Produces a ResponseBuilder for resources that should be cached on the
	 * client-side for a specific amount of time that support ETag validation.
	 * 
	 * @param timeValue
	 *            The amount of time to cache the resource for.
	 * @param timeUnit
	 *            The unit of time used when specifying the amount of time.
	 * @param etagGenerator
	 *            A method producing the value of the ETag.
	 * @param contentGenerator
	 *            A method producing the response's content in case ETag's
	 *            evaluation preconditions failed.
	 * @param request
	 *            The JAXWS-RS request.
	 * @return A ResponseBuilder having the 'Cache-Control' and 'ETag' headers
	 *         set and (when necessary) its content populated.
	 */
	public static ResponseBuilder expiringETagResponse(long timeValue, TimeUnit timeUnit,
			Callable<String> etagGenerator, Callable<Object> contentGenerator, Request request) {
		CacheControl cc = new CacheControl();
		cc.setMaxAge((int) TimeUnit.SECONDS.convert(timeValue, timeUnit));
		try {
			EntityTag eTag = new EntityTag(etagGenerator.call());
			ResponseBuilder builder = request.evaluatePreconditions(eTag);
			if (builder != null) {
				builder.cacheControl(cc);
				return builder;
			} else {
				return Response.ok(contentGenerator.call()).cacheControl(cc).tag(eTag);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Produces a ResponseBuilder for resources that support an ETag. Since no
	 * 'Cache-Control' headers will be added with this method, the client-side
	 * will always issue an HTTP call for such resources, however your
	 * server-side may avoid unnecessary round-trips in case the ETag's
	 * preconditions hold true.
	 * 
	 * @param etagGenerator
	 *            A method producing the value of the ETag.
	 * @param contentGenerator
	 *            A method producing the response's content in case ETag's
	 *            evaluation preconditions failed.
	 * @param request
	 *            The JAXWS-RS request.
	 * @return A ResponseBuilder having 'ETag' header set and (when necessary)
	 *         its content populated.
	 */
	public static ResponseBuilder eTagResponse(Callable<String> etagGenerator, 
			Callable<Object> contentGenerator, Request request) {
		try {
			EntityTag eTag = new EntityTag(etagGenerator.call());
			ResponseBuilder builder = request.evaluatePreconditions(eTag);
			if (builder != null) {
				return builder;
			} else {
				return Response.ok(contentGenerator.call()).tag(eTag);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
