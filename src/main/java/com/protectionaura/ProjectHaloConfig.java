package com.protectionaura;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(ProjectHaloConfig.GROUP)
public interface ProjectHaloConfig extends Config
{
	String GROUP = "projecthalo";

	@ConfigSection(
		name = "General",
		description = "Core Project Halo settings",
		position = 0
	)
	String generalSection = "generalSection";

	@ConfigSection(
		name = "Colors",
		description = "Aura colors for each protection prayer",
		position = 1
	)
	String colorSection = "colorSection";

	@ConfigItem(
		keyName = "enabled",
		name = "Enable aura",
		description = "Turns Project Halo rendering on or off without disabling the plugin itself",
		position = 0,
		section = generalSection
	)
	default boolean enabled()
	{
		return true;
	}

	@Range(min = 1, max = 12)
	@Units(Units.PIXELS)
	@ConfigItem(
		keyName = "auraThickness",
		name = "Aura thickness",
		description = "Outline thickness in pixels",
		position = 1,
		section = generalSection
	)
	default int auraThickness()
	{
		return 4;
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(
		keyName = "auraOpacity",
		name = "Aura opacity",
		description = "Opacity percentage applied to all aura colors",
		position = 2,
		section = generalSection
	)
	default int auraOpacity()
	{
		return 60;
	}

	@ConfigItem(
		keyName = "meleeColor",
		name = "Protect from Melee color",
		description = "Aura color used when Protect from Melee is active",
		position = 3,
		section = colorSection
	)
	default Color meleeColor()
	{
		return new Color(220, 48, 48);
	}

	@ConfigItem(
		keyName = "rangeColor",
		name = "Protect from Missiles color",
		description = "Aura color used when Protect from Missiles is active",
		position = 4,
		section = colorSection
	)
	default Color rangeColor()
	{
		return new Color(56, 168, 84);
	}

	@ConfigItem(
		keyName = "magicColor",
		name = "Protect from Magic color",
		description = "Aura color used when Protect from Magic is active",
		position = 5,
		section = colorSection
	)
	default Color magicColor()
	{
		return new Color(64, 118, 232);
	}
}
