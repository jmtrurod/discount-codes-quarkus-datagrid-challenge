package com.redhat.challenge.discount;

import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import com.redhat.challenge.discount.model.DiscountCode;
import javax.inject.Inject;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.Cache;
import java.util.List;
import java.util.ArrayList;

import java.util.Date;

@ApplicationScoped
public class DiscountCodesCacheCreation {

    private static final Logger LOGGER = LoggerFactory.getLogger("DiscountsCodeCacheCreation");

    private static final String CACHE_CONFIG = "<distributed-cache name=\"%s\">"
          + " <encoding media-type=\"application/x-protostream\"/>"
          + "</distributed-cache>";


    EmbeddedCacheManager cacheManager = new DefaultCacheManager();
    Cache<String, DiscountCode> discountCache;

    public List<DiscountCode> getAll() {
        return new ArrayList<>(discountCache.values());
    }

    public DiscountCode get(String entry) {
        return discountCache.get(entry);
    }

    public void save(DiscountCode entry) {
        discountCache.put(entry.getName(), entry);
    }

    public void delete(String entry) {
        discountCache.remove(entry);
    }

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Create or get cache named discounts with the default configuration");

        GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
        global.transport().clusterName("Discount");
        cacheManager = new DefaultCacheManager(global.build());

        ConfigurationBuilder config = new ConfigurationBuilder();
        //config.expiration().lifespan(5, TimeUnit.MINUTES).clustering().cacheMode(CacheMode.REPL_SYNC);

        cacheManager.defineConfiguration("Discount", config.build());
        discountCache = cacheManager.getCache("Discount");
        //scoreCache.addListener(new CacheListener());

        //EmbeddedCacheManager emc = new DefaultCacheManager();
        //emc.start();
        // Inject the cache manager and use the administration API to create the cache.
        // You can also use the operator or the WebConsole to create the cache "discounts"
        // String cacheConfig = String.format(CACHE_CONFIG, "discounts");
        // Use XMLStringConfiguration. Grab a look to the simple tutorial about "creating caches on the fly" in the
        // Infinispan Simple Tutorials repository.
    }
}
