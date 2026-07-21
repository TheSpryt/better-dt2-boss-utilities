/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;

/**
 * Replaces the Whisperer's and Vardorvis' prayer-disable projectiles with the
 * range/magic pair of a chosen boss style. A boss set to its own style is left
 * untouched (the target id equals the source id, so nothing is swapped).
 */
public class ProjectileSwapper
{
	private static final int WHISPERER_RANGE = 2444;
	private static final int WHISPERER_MAGIC = 2445;
	private static final int VARDORVIS_RANGE = 2521;
	private static final int VARDORVIS_MAGIC = 2520;

	private final Client client;
	private final BetterDt2UtilitiesConfig config;

	@Inject
	private ProjectileSwapper(Client client, BetterDt2UtilitiesConfig config)
	{
		this.client = client;
		this.config = config;
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
		Projectile projectile = event.getProjectile();
		switch (projectile.getId())
		{
			case WHISPERER_RANGE:
				maybeSwap(projectile, WHISPERER_RANGE, config.whispererRangeProjectile().getRange());
				break;
			case WHISPERER_MAGIC:
				maybeSwap(projectile, WHISPERER_MAGIC, config.whispererMageProjectile().getMagic());
				break;
			case VARDORVIS_RANGE:
				maybeSwap(projectile, VARDORVIS_RANGE, config.vardorvisRangeProjectile().getRange());
				break;
			case VARDORVIS_MAGIC:
				maybeSwap(projectile, VARDORVIS_MAGIC, config.vardorvisMageProjectile().getMagic());
				break;
		}
	}

	private void maybeSwap(Projectile projectile, int sourceId, int targetId)
	{
		if (targetId <= 0 || targetId == sourceId)
		{
			return;
		}
		replaceProjectile(projectile, targetId);
	}

	private void replaceProjectile(Projectile projectile, int projectileId)
	{
		Projectile p = client.createProjectile(projectileId,
			projectile.getFloor(),
			projectile.getX1(), projectile.getY1(),
			projectile.getHeight(),
			projectile.getStartCycle(), projectile.getEndCycle(),
			projectile.getSlope(),
			projectile.getStartHeight(), projectile.getEndHeight(),
			projectile.getInteracting(),
			projectile.getTarget().getX(), projectile.getTarget().getY());

		client.getProjectiles().addLast(p);
		projectile.setEndCycle(0);
	}
}
