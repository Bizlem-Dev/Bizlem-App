package biz.com.app.servlet;
import java.io.InputStream;

import net.sf.ehcache.*;

public class EhCacheManager {
	
	private static Cache newCache;
	private static EhCacheManager manager = new EhCacheManager();
	
	private EhCacheManager(){
		InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("ehcache.xml");
		System.out.println("start 1--"+inputStream1);
		CacheManager cacheManager = CacheManager.newInstance(inputStream1);
		EhCacheManager.newCache = cacheManager.getCache("mainpage");
	}
	
	public static Cache getCache(){
		return EhCacheManager.newCache;
	}

}
