/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.ArrayUtils;

@Singleton
public class WhispererSeedsManager
{
	private static final int WHISPERER_REGION = 10595;
	private static final int SHADOW_REGION = 9571;
	// The orbs arrive in waves with a short lull between them, so the phase must
	// survive that gap without ending - otherwise the marks vanish mid-phase and
	// only return on the next wave. This bridges the gap while still clearing the
	// marks promptly once the seeds are actually gone.
	private static final int SEEDS_GRACE = 7;

	// The seed-phase orb graphics. These fire only during corrupted seeds, which
	// is what separates a real phase from the pillar-phase floor flood (graphic
	// 2451). The graphics do NOT distinguish safe from dangerous tiles - only that
	// a seed phase is happening - so they are used purely for phase detection.
	private static final Set<Integer> SEED_GRAPHICS = ImmutableSet.of(2459, 2460, 2464, 2465);
	// The seed game objects. In the real realm the seed is undifferentiated (47575);
	// in the Shadow Realm it is pre-classified as good/safe (47573) or bad/dark
	// (47574), which resolves the pattern outright.
	private static final int SEED_REAL = 47575;
	private static final int SEED_SHADOW_GOOD = 47573;
	private static final int SEED_SHADOW_BAD = 47574;
	private static final Set<Integer> SEED_OBJECT_IDS = ImmutableSet.of(SEED_REAL, SEED_SHADOW_GOOD, SEED_SHADOW_BAD);

	// Safe-seed positions per pattern, as {regionX, regionY} in region 10595.
	private static final int[][] PATTERN_1 = {
		{31, 37}, {33, 37}, {31, 39}, {33, 39}, {35, 35}, {37, 33}, {35, 31},
		{33, 29}, {31, 29}, {31, 27}, {33, 27}, {29, 31}, {27, 33}, {29, 35},
	};
	private static final int[][] PATTERN_2 = {
		{31, 37}, {33, 37}, {35, 35}, {37, 35}, {37, 33}, {37, 31}, {35, 31},
		{33, 29}, {31, 29}, {29, 31}, {27, 31}, {27, 33}, {27, 35}, {29, 35},
	};
	private static final int[][] PATTERN_3 = {
		{32, 35}, {34, 37}, {30, 37}, {36, 35}, {36, 31}, {34, 29}, {32, 31},
		{28, 31}, {28, 35}, {30, 33}, {30, 29}, {34, 33},
	};
	private static final int[][] PATTERN_4 = {
		{32, 35}, {34, 37}, {32, 37}, {30, 37}, {36, 35}, {36, 33}, {36, 31},
		{34, 29}, {32, 31}, {32, 29}, {28, 31}, {28, 33}, {28, 35}, {30, 33},
		{30, 29}, {34, 33},
	};

	// patterns.get(0..3) -> the safe-seed tiles of that pattern
	private static final List<Set<WorldPoint>> PATTERNS = new ArrayList<>();
	private static final Set<WorldPoint> ALL_SEED_TILES = new HashSet<>();
	// Family id per pattern. Two patterns are the same family if they share a tile.
	// Seeds only ever appear on the active pattern's family, so an orb on any tile
	// of a family reveals the family and rules the others out. Here: {P1,P2} and
	// {P3,P4}.
	private static final int[] FAMILY = new int[4];

	static
	{
		for (int[][] raw : new int[][][]{PATTERN_1, PATTERN_2, PATTERN_3, PATTERN_4})
		{
			Set<WorldPoint> set = new HashSet<>();
			for (int[] t : raw)
			{
				WorldPoint wp = new WorldPoint(2624 + t[0], 6336 + t[1], 0);
				set.add(wp);
				ALL_SEED_TILES.add(wp);
			}
			PATTERNS.add(set);
		}

		// Connected components by shared tiles (relabel-to-min until stable).
		for (int i = 0; i < FAMILY.length; i++)
		{
			FAMILY[i] = i;
		}
		boolean changed = true;
		while (changed)
		{
			changed = false;
			for (int i = 0; i < PATTERNS.size(); i++)
			{
				for (int j = i + 1; j < PATTERNS.size(); j++)
				{
					if (FAMILY[i] != FAMILY[j] && !Collections.disjoint(PATTERNS.get(i), PATTERNS.get(j)))
					{
						int lo = Math.min(FAMILY[i], FAMILY[j]);
						FAMILY[i] = lo;
						FAMILY[j] = lo;
						changed = true;
					}
				}
			}
		}
	}

	@Inject
	private Client client;

	@Inject
	private BetterDt2UtilitiesConfig config;

