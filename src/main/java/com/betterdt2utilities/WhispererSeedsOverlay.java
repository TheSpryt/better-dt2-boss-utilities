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
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class WhispererSeedsOverlay extends Overlay
{
	private final Client client;
	private final BetterDt2UtilitiesConfig config;
	private final WhispererSeedsManager manager;

	@Inject
	private WhispererSeedsOverlay(Client client, BetterDt2UtilitiesConfig config, WhispererSeedsManager manager)
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
		if (!config.whispererSeeds() || !manager.isSeedsActive())
		{
			return null;
		}

		if (client.getLocalPlayer() == null)
		{
			return null;
		}

		Color color = config.whispererSeedColor();
		Color fill = config.whispererSeedFillColor();
		Stroke stroke = new BasicStroke((float) config.whispererSeedWidth());

		for (WorldPoint template : manager.getTilesToShow())
		{
			Highlights.templateTile(client, graphics, template, color, fill, stroke);
		}
		return null;
	}
}
