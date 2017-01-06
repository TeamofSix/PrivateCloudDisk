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

package com.neil.client.attr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neil.client.utils.Utility;
import com.neil.client.utils.UtilityException;
import com.neil.client.attr.ExchangedAddress;
import com.neil.client.attr.LocalAddress;

public abstract class MessageAttribute implements MessageAttributeInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageAttribute.class);
	MessageAttributeType type;
	
	public MessageAttribute() {
	}
	
	public MessageAttribute(MessageAttributeType type) {
		setType(type);
	}
	
	public void setType(MessageAttributeType type) {
		this.type = type;
	}
	
	public MessageAttribute.MessageAttributeType getType() {
		return type;
	}
	
	public static int typeToInteger(MessageAttributeType type) {
		if (type == MessageAttributeType.MappedAddress) return MAPPEDADDRESS;
		if (type == MessageAttributeType.Username) return USERNAME;
		if (type == MessageAttributeType.Password) return PASSWORD;
		if (type == MessageAttributeType.MessageIntegrity) return MESSAGEINTEGRITY;
		if (type == MessageAttributeType.ErrorCode) return ERRORCODE;
		if (type == MessageAttributeType.UnknownAttribute) return UNKNOWNATTRIBUTE;
		if (type == MessageAttributeType.ReflectedFrom) return REFLECTEDFROM;
		if (type == MessageAttributeType.Dummy) return DUMMY;
		if (type == MessageAttributeType.LocalAddress) return LOCALADDRESS;
		if (type == MessageAttributeType.ExchangedAddress) return EXCHANGEDADDRESS;
		return -1;
	}
	
	public static MessageAttributeType intToType(long type) {
		if (type == MAPPEDADDRESS) return MessageAttributeType.MappedAddress;
		if (type == USERNAME) return MessageAttributeType.Username;
		if (type == PASSWORD) return MessageAttributeType.Password;
		if (type == MESSAGEINTEGRITY) return MessageAttributeType.MessageIntegrity;
		if (type == ERRORCODE) return MessageAttributeType.ErrorCode;
		if (type == UNKNOWNATTRIBUTE) return MessageAttributeType.UnknownAttribute;
		if (type == REFLECTEDFROM) return MessageAttributeType.ReflectedFrom;
		if (type == DUMMY) return MessageAttributeType.Dummy;
		if (type == LOCALADDRESS) return MessageAttributeType.LocalAddress;
		if (type == EXCHANGEDADDRESS) return MessageAttributeType.ExchangedAddress;
		return null;
	}
	
	abstract public byte[] getBytes() throws UtilityException;
	//abstract public MessageAttribute parse(byte[] data) throws MessageAttributeParsingException;
	
	public int getLength() throws UtilityException {
		int length = getBytes().length;
		return length;
	}
	
	public static MessageAttribute parseCommonHeader(byte[] data) throws MessageAttributeParsingException {
		try {			
			byte[] typeArray = new byte[2];
			System.arraycopy(data, 0, typeArray, 0, 2);
			int type = Utility.twoBytesToInteger(typeArray);
			byte[] lengthArray = new byte[2];
			System.arraycopy(data, 2, lengthArray, 0, 2);
			int lengthValue = Utility.twoBytesToInteger(lengthArray);
			byte[] valueArray = new byte[lengthValue];
			System.arraycopy(data, 4, valueArray, 0, lengthValue);
			MessageAttribute ma;
			switch (type) {
			case MAPPEDADDRESS: ma = MappedAddress.parse(valueArray); break;
			case USERNAME: ma = Username.parse(valueArray); break;
			case PASSWORD: ma = Password.parse(valueArray); break;
			case MESSAGEINTEGRITY: ma = MessageIntegrity.parse(valueArray); break;
			case ERRORCODE: ma = ErrorCode.parse(valueArray); break;
			case UNKNOWNATTRIBUTE: ma = UnknownAttribute.parse(valueArray); break;
			case REFLECTEDFROM: ma = ReflectedFrom.parse(valueArray); break;
			case LOCALADDRESS: ma = LocalAddress.parse(valueArray); break;
			case EXCHANGEDADDRESS: ma = ExchangedAddress.parse(valueArray); break;
			default:
				if (type <= 0x7fff) {
					throw new UnknownMessageAttributeException("Unkown mandatory message attribute", intToType(type));
				} else {
					LOGGER.debug("MessageAttribute with type " + type + " unkown.");
					ma = Dummy.parse(valueArray);
					break;
				}
			}
			return ma;
		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		}
	}
}
