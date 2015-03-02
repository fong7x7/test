package com.workfront.usernotebuilder.config;

import com.google.common.base.*;
import com.google.common.cache.*;
import com.google.common.collect.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.cache.concurrent.*;
import org.springframework.cache.support.*;
import org.springframework.context.annotation.*;
import org.springframework.core.io.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * AtTask Cache Configuration. Incorporate components from cache-core &
 * cache-api to improve API SDK interactions and overall application
 * performance.
 *
 */
@Configuration
@DependsOn("serviceConfig")
@ComponentScan({"com.attask.cache"})
@ImportResource({
	"classpath:attask-cache-api-context.xml"
})
@EnableCaching
@Profile("ehcache")
public class CacheConfig {
	public static final String CACHE_HANDLER = "cache.handler";

	@Value("${attask.cache.adminSession}")
	private String adminCacheName;

	@Value("${attask.cache.aspSession}")
	private String aspCacheName;

	@Value("${attask.cache.userSession}")
	private String userCacheName;

	@Value("${ehcache.config.location:classpath:ehcache.xml}")
	private Resource configurationLocation;

	@Value("${attask.ehcache.configfile:classpath:ehcache.xml}")
	private Resource ehcacheConfigFile;

	@Bean
	public org.springframework.cache.CacheManager localCacheManager(
		@Value("${cache.tcp.ttl:300}") final Long cacheNetworkTimeToLive,
		@Value("${cache.filter.ttl:300}") final Long cacheFilterTTL)
	{
		final SimpleCacheManager cacheManager = new SimpleCacheManager();
		// CNC set a maximum cache size
		final ConcurrentMapCache tcpCache = createConcurrentMapCache(
			cacheNetworkTimeToLive, "cache.tcp", Optional.<Long>absent());

		final ConcurrentMapCache filterCache = createConcurrentMapCache(
			cacheFilterTTL, "cache.filter", Optional.of((long) 100));

		final List<ConcurrentMapCache> cacheList = ImmutableList.of(tcpCache, filterCache);

		cacheManager.setCaches(cacheList);
		return cacheManager;
	}

	private ConcurrentMapCache createConcurrentMapCache(
		final Long timeToLive, final String name, final Optional<Long> cacheSize)
	{
		CacheBuilder<Object, Object> cacheBuilder =
			CacheBuilder.newBuilder().expireAfterWrite(
				timeToLive, TimeUnit.SECONDS);

		if (cacheSize.isPresent()) {
			cacheBuilder.maximumSize(cacheSize.get());
		}
		ConcurrentMap<Object, Object> map = cacheBuilder.build().asMap();
		return new ConcurrentMapCache(name, map, false);
	}
}
