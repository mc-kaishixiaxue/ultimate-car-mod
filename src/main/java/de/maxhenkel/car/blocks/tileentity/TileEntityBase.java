package de.maxhenkel.car.blocks.tileentity;

import de.maxhenkel.tools.MathTools;
import de.maxhenkel.car.net.MessageSyncTileEntity;
import de.maxhenkel.car.proxy.CommonProxy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBase extends TileEntity{

	private NBTTagCompound compoundLast;
	
	public void synchronize(){
		if (!worldObj.isRemote) {
			NBTTagCompound last=writeToNBT(new NBTTagCompound());
			if(compoundLast==null||!compoundLast.equals(last)){
				CommonProxy.simpleNetworkWrapper.sendToAllAround(new MessageSyncTileEntity(pos, last), MathTools.getTileEntityTargetPoint(this));
				this.compoundLast=last;
			}
		}
	}
	
	public void synchronize(int ticks){
		if(worldObj.getTotalWorldTime()%ticks==0){
			synchronize();
		}
	}
	
}
