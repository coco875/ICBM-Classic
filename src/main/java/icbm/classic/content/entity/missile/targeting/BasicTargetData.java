package icbm.classic.content.entity.missile.targeting;

import icbm.classic.api.missiles.IMissileTarget;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BasicTargetData implements IMissileTarget {
    private Vec3d position;

    public BasicTargetData() {
        //Only used for save/load
    }

    public BasicTargetData(double x, double y, double z) {
        this.position = new Vec3d(x, y, z);
    }

    public BasicTargetData(Vec3d position) {
        this.position = position;
    }

    public BasicTargetData(BlockPos pos) {
        this.position = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }



    public Vec3d getPosition() {
        return position;
    }

    @Override
    public boolean isValid() {
        return position != null
                && Double.isNaN(getX()) && Double.isFinite(getX())
                && Double.isNaN(getY()) && Double.isFinite(getY())
                && Double.isNaN(getZ()) && Double.isFinite(getZ());
    }

    @Override
    public double getX() {
        return position != null ? position.x : Double.NaN;
    }

    @Override
    public double getY() {
        return position != null ? position.y : Double.NaN;
    }

    @Override
    public double getZ() {
        return position != null ? position.z : Double.NaN;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound saveData = new NBTTagCompound();
        saveData.setDouble("x", position.x);
        saveData.setDouble("y", position.y);
        saveData.setDouble("z", position.z);
        return saveData;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        double x = nbt.getDouble("x");
        double y = nbt.getDouble("y");
        double z = nbt.getDouble("z");
        this.position = new Vec3d(x, y, z);
    }
}
