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
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class SoulOverlay extends Overlay
{
	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final SoulManager manager;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private SoulOverlay(Client client, BetterDt2UtilitiesConfig config, SoulManager manager, ModelOutlineRenderer modelOutlineRenderer)
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
		HighlightStyle style = config.soulsHighlight();
		if (style == HighlightStyle.NONE)
		{
			return null;
		}

		Stroke stroke = new BasicStroke((float) config.soulsHighlightWidth());
		for (NPC npc : client.getNpcs())
		{
			if (!manager.isSoul(npc) || manager.isHidden(npc))
			{
				continue;
			}
			SoulManager.SoulType type = manager.typeOf(npc);
			if (type == null)
			{
				continue;
			}
			Color color = colorFor(type);
			Color fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 4);
			switch (style)
			{
				case HULL:
				case TRUE_TILE:
					Shape hull = npc.getConvexHull();
					if (hull != null)
					{
						OverlayUtil.renderPolygon(graphics, hull, color, fill, stroke);
					}
					break;
				case TILE:
					Polygon poly = npc.getCanvasTilePoly();
					if (poly != null)
					{
						OverlayUtil.renderPolygon(graphics, poly, color, fill, stroke);
					}
					break;
				case OUTLINE:
					modelOutlineRenderer.drawOutline(npc, (int) config.soulsHighlightWidth(), color, 0);
					break;
			}
		}
		return null;
	}

	private Color colorFor(SoulManager.SoulType type)
	{
		switch (type)
		{
			case VITA:
				return config.soulVitaColor();
			case MORS:
				return config.soulMorsColor();
			case SANITAS:
				return config.soulSanitasColor();
			case ORATIO:
				return config.soulOratioColor();
			default:
				return Color.WHITE;
		}
	}
}
