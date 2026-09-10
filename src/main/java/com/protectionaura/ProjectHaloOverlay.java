package com.protectionaura;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class ProjectHaloOverlay extends Overlay
{
	private final Client client;
	private final ProjectHaloPlugin plugin;
	private final ProjectHaloConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private ProjectHaloOverlay(
		Client client,
		ProjectHaloPlugin plugin,
		ProjectHaloConfig config,
		ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.enabled() || client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}

		renderPlayer(client.getLocalPlayer());
		return null;
	}

	private void renderPlayer(Player player)
	{
		final Color auraColor = plugin.getAuraColor(player);
		if (auraColor == null)
		{
			return;
		}

		modelOutlineRenderer.drawOutline(
			player,
			config.auraThickness(),
			auraColor,
			plugin.getAuraFeather());
	}
}
