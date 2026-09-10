package com.protectionaura;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ProjectHaloPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ProjectHaloPlugin.class);
		RuneLite.main(args);
	}
}
