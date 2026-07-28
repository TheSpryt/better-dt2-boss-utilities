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
import java.awt.Polygon;
import java.awt.Stroke;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class BindRadiusOverlay extends Overlay
{
	// Ring distance from her footprint - matches the Radius Markers attack range (11).
	private static final int BIND_RADIUS_TILES = 11;

	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final BindRadiusManager manager;

	@Inject
	private BindRadiusOverlay(Client client, BetterDt2UtilitiesConfig config, BindRadiusManager manager)
	{
		this.client = client;
		this.config = config;
		this.manager = manager;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showBindRadius())
		{
			return null;
		}
		NPC whisperer = manager.getBindNpc();
		if (whisperer == null)
		{
			return null;
		}

		NPCComposition composition = whisperer.getTransformedComposition();
		int size = composition != null ? composition.getSize() : 5;
		WorldPoint sw = whisperer.getWorldLocation();
		if (sw == null)
		{
			return null;
		}
		// Centre the radius on the middle of her footprint, then extend the safe
		// boundary BIND_RADIUS_TILES tiles beyond each edge.
		WorldPoint centre = new WorldPoint(sw.getX() + size / 2, sw.getY() + size / 2, sw.getPlane());
		LocalPoint lp = LocalPoint.fromWorld(client, centre);
		if (lp == null)
		{
			return null;
		}
		int areaSize = size + 2 * BIND_RADIUS_TILES;
		Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, areaSize);
		if (poly != null)
		{
			Stroke stroke = new BasicStroke((float) config.bindRadiusWidth());
			OverlayUtil.renderPolygon(graphics, poly, config.bindRadiusColor(), config.bindRadiusFillColor(), stroke);
		}
		return null;
	}
}
