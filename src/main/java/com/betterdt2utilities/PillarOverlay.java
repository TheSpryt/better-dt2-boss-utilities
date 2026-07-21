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
import java.awt.Shape;
import java.awt.Stroke;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class PillarOverlay extends Overlay
{
	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final PillarManager manager;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private PillarOverlay(Client client, BetterDt2UtilitiesConfig config, PillarManager manager, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.config = config;
		this.manager = manager;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		PillarHighlightStyle style = config.pillarHighlight();
		if (style == PillarHighlightStyle.NONE)
		{
			return null;
		}

		Color color = config.pillarBorderColor();
		Color fill = config.pillarFillColor();
		Stroke stroke = new BasicStroke((float) config.pillarHighlightWidth());

		// Tile markers come from the stored locations, so they draw whether the
		// pillars are shown or hidden.
		if (style == PillarHighlightStyle.TILE)
		{
			for (WorldPoint wp : manager.getPillarTiles())
			{
				drawTile(graphics, wp, color, fill, stroke);
			}
			return null;
		}

		// A hidden pillar's object is removed from the scene but keeps its model
		// and location, so hull and outline still draw on it. The chosen style is
		// honoured whether the pillar is shown or hidden.
		for (GameObject pillar : manager.getPillarObjects())
		{
			if (pillar == null)
			{
				continue;
			}
			if (style == PillarHighlightStyle.HULL)
			{
				Shape hull = pillar.getConvexHull();
				if (hull != null)
				{
					OverlayUtil.renderPolygon(graphics, hull, color, fill, stroke);
				}
			}
			else if (style == PillarHighlightStyle.OUTLINE)
			{
				modelOutlineRenderer.drawOutline(pillar, (int) config.pillarHighlightWidth(), color, config.pillarOutlineFeather());
			}
		}
		return null;
	}

	private void drawTile(Graphics2D graphics, WorldPoint wp, Color color, Color fill, Stroke stroke)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, wp);
		if (lp == null)
		{
			return;
		}
		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, color, fill, stroke);
		}
	}
}
