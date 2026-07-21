/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(BetterDt2UtilitiesConfig.GROUP)
public interface BetterDt2UtilitiesConfig extends Config
{
	String GROUP = "betterdt2utilities";

	enum AxeType
	{
		Static,
		Moving,
		Both
	}

	// Vardorvis axes
	@ConfigSection(
		name = "Vardorvis axes",
		description = "Hide and highlight specific axe spawns in Vardorvis' arena",
		position = 1,
		closedByDefault = true
	)
	String axesSection = "axesSection";

	@ConfigItem(
		position = 0,
		keyName = "axeHideType",
		name = "Axes to hide",
		description = "Hide static axes, moving axes, or both, for the spawns selected below",
		section = axesSection
	)
	default AxeType axeHideType()
	{
		return AxeType.Both;
	}

	@ConfigItem(position = 1, keyName = "hideAxeNorth", name = "Hide north axe", description = "Hide axes from the north spawn", section = axesSection)
	default boolean hideAxeNorth()
	{
		return false;
	}

	@ConfigItem(position = 2, keyName = "hideAxeNorthEast", name = "Hide north-east axe", description = "Hide axes from the north-east spawn", section = axesSection)
	default boolean hideAxeNorthEast()
	{
		return false;
	}

	@ConfigItem(position = 3, keyName = "hideAxeEast", name = "Hide east axe", description = "Hide axes from the east spawn", section = axesSection)
	default boolean hideAxeEast()
	{
		return false;
	}

	@ConfigItem(position = 4, keyName = "hideAxeSouthEast", name = "Hide south-east axe", description = "Hide axes from the south-east spawn", section = axesSection)
	default boolean hideAxeSouthEast()
	{
		return false;
	}

	@ConfigItem(position = 5, keyName = "hideAxeSouth", name = "Hide south axe", description = "Hide axes from the south spawn", section = axesSection)
	default boolean hideAxeSouth()
	{
		return false;
	}

	@ConfigItem(position = 6, keyName = "hideAxeSouthWest", name = "Hide south-west axe", description = "Hide axes from the south-west spawn", section = axesSection)
	default boolean hideAxeSouthWest()
	{
		return false;
	}

	@ConfigItem(position = 7, keyName = "hideAxeWest", name = "Hide west axe", description = "Hide axes from the west spawn", section = axesSection)
	default boolean hideAxeWest()
	{
		return false;
	}

	@ConfigItem(position = 8, keyName = "hideAxeNorthWest", name = "Hide north-west axe", description = "Hide axes from the north-west spawn", section = axesSection)
	default boolean hideAxeNorthWest()
	{
		return false;
	}

	@ConfigItem(
		position = 9,
		keyName = "muteHiddenAxeSounds",
		name = "Mute hidden axe sounds",
		description = "Silence the sound effects of axes that are hidden",
		section = axesSection
	)
	default boolean muteHiddenAxeSounds()
	{
		return false;
	}

	@ConfigItem(
		position = 10,
		keyName = "axeHighlight",
		name = "Highlight axes",
		description = "Highlight axes; axes you hide are skipped",
		section = axesSection
	)
	default HighlightStyle axeHighlight()
	{
		return HighlightStyle.NONE;
	}

	@ConfigItem(
		position = 11,
		keyName = "axeHighlightType",
		name = "Axes to highlight",
		description = "Highlight static axes, moving axes, or both",
		section = axesSection
	)
	default AxeType axeHighlightType()
	{
		return AxeType.Both;
	}

