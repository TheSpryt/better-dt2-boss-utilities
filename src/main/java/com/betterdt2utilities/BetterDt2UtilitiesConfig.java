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
		return new Color(0, 0, 0, 0xB6);
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
		description = "Highlight style for the arena pillars, applied whether they are shown or hidden",
		section = pillarsSection
	)
	default HighlightStyle pillarHighlight()
	{
		return HighlightStyle.NONE;
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

	// Whisperer projectiles
	@ConfigSection(
		name = "Whisperer projectiles",
		description = "Swap the Whisperer's magic and ranged projectiles",
		position = 5,
		closedByDefault = true
	)
	String whispererProjectilesSection = "whispererProjectilesSection";

	@ConfigItem(
		position = 0,
		keyName = "whispererMageProjectile",
		name = "Magic projectile",
		description = "Replace the Whisperer's magic attack projectile with this boss style",
		section = whispererProjectilesSection
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
		section = whispererProjectilesSection
	)
	default ProjectileStyle whispererRangeProjectile()
	{
		return ProjectileStyle.Whisperer;
	}

	// Whisperer seeds
	@ConfigSection(
		name = "Whisperer seeds",
		description = "Mark the corrupted-seed tiles during the Whisperer's seeds special phase",
		position = 6,
		closedByDefault = true
	)
	String whispererSeedsSection = "whispererSeedsSection";

	@ConfigItem(
		position = 0,
		keyName = "whispererSeeds",
		name = "Mark corrupted seeds",
		description = "Mark the corrupted-seed tiles while the Whisperer is in the seeds special phase",
		section = whispererSeedsSection
	)
	default boolean whispererSeeds()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "whispererSeedColor",
		name = "Seed border color",
		description = "The outline color of the corrupted-seed tiles",
		section = whispererSeedsSection
	)
	default Color whispererSeedColor()
	{
		return new Color(0x28, 0xC8, 0x50, 0xC8);
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "whispererSeedFillColor",
		name = "Seed fill color",
		description = "The fill color of the corrupted-seed tiles; set alpha to 0 for outline only",
		section = whispererSeedsSection
	)
	default Color whispererSeedFillColor()
	{
		return new Color(0x28, 0xC8, 0x50, 0x19);
	}

	@ConfigItem(
		position = 3,
		keyName = "whispererSeedWidth",
		name = "Seed border width",
		description = "Width of the corrupted-seed tile border",
		section = whispererSeedsSection
	)
	default double whispererSeedWidth()
	{
		return 2;
	}


	// Whisperer pillars
	@ConfigSection(
		name = "Whisperer pillars",
		description = "Solve the Whisperer's screech pillars: read their health in the Shadow Realm and label the hide order",
		position = 7,
		closedByDefault = true
	)
	String whispererPillarsSection = "whispererPillarsSection";

	@ConfigItem(
		position = 0,
		keyName = "solvePillars",
		name = "Solve screech pillars",
		description = "Read pillar health in the Shadow Realm and label the pillars in the order to hide behind them",
		section = whispererPillarsSection
	)
	default boolean solvePillars()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "pillarLabelColor",
		name = "Label colour",
		description = "Colour of the pillar order labels",
		section = whispererPillarsSection
	)
	default Color pillarLabelColor()
	{
		return new Color(0xFF, 0xFF, 0x00, 0xFF);
	}

	@ConfigItem(
		position = 2,
		keyName = "pillarLabelSize",
		name = "Label size",
		description = "Font size of the pillar order labels",
		section = whispererPillarsSection
	)
	default int pillarLabelSize()
	{
		return 16;
	}

	@ConfigItem(
		position = 3,
		keyName = "pillarMarkHideTiles",
		name = "Mark hide tiles",
		description = "Also mark the 2x2 tile behind each labelled pillar where you stand to avoid the screech",
		section = whispererPillarsSection
	)
	default boolean pillarMarkHideTiles()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "pillarHideTileColor",
		name = "Hide tile colour",
		description = "Border colour of the 2x2 hide tiles",
		section = whispererPillarsSection
	)
	default Color pillarHideTileColor()
	{
		return new Color(0x40, 0x80, 0xFF, 0xFF);
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "pillarHideTileFillColor",
		name = "Hide tile fill colour",
		description = "Fill colour of the 2x2 hide tiles; set alpha to 0 for outline only",
		section = whispererPillarsSection
	)
	default Color pillarHideTileFillColor()
	{
		return new Color(0x40, 0x80, 0xFF, 0x32);
	}

	// Whisperer souls
	@ConfigSection(
		name = "Whisperer souls",
		description = "Highlight, hide and relabel the lost souls during the Whisperer's soul-siphon phase",
		position = 8,
		closedByDefault = true
	)
	String whispererSoulsSection = "whispererSoulsSection";

	@ConfigItem(
		position = 0,
		keyName = "soulsRenameSpeech",
		name = "Rename soul speech",
		description = "Relabel the souls' overhead chant: Vita to Health, Mors to Damage, Sanitas to Sanity, Oratio to Prayer",
		section = whispererSoulsSection
	)
	default boolean soulsRenameSpeech()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "soulsHighlight",
		name = "Highlight style",
		description = "How to highlight the souls; each type is drawn in its own colour below",
		section = whispererSoulsSection
	)
	default HighlightStyle soulsHighlight()
	{
		return HighlightStyle.NONE;
	}

	@ConfigItem(
		position = 2,
		keyName = "soulsHighlightWidth",
		name = "Highlight width",
		description = "Border/outline width of the soul highlights",
		section = whispererSoulsSection
	)
	default double soulsHighlightWidth()
	{
		return 2;
	}

	@Alpha
	@ConfigItem(position = 3, keyName = "soulVitaColor", name = "Vita (Health) colour", description = "Highlight colour for Vita souls", section = whispererSoulsSection)
	default Color soulVitaColor()
	{
		return new Color(0x28, 0xC8, 0x50, 0xC8);
	}

	@Alpha
	@ConfigItem(position = 4, keyName = "soulMorsColor", name = "Mors (Damage) colour", description = "Highlight colour for Mors souls", section = whispererSoulsSection)
	default Color soulMorsColor()
	{
		return new Color(0xFF, 0x00, 0x00, 0x39);
	}

	@Alpha
	@ConfigItem(position = 5, keyName = "soulSanitasColor", name = "Sanitas (Sanity) colour", description = "Highlight colour for Sanitas souls", section = whispererSoulsSection)
	default Color soulSanitasColor()
	{
		return new Color(0xFF, 0xFF, 0x00, 0x82);
	}

	@Alpha
	@ConfigItem(position = 6, keyName = "soulOratioColor", name = "Oratio (Prayer) colour", description = "Highlight colour for Oratio souls", section = whispererSoulsSection)
	default Color soulOratioColor()
	{
		return new Color(0x1A, 0x29, 0x47, 0xFF);
	}

	@ConfigItem(position = 7, keyName = "soulsHideVita", name = "Hide Vita souls", description = "Hide the Vita (Health) souls", section = whispererSoulsSection)
	default boolean soulsHideVita()
	{
		return false;
	}

	@ConfigItem(position = 8, keyName = "soulsHideMors", name = "Hide Mors souls", description = "Hide the Mors (Damage) souls", section = whispererSoulsSection)
	default boolean soulsHideMors()
	{
		return false;
	}

	@ConfigItem(position = 9, keyName = "soulsHideSanitas", name = "Hide Sanitas souls", description = "Hide the Sanitas (Sanity) souls", section = whispererSoulsSection)
	default boolean soulsHideSanitas()
	{
		return false;
	}

	@ConfigItem(position = 10, keyName = "soulsHideOratio", name = "Hide Oratio souls", description = "Hide the Oratio (Prayer) souls", section = whispererSoulsSection)
	default boolean soulsHideOratio()
	{
		return false;
	}

	@ConfigItem(
		position = 11,
		keyName = "soulsShowOnlyTarget",
		name = "Show only Venator target",
		description = "Hide every soul except the one to hit with a Venator bow to ricochet through all three in its line",
		section = whispererSoulsSection
	)
	default boolean soulsShowOnlyTarget()
	{
		return false;
	}

	@ConfigItem(
		position = 12,
		keyName = "soulsUnhideVitaHp",
		name = "Unhide Vita below HP",
		description = "Show hidden Vita (Health) souls when your current hitpoints drop to or below this value; 0 to disable",
		section = whispererSoulsSection
	)
	default int soulsUnhideVitaHp()
	{
		return 0;
	}

	@ConfigItem(
		position = 13,
		keyName = "soulsUnhideOratioPrayer",
		name = "Unhide Oratio below Prayer",
		description = "Show hidden Oratio (Prayer) souls when your current prayer points drop to or below this value; 0 to disable",
		section = whispererSoulsSection
	)
	default int soulsUnhideOratioPrayer()
	{
		return 0;
	}

	@ConfigItem(
		position = 14,
		keyName = "soulsUnhideSanitasSanity",
		name = "Unhide Sanitas below Sanity %",
		description = "Show hidden Sanitas (Sanity) souls when your sanity drops to or below this percentage; 0 to disable",
		section = whispererSoulsSection
	)
	default int soulsUnhideSanitasSanity()
	{
		return 0;
	}

	// Whisperer tentacles
	@ConfigSection(
		name = "Whisperer tentacles",
		description = "Highlight the Whisperer's tentacles and mark the L-walk dodge tiles",
		position = 9,
		closedByDefault = true
	)
	String whispererTentaclesSection = "whispererTentaclesSection";

	@ConfigItem(
		position = 0,
		keyName = "tentacleHighlight",
		name = "Highlight style",
		description = "How to highlight the Whisperer's tentacles",
		section = whispererTentaclesSection
	)
	default HighlightStyle tentacleHighlight()
	{
		return HighlightStyle.NONE;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "tentacleColor",
		name = "Tentacle colour",
		description = "Highlight colour for the tentacles",
		section = whispererTentaclesSection
	)
	default Color tentacleColor()
	{
		return new Color(0xC8, 0x28, 0xC8, 0xFF);
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "tentacleFillColor",
		name = "Tentacle fill colour",
		description = "Fill colour for the tentacle highlight; set alpha to 0 for outline only",
		section = whispererTentaclesSection
	)
	default Color tentacleFillColor()
	{
		return new Color(0xC8, 0x28, 0xC8, 0x32);
	}

	@ConfigItem(
		position = 3,
		keyName = "tentacleHighlightWidth",
		name = "Highlight width",
		description = "Border/outline width of the tentacle highlights",
		section = whispererTentaclesSection
	)
	default double tentacleHighlightWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 4,
		keyName = "markLWalk",
		name = "Mark L-movement tiles",
		description = "Mark the L-movement tiles used to dodge the Whisperer's attacks",
		section = whispererTentaclesSection
	)
	default boolean markLWalk()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "lMovementShadow",
		name = "Also in shadow realm",
		description = "Also show the L-movement tiles in the Shadow Realm during the final enrage phase",
		section = whispererTentaclesSection
	)
	default boolean lMovementShadow()
	{
		return false;
	}

	@ConfigItem(
		position = 9,
		keyName = "lMovementEnrageHp",
		name = "Enrage HP (shadow)",
		description = "The Whisperer's health at or below which the shadow-realm L-movement tiles show (marks the enrage phase)",
		section = whispererTentaclesSection
	)
	default int lMovementEnrageHp()
	{
		return 250;
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "lWalkColor",
		name = "L-movement border colour",
		description = "Outline colour of the L-movement tiles",
		section = whispererTentaclesSection
	)
	default Color lWalkColor()
	{
		return new Color(0x43, 0x83, 0xFF, 0x64);
	}

	@Alpha
	@ConfigItem(
		position = 7,
		keyName = "lWalkFillColor",
		name = "L-movement fill colour",
		description = "Fill colour of the L-movement tiles; set alpha to 0 for outline only",
		section = whispererTentaclesSection
	)
	default Color lWalkFillColor()
	{
		return new Color(0x40, 0x80, 0xFF, 0x00);
	}

	@ConfigItem(
		position = 8,
		keyName = "lWalkWidth",
		name = "L-movement border width",
		description = "Border width of the L-movement tiles",
		section = whispererTentaclesSection
	)
	default double lWalkWidth()
	{
		return 2;
	}

	// Whisperer bind
	@ConfigSection(
		name = "Whisperer bind",
		description = "Show a safe-distance radius during the Whisperer's post-special bind and melee chase",
		position = 10,
		closedByDefault = true
	)
	String whispererBindSection = "whispererBindSection";

	@ConfigItem(
		position = 0,
		keyName = "showBindRadius",
		name = "Show run-away radius",
		description = "Draw the Whisperer's 10-tile radius after a special so you can keep clear of her melee chase",
		section = whispererBindSection
	)
	default boolean showBindRadius()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "bindRadiusColor",
		name = "Radius border colour",
		description = "Outline colour of the run-away radius",
		section = whispererBindSection
	)
	default Color bindRadiusColor()
	{
		return new Color(0xFF, 0x30, 0x30, 0xFF);
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "bindRadiusFillColor",
		name = "Radius fill colour",
		description = "Fill colour of the run-away radius; set alpha to 0 for outline only",
		section = whispererBindSection
	)
	default Color bindRadiusFillColor()
	{
		return new Color(0xFF, 0x30, 0x30, 0x00);
	}

	@ConfigItem(
		position = 3,
		keyName = "bindRadiusWidth",
		name = "Radius border width",
		description = "Border width of the run-away radius",
		section = whispererBindSection
	)
	default double bindRadiusWidth()
	{
		return 2;
	}
}
