package NEILootbags.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI
{
	@Override
	public String getName() 
	{
		return "NEILootbags";
	}

	@Override
	public String getVersion() 
	{
		return "1.1.2";
	}

	@Override
	public void loadConfig() 
	{
		NEILootbagHandler handler = new NEILootbagHandler();
		API.registerRecipeHandler(handler);
		API.registerUsageHandler(handler);
	}
}
