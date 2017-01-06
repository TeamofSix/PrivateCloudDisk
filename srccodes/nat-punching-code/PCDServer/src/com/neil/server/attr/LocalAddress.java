/*
 * This file is part of JSTUN. 
 * 
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 * 
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package com.neil.server.attr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalAddress extends MappedResponseChangedSourceAddressReflectedFrom {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalAddress.class);
	public LocalAddress() {
		super(MessageAttribute.MessageAttributeType.LocalAddress);
	}
	
	public static MessageAttribute parse(byte[] data) throws MessageAttributeParsingException {
		LocalAddress ma = new LocalAddress();
		MappedResponseChangedSourceAddressReflectedFrom.parse(ma, data);
		LOGGER.debug("Message Attribute: Local Address parsed: " + ma.toString() + ".");
		return ma;
	}
}
