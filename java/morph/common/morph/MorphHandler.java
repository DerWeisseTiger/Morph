package morph.common.morph;

import ichun.core.network.PacketHandler;
import morph.common.Morph;
import morph.common.packet.PacketMorphStates;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class MorphHandler 
{

    //TODO find out that getting named morphs (eg name tags) still work as intended
	public static MorphState addOrGetMorphState(ArrayList<MorphState> states, MorphState state)
	{
		int pos = -1;
		boolean isFavourite = state.isFavourite;
		for(int i = states.size() - 1; i >= 0; i--)
		{
			MorphState mState = states.get(i);
			if(mState.identifier.equalsIgnoreCase(state.identifier) || !state.playerMorph.equalsIgnoreCase("") && mState.playerMorph.equalsIgnoreCase(state.playerMorph))
			{
				isFavourite = mState.isFavourite;
				states.remove(i);
				pos = i;
			}
		}
		if(state.playerName.equalsIgnoreCase(state.playerMorph) && !state.playerMorph.equalsIgnoreCase(""))
		{
			isFavourite = true;
			pos = 0;
		}
		if(pos != -1)
		{
			states.add(pos, state);
		}
		else
		{
			states.add(state);
		}
		state.isFavourite = isFavourite;
		if(Morph.sortMorphs == 2)
		{
			Collections.sort(states);
		}
		return state;
	}
	
	public static void updatePlayerOfMorphStates(EntityPlayerMP player, MorphState morphState, boolean clear)
	{
		if(player.playerNetServerHandler == null || player.getClass() == FakePlayer.class)
		{
			return;
		}
		ArrayList<MorphState> states;
		if(morphState == null)
		{
			states = Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, player.getCommandSenderName());
		}
		else
		{
			states = new ArrayList<MorphState>();
			states.add(morphState);
		}

        PacketHandler.sendToPlayer(Morph.channels, new PacketMorphStates(clear, states), player);
	}

	public static MorphState getMorphState(EntityPlayerMP player, String identifier) 
	{
		ArrayList<MorphState> states = Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, player.getCommandSenderName());
		
		for(MorphState state : states)
		{
			if(state.identifier.equalsIgnoreCase(identifier))
			{
				return state;
			}
		}
		return null;
	}

	public static void reorderMorphs(String starter, LinkedHashMap<String, ArrayList<MorphState>> morphMap) 
	{
		if(Morph.sortMorphs == 1 || Morph.sortMorphs == 2)
		{
			ArrayList<String> order = new ArrayList<String>();
			Iterator<String> ite = morphMap.keySet().iterator();
			while(ite.hasNext())
			{
				order.add(ite.next());
			}
			Collections.sort(order);
			
			order.remove(starter);
			
			order.add(0, starter);
			
			LinkedHashMap<String, ArrayList<MorphState>> bufferList = new LinkedHashMap<String, ArrayList<MorphState>>(morphMap);
			
			morphMap.clear();
			
			for(int i = 0; i < order.size(); i++)
			{
				morphMap.put(order.get(i), bufferList.get(order.get(i)));
			}
		}
	}
	
}
