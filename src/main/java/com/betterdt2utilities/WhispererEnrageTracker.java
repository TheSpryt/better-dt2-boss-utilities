/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class WhispererEnrageTracker
{
	// The normal Whisperer's max health, used to turn the health-bar ratio into an
	// approximate hitpoints value for the enrage threshold.
	private static final int MAX_HP = 900;
	// Once enrage is set it stays set until she has been gone this many ticks.
	private static final int ABSENT_RESET_TICKS = 15;

	@Inject
	private Client client;

	@Inject
	private BetterDt2UtilitiesConfig config;

	private boolean enrage;
	private int absentTicks;

	public boolean isEnrage()
	{
		return enrage;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		boolean found = false;
		for (NPC npc : client.getNpcs())
		{
			if (!isWhisperer(npc))
			{
				continue;
			}
			found = true;
			int scale = npc.getHealthScale();
			int ratio = npc.getHealthRatio();
			if (scale > 0 && ratio >= 0)
			{
				int hp = ratio * MAX_HP / scale;
				// Once she drops to the enrage threshold it latches for the rest of the
				// fight, so it survives the enrage shadow realm where she is unreadable.
				if (hp > 0 && hp <= config.lMovementEnrageHp())
				{
					enrage = true;
				}
			}
		}

		// Cleared only once she has been absent for a while, i.e. the fight is over.
		if (found)
		{
			absentTicks = 0;
		}
		else if (enrage && ++absentTicks > ABSENT_RESET_TICKS)
		{
			enrage = false;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			enrage = false;
			absentTicks = 0;
		}
	}

	private boolean isWhisperer(NPC npc)
	{
		String name = npc.getName();
		return name != null && name.toLowerCase(Locale.ROOT).contains("whisperer");
	}
}