	private boolean seedsActive;
	private int lastSeenTick = -100;
	// Pattern indices (0-3) still consistent with everything observed this phase.
	private final Set<Integer> possiblePatterns = new HashSet<>();
	// Tiles proven dangerous this phase - damage was taken while standing on them.
	// Damage during a special phase is always a seed (there are no normal attacks),
	// so this is a fully reliable signal.
	private final Set<WorldPoint> darkTiles = new HashSet<>();
	// Tiles the player has stood on unharmed this phase, i.e. proven safe. Fed only
	// by the player's own position, never by the orb graphics. Used to narrow the
	// pattern (not to clear the display - that is driven by the live seed objects).
	private final Set<WorldPoint> safeTiles = new HashSet<>();
	// Tiles that currently have a live seed object on them. A marked tile is only
	// shown while its seed is present, so the instant a seed pops - whether stepped
	// on or bloomed - its tile clears, regardless of the deduction.
	private final Set<WorldPoint> liveSeedTiles = new HashSet<>();
	// Whether the player took real damage this tick, so the tile they are on is not
	// also counted as safe on the same tick.
	private boolean damagedThisTick;
	// The family the seeds belong to this phase (-1 until an orb reveals it). Once
	// known, only patterns of that family remain candidates.
	private int knownFamily = -1;

	public boolean isSeedsActive()
	{
		return seedsActive;
	}

	// Tiles to draw: the safe tiles of every still-possible pattern that currently
	// have a live seed on them, minus any tile proven dark. Gating on the live seed
	// objects means a tile clears the instant its seed pops (stepped on or bloomed),
	// which avoids tiles lingering when a step and a pattern-narrow land on the same
	// tick. Seeds only spawn on the active family's tiles, so this naturally spans
	// only that family.
	public Set<WorldPoint> getTilesToShow()
	{
		Set<WorldPoint> show = new HashSet<>();
		for (int p : possiblePatterns)
		{
			show.addAll(PATTERNS.get(p));
		}
		show.retainAll(liveSeedTiles);
		show.removeAll(darkTiles);
		return show;
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		if (!inWhispererRegion())
		{
			return;
		}
		int id = event.getGraphicsObject().getId();

		// Any seed orb (in either realm) marks that a corrupted-seeds phase is
		// running. The pillar-phase floor flood uses a different graphic and so
		// never lands here.
		if (SEED_GRAPHICS.contains(id))
		{
			markSeedActivity();

			// An orb landing on a pattern tile reveals which family the seeds belong
			// to, ruling out the other family (whose tiles have no seed on them).
			WorldPoint owp = worldPointOf(event.getGraphicsObject().getLocation());
			if (owp != null && ALL_SEED_TILES.contains(owp) && knownFamily == -1)
			{
				knownFamily = familyOfTile(owp);
				recompute();
			}
		}
	}

