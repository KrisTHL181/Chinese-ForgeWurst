/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.clickgui.Component;
import net.wurstclient.forge.clickgui.LongTypeSlider;
import net.wurstclient.forge.utils.MathUtils;

public final class LongTypeSliderSetting extends Setting
{
	private long value;
	private final long defaultValue;
	private final long min;
	private final long max;
	private final long increment;
	private final LongValueDisplay display;

	public LongTypeSliderSetting(String name, String description, long value,
		long min, long max, long increment, LongValueDisplay display)
	{
		super(name, description);
		this.value = value;
		defaultValue = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
		this.display = display;
	}
	
	public LongTypeSliderSetting(String name, long value, long min, long max,
		long increment, LongValueDisplay display)
	{
		this(name, null, value, min, max, increment, display);
	}
	
	public long getValue()
	{
		return value;
	}
	
	public float getValueF()
	{
		return (float)value;
	}
	
	public int getValueI()
	{
		return (int)value;
	}
	
	public String getValueString()
	{
		return display.getValueString(value);
	}
	
	public long getDefaultValue()
	{
		return defaultValue;
	}
	
	public void setValue(long value)
	{
		value = Math.round(value / increment) * increment;
		value = MathUtils.clamp(value, min, max);
		
		this.value = value;
		ForgeWurst.getForgeWurst().getHax().saveSettings();
	}
	
	public long getMin()
	{
		return min;
	}
	
	public long getMax()
	{
		return max;
	}
	
	@Override
	public Component getComponent()
	{
		return new LongTypeSlider(this);
	}
	
	@Override
	public void fromJson(JsonElement json)
	{
		if(!json.isJsonPrimitive())
			return;
		
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if(!primitive.isNumber())
			return;

        setValue(primitive.getAsLong());
	}

	@Override
	public JsonElement toJson()
	{
		return new JsonPrimitive(Math.round(value * 1e6) / 1e6);
	}
	
	public static interface LongValueDisplay
	{
		public static final LongValueDisplay DECIMAL =
			v -> Math.round(v * 1e6) / 1e6 + "";
		public static final LongValueDisplay INTEGER = v -> (int)v + "";
		public static final LongValueDisplay PERCENTAGE =
			v -> (int)(Math.round(v * 1e8) / 1e6) + "%";
		public static final LongValueDisplay DEGREES = v -> (int)v + "°";
		public static final LongValueDisplay NONE = v -> "";
		
		public String getValueString(long value);
	}
}
