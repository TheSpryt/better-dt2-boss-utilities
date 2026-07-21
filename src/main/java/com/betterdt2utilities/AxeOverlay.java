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

public class AxeOverlay extends Overlay
{
	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final AxeHider axeHider;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private AxeOverlay(Client client, BetterDt2UtilitiesConfig config, AxeHider axeHider, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.config = config;
		this.axeHider = axeHider;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		HighlightStyle renderType = config.axeHighlight();
		if (renderType == HighlightStyle.NONE)
		{
			return null;
		}

		BetterDt2UtilitiesConfig.AxeType highlightType = config.axeHighlightType();
		Color color = config.axeHighlightColor();
		Color fillColor = config.axeFillColor();
		Stroke stroke = new BasicStroke((float) config.axeHighlightWidth());

		for (NPC npc : client.getNpcs())
		{
			int id = npc.getId();
			if (id != AxeHider.AXE_STATIC && id != AxeHider.AXE_MOVING)
			{
				continue;
			}
			if (id == AxeHider.AXE_STATIC && highlightType == BetterDt2UtilitiesConfig.AxeType.Moving)
			{
				continue;
			}
			if (id == AxeHider.AXE_MOVING && highlightType == BetterDt2UtilitiesConfig.AxeType.Static)
			{
				continue;
			}
			// Don't highlight axes that are hidden
			if (axeHider.isHidden(npc))
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
					modelOutlineRenderer.drawOutline(npc, (int) config.axeHighlightWidth(), color, config.axeOutlineFeather());
					break;
			}
		}
		return null;
	}
}
