/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Better DT2 Boss Utilities",
	description = "Vardorvis axe hiding, mage/range head highlights, spore captcha feedback, and boss projectile swaps",
	tags = {"dt2", "desert", "treasure", "vardorvis", "whisperer", "awakened", "axe", "head", "captcha", "spore", "projectile"}
)
public class BetterDt2UtilitiesPlugin extends Plugin
{
	@Inject
	private EventBus eventBus;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AxeHider axeHider;

	@Inject
	private HeadTracker headTracker;

	@Inject
	private ProjectileSwapper projectileSwapper;

	@Inject
	private AxeOverlay axeOverlay;

	@Inject
	private HeadOverlay headOverlay;

	@Inject
	private CaptchaOverlay captchaOverlay;

	@Inject
	private PillarManager pillarManager;

	@Inject
	private PillarOverlay pillarOverlay;

	@Override
	protected void startUp()
	{
		eventBus.register(axeHider);
		eventBus.register(headTracker);
		eventBus.register(projectileSwapper);
		eventBus.register(pillarManager);
		axeHider.startUp();
		pillarManager.startUp();
		overlayManager.add(axeOverlay);
		overlayManager.add(headOverlay);
		overlayManager.add(captchaOverlay);
		overlayManager.add(pillarOverlay);
	}

	@Override
	protected void shutDown()
	{
		axeHider.shutDown();
		pillarManager.shutDown();
		eventBus.unregister(axeHider);
		eventBus.unregister(headTracker);
		eventBus.unregister(projectileSwapper);
		eventBus.unregister(pillarManager);
		overlayManager.remove(axeOverlay);
		overlayManager.remove(headOverlay);
		overlayManager.remove(captchaOverlay);
		overlayManager.remove(pillarOverlay);
	}

	@Provides
	BetterDt2UtilitiesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BetterDt2UtilitiesConfig.class);
	}
}
