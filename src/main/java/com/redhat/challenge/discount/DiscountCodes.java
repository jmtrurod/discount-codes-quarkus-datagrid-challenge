package com.redhat.challenge.discount;

import com.redhat.challenge.discount.model.DiscountCode;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class DiscountCodes {
   private long totalCount;
   private Integer totalUse;
   private List<DiscountCode> discountCodesList;

   public DiscountCodes() {
   }

   public DiscountCodes(List<DiscountCode> discountCodesList, long totalCount) {
    System.out.println("A");
      this.discountCodesList = discountCodesList;
      this.totalCount = totalCount;
      setTotalUse(discountCodesList);
   }

   public List<DiscountCode> getDiscountCodesList() {
      return discountCodesList;
   }

   public void setDiscountCodesList(List<DiscountCode> discountCodesList) {
      this.discountCodesList = discountCodesList;
   }

   public long getTotalCount() {
      return totalCount;
   }

   public void setTotalCount(long totalCount) {
      this.totalCount = totalCount;
   }

   public void setTotalUse(List<DiscountCode> discountCodesList) {
       System.out.println("B");
       Integer total = 0;
       for (DiscountCode discount : discountCodesList) {
           total =  total + discount.getUsed();
       }

       System.out.println("TOTAL --> " + total);
       this.totalUse = total;
   }

   public Integer getTotalUse() {
       return this.totalUse;
   }
}
