package powercrystals.core.position;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.AxisAlignedBB;

public class Area
{
	private static Area pool;
	
	public static Area create()
	{
		Area ret;
		if (pool == null)
		{
			ret = new Area(0, 0, 0, 0, 0, 0);
		}
		else
		{
			ret = pool;
			pool = ret.next;
			ret.next = null;
		}
		
		return ret;
	}
	
	public int xMin;
	public int xMax;
	public int yMin;
	public int yMax;
	public int zMin;
	public int zMax;
	
	private BlockPosition _origin;
	private Area next;
	
	public Area(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax)
	{
		from(xMin, xMax, yMin, yMax, zMin, zMax);
	}
	
	public Area(BlockPosition center, int radius, int yNegOffset, int yPosOffset)
	{
		from(center, radius, yNegOffset, yPosOffset);
	}
	
	public Area from(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax)
	{
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		return this;
	}
	
	public Area from(BlockPosition center, int radius, int yNegOffset, int yPosOffset)
	{
		xMin = center.x - radius;
		xMax = center.x + radius;
		yMin = center.y - yNegOffset;
		yMax = center.y + yPosOffset;
		zMin = center.z - radius;
		zMax = center.z + radius;
		
		_origin = center.copy();
		return this;
	}
	
	public BlockPosition getMin()
	{
		return BlockPosition.create().from(xMin, yMin, zMin);
	}
	
	public BlockPosition getMax()
	{
		return BlockPosition.create().from(xMax, yMax, zMax);
	}
	
	public List<BlockPosition> getPositionsTopFirst()
	{
		ArrayList<BlockPosition> positions = new ArrayList<BlockPosition>();
		for(int y = yMax; y >= yMin; y--)
		{
			for(int x = xMin; x <= xMax; x++)
			{
				for(int z = zMin; z <= zMax; z++)
				{
					positions.add(BlockPosition.create().from(x, y, z));
				}
			}
		}
		return positions;
	}
	
	public List<BlockPosition> getPositionsBottomFirst()
	{
		ArrayList<BlockPosition> positions = new ArrayList<BlockPosition>();
		for(int y = yMin; y <= yMax; y++)
		{
			for(int x = xMin; x <= xMax; x++)
			{
				for(int z = zMin; z <= zMax; z++)
				{
					positions.add(BlockPosition.create().from(x, y, z));
				}
			}
		}
		return positions;
	}
	
	public BlockPosition getOrigin()
	{
		return _origin != null ? _origin.copy() : null;
	}
	
	public AxisAlignedBB toAxisAlignedBB()
	{
		return AxisAlignedBB.getBoundingBox(xMin, yMin, zMin, xMax + 1, yMax + 1, zMax + 1);
	}
	
	public Area copy()
	{
		return create().from(xMin, yMin, zMin, xMax, yMax, zMax);
	}
	
	public Area free()
	{
		next = pool;
		pool = this;
		_origin.free();
		_origin = null;
		return this;
	}
	
}
