package com.redhat.challenge.discount;

import com.redhat.challenge.discount.model.DiscountCode;
import com.redhat.challenge.discount.model.DiscountCodeType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.manager.DefaultCacheManager;
import javax.inject.Inject;
import java.util.Date;

@Path("/discounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiscountCodesResource {

    @Inject
    DiscountCodesCacheCreation cache;

    Map<String, DiscountCode> discounts = new HashMap<>();

    @POST
    @Path("/{expiration}")
    public Response create(@PathParam("expiration") Integer expiration, DiscountCode discountCode) {
        if (!discounts.containsKey(discountCode.getName())) {
            discountCode.setUsed(0);
            discountCode.setInput(new Date());
            discountCode.setExpiration(expiration);
            discounts.put(discountCode.getName(), discountCode);
            cache.save(discountCode);
            return Response.created(URI.create(discountCode.getName())).build();
        }

        return Response.ok(URI.create(discountCode.getName())).build();
    }

    @GET
    @Path("/consume/{name}")
    public Response consume(@PathParam("name") String name) {
        DiscountCode discountCode = (DiscountCode) cache.get(name);
        System.out.println(discountCode);

        if(discountCode == null) {
            return Response.noContent().build();
        }

        if (((new Date()).getTime() - discountCode.getInput().getTime())/1000 > discountCode.getExpiration().longValue()) {
            cache.delete(discountCode.getName());
            discounts.remove(discountCode.getName());
            return Response.noContent().build();
        }

        discountCode.setUsed(discountCode.getUsed() + 1);
        discounts.put(name, discountCode);
        cache.save(discountCode);

        return Response.ok(discountCode).build();
    }

    @GET
    @Path("/{type}")
    public DiscountCodes getByType(@PathParam("type") DiscountCodeType type) {
        List<DiscountCode> discountCodes = cache.getAll().stream().filter((code) -> code.getType() == type)
              .collect(Collectors.toList());
        return new DiscountCodes(discountCodes, discountCodes.size());
    }

}
