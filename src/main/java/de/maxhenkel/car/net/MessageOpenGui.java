package de.maxhenkel.car.net;

import java.util.UUID;

import de.maxhenkel.car.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageOpenGui implements IMessage, IMessageHandler<MessageOpenGui, IMessage>{

	private int posX;
	private int posY;
	private int posZ;
	private int guiID;
	private UUID uuid;
	
	public MessageOpenGui() {
		this.uuid=new UUID(0, 0);
	}
	
	public MessageOpenGui(BlockPos pos, int guiID, EntityPlayer player) {
		this.posX=pos.getX();
		this.posY=pos.getY();
		this.posZ=pos.getZ();
		this.guiID=guiID;
		this.uuid=player.getUniqueID();
	}

	@Override
	public IMessage onMessage(MessageOpenGui message, MessageContext ctx) {
		if(ctx.side.equals(Side.SERVER)){
			EntityPlayer player=ctx.getServerHandler().playerEntity;
			
			if(!player.getUniqueID().equals(message.uuid)){
				return null;
			}
			
			player.openGui(Main.instance(), message.guiID, player.world, message.posX, message.posY, message.posZ);
			
		}
		return null;
	}
	
	public MessageOpenGui open(EntityPlayer player){
		if(player.world.isRemote){
			player.openGui(Main.instance(), guiID, player.world, posX, posY, posZ);
		}
		return this;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.posX=buf.readInt();
		this.posY=buf.readInt();
		this.posZ=buf.readInt();
		this.guiID=buf.readInt();
		
		long l1=buf.readLong();
		long l2=buf.readLong();
		this.uuid=new UUID(l1, l2);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(posX);
		buf.writeInt(posY);
		buf.writeInt(posZ);
		buf.writeInt(guiID);
		
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}

}
