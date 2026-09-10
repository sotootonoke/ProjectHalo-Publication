package com.protectionaura;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Project Halo",
	description = "Visual-only prayer aura for visible protection prayers",
	tags = {"prayer", "outline", "aura", "overlay", "visual"},
	enabledByDefault = false
)
public class ProjectHaloPlugin extends Plugin
{
	private ProtectionPrayer activeLocalPrayer = ProtectionPrayer.NONE;
	private Color meleeAuraColor;
	private Color rangeAuraColor;
	private Color magicAuraColor;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ProjectHaloOverlay overlay;

	@Inject
	private ProjectHaloConfig config;

	@Override
	protected void startUp()
	{
		refreshCachedColors();
		refreshLocalProtectionPrayer();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		activeLocalPrayer = ProtectionPrayer.NONE;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			refreshLocalProtectionPrayer();
			return;
		}

		activeLocalPrayer = ProtectionPrayer.NONE;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		final int varbitId = event.getVarbitId();
		if (varbitId == Prayer.PROTECT_FROM_MELEE.getVarbit()
			|| varbitId == Prayer.PROTECT_FROM_MISSILES.getVarbit()
			|| varbitId == Prayer.PROTECT_FROM_MAGIC.getVarbit())
		{
			refreshLocalProtectionPrayer();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!ProjectHaloConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		refreshCachedColors();
	}

	Color getAuraColor(Player player)
	{
		final ProtectionPrayer protectionPrayer = resolveProtectionPrayer(player);
		switch (protectionPrayer)
		{
			case MELEE:
				return meleeAuraColor;
			case MISSILES:
				return rangeAuraColor;
			case MAGIC:
				return magicAuraColor;
			case NONE:
			default:
				return null;
		}
	}

	int getAuraFeather()
	{
		return Math.max(2, Math.min(8, config.auraThickness() + 1));
	}

	private void refreshLocalProtectionPrayer()
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			activeLocalPrayer = ProtectionPrayer.NONE;
			return;
		}

		if (isPrayerActive(Prayer.PROTECT_FROM_MELEE))
		{
			activeLocalPrayer = ProtectionPrayer.MELEE;
		}
		else if (isPrayerActive(Prayer.PROTECT_FROM_MISSILES))
		{
			activeLocalPrayer = ProtectionPrayer.MISSILES;
		}
		else if (isPrayerActive(Prayer.PROTECT_FROM_MAGIC))
		{
			activeLocalPrayer = ProtectionPrayer.MAGIC;
		}
		else
		{
			activeLocalPrayer = ProtectionPrayer.NONE;
		}
	}

	private boolean isPrayerActive(Prayer prayer)
	{
		return client.getVarbitValue(prayer.getVarbit()) == 1;
	}

	private ProtectionPrayer resolveProtectionPrayer(Player player)
	{
		if (player == null || player.isDead() || player.getModel() == null)
		{
			return ProtectionPrayer.NONE;
		}

		final Player localPlayer = client.getLocalPlayer();
		if (player == localPlayer)
		{
			return activeLocalPrayer;
		}

		return ProtectionPrayer.NONE;
	}

	private void refreshCachedColors()
	{
		meleeAuraColor = withOpacity(config.meleeColor());
		rangeAuraColor = withOpacity(config.rangeColor());
		magicAuraColor = withOpacity(config.magicColor());
	}

	private Color withOpacity(Color baseColor)
	{
		final int opacityPercent = Math.max(0, Math.min(100, config.auraOpacity()));
		final int alpha = Math.round(opacityPercent * 255.0f / 100.0f);
		return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
	}

	@Provides
	ProjectHaloConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ProjectHaloConfig.class);
	}

	private enum ProtectionPrayer
	{
		NONE,
		MELEE,
		MISSILES,
		MAGIC
	}
}
