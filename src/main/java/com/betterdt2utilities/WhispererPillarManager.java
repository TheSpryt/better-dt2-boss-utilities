/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class WhispererPillarManager
{
	static final int PILLAR_SHADOW = 12209;
	static final int PILLAR_NORMAL = 12210;
	// Drop the remembered solve once the pillars have been gone this long.
	private static final int CLEAR_AFTER_TICKS = 25;

	@Inject
	private Client client;

	@Inject
	private BetterDt2UtilitiesConfig config;

	// Template world point of each pillar -> the health tier read in the Shadow
	// Realm (kept as the maximum seen, i.e. the value before screech damage).
	private final Map<WorldPoint, Integer> pillarHp = new HashMap<>();
	// Ordered hide sequence (template world points), lowest health first.
	private List<WorldPoint> solution = new ArrayList<>();
	private int lastSeenTick = -100;

	public List<WorldPoint> getSolution()
	{
		return solution;
	}

	// The floating columns - same ids in the awakened fight, so id is sufficient.
	public boolean isPillar(NPC npc)
	{
		int id = npc.getId();
		return id == PILLAR_SHADOW || id == PILLAR_NORMAL;
	}

	// The 1-based hide order for a pillar at the given template point, or 0 if it is
	// not part of the solve.
	public int orderOf(WorldPoint template)
	{
		int idx = solution.indexOf(template);
		return idx < 0 ? 0 : idx + 1;
	}

	// A pillar's position as region-relative coordinates. The Shadow Realm sits in a
	// different template region (9571) from the real arena (10595), but a pillar
	// occupies the same region-relative tile in both, so keying on that lets the
	// health read in the shadow realm carry over to the real-realm pillar.
	public WorldPoint templatePoint(NPC npc)
	{
		LocalPoint lp = npc.getLocalLocation();
		if (lp == null)
		{
			return null;
		}
		WorldPoint w = WorldPoint.fromLocalInstance(client, lp);
		return w == null ? null : new WorldPoint(w.getRegionX(), w.getRegionY(), 0);
	}

	// The pillars share a max health of 60 but spawn at 20/40/60, shown on a fixed
	// 40-unit bar: ratio 14 -> 20hp, 27 -> 40hp, 40 -> 60hp. The ratio is therefore
	// the health tier, and sorting by it ascending gives the hide order.
	private int healthOf(NPC npc)
	{
		return npc.getHealthRatio();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!config.solvePillars())
		{
			return;
		}

		boolean sawShadow = false;
		boolean sawAny = false;
		for (NPC npc : client.getNpcs())
		{
			if (!isPillar(npc))
			{
				continue;
			}
			sawAny = true;
			lastSeenTick = client.getTickCount();
			// A visible health bar means this is the Shadow Realm form, whatever its
			// id - that is where the tier can be read.
			int hp = healthOf(npc);
			if (hp > 0 && npc.getHealthScale() > 0)
			{
				sawShadow = true;
				WorldPoint wp = templatePoint(npc);
				if (wp != null)
				{
					pillarHp.merge(wp, hp, Math::max);
				}
			}
		}

		if (sawShadow)
		{
			recomputeSolution();
		}
		if (!sawAny && !pillarHp.isEmpty() && client.getTickCount() - lastSeenTick > CLEAR_AFTER_TICKS)
		{
			clear();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING
			|| event.getGameState() == GameState.LOGIN_SCREEN
			|| event.getGameState() == GameState.HOPPING)
		{
			clear();
		}
	}

	// Choose one pillar per distinct health tier, ascending (hide the lowest first).
	// Every one-per-tier combination is scored and the best is kept: the primary
	// objective is the shortest total run between consecutive pillars (so the whole
	// 1 -> 2 -> 3 chain stays grouped and there is no dash across the arena); ties
	// are broken by the pillar closest to the player.
	private void recomputeSolution()
	{
		TreeMap<Integer, List<WorldPoint>> byTier = new TreeMap<>();
		for (Map.Entry<WorldPoint, Integer> e : pillarHp.entrySet())
		{
			byTier.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
		}

		List<List<WorldPoint>> tiers = new ArrayList<>(byTier.values());
		if (tiers.isEmpty())
		{
			solution = new ArrayList<>();
			return;
		}

		WorldPoint player = playerPoint();
		List<WorldPoint> best = null;
		int bestChain = Integer.MAX_VALUE;
		int bestPlayer = Integer.MAX_VALUE;

		int[] idx = new int[tiers.size()];
		while (true)
		{
			List<WorldPoint> combo = new ArrayList<>();
			for (int i = 0; i < tiers.size(); i++)
			{
				combo.add(tiers.get(i).get(idx[i]));
			}

			int chain = 0;
			for (int i = 1; i < combo.size(); i++)
			{
				chain += combo.get(i - 1).distanceTo(combo.get(i));
			}
			int toPlayer = player == null ? 0 : player.distanceTo(combo.get(0));

			if (chain < bestChain || (chain == bestChain && toPlayer < bestPlayer))
			{
				best = combo;
				bestChain = chain;
				bestPlayer = toPlayer;
			}

			int k = tiers.size() - 1;
			while (k >= 0 && ++idx[k] >= tiers.get(k).size())
			{
				idx[k] = 0;
				k--;
			}
			if (k < 0)
			{
				break;
			}
		}

		solution = best;
	}

	private WorldPoint playerPoint()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}
		LocalPoint lp = LocalPoint.fromWorld(client, player.getWorldLocation());
		WorldPoint w = lp == null ? null : WorldPoint.fromLocalInstance(client, lp);
		return w == null ? null : new WorldPoint(w.getRegionX(), w.getRegionY(), 0);
	}

	private void clear()
	{
		pillarHp.clear();
		solution = new ArrayList<>();
	}
}
