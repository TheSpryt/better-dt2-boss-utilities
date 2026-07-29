/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class TentacleOverlay extends Overlay
{
	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final TentacleManager manager;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private TentacleOverlay(Client client, BetterDt2UtilitiesConfig config, TentacleManager manager, ModelOutlineRenderer modelOutlineRenderer)
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
		HighlightStyle style = config.tentacleHighlight();
		if (style == HighlightStyle.NONE)
		{
			return null;
		}

		Color color = config.tentacleColor();
		Color fill = config.tentacleFillColor();
		float width = (float) config.tentacleHighlightWidth();
		for (NPC npc : client.getNpcs())
		{
			if (!manager.isTentacle(npc))
			{
				continue;
			}
			Highlights.npc(client, graphics, npc, style, color, fill, width, modelOutlineRenderer);
		}
		return null;
	}
}
