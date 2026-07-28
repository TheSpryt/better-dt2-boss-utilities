/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class WhispererPillarOverlay extends Overlay
{
	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final WhispererPillarManager manager;

	@Inject
	private WhispererPillarOverlay(Client client, BetterDt2UtilitiesConfig config, WhispererPillarManager manager)
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
		if (!config.solvePillars() || manager.getSolution().isEmpty())
		{
			return null;
		}

		graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, config.pillarLabelSize()));
		Color color = config.pillarLabelColor();
		for (NPC npc : client.getNpcs())
		{
			if (!manager.isPillar(npc))
			{
				continue;
			}
			WorldPoint template = manager.templatePoint(npc);
			int order = template == null ? 0 : manager.orderOf(template);
			if (order == 0)
			{
				continue;
			}

			String label = Integer.toString(order);
			Point text = Perspective.getCanvasTextLocation(client, graphics, npc.getLocalLocation(), label, npc.getLogicalHeight() + 40);
			if (text != null)
			{
				OverlayUtil.renderTextLocation(graphics, text, label, color);
			}

			if (config.pillarMarkHideTiles())
			{
				drawHideTile(graphics, npc);
			}
		}
		return null;
	}

	// Marker for the 2x2 hide spot behind the pillar. The screech comes from the
	// Whisperer at the south end, so the safe side is north of the pillar - two tiles
	// back from its centre so the square sits fully behind the pillar's footprint
	// rather than clipping into it.
	private void drawHideTile(Graphics2D graphics, NPC npc)
	{
		LocalPoint base = npc.getLocalLocation();
		if (base == null)
		{
			return;
		}
		LocalPoint behind = new LocalPoint(base.getX(), base.getY() + 256);
		Polygon poly = Perspective.getCanvasTileAreaPoly(client, behind, 2);
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, config.pillarHideTileColor(), config.pillarHideTileFillColor(), new java.awt.BasicStroke(2));
		}
	}
}
