package NEILootbags.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import mal.lootbags.Bag;
import mal.lootbags.LootBags;
import mal.lootbags.LootbagsUtil;
import mal.lootbags.handler.BagHandler;
import mal.lootbags.item.LootbagItem;
import mal.lootbags.loot.LootItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class NEILootbagHandler extends TemplateRecipeHandler 
{
	private static final int X_FIRST_ITEM = 2;
	private static final int Y_FIRST_ITEM = 51;

	private static final int ITEMS_PER_PAGE = 9 * 4;
	private static final int SPACING_X = 166 / 9;
	private static final int SPACING_Y = 80 / 4;
	private static int bagtype;
    private static int lastRecipe = -1;
    private static int CYCLE_TIME = (int) (20 * 2);



	@Override
	public String getRecipeName() 
	{
		return "Lootbag's Loot";
	}

	@Override
	public String getGuiTexture() 
	{
		return "neilootbags:textures/gui/NEILootGUI1.png";
	}

	@Override
	public void drawBackground(int recipe) 
	{
		GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 130);
	}

	@Override
	public int recipiesPerPage() 
	{
		return 1;
	}
	
	@Override
	public void loadCraftingRecipes(ItemStack result) 
	{
		bagtype = 0;
		for (int k = 0; k < BagHandler.getHighestUsedID(); k++) 
		{
			Bag b = BagHandler.getBag(k);

			for (int i = 0; i < b.getMap().size(); i++) 
			{
				if (LootBags.areItemStacksEqualItem(b.getSpecificItem(i), result, true, true)) 
				{
					arecipes.add(new CachedLootRecipe(k));
					this.bagtype = k;
		            lastRecipe = -1;
					break;
				}
				else super.loadCraftingRecipes(result);
			}
		}
	}
	
	@Override
	public void loadUsageRecipes(ItemStack ingredient) 
	{
		bagtype = 0;
		if (ingredient.getItem() instanceof LootbagItem)
		{
			int meta = ingredient.getItemDamage();
			Bag b = BagHandler.getBag(meta);
			arecipes.add(new CachedLootRecipe(meta));
			this.bagtype = meta;
            lastRecipe = -1;
		}
		else super.loadUsageRecipes(ingredient);
	}

	@Override
	public void drawExtras(int recipe) 
	{
		CachedLootRecipe cachedloot = (CachedLootRecipe) arecipes.get(recipe);
		cachedloot.cycleOutputs(cycleticks, recipe);
	}

	public class CachedLootRecipe extends TemplateRecipeHandler.CachedRecipe 
	{
		private ItemStack stack;
		public int set, lastSet;
		private long cycleAt;
		public List<ItemStack> outputs;

		public CachedLootRecipe(int bagtype) 
		{
			set = 0;
            cycleAt = -1;

			this.outputs = new ArrayList<ItemStack>();

			Bag b = BagHandler.getBag(bagtype);

			for (int i = 0; i < b.getMap().size(); i++) 
			{
				outputs.add(b.getSpecificItem(i));
	            this.lastSet = (this.outputs.size() / (ITEMS_PER_PAGE + 1));
			}
		}

		@Override
		public PositionedStack getIngredient() 
		{
			return new PositionedStack(new ItemStack(LootBags.lootbagItem, 1, bagtype), 75, 5);
		}

		@Override
		public PositionedStack getResult() 
		{
            return new PositionedStack(this.outputs.get(set * ITEMS_PER_PAGE), X_FIRST_ITEM, Y_FIRST_ITEM);
		}

		@Override
		public List<PositionedStack> getOtherStacks() 
		{
			List<PositionedStack> list = new ArrayList<PositionedStack>();
			int x = X_FIRST_ITEM;
			int y = Y_FIRST_ITEM;
			for (int i = ITEMS_PER_PAGE * set; i < ITEMS_PER_PAGE * set + ITEMS_PER_PAGE; i++) 
			{
				if (i >= this.outputs.size()) break;
				list.add(new PositionedStack(this.outputs.get(i), x, y));
				y += SPACING_Y;
				if (y >= Y_FIRST_ITEM + SPACING_Y * 4) 
				{
					y = Y_FIRST_ITEM;
					x += SPACING_X;
				}
			}
			if (list.size() > 0)
				list.remove(0);
			return list;
		}
		
        public void cycleOutputs(long tick, int recipe)
        {
            if (cycleAt == -1 || recipe != lastRecipe)
            {
                lastRecipe = recipe;
                cycleAt = tick + CYCLE_TIME;
                return;
            }

            if (tick >= cycleAt)
            {
                if (++set > lastSet) set = 0;
                cycleAt += CYCLE_TIME;
            }
        }
	}
}