	@Alpha
	@ConfigItem(
		position = 12,
		keyName = "axeHighlightColor",
		name = "Axe border color",
		description = "The outline color of the axe highlight",
		section = axesSection
	)
	default Color axeHighlightColor()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		position = 13,
		keyName = "axeFillColor",
		name = "Axe fill color",
		description = "The fill color of the axe highlight; set alpha to 0 for outline only",
		section = axesSection
	)
	default Color axeFillColor()
	{
		return new Color(255, 0, 0, 0);
	}

	@ConfigItem(
		position = 14,
		keyName = "axeHighlightWidth",
		name = "Axe highlight width",
		description = "Width of the axe highlight border",
		section = axesSection
	)
	default double axeHighlightWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 15,
		keyName = "axeOutlineFeather",
		name = "Axe outline feather",
		description = "Softness of the edge on the axe outline highlight (Outline mode only)",
		section = axesSection
	)
	default int axeOutlineFeather()
	{
		return 2;
	}

	// Vardorvis heads
	@ConfigSection(
		name = "Vardorvis heads",
		description = "Highlight Vardorvis' heads, coloured by attack style (magic/ranged)",
		position = 2,
		closedByDefault = true
	)
	String headsSection = "headsSection";

	@ConfigItem(
		position = 0,
		keyName = "headHighlight",
		name = "Head highlight",
		description = "Highlight style for the attacking heads; colour is set per attack style below",
		section = headsSection
	)
	default HighlightStyle headHighlight()
	{
		return HighlightStyle.NONE;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "mageHeadColor",
		name = "Magic border color",
		description = "The outline color of the magic head highlight",
		section = headsSection
	)
	default Color mageHeadColor()
	{
		return new Color(160, 32, 240);
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "mageHeadFillColor",
		name = "Magic fill color",
		description = "The fill color of the magic head highlight; set alpha to 0 for outline only",
		section = headsSection
	)
	default Color mageHeadFillColor()
	{
		return new Color(160, 32, 240, 50);
	}

	@ConfigItem(
		position = 3,
		keyName = "vardorvisMageProjectile",
		name = "Magic projectile",
		description = "Replace the head's magic prayer-disable projectile with this boss style",
		section = headsSection
	)
	default ProjectileStyle vardorvisMageProjectile()
	{
		return ProjectileStyle.Vardorvis;
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "rangeHeadColor",
		name = "Ranged border color",
		description = "The outline color of the ranged head highlight",
		section = headsSection
	)
	default Color rangeHeadColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "rangeHeadFillColor",
		name = "Ranged fill color",
		description = "The fill color of the ranged head highlight; set alpha to 0 for outline only",
		section = headsSection
	)
	default Color rangeHeadFillColor()
	{
		return new Color(0, 255, 0, 50);
	}

	@ConfigItem(
		position = 7,
		keyName = "vardorvisRangeProjectile",
		name = "Ranged projectile",
		description = "Replace the head's ranged prayer-disable projectile with this boss style",
		section = headsSection
	)
	default ProjectileStyle vardorvisRangeProjectile()
	{
		return ProjectileStyle.Vardorvis;
	}

	@ConfigItem(
		position = 8,
		keyName = "headHighlightWidth",
		name = "Highlight width",
		description = "Width of the head highlight border",
		section = headsSection
	)
	default double headHighlightWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 9,
		keyName = "headOutlineFeather",
		name = "Head outline feather",
		description = "Softness of the edge on the head outline highlight (Outline mode only)",
		section = headsSection
	)
	default int headOutlineFeather()
	{
		return 2;
	}

	// Vardorvis captcha
	@ConfigSection(
		name = "Vardorvis captcha",
		description = "Feedback for the spore captcha (quick-time event)",
		position = 3,
		closedByDefault = true
	)
	String captchaSection = "captchaSection";

	@ConfigItem(
		position = 0,
		keyName = "captchaHoverHighlight",
		name = "Highlight hovered spore",
		description = "Highlight the spore your cursor is hovering over during the captcha",
		section = captchaSection
	)
	default boolean captchaHoverHighlight()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "captchaHoverColor",
		name = "Hover color",
		description = "Color of the highlight on the spore under your cursor",
		section = captchaSection
	)
	default Color captchaHoverColor()
	{
		return new Color(255, 255, 0, 130);
	}

	@ConfigItem(
		position = 2,
		keyName = "captchaHoverWidth",
		name = "Border width",
		description = "Width of the hover highlight border",
		section = captchaSection
	)
	default double captchaHoverWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 3,
		keyName = "captchaHighlightAll",
		name = "Outline all spores",
		description = "Draw an outline around every spore",
		section = captchaSection
	)
	default boolean captchaHighlightAll()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "captchaAllColor",
		name = "Outline color",
		description = "Color of the outline drawn around every spore",
		section = captchaSection
	)
	default Color captchaAllColor()
	{
		return new Color(255, 255, 255, 90);
	}

	// Vardorvis pillars
	@ConfigSection(
		name = "Vardorvis pillars",
		description = "Hide and highlight the pillars in Vardorvis' arena",
		position = 4,
		closedByDefault = true
	)
	String pillarsSection = "pillarsSection";

	@ConfigItem(
		position = 0,
		keyName = "hidePillars",
		name = "Hide pillars",
		description = "Remove the arena pillars so you can click through them",
		section = pillarsSection
	)
	default boolean hidePillars()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "pillarHighlight",
		name = "Pillar highlight",
		description = "Highlight style for the arena pillars (hull and outline only draw while a pillar is shown)",
		section = pillarsSection
	)
	default PillarHighlightStyle pillarHighlight()
	{
		return PillarHighlightStyle.NONE;
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "pillarBorderColor",
		name = "Pillar border color",
		description = "The outline color of the pillar highlight",
		section = pillarsSection
	)
	default Color pillarBorderColor()
	{
		return new Color(0, 0, 0, 255);
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "pillarFillColor",
		name = "Pillar fill color",
		description = "The fill color of the pillar highlight; set alpha to 0 for outline only",
		section = pillarsSection
	)
	default Color pillarFillColor()
	{
		return new Color(255, 255, 255, 0x32);
	}

	@ConfigItem(
		position = 4,
		keyName = "pillarHighlightWidth",
		name = "Pillar highlight width",
		description = "Width of the pillar highlight border",
		section = pillarsSection
	)
	default double pillarHighlightWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 5,
		keyName = "pillarOutlineFeather",
		name = "Pillar outline feather",
		description = "Softness of the edge on the pillar outline highlight (Outline mode only)",
		section = pillarsSection
	)
	default int pillarOutlineFeather()
	{
		return 2;
	}

	// Whisperer
	@ConfigSection(
		name = "Whisperer",
		description = "Swap the Whisperer's magic and ranged projectiles",
		position = 5,
		closedByDefault = true
	)
	String whispererSection = "whispererSection";

	@ConfigItem(
		position = 0,
		keyName = "whispererMageProjectile",
		name = "Magic projectile",
		description = "Replace the Whisperer's magic attack projectile with this boss style",
		section = whispererSection
	)
	default ProjectileStyle whispererMageProjectile()
	{
		return ProjectileStyle.Whisperer;
	}

	@ConfigItem(
		position = 1,
		keyName = "whispererRangeProjectile",
		name = "Ranged projectile",
		description = "Replace the Whisperer's ranged attack projectile with this boss style",
		section = whispererSection
	)
	default ProjectileStyle whispererRangeProjectile()
	{
		return ProjectileStyle.Whisperer;
	}
}
