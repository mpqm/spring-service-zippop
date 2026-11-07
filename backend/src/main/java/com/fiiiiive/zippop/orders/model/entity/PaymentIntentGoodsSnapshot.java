package com.fiiiiive.zippop.orders.model.entity;

/** Immutable checkout-time product values; current catalogue edits must not alter an authorized payment. */
public record PaymentIntentGoodsSnapshot(int quantity, int unitPrice, String goodsName) {}
