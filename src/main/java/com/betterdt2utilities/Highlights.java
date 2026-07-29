/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

// Shared rendering helpers so the per-feature overlays do not each repeat the same
// highlight and tile-drawing boilerplate.
final class Highlights
{
	private Highlights()
	{
	}

	// Draw an NPC in the chosen style. NONE is a no-op; callers are expected to skip
	// it, but it is handled here too so the switch stays exhaustive.
	static void npc(Client client, Graphics2D graphics, NPC npc, HighlightStyle style,
		Color color, Color fill, float width, ModelOutlineRenderer outlineRenderer)
	{
		Stroke stroke = new BasicStroke(width);
		switch (style)
		{
			case HULL:
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
						OverlayUtil.renderPolygon(graphics, tile, color, fill, stroke);
					}
				}
				break;
			case OUTLINE:
				outlineRenderer.drawOutline(npc, (int) width, color, 0);
				break;
			default:
				break;
		}
	}

	// Draw a single template (region-relative) tile, mapping it into every matching
	// spot in the current - possibly instanced - scene.
	static void templateTile(Client client, Graphics2D graphics, WorldPoint template,
		Color color, Color fill, Stroke stroke)
	{
		for (WorldPoint wp : WorldPoint.toLocalInstance(client, template))
		{
			LocalPoint lp = LocalPoint.fromWorld(client, wp);
			if (lp == null)
			{
				continue;
			}
			Polygon poly = Perspective.getCanvasTilePoly(client, lp);
			if (poly != null)
			{
				OverlayUtil.renderPolygon(graphics, poly, color, fill, stroke);
			}
		}
	}
}
