/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class LWalkOverlay extends Overlay
{
	// The real arena and its Shadow Realm mirror, with the base tile of each region.
	// Both copies are loaded in the instance for the whole fight, so the realm is told
	// apart by the player's own region: the real arena sits at regionX 40-42, the
	// shadow mirror 256 tiles west at regionX 36-38.
	private static final int REAL_BASE_X = 2624;
	private static final int REAL_BASE_Y = 6336;
	private static final int SHADOW_BASE_X = 2368;
	private static final int SHADOW_BASE_Y = 6336;
	private static final int REAL_MIN_REGION_X = 40;
	private static final int SHADOW_MAX_REGION_X = 38;

	// L-movement tiles as {regionX, regionY} - the two dodge zones, one on each side
	// of the arena. Region-relative so they map into either realm.
	private static final int[][] L_MOVEMENT = {
		{26, 29}, {27, 27}, {28, 25}, {24, 28}, {23, 25}, {25, 26}, {26, 24}, {24, 23},
		{36, 25}, {37, 27}, {38, 29}, {40, 28}, {39, 26}, {38, 24}, {40, 23}, {41, 25},
	};

	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final WhispererEnrageTracker enrageTracker;

	@Inject
	private LWalkOverlay(Client client, BetterDt2UtilitiesConfig config, WhispererEnrageTracker enrageTracker)
	{
		this.client = client;
		this.config = config;
		this.enrageTracker = enrageTracker;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.markLWalk())
		{
			return null;
		}

		// Work out which realm the player is standing in from their template region.
		Player player = client.getLocalPlayer();
		if (player == null || player.getLocalLocation() == null)
		{
			return null;
		}
		WorldPoint playerWp = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
		if (playerWp == null)
		{
			return null;
		}
		int regionX = playerWp.getRegionID() >> 8;

		int baseX;
		int baseY;
		if (regionX >= REAL_MIN_REGION_X)
		{
			// Real arena - shown whenever L-movement marking is on.
			baseX = REAL_BASE_X;
			baseY = REAL_BASE_Y;
		}
		else if (regionX <= SHADOW_MAX_REGION_X && config.lMovementShadow() && enrageTracker.isEnrage())
		{
			// Shadow mirror - only during the enrage phase, when asked for.
			baseX = SHADOW_BASE_X;
			baseY = SHADOW_BASE_Y;
		}
		else
		{
			return null;
		}

		Color color = config.lWalkColor();
		Color fill = config.lWalkFillColor();
		Stroke stroke = new BasicStroke((float) config.lWalkWidth());
		for (int[] t : L_MOVEMENT)
		{
			WorldPoint template = new WorldPoint(baseX + t[0], baseY + t[1], 0);
			Highlights.templateTile(client, graphics, template, color, fill, stroke);
		}
		return null;
	}
}
