/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import org.apache.commons.lang3.ArrayUtils;

@Singleton
public class PillarManager
{
	private static final Set<Integer> PILLAR_IDS = ImmutableSet.of(
		48419, 48420, 48422, 48423, 48424, 48426, 48427, 48428);
	private static final int VARDORVIS_REGION = (17 << 8) | 53;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private BetterDt2UtilitiesConfig config;

	// Tile locations of every pillar, captured while they are still in the
	// scene. These survive hiding, so tile markers can always be drawn.
	@Getter
	private final List<WorldPoint> pillarTiles = new ArrayList<>();

	// Live pillar objects, used for hull/outline highlighting while shown.
	// Once hidden (removed from the scene) they can no longer be drawn.
	@Getter
	private final List<GameObject> pillarObjects = new ArrayList<>();

	public void startUp()
	{
		refresh();
	}

	public void shutDown()
	{
		pillarTiles.clear();
		pillarObjects.clear();
		reloadScene();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			refresh();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(BetterDt2UtilitiesConfig.GROUP) || !event.getKey().equals("hidePillars"))
		{
			return;
		}
		if (config.hidePillars())
		{
			refresh();
		}
		else
		{
			// removeGameObject is not reversible; reload the scene to restore them
			reloadScene();
		}
	}

	private void refresh()
	{
		clientThread.invoke(this::scan);
	}

	private void scan()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		if (!isInArena())
		{
			pillarTiles.clear();
			pillarObjects.clear();
			return;
		}

		boolean hide = config.hidePillars();
		Scene scene = client.getScene();
		Tile[][] tiles = scene.getTiles()[client.getPlane()];
		List<WorldPoint> foundTiles = new ArrayList<>();
		List<GameObject> foundObjects = new ArrayList<>();
		Set<GameObject> seen = new HashSet<>();

		for (int x = 0; x < Constants.SCENE_SIZE; ++x)
		{
			for (int y = 0; y < Constants.SCENE_SIZE; ++y)
			{
				Tile tile = tiles[x][y];
				if (tile == null)
				{
					continue;
				}
				for (GameObject gameObject : tile.getGameObjects())
				{
					if (gameObject != null && PILLAR_IDS.contains(gameObject.getId()))
					{
						foundTiles.add(tile.getWorldLocation());
						if (seen.add(gameObject))
						{
							foundObjects.add(gameObject);
						}
						if (hide)
						{
							scene.removeGameObject(gameObject);
						}
						break;
					}
				}
			}
		}

		// Only replace when pillars were actually found, so a re-scan after they
		// have already been hidden does not wipe the tracked locations.
		if (!foundTiles.isEmpty())
		{
			pillarTiles.clear();
			pillarTiles.addAll(foundTiles);
			pillarObjects.clear();
			pillarObjects.addAll(foundObjects);
		}
	}

	private void reloadScene()
	{
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				client.setGameState(GameState.LOADING);
			}
		});
	}

	private boolean isInArena()
	{
		return ArrayUtils.contains(client.getMapRegions(), VARDORVIS_REGION);
	}
}
