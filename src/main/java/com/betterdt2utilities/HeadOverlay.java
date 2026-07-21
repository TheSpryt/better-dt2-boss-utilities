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
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class HeadOverlay extends Overlay
{
	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final HeadTracker tracker;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private HeadOverlay(Client client, BetterDt2UtilitiesConfig config, HeadTracker tracker, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.config = config;
		this.tracker = tracker;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.highlightMageHead() == HighlightStyle.NONE && config.highlightRangeHead() == HighlightStyle.NONE)
		{
			return null;
		}
		if (tracker.getHeadStyles().isEmpty())
		{
			return null;
		}

		Stroke stroke = new BasicStroke((float) config.headHighlightWidth());
		for (NPC npc : client.getNpcs())
		{
			if (npc.getId() != HeadTracker.HEAD_ID)
			{
				continue;
			}
			HeadTracker.HeadAttackStyle style = tracker.getHeadStyles().get(npc.getIndex());
			if (style == null)
			{
				continue;
			}

			HighlightStyle renderType;
			Color color;
			Color fillColor;
			if (style == HeadTracker.HeadAttackStyle.MAGE)
			{
				renderType = config.highlightMageHead();
				color = config.mageHeadColor();
				fillColor = config.mageHeadFillColor();
			}
			else
			{
				renderType = config.highlightRangeHead();
				color = config.rangeHeadColor();
				fillColor = config.rangeHeadFillColor();
			}
			if (renderType == HighlightStyle.NONE)
			{
				continue;
			}
			switch (renderType)
			{
				case HULL:
					Shape hull = npc.getConvexHull();
					if (hull != null)
					{
						OverlayUtil.renderPolygon(graphics, hull, color, fillColor, stroke);
					}
					break;
				case TILE:
					Polygon poly = npc.getCanvasTilePoly();
					if (poly != null)
					{
						OverlayUtil.renderPolygon(graphics, poly, color, fillColor, stroke);
					}
					break;
				case TRUE_TILE:
					NPCComposition composition = npc.getTransformedComposition();
					int size = composition != null ? composition.getSize() : 1;
					LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldLocation());
					if (lp != null)
					{
						lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
						Polygon tile = Perspective.getCanvasTileAreaPoly(client, lp, size);
						if (tile != null)
						{
							OverlayUtil.renderPolygon(graphics, tile, color, fillColor, stroke);
						}
					}
					break;
				case OUTLINE:
					modelOutlineRenderer.drawOutline(npc, (int) config.headHighlightWidth(), color, config.headOutlineFeather());
					break;
			}
		}
		return null;
	}
}
