package com.antu.nmea.message.field.codec;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.antu.nmea.annotation.FieldSetting;

public class StringMessageFieldCodec extends AbstractMessageFieldCodec {

	static private Log logger = LogFactory.getLog(StringMessageFieldCodec.class);

	public StringMessageFieldCodec() {
	}

	@Override
	public String fieldCodecType() {
		return "string";
	}

	@Override
	protected boolean doDecode(List<Byte> bits, int startIndex, Object obj,
			Field field, FieldSetting setting) {
		
		String value = MessageFieldCodecHelper.parseString(bits, startIndex, setting.getFieldWidth(), false);
		value = MessageFieldCodecHelper.convertReservedChars(value);
		value = MessageFieldCodecHelper.removeAtSigns(value);
		
		if (value != null) {
			try {
				field.set(obj, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error("unable to set field: " + field.getName(), e);
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean doEncode(List<Byte> bits, Object obj, Field field,
			FieldSetting setting) {
		
		try {
			Object val = field.get(obj);
			
			if (val != null && val instanceof String) {
				
				if (MessageFieldCodecHelper.stringToBits(bits, (String)val, setting.getFieldWidth())) {
					return true;
				} else {
					return false;
				}
			} else {
				logger.error("unable to get value for field: " + field.getName());
				return false;
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.error("unable to get value from field: " + field.getName(), e);
			return false;
		}
	}

}
