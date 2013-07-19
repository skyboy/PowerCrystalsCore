package powercrystals.core.position;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockPosition
{
	private static BlockPosition pool;
	
	public static BlockPosition create()
	{
		BlockPosition ret;
		if (pool == null)
		{
			ret = new BlockPosition(0, 0, 0);
		}
		else
		{
			ret = pool;
			pool = ret.next;
			ret.next = null;
		}
		
		return ret;
	}
	
	public int x;
	public int y;
	public int z;
	public ForgeDirection orientation;
	private BlockPosition next;
	
	public BlockPosition(int x, int y, int z) { from(x, y, z); }
	
	public BlockPosition(int x, int y, int z, ForgeDirection orientation) { from(x, y, z, orientation); }
	
	public BlockPosition(BlockPosition p) { from(p); }
	
	public BlockPosition(NBTTagCompound nbttagcompound) { from(nbttagcompound); }
	
	public BlockPosition(TileEntity tile) { from(tile); }
	
	public BlockPosition from(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		orientation = ForgeDirection.UNKNOWN;
		return this;
	}
	
	public BlockPosition from(int x, int y, int z, ForgeDirection corientation)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		orientation = corientation;
		return this;
	}
	
	public BlockPosition from(BlockPosition p)
	{
		x = p.x;
		y = p.y;
		z = p.z;
		orientation = p.orientation;
		return this;
	}
	
	public BlockPosition from(NBTTagCompound tag)
	{
		x = tag.getInteger("i");
		y = tag.getInteger("j");
		z = tag.getInteger("k");
		
		orientation = tag.hasKey("o") ? ForgeDirection.values()[tag.getByte("o")] : ForgeDirection.UNKNOWN;
		return this;
	}
	
	public BlockPosition from(TileEntity tile)
	{
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		orientation = ForgeDirection.UNKNOWN;
		return this;
	}
	
	public static BlockPosition fromFactoryTile(IRotateableTile te)
	{
		BlockPosition bp = create().from((TileEntity)te);
		bp.orientation = te.getDirectionFacing();
		return bp;
	}
	
	public BlockPosition copy()
	{
		return create().from(x, y, z, orientation);
	}
	
	public BlockPosition free()
	{
		next = pool;
		pool = this;
		return this;
	}
	
	public void moveRight(int step)
	{
		switch(orientation)
		{
		case SOUTH:
			x = x - step;
			break;
		case NORTH:
			x = x + step;
			break;
		case EAST:
			z = z + step;
			break;
		case WEST:
			z = z - step;
			break;
		default:
			break;
		}
	}
	
	public void moveLeft(int step)
	{
		moveRight(-step);
	}
	
	public void moveForwards(int step)
	{
		switch(orientation)
		{
		case UP:
			y = y + step;
			break;
		case DOWN:
			y = y - step;
			break;
		case SOUTH:
			z = z + step;
			break;
		case NORTH:
			z = z - step;
			break;
		case EAST:
			x = x + step;
			break;
		case WEST:
			x = x - step;
			break;
		default:
		}
	}	
	
	public void moveBackwards(int step)
	{
		moveForwards(-step);
	}
	
	public void moveUp(int step)
	{
		switch(orientation)
		{
		case EAST:
		case WEST:
		case NORTH:
		case SOUTH:
			y = y + step;
			break;
		default:
			break;
		}
		
	}
	
	public void moveDown(int step)
	{
		moveUp(-step);
	}
	
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		nbttagcompound.setDouble("i", x);
		nbttagcompound.setDouble("j", y);
		nbttagcompound.setDouble("k", z);
		nbttagcompound.setByte("o", (byte)(orientation == null ? ForgeDirection.UNKNOWN : orientation).ordinal());
	}
	
	@Override
	public String toString()
	{
		if(orientation == null)
		{
			return "{" + x + ", " + y + ", " + z + "}";
		}
		return "{" + x + ", " + y + ", " + z + ";" + orientation.toString() + "}";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		BlockPosition bp = (BlockPosition)obj;
		return bp != null && bp.x == x & bp.y == y & bp.z == z & bp.orientation == orientation;
	}
	
	@Override
	public int hashCode()
	{
		return (x & 0xFFF) | (y & 0xFF << 8) | (z & 0xFFF << 12);
	}
	
	public BlockPosition min(BlockPosition b)
	{
		return from(b.x > x ? x : b.x, b.y > y ? y : b.y, b.z > z ? z : b.z);
	}
	
	public BlockPosition max(BlockPosition b)
	{
		return from(b.x < x ? x : b.x, b.y < y ? y : b.y, b.z < z ? z : b.z);
	}
	
	public static BlockPosition min(BlockPosition a, BlockPosition b)
	{
		return create().from(b.x > a.x ? a.x : b.x, b.y > a.y ? a.y : b.y, b.z > a.z ? a.z : b.z);
	}
	
	public static BlockPosition max(BlockPosition a, BlockPosition b)
	{
		return create().from(b.x < a.x ? a.x : b.x, b.y < a.y ? a.y : b.y, b.z < a.z ? a.z : b.z);
	}
	
	public List<BlockPosition> getAdjacent(boolean includeVertical)
	{
		List<BlockPosition> a = new ArrayList<BlockPosition>();
		a.add(create().from(x + 1, y, z, ForgeDirection.EAST));
		a.add(create().from(x - 1, y, z, ForgeDirection.WEST));
		a.add(create().from(x, y, z + 1, ForgeDirection.SOUTH));
		a.add(create().from(x, y, z - 1, ForgeDirection.NORTH));
		if(includeVertical)
		{
			a.add(create().from(x, y + 1, z, ForgeDirection.UP));
			a.add(create().from(x, y - 1, z, ForgeDirection.DOWN));
		}
		return a;
	}
	
	public TileEntity getTileEntity(World world)
	{
		return world.getBlockTileEntity(x, y, z);
	}
	
	public static TileEntity getAdjacentTileEntity(TileEntity start, ForgeDirection direction)
	{
		int x = start.xCoord,
				y = start.yCoord,
				z = start.zCoord;
		switch(direction)
		{
		case UP:
			++y;
			break;
		case DOWN:
			--y;
			break;
		case SOUTH:
			++z;
			break;
		case NORTH:
			--z;
			break;
		case EAST:
			++x;
			break;
		case WEST:
			--x;
			break;
		default:
		}
		return start.worldObj.getBlockTileEntity(x, y, z);
	}
	
	public static TileEntity getAdjacentTileEntity(TileEntity start, ForgeDirection direction, Class<? extends TileEntity> targetClass)
	{
		TileEntity te = getAdjacentTileEntity(start, direction);
		if(targetClass.isAssignableFrom(te.getClass()))
		{
			return te;
		}
		else
		{
			return null;
		}
	}
}