	private int familyOfTile(WorldPoint tile)
	{
		for (int i = 0; i < PATTERNS.size(); i++)
		{
			if (PATTERNS.get(i).contains(tile))
			{
				return FAMILY[i];
			}
		}
		return -1;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject go = event.getGameObject();
		int id = go.getId();
		if (!SEED_OBJECT_IDS.contains(id))
		{
			return;
		}
		WorldPoint wp = worldPointOf(go.getLocalLocation());
		if (wp == null)
		{
			return;
		}
		liveSeedTiles.add(wp);
		markSeedActivity();

		// The Shadow Realm seeds are pre-classified, so they resolve the pattern
		// directly - and that resolution persists into the real realm.
		if (id == SEED_SHADOW_GOOD)
		{
			darkTiles.remove(wp);
			safeTiles.add(wp);
			recompute();
		}
		else if (id == SEED_SHADOW_BAD)
		{
			safeTiles.remove(wp);
			darkTiles.add(wp);
			recompute();
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject go = event.getGameObject();
		if (SEED_OBJECT_IDS.contains(go.getId()))
		{
			WorldPoint wp = worldPointOf(go.getLocalLocation());
			if (wp != null)
			{
				liveSeedTiles.remove(wp);
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() != client.getLocalPlayer() || !seedsActive)
		{
			return;
		}
		int amount = event.getHitsplat().getAmount();
		WorldPoint tile = playerTile();

		if (amount > 0)
		{
			damagedThisTick = true;
		}

		// Real damage on the tile you popped proves it is dark: hide it and every
		// pattern that called it safe, immediately. Prayer-blocked autos arrive as
		// 0-damage splats and are ignored.
		if (amount > 0 && tile != null && ALL_SEED_TILES.contains(tile))
		{
			darkTiles.add(tile);
			safeTiles.remove(tile);
			recompute();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!inWhispererRegion())
		{
			endPhase();
			return;
		}

		if (!seedsActive)
		{
			return;
		}

		// Sustain the phase while seeds are still present (orbs or seed objects, in
		// either realm); end it once they are gone for the grace window - which also
		// bridges the brief gap while toggling between realms.
		if (seedGraphicPresent() || !liveSeedTiles.isEmpty())
		{
			lastSeenTick = client.getTickCount();
		}
		else if (client.getTickCount() - lastSeenTick > SEEDS_GRACE)
		{
			endPhase();
			return;
		}

		// The tile you are standing on, having popped its seed without taking damage
		// this tick, is proven safe: keep only the patterns that contain it (removing
		// the other sub-pattern's tiles) and clear it as crossed - all immediately.
		WorldPoint tile = playerTile();
		if (!damagedThisTick && tile != null && ALL_SEED_TILES.contains(tile) && !darkTiles.contains(tile) && safeTiles.add(tile))
		{
			recompute();
		}

		damagedThisTick = false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState state = event.getGameState();
		// Toggling realms triggers a LOADING - clear the stale seed objects but keep
		// the deduction so the Shadow Realm result carries into the real realm. A true
		// logout/hop ends the phase.
		if (state == GameState.LOADING)
		{
			liveSeedTiles.clear();
		}
		else if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING)
		{
			endPhase();
			liveSeedTiles.clear();
		}
	}

	private void markSeedActivity()
	{
		if (!seedsActive)
		{
			resetSolver();
			seedsActive = true;
		}
		lastSeenTick = client.getTickCount();
	}

	private void endPhase()
	{
		seedsActive = false;
		resetSolver();
	}

	// Rebuild the candidate set from scratch against every constraint gathered so
	// far: a pattern survives only if it contains all proven-safe tiles and none of
	// the proven-dark tiles. Recomputing (rather than incrementally eliminating)
	// keeps subset patterns and event ordering from ever corrupting the result. If
	// the constraints are momentarily contradictory, damage wins over safe.
	private void recompute()
	{
		Set<Integer> next = matching(safeTiles, darkTiles);
		if (next.isEmpty())
		{
			next = matching(Collections.emptySet(), darkTiles);
		}
		if (next.isEmpty())
		{
			next = matching(Collections.emptySet(), Collections.emptySet());
		}
		possiblePatterns.clear();
		possiblePatterns.addAll(next);
	}

	private Set<Integer> matching(Set<WorldPoint> mustContain, Set<WorldPoint> mustExclude)
	{
		Set<Integer> out = new HashSet<>();
		for (int i = 0; i < PATTERNS.size(); i++)
		{
			if (knownFamily != -1 && FAMILY[i] != knownFamily)
			{
				continue;
			}
			Set<WorldPoint> pat = PATTERNS.get(i);
			if (pat.containsAll(mustContain) && Collections.disjoint(pat, mustExclude))
			{
				out.add(i);
			}
		}
		return out;
	}

	// True while any seed orb is still playing on the floor.
	private boolean seedGraphicPresent()
	{
		for (GraphicsObject go : client.getGraphicsObjects())
		{
			if (!go.finished() && SEED_GRAPHICS.contains(go.getId()))
			{
				return true;
			}
		}
		return false;
	}

	private void resetSolver()
	{
		possiblePatterns.clear();
		possiblePatterns.addAll(allPatternIndices());
		darkTiles.clear();
		safeTiles.clear();
		damagedThisTick = false;
		knownFamily = -1;
	}

	private Set<Integer> allPatternIndices()
	{
		Set<Integer> all = new HashSet<>();
		for (int i = 0; i < PATTERNS.size(); i++)
		{
			all.add(i);
		}
		return all;
	}

	// The player's logical tile, resolved through the instance to region-10595
	// coordinates. Built from getWorldLocation() (the server tile, updated once per
	// tick) rather than getLocalLocation() (interpolated between tiles while moving),
	// so damage and safe steps are attributed to the tile actually stood on.
	private WorldPoint playerTile()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}
		LocalPoint lp = LocalPoint.fromWorld(client, player.getWorldLocation());
		return worldPointOf(lp);
	}

	// Resolve a local point to the pattern's coordinate frame. The Shadow Realm mirror
	// sits in a different region (9571) from the real arena (10595) but at the same
	// region-relative tiles, so normalising to region-relative + the real base keeps
	// the deduction consistent whichever realm the player is in.
	private WorldPoint worldPointOf(LocalPoint lp)
	{
		if (lp == null)
		{
			return null;
		}
		WorldPoint w = WorldPoint.fromLocalInstance(client, lp);
		return w == null ? null : new WorldPoint(2624 + w.getRegionX(), 6336 + w.getRegionY(), 0);
	}

	private boolean inWhispererRegion()
	{
		int[] regions = client.getMapRegions();
		return ArrayUtils.contains(regions, WHISPERER_REGION) || ArrayUtils.contains(regions, SHADOW_REGION);
	}
}
